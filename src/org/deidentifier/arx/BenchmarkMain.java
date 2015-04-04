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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.deidentifier.arx.BenchmarkAnalysis.VARIABLES;
import org.deidentifier.arx.BenchmarkConfiguration.AnonConfiguration;
import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.metric.Metric;

import cern.colt.Arrays;
import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;
import de.linearbits.subframe.analyzer.buffered.BufferedArithmeticMeanAnalyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedStandardDeviationAnalyzer;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;

/**
 * Main benchmark class. Run with java -Xmx5G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class BenchmarkMain {

    /** The benchmark instance */
    protected static final Benchmark MAIN_BENCHMARK       = new Benchmark(BenchmarkSetup.getHeader());
    protected static final Benchmark SUPPORTING_BENCHMARK = new Benchmark(BenchmarkSetup.getHeader());

    /** Label for execution times */
    public static int                EXECUTION_TIME;
    /** Label for number of checks */
    public static int                NUMBER_OF_CHECKS;
    /** Label for number of roll-ups */
    public static int                NUMBER_OF_ROLLUPS;
    /** Label for number of roll-ups */
    public static int                NUMBER_OF_SNAPSHOTS;
    /** Label for size of lattice */
    public static int                LATTICE_SIZE;
    /** Label for information loss */
    public static int                INFORMATION_LOSS;
    /** Label for information loss transformation */
    public static int                INFORMATION_LOSS_TRANSFORMATION;
    /** Label for information loss minimum */
    public static int                INFORMATION_LOSS_MINIMUM;
    /** Label for information loss minimum transformation */
    public static int                INFORMATION_LOSS_MINIMUM_TRANSFORMATION;
    /** Label for information loss maximum */
    public static int                INFORMATION_LOSS_MAXIMUM;
    /** Label for information loss maximum transformation */
    public static int                INFORMATION_LOSS_MAXIMUM_TRANSFORMATION;
    /** Label for information loss maximum transformation */
    public static int                SOLUTION_DISCOVERY_TIME;

    private static void initMeasures() {

        EXECUTION_TIME = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.EXECUTION_TIME.val);
        NUMBER_OF_CHECKS = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.NUMBER_OF_CHECKS.val);
        NUMBER_OF_ROLLUPS = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.NUMBER_OF_ROLLUPS.val);
        NUMBER_OF_SNAPSHOTS = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.NUMBER_OF_SNAPSHOTS.val);
        LATTICE_SIZE = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.LATTICE_SIZE.val);
        INFORMATION_LOSS = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS.val);
        INFORMATION_LOSS_TRANSFORMATION = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_TRANSFORMATION.val);
        SOLUTION_DISCOVERY_TIME = MAIN_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.SOLUTION_DISCOVERY_TIME.val);

        // InformationLossBounds
        INFORMATION_LOSS_MINIMUM = SUPPORTING_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MINIMUM.val);
        INFORMATION_LOSS_MINIMUM_TRANSFORMATION = SUPPORTING_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MINIMUM_TRANSFORMATION.val);
        INFORMATION_LOSS_MAXIMUM = SUPPORTING_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MAXIMUM.val);
        INFORMATION_LOSS_MAXIMUM_TRANSFORMATION = SUPPORTING_BENCHMARK.addMeasure(BenchmarkAnalysis.VARIABLES.INFORMATION_LOSS_MAXIMUM_TRANSFORMATION.val);
    }

    private static void initAnalyzers(int repetitions) {

        MAIN_BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedArithmeticMeanAnalyzer(repetitions));
        MAIN_BENCHMARK.addAnalyzer(EXECUTION_TIME, new BufferedStandardDeviationAnalyzer(repetitions));
        MAIN_BENCHMARK.addAnalyzer(NUMBER_OF_CHECKS, new ValueBuffer());
        MAIN_BENCHMARK.addAnalyzer(NUMBER_OF_ROLLUPS, new ValueBuffer());
        MAIN_BENCHMARK.addAnalyzer(NUMBER_OF_SNAPSHOTS, new ValueBuffer());
        MAIN_BENCHMARK.addAnalyzer(LATTICE_SIZE, new ValueBuffer());
        MAIN_BENCHMARK.addAnalyzer(INFORMATION_LOSS, new ValueBuffer());
        MAIN_BENCHMARK.addAnalyzer(INFORMATION_LOSS_TRANSFORMATION, new ValueBuffer());
        MAIN_BENCHMARK.addAnalyzer(SOLUTION_DISCOVERY_TIME, new BufferedArithmeticMeanAnalyzer(repetitions));

        // InformationLossBounds
        SUPPORTING_BENCHMARK.addAnalyzer(INFORMATION_LOSS_MINIMUM, new ValueBuffer());
        SUPPORTING_BENCHMARK.addAnalyzer(INFORMATION_LOSS_MINIMUM_TRANSFORMATION, new ValueBuffer());
        SUPPORTING_BENCHMARK.addAnalyzer(INFORMATION_LOSS_MAXIMUM, new ValueBuffer());
        SUPPORTING_BENCHMARK.addAnalyzer(INFORMATION_LOSS_MAXIMUM_TRANSFORMATION, new ValueBuffer());
    }

    /**
     * Check whether the number of arguments is correct.
     * @param args
     */
    private static void checkArgsLength(String[] args) {
        if (args.length < 1 || args.length > 3) {
            printUsage();
            System.exit(0);
        }
    }

    /**
     * Print usage of the {@link BenchmarkMain#main(String[])}.
     */
    private static void printUsage() {
        System.out.println("Usage: java -Xmx5G -jar <jar> mode numRepetitions [configurationFile]");
        System.out.println("Usage of mode:");
        System.out.println("1 - execute algorithms/compute ILbounds/compute relative IL");
        System.out.println("2 - execute algorithms/compute ILbounds");
        System.out.println("3 - execute algorithms/compute relative IL");
        System.out.println("4 - compute ILbounds");
        System.out.println("5 - compute relative IL");
        System.out.println("6 - create configurationFile based on code");
    }

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) {

        checkArgsLength(args);

        int mode = -1;
        try {
            mode = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong format: mode needs to be an Integer.");
            printUsage();
            System.exit(0);
        }

        if (1 == mode || 2 == mode || 3 == mode || 4 == mode || 6 == mode) {
            executeAlgorithms(args, mode);
        }
        if (1 == mode || 3 == mode || 5 == mode) {
            computeRelativeInformationLoss();
        }
    }

    private static void computeRelativeInformationLoss() {
        try {
            writeInformationLossBoundsToResults();
        } catch (
                IOException
                | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes the algorithms based on the configuration provided by the run argument.
     * @param args
     * @param mode indicating which algorithms shall be executed
     */
    private static void executeAlgorithms(String[] args, int mode) {

        // CONFIGURATION LIST
        String configurationListFile = "";

        // configuration file was specified
        if (args.length == 3) {
            configurationListFile = args[2];
        }
        // create configuration file based on default settings
        else {
            configurationListFile = BenchmarkSetup.DEFAULT_CONFIGURAITON_FILE;
            BenchmarkSetup.createAndSaveDefaultBenchmarkConfiguration(configurationListFile);
        }

        // create configuration file only
        if (6 == mode) {
            System.exit(0);
        }

        // REPETITIONS
        int repetitions = 0;
        try {
            repetitions = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong format: numRepititions needs to be an Integer.");
            printUsage();
            System.exit(0);
        }

        BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();
        ArrayList<AnonConfiguration> anonConfigurations = null;
        try {
            anonConfigurations = benchmarkConfiguration.readBenchmarkConfiguration(configurationListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BenchmarkDriver mainDriver = new BenchmarkDriver(MAIN_BENCHMARK);
        BenchmarkDriver supportingDriver = new BenchmarkDriver(SUPPORTING_BENCHMARK);

        for (AnonConfiguration c : anonConfigurations) {
            if ((1 == mode || 2 == mode || 4 == mode) && AlgorithmType.INFORMATION_LOSS_BOUNDS == c.getAlgorithm().getType()) {
                runBenchmark(supportingDriver, repetitions, c, SUPPORTING_BENCHMARK);
            } else if (1 == mode || 2 == mode || 3 == mode) {
                runBenchmark(mainDriver, repetitions, c, MAIN_BENCHMARK);

            }
        }
    }

    private static void runBenchmark(BenchmarkDriver driver, int repetitions,
                                     AnonConfiguration c, Benchmark benchmark) {

        if (AlgorithmType.INFORMATION_LOSS_BOUNDS != c.getAlgorithm().getType()) {

            // Print status info
            System.out.println("Warm Up: " + c.getStatusLine());

            // Warmup run
            driver.anonymize(c, true);
        }

        // Print status info
        System.out.println("Running: " + c.getStatusLine());

        // init analyzers and measures for this run
        initMeasures();
        initAnalyzers(repetitions);

        // Benchmark
        benchmark.addRun(c.getAlgorithm().toString(),
                         c.getDataset().toString(),
                         Arrays.toString(c.getCriteria()),
                         c.getILMetric().getName(),
                         String.valueOf(c.getSuppression()),
                         c.getQICount(), c.getAlgorithm().getTerminationConfig().getValue());

        // Repeat
        for (int i = 0; i < repetitions; i++) {
            driver.anonymize(c, false);

            // do only one iteration for informationLossBounds
            if (AlgorithmType.INFORMATION_LOSS_BOUNDS == c.getAlgorithm().getType()) {
                break;
            }
        }

        // Write results incrementally
        String fileName = "";
        if (AlgorithmType.INFORMATION_LOSS_BOUNDS == c.getAlgorithm().getType()) {
            fileName = BenchmarkSetup.INFORMATION_LOSS_FILE;
        }
        else {
            fileName = BenchmarkSetup.RESULTS_FILE;
        }

        try {
            benchmark.getResults().write(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method reads the information loss bounds from informationLossBounds.csv and adds the data as new columns into results.csv.
     * @throws IOException
     * @throws ParseException
     */
    private static void writeInformationLossBoundsToResults() throws IOException, ParseException {
        CSVFile results = null;
        CSVFile bounds = null;
        try {
            results = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));
            bounds = new CSVFile(new File(BenchmarkSetup.INFORMATION_LOSS_FILE));
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(0);
        }

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

        Selector<String[]> selector;
        double min = 0;
        double max = 0;
        String minTransformation = "";
        String maxTransformation = "";

        Algorithm algorithm = new BenchmarkSetup.Algorithm(AlgorithmType.INFORMATION_LOSS_BOUNDS, null);

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

                        // for (int qiCount = BenchmarkSetup.getMinQICount(data); qiCount <= BenchmarkSetup.getMaxQICount(algorithm,
                        // data); qiCount++) {

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
                                         // .and()
                                         // .field(VARIABLES.QI_COUNT.val)
                                         // .equals(String.valueOf(qiCount))
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
                        for (Algorithm benchmarkAlgorithm : new Algorithm[] { new Algorithm(AlgorithmType.HEURAKLES, null) }) {

                            if (AlgorithmType.HEURAKLES == benchmarkAlgorithm.getType()) {
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
                                                  // .and()
                                                  // .field(VARIABLES.QI_COUNT.val)
                                                  // .equals(String.valueOf(qiCount))
                                                  // .and()
                                                  // .field(VARIABLES.TERMINATION_LIMIT.val)
                                                  // .equals(String.valueOf(benchmarkAlgorithm.getTerminationConfig().getValue()))
                                                  .build();
                            } else {
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
                                                  // .and()
                                                  // .field(VARIABLES.QI_COUNT.val)
                                                  // .equals(String.valueOf(qiCount))
                                                  .build();
                            }

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

                            // }
                        }
                    }
                }
            }
        }

        results.write(new File(BenchmarkSetup.RESULTS_FILE));
    }

}
