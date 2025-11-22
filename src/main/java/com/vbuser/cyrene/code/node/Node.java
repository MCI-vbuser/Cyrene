package com.vbuser.cyrene.code.node;

import com.vbuser.cyrene.code.diagram.Diagram;
import com.vbuser.cyrene.code.parameter.Parameter;

@SuppressWarnings("unused")
public abstract class Node {

    public Diagram diagram;
    public Node[] upstream;
    public Node[] downstream;
    public Parameter[] input;
    public Parameter[] output;
    public int id;

    public Node(Diagram diagram, Node[] upstream, Node[] downstream
            , Parameter[] input, Parameter[] output, int id) {
        this.upstream = upstream;
        this.downstream = downstream;
        this.input = input;
        this.output = output;
        this.id = id;
    }

    public abstract void execute();

    public abstract String name();

}
