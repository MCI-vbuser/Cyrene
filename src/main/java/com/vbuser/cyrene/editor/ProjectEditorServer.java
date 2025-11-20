package com.vbuser.cyrene.editor;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.vbuser.cyrene.Main;
import org.json.JSONObject;
import org.json.JSONArray;

public class ProjectEditorServer {
    private static final int PORT = 8081;
    private static HttpServer server;
    private static String currentProjectPath;
    static final Map<String, String> openFiles = new HashMap<>();

    public static HttpServer getServer() {
        return server;
    }

    public static void startServer(String projectPath) {
        currentProjectPath = projectPath;
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/", new EditorHandler());
            server.createContext("/api/file/tree", new FileTreeHandler());
            server.createContext("/api/file/content", new FileContentHandler());
            server.createContext("/api/file/save", new FileSaveHandler());
            server.createContext("/api/file/create", new FileCreateHandler());
            server.createContext("/api/file/delete", new FileDeleteHandler());
            server.createContext("/api/file/rename", new FileRenameHandler());
            server.createContext("/static/", new EditorStaticFileHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("项目编辑器已启动: http://localhost:" + PORT);
            System.out.println("项目路径: " + currentProjectPath);

            Main.openBrowser("http://localhost:" + PORT);
            createProjectStructure();

        } catch (IOException e) {
            System.err.println("无法启动项目编辑器: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void stopServer() {
        if (server != null) {
            System.out.println("正在停止项目编辑器...");
            server.stop(0);
            server = null;
            openFiles.clear();
        }
    }

    private static void createProjectStructure() {
        try {
            Path srcPath = Paths.get(currentProjectPath, "src");
            Path runPath = Paths.get(currentProjectPath, "run");

            Files.createDirectories(srcPath);
            Files.createDirectories(runPath);

            System.out.println("项目结构已创建");
        } catch (IOException e) {
            System.err.println("创建项目结构失败: " + e.getMessage());
        }
    }

    static class EditorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = getEditorPage();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class FileTreeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    JSONArray fileTree = buildFileTree(Paths.get(currentProjectPath));
                    String response = fileTree.toString();

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    sendErrorResponse(exchange, "获取文件树失败: " + e.getMessage());
                }
            }
        }

        private JSONArray buildFileTree(Path rootPath) throws IOException {
            JSONArray result = new JSONArray();

            if (Files.exists(rootPath) && Files.isDirectory(rootPath)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
                    List<Path> paths = new ArrayList<>();
                    for (Path path : stream) {
                        paths.add(path);
                    }

                    paths.sort((p1, p2) -> {
                        boolean isDir1 = Files.isDirectory(p1);
                        boolean isDir2 = Files.isDirectory(p2);
                        if (isDir1 && !isDir2) return -1;
                        if (!isDir1 && isDir2) return 1;
                        return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
                    });

                    for (Path path : paths) {
                        String fileName = path.getFileName().toString();
                        if (fileName.startsWith(".")) continue;

                        JSONObject item = new JSONObject();
                        item.put("name", fileName);
                        item.put("path", rootPath.relativize(path).toString());
                        item.put("type", Files.isDirectory(path) ? "directory" : getFileType(fileName));

                        if (Files.isDirectory(path)) {
                            item.put("children", buildFileTree(path));
                            item.put("expanded", false);
                        }

                        result.put(item);
                    }
                }
            }

            return result;
        }

        private String getFileType(String fileName) {
            if (fileName.endsWith(".cyr")) return "cyr";
            if (fileName.endsWith(".ndm")) return "ndm";
            return "file";
        }
    }

    static class FileContentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                String filePath = getQueryParam(query);

                if (filePath != null && !filePath.isEmpty()) {
                    try {
                        Path fullPath = Paths.get(currentProjectPath, filePath);
                        if (Files.exists(fullPath) && !Files.isDirectory(fullPath)) {
                            List<String> lines = Files.readAllLines(fullPath, StandardCharsets.UTF_8);
                            StringBuilder content = new StringBuilder();
                            for (String line : lines) {
                                content.append(line).append("\n");
                            }

                            JSONObject response = new JSONObject();
                            response.put("content", content.toString());
                            response.put("type", getFileType(fullPath.getFileName().toString()));

                            exchange.getResponseHeaders().set("Content-Type", "application/json");
                            exchange.sendResponseHeaders(200, response.toString().getBytes().length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.toString().getBytes());
                            os.close();
                            return;
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                sendErrorResponse(exchange, "文件不存在或无法读取");
            }
        }
    }

    static class FileSaveHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    JSONObject request = getJsonObject(exchange);

                    String filePath = request.getString("path");
                    String content = request.getString("content");

                    Path fullPath = Paths.get(currentProjectPath, filePath);
                    Files.createDirectories(fullPath.getParent());

                    Files.write(fullPath, content.getBytes(StandardCharsets.UTF_8));

                    openFiles.put(filePath, content);

                    JSONObject response = new JSONObject();
                    response.put("status", "success");
                    response.put("message", "文件已保存");

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.toString().getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();

                } catch (Exception e) {
                    sendErrorResponse(exchange, "保存文件失败: " + e.getMessage());
                }
            }
        }

        private static JSONObject getJsonObject(HttpExchange exchange) throws IOException {
            InputStream requestBody = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = requestBody.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String body = result.toString(StandardCharsets.UTF_8.name());

            return new JSONObject(body);
        }
    }

    static class FileCreateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    JSONObject request = getJsonObject(exchange);

                    String type = request.getString("type");
                    String path = request.getString("path");
                    String name = request.getString("name");

                    Path fullPath = Paths.get(currentProjectPath, path, name);

                    if ("directory".equals(type)) {
                        Files.createDirectories(fullPath);
                    } else {
                        String extension = "";
                        if ("cyr".equals(type)) extension = ".cyr";
                        else if ("ndm".equals(type)) extension = ".ndm";

                        fullPath = Paths.get(fullPath + extension);
                        Files.createDirectories(fullPath.getParent());
                        Files.write(fullPath, new byte[0]);
                    }

                    JSONObject response = new JSONObject();
                    response.put("status", "success");
                    response.put("message", "创建成功");

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.toString().getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();

                } catch (Exception e) {
                    sendErrorResponse(exchange, "创建失败: " + e.getMessage());
                }
            }
        }

        private static JSONObject getJsonObject(HttpExchange exchange) throws IOException {
            InputStream requestBody = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = requestBody.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String body = result.toString(StandardCharsets.UTF_8.name());

            return new JSONObject(body);
        }
    }

    static class FileDeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String path = getString(exchange);
                    Path fullPath = Paths.get(currentProjectPath, path);

                    if (Files.exists(fullPath)) {
                        deleteRecursively(fullPath);

                        JSONObject response = new JSONObject();
                        response.put("status", "success");
                        response.put("message", "删除成功");

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.toString().getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.toString().getBytes());
                        os.close();
                    } else {
                        sendErrorResponse(exchange, "文件不存在");
                    }

                } catch (Exception e) {
                    sendErrorResponse(exchange, "删除失败: " + e.getMessage());
                }
            }
        }

        private static String getString(HttpExchange exchange) throws IOException {
            InputStream requestBody = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = requestBody.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String body = result.toString(StandardCharsets.UTF_8.name());

            JSONObject request = new JSONObject(body);

            return request.getString("path");
        }

        private void deleteRecursively(Path path) throws IOException {
            if (Files.isDirectory(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path entry : stream) {
                        deleteRecursively(entry);
                    }
                }
            }
            Files.delete(path);
        }
    }

    static class FileRenameHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    JSONObject request = getJsonObject(exchange);

                    String oldPath = request.getString("oldPath");
                    String newName = request.getString("newName");

                    Path oldFullPath = Paths.get(currentProjectPath, oldPath);
                    Path newFullPath = oldFullPath.getParent().resolve(newName);

                    if (Files.exists(oldFullPath)) {
                        Files.move(oldFullPath, newFullPath);

                        JSONObject response = new JSONObject();
                        response.put("status", "success");
                        response.put("message", "重命名成功");

                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.toString().getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.toString().getBytes());
                        os.close();
                    } else {
                        sendErrorResponse(exchange, "文件不存在");
                    }

                } catch (Exception e) {
                    sendErrorResponse(exchange, "重命名失败: " + e.getMessage());
                }
            }
        }

        private static JSONObject getJsonObject(HttpExchange exchange) throws IOException {
            InputStream requestBody = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = requestBody.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String body = result.toString(StandardCharsets.UTF_8.name());

            return new JSONObject(body);
        }
    }

    static class EditorStaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/static/editor-style.css")) {
                String response = getEditorCSS();
                exchange.getResponseHeaders().set("Content-Type", "text/css");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    private static void sendErrorResponse(HttpExchange exchange, String message) throws IOException {
        JSONObject response = new JSONObject();
        response.put("status", "error");
        response.put("message", message);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(400, response.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    private static String getQueryParam(String query) {
        if (query == null) return null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("path")) {
                return keyValue[1];
            }
        }
        return null;
    }

    private static String getFileType(String fileName) {
        if (fileName.endsWith(".cyr")) return "cyr";
        if (fileName.endsWith(".ndm")) return "ndm";
        return "file";
    }

    private static String getEditorPage() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Cyrene Editor - 项目编辑器</title>\n" +
                "    <link rel=\"stylesheet\" href=\"/static/editor-style.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"editor-container\">\n" +
                "        <header class=\"editor-header\">\n" +
                "            <div class=\"header-left\">\n" +
                "                <button class=\"btn btn-icon\" id=\"sidebar-toggle\">☰</button>\n" +
                "                <h1>Cyrene Editor</h1>\n" +
                "            </div>\n" +
                "            <div class=\"header-right\">\n" +
                "                <span class=\"project-path\" id=\"project-path\">" + currentProjectPath + "</span>\n" +
                "            </div>\n" +
                "        </header>\n" +
                "        \n" +
                "        <div class=\"editor-main\">\n" +
                "            <aside class=\"sidebar\" id=\"sidebar\">\n" +
                "                <div class=\"sidebar-header\">\n" +
                "                    <h3>项目文件</h3>\n" +
                "                    <div class=\"sidebar-actions\">\n" +
                "                        <button class=\"btn btn-icon\" id=\"refresh-tree\" title=\"刷新\">↻</button>\n" +
                "                        <button class=\"btn btn-icon\" id=\"create-new\" title=\"新建\">+</button>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"file-tree\" id=\"file-tree\">\n" +
                "                </div>\n" +
                "            </aside>\n" +
                "            \n" +
                "            <main class=\"editor-content\" id=\"editor-content\">\n" +
                "                <div class=\"welcome-message\" id=\"welcome-message\">\n" +
                "                    <h2>欢迎使用 Cyrene Editor</h2>\n" +
                "                    <p>请从左侧文件树中选择一个文件进行编辑，或创建新文件。</p>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"editor-area hidden\" id=\"editor-area\">\n" +
                "                    <div class=\"editor-toolbar\">\n" +
                "                        <span class=\"file-name\" id=\"current-file\"></span>\n" +
                "                        <span class=\"save-status\" id=\"save-status\">已保存</span>\n" +
                "                    </div>\n" +
                "                    <div class=\"code-editor-container\">\n" +
                "                        <div class=\"line-numbers\" id=\"line-numbers\"></div>\n" +
                "                        <textarea class=\"code-editor\" id=\"code-editor\" spellcheck=\"false\"></textarea>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </main>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <div class=\"modal hidden\" id=\"create-modal\">\n" +
                "        <div class=\"modal-content\">\n" +
                "            <h3>新建</h3>\n" +
                "            <form id=\"create-form\">\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label for=\"create-type\">类型</label>\n" +
                "                    <select id=\"create-type\" name=\"type\">\n" +
                "                        <option value=\"directory\">文件夹</option>\n" +
                "                        <option value=\"cyr\">文本文件 (.cyr)</option>\n" +
                "                        <option value=\"ndm\">节点图文件 (.ndm)</option>\n" +
                "                    </select>\n" +
                "                </div>\n" +
                "                <div class=\"form-group\">\n" +
                "                    <label for=\"create-name\">名称</label>\n" +
                "                    <input type=\"text\" id=\"create-name\" name=\"name\" required>\n" +
                "                </div>\n" +
                "                <input type=\"hidden\" id=\"create-path\" name=\"path\">\n" +
                "                <div class=\"form-actions\">\n" +
                "                    <button type=\"button\" class=\"btn btn-outline\" id=\"cancel-create\">取消</button>\n" +
                "                    <button type=\"submit\" class=\"btn btn-primary\">创建</button>\n" +
                "                </div>\n" +
                "            </form>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <script>\n" +
                "        let currentFile = null;\n" +
                "        let isUnsaved = false;\n" +
                "        let saveTimeout = null;\n" +
                "\n" +
                "        document.addEventListener('DOMContentLoaded', function() {\n" +
                "            loadFileTree();\n" +
                "            setupEventListeners();\n" +
                "        });\n" +
                "\n" +
                "        function setupEventListeners() {\n" +
                "            document.getElementById('sidebar-toggle').addEventListener('click', function() {\n" +
                "                const sidebar = document.getElementById('sidebar');\n" +
                "                sidebar.classList.toggle('collapsed');\n" +
                "            });\n" +
                "\n" +
                "            document.getElementById('refresh-tree').addEventListener('click', loadFileTree);\n" +
                "\n" +
                "            document.getElementById('create-new').addEventListener('click', showCreateModal);\n" +
                "            document.getElementById('create-form').addEventListener('submit', handleCreate);\n" +
                "            document.getElementById('cancel-create').addEventListener('click', hideCreateModal);\n" +
                "\n" +
                "            document.getElementById('code-editor').addEventListener('input', function() {\n" +
                "                if (currentFile) {\n" +
                "                    markUnsaved();\n" +
                "                    scheduleAutoSave();\n" +
                "                    updateLineNumbers();\n" +
                "                }\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "        function loadFileTree() {\n" +
                "            fetch('/api/file/tree')\n" +
                "                .then(response => response.json())\n" +
                "                .then(files => {\n" +
                "                    renderFileTree(files);\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('加载文件树失败:', error);\n" +
                "                });\n" +
                "        }\n" +
                "\n" +
                "        function renderFileTree(files, container = null) {\n" +
                "            const treeContainer = container || document.getElementById('file-tree');\n" +
                "            treeContainer.innerHTML = '';\n" +
                "\n" +
                "            files.forEach(file => {\n" +
                "                const item = createFileTreeItem(file);\n" +
                "                treeContainer.appendChild(item);\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "        function createFileTreeItem(file) {\n" +
                "            const item = document.createElement('div');\n" +
                "            item.className = 'tree-node';\n" +
                "\n" +
                "            const treeItem = document.createElement('div');\n" +
                "            treeItem.className = 'tree-item';\n" +
                "            treeItem.dataset.path = file.path;\n" +
                "            treeItem.dataset.type = file.type;\n" +
                "\n" +
                "            const icon = document.createElement('span');\n" +
                "            icon.className = 'tree-icon';\n" +
                "            icon.textContent = getFileIcon(file);\n" +
                "\n" +
                "            const name = document.createElement('span');\n" +
                "            name.className = 'tree-name';\n" +
                "            name.textContent = file.name;\n" +
                "\n" +
                "            treeItem.appendChild(icon);\n" +
                "            treeItem.appendChild(name);\n" +
                "\n" +
                "            if (file.type === 'directory') {\n" +
                "                treeItem.addEventListener('click', function(e) {\n" +
                "                    e.stopPropagation();\n" +
                "                    toggleDirectory(this);\n" +
                "                });\n" +
                "\n" +
                "                const childrenContainer = document.createElement('div');\n" +
                "                childrenContainer.className = 'tree-children';\n" +
                "                \n" +
                "                if (file.expanded && file.children) {\n" +
                "                    childrenContainer.classList.add('expanded');\n" +
                "                    renderFileTree(file.children, childrenContainer);\n" +
                "                }\n" +
                "\n" +
                "                item.appendChild(treeItem);\n" +
                "                item.appendChild(childrenContainer);\n" +
                "            } else {\n" +
                "                treeItem.addEventListener('click', function() {\n" +
                "                    openFile(file.path, file.type);\n" +
                "                });\n" +
                "\n" +
                "                treeItem.addEventListener('contextmenu', function(e) {\n" +
                "                    e.preventDefault();\n" +
                "                    showContextMenu(e, file);\n" +
                "                });\n" +
                "\n" +
                "                item.appendChild(treeItem);\n" +
                "            }\n" +
                "\n" +
                "            return item;\n" +
                "        }\n" +
                "\n" +
                "        function getFileIcon(file) {\n" +
                "            switch (file.type) {\n" +
                "                case 'directory': return '📁';\n" +
                "                case 'cyr': return '📄';\n" +
                "                case 'ndm': return '📊';\n" +
                "                default: return '📄';\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        function toggleDirectory(element) {\n" +
                "            const children = element.parentElement.querySelector('.tree-children');\n" +
                "            const isExpanded = children.classList.contains('expanded');\n" +
                "\n" +
                "            if (isExpanded) {\n" +
                "                children.classList.remove('expanded');\n" +
                "            } else {\n" +
                "                children.classList.add('expanded');\n" +
                "                if (children.children.length === 0) {\n" +
                "                    const path = element.dataset.path;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        function openFile(filePath, fileType) {\n" +
                "            if (fileType !== 'cyr') {\n" +
                "                alert('目前只支持编辑 .cyr 文件');\n" +
                "                return;\n" +
                "            }\n" +
                "\n" +
                "            fetch('/api/file/content?path=' + encodeURIComponent(filePath))\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    currentFile = filePath;\n" +
                "                    document.getElementById('current-file').textContent = filePath;\n" +
                "                    document.getElementById('code-editor').value = data.content;\n" +
                "                    \n" +
                "                    document.getElementById('welcome-message').classList.add('hidden');\n" +
                "                    document.getElementById('editor-area').classList.remove('hidden');\n" +
                "                    \n" +
                "                    markSaved();\n" +
                "                    updateLineNumbers();\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('打开文件失败:', error);\n" +
                "                    alert('打开文件失败: ' + error.message);\n" +
                "                });\n" +
                "        }\n" +
                "\n" +
                "        function markUnsaved() {\n" +
                "            isUnsaved = true;\n" +
                "            document.getElementById('save-status').textContent = '未保存';\n" +
                "            document.getElementById('save-status').classList.add('unsaved');\n" +
                "        }\n" +
                "\n" +
                "        function markSaved() {\n" +
                "            isUnsaved = false;\n" +
                "            document.getElementById('save-status').textContent = '已保存';\n" +
                "            document.getElementById('save-status').classList.remove('unsaved');\n" +
                "        }\n" +
                "\n" +
                "        function scheduleAutoSave() {\n" +
                "            if (saveTimeout) {\n" +
                "                clearTimeout(saveTimeout);\n" +
                "            }\n" +
                "            saveTimeout = setTimeout(saveFile, 1000);\n" +
                "        }\n" +
                "\n" +
                "        function saveFile() {\n" +
                "            if (!currentFile || !isUnsaved) return;\n" +
                "\n" +
                "            const content = document.getElementById('code-editor').value;\n" +
                "            \n" +
                "            fetch('/api/file/save', {\n" +
                "                method: 'POST',\n" +
                "                headers: {\n" +
                "                    'Content-Type': 'application/json',\n" +
                "                },\n" +
                "                body: JSON.stringify({\n" +
                "                    path: currentFile,\n" +
                "                    content: content\n" +
                "                })\n" +
                "            })\n" +
                "            .then(response => response.json())\n" +
                "            .then(data => {\n" +
                "                if (data.status === 'success') {\n" +
                "                    markSaved();\n" +
                "                } else {\n" +
                "                    alert('保存失败: ' + data.message);\n" +
                "                }\n" +
                "            })\n" +
                "            .catch(error => {\n" +
                "                console.error('保存文件失败:', error);\n" +
                "                alert('保存文件失败: ' + error.message);\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "        function updateLineNumbers() {\n" +
                "            const textarea = document.getElementById('code-editor');\n" +
                "            const lineNumbers = document.getElementById('line-numbers');\n" +
                "            const lines = textarea.value.split('\\n').length;\n" +
                "            \n" +
                "            let numbers = '';\n" +
                "            for (let i = 1; i <= lines; i++) {\n" +
                "                numbers += i + '\\n';\n" +
                "            }\n" +
                "            \n" +
                "            lineNumbers.textContent = numbers;\n" +
                "        }\n" +
                "\n" +
                "        function showCreateModal() {\n" +
                "            document.getElementById('create-modal').classList.remove('hidden');\n" +
                "            document.getElementById('create-name').focus();\n" +
                "        }\n" +
                "\n" +
                "        function hideCreateModal() {\n" +
                "            document.getElementById('create-modal').classList.add('hidden');\n" +
                "            document.getElementById('create-form').reset();\n" +
                "        }\n" +
                "\n" +
                "        function handleCreate(e) {\n" +
                "            e.preventDefault();\n" +
                "            \n" +
                "            const type = document.getElementById('create-type').value;\n" +
                "            const name = document.getElementById('create-name').value;\n" +
                "            const path = document.getElementById('create-path').value || '';\n" +
                "            \n" +
                "            fetch('/api/file/create', {\n" +
                "                method: 'POST',\n" +
                "                headers: {\n" +
                "                    'Content-Type': 'application/json',\n" +
                "                },\n" +
                "                body: JSON.stringify({\n" +
                "                    type: type,\n" +
                "                    path: path,\n" +
                "                    name: name\n" +
                "                })\n" +
                "            })\n" +
                "            .then(response => response.json())\n" +
                "            .then(data => {\n" +
                "                if (data.status === 'success') {\n" +
                "                    hideCreateModal();\n" +
                "                    loadFileTree();\n" +
                "                } else {\n" +
                "                    alert('创建失败: ' + data.message);\n" +
                "                }\n" +
                "            })\n" +
                "            .catch(error => {\n" +
                "                console.error('创建失败:', error);\n" +
                "                alert('创建失败: ' + error.message);\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "        function showContextMenu(e, file) {\n" +
                "            const menu = document.createElement('div');\n" +
                "            menu.className = 'context-menu';\n" +
                "            menu.style.left = e.pageX + 'px';\n" +
                "            menu.style.top = e.pageY + 'px';\n" +
                "            \n" +
                "            const renameItem = document.createElement('div');\n" +
                "            renameItem.className = 'context-menu-item';\n" +
                "            renameItem.textContent = '重命名';\n" +
                "            renameItem.addEventListener('click', function() {\n" +
                "                renameFile(file);\n" +
                "                document.body.removeChild(menu);\n" +
                "            });\n" +
                "            \n" +
                "            const deleteItem = document.createElement('div');\n" +
                "            deleteItem.className = 'context-menu-item';\n" +
                "            deleteItem.textContent = '删除';\n" +
                "            deleteItem.addEventListener('click', function() {\n" +
                "                if (confirm('确定要删除 ' + file.name + ' 吗？')) {\n" +
                "                    deleteFile(file.path);\n" +
                "                }\n" +
                "                document.body.removeChild(menu);\n" +
                "            });\n" +
                "            \n" +
                "            menu.appendChild(renameItem);\n" +
                "            menu.appendChild(deleteItem);\n" +
                "            \n" +
                "            document.body.appendChild(menu);\n" +
                "            \n" +
                "            setTimeout(() => {\n" +
                "                const closeMenu = function(clickE) {\n" +
                "                    if (!menu.contains(clickE.target)) {\n" +
                "                        document.body.removeChild(menu);\n" +
                "                        document.removeEventListener('click', closeMenu);\n" +
                "                    }\n" +
                "                };\n" +
                "                document.addEventListener('click', closeMenu);\n" +
                "            }, 100);\n" +
                "        }\n" +
                "\n" +
                "        function renameFile(file) {\n" +
                "            const newName = prompt('请输入新名称:', file.name);\n" +
                "            if (newName && newName !== file.name) {\n" +
                "                fetch('/api/file/rename', {\n" +
                "                    method: 'POST',\n" +
                "                    headers: {\n" +
                "                        'Content-Type': 'application/json',\n" +
                "                    },\n" +
                "                    body: JSON.stringify({\n" +
                "                        oldPath: file.path,\n" +
                "                        newName: newName\n" +
                "                    })\n" +
                "                })\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    if (data.status === 'success') {\n" +
                "                        loadFileTree();\n" +
                "                    } else {\n" +
                "                        alert('重命名失败: ' + data.message);\n" +
                "                    }\n" +
                "                })\n" +
                "                .catch(error => {\n" +
                "                    console.error('重命名失败:', error);\n" +
                "                    alert('重命名失败: ' + error.message);\n" +
                "                });\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        function deleteFile(filePath) {\n" +
                "            fetch('/api/file/delete', {\n" +
                "                method: 'POST',\n" +
                "                headers: {\n" +
                "                    'Content-Type': 'application/json',\n" +
                "                },\n" +
                "                body: JSON.stringify({\n" +
                "                    path: filePath\n" +
                "                })\n" +
                "            })\n" +
                "            .then(response => response.json())\n" +
                "            .then(data => {\n" +
                "                if (data.status === 'success') {\n" +
                "                    loadFileTree();\n" +
                "                    if (currentFile === filePath) {\n" +
                "                        closeEditor();\n" +
                "                    }\n" +
                "                } else {\n" +
                "                    alert('删除失败: ' + data.message);\n" +
                "                }\n" +
                "            })\n" +
                "            .catch(error => {\n" +
                "                console.error('删除失败:', error);\n" +
                "                alert('删除失败: ' + error.message);\n" +
                "            });\n" +
                "        }\n" +
                "\n" +
                "        function closeEditor() {\n" +
                "            currentFile = null;\n" +
                "            document.getElementById('welcome-message').classList.remove('hidden');\n" +
                "            document.getElementById('editor-area').classList.add('hidden');\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    private static String getEditorCSS() {
        return ":root {\n" +
                "    --bg-primary: #2b2b2b;\n" +
                "    --bg-secondary: #3c3f41;\n" +
                "    --bg-tertiary: #323232;\n" +
                "    --bg-editor: #1e1e1e;\n" +
                "    --text-primary: #cccccc;\n" +
                "    --text-secondary: #999999;\n" +
                "    --text-muted: #666666;\n" +
                "    --accent-color: #4e7ab5;\n" +
                "    --accent-hover: #5a8ac8;\n" +
                "    --border-color: #555555;\n" +
                "    --success-color: #499c54;\n" +
                "    --warning-color: #d19a66;\n" +
                "    --error-color: #e06c75;\n" +
                "    --sidebar-width: 280px;\n" +
                "    --header-height: 50px;\n" +
                "}\n" +
                "\n" +
                "* {\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    box-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;\n" +
                "    background: var(--bg-primary);\n" +
                "    color: var(--text-primary);\n" +
                "    height: 100vh;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                ".editor-container {\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    height: 100vh;\n" +
                "}\n" +
                "\n" +
                ".editor-header {\n" +
                "    height: var(--header-height);\n" +
                "    background: var(--bg-secondary);\n" +
                "    border-bottom: 1px solid var(--border-color);\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    justify-content: space-between;\n" +
                "    padding: 0 1rem;\n" +
                "}\n" +
                "\n" +
                ".header-left {\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    gap: 1rem;\n" +
                "}\n" +
                "\n" +
                ".header-left h1 {\n" +
                "    font-size: 1.2rem;\n" +
                "    font-weight: 600;\n" +
                "}\n" +
                "\n" +
                ".project-path {\n" +
                "    color: var(--text-secondary);\n" +
                "    font-size: 0.9rem;\n" +
                "}\n" +
                "\n" +
                ".editor-main {\n" +
                "    display: flex;\n" +
                "    flex: 1;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                ".sidebar {\n" +
                "    width: var(--sidebar-width);\n" +
                "    background: var(--bg-secondary);\n" +
                "    border-right: 1px solid var(--border-color);\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    transition: width 0.3s ease;\n" +
                "    height: calc(100vh - var(--header-height));\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                ".sidebar.collapsed {\n" +
                "    width: 0;\n" +
                "    min-width: 0;\n" +
                "}\n" +
                "\n" +
                ".sidebar-header {\n" +
                "    padding: 1rem;\n" +
                "    border-bottom: 1px solid var(--border-color);\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    justify-content: space-between;\n" +
                "    flex-shrink: 0;\n" +
                "}\n" +
                "\n" +
                ".sidebar-header h3 {\n" +
                "    font-size: 1rem;\n" +
                "    font-weight: 600;\n" +
                "}\n" +
                "\n" +
                ".sidebar-actions {\n" +
                "    display: flex;\n" +
                "    gap: 0.5rem;\n" +
                "}\n" +
                "\n" +
                ".file-tree {\n" +
                "    flex: 1;\n" +
                "    overflow-y: auto;\n" +
                "    padding: 0.5rem;\n" +
                "    height: 100%;\n" +
                "}\n" +
                "\n" +
                ".tree-node {\n" +
                "    margin: 2px 0;\n" +
                "}\n" +
                "\n" +
                ".tree-item {\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    padding: 4px 8px;\n" +
                "    border-radius: 4px;\n" +
                "    cursor: pointer;\n" +
                "    user-select: none;\n" +
                "    transition: background 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".tree-item:hover {\n" +
                "    background: var(--bg-tertiary);\n" +
                "}\n" +
                "\n" +
                ".tree-item.selected {\n" +
                "    background: var(--accent-color);\n" +
                "}\n" +
                "\n" +
                ".tree-icon {\n" +
                "    margin-right: 6px;\n" +
                "    width: 16px;\n" +
                "    text-align: center;\n" +
                "}\n" +
                "\n" +
                ".tree-children {\n" +
                "    margin-left: 16px;\n" +
                "    display: none;\n" +
                "}\n" +
                "\n" +
                ".tree-children.expanded {\n" +
                "    display: block;\n" +
                "}\n" +
                "\n" +
                ".editor-content {\n" +
                "    flex: 1;\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    background: var(--bg-primary);\n" +
                "    height: calc(100vh - var(--header-height));\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                ".welcome-message {\n" +
                "    flex: 1;\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    align-items: center;\n" +
                "    justify-content: center;\n" +
                "    text-align: center;\n" +
                "    color: var(--text-secondary);\n" +
                "}\n" +
                "\n" +
                ".welcome-message h2 {\n" +
                "    margin-bottom: 1rem;\n" +
                "    color: var(--accent-color);\n" +
                "}\n" +
                "\n" +
                ".editor-area {\n" +
                "    flex: 1;\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    height: 100%;\n" +
                "}\n" +
                "\n" +
                ".editor-toolbar {\n" +
                "    padding: 0.5rem 1rem;\n" +
                "    background: var(--bg-secondary);\n" +
                "    border-bottom: 1px solid var(--border-color);\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    justify-content: space-between;\n" +
                "    flex-shrink: 0;\n" +
                "}\n" +
                "\n" +
                ".file-name {\n" +
                "    font-weight: 600;\n" +
                "    font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', 'Monaco', monospace;\n" +
                "}\n" +
                "\n" +
                ".save-status {\n" +
                "    color: var(--success-color);\n" +
                "    font-size: 0.9rem;\n" +
                "}\n" +
                "\n" +
                ".save-status.unsaved {\n" +
                "    color: var(--warning-color);\n" +
                "}\n" +
                "\n" +
                ".code-editor-container {\n" +
                "    flex: 1;\n" +
                "    display: flex;\n" +
                "    background: var(--bg-editor);\n" +
                "    overflow: hidden;\n" +
                "    height: 100%;\n" +
                "}\n" +
                "\n" +
                ".line-numbers {\n" +
                "    background: var(--bg-tertiary);\n" +
                "    color: var(--text-muted);\n" +
                "    padding: 1rem 0.5rem;\n" +
                "    text-align: right;\n" +
                "    font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', 'Monaco', monospace;\n" +
                "    font-size: 14px;\n" +
                "    line-height: 1.5;\n" +
                "    user-select: none;\n" +
                "    min-width: 60px;\n" +
                "    overflow: hidden;\n" +
                "    white-space: pre;\n" +
                "    height: 100%;\n" +
                "    overflow-y: auto;\n" +
                "}\n" +
                "\n" +
                ".code-editor {\n" +
                "    flex: 1;\n" +
                "    background: transparent;\n" +
                "    color: var(--text-primary);\n" +
                "    border: none;\n" +
                "    outline: none;\n" +
                "    padding: 1rem;\n" +
                "    font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', 'Monaco', monospace;\n" +
                "    font-size: 14px;\n" +
                "    line-height: 1.5;\n" +
                "    resize: none;\n" +
                "    white-space: pre;\n" +
                "    overflow-wrap: normal;\n" +
                "    overflow: auto;\n" +
                "    height: 100%;\n" +
                "}\n" +
                "\n" +
                ".btn {\n" +
                "    padding: 0.5rem 1rem;\n" +
                "    border: none;\n" +
                "    border-radius: 4px;\n" +
                "    font-size: 0.9rem;\n" +
                "    cursor: pointer;\n" +
                "    transition: all 0.3s ease;\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    gap: 0.5rem;\n" +
                "}\n" +
                "\n" +
                ".btn:disabled {\n" +
                "    opacity: 0.5;\n" +
                "    cursor: not-allowed;\n" +
                "}\n" +
                "\n" +
                ".btn-primary {\n" +
                "    background: var(--accent-color);\n" +
                "    color: white;\n" +
                "}\n" +
                "\n" +
                ".btn-primary:hover:not(:disabled) {\n" +
                "    background: var(--accent-hover);\n" +
                "}\n" +
                "\n" +
                ".btn-outline {\n" +
                "    background: transparent;\n" +
                "    color: var(--text-primary);\n" +
                "    border: 1px solid var(--border-color);\n" +
                "}\n" +
                "\n" +
                ".btn-outline:hover:not(:disabled) {\n" +
                "    background: var(--bg-tertiary);\n" +
                "    border-color: var(--accent-color);\n" +
                "}\n" +
                "\n" +
                ".btn-icon {\n" +
                "    padding: 0.5rem;\n" +
                "    background: transparent;\n" +
                "    color: var(--text-primary);\n" +
                "    border: none;\n" +
                "    border-radius: 4px;\n" +
                "    cursor: pointer;\n" +
                "    transition: background 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".btn-icon:hover {\n" +
                "    background: var(--bg-tertiary);\n" +
                "}\n" +
                "\n" +
                ".modal {\n" +
                "    position: fixed;\n" +
                "    top: 0;\n" +
                "    left: 0;\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    background: rgba(0, 0, 0, 0.5);\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    justify-content: center;\n" +
                "    z-index: 1000;\n" +
                "}\n" +
                "\n" +
                ".modal-content {\n" +
                "    background: var(--bg-secondary);\n" +
                "    border-radius: 8px;\n" +
                "    padding: 2rem;\n" +
                "    min-width: 400px;\n" +
                "    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);\n" +
                "}\n" +
                "\n" +
                ".modal-content h3 {\n" +
                "    margin-bottom: 1.5rem;\n" +
                "    color: var(--text-primary);\n" +
                "}\n" +
                "\n" +
                ".form-group {\n" +
                "    margin-bottom: 1rem;\n" +
                "}\n" +
                "\n" +
                ".form-group label {\n" +
                "    display: block;\n" +
                "    margin-bottom: 0.5rem;\n" +
                "    color: var(--text-primary);\n" +
                "    font-weight: 500;\n" +
                "}\n" +
                "\n" +
                ".form-group input,\n" +
                ".form-group select {\n" +
                "    width: 100%;\n" +
                "    padding: 0.75rem;\n" +
                "    background: var(--bg-tertiary);\n" +
                "    border: 1px solid var(--border-color);\n" +
                "    border-radius: 4px;\n" +
                "    color: var(--text-primary);\n" +
                "    font-size: 1rem;\n" +
                "}\n" +
                "\n" +
                ".form-group input:focus,\n" +
                ".form-group select:focus {\n" +
                "    outline: none;\n" +
                "    border-color: var(--accent-color);\n" +
                "}\n" +
                "\n" +
                ".form-actions {\n" +
                "    display: flex;\n" +
                "    gap: 1rem;\n" +
                "    margin-top: 1.5rem;\n" +
                "    justify-content: flex-end;\n" +
                "}\n" +
                "\n" +
                ".hidden {\n" +
                "    display: none !important;\n" +
                "}\n" +
                "\n" +
                ".context-menu {\n" +
                "    position: fixed;\n" +
                "    background: var(--bg-secondary);\n" +
                "    border: 1px solid var(--border-color);\n" +
                "    border-radius: 4px;\n" +
                "    padding: 0.5rem 0;\n" +
                "    min-width: 150px;\n" +
                "    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);\n" +
                "    z-index: 1000;\n" +
                "}\n" +
                "\n" +
                ".context-menu-item {\n" +
                "    padding: 0.5rem 1rem;\n" +
                "    cursor: pointer;\n" +
                "    transition: background 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".context-menu-item:hover {\n" +
                "    background: var(--accent-color);\n" +
                "}\n" +
                "\n" +
                "@media (max-width: 768px) {\n" +
                "    .sidebar {\n" +
                "        position: absolute;\n" +
                "        height: 100%;\n" +
                "        z-index: 100;\n" +
                "    }\n" +
                "    \n" +
                "    .sidebar.collapsed {\n" +
                "        transform: translateX(-100%);\n" +
                "    }\n" +
                "}";
    }
}