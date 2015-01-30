package test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.algorithm.TerminationConfiguration;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;
import org.deidentifier.arx.metric.v2.AbstractILMultiDimensional;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;
import de.linearbits.subframe.analyzer.buffered.BufferedArithmeticMeanAnalyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedStandardDeviationAnalyzer;

public class InformationLossTest {

    /** Repetitions */
    private static final int         REPETITIONS         = 1;
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
        BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedStandardDeviationAnalyzer(REPETITIONS));
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
        String file = "results/resultsTest.csv";

        Algorithm[] algorithms = {
        		new Algorithm(AlgorithmType.FLASH, null),
        		new Algorithm(AlgorithmType.HEURAKLES, new TerminationConfiguration(TerminationConfiguration.Type.TIME, 10000))
        };
        BenchmarkDataset[] datasets = { BenchmarkDataset.ADULT, BenchmarkDataset.CUP };
        BenchmarkCriterion[] criteria = { BenchmarkCriterion.K_ANONYMITY };
        Metric<AbstractILMultiDimensional> lossMetric = Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN);
        Metric<AbstractILMultiDimensional> entropyMetric = Metric.createEntropyMetric();
        double suppression = 0.05d;

        // Runs for loss metric
        for (BenchmarkDataset data : datasets) {
            for (Algorithm algorithm : algorithms) {
                // Warmup run
                driver.anonymize(data, criteria, algorithm, lossMetric, suppression, true, true);

                // Benchmark
                BENCHMARK.addRun(algorithm.toString(),
                                 data.toString(),
                                 Arrays.toString(criteria),
                                 lossMetric.getName(),
                                 String.valueOf(suppression));

                driver.anonymize(data, criteria, algorithm, lossMetric, suppression, false, true);

                // Write results incrementally
                BENCHMARK.getResults().write(new File(file));
            }
        }

        // Runs for Non-monotonic non-uniform entropy
        // Warmup run
        driver.anonymize(BenchmarkDataset.ADULT, criteria, new Algorithm(AlgorithmType.FLASH, null), entropyMetric, suppression, true, true);

        // Benchmark
        BENCHMARK.addRun(AlgorithmType.FLASH,
                         BenchmarkDataset.ADULT.toString(),
                         Arrays.toString(criteria),
                         entropyMetric.getName(),
                         String.valueOf(suppression));

        driver.anonymize(BenchmarkDataset.ADULT, criteria, new Algorithm(AlgorithmType.FLASH, null), entropyMetric, suppression, false, true);

        // Write results incrementally
        BENCHMARK.getResults().write(new File(file));

    }
}
