package com.vbuser.cyrene.code.parameter;

import com.vbuser.cyrene.code.node.Node;

@SuppressWarnings("unused")
public class Parameter {
    public enum Type {
        STRING,
        INT,
        NUMBER
    }

    public String name;
    public Type type;
    public Node parent;
    public String value;

    public Parameter(String name, Type type, Node parent, String value) {
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.value = value;
    }

    public String getStringValue() {
        return value;
    }

    public int getIntValue() {
        return (this.type == Type.INT) ? Integer.parseInt(value) : 0;
    }

    public void setIntValue(int var) {
        if(this.type == Type.INT) {
            this.value = String.valueOf(var);
        }
    }

}
