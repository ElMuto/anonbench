package org.deidentifier.arx.algorithm;

public class TerminationConfiguration {

    private final Type	type;
    private final int 	value;

    public static enum Type {
        TIME,
        CHECKS
    }

    public TerminationConfiguration(Type type, int value) {
    	this.type = type;
        this.value = value;
    }
    
    public Type getType() {
    	return type;
    }
    
    public int getValue() {
    	return value;
    }
}
