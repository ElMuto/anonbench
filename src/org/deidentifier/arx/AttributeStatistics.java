package org.deidentifier.arx;

public class AttributeStatistics {

    public final Integer numValues;
    public final Double frequencyDeviation;
    public final Double variance;
    public final Double skewness;
    public final Double kurtosis;
    public final Double standDeviation;
    public final Double variance_coeff;
    public final Double deviation_norm;
    public final Double quartil_coeff;
    public final Double mean_arith;
    public final Double mean_geom;
    public final Double median;
    public final Double entropy;
    
    /**
     * @param numValues
     * @param frequencyDeviation
     * @param variance
     * @param skewness
     * @param kurtosis
     * @param standDeviation
     * @param variance_coeff
     * @param deviation_norm
     * @param quartil_coeff
     * @param mean_arith
     * @param mean_geom
     * @param median
     * @param entropy
     */
    public AttributeStatistics(Integer numValues,
                               Double frequencyDeviation,
                               Double variance,
                               Double skewness,
                               Double kurtosis,
                               Double standDeviation,
                               Double variance_coeff,
                               Double deviation_norm,
                               Double quartil_coeff,
                               Double mean_arith,
                               Double mean_geom,
                               Double median,
                               Double entropy) {
        this.numValues = numValues;
        this.frequencyDeviation = frequencyDeviation;
        this.variance = variance;
        this.skewness = skewness;
        this.kurtosis = kurtosis;
        this.standDeviation = standDeviation;
        this.variance_coeff = variance_coeff;
        this.deviation_norm = deviation_norm;
        this.quartil_coeff = quartil_coeff;
        this.mean_arith = mean_arith;
        this.mean_geom = mean_geom;
        this.median = median;
        this.entropy = entropy;
    }

    public Integer getNumValues() {
        return numValues;
    }

    public Double getFrequencyDeviation() {
        return frequencyDeviation;
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

    public Double getStandDeviation() {
        return standDeviation;
    }

    public Double getVariance_coeff() {
        return variance_coeff;
    }

    public Double getDeviation_norm() {
        return deviation_norm;
    }

    public Double getQuartil_coeff() {
        return quartil_coeff;
    }

    public Double getMean_arith() {
        return mean_arith;
    }

    public Double getMean_geom() {
        return mean_geom;
    }

    public Double getMedian() {
        return median;
    }

    public Double getEntropy() {
        return entropy;
    }
}