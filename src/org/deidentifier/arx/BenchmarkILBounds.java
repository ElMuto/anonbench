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

    public static void main(String[] args) throws IOException, ParseException {

        BenchmarkDriver driver = new BenchmarkDriver(BENCHMARK);
        Algorithm algorithm = BenchmarkSetup.getAlgorithmByType(AlgorithmType.INFORMATION_LOSS_BOUNDS);
        String algoName = algorithm.getType().toString();

        // For each dataset
        for (BenchmarkDataset data : BenchmarkSetup.getDatasets()) {

            // For each metric
            for (Metric<?> metric : BenchmarkSetup.getMetrics()) {

                // For each suppression factor
                for (double suppression : BenchmarkSetup.getSuppression()) {

                    // For each combination of criteria
                    for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {

                        // Print status info
                        System.out.println("Running: " + algoName + " / " + data.toString() +
                                           " / " +
                                           metric.getName() +
                                           " / " + suppression + " / " +
                                           Arrays.toString(criteria));

                        // Benchmark
                        BENCHMARK.addRun(algoName,
                                         data.toString(),
                                         Arrays.toString(criteria),
                                         metric.getName(),
                                         String.valueOf(suppression));

                        driver.anonymize(data,
                                         criteria,
                                         algorithm,
                                         metric,
                                         suppression,
                                         BenchmarkSetup.getQuasiIdentifyingAttributes(data).length,
                                         false,
                                         false);

                        // Write results incrementally
                        BENCHMARK.getResults().write(new File("results/informationLossBounds.csv"));
                    }
                }
            }
        }

        writeInformationLossBoundsToResults();

    }

    /**
     * This method reads the information loss bounds from informationLossBounds.csv and adds the data as new columns into results.csv.
     * @throws IOException
     * @throws ParseException
     */
    private static void writeInformationLossBoundsToResults() throws IOException, ParseException {
        CSVFile results = new CSVFile(new File("results/results.csv"));

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

        CSVFile bounds = new CSVFile(new File("results/informationLossBounds.csv"));
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

                        // Select data point acc to the variables
                        selector = bounds.getSelectorBuilder().field("Suppression").equals(suppression).and()
                                         .field("Metric").equals(metric.getName()).and()
                                         .field("Criteria").equals(scriteria).and().field("Dataset").equals(data.toString())
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

                        for (Algorithm algorithm : BenchmarkSetup.getAlgorithms()) {

                            // Select data point acc to the variables
                            selector = results.getSelectorBuilder()
                                              .field("Algorithm")
                                              .equals(algorithm.getType().toString())
                                              .and()
                                              .field("Suppression")
                                              .equals(suppression)
                                              .and()
                                              .field("Metric")
                                              .equals(metric.getName())
                                              .and()
                                              .field("Criteria")
                                              .equals(scriteria).and().field("Dataset").equals(data.toString())
                                              .build();

                            iter = results.iterator();
                            while (iter.hasNext()) {
                                CSVLine csvLine = iter.next();
                                String[] line = csvLine.getData();
                                if (selector.isSelected(line)) {
                                    // get information loss value
                                    double value = Double.parseDouble(csvLine.get(VARIABLES.INFORMATION_LOSS.val, "Value"));

                                    // compute relative percentage
                                    value = ((value - min) / (max - min)) * 100.0;
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

        results.write(new File("results/results.csv"));
    }
}
