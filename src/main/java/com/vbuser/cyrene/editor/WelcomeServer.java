package com.vbuser.cyrene.editor;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WelcomeServer {
    private static final int PORT = 8080;
    private static final String PROJECT_FILE = "project.txt";
    private static HttpServer server;

    public static HttpServer getServer() {
        return server;
    }

    public static void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/", new WelcomeHandler());
            server.createContext("/api/open-project", new OpenProjectHandler());
            server.createContext("/api/create-project", new CreateProjectHandler());
            server.createContext("/api/recent-projects", new RecentProjectsHandler());
            server.createContext("/api/status", new StatusHandler());
            server.createContext("/static/", new StaticFileHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("文本编辑器已启动: http://localhost:" + PORT);
            System.out.println("按 Ctrl+C 停止服务器");

        } catch (IOException e) {
            System.err.println("无法启动HTTP服务器: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void stopServer() {
        if (server != null) {
            System.out.println("正在停止HTTP服务器...");
            server.stop(0);
            server = null;
        }
    }

    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = String.format("                    {\n" +
                        "                        \"status\": \"running\",\n" +
                        "                        \"javaVersion\": \"%s\",\n" +
                        "                        \"port\": %d,\n" +
                        "                        \"timestamp\": %d\n" +
                        "                    }", System.getProperty("java.version"), PORT, System.currentTimeMillis());

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class WelcomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<String> recentProjects = getRecentProjects();
                String response;
                if (recentProjects.isEmpty()) {
                    response = getCreateProjectPage();
                } else {
                    response = getEditorPage();
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class OpenProjectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String line;
                StringBuilder body = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }

                String projectPath = body.toString().replace("path=", "");
                saveProjectPath(projectPath);

                String response = "{\"status\":\"success\",\"message\":\"项目已打开\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class CreateProjectHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }

                String[] params = body.toString().split("&");
                String projectName = "";
                String projectPath = "";
                boolean initGit = false;

                for (String param : params) {
                    if (param.startsWith("name=")) {
                        projectName = java.net.URLDecoder.decode(param.substring(5), "UTF-8");
                    } else if (param.startsWith("path=")) {
                        projectPath = java.net.URLDecoder.decode(param.substring(5), "UTF-8");
                    } else if (param.startsWith("git=")) {
                        initGit = "true".equals(param.substring(4));
                    }
                }

                String fullPath = projectPath + File.separator + projectName;

                try {
                    Files.createDirectories(Paths.get(fullPath));

                    if (initGit) {
                        ProcessBuilder pb = new ProcessBuilder("git", "init");
                        pb.directory(new File(fullPath));
                        Process process = pb.start();
                        process.waitFor();
                    }

                    saveProjectPath(fullPath);

                    String escapedPath = fullPath.replace("\\", "\\\\").replace("\"", "\\\"");
                    String response = "{\"status\":\"success\",\"message\":\"项目已创建\",\"path\":\"" + escapedPath + "\"}";

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                } catch (Exception e) {
                    String response = "{\"status\":\"error\",\"message\":\"创建项目失败: " + e.getMessage().replace("\"", "\\\"") + "\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }
    }

    static class RecentProjectsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<String> recentProjects = getRecentProjects();
                String response = "{\"projects\":" + recentProjects + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/static/style.css")) {
                String response = getCSS();
                exchange.getResponseHeaders().set("Content-Type", "text/css");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    private static void saveProjectPath(String path) {
        try (PrintWriter out = new PrintWriter(new FileWriter(PROJECT_FILE, true))) {
            out.println(path);
        } catch (IOException e) {
            System.err.println("无法保存项目路径: " + e.getMessage());
        }
    }

    private static List<String> getRecentProjects() {
        List<String> projects = new ArrayList<>();
        try {
            Path path = Paths.get(PROJECT_FILE);
            if (Files.exists(path)) {
                projects = Files.readAllLines(path);
            }
        } catch (IOException e) {
            System.err.println("无法读取项目文件: " + e.getMessage());
        }
        return projects;
    }

    private static String getCreateProjectPage() {
        return "<!DOCTYPE html>\n" +
                "            <html lang=\"zh-CN\">\n" +
                "            <head>\n" +
                "                <meta charset=\"UTF-8\">\n" +
                "                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "                <title>Cyrene Editor - 创建新项目</title>\n" +
                "                <link rel=\"stylesheet\" href=\"/static/style.css\">\n" +
                "            </head>\n" +
                "            <body>\n" +
                "                <div class=\"container\">\n" +
                "                    <header class=\"header\">\n" +
                "                        <h1>Cyrene Editor</h1>\n" +
                "                        <p>创建新项目</p>\n" +
                "                    </header>\n" +
                "                    \n" +
                "                    <main class=\"main-content\">\n" +
                "                        <div class=\"welcome-section\">\n" +
                "                            <form id=\"create-project-form\" class=\"project-form\">\n" +
                "                                <div class=\"form-group\">\n" +
                "                                    <label for=\"project-name\">项目名称</label>\n" +
                "                                    <input type=\"text\" id=\"project-name\" name=\"project-name\" required>\n" +
                "                                </div>\n" +
                "                                \n" +
                "                                <div class=\"form-group\">\n" +
                "                                    <label for=\"project-path\">项目路径</label>\n" +
                "                                    <div class=\"path-input-group\">\n" +
                "                                        <input type=\"text\" id=\"project-path\" name=\"project-path\" required>\n" +
                "                                        <button type=\"button\" class=\"btn btn-outline\" onclick=\"selectFolder()\" disabled>\n" +
                "                                            <span class=\"icon\">📁</span>\n" +
                "                                            选择文件夹\n" +
                "                                        </button>\n" +
                "                                    </div>\n" +
                "                                    <small class=\"hint\">由于浏览器限制，请手动输入项目路径</small>\n" +
                "                                </div>\n" +
                "                                \n" +
                "                                <div class=\"form-group\">\n" +
                "                                    <label class=\"checkbox-container\">\n" +
                "                                        <input type=\"checkbox\" id=\"init-git\" name=\"init-git\">\n" +
                "                                        <span class=\"checkmark\"></span>\n" +
                "                                        <span class=\"checkbox-text\">初始化 Git 仓库</span>\n" +
                "                                    </label>\n" +
                "                                </div>\n" +
                "                                \n" +
                "                                <div class=\"form-actions\">\n" +
                "                                    <button type=\"submit\" class=\"btn btn-primary\">\n" +
                "                                        <span class=\"icon\">+</span>\n" +
                "                                        创建项目\n" +
                "                                    </button>\n" +
                "                                    <button type=\"button\" class=\"btn btn-outline\" onclick=\"openExistingProject()\">\n" +
                "                                        <span class=\"icon\">📂</span>\n" +
                "                                        打开现有项目\n" +
                "                                    </button>\n" +
                "                                </div>\n" +
                "                            </form>\n" +
                "                        </div>\n" +
                "                    </main>\n" +
                "                    \n" +
                "                </div>\n" +
                "                \n" +
                "                <script>\n" +
                "                    function selectFolder() {\n" +
                "                        alert('由于浏览器安全限制，无法使用原生文件夹选择器。请手动输入项目路径。');\n" +
                "                    }\n" +
                "                    \n" +
                "                    function openExistingProject() {\n" +
                "                        const projectPath = prompt('请输入现有项目路径:');\n" +
                "                        if (projectPath) {\n" +
                "                            fetch('/api/open-project', {\n" +
                "                                method: 'POST',\n" +
                "                                headers: {\n" +
                "                                    'Content-Type': 'application/x-www-form-urlencoded',\n" +
                "                                },\n" +
                "                                body: 'path=' + encodeURIComponent(projectPath)\n" +
                "                            })\n" +
                "                            .then(response => response.json())\n" +
                "                            .then(data => {\n" +
                "                                alert(data.message);\n" +
                "                                window.location.reload();\n" +
                "                            });\n" +
                "                        }\n" +
                "                    }\n" +
                "                    \n" +
                "                    document.getElementById('create-project-form').addEventListener('submit', function(e) {\n" +
                "                        e.preventDefault();\n" +
                "                        \n" +
                "                        const projectName = document.getElementById('project-name').value;\n" +
                "                        const projectPath = document.getElementById('project-path').value;\n" +
                "                        const initGit = document.getElementById('init-git').checked;\n" +
                "                        \n" +
                "                        if (!projectName || !projectPath) {\n" +
                "                            alert('请填写项目名称和路径');\n" +
                "                            return;\n" +
                "                        }\n" +
                "                        \n" +
                "                        fetch('/api/create-project', {\n" +
                "                            method: 'POST',\n" +
                "                            headers: {\n" +
                "                                'Content-Type': 'application/x-www-form-urlencoded',\n" +
                "                            },\n" +
                "                            body: 'name=' + encodeURIComponent(projectName) + \n" +
                "                                  '&path=' + encodeURIComponent(projectPath) + \n" +
                "                                  '&git=' + initGit\n" +
                "                        })\n" +
                "                        .then(response => response.json())\n" +
                "                        .then(data => {\n" +
                "                            if (data.status === 'success') {\n" +
                "                                alert('项目创建成功: ' + data.path);\n" +
                "                                window.location.reload();\n" +
                "                            } else {\n" +
                "                                alert('创建失败: ' + data.message);\n" +
                "                            }\n" +
                "                        })\n" +
                "                        .catch(error => {\n" +
                "                            alert('创建失败: ' + error);\n" +
                "                        });\n" +
                "                    });\n" +
                "                </script>\n" +
                "            </body>\n" +
                "            </html>";
    }

    private static String getEditorPage() {
        return "<!DOCTYPE html>\n" +
                "            <html lang=\"zh-CN\">\n" +
                "            <head>\n" +
                "                <meta charset=\"UTF-8\">\n" +
                "                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "                <title>Cyrene Editor</title>\n" +
                "                <link rel=\"stylesheet\" href=\"/static/style.css\">\n" +
                "            </head>\n" +
                "            <body>\n" +
                "                <div class=\"container\">\n" +
                "                    <header class=\"header\">\n" +
                "                        <h1>Cyrene Editor</h1>\n" +
                "                    </header>\n" +
                "                    \n" +
                "                    <main class=\"main-content\">\n" +
                "                        <div class=\"editor-placeholder\">\n" +
                "                            <h2>文本编辑器</h2>\n" +
                "                            <p>编辑器界面正在开发中...</p>\n" +
                "                            <button class=\"btn btn-outline\" onclick=\"showCreateProject()\">创建新项目</button>\n" +
                "                        </div>\n" +
                "                    </main>\n" +
                "                </div>\n" +
                "                \n" +
                "                <script>\n" +
                "                    function showCreateProject() {\n" +
                "                        window.location.href = '/';\n" +
                "                    }\n" +
                "                </script>\n" +
                "            </body>\n" +
                "            </html>";
    }

    private static String getCSS() {
        return ":root {\n" +
                "                --bg-primary: #2b2b2b;\n" +
                "                --bg-secondary: #3c3f41;\n" +
                "                --bg-tertiary: #323232;\n" +
                "                --text-primary: #cccccc;\n" +
                "                --text-secondary: #999999;\n" +
                "                --accent-color: #4e7ab5;\n" +
                "                --accent-hover: #5a8ac8;\n" +
                "                --border-color: #555555;\n" +
                "                --success-color: #499c54;\n" +
                "            }\n" +
                "            \n" +
                "            * {\n" +
                "                margin: 0;\n" +
                "                padding: 0;\n" +
                "                box-sizing: border-box;\n" +
                "            }\n" +
                "            \n" +
                "            body {\n" +
                "                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "                background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-tertiary) 100%);\n" +
                "                color: var(--text-primary);\n" +
                "                min-height: 100vh;\n" +
                "                line-height: 1.6;\n" +
                "            }\n" +
                "            \n" +
                "            .container {\n" +
                "                max-width: 900px;\n" +
                "                margin: 0 auto;\n" +
                "                min-height: 100vh;\n" +
                "                display: flex;\n" +
                "                flex-direction: column;\n" +
                "            }\n" +
                "            \n" +
                "            .header {\n" +
                "                text-align: center;\n" +
                "                padding: 3rem 2rem 2rem;\n" +
                "                border-bottom: 1px solid var(--border-color);\n" +
                "            }\n" +
                "            \n" +
                "            .header h1 {\n" +
                "                font-size: 2.5rem;\n" +
                "                margin-bottom: 0.5rem;\n" +
                "                background: linear-gradient(45deg, var(--accent-color), #67b0ff);\n" +
                "                -webkit-background-clip: text;\n" +
                "                -webkit-text-fill-color: transparent;\n" +
                "                background-clip: text;\n" +
                "            }\n" +
                "            \n" +
                "            .header p {\n" +
                "                color: var(--text-secondary);\n" +
                "                font-size: 1.1rem;\n" +
                "            }\n" +
                "            \n" +
                "            .main-content {\n" +
                "                flex: 1;\n" +
                "                padding: 2rem;\n" +
                "            }\n" +
                "            \n" +
                "            .welcome-section {\n" +
                "                background: var(--bg-secondary);\n" +
                "                border-radius: 8px;\n" +
                "                padding: 2rem;\n" +
                "                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);\n" +
                "            }\n" +
                "            \n" +
                "            .project-form {\n" +
                "                max-width: 500px;\n" +
                "                margin: 0 auto;\n" +
                "            }\n" +
                "            \n" +
                "            .form-group {\n" +
                "                margin-bottom: 1.5rem;\n" +
                "            }\n" +
                "            \n" +
                "            .form-group label {\n" +
                "                display: block;\n" +
                "                margin-bottom: 0.5rem;\n" +
                "                color: var(--text-primary);\n" +
                "                font-weight: 500;\n" +
                "            }\n" +
                "            \n" +
                "            .form-group input[type=\"text\"] {\n" +
                "                width: 100%;\n" +
                "                padding: 0.75rem;\n" +
                "                background: var(--bg-tertiary);\n" +
                "                border: 1px solid var(--border-color);\n" +
                "                border-radius: 4px;\n" +
                "                color: var(--text-primary);\n" +
                "                font-size: 1rem;\n" +
                "            }\n" +
                "            \n" +
                "            .form-group input[type=\"text\"]:focus {\n" +
                "                outline: none;\n" +
                "                border-color: var(--accent-color);\n" +
                "            }\n" +
                "            \n" +
                "            .path-input-group {\n" +
                "                display: flex;\n" +
                "                gap: 0.5rem;\n" +
                "            }\n" +
                "            \n" +
                "            .path-input-group input {\n" +
                "                flex: 1;\n" +
                "            }\n" +
                "            \n" +
                "            .hint {\n" +
                "                color: var(--text-secondary);\n" +
                "                font-size: 0.85rem;\n" +
                "                margin-top: 0.25rem;\n" +
                "                display: block;\n" +
                "            }\n" +
                "            \n" +
                "            .checkbox-container {\n" +
                "                display: flex;\n" +
                "                align-items: center;\n" +
                "                cursor: pointer;\n" +
                "                padding: 0.5rem 0;\n" +
                "                font-weight: normal;\n" +
                "                user-select: none;\n" +
                "                white-space: nowrap;\n" +
                "                line-height: 20px;\n" +
                "            }\n" +
                "            \n" +
                "            .checkbox-container input[type=\"checkbox\"] {\n" +
                "                display: none;\n" +
                "            }\n" +
                "            \n" +
                "            .checkmark {\n" +
                "                width: 20px;\n" +
                "                height: 20px;\n" +
                "                background: var(--bg-tertiary);\n" +
                "                border: 2px solid var(--border-color);\n" +
                "                border-radius: 4px;\n" +
                "                margin-right: 8px;\n" +
                "                position: relative;\n" +
                "                transition: all 0.2s ease;\n" +
                "                display: inline-block;\n" +
                "                flex-shrink: 0;\n" +
                "                vertical-align: middle;\n" +
                "            }\n" +
                "            \n" +
                "            .checkbox-container:hover .checkmark {\n" +
                "                border-color: var(--accent-color);\n" +
                "            }\n" +
                "            \n" +
                "            .checkbox-container input[type=\"checkbox\"]:checked + .checkmark {\n" +
                "                background: var(--accent-color);\n" +
                "                border-color: var(--accent-color);\n" +
                "            }\n" +
                "            \n" +
                "            .checkbox-container input[type=\"checkbox\"]:checked + .checkmark::after {\n" +
                "                content: '';\n" +
                "                width: 5px;\n" +
                "                height: 10px;\n" +
                "                border: solid white;\n" +
                "                border-width: 0 2px 2px 0;\n" +
                "                transform: rotate(45deg);\n" +
                "                position: absolute;\n" +
                "                top: 2px;\n" +
                "                left: 6px;\n" +
                "            }\n" +
                "            \n" +
                "            .checkbox-text {\n" +
                "                display: inline;\n" +
                "                color: var(--text-primary);\n" +
                "                font-weight: normal;\n" +
                "                vertical-align: middle;\n" +
                "                line-height: 20px;\n" +
                "            }\n" +
                "            \n" +
                "            .form-actions {\n" +
                "                display: flex;\n" +
                "                gap: 1rem;\n" +
                "                margin-top: 2rem;\n" +
                "                flex-wrap: wrap;\n" +
                "            }\n" +
                "            \n" +
                "            .btn {\n" +
                "                padding: 0.75rem 1.5rem;\n" +
                "                border: none;\n" +
                "                border-radius: 4px;\n" +
                "                font-size: 1rem;\n" +
                "                cursor: pointer;\n" +
                "                transition: all 0.3s ease;\n" +
                "                display: flex;\n" +
                "                align-items: center;\n" +
                "                gap: 0.5rem;\n" +
                "                text-decoration: none;\n" +
                "            }\n" +
                "            \n" +
                "            .btn:disabled {\n" +
                "                opacity: 0.5;\n" +
                "                cursor: not-allowed;\n" +
                "            }\n" +
                "            \n" +
                "            .btn:disabled:hover {\n" +
                "                transform: none;\n" +
                "                background: transparent;\n" +
                "                border-color: var(--border-color);\n" +
                "            }\n" +
                "            \n" +
                "            .btn-primary {\n" +
                "                background: var(--accent-color);\n" +
                "                color: white;\n" +
                "            }\n" +
                "            \n" +
                "            .btn-primary:hover {\n" +
                "                background: var(--accent-hover);\n" +
                "                transform: translateY(-2px);\n" +
                "            }\n" +
                "            \n" +
                "            .btn-outline {\n" +
                "                background: transparent;\n" +
                "                color: var(--text-primary);\n" +
                "                border: 1px solid var(--border-color);\n" +
                "            }\n" +
                "            \n" +
                "            .btn-outline:hover {\n" +
                "                background: var(--bg-tertiary);\n" +
                "                border-color: var(--accent-color);\n" +
                "            }\n" +
                "            \n" +
                "            .editor-placeholder {\n" +
                "                text-align: center;\n" +
                "                padding: 4rem 2rem;\n" +
                "                background: var(--bg-secondary);\n" +
                "                margin: 2rem;\n" +
                "                border-radius: 8px;\n" +
                "            }\n" +
                "            \n" +
                "            .editor-placeholder h2 {\n" +
                "                margin-bottom: 1rem;\n" +
                "                color: var(--accent-color);\n" +
                "            }\n" +
                "            \n" +
                "            .editor-placeholder p {\n" +
                "                margin-bottom: 2rem;\n" +
                "                color: var(--text-secondary);\n" +
                "            }\n" +
                "            \n" +
                "            .icon {\n" +
                "                font-size: 1.1rem;\n" +
                "            }\n" +
                "            \n" +
                "            @media (max-width: 768px) {\n" +
                "                .form-actions {\n" +
                "                    flex-direction: column;\n" +
                "                }\n" +
                "                \n" +
                "                .btn {\n" +
                "                    justify-content: center;\n" +
                "                }\n" +
                "                \n" +
                "                .container {\n" +
                "                    margin: 0;\n" +
                "                }\n" +
                "                \n" +
                "                .main-content {\n" +
                "                    padding: 1rem;\n" +
                "                }\n" +
                "                \n" +
                "                .path-input-group {\n" +
                "                    flex-direction: column;\n" +
                "                }\n" +
                "            }";
    }
}