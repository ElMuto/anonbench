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
						datafile, 0.00d, BenchmarkMeasure.ENTROPY, sa);
				
				int[] traFo = { 0, 0, 0 };
				ARXResult result = compSetup.anonymizeTrafos(traFo, traFo);

				DataHandle outHandle;
				int numOfsuppressedRecords = 0;
				if (result.getGlobalOptimum() != null) {
					outHandle = result.getOutput(result.getGlobalOptimum(), false);
					numOfsuppressedRecords = outHandle.getStatistics().getEquivalenceClassStatistics().getNumberOfOutlyingTuples();
		            outHandle.release();
				}
	            
	            System.out.printf("%s - %s:\t%s=%.4f,\t%s=%.4f,\t%s=%s,\t%s=%d\n", datafile, sa,
	            		"dMax", DisclosureRiskCalculator.getDelta().getMax(),
	            		"bMax", DisclosureRiskCalculator.getBeta().getMax(),
	            		"traFo", Arrays.toString(traFo),
	            		"numSupRecs", numOfsuppressedRecords);
	            
			}

		}

	}
}
