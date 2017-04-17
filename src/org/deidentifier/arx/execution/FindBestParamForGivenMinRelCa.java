package org.deidentifier.arx.execution;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;


import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.criteria.ParamTransformer;
import org.deidentifier.arx.util.Classificator;

public class FindBestParamForGivenMinRelCa {
	
	static double targetValue = 0.8;
	static double minIntervalSize = 0.01;
	static int    k = 5;
	
	static BenchmarkCriterion criterion = BenchmarkCriterion.BASIC_BETA_LIKENESS;
	static BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
	static String sa = "ED";
	static BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
	static double sf = 0.05;
	
	static ParametrizationSetup setup = new ParametrizationSetup(datafile,sa, k, 1d, criterion, measure, sf);
	static BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, setup.getDataset());

	public static void main(String[] args) {
		
		double normalizedResult = binarySearch(targetValue, 0.000001, 1d);
		double denormalizedResult =  ParamTransformer.getDenormalizedParamVal(
				setup.getDataset().getDatafile(),
				setup.getSa(),
				setup.getDim2Crit(),
				normalizedResult);
		
		
		System.out.format("Final result is %.4f", denormalizedResult);

	}
	
	static double binarySearch(double targetValue, double minParam, double maxParam) {
		
		double intervalSize = (maxParam - minParam);
		double halfOfIntervalSize = intervalSize / 2d;
		double middleOfInterval = minParam + halfOfIntervalSize;
		
		if(halfOfIntervalSize >= minIntervalSize) {
			double newMin, newMax;
			double relCa = calcRelCa(middleOfInterval);
			if (relCa < targetValue) {
				newMin = minParam;
				newMax = middleOfInterval;
			} else {
				newMin = middleOfInterval;
				newMax = maxParam;
			}
			return binarySearch(targetValue, newMin, newMax);
		} else {
			return minParam;
		}
	}
	
	static double calcRelCa(double param) {
		
		double denormalizedParam =  ParamTransformer.getDenormalizedParamVal(
				setup.getDataset().getDatafile(),
				setup.getSa(),
				setup.getDim2Crit(),
				param);
		setup.setDim2Param(denormalizedParam);
		System.out.format("RelCa for paramVal=%.4f is ", denormalizedParam);
		
		Classificator classi = new Classificator(setup);
		classi.findOptimalRelCa();
		double result = classi.getMaxRelPa();

		System.out.format("%.4f\n", result);
		
		return result;
		
	}
}
