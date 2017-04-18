package org.deidentifier.arx.execution;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;


import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.criteria.ParamTransformer;
import org.deidentifier.arx.util.Classificator;

public class FindBestParamForGivenMinRelCa {
	
	static String outputFilename = "results/bestParamsForRelCa.csv";
	
	static double targetValue = 0.8;
	static double epsilon = 0.01;
//	static double epsilon = 0.3;
	static int    k = 5;

//	static BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
	static BenchmarkDatafile datafile = BenchmarkDatafile.IHIS;
	static BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
	static double sf = 0.05;
	
	static String[] sas = {
			"MS",
			"ED",
			};
	static BenchmarkCriterion[] criteria = {
			BenchmarkCriterion.L_DIVERSITY_DISTINCT,
			BenchmarkCriterion.L_DIVERSITY_RECURSIVE,
			BenchmarkCriterion.L_DIVERSITY_ENTROPY,
			BenchmarkCriterion.T_CLOSENESS_ED,
			BenchmarkCriterion.D_DISCLOSURE_PRIVACY,
			BenchmarkCriterion.BASIC_BETA_LIKENESS,
	};
	
	static String header =
			"dataset;" + 
			"sa;" + 
			"pm;" + 
			"paramNorm;" + 
			"paramDenorm;" + 
			"targetCa;" + 
			"actualCa;" + 
			"epsilon"
			;

	public static void main(String[] args) {
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(outputFilename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pw.println(header);
		pw.flush();

		for (String sa : sas) {

			for (BenchmarkCriterion criterion : criteria) {

				ParametrizationSetup setup = new ParametrizationSetup(datafile,sa, k, 1d, criterion, measure, sf);

				System.out.format("Calculating parameters for %s-%s - %s (targetCa=%.2f)\n",
						datafile, sa, criterion, targetValue);
				
				double[] result = binarySearch(targetValue, 0.000001, 1d, setup, 0d);
				double normalizedParam = result[0];
				double denormalizedParam =  ParamTransformer.getDenormalizedParamVal(
						setup.getDataset().getDatafile(),
						setup.getSa(),
						setup.getDim2Crit(),
						normalizedParam);
				double actualCa = result[1];


				System.out.format("Norm. param is %.4f, actual param is %.4f, actual Ca is %.4f\n\n",
						normalizedParam, denormalizedParam, actualCa);
				pw.format("%s;%s;%s;%.4f;%.4f;%.4f;%.4f;%.4f\n", datafile, sa, criterion, normalizedParam, denormalizedParam, targetValue, actualCa, epsilon);
				pw.flush();
			}

		}
		pw.close();
	}
	
	/**
	 * @param targetValue
	 * @param minParam
	 * @param maxParam
	 * @param setup
	 * @param currentActualCa TODO
	 * @return result[0]=normalizedParam, result[1]=actualCa
	 */
	static double[] binarySearch(double targetValue, double minParam, double maxParam, ParametrizationSetup setup, double currentActualCa) {
		
		double intervalSize = (maxParam - minParam);
		double halfOfIntervalSize = intervalSize / 2d;
		double middleOfInterval = minParam + halfOfIntervalSize;
		
		if(halfOfIntervalSize >= epsilon) {
			double newMin, newMax;
			double relCa = calcRelCa(middleOfInterval, setup);
			if (relCa < targetValue) {
				newMin = minParam;
				newMax = middleOfInterval;
			} else {
				newMin = middleOfInterval;
				newMax = maxParam;
				currentActualCa = relCa;
			}
			return binarySearch(targetValue, newMin, newMax, setup, currentActualCa);
		} else {
			return new double[] { minParam, currentActualCa };
		}
	}
	
	/**
	 * @param param
	 * @param setup
	 * @return
	 */
	static double calcRelCa(double param, ParametrizationSetup setup) {
		
		double denormalizedParam =  ParamTransformer.getDenormalizedParamVal(
				setup.getDataset().getDatafile(),
				setup.getSa(),
				setup.getDim2Crit(),
				param);
		setup.setDim2Param(denormalizedParam);
		System.out.format("\tRelCa for paramVal=%.4f is ", denormalizedParam);
		
		Classificator classi = new Classificator(setup);
		classi.findOptimalRelCa();
		double result = classi.getMaxRelPa();

		System.out.format("%.4f\n", result);
		
		return result;
		
	}
}
