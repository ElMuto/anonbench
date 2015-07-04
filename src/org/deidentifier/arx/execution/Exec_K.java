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
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;

/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class Exec_K {

	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		evaluate_k_anonymity();
		System.out.println("done.");
	}

	private static void evaluate_k_anonymity() throws IOException {
		
		// for each metric
		for (BenchmarkMeasure metric : BenchmarkSetup.getMeasures()) {

			// for each suppression factor
			for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {

				// For each dataset
				for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets(new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY })) {

					// For each combination of non subset-based criteria
					for (int k = 2; k <= 100; k ++) {

						// Print status info
						System.out.println("Running k-Anonymity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / k = " + k);
 						BenchmarkDriver.anonymize(metric, suppFactor, dataset, false, k,
								null, null, null, 
								null, null, null,
								null);
					}
				}
			}
		}
	}
}
