package org.deidentifier.arx.criteria;

import java.util.HashMap;
import java.util.Map;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class ParamTransformer {

	private static Map<String, Integer>				dimSa		= new HashMap<>();
	
	private static double[][][] maxVals;
	
	static {
		
		dimSa.put("MS",									        0);
		dimSa.put("Marital status",						        0);
		dimSa.put("MARSTAT",							        0);
		dimSa.put("MS",									        0);
		dimSa.put("ED",									        1);
		dimSa.put("Education",							        1);
		dimSa.put("Highest level of school completed",	        1);
		dimSa.put("EDUC",								        1);
		
		maxVals = new double[BenchmarkDatafile.values().length][dimSa.size()][BenchmarkCriterion.values().length];

		maxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()]	=   7.161;
		maxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()]		=  28.527;
		maxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()]	=   6.362;
		maxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()]		=  75.361;

		maxVals[BenchmarkDatafile.ATUS.ordinal()] [dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()]	=   9.697;
		maxVals[BenchmarkDatafile.ATUS.ordinal()] [dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()]		=  27.852;
		maxVals[BenchmarkDatafile.ATUS.ordinal()] [dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()]	=  27.852;
		maxVals[BenchmarkDatafile.ATUS.ordinal()] [dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()]		= 133.561;

		maxVals[BenchmarkDatafile.IHIS.ordinal()] [dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()]	=  10.68;
		maxVals[BenchmarkDatafile.IHIS.ordinal()] [dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()]		= 683.349;
		maxVals[BenchmarkDatafile.IHIS.ordinal()] [dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()]	=   7.812;
		maxVals[BenchmarkDatafile.IHIS.ordinal()] [dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()]		= 160.612;
	}
	
	public static double getNormalizedParamVal(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, double value) {
		
		return maxVals[datafile.ordinal()][dimSa.get(sa)][crit.ordinal()] / value;
		
	}
	
	public static double getDenormalizedParamVal(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, double value) {
		
		return maxVals[datafile.ordinal()][dimSa.get(sa)][crit.ordinal()] * value;
		
	}
}
