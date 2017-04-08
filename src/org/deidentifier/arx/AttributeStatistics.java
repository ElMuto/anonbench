package org.deidentifier.arx;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class AttributeStatistics {

	public final Integer numRows;
	public final Integer numValues;
    public final Double frequencyDeviation;
    public final Double minFrequency;
    public final Double maxFrequency;
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
     * @param numRows TODO
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
     * @param minFrequency TODO
     * @param maxFrequency TODO
     */
    public AttributeStatistics(Integer numRows,
                               Integer numValues,
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
                               Double median, Double entropy, Double minFrequency, Double maxFrequency) {
    	
    	if (minFrequency > maxFrequency) throw new RuntimeException("This should not happen");
    	
    	this.numRows = numRows;
        this.numValues = numValues;
        this.frequencyDeviation = frequencyDeviation;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
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

    public Double getFrequencySpan() {
        return this.maxFrequency - this.minFrequency;
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
    
    public Double getMinFrequency() {
		return minFrequency;
	}

	public Double getMaxFrequency() {
		return maxFrequency;
	}
	


    /** Log 2. */
    private static final double LOG2             = Math.log(2);

    /**
     * Computes log 2.
     *
     * @param num
     * @return
     */
    static final double log2(final double num) {
        return Math.log(num) / LOG2;
    }
	
	public Double getMinGuarantee(BenchmarkCriterion crit) {
		switch (crit) {
		case BASIC_BETA_LIKENESS:
			return ((1d - getMinFrequency()) / getMinFrequency());
		case D_DISCLOSURE_PRIVACY:
			return Math.abs(log2(1d / (getNumRows() * getMaxFrequency())));
		case L_DIVERSITY_DISTINCT:
			return 1d;
		case L_DIVERSITY_ENTROPY:
			return 1d;
		case L_DIVERSITY_RECURSIVE:
			return 1d;
		case T_CLOSENESS_ED:
			return 1d;
		default:
			throw new IllegalArgumentException("Unsopported criterion: " + crit);		
		}
	}
	
	public Double getMaxGuarantee(BenchmarkCriterion crit) {
		switch (crit) {
		case BASIC_BETA_LIKENESS:
			return 0d;
		case D_DISCLOSURE_PRIVACY:
			return 0d;
		case L_DIVERSITY_DISTINCT:
			return Double.valueOf(getNumValues());
		case L_DIVERSITY_ENTROPY:
			return Double.valueOf(getNumValues());
		case L_DIVERSITY_RECURSIVE:
			return Double.valueOf(getNumValues());
		case T_CLOSENESS_ED:
			return 0d;
		default:
			throw new IllegalArgumentException("Unsopported criterion: " + crit);		
		}
	}

	private int getNumRows() {
		return numRows;
	}
}