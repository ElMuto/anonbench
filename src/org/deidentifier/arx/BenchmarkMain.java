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
import java.text.ParseException;
import java.util.Arrays;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.metric.Metric;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;
import de.linearbits.subframe.analyzer.buffered.BufferedArithmeticMeanAnalyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedStandardDeviationAnalyzer;

/**
 * Main benchmark class. Run with java -Xmx5G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class BenchmarkMain {

    /** Repetitions */
    private static final int         REPETITIONS                     = 3;
    /** The benchmark instance */
    protected static final Benchmark BENCHMARK                       = new Benchmark(BenchmarkSetup.getHeader());
    /** Label for execution times */
    public static final int          EXECUTION_TIME                  = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.EXECUTION_TIME.val);
    /** Label for number of checks */
    public static final int          NUMBER_OF_CHECKS                = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.NUMBER_OF_CHECKS.val);
    /** Label for number of roll-ups */
    public static final int          NUMBER_OF_ROLLUPS               = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.NUMBER_OF_ROLLUPS.val);
    /** Label for number of roll-ups */
    public static final int          NUMBER_OF_SNAPSHOTS             = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.NUMBER_OF_SNAPSHOTS.val);
    /** Label for size of lattice */
    public static final int          LATTICE_SIZE                    = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.LATTICE_SIZE.val);
    /** Label for information loss */
    public static final int          INFORMATION_LOSS                = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS.val);
    /** Label for information loss transformation */
    public static final int          INFORMATION_LOSS_TRANSFORMATION = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_TRANSFORMATION.val);

    static {
        BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedArithmeticMeanAnalyzer(REPETITIONS));
        BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedStandardDeviationAnalyzer(REPETITIONS));
        BENCHMARK.addAnalyzer(NUMBER_OF_CHECKS, new ValueBuffer());
        BENCHMARK.addAnalyzer(NUMBER_OF_ROLLUPS, new ValueBuffer());
        BENCHMARK.addAnalyzer(NUMBER_OF_SNAPSHOTS, new ValueBuffer());
        BENCHMARK.addAnalyzer(LATTICE_SIZE, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFORMATION_LOSS, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFORMATION_LOSS_TRANSFORMATION, new ValueBuffer());
    }

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {

        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);
        String outputFileName = BenchmarkSetup.RESULTS_FILE;
        boolean benchmarkRun = true;

        // For each algorithm
        for (Algorithm algorithm : BenchmarkSetup.getBenchmarkAlgorithms()) {
            BenchmarkDriver.runIterations(BENCHMARK, driver, REPETITIONS, outputFileName, benchmarkRun, algorithm);
        }

        // Determine min/max IL via DFS traversal over the whole lattice and add data to results.csv
        if (BenchmarkSetup.INCLUDE_RELATIVE_INFORMATION_LOSS) {
            BenchmarkILBounds.main(new String[] {});
        }
    }

}
