package org.deidentifier.arx.util;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;

public class CommandLineParser {
	public static BenchmarkCriterion parseCritString(String dim2Qual) {
		String[] allowedPrivacyMeasures = new String[] { "ld", "lr", "le", "t", "d", "b" };
		BenchmarkCriterion crit = null;
		boolean validInput = false;		
		for (String s : allowedPrivacyMeasures) {
			if (dim2Qual != null && s.equals(dim2Qual)) {
				validInput = true;
				break;
			}
		}		
		if (validInput) {
			if ("ld".equals(dim2Qual)) {
				crit = BenchmarkCriterion.L_DIVERSITY_DISTINCT;
			} else if ("lr".equals(dim2Qual)) {
				crit = BenchmarkCriterion.L_DIVERSITY_RECURSIVE;
			} else if ("le".equals(dim2Qual)) {
				crit = BenchmarkCriterion.L_DIVERSITY_ENTROPY;
			} else if ("t".equals(dim2Qual)) {
				crit = BenchmarkCriterion.T_CLOSENESS_ED;
			} else if ("d".equals(dim2Qual)) {
				crit = BenchmarkCriterion.D_DISCLOSURE_PRIVACY;
			} else if ("b".equals(dim2Qual)) {
				crit = BenchmarkCriterion.BASIC_BETA_LIKENESS;
			} else {
				throw new RuntimeException("Unsupported input string: '" + dim2Qual + "'");
			}	
		} else {
			throw new RuntimeException("Unsupported input string: '" + dim2Qual + "'");
		}
		return crit;
	}
}
