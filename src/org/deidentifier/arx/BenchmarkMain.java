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
import java.util.Arrays;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.metric.Metric;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;
import de.linearbits.subframe.analyzer.buffered.BufferedArithmeticMeanAnalyzer;

/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class BenchmarkMain {

    /** Repetitions */
    private static final int         REPETITIONS         = 3;
    /** The benchmark instance */
    protected static final Benchmark BENCHMARK           = new Benchmark(new String[] {
                                                         "Algorithm",
                                                         "Dataset",
                                                         "Criteria",
                                                         "Metric",
                                                         "Suppression" });
    /** Label for execution times */
    public static final int          EXECUTION_TIME      = BENCHMARK.addMeasure("Execution time");
    /** Label for number of checks */
    public static final int          NUMBER_OF_CHECKS    = BENCHMARK.addMeasure("Number of checks");
    /** Label for number of roll-ups */
    public static final int          NUMBER_OF_ROLLUPS   = BENCHMARK.addMeasure("Number of rollups");
    /** Label for number of roll-ups */
    public static final int          NUMBER_OF_SNAPSHOTS = BENCHMARK.addMeasure("Number of snapshots");
    /** Label for size of lattice */
    public static final int          LATTICE_SIZE        = BENCHMARK.addMeasure("Size of lattice");
    /** Label for information loss */
    public static final int          INFORMATION_LOSS    = BENCHMARK.addMeasure("Information loss");

    static {
        BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedArithmeticMeanAnalyzer(REPETITIONS));
        // TODO currently not working since only one value per criteria-combination is written to output and standardDeviation results in Nan
        // BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedStandardDeviationAnalyzer(REPETITIONS));
        BENCHMARK.addAnalyzer(NUMBER_OF_CHECKS, new ValueBuffer());
        BENCHMARK.addAnalyzer(NUMBER_OF_ROLLUPS, new ValueBuffer());
        BENCHMARK.addAnalyzer(NUMBER_OF_SNAPSHOTS, new ValueBuffer());
        BENCHMARK.addAnalyzer(LATTICE_SIZE, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFORMATION_LOSS, new ValueBuffer());
    }

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);

        // For each algorithm
        for (BenchmarkAlgorithm algorithm : BenchmarkSetup.getAlgorithms()) {

            // For each dataset
            for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

                // For each metric
                for (Metric<?> metric : BenchmarkSetup.getMetrics()) {

                    // For each suppression factor
                    for (double suppression : BenchmarkSetup.getSuppression()) {

                        // For each combination of criteria
                        for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {

                            // Warmup run
                            driver.anonymize(data, criteria, algorithm, metric, suppression, true);

                            // Print status info
                            System.out.println("Running: " + algorithm.toString() + " / " + data.toString() + " / " + metric.getName() +
                                               " / " + suppression + " / " +
                                               Arrays.toString(criteria));

                            // Benchmark
                            BENCHMARK.addRun(algorithm.toString(),
                                             data.toString(),
                                             Arrays.toString(criteria),
                                             metric.getName(),
                                             String.valueOf(suppression));

                            // TODO check how the #repetitions affect the result values
                            // Repeat
                            for (int i = 0; i < REPETITIONS; i++) {
                                driver.anonymize(data, criteria, algorithm, metric, suppression, false);
                            }

                            // Write results incrementally
                            BENCHMARK.getResults().write(new File("results/results.csv"));
                        }
                    }
                }
            }
        }
    }
}
