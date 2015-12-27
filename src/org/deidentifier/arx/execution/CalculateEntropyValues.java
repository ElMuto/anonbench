package org.deidentifier.arx.execution;

import org.deidentifier.arx.BenchmarkDriver;

public class CalculateEntropyValues {

	public static void main(String[] args) {
//		final double[] f1 = { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 };
//		final double[] f2 = { 0.99999, 0.00001 };
//		final double[] f3 = { 0.499, 0.501 };
//		final double[] f4 = {1d/3d, 1d/3d, 1d/3d};
//		final double[] f5 = {0.25, 0.25, 0.25, 0.25};
		final double[] f6 = {0.2, 0.2, 0.2, 0.2, 0.2};
		
		final double[] freqs = f6;

		System.out.printf("Entropy:\t\t%f\nNormalized entropy:\t%f", BenchmarkDriver.calcEntropy(freqs), BenchmarkDriver.calcNormalizedEntropy(freqs));
	}

}
