package org.deidentifier.arx;

import java.util.HashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;


/**
 * Summarizes statistical properties of a given attribute in a certain dataset
 * 
 * @author Helmut Spengler
 *
 */
public class AttributeStatistics {

    final static HashMap<String, AttributeStatistics> statsCache = new HashMap<String, AttributeStatistics>();

	private final Integer numRows;
	private final Integer domainSize;
	private final Double frequencyDeviation;
	private final Double minFrequency;
    private final Double maxFrequency;
    private final Double variance;
    private final Double skewness;
    private final Double kurtosis;
    private final Double standDeviation;
    private final Double variance_coeff;
    private final Double deviation_norm;
    private final Double quartil_coeff;
    private final Double mean_arith;
    private final Double mean_geom;
    private final Double median;
    private final Double entropy;

    private final Double rpgLmin, rpgLmax, rpgTmin, rpgTmax, rpgBmin, rpgBmax, rpgDmin, rpgDmax;
    
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
     * @param rpgLmin TODO
     * @param rpgLmax TODO
     * @param rpgTmin TODO
     * @param rpgTmax TODO
     * @param rpgBmin TODO
     * @param rpgBmax TODO
     * @param rpgDmin TODO
     * @param rpgDmax TODO
     */
    private AttributeStatistics(Integer numRows,
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
                               Double median, Double entropy,
                               Double minFrequency, Double maxFrequency,
                               Double rpgLmin, Double rpgLmax, Double rpgTmin, Double rpgTmax,
                               Double rpgBmin, Double rpgBmax, Double rpgDmin, Double rpgDmax) {
    	
    	if (minFrequency > maxFrequency) throw new RuntimeException("This should not happen");
    	
    	this.numRows = numRows;  this.domainSize = numValues; this.frequencyDeviation = frequencyDeviation;
        this.minFrequency = minFrequency; this.maxFrequency = maxFrequency;
        this.variance = variance; this.skewness = skewness; this.kurtosis = kurtosis;
        this.standDeviation = standDeviation; this.variance_coeff = variance_coeff;
        this.deviation_norm = deviation_norm;  this.quartil_coeff = quartil_coeff;
        this.mean_arith = mean_arith; this.mean_geom = mean_geom; this.median = median; this.entropy = entropy;
        this.rpgLmin = rpgLmin; this.rpgLmax = rpgLmax;
        this.rpgTmin = rpgTmin; this.rpgTmax = rpgTmax;
        this.rpgBmin = rpgBmin; this.rpgBmax = rpgBmax;
        this.rpgDmin = rpgDmin; this.rpgDmax = rpgDmax;
    }

    public Integer getDomainSize() {
        return domainSize;
    }

    public Double getRpgLmin() {
		return rpgLmin;
	}

    public Double getRpgLmax() {
		return rpgLmax;
	}

	public Integer getNumRows() {
		return numRows;
	}

	public Double getRpgTmin() {
		return rpgTmin;
	}

	public Double getRpgTmax() {
		return rpgTmax;
	}

	public Double getRpgBmin() {
		return rpgBmin;
	}

	public Double getRpgBmax() {
		return rpgBmax;
	}

	public Double getRpgDmin() {
		return rpgDmin;
	}

	public Double getRpgDmax() {
		return rpgDmax;
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

	/**
	 * @param dataset
	 * @param attr
	 * @return
	 */
	public static AttributeStatistics get(BenchmarkDataset dataset, String attr) {
	    String statsKey = dataset.toString() + "-" + attr;
	    if (statsCache.containsKey(statsKey)) {
	        return statsCache.get(statsKey);
	    } else {
	    	DataHandle handle = dataset.getArxData().getHandle();
	        Integer numRows = null;
	        Integer domainSize = null;
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
	        
	        Double rpgLmax, rpgLmin;
	        Double rpgTmin = 1d, rpgTmax = 0d;
//	        Double rpgBmax, rpgDmax = 0d;
//	        Double rpgBmin, rpgDmin;
	        	
	        int attrColIndex = handle.getColumnIndexOf(attr);
	        numRows = handle.getNumRows();
	        String[] distinctValues = handle.getStatistics().getDistinctValues(attrColIndex);
	        domainSize = distinctValues.length;
	        
	        rpgLmin = 1e-6;
	        rpgLmax = Double.valueOf(domainSize);
	        
	        // get the frequencies of attribute instantiations
	        double[] freqs  = handle.getStatistics().getFrequencyDistribution(handle.getColumnIndexOf(attr)).frequency;
	        
	        double normalizedEntropy = BenchmarkDriver.calcNormalizedEntropy(freqs);
	        
	        if (  // the attribute has interval scale
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

	        } else { // the attribute has nominal scale
	            
	            // initialize stats package and read values for calculating standard deviation
	            DescriptiveStatistics stats = new DescriptiveStatistics();
	            for (int i = 0; i < freqs.length; i++) {
	                stats.addValue(freqs[i]);
	            }
	            frequencyDeviation = stats.getStandardDeviation();
	            minFrequency = stats.getMin();
	            maxFrequency = stats.getMax();
	        }
	        
	        // calc dMax and bMax
			BenchmarkCriterion crit = BenchmarkCriterion.T_CLOSENESS_ED;
			ParametrizationSetup compSetup =  new ParametrizationSetup(
					dataset.getDatafile(),
					attr, 1, PrivacyModel.getMostRelaxedParam2(crit), crit, BenchmarkMeasure.ENTROPY, 0d);
			int[] traFo = { 0, 0, 0 };
			compSetup.anonymize(traFo, traFo);
            
	        statsCache.put(statsKey, new AttributeStatistics(numRows, domainSize,
	        							   frequencyDeviation, variance, skewness,
	                                       kurtosis, standDeviation, variance_coeff,
	                                       deviation_norm, quartil_coeff,
	                                       mean_arith, mean_geom, median, normalizedEntropy,
	                                       minFrequency, maxFrequency,
	                                       rpgLmin, rpgLmax, rpgTmin, rpgTmax,
	                                       DisclosureRiskCalculator.getBeta().getMax(), 1e-9,
	                                       DisclosureRiskCalculator.getDelta().getMax(), 1e-9));
	        
	        return statsCache.get(statsKey);
	    }
	}

	public static AttributeStatistics get(BenchmarkDatafile datafile, String sa) {
		BenchmarkDataset dataset = new BenchmarkDataset(datafile, new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.BASIC_BETA_LIKENESS }, sa );
		return get (dataset, sa);
	}
	
	public double getRpgMin(BenchmarkCriterion crit) {
		switch (crit) {
		case BASIC_BETA_LIKENESS:
			return rpgBmin;
		case D_DISCLOSURE_PRIVACY:
			return rpgDmin;
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
			return rpgLmin;
		case T_CLOSENESS_ED:
			return rpgTmin;
		default:
			throw new IllegalArgumentException("Invalid crit: " + crit);
		}
	}
	
	public double getRpgMax(BenchmarkCriterion crit) {
		switch (crit) {
		case BASIC_BETA_LIKENESS:
			return rpgBmax;
		case D_DISCLOSURE_PRIVACY:
			return rpgDmax;
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
			return rpgLmax;
		case T_CLOSENESS_ED:
			return rpgTmax;
		default:
			throw new IllegalArgumentException("Invalid crit: " + crit);
		}
	}
}