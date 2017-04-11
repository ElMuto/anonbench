package org.deidentifier.arx.execution;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.PrivacyModel;


/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class Compare2d_InfoLoss {
	
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

		for (BenchmarkMeasure measure : new BenchmarkMeasure[] {BenchmarkMeasure.LOSS, BenchmarkMeasure.ENTROPY}) {

			// For each dataset
			for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

				// for each sensitive attribute candidate
				for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

					// for each privacy model
					for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsConfigsForParameterComparison("t", sa)) {

						BenchmarkCriterion[] criteria =  new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, privacyModel.getCriterion() };
						
						BenchmarkDataset dataset = new BenchmarkDataset(datafile, criteria, sa);
						BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);

						// for each suppression factor
						for (double suppFactor : new double[] { 0.05d }) {
							// Print status info
							System.out.println("Running t=" + privacyModel.getT() + ", k=" + privacyModel.getK() + " on " + datafile.toString() + " with SA=" + sa + " and IL-Measure " + measure);
							driver.anonymize(measure, suppFactor, dataset, false,
									privacyModel.getK(),
									privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
									privacyModel.getD(), privacyModel.getB(), null,
									null, sa, null, "results/resultsTKIL.csv", false, privacyModel);
						}
						dataset.getArxData().getHandle().release();
					}
				}
			}
		}
	}
}
