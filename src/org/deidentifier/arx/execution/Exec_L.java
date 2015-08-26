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
import org.deidentifier.arx.QiConfig;
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
        for (BenchmarkMeasure measure : BenchmarkSetup.getMeasures()) {
        	
            // for each suppression factor
            for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {

                // For each dataset
                for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

                    // for each sensitive attribute candidate
                    for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

                        for (QiConfig qiConf : BenchmarkSetup.getQiConfigPowerSet()) {
                            BenchmarkDataset dataset = new BenchmarkDataset(datafile, qiConf, new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_RECURSIVE }, sa);
                            BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);
                            for (int l = 3; l <= 3 ; l ++) {
                                for (double c : new double[] { 4d }) {
                                    // Print status info
                                    System.out.println("Running recursive (cl)-diversity: " + measure.toString() + " / " + String.valueOf(suppFactor) + " / " + dataset.toString() + " / SA = " + sa + " / c = " + c + " / l = " + l);
                                    driver.anonymize(measure, suppFactor, dataset, true, null,
                                                     l, c, null, 
                                                     null, null, sa,
                                                     null, qiConf);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
