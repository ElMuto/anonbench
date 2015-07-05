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
public class Exec_D {

	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		evaluate_d_presence();
		System.out.println("done.");
	}

	private static void evaluate_d_presence() throws IOException {
		
		// for each metric
		for (BenchmarkMeasure benchmarkMeasure : BenchmarkSetup.getMeasures()) {

			// for each suppression factor
			for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {

				// For each dataset
				for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets(new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE })) {

		            BenchmarkDriver driver = new BenchmarkDriver(benchmarkMeasure, dataset);

					// For each combination of non subset-based criteria
					for (double[] dParams : BenchmarkSetup.get_d_values()) {
						
						for (int ssNum = 1; ssNum <= 100; ssNum++) {

							// Print status info
							System.out.println("Running d-Presence: " + benchmarkMeasure.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / d = [" + dParams[0] + ", " + dParams[1] + "], subset-num = " + ssNum);
	 							driver.anonymize(benchmarkMeasure, suppFactor, dataset, false, null,
									null, null, null, 
									dParams[0], dParams[1], null,
									ssNum);
						}
					}
				}
			}
		}
	}
}
