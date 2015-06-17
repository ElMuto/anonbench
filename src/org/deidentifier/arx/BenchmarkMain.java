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

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.BenchmarkSetup.PLOT_VARIABLES;

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
            PLOT_VARIABLES.UTLITY_MEASURE.toString(),
            PLOT_VARIABLES.SUPPRESSION_FACTOR.toString(),
            PLOT_VARIABLES.DATASET.toString(),
            PLOT_VARIABLES.CRITERIA.toString(),
            PLOT_VARIABLES.SUBSET_NATURE.toString(),
            PLOT_VARIABLES.PARAM_K.toString(),
            PLOT_VARIABLES.PARAM_L.toString(),
            PLOT_VARIABLES.PARAM_C.toString(),
            PLOT_VARIABLES.PARAM_T.toString(),
            PLOT_VARIABLES.PARAM_DMIN.toString(),
            PLOT_VARIABLES.PARAM_DMAX.toString(),
            PLOT_VARIABLES.SENS_ATTR.toString(),
//            VARIABLES.QI_SET.toString(),
//            VARIABLES.SS_NUM.toString(),
    });
    
    /** Label for minimum utility */
    public static final int        INFO_LOSS    = BENCHMARK.addMeasure(PLOT_VARIABLES.UTILITY_VALUE.toString());

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

//        evaluateCriteriaWithDifferentSuppressionValues();
        evaluate_k_anonymity();
        
        System.out.println("done.");
    }

    private static void evaluateCriteriaWithDifferentSuppressionValues() throws IOException {
        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);
        
        // values for k, l, etc
        Integer k = 5;
        Integer l = 4;
        Integer c = 3;
        Double  t = 0.2d;
        Double  dMin = 0.05d;
        Double  dMax = 0.15d;
        String  sa = null;

        // for each metric
        for (BenchmarkMeasure metric : BenchmarkSetup.getMeasures()) {
        	
        	// for each suppression factor
        	for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {
        		
            // For each dataset
            for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

    			// For each combination of non subset-based criteria
    			for (BenchmarkCriterion[] criteria : BenchmarkSetup.getNonSubsetBasedCriteria()) {
                    // Print status info
                    System.out.println("Running: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + data.toString() + " / " + Arrays.toString(criteria));
    				processCriteria(driver, metric, suppFactor, data, criteria, false, k, l, c, t, dMin, dMax, sa);
    			}

    			// For each combination of subset-based criteria
    			for (BenchmarkCriterion[] criteria : BenchmarkSetup.getSubsetBasedCriteria()) {
                    // Print status info
                    System.out.println("Running: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + data.toString() + " / " + Arrays.toString(criteria));
    				processCriteria(driver, metric, suppFactor, data, criteria, true, k, l, c, t, dMin, dMax, sa);
    			}
        		}
        	}
        }
    }
    
    private static void evaluate_k_anonymity() throws IOException {
        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);
        // for each metric
        for (BenchmarkMeasure metric : BenchmarkSetup.getMeasures()) {
            
            // for each suppression factor
            for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {
                
            // For each dataset
            for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

                // For each combination of non subset-based criteria
                for (int k : BenchmarkSetup.get_k_values()) {

                    // Print status info
                    System.out.println("Running k-Anonymity: " + metric.toString() + " / " + String.valueOf(suppFactor) + " / " + data.toString() + " / k = " + k);
                    processCriteria(driver, metric, suppFactor, data, new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY }, false,
                                    k, null, null, 
                                    null, null, null,
                                    null);
                }
            }
        }
    }
    }

	private static void processCriteria(BenchmarkDriver driver,
			BenchmarkMeasure metric,
			double suppFactor, BenchmarkDataset data,
			BenchmarkCriterion[] criteria, boolean subsetBased,
			Integer k, Integer l, Integer c,
			Double t, Double dMin, Double dMax,
			String _sa
			) throws IOException {

		// Benchmark
		BENCHMARK.addRun(metric.toString(),
		                 String.valueOf(suppFactor),
		                 data.toString(),
		                 Arrays.toString(criteria),
		                 Boolean.toString(subsetBased),
		                 k != null ? k.toString() : "", l != null ? l.toString() : "", c != null ? c.toString() : "",
		                 t != null ? t.toString() : "", dMin != null ? dMin.toString() : "", dMax != null ? dMax.toString() : "",
		                 _sa != null ? _sa.toString() : "");

		// Repeat
		for (int i = 0; i < REPETITIONS; i++) {
			driver.anonymize(data, criteria, suppFactor, metric, k, l, c, t, dMin, dMax, _sa, false);
		}

		// Write results incrementally
		BENCHMARK.getResults().write(new File("results/results.csv"));
	}
}
