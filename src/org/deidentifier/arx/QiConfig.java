package org.deidentifier.arx;

public class QiConfig {
    private final int numQis;
    private final int[] activeQis;
    
    public QiConfig(int numQis) {
        super();
        this.numQis = numQis;
        this.activeQis = null;
    }

    public QiConfig(int[] activeQis) {
        super();
        this.activeQis = activeQis;
        numQis = activeQis != null ? activeQis.length : 0;
    }

    public int getNumQis() {
        return numQis;
    }

    public int[] getActiveQis() {
        return activeQis;
    }
}
