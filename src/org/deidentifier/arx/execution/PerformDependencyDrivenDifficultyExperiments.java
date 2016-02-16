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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.QiConfig;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;

import org.deidentifier.arx.PrivacyModel;


/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class PerformDependencyDrivenDifficultyExperiments {
	
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

		BenchmarkMeasure measure = BenchmarkMeasure.LOSS;
		List<String> lines = Files.readAllLines(Paths.get("dependency_classes.csv"), StandardCharsets.UTF_8);
		
		for (String line : lines) {

			String[] lineTokens = line.split(";");

			Double[] accuracies        = parseAccuracies(lineTokens);
			BenchmarkDatafile datafile = parseDatafile(lineTokens);
			QiConfig qiConf            = parseQiConf(lineTokens);
			String se                  = parseSe(lineTokens);

			// for each privacy model
			for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModels()) {
				if (privacyModel.isSaBased()) {
					BenchmarkDataset dataset = new BenchmarkDataset(datafile, qiConf, new BenchmarkCriterion[] { privacyModel.getCriterion() }, se);
					BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);
					// for each suppression factor
					for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {
						// Print status info
						System.out.println("Running " + privacyModel.toString() + " with SF=" + suppFactor);
						driver.anonymize(measure, suppFactor, dataset, false,
								privacyModel.getK(),
								privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
								null, null, se,
								null, qiConf, accuracies);
					}
				} else { // !privacyModel.isSaBased()

					BenchmarkDataset dataset = new BenchmarkDataset(datafile, qiConf, new BenchmarkCriterion[] { privacyModel.getCriterion() }, null);
					BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);

					// for each suppression factor
					for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {
						// Print status info
						System.out.println("Running " + privacyModel.toString() + " with SF=" + suppFactor);
						driver.anonymize(measure, suppFactor, dataset, false,
								privacyModel.getK(),
								privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
								null, null, null,
								null, qiConf);
					}
				}
			}
			System.out.println();
		}
	}


	private static String parseSe(String[] lineTokens) {
		return lineTokens[3];
	}

	private static QiConfig parseQiConf(String[] lineTokens) {
		return new QiConfig(lineTokens[2].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(", ", ",").split(","));
	}

	private static BenchmarkDatafile parseDatafile(String[] lineTokens) {
		int di = 0;
		if (lineTokens[di].equals("ACS13")) {
			return BenchmarkDatafile.ACS13;
		} else if (lineTokens[di].equals("Adult")) {
			return BenchmarkDatafile.ADULT;
		} else if (lineTokens[di].equals("Atus")) {
			return BenchmarkDatafile.ATUS;
		} else if (lineTokens[di].equals("Cup")) {
			return BenchmarkDatafile.CUP;
		} else if (lineTokens[di].equals("Fars")) {
			return BenchmarkDatafile.FARS;
		} else if (lineTokens[di].equals("Ihis")) {
			return BenchmarkDatafile.IHIS;
		} else {
			return null;
		}
	}

	private static Double[] parseAccuracies(String[] lineTokens) {
		return new Double[] { Double.parseDouble(lineTokens[4]), Double.parseDouble(lineTokens[5]) };
	}
}
