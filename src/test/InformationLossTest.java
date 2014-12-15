package test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.algorithm.AbstractBenchmarkAlgorithm;
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
        
        BenchmarkAlgorithm[] algorithms = {BenchmarkAlgorithm.FLASH, BenchmarkAlgorithm.HEURAKLES};
        BenchmarkDataset[] datasets = {BenchmarkDataset.ADULT, BenchmarkDataset.CUP};
        BenchmarkCriterion[] criteria = {BenchmarkCriterion.K_ANONYMITY};
        Metric<AbstractILMultiDimensional> metric = Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN);
        double suppression = 0.05d;
       
        for(BenchmarkDataset data: datasets) {
            for(BenchmarkAlgorithm algorithm : algorithms) {
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

                driver.anonymize(data, criteria, algorithm, metric, suppression, false);

                // Write results incrementally
                BENCHMARK.getResults().write(new File("results/resultsTest.csv"));
            }
        }
    }
}


