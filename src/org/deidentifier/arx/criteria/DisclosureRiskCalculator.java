package org.deidentifier.arx.criteria;

import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;

public class DisclosureRiskCalculator {

	public static class DisclosureRisk {
		private double numValues = 0;
		private double avg = 0;
		private double min = Double.MAX_VALUE;
		private double max = -Double.MAX_VALUE;

		private void process(double value) {
			avg += value;
			numValues ++;

			min = Math.min(min,  value);
			max = Math.max(max,  value);
		}

		private void calcAvg() {
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
	}

	private static DisclosureRisk beta;

	private static DisclosureRisk t;

	private static DisclosureRisk ld;

	private static DisclosureRisk delta;

	public static void prepare() {

		beta = new DisclosureRisk();
		ld   = new DisclosureRisk();
	}

	public static DisclosureRisk getBeta() {
		beta.calcAvg();
		return beta;
	}

	public static DisclosureRisk getLd() {
		ld.calcAvg();
		return ld;
	}

	public static void calculateDisclosureRisk(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
		calculateBeta(distribution, entry, index, suppressed);
		calculateLd(distribution, entry, index, suppressed);
//		calculateT(distribution, entry, index, suppressed);
//		calculateDelta(distribution, entry, index, suppressed);
	}

	private static void calculateLd(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {


		if (suppressed) {
			return;
		}
		int l = entry.distributions[index].size(); // minSize=(int)l;
		if (DisclosureRiskCalculator.ld != null) {
			DisclosureRiskCalculator.ld.process(l);
		}
		
	}

	public static void calculateBeta(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {

		if (suppressed) {
			return;
		}

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
}
