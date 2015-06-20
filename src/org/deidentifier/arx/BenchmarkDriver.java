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
        DataHandle output = result.getOutput().getView();
        
        
//        DataHandle handle = data.getHandle();
//
//        // Encode
//        final String[] header = ((DataHandleInput) handle).header;
//        final int[][] dataArray = ((DataHandleInput) handle).data;
//        final Dictionary dictionary = ((DataHandleInput) handle).dictionary;
//        final DataManager manager = new DataManager(header,
//                                                    dataArray,
//                                                    dictionary,
//                                                    data.getDefinition(),
//                                                    config.getCriteria());
//
//        // Initialize
//        config.initialize(manager);
//
//        // Build or clean the lattice
//        Lattice lattice = new LatticeBuilder(manager.getMaxLevels(),
//                                             manager.getMinLevels()).build();
//
//        // Build a node checker, for all algorithms but Incognito
//        INodeChecker checker = null;
//
//        // Initialize the metric
//        config.getMetric().initialize(handle.getDefinition(),
//                                      manager.getDataQI(),
//                                      manager.getHierarchies(),
//                                      config);
//
//
//        // Execute
//        implementation.traverse();
//        if (!warmup) {
            if (result.getGlobalOptimum() != null) {
//                benchmark.addValue(BenchmarkMain.INFO_LOSS, Double.valueOf(implementation.getGlobalOptimum().getInformationLoss().toString()));
                benchmark.addValue(BenchmarkMain.INFO_LOSS, Double.valueOf(result.getGlobalOptimum().getMinimumInformationLoss().toString()));
            } else {
                System.out.println("No solution found");
                benchmark.addValue(BenchmarkMain.INFO_LOSS, BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL);
            }
//        }
//    }
//
//    /**
//     * @param dataset
//     * @param criteria
//     * @param algorithm
//     * @return
//     * @throws IOException
//     */
//    private ARXAnonymizer getImplementation(BenchmarkDataset dataset,
//                                                         BenchmarkCriterion[] criteria,
//                                                         double suppFactor,
//                                                         BenchmarkMetric metric) throws IOException {
//return null;
    }
}
