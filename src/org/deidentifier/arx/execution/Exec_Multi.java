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
import java.util.Arrays;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.QiConfig;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;


/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class Exec_Multi {
	
	private static class PrivacyModel {
		private final BenchmarkCriterion criterion;
		private final Integer k;
		private final Double  c;
		private final Integer l;
		private final Double  t;
		private final boolean isSaBased;

		/**
		 * @param criterion
		 * @param k
		 * @param c
		 * @param l
		 * @param t
		 */
		public PrivacyModel(BenchmarkCriterion criterion, Integer k, Double c, Integer l, Double t, boolean isSaBased) {
			super();
			this.criterion = criterion;
			this.k = k;
			this.c = c;
			this.l = l;
			this.t = t;
			this.isSaBased = isSaBased;
		}

		public BenchmarkCriterion getCriterion() {
			return criterion;
		}

		public Integer getK() {
			return k;
		}

		public Double getC() {
			return c;
		}

		public Integer getL() {
			return l;
		}

		public Double getT() {
			return t;
		}
		
		public boolean isSaBased() {
			return isSaBased;
		}

		@Override
		public String toString() {
			String theString;
			switch (criterion) {
			case K_ANONYMITY:
				theString = k + "-anonymity";
				break;
			case L_DIVERSITY_DISTINCT:
				theString = "distinct-" + l + "-diversity";
				break;
			case L_DIVERSITY_ENTROPY:
				theString = "entropy-" + l + "-diversity";
				break;
			case L_DIVERSITY_RECURSIVE:
				theString = "recursive-(" + c + ", " + l + ")-diversity";
				break;
			case T_CLOSENESS_ED:
				theString = "equal-distance-" + t + "-closeness";
				break;
			case T_CLOSENESS_HD:
				theString = "hierarchical-distance-" + t + "-closeness";
				break;
			default:
				throw new RuntimeException("Invalid criterion");
			}
			return theString;
		}
	}

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
		PrivacyModel[] privacyModels = new PrivacyModel[] {
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_RECURSIVE, null, 3.0d, 2,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_RECURSIVE, null, 3.0d, 4,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_RECURSIVE, null, 3.0d, 6,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_RECURSIVE, null, 4.0d, 2,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_RECURSIVE, null, 4.0d, 4,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_RECURSIVE, null, 4.0d, 6,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_DISTINCT,  null, null, 2,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_DISTINCT,  null, null, 4,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_DISTINCT,  null, null, 6,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_ENTROPY,   null, null, 2,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_ENTROPY,   null, null, 4,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.L_DIVERSITY_ENTROPY,   null, null, 6,    null, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.T_CLOSENESS_HD,        null, null, null, 0.15d, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.T_CLOSENESS_HD,        null, null, null, 0.2d, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.T_CLOSENESS_ED,        null, null, null, 0.15d, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.T_CLOSENESS_ED,        null, null, null, 0.2d, true),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.K_ANONYMITY,            3,   null, null, null, false),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.K_ANONYMITY,            5,   null, null, null, false),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.K_ANONYMITY,           10,   null, null, null, false),
				new Exec_Multi.PrivacyModel(BenchmarkCriterion.K_ANONYMITY,           20,   null, null, null, false),
				};
		

		// for each privacy model
		for (PrivacyModel privacyModel : privacyModels) {

			// For each dataset
			for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

				// for each qi configuration
				for (QiConfig qiConf : BenchmarkSetup.getQiConfigPowerSet()) {

					BenchmarkMeasure measure = BenchmarkMeasure.LOSS;
					if (privacyModel.isSaBased()) {

						// for each sensitive attribute candidate
						for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

							BenchmarkDataset dataset = new BenchmarkDataset(datafile, qiConf, new BenchmarkCriterion[] { privacyModel.getCriterion() }, sa);
							BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);

							// for each suppression factor
							for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {
								// Print status info
								System.out.println("Running " + privacyModel.toString() + " with SA=" + sa + " and SF=" + suppFactor);
								driver.anonymize(measure, suppFactor, dataset, false,
										privacyModel.getK(),
										privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
										null, null, sa,
										null, qiConf);
							}
						}
					} else {
						
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

	private static void evaluateCriteriaWithDifferentSuppressionValues() throws IOException {

		// values for k, l, etc
		Integer k = 5;
		Integer l = 4;
		Double c = 3d;
		Double  t = 0.2d;
		Double  dMin = 0.05d;
		Double  dMax = 0.15d;
		Integer ssNum = null;

		// for each metric
		for (BenchmarkMeasure benchmarkMeasure : BenchmarkSetup.getMeasures()) {

			// for each suppression factor
			for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {

				// For each dataset
				for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {
					
					// For each combination of non subset-based criteria
					for (BenchmarkCriterion[] criteria : BenchmarkSetup.getNonSubsetBasedCriteria()) {
						// Print status info
						System.out.println("Running: " + benchmarkMeasure.toString() + " / " + String.valueOf(suppFactor) + " / " + datafile.toString() + " / " + Arrays.toString(criteria));
						BenchmarkDataset dataset = new BenchmarkDataset(datafile, null, criteria);
						BenchmarkDriver driver = new BenchmarkDriver(benchmarkMeasure, dataset);
						driver.anonymize(benchmarkMeasure, suppFactor, dataset, false, k, l, c, t, dMin, dMax, dataset.getSensitiveAttribute(), ssNum, null);
					}

					// For each combination of subset-based criteria
					for (BenchmarkCriterion[] criteria : BenchmarkSetup.getSubsetBasedCriteria()) {
						// Print status info
						BenchmarkDataset dataset = new BenchmarkDataset(datafile, null, criteria);
						System.out.println("Running: " + benchmarkMeasure.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / " + Arrays.toString(criteria));
						BenchmarkDriver driver = new BenchmarkDriver(benchmarkMeasure, dataset);
						driver.anonymize(benchmarkMeasure, suppFactor, new BenchmarkDataset(datafile, null, criteria), true, k, l, c, t, dMin, dMax, dataset.getSensitiveAttribute(), ssNum, null);
					}
				}
			}
		}
	}
}
