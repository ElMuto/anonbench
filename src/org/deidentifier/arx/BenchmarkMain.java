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

package org.deidentifier.arx;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMetric;
import org.deidentifier.arx.BenchmarkSetup.VARIABLES;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.buffered.BufferedArithmeticMeanAnalyzer;

/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class BenchmarkMain {

    /** Repetitions */
    private static final int       REPETITIONS  = 1;
    /** The benchmark instance */
    private static final Benchmark BENCHMARK    = new Benchmark(new String[] {
    		VARIABLES.UTLITY_METRIC.toString(),
    		VARIABLES.SUPPRESSION_FACTOR.toString(),
    		VARIABLES.DATASET.toString(),
    		VARIABLES.CRITERIA.toString()
    });
    
    /** Label for minimum utility */
    public static final int        INFO_LOSS    = BENCHMARK.addMeasure(VARIABLES.UTILITY_VALUE.toString());

    static {
        BENCHMARK.addAnalyzer(INFO_LOSS, new BufferedArithmeticMeanAnalyzer(REPETITIONS));
    }

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        EvaluateCriteriaWithDifferentSuppressionValues();
        
        System.out.println("done.");
    }

    private static void EvaluateCriteriaWithDifferentSuppressionValues() throws IOException {
        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);

        BenchmarkAlgorithm algorithm = BenchmarkAlgorithm.FLASH;

        // for each metric
        for (BenchmarkMetric metric : BenchmarkSetup.getMetrics()) {
        	
        	// for each suppression factor
        	for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {
        		
            // For each dataset
            for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

        			// For each combination of criteria
        			for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {

        				// Print status info
        				System.out.println("Running: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + data.toString() + " / " + Arrays.toString(criteria));

        				// Benchmark
        				BENCHMARK.addRun(metric.toString(), String.valueOf(suppFactor), data.toString(), Arrays.toString(criteria));

        				// Repeat
        				for (int i = 0; i < REPETITIONS; i++) {
        					driver.anonymize(data, criteria, algorithm, suppFactor, metric, false);
        				}

        				// Write results incrementally
        				BENCHMARK.getResults().write(new File("results/results.csv"));
        			}
        		}
        	}
        }
    }
}
