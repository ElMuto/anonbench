package org.deidentifier.arx;

public class AttributeStatistics {

    private final Integer numValues;
    private final Double frequencyVariance;
    private final Double variance;
    private final Double skewness;
    private final Double kurtosis;
    
    public AttributeStatistics (Integer numValues, Double frequencyVariance, Double variance, Double skewness, Double kurtosis) {
        this.numValues = numValues;
        this.frequencyVariance = frequencyVariance;
        this.variance = variance;
        this.skewness = skewness;
        this.kurtosis = kurtosis;
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
}