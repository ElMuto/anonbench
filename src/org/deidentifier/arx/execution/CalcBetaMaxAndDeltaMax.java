package org.deidentifier.arx.execution;

import java.util.Arrays;

import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.ComparisonSetup;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class CalcBetaMaxAndDeltaMax {

	public static void main(String[] args) {

		for (BenchmarkDatafile datafile : new BenchmarkDatafile[] { BenchmarkDatafile.ACS13, BenchmarkDatafile.ATUS, BenchmarkDatafile.IHIS }) {

			for (String sa : new String[] { "MS", "ED" } ) {

				ComparisonSetup compSetup =  createCompSetup(datafile, sa, 0.05d, BenchmarkCriterion.T_CLOSENESS_ED);
				
				ARXResult result = compSetup.anonymizeTrafos(
						new int[] { 0, 0, 0 },
						new int[] { 0, 0, 0 }
//						new int[] { 3, 1, 0 },
//						new int[] { 3, 1, 0 }
						);

	        	DataHandle outHandle = result.getOutput(result.getGlobalOptimum(), false);
	        	int numOfsuppressedRecords = outHandle.getStatistics().getEquivalenceClassStatistics().getNumberOfOutlyingTuples();

	            
	            System.out.printf("%s - %s:\t%s=%.4f,\t%s=%.4f,\t%s=%s,\t%s=%d\n", datafile, sa,
	            		"dMax", DisclosureRiskCalculator.getDelta().getMax(),
	            		"bMax", DisclosureRiskCalculator.getBeta().getMax(),
	            		"traFo", Arrays.toString(result.getGlobalOptimum().getTransformation()),
	            		"numSupRecs", numOfsuppressedRecords);
	            
	            outHandle.release();
			}

		}

	}
	
	static ComparisonSetup createCompSetup(BenchmarkDatafile datafile, String sa, double sf, BenchmarkCriterion criterion) {
		
		PrivacyModel privacyModel = new PrivacyModel(criterion);

		String convertedSA = null;
		switch(datafile) {
		case ACS13:
			if ("MS".equals(sa)) {
				convertedSA = "Marital status";
			} else if ("ED".equals(sa)) {
				convertedSA = "Education";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
			break;
		case ATUS:
			if ("MS".equals(sa)) {
				convertedSA = "Marital status";
			} else if ("ED".equals(sa)) {
				convertedSA = "Highest level of school completed";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
			break;
		case IHIS:
			if ("MS".equals(sa)) {
				convertedSA = "MARSTAT";
			} else if ("ED".equals(sa)) {
				convertedSA = "EDUC";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid datafile: " + datafile);
		}

		return new  ComparisonSetup(
    			new BenchmarkCriterion[] {BenchmarkCriterion.K_ANONYMITY, criterion },
    			privacyModel,
    			datafile,
    			sf,
    			BenchmarkMeasure.ENTROPY,
    			convertedSA);
	}
}
