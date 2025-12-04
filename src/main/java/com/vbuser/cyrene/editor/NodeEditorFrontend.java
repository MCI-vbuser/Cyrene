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
                "            <div class=\"node-item\" data-type=\"NodeConcatList\">\n" +
                "                <div>列表拼接</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">将接入列表拼接在目标列表后</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeClearList\">\n" +
                "                <div>清除列表</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">清空指定列表</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetVariable\">\n" +
                "                <div>设置自定义变量</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">为目标实体上的指定变量设置值</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetDiagramVariable\">\n" +
                "                <div>设置节点图变量</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">为当前节点图内指定节点图变量设置值</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetPresetState\">\n" +
                "                <div>设置预设状态</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">设置指定目标实体的预设状态</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeCreateEntity\">\n" +
                "                <div>创建实体</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">根据GUID创建实体</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeCreateComponent\">\n" +
                "                <div>创建元件</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">根据元件ID创建一个实体</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeCreateComponentGroup\">\n" +
                "                <div>创建元件组</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">根据元件组索引创建该元件组内包含的实体</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeToggleModel\">\n" +
                "                <div>激活/关闭模型显示</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">更改实体的模型可见性属性设置</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeDestroyEntity\">\n" +
                "                <div>销毁实体</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">销毁指定实体，会有销毁表现</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeRemoveEntity\">\n" +
                "                <div>移除实体</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">移除指定实体，不会有销毁表现</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeFinishMission\">\n" +
                "                <div>结算关卡</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">触发关卡结算流程</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetEnvironmentTime\">\n" +
                "                <div>设置当前环境时间</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">立即切换环境时间到指定小时</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetEnvironmentTimeScale\">\n" +
                "                <div>设置环境时间流逝速度</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">每秒流逝分钟数（提瓦特速度为1）</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeChangeEntityCamp\">\n" +
                "                <div>修改实体阵营</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">修改指定目标实体的阵营</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeTeleportPlayer\">\n" +
                "                <div>传送玩家</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">传送指定玩家实体</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeReviveCharacter\">\n" +
                "                <div>复苏角色</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">复苏指定的角色实体</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeKnockdownAllCharacters\">\n" +
                "                <div>击倒玩家所有角色</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">击倒指定玩家的所有角色</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeReviveAllCharacters\">\n" +
                "                <div>复苏玩家所有角色</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">复苏指定玩家所有角色实体</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeActivateRevivePoint\">\n" +
                "                <div>激活复苏点</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">为该玩家激活指定序号的复苏点</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetReviveTime\">\n" +
                "                <div>设置玩家复苏耗时</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">设置指定玩家的下一次复苏时长</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeSetReviveCount\">\n" +
                "                <div>设置玩家剩余复苏次数</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">设置指定玩家剩余复苏次数</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeModifyEnvironment\">\n" +
                "                <div>修改环境配置</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">使指定玩家应用指定的环境配置</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeAllowRevive\">\n" +
                "                <div>允许/禁止玩家复苏</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">设置指定玩家是否允许复苏</div>\n" +
                "            </div>\n" +
                "            <div class=\"node-item\" data-type=\"NodeDeactivateRevivePoint\">\n" +
                "                <div>注销复活点</div>\n" +
                "                <div style=\"font-size: 12px; color: #ccc;\">为该玩家注销指定序号的复活点</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <div class=\"node-canvas\" id=\"node-canvas\">\n" +
                "            <div class=\"transform-container\" id=\"transform-container\">\n" +
                "                <svg id=\"connection-layer\" style=\"width: 100%; height: 100%; pointer-events: none;\"></svg>\n" +
                "            </div>\n" +
                "            <div class=\"view-info\">\n" +
                "                <span id=\"zoom-info\">100%</span>\n" +
                "                <span id=\"position-info\">x: 0, y: 0</span>\n" +
                "            </div>\n" +
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
                "        // 画布变换相关\n" +
                "        this.transform = {\n" +
                "            x: 0,\n" +
                "            y: 0,\n" +
                "            scale: 1\n" +
                "        };\n" +
                "        this.isPanning = false;\n" +
                "        this.lastPanPoint = { x: 0, y: 0 };\n" +
                "        this.minScale = 0.1;\n" +
                "        this.maxScale = 5;\n" +
                "        \n" +
                "        this.typeCompatibility = {\n" +
                "            'int': ['int', 'num', 'string'],\n" +
                "            'float': ['float', 'num', 'string'],\n" +
                "            'num': ['int', 'float', 'num', 'string'],\n" +
                "            'string': ['string'],\n" +
                "            'boolean': ['boolean', 'string'],\n" +
                "            'list': ['list', 'string'],\n" +
                "            'entity': ['entity', 'string'],\n" +
                "            'variable': ['variable', 'string'],\n" +
                "            'enum': ['enum', 'string'],\n" +
                "            'auto': ['int', 'float', 'num', 'string', 'boolean', 'list', 'entity', 'variable', 'enum', 'auto'],\n" +
                "            'node': ['node']\n" +
                "        };\n" +
                "        \n" +
                "        this.init();\n" +
                "    }\n" +
                "    \n" +
                "    async init() {\n" +
                "        await this.loadNodeConfigs();\n" +
                "        this.setupEventListeners();\n" +
                "        this.loadFromFile();\n" +
                "        this.updateViewInfo();\n" +
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
                "        const transformContainer = document.getElementById('transform-container');\n" +
                "        \n" +
                "        palette.addEventListener('mousedown', (e) => {\n" +
                "            if (e.target.closest('.node-item')) {\n" +
                "                const item = e.target.closest('.node-item');\n" +
                "                const type = item.dataset.type;\n" +
                "                this.createNode(type, e.clientX, e.clientY);\n" +
                "            }\n" +
                "        });\n" +
                "        \n" +
                "        // 画布事件监听\n" +
                "        canvas.addEventListener('wheel', this.handleCanvasWheel.bind(this));\n" +
                "        canvas.addEventListener('mousedown', this.handleCanvasMouseDown.bind(this));\n" +
                "        canvas.addEventListener('mousemove', this.handleCanvasMouseMove.bind(this));\n" +
                "        canvas.addEventListener('mouseup', this.handleCanvasMouseUp.bind(this));\n" +
                "        canvas.addEventListener('dblclick', this.handleCanvasDoubleClick.bind(this));\n" +
                "        \n" +
                "        // 防止拖拽触发选择文本\n" +
                "        canvas.addEventListener('dragstart', (e) => e.preventDefault());\n" +
                "        \n" +
                "        // 防止右键菜单\n" +
                "        canvas.addEventListener('contextmenu', (e) => e.preventDefault());\n" +
                "        \n" +
                "        document.addEventListener('keydown', this.handleKeyDown.bind(this));\n" +
                "        \n" +
                "        // 初始应用变换\n" +
                "        this.updateTransform();\n" +
                "    }\n" +
                "    \n" +
                "    // 画布变换相关方法\n" +
                "    updateTransform() {\n" +
                "        const container = document.getElementById('transform-container');\n" +
                "        container.style.transform = `translate(${this.transform.x}px, ${this.transform.y}px) scale(${this.transform.scale})`;\n" +
                "        container.style.transformOrigin = '0 0';\n" +
                "        this.updateViewInfo();\n" +
                "    }\n" +
                "    \n" +
                "    updateViewInfo() {\n" +
                "        document.getElementById('zoom-info').textContent = `${Math.round(this.transform.scale * 100)}%`;\n" +
                "        document.getElementById('position-info').textContent = `x: ${Math.round(this.transform.x)}, y: ${Math.round(this.transform.y)}`;\n" +
                "    }\n" +
                "    \n" +
                "    resetView() {\n" +
                "        this.transform = {\n" +
                "            x: 0,\n" +
                "            y: 0,\n" +
                "            scale: 1\n" +
                "        };\n" +
                "        this.updateTransform();\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasWheel(e) {\n" +
                "        e.preventDefault();\n" +
                "        \n" +
                "        const canvas = document.getElementById('node-canvas');\n" +
                "        const rect = canvas.getBoundingClientRect();\n" +
                "        \n" +
                "        // 鼠标相对于画布的坐标\n" +
                "        const mouseX = e.clientX - rect.left;\n" +
                "        const mouseY = e.clientY - rect.top;\n" +
                "        \n" +
                "        // 缩放前的鼠标在变换空间中的坐标\n" +
                "        const worldX = (mouseX - this.transform.x) / this.transform.scale;\n" +
                "        const worldY = (mouseY - this.transform.y) / this.transform.scale;\n" +
                "        \n" +
                "        // 计算缩放\n" +
                "        const delta = e.deltaY > 0 ? 0.9 : 1.1;\n" +
                "        const newScale = Math.max(this.minScale, Math.min(this.maxScale, this.transform.scale * delta));\n" +
                "        \n" +
                "        // 调整位移，使鼠标下的点保持在同一位置\n" +
                "        this.transform.x = mouseX - worldX * newScale;\n" +
                "        this.transform.y = mouseY - worldY * newScale;\n" +
                "        this.transform.scale = newScale;\n" +
                "        \n" +
                "        this.updateTransform();\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseDown(e) {\n" +
                "        // 中键按下：开始平移\n" +
                "        if (e.button === 1) {\n" +
                "            e.preventDefault();\n" +
                "            this.isPanning = true;\n" +
                "            this.lastPanPoint = { x: e.clientX, y: e.clientY };\n" +
                "            document.getElementById('node-canvas').style.cursor = 'grabbing';\n" +
                "            return;\n" +
                "        }\n" +
                "        \n" +
                "        // 左键按下：检查是否点击了节点或连接点\n" +
                "        if (e.button === 0) {\n" +
                "            const node = e.target.closest('.node');\n" +
                "            if (node) {\n" +
                "                if (!e.target.classList.contains('port-dot')) {\n" +
                "                    e.preventDefault();\n" +
                "                    this.selectNode(node);\n" +
                "                    \n" +
                "                    // 开始拖拽节点\n" +
                "                    this.startDragging(node, e);\n" +
                "                }\n" +
                "            } else {\n" +
                "                // 点击画布空白处，取消选择\n" +
                "                this.selectNode(null);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseMove(e) {\n" +
                "        // 处理画布平移\n" +
                "        if (this.isPanning) {\n" +
                "            const deltaX = e.clientX - this.lastPanPoint.x;\n" +
                "            const deltaY = e.clientY - this.lastPanPoint.y;\n" +
                "            \n" +
                "            this.transform.x += deltaX;\n" +
                "            this.transform.y += deltaY;\n" +
                "            \n" +
                "            this.lastPanPoint = { x: e.clientX, y: e.clientY };\n" +
                "            this.updateTransform();\n" +
                "            return;\n" +
                "        }\n" +
                "        \n" +
                "        // 处理临时连接线\n" +
                "        if (this.connecting && this.tempConnection) {\n" +
                "            const canvas = document.getElementById('node-canvas');\n" +
                "            const rect = canvas.getBoundingClientRect();\n" +
                "            const mouseX = e.clientX - rect.left;\n" +
                "            const mouseY = e.clientY - rect.top;\n" +
                "            \n" +
                "            // 将屏幕坐标转换为变换空间坐标\n" +
                "            const worldX = (mouseX - this.transform.x) / this.transform.scale;\n" +
                "            const worldY = (mouseY - this.transform.y) / this.transform.scale;\n" +
                "            \n" +
                "            this.tempConnection.endX = worldX;\n" +
                "            this.tempConnection.endY = worldY;\n" +
                "            this.drawTempConnection();\n" +
                "        }\n" +
                "        \n" +
                "        // 处理节点拖拽\n" +
                "        if (this.dragging && this.draggingNode) {\n" +
                "            const canvas = document.getElementById('node-canvas');\n" +
                "            const rect = canvas.getBoundingClientRect();\n" +
                "            \n" +
                "            // 将屏幕坐标转换为变换空间坐标\n" +
                "            const worldX = (e.clientX - rect.left - this.dragOffset.x - this.transform.x) / this.transform.scale;\n" +
                "            const worldY = (e.clientY - rect.top - this.dragOffset.y - this.transform.y) / this.transform.scale;\n" +
                "            \n" +
                "            this.draggingNode.style.left = worldX + 'px';\n" +
                "            this.draggingNode.style.top = worldY + 'px';\n" +
                "            \n" +
                "            const nodeId = parseInt(this.draggingNode.dataset.nodeId);\n" +
                "            const node = this.nodes.find(n => n.id === nodeId);\n" +
                "            if (node) {\n" +
                "                node.position.x = worldX;\n" +
                "                node.position.y = worldY;\n" +
                "            }\n" +
                "            \n" +
                "            this.updateConnections();\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasMouseUp(e) {\n" +
                "        // 结束画布平移\n" +
                "        if (e.button === 1 && this.isPanning) {\n" +
                "            this.isPanning = false;\n" +
                "            document.getElementById('node-canvas').style.cursor = 'default';\n" +
                "            this.saveToFile();\n" +
                "            return;\n" +
                "        }\n" +
                "        \n" +
                "        // 处理连接完成\n" +
                "        if (this.connecting && this.connectionSource && e.button === 0) {\n" +
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
                "        \n" +
                "        // 结束节点拖拽\n" +
                "        if (e.button === 0 && this.dragging) {\n" +
                "            this.handleDragEnd();\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    handleCanvasDoubleClick(e) {\n" +
                "        if (e.target.id === 'node-canvas' || e.target.id === 'transform-container') {\n" +
                "            const canvas = document.getElementById('node-canvas');\n" +
                "            const rect = canvas.getBoundingClientRect();\n" +
                "            \n" +
                "            // 将屏幕坐标转换为变换空间坐标\n" +
                "            const worldX = (e.clientX - rect.left - this.transform.x) / this.transform.scale;\n" +
                "            const worldY = (e.clientY - rect.top - this.transform.y) / this.transform.scale;\n" +
                "            \n" +
                "            this.createNode('NodePrintLog', worldX, worldY);\n" +
                "        }\n" +
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
                "        // 如果未提供坐标，则创建在视口中心\n" +
                "        let nodeX = x;\n" +
                "        let nodeY = y;\n" +
                "        \n" +
                "        if (nodeX === undefined || nodeY === undefined) {\n" +
                "            const canvas = document.getElementById('node-canvas');\n" +
                "            const canvasRect = canvas.getBoundingClientRect();\n" +
                "            \n" +
                "            // 将画布中心转换为变换空间坐标\n" +
                "            const centerX = canvasRect.width / 2;\n" +
                "            const centerY = canvasRect.height / 2;\n" +
                "            \n" +
                "            nodeX = (centerX - this.transform.x) / this.transform.scale - 90; // 减去节点宽度的一半\n" +
                "            nodeY = (centerY - this.transform.y) / this.transform.scale - 50; // 减去节点高度的一半\n" +
                "        }\n" +
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
                "    }\n" +
                "    \n" +
                "    getDefaultValue(type) {\n" +
                "        switch(type) {\n" +
                "            case 'int': return 0;\n" +
                "            case 'float': return 0.0;\n" +
                "            case 'num': return 0;\n" +
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
                "        document.getElementById('transform-container').appendChild(nodeEl);\n" +
                "        this.setupNodeEvents(nodeEl);\n" +
                "    }\n" +
                "    \n" +
                "    setupNodeEvents(nodeEl) {\n" +
                "        const nodeId = parseInt(nodeEl.dataset.nodeId);\n" +
                "        const node = this.nodes.find(n => n.id === nodeId);\n" +
                "        \n" +
                "        if (!node) return;\n" +
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
                "    }\n" +
                "    \n" +
                "    startTempConnection(portEl) {\n" +
                "        const portRect = portEl.getBoundingClientRect();\n" +
                "        const canvas = document.getElementById('node-canvas');\n" +
                "        const canvasRect = canvas.getBoundingClientRect();\n" +
                "        \n" +
                "        // 将屏幕坐标转换为变换空间坐标\n" +
                "        const startX = (portRect.left + portRect.width / 2 - canvasRect.left - this.transform.x) / this.transform.scale;\n" +
                "        const startY = (portRect.top + portRect.height / 2 - canvasRect.top - this.transform.y) / this.transform.scale;\n" +
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
                "        if (!this.isTypeCompatible(source.dataType, target.dataType)) {\n" +
                "            alert(`数据类型不匹配: ${source.dataType} 不能连接到 ${target.dataType}`);\n" +
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
                "    isTypeCompatible(sourceType, targetType) {\n" +
                "        if (sourceType === targetType) {\n" +
                "            return true;\n" +
                "        }\n" +
                "        \n" +
                "        if (sourceType === 'node' || targetType === 'node') {\n" +
                "            return sourceType === targetType;\n" +
                "        }\n" +
                "        \n" +
                "        if (targetType === 'string') {\n" +
                "            return true;\n" +
                "        }\n" +
                "        \n" +
                "        const compatibleTypes = this.typeCompatibility[sourceType];\n" +
                "        if (compatibleTypes && compatibleTypes.includes(targetType)) {\n" +
                "            return true;\n" +
                "        }\n" +
                "        \n" +
                "        if (targetType === 'auto') {\n" +
                "            return true;\n" +
                "        }\n" +
                "        \n" +
                "        return false;\n" +
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
                "                const allowedInputTypes = ['int', 'float', 'num', 'string', 'enum', 'boolean'];\n" +
                "                if (allowedInputTypes.includes(prop.dataType)) {\n" +
                "                    html += `\n" +
                "                        <div class=\"property-group\">\n" +
                "                            <label>${key}</label>\n" +
                "                            <input type=\"text\" \n" +
                "                                   value=\"${prop.value}\" \n" +
                "                                   onchange=\"editor.updateNodeProperty(${node.id}, '${key}', this.value)\">\n" +
                "                        </div>\n" +
                "                    `;\n" +
                "                } else {\n" +
                "                    html += `\n" +
                "                        <div class=\"property-group\">\n" +
                "                            <label>${key}</label>\n" +
                "                            <div style=\"color: #ccc; font-size: 12px; margin-bottom: 5px;\">\n" +
                "                                等待连接... <br>\n" +
                "                                (类型: ${prop.dataType})\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    `;\n" +
                "                }\n" +
                "            } else if (prop.type === 'connection') {\n" +
                "                html += `\n" +
                "                    <div class=\"property-group\">\n" +
                "                        <label>${key}</label>\n" +
                "                        <div style=\"color: #ccc; font-size: 12px; margin-bottom: 5px;\">\n" +
                "                            连接到: <br>节点 ${prop.sourceNode} 的 <br>${prop.sourceOutput} 参数<br> <br> \n" +
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
                "        const canvas = document.getElementById('node-canvas');\n" +
                "        const canvasRect = canvas.getBoundingClientRect();\n" +
                "        \n" +
                "        // 将屏幕坐标转换为变换空间坐标\n" +
                "        const startX = (sourceRect.left + sourceRect.width / 2 - canvasRect.left - this.transform.x) / this.transform.scale;\n" +
                "        const startY = (sourceRect.top + sourceRect.height / 2 - canvasRect.top - this.transform.y) / this.transform.scale;\n" +
                "        const endX = (targetRect.left + targetRect.width / 2 - canvasRect.left - this.transform.x) / this.transform.scale;\n" +
                "        const endY = (targetRect.top + targetRect.height / 2 - canvasRect.top - this.transform.y) / this.transform.scale;\n" +
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
                "            })),\n" +
                "            transform: this.transform\n" +
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
                "                        // 加载变换状态\n" +
                "                        if (graphData.transform) {\n" +
                "                            this.transform = graphData.transform;\n" +
                "                            this.updateTransform();\n" +
                "                        }\n" +
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
                "    handleKeyDown(e) {\n" +
                "        if ((e.key === 'Delete' || e.key === 'Backspace') && this.selectedNode) {\n" +
                "            this.deleteNode(this.selectedNode.id);\n" +
                "        }\n" +
                "        \n" +
                "        if (e.key === 'Escape' && this.connecting) {\n" +
                "            this.cleanupConnection();\n" +
                "        }\n" +
                "        \n" +
                "        // 快捷键：空格键重置视角\n" +
                "        if (e.key === ' ' && e.target === document.body) {\n" +
                "            e.preventDefault();\n" +
                "            this.resetView();\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "const editor = new EnhancedNodeEditor();\n" +
                "\n" +
                "// 全局函数供HTML调用\n" +
                "window.resetView = function() {\n" +
                "    editor.resetView();\n" +
                "};";
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
                "\n" +
                ".transform-container {\n" +
                "    position: absolute;\n" +
                "    width: 100%;\n" +
                "    height: 100%;\n" +
                "    transform-origin: 0 0;\n" +
                "}\n" +
                "        \n" +
                ".view-info {\n" +
                "    position: absolute;\n" +
                "    bottom: 10px;\n" +
                "    right: 10px;\n" +
                "    background: rgba(0, 0, 0, 0.7);\n" +
                "    padding: 5px 10px;\n" +
                "    border-radius: 4px;\n" +
                "    font-size: 12px;\n" +
                "    color: #ccc;\n" +
                "    display: flex;\n" +
                "    flex-direction: column;\n" +
                "    gap: 2px;\n" +
                "    pointer-events: none;\n" +
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