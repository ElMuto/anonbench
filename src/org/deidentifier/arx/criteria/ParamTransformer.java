package org.deidentifier.arx.criteria;

import java.util.HashMap;
import java.util.Map;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class ParamTransformer {

	private static Map<BenchmarkDatafile, Integer>	dimDatafile = new HashMap<>();
	private static Map<String, Integer>				dimSa		= new HashMap<>();
	private static Map<BenchmarkCriterion, Integer>	dimCrit		= new HashMap<>();
	
	private static double[][][] maxVals;
	
	static {
		dimDatafile.put(BenchmarkDatafile.ACS13,	            0);
		dimDatafile.put(BenchmarkDatafile.ATUS,		            1);
		dimDatafile.put(BenchmarkDatafile.IHIS,		            2);
		
		dimSa.put("MS",									        0);
		dimSa.put("Marital status",						        0);
		dimSa.put("MARSTAT",							        0);
		dimSa.put("MS",									        0);
		dimSa.put("ED",									        1);
		dimSa.put("Education",							        1);
		dimSa.put("Highest level of school completed",	        1);
		dimSa.put("EDUC",								        1);

		dimCrit.put(BenchmarkCriterion.D_DISCLOSURE_PRIVACY,	0);
		dimCrit.put(BenchmarkCriterion.BASIC_BETA_LIKENESS,		1);
		
		maxVals = new double[dimDatafile.size()][dimSa.size()][dimCrit.size()];

		maxVals[dimDatafile.get(BenchmarkDatafile.ACS13)][dimSa.get("MS")][dimCrit.get(BenchmarkCriterion.D_DISCLOSURE_PRIVACY)]	=   7.161;
		maxVals[dimDatafile.get(BenchmarkDatafile.ACS13)][dimSa.get("MS")][dimCrit.get(BenchmarkCriterion.BASIC_BETA_LIKENESS)]		=  28.527;
		maxVals[dimDatafile.get(BenchmarkDatafile.ACS13)][dimSa.get("ED")][dimCrit.get(BenchmarkCriterion.D_DISCLOSURE_PRIVACY)]	=   6.362;
		maxVals[dimDatafile.get(BenchmarkDatafile.ACS13)][dimSa.get("ED")][dimCrit.get(BenchmarkCriterion.BASIC_BETA_LIKENESS)]		=  75.361;

		maxVals[dimDatafile.get(BenchmarkDatafile.ATUS)] [dimSa.get("MS")][dimCrit.get(BenchmarkCriterion.D_DISCLOSURE_PRIVACY)]	=   9.697;
		maxVals[dimDatafile.get(BenchmarkDatafile.ATUS)] [dimSa.get("MS")][dimCrit.get(BenchmarkCriterion.BASIC_BETA_LIKENESS)]		=  27.852;
		maxVals[dimDatafile.get(BenchmarkDatafile.ATUS)] [dimSa.get("ED")][dimCrit.get(BenchmarkCriterion.D_DISCLOSURE_PRIVACY)]	=  27.852;
		maxVals[dimDatafile.get(BenchmarkDatafile.ATUS)] [dimSa.get("ED")][dimCrit.get(BenchmarkCriterion.BASIC_BETA_LIKENESS)]		= 133.561;

		maxVals[dimDatafile.get(BenchmarkDatafile.IHIS)] [dimSa.get("MS")][dimCrit.get(BenchmarkCriterion.D_DISCLOSURE_PRIVACY)]	=  10.68;
		maxVals[dimDatafile.get(BenchmarkDatafile.IHIS)] [dimSa.get("MS")][dimCrit.get(BenchmarkCriterion.BASIC_BETA_LIKENESS)]		= 683.349;
		maxVals[dimDatafile.get(BenchmarkDatafile.IHIS)] [dimSa.get("ED")][dimCrit.get(BenchmarkCriterion.D_DISCLOSURE_PRIVACY)]	=   7.812;
		maxVals[dimDatafile.get(BenchmarkDatafile.IHIS)] [dimSa.get("ED")][dimCrit.get(BenchmarkCriterion.BASIC_BETA_LIKENESS)]		= 160.612;
	}
	
	public static double getNormalizedParamVal(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, double value) {
		
		return maxVals[dimDatafile.get(datafile)][dimSa.get(sa)][dimCrit.get(crit)] / value;
		
	}
	
	public static double getDenormalizedParamVal(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, double value) {
		
		return maxVals[dimDatafile.get(datafile)][dimSa.get(sa)][dimCrit.get(crit)] * value;
		
	}
}
