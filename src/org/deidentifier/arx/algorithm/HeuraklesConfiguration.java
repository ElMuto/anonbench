package org.deidentifier.arx.algorithm;

public class HeuraklesConfiguration {

    private final int milliseconds;
    private final int checks;

    public static enum Limit {
        TIME,
        CHECKS
    }

    public HeuraklesConfiguration(Limit limit, int value) {
        milliseconds = limit == Limit.TIME ? value : -1;
        checks = limit == Limit.CHECKS ? value : -1;
    }

    public int getMilliseconds() {
        return milliseconds;
    }

    public int getChecks() {
        return checks;
    }
}
