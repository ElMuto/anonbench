package org.deidentifier.arx;

public class QiConfig {
    private int numQis;
    private int[] activeQis = null;
    private String[] allQis = null;
    private boolean xTraQi = false;
    
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

    public QiConfig(String[] qiArray) {
        super();
        this.allQis = qiArray;
        numQis = qiArray != null ? qiArray.length : 0;
    }

    public int getNumQis() {
        return numQis;
    }

    /**
     * @return an array with the indices of the activated QIs,
     * <code>null</code> if no specific indices are configured.
     */
    public int[] getActiveQis() {
        return activeQis;
    }

	public String[] getAllQis() {
		return allQis;
	}

	public void addQi(String se) {
		if (!xTraQi) {
			String[] extendedQis = new String[allQis.length + 1];
			for (int i = 0; i < allQis.length; i++) {
				extendedQis[i] = allQis[i];
			}
			extendedQis[allQis.length] = new String(se);
			allQis = extendedQis;
			numQis++;
			xTraQi = true;
		}
	}
}
