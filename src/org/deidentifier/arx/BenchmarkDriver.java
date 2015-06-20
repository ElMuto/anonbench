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

import java.io.IOException;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;

import de.linearbits.subframe.Benchmark;

/**
 * This class implements the main benchmark driver
 * @author Fabian Prasser
 */
public class BenchmarkDriver {

    /** The benchmark instance */
    private final Benchmark benchmark;

    /**
     * Creates a new benchmark driver
     * 
     * @param benchmark
     */
    public BenchmarkDriver(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    /**
     * Performs data anonymization
     * 
     * @param dataset
     * @param criteria
     * @param algorithm
     * @param warmup
     * @throws IOException
     */
    public void anonymize(BenchmarkDataset dataset,
                          BenchmarkCriterion[] criteria,
                          double suppFactor,
                          BenchmarkMeasure metric,
                          Integer k, Integer l, Integer c,
                          Double t, Double dMin, Double dMax,
                          String sa,
                          boolean warmup) throws IOException {

        // Build implementation
        // Prepare
        Data data = dataset.toArxData(criteria);
        ARXConfiguration config = BenchmarkSetup.getConfiguration(dataset, suppFactor, metric, k, l, c, t, dMin, dMax, sa, criteria);
        
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(data, config);
        if (result.getGlobalOptimum() != null) {
        	benchmark.addValue(BenchmarkMain.INFO_LOSS, Double.valueOf(result.getGlobalOptimum().getMinimumInformationLoss().toString()));
        } else {
        	System.out.println("No solution found");
        	benchmark.addValue(BenchmarkMain.INFO_LOSS, BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL);
        }
    }
}
