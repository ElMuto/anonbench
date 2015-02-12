/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal
 * methods for the de-identification of biomedical data"
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

import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.algorithm.AbstractBenchmarkAlgorithm;
import org.deidentifier.arx.algorithm.AlgorithmFlash;
import org.deidentifier.arx.algorithm.AlgorithmHeurakles;
import org.deidentifier.arx.algorithm.AlgorithmInformationLossBounds;
import org.deidentifier.arx.framework.check.INodeChecker;
import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.data.Dictionary;
import org.deidentifier.arx.framework.lattice.AbstractLattice;
import org.deidentifier.arx.framework.lattice.LatticeBuilder;
import org.deidentifier.arx.framework.lattice.MaterializedLattice;
import org.deidentifier.arx.framework.lattice.Node;
import org.deidentifier.arx.framework.lattice.VirtualLattice;
import org.deidentifier.arx.metric.InformationLoss;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.test.TestConfiguration;

import cern.colt.Arrays;
import de.linearbits.subframe.Benchmark;

/**
 * This class implements the main benchmark driver
 * @author Fabian Prasser
 */
public class BenchmarkDriver {

    /** Snapshot size. */
    private final double    snapshotSizeDataset  = 0.2d;

    /** Snapshot size snapshot */
    private final double    snapshotSizeSnapshot = 0.8d;

    /** History size. */
    private final int       historySize          = 200;

    /** The benchmark instance */
    private final Benchmark benchmark;

    /** the integer value used for the information loss metric in case no solution has been found **/
    public static final int NO_SOLUTION_FOUND    = -1;

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
     * @param suppression
     * @param metric
     * @param runTimeLimit the number of checks, after which Heurakles should terminate. This parameter is ignored by the other algorithms
     * @param warmup
     * @param benchmarkRun true if a regular benchmark run shall be executed, false for a DFS search over the whole lattice in order to determine minmal/maximal information loss
     * @throws IOException
     */
    public void anonymize(BenchmarkDataset dataset,
                          BenchmarkCriterion[] criteria,
                          Algorithm algorithm,
                          Metric<?> metric, double suppression, int qiCount, boolean warmup, boolean benchmarkRun) throws IOException {

        // Build implementation
        AbstractBenchmarkAlgorithm implementation = getImplementation(dataset, criteria, algorithm, metric, suppression, qiCount);

        // for real benchmark run
        if (benchmarkRun) {
            // Execute
            if (!warmup) benchmark.startTimer(BenchmarkMain.EXECUTION_TIME);
            implementation.traverse();
            if (!warmup) benchmark.addStopTimer(BenchmarkMain.EXECUTION_TIME);
            if (!warmup) benchmark.addValue(BenchmarkMain.NUMBER_OF_CHECKS, implementation.getNumChecks());
            if (!warmup) benchmark.addValue(BenchmarkMain.NUMBER_OF_ROLLUPS, implementation.getNumRollups());
            if (!warmup) benchmark.addValue(BenchmarkMain.NUMBER_OF_SNAPSHOTS, implementation.getNumSnapshots());
            if (!warmup) benchmark.addValue(BenchmarkMain.LATTICE_SIZE, implementation.getLatticeSize());
            if (!warmup) benchmark.addValue(BenchmarkMain.INFORMATION_LOSS, implementation.getGlobalOptimum() != null ?
                    implementation.getGlobalOptimum().getInformationLoss().toString() :
                    NO_SOLUTION_FOUND);
            if (!warmup) benchmark.addValue(BenchmarkMain.INFORMATION_LOSS_TRANSFORMATION, implementation.getGlobalOptimum() != null ?
                    Arrays.toString(implementation.getGlobalOptimum().getTransformation()) :
                    Arrays.toString(new int[0]));
        }
        // run for DFS over whole lattice in order to determine the minimal and maximal values in regards to information loss
        else {
            if (!warmup) {
                AlgorithmInformationLossBounds algo = (AlgorithmInformationLossBounds) implementation;
                algo.traverse();
                // TODO handle the case that no solution exists at all
                benchmark.addValue(BenchmarkILBounds.INFORMATION_LOSS_MINIMUM,
                                   (null != algo.getGlobalMinimum() ? algo.getGlobalMinimum().getInformationLoss() : NO_SOLUTION_FOUND));
                benchmark.addValue(BenchmarkILBounds.INFORMATION_LOSS_MINIMUM_TRANSFORMATION,
                                   null != algo.getGlobalMinimum() ? (Arrays.toString(algo.getGlobalMinimum().getTransformation())) : Arrays.toString(new int[0]));
                benchmark.addValue(BenchmarkILBounds.INFORMATION_LOSS_MAXIMUM,
                                   null != algo.getGlobalMaximum() ? algo.getGlobalMaximum().getInformationLoss() : NO_SOLUTION_FOUND);
                benchmark.addValue(BenchmarkILBounds.INFORMATION_LOSS_MAXIMUM_TRANSFORMATION, null != algo.getGlobalMaximum() ?
                        (Arrays.toString(algo.getGlobalMaximum().getTransformation())) : Arrays.toString(new int[0]));
            }
        }
    }

    /**
     * Performs data anonymization and returns a TestConfiguration
     * 
     * @param dataset
     * @param criteria
     * @param algorithm
     * @param warmup
     * @throws IOException
     */
    public TestConfiguration test(BenchmarkDataset dataset,
                                  BenchmarkCriterion[] criteria,
                                  Algorithm algorithm, Metric<?> metric, double suppression, int qiCount) throws IOException {

        // Build implementation
        AbstractBenchmarkAlgorithm implementation = getImplementation(dataset, criteria, algorithm, metric, suppression, qiCount);

        // Execute
        implementation.traverse();

        // Collect
        Node optimum = implementation.getGlobalOptimum();
        String loss = String.valueOf(optimum.getInformationLoss().getValue());
        int[] transformation = optimum.getTransformation();

        return new TestConfiguration(dataset, criteria, loss, transformation);
    }

    /**
     * @param dataset
     * @param criteria
     * @param algorithm
     * @param metric
     * @param suppression
     * @return
     * @throws IOException
     */
    private AbstractBenchmarkAlgorithm
            getImplementation(BenchmarkDataset dataset,
                              BenchmarkCriterion[] criteria,
                              Algorithm algorithm, Metric<?> metric, double suppression, int qiCount) throws IOException {
        // Prepare
        Data data = BenchmarkSetup.getData(dataset, criteria, qiCount);
        ARXConfiguration config = BenchmarkSetup.getConfiguration(dataset, metric, suppression, qiCount, criteria);
        DataHandle handle = data.getHandle();

        // Encode
        final String[] header = ((DataHandleInput) handle).header;
        final int[][] dataArray = ((DataHandleInput) handle).data;
        final Dictionary dictionary = ((DataHandleInput) handle).dictionary;
        final DataManager manager = new DataManager(header,
                                                    dataArray,
                                                    dictionary,
                                                    data.getDefinition(),
                                                    config.getCriteria());

        // Initialize
        config.initialize(manager);

        // Build or clean the lattice
        AbstractLattice lattice;
        // Heurakles does not need materialized lattice
        if (AlgorithmType.HEURAKLES == algorithm.getType()) {
            lattice = new VirtualLattice(manager.getMinLevels(), manager.getMaxLevels());
        }
        else {
            lattice = new LatticeBuilder(manager.getMaxLevels(),
                                         manager.getMinLevels()).build();
        }

        // Build a node checker, for all algorithms but Incognito
        INodeChecker checker = new NodeChecker(manager,
                                               config.getMetric(),
                                               config.getInternalConfiguration(),
                                               historySize,
                                               snapshotSizeDataset,
                                               snapshotSizeSnapshot);

        // Initialize the metric
        config.getMetric().initialize(handle.getDefinition(),
                                      manager.getDataQI(),
                                      manager.getHierarchies(),
                                      config);

        // Create an algorithm instance
        AbstractBenchmarkAlgorithm implementation;
        switch (algorithm.getType()) {
        case FLASH:
            implementation = AlgorithmFlash.create((MaterializedLattice) lattice, checker, manager.getHierarchies());
            break;
        case HEURAKLES:
            implementation = new AlgorithmHeurakles(lattice, checker, algorithm.getTerminationConfig());
            break;
        case INFORMATION_LOSS_BOUNDS:
            implementation = new AlgorithmInformationLossBounds((MaterializedLattice) lattice, checker);
            break;
        default:
            throw new RuntimeException("Invalid algorithm");
        }
        return implementation;
    }

    public static void
            runIterations(Benchmark BENCHMARK,
                          BenchmarkDriver driver,
                          int REPETITIONS,
                          String outputFileName,
                          boolean benchmarkRun,
                          Algorithm algorithm) throws IOException {

        // For each metric
        for (Metric<?> metric : BenchmarkSetup.getMetrics()) {

            // For each suppression factor
            for (double suppression : BenchmarkSetup.getSuppression()) {

                // For each combination of criteria
                for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {

                    // For each dataset
                    for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

                        for (int qiCount = BenchmarkSetup.getMinQICount(data); qiCount <= BenchmarkSetup.getMaxQICount(algorithm, data); qiCount++) {

                            runBenchmark(BENCHMARK, REPETITIONS, driver, outputFileName, benchmarkRun,
                                         algorithm,
                                         data,
                                         criteria,
                                         metric,
                                         suppression,
                                         qiCount);

                        }
                    }
                }
            }
        }

    }

    private static void runBenchmark(Benchmark BENCHMARK,
                                     int REPETITIONS,
                                     BenchmarkDriver driver,
                                     String outputFileName,
                                     boolean benchmarkRun,
                                     Algorithm algorithm,
                                     BenchmarkDataset data,
                                     BenchmarkCriterion[] criteria,
                                     Metric<?> metric,
                                     double suppression,
                                     int qiCount) throws IOException {
        // Warmup run
        driver.anonymize(data, criteria, algorithm, metric, suppression, qiCount, true, benchmarkRun);

        // Print status info
        System.out.println("Running: " + algorithm.toString() + " with " + algorithm.getStatusSuffix() + " / " + data.toString() + " / " +
                           metric.getName() +
                           " / " + suppression + " / " + Arrays.toString(criteria) + " / " + qiCount + " QIs");

        // Benchmark
        BENCHMARK.addRun(algorithm.toString(),
                         data.toString(),
                         Arrays.toString(criteria),
                         metric.getName(),
                         String.valueOf(suppression),
                         qiCount);

        // Repeat
        for (int i = 0; i < REPETITIONS; i++) {
            driver.anonymize(data, criteria, algorithm, metric, suppression, qiCount, false, benchmarkRun);
        }

        // Write results incrementally
        BENCHMARK.getResults().write(new File(outputFileName));
    }
}
