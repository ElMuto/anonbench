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
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;

/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class ExecIntra_L {

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        evaluate_l_diversity();
        System.out.println("done.");
    }

    private static void evaluate_l_diversity() throws IOException {

        // for each metric
        for (BenchmarkMeasure metric : BenchmarkSetup.getMeasures()) {

            // for each suppression factor
            for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {

                // For each dataset
                for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

                    // for each sensitive attribute candidate
                    for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

                        BenchmarkDataset dataset = new BenchmarkDataset(datafile, 4, sa);

                        // distinct l-diversity
                        BenchmarkCriterion[] criteria = new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_DISTINCT };
                        Data arxData = dataset.toArxData(criteria);
                        for (int l : new int[] { 2, 3, 4, 5, 7, 10, 15, 20, 30, 50, 70, 100 }) {
                            // Print status info
                            System.out.println("Running distinct l-diversity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / SA = " + sa + " / l = " + l);
                            BenchmarkDriver.anonymize(metric, suppFactor, dataset, arxData, criteria,
                                                      false, null, l, 
                                                      null, null, null,
                                                      null, sa, null);
                        }

                        // entropy l-diversity
                        criteria = new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_ENTROPY };
                        arxData = dataset.toArxData(criteria);
                        for (int l : new int[] { 2, 3, 4, 5, 7, 10, 15, 20, 30, 50, 70, 100 }) {
                            // Print status info
                            System.out.println("Running entropy l-diversity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / SA = " + sa + " / l = " + l);
                            BenchmarkDriver.anonymize(metric, suppFactor, dataset, arxData, new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_ENTROPY },
                                                      false, null, l, 
                                                      null, null, null,
                                                      null, sa, null);
                        }

                        // recursive c,l-diversity
                        criteria = new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_RECURSIVE };
                        arxData = dataset.toArxData(criteria);
                        for (int l : new int[] { 2, 3, 4, 5, 7, 10, 15, 20, 30, 50, 70, 100 }) {
                            for (double c : new double[] { 0.5, 1.0, 1.5d, 2d, 3d, 5d }) {
                                // Print status info
                                System.out.println("Running recursive (cl)-diversity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / SA = " + sa + " / c = " + c + " / l = " + l);
                                BenchmarkDriver.anonymize(metric, suppFactor, dataset, arxData, new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
                                                          false, null, l, 
                                                          c, null, null,
                                                          null, sa, null);
                            }
                        }
                    }
                }
            }
        }
    }
}
