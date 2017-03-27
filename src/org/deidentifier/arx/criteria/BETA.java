package org.deidentifier.arx.criteria;

import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;

public class BETA {

    public static void prepareBeta() {
        numBeta = 0;
        avgBeta = 0;
        minBeta = Double.MAX_VALUE;
        maxBeta = -Double.MAX_VALUE;
    }

    public static double numBeta = 0;
    public static double avgBeta = 0;
    public static double minBeta = Double.MAX_VALUE;
    public static double maxBeta = -Double.MAX_VALUE;

    public static void doneBeta() {
        avgBeta /= numBeta;
    }

    public static void process(double[] distribution, HashGroupifyEntry entry, int index, boolean suppressed) {
    	
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
        avgBeta += beta;
        numBeta ++;
        minBeta = Math.min(minBeta,  beta);
        maxBeta = Math.max(maxBeta,  beta);

    }
}
