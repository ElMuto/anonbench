package org.deidentifier.arx.criteria;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class ParamTransformer {
	
	public static double getNormalizedParamVal(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, double value) {		
		
		Double[] minMax = getRpgMinAndMax(datafile, sa, crit);
		Double rpgMin = minMax[0];
		Double rpgMax = minMax[1];

		Double result = null;
		
		switch (crit) {
		case BASIC_BETA_LIKENESS:
		case D_DISCLOSURE_PRIVACY:
		case T_CLOSENESS_ED:
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
			result = (value - rpgMin) / (rpgMax - rpgMin);
			break;
		default:
			throw new IllegalArgumentException("Invalid crit: " + crit);	
		}
		
		return result;
	}
	
	public static Double getDenormalizedParamVal(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, double value) {		
		
		Double[] minMax = getRpgMinAndMax(datafile, sa, crit);
		Double rpgMin = minMax[0];
		Double rpgMax = minMax[1];

		Double result = null;
		
		switch (crit) {
		case BASIC_BETA_LIKENESS:
		case D_DISCLOSURE_PRIVACY:
		case T_CLOSENESS_ED:
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
			result = (value * (rpgMax - rpgMin)) + rpgMin;
			break;
		default:
			throw new IllegalArgumentException("Invalid crit: " + crit);	
		}
		
		return result;
	}

	private static Double[] getRpgMinAndMax(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit) {
		Double rpgMin;
		Double rpgMax;
		switch(crit) {
		case BASIC_BETA_LIKENESS:
			rpgMin = AttributeStatistics.get(datafile, sa).getRpgBmin();
			rpgMax = AttributeStatistics.get(datafile, sa).getRpgBmax();
			break;
		case D_DISCLOSURE_PRIVACY:
			rpgMin = AttributeStatistics.get(datafile, sa).getRpgDmin();
			rpgMax = AttributeStatistics.get(datafile, sa).getRpgDmax();
			break;
		case T_CLOSENESS_ED:
			rpgMin = AttributeStatistics.get(datafile, sa).getRpgTmin();
			rpgMax = AttributeStatistics.get(datafile, sa).getRpgTmax();
			break;
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
			rpgMin = AttributeStatistics.get(datafile, sa).getRpgLmin();
			rpgMax = AttributeStatistics.get(datafile, sa).getRpgLmax();
			break;
		default:
			throw new IllegalArgumentException("Invalid crit: " + crit);		
		}
		
		return new Double[] { rpgMin, rpgMax };
	}
}
