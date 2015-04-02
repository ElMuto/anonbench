package org.deidentifier.arx.algorithm;

public class TerminationConfiguration {

    private final Type type;
    private final int  value;

    public static enum Type {
        TIME("t"),
        CHECKS("c"),
        ANONYMITY("a");

        public static Type fromLabel(String label) {
            if (label != null) {
                for (Type t : Type.values()) {
                    if (label.equalsIgnoreCase(t.label)) {
                        return t;
                    }
                }
            }
            System.out.println("Unsupported Termination Limit Type: " + label);
            System.exit(0);
            return null;
        }

        private String label;

        private Type(String label) {
            this.label = label;
        }

        public String toString() {
            return this.label;
        }
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
