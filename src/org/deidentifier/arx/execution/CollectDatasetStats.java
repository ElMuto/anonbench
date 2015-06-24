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
public class CollectDatasetStats {

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        collectStats();
    }

    private static void collectStats() throws IOException {

        BenchmarkMeasure metric = BenchmarkSetup.getMeasures()[0];
        double suppFactor = BenchmarkSetup.getSuppressionFactors()[0];

        // For each dataset
        for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

            BenchmarkDataset data = new BenchmarkDataset(datafile, 4);

            System.out.println("Getting stats for dataset " + data.toString());
            BenchmarkDriver.anonymize(metric, suppFactor, data, new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY }, false,
                                      5, null, null, 
                                      null, null, null,
                                      null, null, true);
        }
    }
}
