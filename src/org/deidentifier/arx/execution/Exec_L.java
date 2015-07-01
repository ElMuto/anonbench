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
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;

/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class Exec_L {

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
                        for (int l = 2; l <= 20 ; l++) {
                            // Print status info
                            System.out.println("Running distinct l-diversity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / SA = " + sa + " / l = " + l);
                            BenchmarkDriver.anonymize(metric, suppFactor, dataset, criteria, true,
                                                      null, l, null, 
                                                      null, null, null,
                                                      sa, null);
                        }

                        // entropy l-diversity
                        criteria = new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_ENTROPY };
                        for (int l = 2; l <= 20 ; l++) {
                            // Print status info
                            System.out.println("Running entropy l-diversity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / SA = " + sa + " / l = " + l);
                            BenchmarkDriver.anonymize(metric, suppFactor, dataset, criteria, true,
                                                      null, l, null, 
                                                      null, null, null,
                                                      sa, null);
                        }

                        // recursive c,l-diversity
                        criteria = new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_RECURSIVE };
                        for (int l = 2; l <= 20 ; l ++) {
                            for (double c : new double[] { 0.25, 0.5, 0.75, 1d,
                                                           1.25, 1.5, 1.75, 2d,
                                                           2.25, 2.5, 2.75, 3d,
                                                           3.25, 3.5, 3.75, 4d,
                                                           4.25, 4.5, 4.75, 5d,
                                                           5.25, 5.5, 5.75, 6d,}) {
                                // Print status info
                                System.out.println("Running recursive (cl)-diversity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / SA = " + sa + " / c = " + c + " / l = " + l);
                                BenchmarkDriver.anonymize(metric, suppFactor, dataset, criteria, true,
                                                          null, l, c, 
                                                          null, null, null,
                                                          sa, null);
                            }
                        }
                    }
                }
            }
        }
    }
}
