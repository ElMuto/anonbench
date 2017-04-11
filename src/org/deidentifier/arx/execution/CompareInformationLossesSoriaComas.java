package org.deidentifier.arx.execution;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.PrivacyModel;


public class CompareInformationLossesSoriaComas {
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		comparePrivacyModels();
		
		System.out.println("done.");
	}

	private static void comparePrivacyModels() throws IOException {

//		for (BenchmarkMeasure measure : new BenchmarkMeasure[] {BenchmarkMeasure.ENTROPY}) {
		for (BenchmarkMeasure measure : new BenchmarkMeasure[] {BenchmarkMeasure.SORIA_COMAS}) {

			// For each dataset
			for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

				// for each sensitive attribute candidate
				for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

					// for each privacy model
					for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsCombinedWithK()) {

						BenchmarkCriterion[] criteria = null;
						if (BenchmarkCriterion.K_ANONYMITY.equals(privacyModel.getCriterion())) {
							criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
						} else {
							criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, privacyModel.getCriterion() };
						}

						BenchmarkDataset dataset = new BenchmarkDataset(datafile, criteria, sa);
						BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);

						// for each suppression factor
						for (double suppFactor : new double[] { 0.05d }) {
							// Print status info
							System.out.println("Running " + privacyModel.toString() + " on " + datafile.toString() + " with SA=" + sa + " and IL-Measure " + measure);
							driver.anonymize(measure, suppFactor, dataset, false,
									privacyModel.getK(),
									privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
									privacyModel.getD(), privacyModel.getB(), null,
									null, sa, null, "results/resultsSC.csv", false, privacyModel);
						}
						dataset.getArxData().getHandle().release();
					}
				}
				System.out.println();
			}
		}
	}
}
