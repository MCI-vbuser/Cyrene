package com.vbuser.cyrene.code.node.server.action;

import com.vbuser.cyrene.code.diagram.Diagram;
import com.vbuser.cyrene.code.node.Node;
import com.vbuser.cyrene.code.parameter.Parameter;

@SuppressWarnings("unused")
public class NodeLimitedCycle extends Node {
    public NodeLimitedCycle(Diagram diagram, Node upstream, Node downstream, Node quitCondition
            , Node cycleBodyHead, Parameter initial_value, Parameter terminal_value
            , Parameter current_value, int id) {
        super(diagram, new Node[]{upstream, quitCondition}, new Node[]{downstream, cycleBodyHead},
                new Parameter[]{initial_value, terminal_value}, new Parameter[]{current_value}, id);
    }

    @Override
    public void execute() {
        NodeQuitCycle quit = null;
        if(upstream[1] instanceof NodeQuitCycle){
            quit = (NodeQuitCycle) upstream[1];
        }
        int i = input[0].getIntValue();
        while (i <= input[1].getIntValue()) {
            if(quit!=null){
                if(quit.quit) break;
            }
            downstream[1].execute();
            //我们这里理论上还要递归执行整个循环体的内容，而非仅仅包括与有限循环节点直接相连的节点
            //由于可能涉及回环、多分枝等复杂内容，此处先不考虑
            output[0].setIntValue(i);
            i++;
        }
    }

    @Override
    public String name() {
        return "有限循环";
    }
}
