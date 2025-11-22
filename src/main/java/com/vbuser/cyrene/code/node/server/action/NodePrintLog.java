package com.vbuser.cyrene.code.node.server.action;

import com.vbuser.cyrene.code.diagram.Diagram;
import com.vbuser.cyrene.code.node.Node;
import com.vbuser.cyrene.code.parameter.Parameter;

@SuppressWarnings("unused")
public class NodePrintLog extends Node {
    public NodePrintLog(Diagram diagram, Node upstream, Node downstream, Parameter input, int id) {
        super(diagram, new Node[]{upstream}, new Node[]{downstream}, new Parameter[]{input}, null, id);
    }

    @Override
    public void execute() {
        System.out.println(this.input[0].getStringValue());
    }

    @Override
    public String name() {
        return "打印字符串";
    }
}
