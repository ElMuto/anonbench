package org.deidentifier.arx;

public class AttributeStatistics {

    private final Integer numValues;
    private final Double frequencyVariance;
    private final Double variance;
    private final Double skewness;
    private final Double kurtosis;
    private final Double deviation;
    private final Double deviation_rel;
    private final Double deviation_norm;
    
    /**
     * @param numValues
     * @param frequencyVariance
     * @param variance
     * @param skewness
     * @param kurtosis
     * @param deviation
     * @param deviation_rel
     * @param deviation_norm
     */
    public AttributeStatistics (Integer numValues, Double frequencyVariance,
                                Double variance, Double skewness, Double kurtosis,
                                Double deviation, Double deviation_rel, Double deviation_norm) {
        this.numValues = numValues;
        this.frequencyVariance = frequencyVariance;
        this.variance = variance;
        this.skewness = skewness;
        this.kurtosis = kurtosis;
        this.deviation = deviation;
        this.deviation_rel = deviation_rel;
        this.deviation_norm = deviation_norm;
    }
    
    public Integer getNumValues() {
        return numValues;
    }

    public Double getFrequencyVariance() {
        return frequencyVariance;
    }

    public Double getVariance() {
        return variance;
    }

    public Double getSkewness() {
        return skewness;
    }

    public Double getKurtosis() {
        return kurtosis;
    }

    public Double getDeviation() {
        return deviation;
    }

    public Double getDeviation_rel() {
        return deviation_rel;
    }

    public Double getDeviation_norm() {
        return deviation_norm;
    }
}