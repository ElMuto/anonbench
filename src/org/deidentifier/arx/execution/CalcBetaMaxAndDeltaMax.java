package org.deidentifier.arx.execution;

import java.util.Arrays;

import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.ComparisonSetup;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class CalcBetaMaxAndDeltaMax {

	public static void main(String[] args) {

		for (BenchmarkDatafile datafile : new BenchmarkDatafile[] { BenchmarkDatafile.ACS13, BenchmarkDatafile.ATUS, BenchmarkDatafile.IHIS }) {

			for (String sa : new String[] { "MS", "ED" } ) {

				BenchmarkCriterion criterion = BenchmarkCriterion.T_CLOSENESS_ED;
				ComparisonSetup compSetup =  new ComparisonSetup(
						new BenchmarkCriterion[] {BenchmarkCriterion.K_ANONYMITY, criterion },
						datafile, 0.05d, BenchmarkMeasure.ENTROPY, sa);
				
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
}
