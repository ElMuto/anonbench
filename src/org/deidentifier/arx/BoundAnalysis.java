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

public class BoundAnalysis {

    /** The benchmark instance */
    protected static final Benchmark BENCHMARK                               = new Benchmark(new String[] {
                                                                             "Algorithm",
                                                                             "Dataset",
                                                                             "Criteria",
                                                                             "Metric",
                                                                             "Suppression" });

    public static final int          INFORMATION_LOSS_MINIMUM                = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MINIMUM.val);
    public static final int          INFORMATION_LOSS_MINIMUM_TRANSFORMATION = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MINIMUM_TRANSFORMATION.val);
    public static final int          INFORMATION_LOSS_MAXIMUM                = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MAXIMUM.val);
    public static final int          INFORMATION_LOSS_MAXIMUM_TRANSFORMATION = BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MAXIMUM_TRANSFORMATION.val);

    static {
        BENCHMARK.addAnalyzer(INFORMATION_LOSS_MINIMUM, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFORMATION_LOSS_MINIMUM_TRANSFORMATION, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFORMATION_LOSS_MAXIMUM, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFORMATION_LOSS_MAXIMUM_TRANSFORMATION, new ValueBuffer());
    }

    public static void main(String[] args) throws IOException {

        int numRuns = BenchmarkSetup.getDatasets().length * BenchmarkSetup.getMetrics().length * BenchmarkSetup.getSuppression().length *
                      BenchmarkSetup.getCriteria().length;
        int counter = 1;

        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);
        BenchmarkAlgorithm algorithm = BenchmarkAlgorithm.INFORMATION_LOSS_BOUNDS;

        // For each dataset
        for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

            // For each metric
            for (Metric<?> metric : BenchmarkSetup.getMetrics()) {

                // For each suppression factor
                for (double suppression : BenchmarkSetup.getSuppression()) {

                    // For each combination of criteria
                    for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {

                        // Print status info
                        System.out.println("Running: (" + counter + "/" + numRuns + ") " + algorithm.toString() + " / " + data.toString() +
                                           " / " +
                                           metric.getName() +
                                           " / " + suppression + " / " +
                                           Arrays.toString(criteria));

                        // Benchmark
                        BENCHMARK.addRun(algorithm.toString(),
                                         data.toString(),
                                         Arrays.toString(criteria),
                                         metric.getName(),
                                         String.valueOf(suppression));

                        driver.anonymize(data, criteria, algorithm, metric, suppression, 0, false, false);

                        // Write results incrementally
                        BENCHMARK.getResults().write(new File("results/informationLossBounds.csv"));
                        counter++;
                    }
                }
            }
        }

    }
}
