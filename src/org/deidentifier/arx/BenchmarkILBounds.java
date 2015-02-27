package org.deidentifier.arx;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;

import org.deidentifier.arx.BenchmarkAnalysis.VARIABLES;
import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.metric.Metric;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;

public class BenchmarkILBounds {

    /** Repetitions */
    private static final int         REPETITIONS                             = 1;

    /** The benchmark instance */
    protected static final Benchmark BENCHMARK                               = new Benchmark(BenchmarkSetup.getHeader());

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

    public static void main(String[] args) throws IOException, ParseException {

        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);
        String outputFileName = BenchmarkSetup.INFORMATION_LOSS_FILE;
        boolean benchmarkRun = false;

        Algorithm algorithm = new BenchmarkSetup.Algorithm(AlgorithmType.INFORMATION_LOSS_BOUNDS, null);

        BenchmarkDriver.runIterations(BENCHMARK, driver, REPETITIONS, outputFileName, benchmarkRun, algorithm);

        writeInformationLossBoundsToResults(algorithm);
    }

    /**
     * This method reads the information loss bounds from informationLossBounds.csv and adds the data as new columns into results.csv.
     * @throws IOException
     * @throws ParseException
     */
    private static void writeInformationLossBoundsToResults(Algorithm algorithm) throws IOException, ParseException {
        CSVFile results = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));

        // add header column information loss minimum
        results.addHeader1Column(VARIABLES.INFORMATION_LOSS_MINIMUM.val);
        results.addHeader2Column("Value");

        // add header column information loss minimum (transformation)
        results.addHeader1Column(VARIABLES.INFORMATION_LOSS_MINIMUM_TRANSFORMATION.val);
        results.addHeader2Column("Value");

        // add header column information loss maximum
        results.addHeader1Column(VARIABLES.INFORMATION_LOSS_MAXIMUM.val);
        results.addHeader2Column("Value");

        // add header column information loss maximum (transformation)
        results.addHeader1Column(VARIABLES.INFORMATION_LOSS_MAXIMUM_TRANSFORMATION.val);
        results.addHeader2Column("Value");

        // add header column information loss percentage
        results.addHeader1Column(VARIABLES.INFORMATION_LOSS_PERCENTAGE.val);
        results.addHeader2Column("Value");

        CSVFile bounds = new CSVFile(new File(BenchmarkSetup.INFORMATION_LOSS_FILE));
        Selector<String[]> selector;
        double min = 0;
        double max = 0;
        String minTransformation = "";
        String maxTransformation = "";

        // For each dataset
        for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

            // for each metric
            for (Metric<?> metric : BenchmarkSetup.getMetrics()) {

                // For each combination of criteria
                for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
                    String scriteria = Arrays.toString(criteria);

                    // for each suppression
                    for (double suppr : BenchmarkSetup.getSuppression()) {
                        String suppression = String.valueOf(suppr);

                        for (int qiCount = BenchmarkSetup.getMinQICount(data); qiCount <= BenchmarkSetup.getMaxQICount(algorithm,
                                                                                                                       data); qiCount++) {

                            // Select data point acc to the variables
                            selector = bounds.getSelectorBuilder()
                                             .field(VARIABLES.DATASET.val)
                                             .equals(data.toString())
                                             .and()
                                             .field(VARIABLES.CRITERIA.val)
                                             .equals(scriteria)
                                             .and()
                                             .field(VARIABLES.METRIC.val)
                                             .equals(metric.getName())
                                             .and()
                                             .field(VARIABLES.SUPPRESSION.val)
                                             .equals(suppression)
                                             .and()
                                             .field(VARIABLES.QI_COUNT.val)
                                             .equals(String.valueOf(qiCount))
                                             .build();

                            Iterator<CSVLine> iter = bounds.iterator();
                            while (iter.hasNext()) {
                                CSVLine csvline = iter.next();
                                String[] line = csvline.getData();
                                if (selector.isSelected(line)) {
                                    // save min and max information loss
                                    min = Double.parseDouble(csvline.get(VARIABLES.INFORMATION_LOSS_MINIMUM.val, "Value"));
                                    minTransformation = csvline.get(VARIABLES.INFORMATION_LOSS_MINIMUM_TRANSFORMATION.val, "Value");
                                    max = Double.parseDouble(csvline.get(VARIABLES.INFORMATION_LOSS_MAXIMUM.val, "Value"));
                                    maxTransformation = csvline.get(VARIABLES.INFORMATION_LOSS_MAXIMUM_TRANSFORMATION.val, "Value");
                                    break;
                                }
                            }

                            for (Algorithm benchmarkAlgorithm : BenchmarkSetup.getBenchmarkAlgorithms()) {

                                // Select data point acc to the variables
                                selector = results.getSelectorBuilder()
                                                  .field(VARIABLES.ALGORITHM.val)
                                                  .equals(benchmarkAlgorithm.toString())
                                                  .and()
                                                  .field(VARIABLES.DATASET.val)
                                                  .equals(data.toString())
                                                  .and()
                                                  .field(VARIABLES.CRITERIA.val)
                                                  .equals(scriteria)
                                                  .and()
                                                  .field(VARIABLES.METRIC.val)
                                                  .equals(metric.getName())
                                                  .and()
                                                  .field(VARIABLES.SUPPRESSION.val)
                                                  .equals(suppression)
                                                  .and()
                                                  .field(VARIABLES.QI_COUNT.val)
                                                  .equals(String.valueOf(qiCount))
                                                  .build();

                                iter = results.iterator();
                                while (iter.hasNext()) {
                                    CSVLine csvLine = iter.next();
                                    String[] line = csvLine.getData();
                                    if (selector.isSelected(line)) {
                                        // get information loss value
                                        double value = Double.parseDouble(csvLine.get(VARIABLES.INFORMATION_LOSS.val, "Value"));

                                        // compute relative percentage
                                        if (max == min && min == value) {
                                            value = 0d;
                                        } else if (value != BenchmarkDriver.NO_SOLUTION_FOUND) {
                                            value = ((value - min) / (max - min)) * 100.0;
                                        }
                                        
                                        // add the value to the line
                                        csvLine.addColumn(String.valueOf(min));
                                        csvLine.addColumn(minTransformation);
                                        csvLine.addColumn(String.valueOf(max));
                                        csvLine.addColumn(maxTransformation);
                                        csvLine.addColumn(String.valueOf(value));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        results.write(new File("results/results.csv"));
    }

    private void doReadAndWrite() {

    }
}
