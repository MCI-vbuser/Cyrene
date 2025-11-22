package com.vbuser.cyrene.code.node.server.action;

import com.vbuser.cyrene.code.diagram.Diagram;
import com.vbuser.cyrene.code.node.Node;

public class NodeQuitCycle extends Node {

    public boolean quit = false;

    public NodeQuitCycle(Diagram diagram, Node upstream, Node downstream, int id) {
        super(diagram, new Node[]{upstream}, new Node[]{downstream}, null, null, id);
    }

    @Override
    public void execute() {

    }

    @Override
    public String name() {
        return "跳出循环";
    }
}
