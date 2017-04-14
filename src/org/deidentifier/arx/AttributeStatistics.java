package org.deidentifier.arx;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;


/**
 * Summarizes statistical properties of a given attribute in a certain dataset
 * 
 * @author Helmut Spengler
 *
 */
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
     * @param minFrequency
     * @param maxFrequency
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

	/**
	 * @param dataset
	 * @param handle
	 * @param attr
	 * @param verbosity
	 * @return
	 * @throws IOException
	 */
	public static AttributeStatistics analyzeAttribute(BenchmarkDataset dataset, DataHandle handle, String attr, int verbosity) throws IOException {
	    String statsKey = dataset.toString() + "-" + attr;
	    if (BenchmarkDriver.statsCache.containsKey(statsKey)) {
	        return BenchmarkDriver.statsCache.get(statsKey);
	    } else {
	        Integer numRows = null;
	        Integer numValues = null;
	        Double  frequencyDeviation = null;
	        Double  variance = null;
	        Double  skewness = null;
	        Double  kurtosis = null;
	        Double  standDeviation = null;
	        Double  deviation_norm = null;
	        Double  variance_coeff = null;
	        Double  quartil_coeff = null;
	        Double  mean_arith = null;
	        Double  mean_geom = null; // done
	        Double  median = null; // done
	
	        Double minFrequency = null;
	        Double maxFrequency = null;
	
	        int attrColIndex = handle.getColumnIndexOf(attr);
	        numRows = handle.getNumRows();
	        String[] distinctValues = handle.getStatistics().getDistinctValues(attrColIndex);
	        numValues = distinctValues.length;
	        if (verbosity >= 1) System.out.println("    " + attr + " (domain size: " + distinctValues.length + ")");
	        
	        // get the frequencies of attribute instantiations
	        double[] freqs  = handle.getStatistics().getFrequencyDistribution(handle.getColumnIndexOf(attr)).frequency;
	        
	        double normalizedEntropy = BenchmarkDriver.calcNormalizedEntropy(freqs);
	        
	        if (
	                BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "AGEP".equals(attr.toString()) ||
	                BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "PWGTP".equals(attr.toString()) ||
	                BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "INTP".equals(attr.toString()) ||
	                BenchmarkDatafile.ADULT.equals(dataset.getDatafile()) && "age".equals(attr.toString()) ||
	                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "AGE".equals(attr.toString()) ||
	                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "INCOME".equals(attr.toString()) ||
	                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "MINRAMNT".equals(attr.toString()) ||
	                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "NGIFTALL".equals(attr.toString()) ||
	                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "RAMNTALL".equals(attr.toString()) ||
	                BenchmarkDatafile.FARS.equals(dataset.getDatafile()) && "iage".equals(attr.toString()) ||
	                BenchmarkDatafile.ATUS.equals(dataset.getDatafile()) && "Age".equals(attr.toString()) ||
	                BenchmarkDatafile.IHIS.equals(dataset.getDatafile()) && "AGE".equals(attr.toString()) ||
	                BenchmarkDatafile.IHIS.equals(dataset.getDatafile()) && "YEAR".equals(attr.toString())
	                ) {
	
	            // initialize stats package and read values
	            DescriptiveStatistics stats = new DescriptiveStatistics();
	            for (int rowNum = 0; rowNum < handle.getNumRows(); rowNum++) {
	                try {
	                    stats.addValue(Double.parseDouble(handle.getValue(rowNum, attrColIndex)));
	                } catch (java.lang.NumberFormatException e) { /* just ignore those entries */ }
	            }
	
	            // calculate stats
	            variance = stats.getVariance();
	            skewness = stats.getSkewness();
	            kurtosis = stats.getKurtosis();
	            standDeviation = stats.getStandardDeviation();
	            variance_coeff = standDeviation / stats.getMean();
	            deviation_norm = standDeviation / (stats.getMax() - stats.getMin());
	            mean_arith = stats.getMean();
	            mean_geom = stats.getGeometricMean();
	            quartil_coeff = (stats.getPercentile(75) - stats.getPercentile(25)) / (stats.getPercentile(75) + stats.getPercentile(25));
	            median = stats.getPercentile(50);
	            
	            // print
	            if (verbosity >= 2) {
	                System.out.println("      stand. dev.        = " + standDeviation);
	                System.out.println("      stand. dev. norm.  = " + deviation_norm);
	                System.out.println("      variance_coeff     = " + variance_coeff);
	                System.out.println("      quartil coeff.     = " + quartil_coeff);
	                System.out.println("      skewness           = " + skewness);
	                System.out.println("      kurtosis           = " + kurtosis);
	                System.out.println("      arith. mean        = " + mean_arith);
	                System.out.println("      geom. mean         = " + mean_geom);
	                System.out.println("      median             = " + median);
	                System.out.println("      normalized entropy = " + normalizedEntropy);
	            }
	        } else {
	            
	            // initialize stats package and read values for calculating standard deviation
	            DescriptiveStatistics stats = new DescriptiveStatistics();
	            for (int i = 0; i < freqs.length; i++) {
	                stats.addValue(freqs[i]);
	            }
	            frequencyDeviation = stats.getStandardDeviation();
	            minFrequency = stats.getMin();
	            maxFrequency = stats.getMax();
	            
	            if (verbosity >= 2) {
	                System.out.println("      std. deviation of frequencies = " + frequencyDeviation);
	                System.out.println("      normalized entropy            = " + normalizedEntropy);
	            }
	            if (verbosity >= 3) {
	                System.out.println("      " + Arrays.toString(distinctValues));
	                System.out.println("      " + Arrays.toString(freqs));
	            }
	        }
	        
	        BenchmarkDriver.statsCache.put(statsKey, new AttributeStatistics(numRows, numValues,
	        							   frequencyDeviation, variance, skewness,
	                                       kurtosis, standDeviation, variance_coeff,
	                                       deviation_norm, quartil_coeff,
	                                       mean_arith, mean_geom, median, normalizedEntropy, minFrequency, maxFrequency));
	        
	        return BenchmarkDriver.statsCache.get(statsKey);
	    }
	}
}