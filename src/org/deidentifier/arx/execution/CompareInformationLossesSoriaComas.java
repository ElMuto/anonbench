/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal 
 *      methods for the de-identification of biomedical data"
 *      
 * Copyright (C) 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
			for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafilesSoriaComas()) {

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
									null, sa, null, "results/resultsSC.csv");
						}
						dataset.getArxData().getHandle().release();
					}
				}
				System.out.println();
			}
		}
	}
}
