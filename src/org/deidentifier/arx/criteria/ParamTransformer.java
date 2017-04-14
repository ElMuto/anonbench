package org.deidentifier.arx.criteria;

import java.util.HashMap;
import java.util.Map;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class ParamTransformer {

	private static Map<String, Integer>				dimSa		= new HashMap<>();

	public static enum PG { min, max };
	
	private static double[][][][] minMaxVals;
	
	static {
		
		dimSa.put("MS",									        0);
		dimSa.put("Marital status",						        0);
		dimSa.put("MARSTAT",							        0);
		dimSa.put("MS",									        0);
		dimSa.put("ED",									        1);
		dimSa.put("Education",							        1);
		dimSa.put("Highest level of school completed",	        1);
		dimSa.put("EDUC",								        1);
		
		minMaxVals = new double[BenchmarkDatafile.values().length][dimSa.size()][BenchmarkCriterion.values().length][2];

		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.min.ordinal()]	=   7.161;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.min.ordinal()]	=  72.8185; // korrigiert von 28.527 auf 72.8185
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.min.ordinal()]	=   6.5767; // urspr.: 6.362;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.min.ordinal()]	=  94.4514;; // korrigert. Urspr.: 75.361;

		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.min.ordinal()]	=   9.697;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.min.ordinal()]	=  99.9837; // urspr. 27.852;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.min.ordinal()]	=  9.0936; // urspr. 9.094;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.min.ordinal()]	= 335.4024; // urspr. 133.561;

		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.min.ordinal()]	=  10.68;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.min.ordinal()]	= 683.34862; // urspr. 683.349;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.min.ordinal()]	=   9.6583; // urspr. 7.812;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.min.ordinal()]	= 807.0596; // urspr. 160.612;
		


		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.max.ordinal()]	= 0d;

		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.max.ordinal()]	= 0d;

		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.T_CLOSENESS_ED.ordinal()]      [PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.D_DISCLOSURE_PRIVACY.ordinal()][PG.max.ordinal()]	= 0d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.BASIC_BETA_LIKENESS.ordinal()][PG.max.ordinal()]	= 0d;
		
		


		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.max.ordinal()]		=   5d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.max.ordinal()]	=   5d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.max.ordinal()]		=   5d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.max.ordinal()]		=  25d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.max.ordinal()]	=  25d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.max.ordinal()]		=  25d;

		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.max.ordinal()]	=   7d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.max.ordinal()]	=   7d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.max.ordinal()]	=   7d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.max.ordinal()]	=  18d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.max.ordinal()]	=  18d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.max.ordinal()]	=  18d;
		
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.max.ordinal()]	=  10d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.max.ordinal()]	=  10d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.max.ordinal()]	=  10d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.max.ordinal()]	=  26d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.max.ordinal()]	=  26d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.max.ordinal()]	=  26d;

		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.min.ordinal()]		=   1d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.min.ordinal()]		=   1d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.min.ordinal()]		=   1d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ACS13.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.min.ordinal()]		=   1d;

		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.ATUS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.min.ordinal()]	=   1d;
		
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("MS")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_DISTINCT.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_RECURSIVE.ordinal()][PG.min.ordinal()]	=   1d;
		minMaxVals[BenchmarkDatafile.IHIS.ordinal()][dimSa.get("ED")][BenchmarkCriterion.L_DIVERSITY_ENTROPY.ordinal()][PG.min.ordinal()]	=   1d;
	}
	
	public static double getNormalizedParamVal(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, double value) {
		
		double minPg = minMaxVals[datafile.ordinal()][dimSa.get(sa)][crit.ordinal()][PG.min.ordinal()];
		double maxPg = minMaxVals[datafile.ordinal()][dimSa.get(sa)][crit.ordinal()][PG.max.ordinal()];
		
		Double result = null;
		
		if (	BenchmarkCriterion.T_CLOSENESS_ED.equals(crit) ||
				BenchmarkCriterion.D_DISCLOSURE_PRIVACY.equals(crit) ||
				BenchmarkCriterion.BASIC_BETA_LIKENESS.equals(crit)) {
			result =  (minPg - value) / (minPg - maxPg);
		} else if (	BenchmarkCriterion.L_DIVERSITY_DISTINCT.equals(crit) ||
					BenchmarkCriterion.L_DIVERSITY_RECURSIVE.equals(crit) ||
					BenchmarkCriterion.L_DIVERSITY_ENTROPY.equals(crit)) {
			result = (value - minPg) / (maxPg - minPg);
		} else {
			throw new IllegalArgumentException("Invalid crit: " + crit);
		}

//		System.out.println(crit + "-minPg=" + minPg + ", maxPg=" + maxPg + ", value=" + value + ", result=" + result);
		
		return result;
	}
}
