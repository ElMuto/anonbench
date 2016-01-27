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
import org.deidentifier.arx.ClassificationConfig;
import org.deidentifier.arx.QiConfig;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.ClassificationConfig.Classifier;

import weka.core.Instances;

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

//		evaluateCriteriaWithDifferentSuppressionValues();
		comparePrivacyModels();
		System.out.println("done.");
	}
	
	private static void comparePrivacyModels() throws IOException {
		
		BenchmarkDatafile[] datafiles = new BenchmarkDatafile[] {
		         BenchmarkDatafile.ADULT,
		         BenchmarkDatafile.ACS13,
		         BenchmarkDatafile.FARS,
		         BenchmarkDatafile.ATUS,
		         BenchmarkDatafile.IHIS,
		};

		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModels()) {

			// For each dataset
			for (BenchmarkDatafile datafile : datafiles) {

				// for each qi configuration
				for (QiConfig qiConf : BenchmarkSetup.getQiConfigPowerSet()) {

					BenchmarkMeasure measure = BenchmarkMeasure.LOSS;
					if (privacyModel.isSaBased()) {

						// for each sensitive attribute candidate
						for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

							BenchmarkDataset dataset = new BenchmarkDataset(datafile, qiConf, new BenchmarkCriterion[] { privacyModel.getCriterion() }, sa);
							BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);

							Double[] accuracies = determineExperimentType(dataset);
							// for each suppression factor
							for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {
								// Print status info
								System.out.println("Running " + privacyModel.toString() + " with SA=" + sa + " and SF=" + suppFactor);
								driver.anonymize(measure, suppFactor, dataset, false,
										privacyModel.getK(),
										privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
										null, null, sa,
										null, qiConf, accuracies);
							}
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
			}
		}
	}


    private static Double[] determineExperimentType(BenchmarkDataset dataset) {
    	
		ClassificationConfig conf_se_se = new ClassificationConfig(
				"",
				Classifier.J48,
				dataset.getDatafile().getBaseStringForFilename() + "_comma.csv",
				dataset.getSensitiveAttribute(),
				dataset.getQuasiIdentifyingAttributes(),
				null).asBaselineConfig();		
		Instances data_se_se = CalculateClassificationAccuracies.loadData(conf_se_se);
		double acc_se_se = CalculateClassificationAccuracies.getClassificationAccuracyFor(data_se_se, conf_se_se.getClassAttribute(), conf_se_se.getClassifier()).pctCorrect();
		
		ClassificationConfig conf_qi_se = new ClassificationConfig(
				"",
				Classifier.J48,
				dataset.getDatafile().getBaseStringForFilename() + "_comma.csv",
				dataset.getSensitiveAttribute(),
				dataset.getQuasiIdentifyingAttributes(),
				null).invertExclusionSet();
		Instances data_qi_se = CalculateClassificationAccuracies.loadData(conf_qi_se);
		double acc_qi_se = CalculateClassificationAccuracies.getClassificationAccuracyFor(data_qi_se, conf_qi_se.getClassAttribute(), conf_qi_se.getClassifier()).pctCorrect();
		
		System.out.printf("acc1=%f, acc2=%f, diff=%f percent\n", acc_se_se, acc_qi_se, 100d * (acc_qi_se - acc_se_se) / acc_se_se);
		return new Double[] { acc_se_se, acc_qi_se };
	}
}
