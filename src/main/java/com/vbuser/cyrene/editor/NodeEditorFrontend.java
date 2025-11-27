package com.vbuser.cyrene.editor;

public class NodeEditorFrontend {

    public static String getNodeEditorPage() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Cyrene Editor - 节点图编辑器</title>\n" +
                "    <link rel=\"stylesheet\" href=\"/static/node-editor.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"node-editor-container\">\n" +
                "        <div class=\"node-palette\" id=\"node-palette\">\n" +
                "            <h3>节点库</h3>\n" +
                "            <div class=\"node-item\" data-type=\"NodePrintLog\">\n" +
                "                <div>打印字符串</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">可以在日志中输出一条字符串，一般用于逻辑检测和调试</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetLocalVariable\">\n" +
                "                <div>设置局部变量</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">与查询节点【查询局部变量的值】连接后可以覆盖该局部变量的值</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeLimitedCycle\">\n" +
                "                <div>有限循环</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">从【循环开始值】开始到【循环结束值】结束，会遍历其中的循环值，每次整数加一。每次循环会执行一次【循环体】后连接的节点逻辑。完成一次完整遍历后，会执行【循环完成】节点后连接的逻辑</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeQuitCycle\">\n" +
                "                <div>跳出循环</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">从有限循环中跳出。出引脚需要与节点【有限循环】的【跳出循环】入参相连</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeForwardEvent\">\n" +
                "                <div>转发事件</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">向指定目标实体转发此节点所在的执行流的源头事件。被转发的目标实体上的节点图上的同名事件会被触发</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeInsertValue\">\n" +
                "                <div>对列表插入值</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">向指定列表的指定序号插入值。被插入的值插入后会出现在列表的插入序号的位置</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeModifyValue\">\n" +
                "                <div>对列表修改值</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">修改指定列表的指定序号位置的值</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeRemoveValue\">\n" +
                "                <div>对列表移除值</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">移除指定列表的指定序号位置的值</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeCycleList\">\n" +
                "                <div>对列表迭代循环</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">按照列表顺序遍历循环指定列表</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSortList\">\n" +
                "                <div>列表排序</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">将指定列表按照排序方式进行排序</div>\n" +
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
                "        this.tempConnection = null;\n" +
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
                "            console.log('加载的节点配置:', this.nodeConfigs);\n" +
                "        } catch (error) {\n" +
                "            console.error('加载节点配置失败:', error);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    setupEventListeners() {\n" +
                "        const palette = document.getElementById('node-palette');\n" +
                "        const canvas = document.getElementById('node-canvas');\n" +
                "        \n" +
                "        palette.addEventListener('mousedown', (e) => {\n" +
                "            if (e.target.closest('.node-item')) {\n" +
                "                const item = e.target.closest('.node-item');\n" +
                "                const type = item.dataset.type;\n" +
                "                this.createNode(type, e.clientX, e.clientY);\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        canvas.addEventListener('mousedown', this.handleCanvasMouseDown.bind(this));\n" +
                "        canvas.addEventListener('mousemove', this.handleCanvasMouseMove.bind(this));\n" +
                "        canvas.addEventListener('mouseup', this.handleCanvasMouseUp.bind(this));\n" +
                "        canvas.addEventListener('dblclick', this.handleCanvasDoubleClick.bind(this));\n" +
                "        \n" +
                "        canvas.addEventListener('dragstart', (e) => e.preventDefault());\n" +
                "        \n" +
                "        document.addEventListener('keydown', this.handleKeyDown.bind(this));\n" +
                "    }\n" +
                "    \n" +
                "    createNode(type, x, y) {\n" +
                "        const nodeId = Date.now();\n" +
                "        const config = this.nodeConfigs[type];\n" +
                "        \n" +
                "        if (!config) {\n" +
                "            console.error('未知节点类型:', type);\n" +
                "            return;\n" +
                "        }\n" +
                "        \n" +
                "        const canvasRect = document.getElementById('node-canvas').getBoundingClientRect();\n" +
                "        const paletteWidth = 250;\n" +
                "        const nodeX = x - paletteWidth - 60;\n" +
                "        const nodeY = y - 60;\n" +
                "        \n" +
                "        const node = {\n" +
                "            id: nodeId,\n" +
                "            type: type,\n" +
                "            name: config.name,\n" +
                "            position: { x: nodeX, y: nodeY },\n" +
                "            properties: {}\n" +
                "        };\n" +
                "        \n" +
                "        config.inputs.forEach(input => {\n" +
                "            if (input.index >= 0) {\n" +
                "                node.properties[input.name] = {\n" +
                "                    type: 'constant',\n" +
                "                    value: this.getDefaultValue(input.type),\n" +
                "                    dataType: input.type\n" +
                "                };\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        this.nodes.push(node);\n" +
                "        this.renderNode(node);\n" +
                "        this.saveToFile();\n" +
                "        \n" +
                "        console.log('创建节点:', node);\n" +
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
                "        if (!config) {\n" +
                "            console.error('无法渲染未知节点类型:', node.type);\n" +
                "            return;\n" +
                "        }\n" +
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
                "        nodeEl.addEventListener('mousedown', (e) => {\n" +
                "            if (!e.target.classList.contains('port-dot')) {\n" +
                "                e.preventDefault();\n" +
                "                this.selectNode(nodeEl);\n" +
                "                \n" +
                "                if (e.button === 0) {\n" +
                "                    this.startDragging(nodeEl, e);\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
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
                "        const newX = e.clientX - canvasRect.left - this.dragOffset.x;\n" +
                "        const newY = e.clientY - canvasRect.top - this.dragOffset.y;\n" +
                "        \n" +
                "        this.draggingNode.style.left = newX + 'px';\n" +
                "        this.draggingNode.style.top = newY + 'px';\n" +
                "        \n" +
                "        const nodeId = parseInt(this.draggingNode.dataset.nodeId);\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        if (node) {\n" +
                "            node.position.x = newX;\n" +
                "            node.position.y = newY;\n" +
                "        }\n" +
                "        \n" +
                "        this.updateConnections();\n" +
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
                "        this.saveToFile();\n" +
                "    }\n" +
                "    \n" +
                "    startConnection(nodeEl, portEl) {\n" +
                "        this.connecting = true;\n" +
                "        this.connectionSource = {\n" +
                "            nodeId: parseInt(nodeEl.dataset.nodeId),\n" +
                "            portName: portEl.dataset.portName,\n" +
                "            portType: portEl.dataset.portType,\n" +
                "            dataType: portEl.dataset.dataType,\n" +
                "            element: portEl\n" +
                "        };\n" +
                "        \n" +
                "        this.startTempConnection(portEl);\n" +
                "        \n" +
                "        console.log('开始连接:', this.connectionSource);\n" +
                "    }\n" +
                "    \n" +
                "    startTempConnection(portEl) {\n" +
                "        const portRect = portEl.getBoundingClientRect();\n" +
                "        const canvasRect = document.getElementById('node-canvas').getBoundingClientRect();\n" +
                "        \n" +
                "        const startX = portRect.left + portRect.width / 2 - canvasRect.left;\n" +
                "        const startY = portRect.top + portRect.height / 2 - canvasRect.top;\n" +
                "        \n" +
                "        this.tempConnection = {\n" +
                "            startX: startX,\n" +
                "            startY: startY,\n" +
                "            endX: startX,\n" +
                "            endY: startY\n" +
                "        };\n" +
                "        \n" +
                "        this.drawTempConnection();\n" +
                "    }\n" +
                "    \n" +
                "    drawTempConnection() {\n" +
                "        const svg = document.getElementById('connection-layer');\n" +
                "        \n" +
                "        const oldTemp = svg.querySelector('.temp-connection');\n" +
                "        if (oldTemp) {\n" +
                "            oldTemp.remove();\n" +
                "        }\n" +
                "        \n" +
                "        if (this.tempConnection) {\n" +
                "            const line = document.createElementNS('http://www.w3.org/2000/svg', 'path');\n" +
                "            line.classList.add('connection', 'temp-connection');\n" +
                "            \n" +
                "            const isLogic = this.connectionSource.dataType === 'node';\n" +
                "            if (isLogic) {\n" +
                "                line.classList.add('logic-connection');\n" +
                "            } else {\n" +
                "                line.classList.add('param-connection');\n" +
                "            }\n" +
                "            \n" +
                "            const path = this.createBezierPath(\n" +
                "                this.tempConnection.startX, \n" +
                "                this.tempConnection.startY, \n" +
                "                this.tempConnection.endX, \n" +
                "                this.tempConnection.endY\n" +
                "            );\n" +
                "            \n" +
                "            line.setAttribute('d', path);\n" +
                "            svg.appendChild(line);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    createBezierPath(startX, startY, endX, endY) {\n" +
                "        const dx = endX - startX;\n" +
                "        const controlOffset = Math.min(Math.abs(dx) * 0.5, 100);\n" +
                "        \n" +
                "        const cp1x = startX + controlOffset;\n" +
                "        const cp1y = startY;\n" +
                "        const cp2x = endX - controlOffset;\n" +
                "        const cp2y = endY;\n" +
                "        \n" +
                "        return `M ${startX} ${startY} C ${cp1x} ${cp1y}, ${cp2x} ${cp2y}, ${endX} ${endY}`;\n" +
                "    }\n" +
                "    \n" +
                "    completeConnection(targetNodeEl, targetPortEl) {\n" +
                "        if (!this.connectionSource) return;\n" +
                "        \n" +
                "        const source = this.connectionSource;\n" +
                "        const target = {\n" +
                "            nodeId: parseInt(targetNodeEl.dataset.nodeId),\n" +
                "            portName: targetPortEl.dataset.portName,\n" +
                "            portType: targetPortEl.dataset.portType,\n" +
                "            dataType: targetPortEl.dataset.dataType\n" +
                "        };\n" +
                "        \n" +
                "        if (this.isConnectionValid(source, target)) {\n" +
                "            const connection = {\n" +
                "                id: 'conn_' + Date.now(),\n" +
                "                source: source,\n" +
                "                target: target,\n" +
                "                type: source.dataType === 'node' ? 'logic' : 'parameter'\n" +
                "            };\n" +
                "            \n" +
                "            this.connections.push(connection);\n" +
                "            \n" +
                "            if (connection.type === 'parameter') {\n" +
                "                this.updateNodePropertyConnection(target.nodeId, target.portName, source);\n" +
                "            }\n" +
                "            \n" +
                "            this.saveToFile();\n" +
                "            this.renderConnections();\n" +
                "            \n" +
                "            console.log('连接创建成功:', connection);\n" +
                "        } else {\n" +
                "            console.log('连接无效');\n" +
                "        }\n" +
                "        \n" +
                "        this.cleanupConnection();\n" +
                "    }\n" +
                "    \n" +
                "    isConnectionValid(source, target) {\n" +
                "        if (source.nodeId === target.nodeId) {\n" +
                "            alert('不能连接同一个节点');\n" +
                "            return false;\n" +
                "        }\n" +
                "        \n" +
                "        if (source.portType === target.portType) {\n" +
                "            alert('只能连接输出端口到输入端口');\n" +
                "            return false;\n" +
                "        }\n" +
                "        \n" +
                "        if (source.dataType !== target.dataType) {\n" +
                "            alert('数据类型不匹配');\n" +
                "            return false;\n" +
                "        }\n" +
                "        \n" +
                "        const existingConnection = this.connections.find(conn => \n" +
                "            conn.source.nodeId === source.nodeId &&\n" +
                "            conn.source.portName === source.portName &&\n" +
                "            conn.target.nodeId === target.nodeId &&\n" +
                "            conn.target.portName === target.portName\n" +
                "        );\n" +
                "        \n" +
                "        if (existingConnection) {\n" +
                "            alert('连接已存在');\n" +
                "            return false;\n" +
                "        }\n" +
                "        \n" +
                "        return true;\n" +
                "    }\n" +
                "    \n" +
                "    updateNodePropertyConnection(nodeId, propertyName, source) {\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        if (node && node.properties[propertyName]) {\n" +
                "            node.properties[propertyName] = {\n" +
                "                type: 'connection',\n" +
                "                sourceNode: source.nodeId,\n" +
                "                sourceOutput: source.portName,\n" +
                "                dataType: source.dataType\n" +
                "            };\n" +
                "            \n" +
                "            if (this.selectedNode && this.selectedNode.id === nodeId) {\n" +
                "                this.showNodeProperties(this.selectedNode);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    removeNodePropertyConnection(nodeId, propertyName) {\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        if (node && node.properties[propertyName]) {\n" +
                "            const dataType = node.properties[propertyName].dataType;\n" +
                "            node.properties[propertyName] = {\n" +
                "                type: 'constant',\n" +
                "                value: this.getDefaultValue(dataType),\n" +
                "                dataType: dataType\n" +
                "            };\n" +
                "            \n" +
                "            if (this.selectedNode && this.selectedNode.id === nodeId) {\n" +
                "                this.showNodeProperties(this.selectedNode);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    cleanupConnection() {\n" +
                "        this.connecting = false;\n" +
                "        this.connectionSource = null;\n" +
                "        this.tempConnection = null;\n" +
                "        \n" +
                "        const svg = document.getElementById('connection-layer');\n" +
                "        const tempLine = svg.querySelector('.temp-connection');\n" +
                "        if (tempLine) {\n" +
                "            tempLine.remove();\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    selectNode(nodeEl) {\n" +
                "        document.querySelectorAll('.node.selected').forEach(node => {\n" +
                "            node.classList.remove('selected');\n" +
                "        });\n" +
                "        \n" +
                "        if (nodeEl) {\n" +
                "            nodeEl.classList.add('selected');\n" +
                "            this.selectedNode = this.nodes.find(node => node.id === parseInt(nodeEl.dataset.nodeId));\n" +
                "            this.showNodeProperties(this.selectedNode);\n" +
                "        } else {\n" +
                "            this.selectedNode = null;\n" +
                "            this.showNodeProperties(null);\n" +
                "        }\n" +
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
                "        html += `<div style=\"font-size: 12px; color: #999; margin-bottom: 15px;\">ID: ${node.id}</div>`;\n" +
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
                "            } else if (prop.type === 'connection') {\n" +
                "                html += `\n" +
                "                    <div class=\"property-group\">\n" +
                "                        <label>${key}</label>\n" +
                "                        <div style=\"color: #ccc; font-size: 12px; margin-bottom: 5px;\">\n" +
                "                            连接到: 节点 ${prop.sourceNode} 的 ${prop.sourceOutput}\n" +
                "                        </div>\n" +
                "                        <button type=\"button\" class=\"disconnect-btn\" onclick=\"editor.disconnectProperty(${node.id}, '${key}')\">断开连接</button>\n" +
                "                    </div>\n" +
                "                `;\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        html += `\n" +
                "            <div class=\"property-group\" style=\"margin-top: 20px; border-top: 1px solid #444; padding-top: 15px;\">\n" +
                "                <button type=\"button\" class=\"delete-node-btn\" onclick=\"editor.deleteNode(${node.id})\">删除节点</button>\n" +
                "            </div>\n" +
                "        `;\n" +
                "        \n" +
                "        content.innerHTML = html;\n" +
                "    }\n" +
                "    \n" +
                "    updateNodeProperty(nodeId, property, value) {\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        if (node && node.properties[property] && node.properties[property].type === 'constant') {\n" +
                "            node.properties[property].value = value;\n" +
                "            this.saveToFile();\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    disconnectProperty(nodeId, propertyName) {\n" +
                "        this.connections = this.connections.filter(conn => \n" +
                "            !(conn.target.nodeId === nodeId && conn.target.portName === propertyName)\n" +
                "        );\n" +
                "        \n" +
                "        this.removeNodePropertyConnection(nodeId, propertyName);\n" +
                "        \n" +
                "        this.saveToFile();\n" +
                "        this.renderConnections();\n" +
                "    }\n" +
                "    \n" +
                "    deleteNode(nodeId) {\n" +
                "        if (confirm('确定要删除这个节点吗？')) {\n" +
                "            this.nodes = this.nodes.filter(n => n.id !== nodeId);\n" +
                "            \n" +
                "            this.connections = this.connections.filter(conn => \n" +
                "                conn.source.nodeId !== nodeId && conn.target.nodeId !== nodeId\n" +
                "            );\n" +
                "            \n" +
                "            const nodeEl = document.querySelector(`[data-node-id=\"${nodeId}\"]`);\n" +
                "            if (nodeEl) {\n" +
                "                nodeEl.remove();\n" +
                "            }\n" +
                "            \n" +
                "            this.selectNode(null);\n" +
                "            \n" +
                "            this.saveToFile();\n" +
                "            this.renderConnections();\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    renderConnections() {\n" +
                "        const svg = document.getElementById('connection-layer');\n" +
                "        \n" +
                "        const connections = svg.querySelectorAll('.connection:not(.temp-connection)');\n" +
                "        connections.forEach(conn => conn.remove());\n" +
                "        \n" +
                "        this.connections.forEach(conn => {\n" +
                "            this.drawConnection(conn);\n" +
                "        });\n" +
                "    }\n" +
                "    \n" +
                "    drawConnection(connection) {\n" +
                "        const sourceNodeEl = document.querySelector(`[data-node-id=\"${connection.source.nodeId}\"]`);\n" +
                "        const targetNodeEl = document.querySelector(`[data-node-id=\"${connection.target.nodeId}\"]`);\n" +
                "        \n" +
                "        if (!sourceNodeEl || !targetNodeEl) return;\n" +
                "        \n" +
                "        const sourcePortEl = sourceNodeEl.querySelector(`[data-port-name=\"${connection.source.portName}\"]`);\n" +
                "        const targetPortEl = targetNodeEl.querySelector(`[data-port-name=\"${connection.target.portName}\"]`);\n" +
                "        \n" +
                "        if (!sourcePortEl || !targetPortEl) return;\n" +
                "        \n" +
                "        const sourceRect = sourcePortEl.getBoundingClientRect();\n" +
                "        const targetRect = targetPortEl.getBoundingClientRect();\n" +
                "        const canvasRect = document.getElementById('node-canvas').getBoundingClientRect();\n" +
                "        \n" +
                "        const startX = sourceRect.left + sourceRect.width / 2 - canvasRect.left;\n" +
                "        const startY = sourceRect.top + sourceRect.height / 2 - canvasRect.top;\n" +
                "        const endX = targetRect.left + targetRect.width / 2 - canvasRect.left;\n" +
                "        const endY = targetRect.top + targetRect.height / 2 - canvasRect.top;\n" +
                "        \n" +
                "        const svg = document.getElementById('connection-layer');\n" +
                "        const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');\n" +
                "        \n" +
                "        path.classList.add('connection');\n" +
                "        if (connection.type === 'logic') {\n" +
                "            path.classList.add('logic-connection');\n" +
                "        } else {\n" +
                "            path.classList.add('param-connection');\n" +
                "        }\n" +
                "        \n" +
                "        const pathData = this.createBezierPath(startX, startY, endX, endY);\n" +
                "        path.setAttribute('d', pathData);\n" +
                "        \n" +
                "        svg.appendChild(path);\n" +
                "    }\n" +
                "    \n" +
                "    updateConnections() {\n" +
                "        this.renderConnections();\n" +
                "    }\n" +
                "    \n" +
                "    saveToFile() {\n" +
                "        const data = {\n" +
                "            version: \"1.0\",\n" +
                "            name: \"节点图\",\n" +
                "            nodes: this.nodes,\n" +
                "            connections: this.connections.map(conn => ({\n" +
                "                id: conn.id,\n" +
                "                type: conn.type,\n" +
                "                sourceNode: conn.source.nodeId,\n" +
                "                sourcePort: conn.source.portName,\n" +
                "                targetNode: conn.target.nodeId,\n" +
                "                targetPort: conn.target.portName\n" +
                "            }))\n" +
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
                "                        \n" +
                "                        this.connections = (graphData.connections || []).map(conn => ({\n" +
                "                            id: conn.id,\n" +
                "                            type: conn.type,\n" +
                "                            source: {\n" +
                "                                nodeId: conn.sourceNode,\n" +
                "                                portName: conn.sourcePort,\n" +
                "                                portType: 'output',\n" +
                "                                dataType: conn.type === 'logic' ? 'node' : 'int'\n" +
                "                            },\n" +
                "                            target: {\n" +
                "                                nodeId: conn.targetNode,\n" +
                "                                portName: conn.targetPort,\n" +
                "                                portType: 'input',\n" +
                "                                dataType: conn.type === 'logic' ? 'node' : 'int'\n" +
                "                            }\n" +
                "                        }));\n" +
                "                        \n" +
                "                        this.connections.forEach(conn => {\n" +
                "                            if (conn.type === 'parameter') {\n" +
                "                                this.updateNodePropertyConnection(\n" +
                "                                    conn.target.nodeId, \n" +
                "                                    conn.target.portName, \n" +
                "                                    conn.source\n" +
                "                                );\n" +
                "                            }\n" +
                "                        });\n" +
                "                        \n" +
                "                        this.renderAllNodes();\n" +
                "                        this.renderConnections();\n" +
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
                "        if (e.target === document.getElementById('node-canvas')) {\n" +
                "            this.selectNode(null);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseMove(e) {\n" +
                "        \n" +
                "        if (this.connecting && this.tempConnection) {\n" +
                "            const canvasRect = document.getElementById('node-canvas').getBoundingClientRect();\n" +
                "            this.tempConnection.endX = e.clientX - canvasRect.left;\n" +
                "            this.tempConnection.endY = e.clientY - canvasRect.top;\n" +
                "            this.drawTempConnection();\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseUp(e) {\n" +
                "        if (this.connecting && this.connectionSource) {\n" +
                "            const targetPort = e.target.closest('.port');\n" +
                "            if (targetPort) {\n" +
                "                const targetNode = targetPort.closest('.node');\n" +
                "                if (targetNode) {\n" +
                "                    this.completeConnection(targetNode, targetPort);\n" +
                "                } else {\n" +
                "                    this.cleanupConnection();\n" +
                "                }\n" +
                "            } else {\n" +
                "                this.cleanupConnection();\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasDoubleClick(e) {\n" +
                "        if (e.target === document.getElementById('node-canvas')) {\n" +
                "            this.createNode('NodePrintLog', e.clientX, e.clientY);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleKeyDown(e) {\n" +
                "        if ((e.key === 'Delete' || e.key === 'Backspace') && this.selectedNode) {\n" +
                "            this.deleteNode(this.selectedNode.id);\n" +
                "        }\n" +
                "        \n" +
                "        if (e.key === 'Escape' && this.connecting) {\n" +
                "            this.cleanupConnection();\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "const editor = new EnhancedNodeEditor();";
    }

    public static String getNodeEditorCSS() {
        return "* {\n" +
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
                "    --error-color: #e06c75;\n" +
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
                "\n" +
                ".port.connected {\n" +
                "    background: rgba(78, 122, 181, 0.2);\n" +
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
                "\n" +
                ".temp-connection {\n" +
                "    stroke-dasharray: 5,5;\n" +
                "    animation: dash 1s linear infinite;\n" +
                "}\n" +
                "\n" +
                "@keyframes dash {\n" +
                "    to {\n" +
                "        stroke-dashoffset: -10;\n" +
                "    }\n" +
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
                ".property-group input:disabled {\n" +
                "    opacity: 0.6;\n" +
                "    cursor: not-allowed;\n" +
                "}\n" +
                "\n" +
                ".disconnect-btn, .delete-node-btn {\n" +
                "    width: 100%;\n" +
                "    padding: 8px 12px;\n" +
                "    background: var(--error-color);\n" +
                "    border: none;\n" +
                "    border-radius: 4px;\n" +
                "    color: white;\n" +
                "    font-size: 14px;\n" +
                "    cursor: pointer;\n" +
                "    transition: background-color 0.2s ease;\n" +
                "}\n" +
                "\n" +
                ".disconnect-btn:hover, .delete-node-btn:hover {\n" +
                "    background: #c85566;\n" +
                "}\n" +
                "\n" +
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