package org.deidentifier.arx.criteria;

import java.util.Locale;

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
		
		private String name;
		private double numValues = 0;
		private double avg = 0;
		private double min = Double.MAX_VALUE;
		private double max = -Double.MAX_VALUE;
		
		private DisclosureRisk(String name) {
			this.name = name;
		}

		private void process(double value) {
			avg += value;
			numValues ++;

			min = Math.min(min,  value);
			max = Math.max(max,  value);
		}

		private void done() {
			avg /= numValues;
		}

		public double getAvg() {
			return avg;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}

		public void println() {
			System.out.format(new Locale ("DE", "de"), "\t"+name+":\t %.3f %.3f %.3f\n", min, avg, max);
		}

		public String toCsv(String sep) {
			String NO_VALUE_FOUND_STRING = "NaN";
			String formattedMin = (min == Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", min));
			String formattedMax = (max == -Double.MAX_VALUE ? NO_VALUE_FOUND_STRING : String.format(new Locale ("DE", "de"), "%.3f", max));
			return String.format(new Locale ("DE", "de"), "%s%s%.3f%s%s", formattedMin, sep, avg, sep, formattedMax);
		}

	}

	private static DisclosureRisk beta;
	private static DisclosureRisk t;
	private static DisclosureRisk l;
	private static DisclosureRisk delta;

	public static void prepare() {

		beta = new DisclosureRisk("Beta");
		l   = new DisclosureRisk("L");
		t   = new DisclosureRisk("T");
		delta   = new DisclosureRisk("Delta");
	}

	public static void done() {
		beta.done();
		l.done();
		t.done();
		delta.done();
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

	public static void calculateDisclosureRisk(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {

		if (suppressed) {
			return;
		}
		
		calculateBeta(distribution, entry, index, suppressed);
		calculateL(distribution, entry, index, suppressed);
		calculateT(distribution, entry, index, suppressed);
		calculateDelta(distribution, entry, index, suppressed);
	}

	private static void calculateDelta(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
		
        // Init
        int[] buckets = entry.distributions[index].getBuckets();
        double count = entry.count;
        
        double _delta = 0d;
        double _count = 0d;
        // For each value in c
        for (int i = 0; i < buckets.length; i += 2) {
            if (buckets[i] != -1) { // bucket not empty
                double frequencyInT = distribution[buckets[i]];
                double frequencyInC = (double) buckets[i + 1] / count;
                double value = Math.abs(log2(frequencyInC / frequencyInT));
                _delta += value;
                _count++;
            }
        }
		if (DisclosureRiskCalculator.delta != null) {
			delta.process(_delta/_count);
		}
	}

	private static void calculateT(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
		
        // Calculate EMD with equal distance
        int[] buckets = entry.distributions[index].getBuckets();
        double count = entry.count;
        
        double val = 1.0d;
        for (int i = 0; i < buckets.length; i += 2) {
            if (buckets[i] != -1) { // bucket not empty
                double frequency = distribution[buckets[i]];
                val += Math.abs((frequency - ((double) buckets[i + 1] / count))) - frequency;
            }
        }
        val /= 2;

		if (DisclosureRiskCalculator.t != null) {
			t.process(val);
		}
        
	}

	private static void calculateL(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
		
		int ld = entry.distributions[index].size(); // minSize=(int)l;
		
		if (DisclosureRiskCalculator.l != null) {
			DisclosureRiskCalculator.l.process(ld);
		}
		
	}

	public static void calculateBeta(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {

		// Calculate beta
		// Init
		int[] buckets = entry.distributions[index].getBuckets();
		double count = entry.count;

		// For each value in c
		double beta = 0d;
		double numBetas = 0d;
		for (int i = 0; i < buckets.length; i += 2) {
			if (buckets[i] != -1) { // bucket not empty
				double frequencyInT = distribution[buckets[i]];
				double frequencyInC = (double) buckets[i + 1] / count;
				double value = (frequencyInC - frequencyInT) / frequencyInT;
				beta += value;
				numBetas++;
			}
		}

		// Average beta for this class
		beta /= numBetas;
		if (DisclosureRiskCalculator.beta != null) {
			DisclosureRiskCalculator.beta.process(beta);
		}
	}
	
	public static String toCsv(String sep) {
		return String.format("%s%s%s%s%s%s%s", l.toCsv(sep), sep, t.toCsv(sep), sep, delta.toCsv(sep), sep, beta.toCsv(sep));
	}

	public static void println() {
		l.println();
		t.println();
		delta.println();
		beta.println();
	}
}
