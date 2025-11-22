package com.vbuser.cyrene.editor;

public class NodeEditorFrontend {

    public static String getNodeEditorPage() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Cyrene Node Editor</title>\n" +
                "    <link rel=\"stylesheet\" href=\"/static/node-editor.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"node-editor-container\">\n" +
                "        <div class=\"node-palette\" id=\"node-palette\">\n" +
                "            <h3>节点库</h3>\n" +
                "            <div class=\"node-item\" data-type=\"NodePrintLog\">\n" +
                "                <div>打印字符串</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">输出文本到控制台</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeLimitedCycle\">\n" +
                "                <div>有限循环</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">指定范围的循环</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeQuitCycle\">\n" +
                "                <div>跳出循环</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">提前终止循环</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"node-canvas\" id=\"node-canvas\">\n" +
                "            <svg id=\"connection-layer\" style=\"position: absolute; width: 100%; height: 100%; pointer-events: none;\"></svg>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"property-panel\" id=\"property-panel\">\n" +
                "            <h3>属性</h3>\n" +
                "            <div id=\"property-content\">\n" +
                "                <p>选择一个节点来编辑属性</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <script src=\"/static/node-editor.js\"></script>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String getNodeEditorJS() {
        return "class EnhancedNodeEditor {\n" +
                "    constructor() {\n" +
                "        this.nodes = [];\n" +
                "        this.connections = [];\n" +
                "        this.selectedNode = null;\n" +
                "        this.dragging = false;\n" +
                "        this.draggingNode = null;\n" +
                "        this.dragOffset = { x: 0, y: 0 };\n" +
                "        this.connecting = false;\n" +
                "        this.connectionSource = null;\n" +
                "        this.nodeConfigs = {};\n" +
                "        \n" +
                "        this.init();\n" +
                "    }\n" +
                "    \n" +
                "    async init() {\n" +
                "        await this.loadNodeConfigs();\n" +
                "        this.setupEventListeners();\n" +
                "        this.loadFromFile();\n" +
                "    }\n" +
                "    \n" +
                "    async loadNodeConfigs() {\n" +
                "        try {\n" +
                "            const response = await fetch('/api/node/config');\n" +
                "            const data = await response.json();\n" +
                "            this.nodeConfigs = data.nodeTypes.reduce((acc, nodeType) => {\n" +
                "                acc[nodeType.type] = nodeType;\n" +
                "                return acc;\n" +
                "            }, {});\n" +
                "        } catch (error) {\n" +
                "            console.error('加载节点配置失败:', error);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    setupEventListeners() {\n" +
                "        const palette = document.getElementById('node-palette');\n" +
                "        const canvas = document.getElementById('node-canvas');\n" +
                "        \n" +
                "        // 节点拖拽创建\n" +
                "        palette.addEventListener('mousedown', (e) => {\n" +
                "            if (e.target.closest('.node-item')) {\n" +
                "                const item = e.target.closest('.node-item');\n" +
                "                const type = item.dataset.type;\n" +
                "                this.createNode(type, e.clientX, e.clientY);\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        // 画布事件\n" +
                "        canvas.addEventListener('mousedown', this.handleCanvasMouseDown.bind(this));\n" +
                "        canvas.addEventListener('mousemove', this.handleCanvasMouseMove.bind(this));\n" +
                "        canvas.addEventListener('mouseup', this.handleCanvasMouseUp.bind(this));\n" +
                "        canvas.addEventListener('dblclick', this.handleCanvasDoubleClick.bind(this));\n" +
                "        \n" +
                "        // 防止画布上的默认拖拽行为\n" +
                "        canvas.addEventListener('dragstart', (e) => e.preventDefault());\n" +
                "    }\n" +
                "    \n" +
                "    createNode(type, x, y) {\n" +
                "        const nodeId = Date.now();\n" +
                "        const config = this.nodeConfigs[type];\n" +
                "        \n" +
                "        // 计算节点位置，确保在可见区域内\n" +
                "        const canvasRect = document.getElementById('node-canvas').getBoundingClientRect();\n" +
                "        const paletteWidth = 250; // 节点面板宽度\n" +
                "        const nodeX = x - paletteWidth - 60; // 调整位置，避免被侧边栏遮挡\n" +
                "        const nodeY = y - 60;\n" +
                "        \n" +
                "        const node = {\n" +
                "            id: nodeId,\n" +
                "            type: type,\n" +
                "            name: config ? config.name : type,\n" +
                "            position: { x: nodeX, y: nodeY },\n" +
                "            properties: {}\n" +
                "        };\n" +
                "        \n" +
                "        if (config) {\n" +
                "            config.inputs.forEach(input => {\n" +
                "                if (input.index >= 0) {\n" +
                "                    node.properties[input.name] = {\n" +
                "                        type: 'constant',\n" +
                "                        value: this.getDefaultValue(input.type),\n" +
                "                        dataType: input.type\n" +
                "                    };\n" +
                "                }\n" +
                "            });\n" +
                "        }\n" +
                "        \n" +
                "        this.nodes.push(node);\n" +
                "        this.renderNode(node);\n" +
                "        this.saveToFile();\n" +
                "    }\n" +
                "    \n" +
                "    getDefaultValue(type) {\n" +
                "        switch(type) {\n" +
                "            case 'int': return 0;\n" +
                "            case 'string': return '';\n" +
                "            case 'boolean': return false;\n" +
                "            default: return '';\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    renderNode(node) {\n" +
                "        const config = this.nodeConfigs[node.type];\n" +
                "        if (!config) return;\n" +
                "        \n" +
                "        const nodeEl = document.createElement('div');\n" +
                "        nodeEl.className = 'node';\n" +
                "        nodeEl.style.left = node.position.x + 'px';\n" +
                "        nodeEl.style.top = node.position.y + 'px';\n" +
                "        nodeEl.dataset.nodeId = node.id;\n" +
                "        \n" +
                "        let portsHTML = '<div class=\"node-ports\">';\n" +
                "        portsHTML += '<div class=\"input-ports\">';\n" +
                "        \n" +
                "        config.inputs.forEach(input => {\n" +
                "            const portType = input.index < 0 ? 'logic' : 'param';\n" +
                "            portsHTML += `\n" +
                "                <div class=\"port input-port ${portType}-port\" \n" +
                "                     data-port-name=\"${input.name}\" \n" +
                "                     data-port-type=\"input\"\n" +
                "                     data-data-type=\"${input.type}\">\n" +
                "                    <div class=\"port-dot\"></div>\n" +
                "                    <span>${input.description}</span>\n" +
                "                </div>\n" +
                "            `;\n" +
                "        });\n" +
                "        \n" +
                "        portsHTML += '</div><div class=\"output-ports\">';\n" +
                "        \n" +
                "        config.outputs.forEach(output => {\n" +
                "            const portType = output.index < 0 ? 'logic' : 'param';\n" +
                "            portsHTML += `\n" +
                "                <div class=\"port output-port ${portType}-port\"\n" +
                "                     data-port-name=\"${output.name}\"\n" +
                "                     data-port-type=\"output\" \n" +
                "                     data-data-type=\"${output.type}\">\n" +
                "                    <span>${output.description}</span>\n" +
                "                    <div class=\"port-dot\"></div>\n" +
                "                </div>\n" +
                "            `;\n" +
                "        });\n" +
                "        \n" +
                "        portsHTML += '</div></div>';\n" +
                "        \n" +
                "        nodeEl.innerHTML = `\n" +
                "            <div class=\"node-header\">${node.name}</div>\n" +
                "            <div class=\"node-content\">\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">ID: ${node.id}</div>\n" +
                "                ${portsHTML}\n" +
                "            </div>\n" +
                "        `;\n" +
                "        \n" +
                "        document.getElementById('node-canvas').appendChild(nodeEl);\n" +
                "        this.setupNodeEvents(nodeEl);\n" +
                "    }\n" +
                "    \n" +
                "    setupNodeEvents(nodeEl) {\n" +
                "        const nodeId = parseInt(nodeEl.dataset.nodeId);\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        \n" +
                "        if (!node) return;\n" +
                "        \n" +
                "        // 节点选择\n" +
                "        nodeEl.addEventListener('mousedown', (e) => {\n" +
                "            if (!e.target.classList.contains('port-dot')) {\n" +
                "                e.preventDefault();\n" +
                "                this.selectNode(nodeEl);\n" +
                "                \n" +
                "                // 开始拖拽\n" +
                "                if (e.button === 0) { // 左键\n" +
                "                    this.startDragging(nodeEl, e);\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        // 端口连接\n" +
                "        const ports = nodeEl.querySelectorAll('.port');\n" +
                "        ports.forEach(port => {\n" +
                "            port.addEventListener('mousedown', (e) => {\n" +
                "                e.stopPropagation();\n" +
                "                this.startConnection(nodeEl, port);\n" +
                "            });\n" +
                "        });\n" +
                "    }\n" +
                "    \n" +
                "    startDragging(nodeEl, e) {\n" +
                "        this.dragging = true;\n" +
                "        this.draggingNode = nodeEl;\n" +
                "        \n" +
                "        // 计算鼠标相对于节点左上角的偏移\n" +
                "        const rect = nodeEl.getBoundingClientRect();\n" +
                "        this.dragOffset = {\n" +
                "            x: e.clientX - rect.left,\n" +
                "            y: e.clientY - rect.top\n" +
                "        };\n" +
                "        \n" +
                "        nodeEl.style.cursor = 'grabbing';\n" +
                "        document.addEventListener('mousemove', this.handleDragMove.bind(this));\n" +
                "        document.addEventListener('mouseup', this.handleDragEnd.bind(this));\n" +
                "    }\n" +
                "    \n" +
                "    handleDragMove(e) {\n" +
                "        if (!this.dragging || !this.draggingNode) return;\n" +
                "        \n" +
                "        const canvas = document.getElementById('node-canvas');\n" +
                "        const canvasRect = canvas.getBoundingClientRect();\n" +
                "        \n" +
                "        // 计算新位置\n" +
                "        const newX = e.clientX - canvasRect.left - this.dragOffset.x;\n" +
                "        const newY = e.clientY - canvasRect.top - this.dragOffset.y;\n" +
                "        \n" +
                "        // 更新节点位置\n" +
                "        this.draggingNode.style.left = newX + 'px';\n" +
                "        this.draggingNode.style.top = newY + 'px';\n" +
                "        \n" +
                "        // 更新节点数据\n" +
                "        const nodeId = parseInt(this.draggingNode.dataset.nodeId);\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        if (node) {\n" +
                "            node.position.x = newX;\n" +
                "            node.position.y = newY;\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleDragEnd() {\n" +
                "        if (this.draggingNode) {\n" +
                "            this.draggingNode.style.cursor = 'grab';\n" +
                "        }\n" +
                "        \n" +
                "        this.dragging = false;\n" +
                "        this.draggingNode = null;\n" +
                "        \n" +
                "        document.removeEventListener('mousemove', this.handleDragMove.bind(this));\n" +
                "        document.removeEventListener('mouseup', this.handleDragEnd.bind(this));\n" +
                "        \n" +
                "        // 保存位置变化\n" +
                "        this.saveToFile();\n" +
                "    }\n" +
                "    \n" +
                "    startConnection(nodeEl, portEl) {\n" +
                "        this.connecting = true;\n" +
                "        this.connectionSource = {\n" +
                "            nodeId: parseInt(nodeEl.dataset.nodeId),\n" +
                "            portName: portEl.dataset.portName,\n" +
                "            portType: portEl.dataset.portType,\n" +
                "            dataType: portEl.dataset.dataType\n" +
                "        };\n" +
                "        \n" +
                "        console.log('开始连接:', this.connectionSource);\n" +
                "    }\n" +
                "    \n" +
                "    selectNode(nodeEl) {\n" +
                "        document.querySelectorAll('.node.selected').forEach(node => {\n" +
                "            node.classList.remove('selected');\n" +
                "        });\n" +
                "        \n" +
                "        nodeEl.classList.add('selected');\n" +
                "        this.selectedNode = this.nodes.find(node => node.id === parseInt(nodeEl.dataset.nodeId));\n" +
                "        this.showNodeProperties(this.selectedNode);\n" +
                "    }\n" +
                "    \n" +
                "    showNodeProperties(node) {\n" +
                "        const content = document.getElementById('property-content');\n" +
                "        if (!node) {\n" +
                "            content.innerHTML = '<p>选择一个节点来编辑属性</p>';\n" +
                "            return;\n" +
                "        }\n" +
                "        \n" +
                "        let html = `<h4>${node.name}</h4>`;\n" +
                "        \n" +
                "        Object.entries(node.properties).forEach(([key, prop]) => {\n" +
                "            if (prop.type === 'constant') {\n" +
                "                html += `\n" +
                "                    <div class=\"property-group\">\n" +
                "                        <label>${key}</label>\n" +
                "                        <input type=\"text\" \n" +
                "                               value=\"${prop.value}\" \n" +
                "                               onchange=\"editor.updateNodeProperty(${node.id}, '${key}', this.value)\">\n" +
                "                    </div>\n" +
                "                `;\n" +
                "            } else {\n" +
                "                html += `\n" +
                "                    <div class=\"property-group\">\n" +
                "                        <label>${key}</label>\n" +
                "                        <div style=\"color: #ccc; font-size: 12px;\">\n" +
                "                            连接到: 节点 ${prop.sourceNode} 的 ${prop.sourceOutput}\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                `;\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        content.innerHTML = html;\n" +
                "    }\n" +
                "    \n" +
                "    updateNodeProperty(nodeId, property, value) {\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        if (node && node.properties[property]) {\n" +
                "            node.properties[property].value = value;\n" +
                "            this.saveToFile();\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    saveToFile() {\n" +
                "        const data = {\n" +
                "            version: \"1.0\",\n" +
                "            name: \"节点图\",\n" +
                "            nodes: this.nodes,\n" +
                "            connections: this.connections\n" +
                "        };\n" +
                "        \n" +
                "        if (window.currentPhnFile) {\n" +
                "            fetch('/api/file/save', {\n" +
                "                method: 'POST',\n" +
                "                headers: { 'Content-Type': 'application/json' },\n" +
                "                body: JSON.stringify({\n" +
                "                    path: window.currentPhnFile,\n" +
                "                    content: JSON.stringify(data, null, 2)\n" +
                "                })\n" +
                "            });\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    loadFromFile() {\n" +
                "        const urlParams = new URLSearchParams(window.location.search);\n" +
                "        const file = urlParams.get('file');\n" +
                "        if (file) {\n" +
                "            window.currentPhnFile = file;\n" +
                "            fetch('/api/file/content?path=' + encodeURIComponent(file))\n" +
                "                .then(response => response.json())\n" +
                "                .then(data => {\n" +
                "                    if (data.content) {\n" +
                "                        const graphData = JSON.parse(data.content);\n" +
                "                        this.nodes = graphData.nodes || [];\n" +
                "                        this.connections = graphData.connections || [];\n" +
                "                        this.renderAllNodes();\n" +
                "                    }\n" +
                "                });\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    renderAllNodes() {\n" +
                "        this.nodes.forEach(node => this.renderNode(node));\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseDown(e) {\n" +
                "        // 点击空白处取消选择\n" +
                "        if (e.target === document.getElementById('node-canvas')) {\n" +
                "            this.selectNode(null);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseMove(e) {\n" +
                "        // 处理拖拽移动（在handleDragMove中处理）\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseUp(e) {\n" +
                "        // 处理连接完成\n" +
                "        if (this.connecting && this.connectionSource) {\n" +
                "            // 这里可以添加连接目标的逻辑\n" +
                "            this.connecting = false;\n" +
                "            this.connectionSource = null;\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasDoubleClick(e) {\n" +
                "        // 双击画布创建默认节点\n" +
                "        if (e.target === document.getElementById('node-canvas')) {\n" +
                "            this.createNode('NodePrintLog', e.clientX, e.clientY);\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "const editor = new EnhancedNodeEditor();";
    }

    public static String getNodeEditorCSS() {
        return "/* 重置样式 */\n" +
                "* {\n" +
                "    margin: 0;\n" +
                "    padding: 0;\n" +
                "    box-sizing: border-box;\n" +
                "}\n" +
                "\n" +
                "html, body {\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    overflow: hidden;\n" +
                "}\n" +
                "\n" +
                ":root {\n" +
                "    --bg-primary: #1e1e1e;\n" +
                "    --bg-secondary: #2d2d2d;\n" +
                "    --bg-tertiary: #3c3c3c;\n" +
                "    --text-primary: #ffffff;\n" +
                "    --text-secondary: #cccccc;\n" +
                "    --accent-color: #4e7ab5;\n" +
                "    --logic-color: #d19a66;\n" +
                "    --param-color: #499c54;\n" +
                "}\n" +
                "        \n" +
                ".node-editor-container {\n" +
                "    display: flex;\n" +
                "    width: 100vw;\n" +
                "    height: 100vh;\n" +
                "    background: var(--bg-primary);\n" +
                "    color: white;\n" +
                "    font-family: Arial, sans-serif;\n" +
                "}\n" +
                "        \n" +
                ".node-palette {\n" +
                "    width: 250px;\n" +
                "    background: var(--bg-secondary);\n" +
                "    padding: 15px;\n" +
                "    border-right: 1px solid #444;\n" +
                "    overflow-y: auto;\n" +
                "    flex-shrink: 0;\n" +
                "}\n" +
                "\n" +
                ".node-palette h3 {\n" +
                "    margin-bottom: 15px;\n" +
                "    color: var(--accent-color);\n" +
                "}\n" +
                "        \n" +
                ".node-item {\n" +
                "    padding: 10px;\n" +
                "    margin: 8px 0;\n" +
                "    background: var(--bg-tertiary);\n" +
                "    border-radius: 6px;\n" +
                "    cursor: grab;\n" +
                "    user-select: none;\n" +
                "    border-left: 4px solid var(--accent-color);\n" +
                "    transition: all 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".node-item:hover {\n" +
                "    background: #4a4a4a;\n" +
                "    transform: translateY(-2px);\n" +
                "}\n" +
                "        \n" +
                ".node-canvas {\n" +
                "    flex: 1;\n" +
                "    position: relative;\n" +
                "    overflow: hidden;\n" +
                "    background: #252525;\n" +
                "    background-image: \n" +
                "        linear-gradient(rgba(255,255,255,.05) 1px, transparent 1px),\n" +
                "        linear-gradient(90deg, rgba(255,255,255,.05) 1px, transparent 1px);\n" +
                "    background-size: 20px 20px;\n" +
                "    cursor: default;\n" +
                "}\n" +
                "        \n" +
                ".node {\n" +
                "    position: absolute;\n" +
                "    min-width: 180px;\n" +
                "    background: var(--bg-secondary);\n" +
                "    border: 2px solid #555;\n" +
                "    border-radius: 8px;\n" +
                "    user-select: none;\n" +
                "    box-shadow: 0 4px 12px rgba(0,0,0,0.3);\n" +
                "    cursor: grab;\n" +
                "    transition: border-color 0.2s ease, box-shadow 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".node:hover {\n" +
                "    border-color: #666;\n" +
                "    box-shadow: 0 6px 16px rgba(0,0,0,0.4);\n" +
                "}\n" +
                "        \n" +
                ".node.selected {\n" +
                "    border-color: var(--accent-color);\n" +
                "    box-shadow: 0 0 0 2px rgba(78, 122, 181, 0.3);\n" +
                "}\n" +
                "        \n" +
                ".node-header {\n" +
                "    background: var(--accent-color);\n" +
                "    padding: 10px 12px;\n" +
                "    border-radius: 6px 6px 0 0;\n" +
                "    font-weight: bold;\n" +
                "    font-size: 14px;\n" +
                "    user-select: none;\n" +
                "}\n" +
                "        \n" +
                ".node-content {\n" +
                "    padding: 10px 12px;\n" +
                "}\n" +
                "        \n" +
                ".node-ports {\n" +
                "    display: flex;\n" +
                "    justify-content: space-between;\n" +
                "    padding: 8px 0;\n" +
                "    gap: 10px;\n" +
                "}\n" +
                "        \n" +
                ".input-ports, .output-ports {\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    gap: 6px;\n" +
                "}\n" +
                "        \n" +
                ".port {\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    gap: 6px;\n" +
                "    padding: 4px 8px;\n" +
                "    border-radius: 12px;\n" +
                "    font-size: 11px;\n" +
                "    cursor: pointer;\n" +
                "    transition: background-color 0.2s ease;\n" +
                "    user-select: none;\n" +
                "}\n" +
                "\n" +
                ".port:hover {\n" +
                "    background: rgba(255, 255, 255, 0.1);\n" +
                "}\n" +
                "        \n" +
                ".input-port {\n" +
                "    flex-direction: row;\n" +
                "}\n" +
                "        \n" +
                ".output-port {\n" +
                "    flex-direction: row-reverse;\n" +
                "}\n" +
                "        \n" +
                ".port-dot {\n" +
                "    width: 12px;\n" +
                "    height: 12px;\n" +
                "    border-radius: 50%;\n" +
                "    border: 2px solid white;\n" +
                "    flex-shrink: 0;\n" +
                "    transition: transform 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".port:hover .port-dot {\n" +
                "    transform: scale(1.2);\n" +
                "}\n" +
                "        \n" +
                ".logic-port .port-dot {\n" +
                "    background: var(--logic-color);\n" +
                "}\n" +
                "        \n" +
                ".param-port .port-dot {\n" +
                "    background: var(--param-color);\n" +
                "}\n" +
                "        \n" +
                ".connection {\n" +
                "    stroke-width: 3;\n" +
                "    fill: none;\n" +
                "    pointer-events: none;\n" +
                "}\n" +
                "        \n" +
                ".logic-connection {\n" +
                "    stroke: var(--logic-color);\n" +
                "}\n" +
                "        \n" +
                ".param-connection {\n" +
                "    stroke: var(--param-color);\n" +
                "    stroke-dasharray: 5,5;\n" +
                "}\n" +
                "        \n" +
                ".property-panel {\n" +
                "    width: 300px;\n" +
                "    background: var(--bg-secondary);\n" +
                "    border-left: 1px solid #444;\n" +
                "    padding: 20px;\n" +
                "    overflow-y: auto;\n" +
                "    flex-shrink: 0;\n" +
                "}\n" +
                "\n" +
                ".property-panel h3 {\n" +
                "    margin-bottom: 20px;\n" +
                "    color: var(--accent-color);\n" +
                "}\n" +
                "        \n" +
                ".property-group {\n" +
                "    margin-bottom: 20px;\n" +
                "}\n" +
                "        \n" +
                ".property-group label {\n" +
                "    display: block;\n" +
                "    margin-bottom: 8px;\n" +
                "    font-size: 14px;\n" +
                "    color: var(--text-secondary);\n" +
                "    font-weight: 500;\n" +
                "}\n" +
                "        \n" +
                ".property-group input {\n" +
                "    width: 100%;\n" +
                "    padding: 8px 12px;\n" +
                "    background: var(--bg-tertiary);\n" +
                "    border: 1px solid #555;\n" +
                "    border-radius: 4px;\n" +
                "    color: white;\n" +
                "    font-size: 14px;\n" +
                "    transition: border-color 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".property-group input:focus {\n" +
                "    outline: none;\n" +
                "    border-color: var(--accent-color);\n" +
                "}\n" +
                "\n" +
                "/* 滚动条样式 */\n" +
                ".node-palette::-webkit-scrollbar,\n" +
                ".property-panel::-webkit-scrollbar {\n" +
                "    width: 6px;\n" +
                "}\n" +
                "\n" +
                ".node-palette::-webkit-scrollbar-track,\n" +
                ".property-panel::-webkit-scrollbar-track {\n" +
                "    background: var(--bg-tertiary);\n" +
                "}\n" +
                "\n" +
                ".node-palette::-webkit-scrollbar-thumb,\n" +
                ".property-panel::-webkit-scrollbar-thumb {\n" +
                "    background: #555;\n" +
                "    border-radius: 3px;\n" +
                "}\n" +
                "\n" +
                ".node-palette::-webkit-scrollbar-thumb:hover,\n" +
                ".property-panel::-webkit-scrollbar-thumb:hover {\n" +
                "    background: #666;\n" +
                "}";
    }
}