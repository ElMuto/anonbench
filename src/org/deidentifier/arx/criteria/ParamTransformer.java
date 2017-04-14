package org.deidentifier.arx.criteria;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class ParamTransformer {
	
	public static double getNormalizedParamVal(BenchmarkDataset dataset, String sa, BenchmarkCriterion crit, double value) {		
		
		Double[] minMax = getRpgMinAndMax(dataset, sa, crit);
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
	
	public static double getDenormalizedParamVal(BenchmarkDataset dataset, String sa, BenchmarkCriterion crit, double value) {		
		
		Double[] minMax = getRpgMinAndMax(dataset, sa, crit);
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

	private static Double[] getRpgMinAndMax(BenchmarkDataset dataset, String sa, BenchmarkCriterion crit) {
		Double rpgMin;
		Double rpgMax;
		switch(crit) {
		case BASIC_BETA_LIKENESS:
			rpgMin = AttributeStatistics.get(dataset, sa).getRpgBmin();
			rpgMax = AttributeStatistics.get(dataset, sa).getRpgBmax();
			break;
		case D_DISCLOSURE_PRIVACY:
			rpgMin = AttributeStatistics.get(dataset, sa).getRpgDmin();
			rpgMax = AttributeStatistics.get(dataset, sa).getRpgDmax();
			break;
		case T_CLOSENESS_ED:
			rpgMin = AttributeStatistics.get(dataset, sa).getRpgTmin();
			rpgMax = AttributeStatistics.get(dataset, sa).getRpgTmax();
			break;
		case L_DIVERSITY_DISTINCT:
		case L_DIVERSITY_ENTROPY:
		case L_DIVERSITY_RECURSIVE:
			rpgMin = AttributeStatistics.get(dataset, sa).getRpgLmin();
			rpgMax = AttributeStatistics.get(dataset, sa).getRpgLmax();
			break;
		default:
			throw new IllegalArgumentException("Invalid crit: " + crit);		
		}
		
		return new Double[] { rpgMin, rpgMax };
	}
}
