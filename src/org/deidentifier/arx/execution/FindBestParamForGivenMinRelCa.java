package org.deidentifier.arx.execution;

public class FindBestParamForGivenMinRelCa {

	public static void main(String[] args) {
		System.out.println(binarySearch(0.8, 0d, 1d, 0.005, 1));

	}
	
	static double binarySearch(double targeValue, double minParam, double maxParam, double minIntervalSize, int recursionDepth) {
		
		double intervalSize = (maxParam - minParam);
		double halfOfIntervalSize = intervalSize / 2d;
		double middleOfInterval = minParam + halfOfIntervalSize;
		
//		System.out.println("Reached recursion depth " + recursionDepth++ + ", intervalsize = " +  intervalSize);
		
		if(halfOfIntervalSize >= minIntervalSize) {
			double newMin, newMax;
			if (calcRelCa(middleOfInterval) < targeValue) {
				newMin = minParam;
				newMax = middleOfInterval;
			} else {
				newMin = middleOfInterval;
				newMax = maxParam;
			}
			return binarySearch(targeValue, newMin, newMax, minIntervalSize,recursionDepth);
		} else {
			return minParam;
		}
	}
	
	static double calcRelCa(double param) {
		return 1d - param;
	}
}
