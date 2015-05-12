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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.BenchmarkConfiguration.AnonConfiguration;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
import org.deidentifier.arx.algorithm.AbstractBenchmarkAlgorithm;
import org.deidentifier.arx.algorithm.AlgorithmFlash;
import org.deidentifier.arx.algorithm.AlgorithmHeuraklesBFSDFS;
import org.deidentifier.arx.algorithm.AlgorithmHeuraklesDFS;
import org.deidentifier.arx.algorithm.AlgorithmHeuraklesBFS;
import org.deidentifier.arx.algorithm.AlgorithmInformationLossBounds;
import org.deidentifier.arx.framework.check.INodeChecker;
import org.deidentifier.arx.framework.check.NodeChecker;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.data.Dictionary;
import org.deidentifier.arx.framework.lattice.AbstractLattice;
import org.deidentifier.arx.framework.lattice.LatticeBuilder;
import org.deidentifier.arx.framework.lattice.MaterializedLattice;
import org.deidentifier.arx.framework.lattice.VirtualLattice;

import cern.colt.Arrays;
import de.linearbits.subframe.Benchmark;

/**
 * This class implements the main benchmark driver
 * @author Fabian Prasser
 */
public class BenchmarkDriver {

    /** the integer value used for the information loss metric in case no solution has been found **/
    public static final int NO_SOLUTION_FOUND    = -1;

    /** The benchmark instance */
    private final Benchmark benchmark;

    /** History size. */
    private final int       historySize          = 200;

    /** Snapshot size. */
    private final double    snapshotSizeDataset  = 0.2d;

    /** Snapshot size snapshot */
    private final double    snapshotSizeSnapshot = 0.8d;

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
    public void anonymize(AnonConfiguration c, boolean warmup) throws IOException {

        // Build implementation
        AbstractBenchmarkAlgorithm implementation = null;
        try {
            implementation = getImplementation(c, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (AlgorithmType.INFORMATION_LOSS_BOUNDS == c.getAlgorithm().getType()) {
            // run for DFS over whole lattice in order to determine the minimal and maximal values in regards to information loss
            AlgorithmInformationLossBounds algo = (AlgorithmInformationLossBounds) implementation;
            algo.traverse();
            // TODO handle the case that no solution exists at all
            benchmark.addValue(BenchmarkMain.INFORMATION_LOSS_MINIMUM,
                               (null != algo.getGlobalMinimum() ? algo.getGlobalMinimum().getInformationLoss() : NO_SOLUTION_FOUND));
            benchmark.addValue(BenchmarkMain.INFORMATION_LOSS_MINIMUM_TRANSFORMATION,
                               null != algo.getGlobalMinimum() ? (Arrays.toString(algo.getGlobalMinimum().getTransformation())) : Arrays.toString(new int[0]));
            benchmark.addValue(BenchmarkMain.INFORMATION_LOSS_MAXIMUM,
                               null != algo.getGlobalMaximum() ? algo.getGlobalMaximum().getInformationLoss() : NO_SOLUTION_FOUND);
            benchmark.addValue(BenchmarkMain.INFORMATION_LOSS_MAXIMUM_TRANSFORMATION, null != algo.getGlobalMaximum() ?
                    (Arrays.toString(algo.getGlobalMaximum().getTransformation())) : Arrays.toString(new int[0]));
        }
        // for Flash, Heurakles
        else if (!warmup) {
            // Execute
            benchmark.startTimer(BenchmarkMain.EXECUTION_TIME);
            long startTimestamp = System.nanoTime();
            implementation.traverse();

            // Add values
            benchmark.addStopTimer(BenchmarkMain.EXECUTION_TIME);
            benchmark.addValue(BenchmarkMain.NUMBER_OF_CHECKS, implementation.getNumChecks());
            benchmark.addValue(BenchmarkMain.NUMBER_OF_ROLLUPS, implementation.getNumRollups());
            benchmark.addValue(BenchmarkMain.NUMBER_OF_SNAPSHOTS, implementation.getNumSnapshots());
            benchmark.addValue(BenchmarkMain.LATTICE_SIZE, implementation.getLatticeSize());
            benchmark.addValue(BenchmarkMain.TOTAL_LATTICE_SIZE, implementation.getTotalLatticeSize());
            benchmark.addValue(BenchmarkMain.LATTICE_COMPLETED, implementation.getLatticeCompleted());
            benchmark.addValue(BenchmarkMain.INFORMATION_LOSS_TRANSFORMATION, implementation.getGlobalOptimum() != null ?
                    Arrays.toString(implementation.getGlobalOptimum().getTransformation()) :
                    Arrays.toString(new int[0]));

            if (BenchmarkSetup.RECORD_ALL_OPTIMA) {
                List<Long> discoveryTimes = new ArrayList<Long>();
                for (Long timestamp : implementation.getOptimumCheckedTimestamps())
                    discoveryTimes.add(timestamp - startTimestamp);
                benchmark.addValue(BenchmarkMain.SOLUTION_DISCOVERY_TIME, discoveryTimes.toString());
                benchmark.addValue(BenchmarkMain.INFORMATION_LOSS, implementation.getOptimaInformationLosses().toString());
            } else {
                benchmark.addValue(BenchmarkMain.INFORMATION_LOSS, getInformationLoss(implementation, c));
                int size = implementation.getOptimumCheckedTimestamps().size();
                benchmark.addValue(BenchmarkMain.SOLUTION_DISCOVERY_TIME, size > 0 ?
                        implementation.getOptimumCheckedTimestamps().get(size - 1) - startTimestamp :
                        NO_SOLUTION_FOUND);
            }
        }
    }

    /**
     * Returns and potentially converts IL
     * @param implementation
     * @param c
     * @param globalOptimum
     * @return
     * @throws IOException
     */
    private String getInformationLoss(AbstractBenchmarkAlgorithm implementation, AnonConfiguration c) throws IOException {
        if (implementation.getGlobalOptimum() == null) {
            return String.valueOf(NO_SOLUTION_FOUND);
        } else if (c.getILMetric() == null) {
            return implementation.getGlobalOptimum().getInformationLoss().toString();
        } else {
            AbstractBenchmarkAlgorithm algorithm = getImplementation(c, true);
            return algorithm.getInformationLoss(implementation.getGlobalOptimum()).toString();
        }
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
            getImplementation(AnonConfiguration c, boolean useILMetric) throws IOException {
        // Prepare
        Data data = BenchmarkSetup.getData(c.getDataset(), c.getPrivacyCriteria(), c.getQICount());
        ARXConfiguration config = BenchmarkSetup.getConfiguration(c.getDataset(),
                                                                  useILMetric ? c.getILMetric() : c.getDecisionMetric(),
                                                                  c.getSuppression(),
                                                                  c.getQICount(),
                                                                  c.getPrivacyCriteria());

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
        if (AlgorithmType.HEURAKLES_DFS == c.getAlgorithm().getType() || AlgorithmType.HEURAKLES_BFS == c.getAlgorithm().getType() ||
            AlgorithmType.DATAFLY == c.getAlgorithm().getType() ||
            AlgorithmType.IMPROVED_GREEDY == c.getAlgorithm().getType()) {
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
        switch (c.getAlgorithm().getType()) {
        case FLASH:
            implementation = AlgorithmFlash.create((MaterializedLattice) lattice, checker, manager.getHierarchies());
            break;
        case HEURAKLES_DFS:
        case DATAFLY:
        case IMPROVED_GREEDY:
            implementation = new AlgorithmHeuraklesDFS(lattice, checker, c);
            break;
        case INFORMATION_LOSS_BOUNDS:
            implementation = new AlgorithmInformationLossBounds((MaterializedLattice) lattice, checker);
            break;
        case HEURAKLES_BFS:
            implementation = new AlgorithmHeuraklesBFS(lattice, checker, c);
            break;
        case HEURAKLES_BFSDFS:
            implementation = new AlgorithmHeuraklesBFSDFS(lattice, checker, c);
            break;
        default:
            throw new RuntimeException("Invalid algorithm");
        }
        return implementation;
    }
}
