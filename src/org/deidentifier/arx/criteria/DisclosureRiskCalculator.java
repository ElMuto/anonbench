package org.deidentifier.arx.criteria;

import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;

public class DisclosureRiskCalculator {

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

	public static class DisclosureRisk {
		
		private final String name;
		private double numValues;
		private double avg;
		private double min;
		private double max;
		
		final BenchmarkDatafile datafile;
		final String sa;
		final BenchmarkCriterion crit;
		
		private DisclosureRisk(String name, BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit) {
			this.name = name;
			this.numValues = 0;
			this.min = Double.MAX_VALUE;
			this.max = -Double.MAX_VALUE;
			
			this.datafile = datafile;
			this.sa = sa;
			this.crit = crit;
		}

		/**
		 * @param value
		 * @param weight TODO
		 */
		private void collect(double value, double weight) {

			collect (value, value, value, weight);
			
		}

		/**
		 * @param min
		 * @param max
		 * @param avg
		 * @param weight
		 */
		private void collect(double min, double max, double avg, double weight) {
			this.avg += ( avg * weight);
			this.numValues += weight;

			this.min = Math.min(this.min,  min);
			this.max = Math.max(this.max,  max);
		}

		private void summarize() {
			avg /= numValues;
		}

		public double getAvg() {
			return avg;
		}

		public double getAvgNormalized() {
			return ParamTransformer.getNormalizedParamVal(datafile, sa, crit, avg);
		}

		public double getMin() {
			return min;
		}

		public double getMinNormalized() {
			return ParamTransformer.getNormalizedParamVal(datafile, sa, crit, min);
		}

		public double getMax() {
			return max;
		}

		public double getMaxNormalized() {
			return ParamTransformer.getNormalizedParamVal(datafile, sa, crit, max);
		}

		public void println() {
			System.out.format(new Locale ("DE", "de"), "\t"+name+":\t %.3f %.3f %.3f\n", min, avg, max);
		}

		public String[] toStringArray() {
			String NO_VALUE_FOUND_STRING = "NaN";
			String formattedMin = (min == Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", min));
			String formattedMax = (max == -Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", max));
			String formattedAvg= (min == Double.MAX_VALUE && max == -Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", avg));
			

			String formattedMinN = (min == Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", getMinNormalized()));
			String formattedMaxN = (max == -Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", getMaxNormalized()));
			String formattedAvgN = (min == Double.MAX_VALUE && max == -Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", getAvgNormalized()));
			
			return new String[] { formattedMin, formattedAvg, formattedMax, formattedMinN, formattedAvgN, formattedMaxN };
		}

		public Double[] toArray() {
			return new Double[] { min, avg, max, getMinNormalized(), getAvgNormalized(), getMaxNormalized()};
		}

		public String[] getHeader() {
			return new String[] { name + "-min", name + "-avg", name + "-max", name + "-min-norm", name + "-avg-norm", name + "-max-norm" };
		}

	}

	private static DisclosureRisk beta;
	private static DisclosureRisk t;
	private static DisclosureRisk l;
	private static DisclosureRisk delta;

	public static void prepare(BenchmarkDatafile datafile, String sa) {

		beta = new DisclosureRisk("Beta", datafile, sa, BenchmarkCriterion.BASIC_BETA_LIKENESS);
		l   = new DisclosureRisk("L", datafile, sa, BenchmarkCriterion.L_DIVERSITY_DISTINCT);
		t   = new DisclosureRisk("T", datafile, sa, BenchmarkCriterion.T_CLOSENESS_ED);
		delta   = new DisclosureRisk("Delta", datafile, sa, BenchmarkCriterion.D_DISCLOSURE_PRIVACY);
	}

	public static void summarize() {
		beta.summarize();
		l.summarize();
		t.summarize();
		delta.summarize();
	}
	
	public static DisclosureRisk getBeta() {
		return beta;
	}

	public static DisclosureRisk getL() {
		return l;
	}

	public static DisclosureRisk getT() {
		return t;
	}

	public static DisclosureRisk getDelta() {
		return delta;
	}

	/**
	 * @param distribution the distribution of the sensitive attribute in the original dataset
	 * @param entry the equivalence class EC
	 * @param index the index position of the sensitive attribute
	 * @param suppressed true, if the EC is suppressed
	 */
	public static void calculateDisclosureRisk(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {

		if (suppressed) {
			return;
		}
		
		calculateBeta(distribution, entry, index, suppressed);
		calculateL(distribution, entry, index, suppressed);
		calculateT(distribution, entry, index, suppressed);
		calculateDelta(distribution, entry, index, suppressed);
	}


	/**
	 * @param distribution the distribution of the sensitive attribute in the original dataset
	 * @param entry the equivalence class EC
	 * @param index the index position of the sensitive attribute
	 * @param suppressed true, if the EC is suppressed
	 */
	private static void calculateDelta(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
		
        // Init
        int[] buckets = entry.distributions[index].getBuckets();
        double count = entry.count;
        
        double minDelta = Double.MAX_VALUE;
        double maxDelta = 0d;
        double avgDelta = 0d;
        double numDeltas = 0d;
        // For each value in c
        for (int i = 0; i < buckets.length; i += 2) {
            if (buckets[i] != -1) { // bucket not empty
                double frequencyInT = distribution[buckets[i]];
                double frequencyInC = (double) buckets[i + 1] / count;
                double value = Math.abs(log2(frequencyInC / frequencyInT));
                avgDelta += value;
                numDeltas++;
                minDelta = Math.min(value, minDelta);
                maxDelta = Math.max(value, maxDelta);
            }
        }
        avgDelta /= numDeltas;
		if (DisclosureRiskCalculator.delta != null && numDeltas != 0d) {
			delta.collect(minDelta, maxDelta, avgDelta, count);
		}
	}


	/**
	 * @param distribution the distribution of the sensitive attribute in the original dataset
	 * @param entry the equivalence class EC
	 * @param index the index position of the sensitive attribute
	 * @param suppressed true, if the EC is suppressed
	 */
	public static void calculateBeta(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
	
		// Calculate beta
		// Init
		int[] buckets = entry.distributions[index].getBuckets();
		// the number of rows in this EC
		double count = entry.count;
		
		
	
		// For each value in EC
		// NEW version
		double minBeta = Double.MAX_VALUE;
		double maxBeta = Double.MIN_VALUE;
		double avgBeta  = 0d;
		double numBetas = 0d;
		for (int i = 0; i < buckets.length; i += 2) {
			if (buckets[i] != -1) { // bucket not empty
				double frequencyInT = distribution[buckets[i]];
				double frequencyInEC = (double) buckets[i + 1] / count;
				
				if (frequencyInT <= frequencyInEC) {
					double value = (frequencyInEC - frequencyInT) / frequencyInT;
					maxBeta = Math.max(value, maxBeta);
					minBeta = Math.min(value, maxBeta);
					avgBeta += value;
					numBetas++;
				}
			}
		}
		avgBeta /= numBetas;
		
		
		if (DisclosureRiskCalculator.beta != null && numBetas != 0d) {
			DisclosureRiskCalculator.beta.collect(minBeta, maxBeta, avgBeta, count);
		}
	}

	/**
	 * @param distribution the distribution of the sensitive attribute in the original dataset
	 * @param entry the equivalence class EC
	 * @param index the index position of the sensitive attribute
	 * @param suppressed true, if the EC is suppressed
	 */
	private static void calculateT(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
		
        // Calculate EMD with equal distance
        int[] buckets = entry.distributions[index].getBuckets();
        double count = entry.count;
        
        double val = 1.0d;
        for (int i = 0; i < buckets.length; i += 2) {
            if (buckets[i] != -1) { // bucket not empty
                double frequencyinT = distribution[buckets[i]];
                double frequencyInC = (double) buckets[i + 1] / count;
                val += Math.abs((frequencyinT - frequencyInC)) - frequencyinT;
            }
        }
        val /= 2;

		if (DisclosureRiskCalculator.t != null) {
			t.collect(val, entry.count);
		}
        
	}


	/**
	 * @param distribution the distribution of the sensitive attribute in the original dataset
	 * @param entry the equivalence class EC
	 * @param index the index position of the sensitive attribute
	 * @param suppressed true, if the EC is suppressed
	 */
	private static void calculateL(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
		
		int ld = entry.distributions[index].size(); // minSize=(int)l;
		
		if (DisclosureRiskCalculator.l != null) {
			DisclosureRiskCalculator.l.collect(ld, entry.count);
		}
		
	}
	
	public static String[] toStringArray() {
		return (String[]) BenchmarkDriver.concat(BenchmarkDriver.concat(BenchmarkDriver.concat(l.toStringArray(), t.toStringArray()), delta.toStringArray()), beta.toStringArray());
	}
	
	public static Double[] toArray() {
		return (Double[]) BenchmarkDriver.concat(BenchmarkDriver.concat(BenchmarkDriver.concat(l.toArray(), t.toArray()), delta.toArray()), beta.toArray());
	}
	
	public static String[] getHeader() {
		DisclosureRiskCalculator.prepare(null, null);
		return (String[]) BenchmarkDriver.concat(BenchmarkDriver.concat(BenchmarkDriver.concat(l.getHeader(), t.getHeader()), delta.getHeader()), beta.getHeader());
	}

	public static void println() {
		l.println();
		t.println();
		delta.println();
		beta.println();
	}
}
