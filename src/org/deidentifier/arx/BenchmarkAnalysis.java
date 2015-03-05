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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.metric.Metric;

import de.linearbits.objectselector.Selector;
import de.linearbits.objectselector.SelectorBuilder;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedGeometricMeanAnalyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Function;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.Plot;
import de.linearbits.subframe.graph.PlotHistogramClustered;
import de.linearbits.subframe.graph.PlotLinesClustered;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Point3D;
import de.linearbits.subframe.graph.Series2D;
import de.linearbits.subframe.graph.Series3D;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;
import de.linearbits.subframe.render.LaTeX;
import de.linearbits.subframe.render.PlotGroup;

public class BenchmarkAnalysis {

    /**
     * Enum for all variables used in the analysis.
     */
    protected static enum VARIABLES {
        EXECUTION_TIME("Execution time"),
        NUMBER_OF_CHECKS("Number of checks"),
        NUMBER_OF_ROLLUPS("Number of rollups"),
        NUMBER_OF_SNAPSHOTS("Number of snapshots"),
        INFORMATION_LOSS("Information loss"),
        LATTICE_SIZE("Size of lattice"),
        INFORMATION_LOSS_MINIMUM("Information loss minimum"),
        INFORMATION_LOSS_MINIMUM_TRANSFORMATION("Information loss minimum (Transformation)"),
        INFORMATION_LOSS_MAXIMUM("Information loss maximum"),
        INFORMATION_LOSS_MAXIMUM_TRANSFORMATION("Information loss maximum (Transformation)"),
        INFORMATION_LOSS_PERCENTAGE("Relative information loss"),
        INFORMATION_LOSS_TRANSFORMATION("Information loss (Transformation)"),
        QI_COUNT("QI count"),
        ALGORITHM("Algorithm"),
        DATASET("Dataset"),
        CRITERIA("Criteria"),
        METRIC("Metric"),
        SUPPRESSION("Suppression");

        protected final String                val;
        private static Map<String, VARIABLES> value2Enum = new HashMap<String, VARIABLES>();

        static {
            for (VARIABLES value : VARIABLES.values()) {
                value2Enum.put(value.val, value);
            }
        }

        private VARIABLES(String val) {
            this.val = val;
        }

        public static VARIABLES getEnum(String val) {
            return value2Enum.get(val);
        }
    }

    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {

        generateTables();
        generatePlots();
        generateQICountScalingPlots();
    }

    /**
     * Generate the plots
     * @throws IOException
     * @throws ParseException
     */
    private static void generatePlots() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));

        // for each metric
        for (Metric<?> metric : BenchmarkSetup.getMetrics()) {
            List<PlotGroup> groups = new ArrayList<PlotGroup>();

            // For each combination of criteria
            for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
                String scriteria = Arrays.toString(criteria);

                // for each suppression
                for (double suppr : BenchmarkSetup.getSuppression()) {
                    String suppression = String.valueOf(suppr);
                    BenchmarkDataset[] datasets = BenchmarkSetup.getConventionalDatasets();
                    List<Algorithm> algorithms = BenchmarkSetup.getBenchmarkAlgorithms();

                    if (datasets.length > 0) {
                        groups.add(getGroup(file,
                                            Arrays.asList(new VARIABLES[] { VARIABLES.EXECUTION_TIME }),
                                            Arrays.asList(new String[] { Analyzer.ARITHMETIC_MEAN }),
                                            VARIABLES.DATASET.val,
                                            scriteria,
                                            suppression,
                                            datasets,
                                            algorithms,
                                            metric));
                        groups.add(getGroup(file,
                                            Arrays.asList(new VARIABLES[] { VARIABLES.NUMBER_OF_CHECKS }),
                                            Arrays.asList(new String[] { Analyzer.VALUE }),
                                            VARIABLES.DATASET.val,
                                            scriteria,
                                            suppression,
                                            datasets,
                                            algorithms,
                                            metric));
                        groups.add(getGroup(file,
                                            Arrays.asList(new VARIABLES[] { VARIABLES.NUMBER_OF_ROLLUPS }),
                                            Arrays.asList(new String[] { Analyzer.VALUE }),
                                            VARIABLES.DATASET.val,
                                            scriteria,
                                            suppression,
                                            datasets,
                                            algorithms,
                                            metric));
                        groups.add(getGroup(file,
                                            Arrays.asList(new VARIABLES[] { VARIABLES.NUMBER_OF_SNAPSHOTS }),
                                            Arrays.asList(new String[] { Analyzer.VALUE }),
                                            VARIABLES.DATASET.val,
                                            scriteria,
                                            suppression,
                                            datasets,
                                            algorithms,
                                            metric));
                        groups.add(getGroup(file,
                                            Arrays.asList(new VARIABLES[] { VARIABLES.INFORMATION_LOSS }),
                                            Arrays.asList(new String[] { Analyzer.VALUE }),
                                            VARIABLES.DATASET.val,
                                            scriteria,
                                            suppression,
                                            datasets,
                                            algorithms,
                                            metric));
                        groups.add(getGroup(file,
                                            Arrays.asList(new VARIABLES[] { VARIABLES.INFORMATION_LOSS_PERCENTAGE }),
                                            Arrays.asList(new String[] { Analyzer.VALUE }),
                                            VARIABLES.DATASET.val,
                                            scriteria,
                                            suppression,
                                            datasets,
                                            algorithms,
                                            metric));
                    }
                }
            }

            if (!groups.isEmpty()) {
                LaTeX.plot(groups, "results/results_" + metric.getName().toLowerCase().replaceAll(" ", "_"));
            }
        }
    }

    /**
     * Generate the plots for the QI count scaling benchmark
     * @throws IOException
     * @throws ParseException
     */
    private static void generateQICountScalingPlots() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));

        Algorithm flash = null;
        for (Algorithm algorithm : BenchmarkSetup.getBenchmarkAlgorithms()) {
            if (algorithm.getType() == AlgorithmType.FLASH) {
                flash = algorithm;
            }
        }
        if (flash == null) {
            throw new RuntimeException("Flash algorithm not found");
        }

        for (BenchmarkDataset dataset : BenchmarkSetup.getQICountScalingDatasets()) {
            List<PlotGroup> groups = new ArrayList<PlotGroup>();

            // for each metric
            for (Metric<?> metric : BenchmarkSetup.getMetrics()) {

                // For each combination of criteria
                for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
                    String scriteria = Arrays.toString(criteria);

                    // for each suppression
                    for (double suppr : BenchmarkSetup.getSuppression()) {
                        String suppression = String.valueOf(suppr);

                        // For each algorithm of type heurakles
                        for (Algorithm heurakles : BenchmarkSetup.getBenchmarkAlgorithms()) {
                            if (heurakles.getType() != AlgorithmType.HEURAKLES) continue;

                            groups.add(getGroup(file,
                                                Arrays.asList(new VARIABLES[] {
                                                        VARIABLES.EXECUTION_TIME,
                                                        VARIABLES.INFORMATION_LOSS_PERCENTAGE }),
                                                Arrays.asList(new String[] { Analyzer.ARITHMETIC_MEAN, Analyzer.VALUE }),
                                                VARIABLES.QI_COUNT.val,
                                                scriteria,
                                                suppression,
                                                new BenchmarkDataset[] { dataset },
                                                Arrays.asList(new Algorithm[] { flash, heurakles }),
                                                metric));
                        }
                    }
                }
            }

            if (!groups.isEmpty()) {
                LaTeX.plot(groups, "results/results_QI_count_scaling_" + dataset.toString());
            }
        }
    }

    /**
     * Generates a single table
     * @param file
     * @param suppression
     * @param metric
     * @param variable
     * @param measure
     * @param lowerIsBetter
     * @throws ParseException
     * @throws IOException
     */
    private static void
            generateTable(CSVFile file, String suppression, Metric<?> metric, VARIABLES variable, String measure, boolean lowerIsBetter) throws ParseException,
                                                                                                                                        IOException {

        // Create csv header
        ArrayList<String> header = new ArrayList<String>();
        header.add("");
        for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {
            int minQI = BenchmarkSetup.getMinQICount(dataset);
            int maxQI = minQI;
            for (Algorithm algorithm : BenchmarkSetup.getBenchmarkAlgorithms()) {
                maxQI = Math.max(BenchmarkSetup.getMaxQICount(algorithm, dataset), maxQI);
            }

            for (int numQI = minQI; numQI <= maxQI; ++numQI) {
                header.add(dataset.toString() + " " + numQI + " QIs");
            }
        }

        String[] header2 = new String[header.size()];
        header.toArray(header2);
        String[] header1 = new String[header.size()];
        Arrays.fill(header1, "");

        // Create csv
        CSVFile csv = new CSVFile(header1, header2);

        // For each criterion
        for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {

            // For each algorithm of type heurakles
            for (Algorithm heurakles : BenchmarkSetup.getBenchmarkAlgorithms()) {
                if (heurakles.getType() != AlgorithmType.HEURAKLES) continue;

                // The current line
                String scriteria = Arrays.toString(criteria);
                ArrayList<String> line = new ArrayList<String>();
                line.add(scriteria);

                // For each dataset and QI count combination
                for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {
                    int minQI = BenchmarkSetup.getMinQICount(dataset);
                    int maxQI = minQI;
                    for (Algorithm algorithm : BenchmarkSetup.getBenchmarkAlgorithms()) {
                        maxQI = Math.max(BenchmarkSetup.getMaxQICount(algorithm, dataset), maxQI);
                    }

                    for (int numQI = minQI; numQI <= maxQI; ++numQI) {

                        // Init
                        String firstAlgorithm = null;
                        String secondAlgorithm = null;
                        double firstValue = Double.MAX_VALUE;
                        double secondValue = Double.MAX_VALUE;
                        if (!lowerIsBetter) {
                            firstValue = Double.MIN_VALUE;
                            secondValue = Double.MIN_VALUE;
                        }

                        // Select data for the given data point
                        Selector<String[]> selector = file.getSelectorBuilder()
                                                          .field(VARIABLES.DATASET.val)
                                                          .equals(dataset.toString())
                                                          .and()
                                                          .field(VARIABLES.QI_COUNT.val)
                                                          .equals(String.valueOf(numQI))
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
                                                          .begin()
                                                          .field(VARIABLES.ALGORITHM.val)
                                                          .equals(heurakles.toString())
                                                          .or()
                                                          .field(VARIABLES.ALGORITHM.val)
                                                          .equals(AlgorithmType.FLASH.toString())
                                                          .end()
                                                          .build();

                        // Create series
                        Series2D series = new Series2D(file, selector,
                                                       new Field(VARIABLES.ALGORITHM.val),
                                                       new Field(variable.val, measure));

                        boolean noSolutionFound = false;

                        // Select from series
                        for (Point2D point : series.getData()) {

                            // Read
                            double value = Double.valueOf(point.y);
                            if (variable == VARIABLES.INFORMATION_LOSS && value == (double) BenchmarkDriver.NO_SOLUTION_FOUND) noSolutionFound = true;
                            String algorithm = point.x;

                            // Check
                            if ((lowerIsBetter && value < firstValue) ||
                                (!lowerIsBetter && value > firstValue)) {

                                secondValue = firstValue;
                                secondAlgorithm = firstAlgorithm;

                                firstValue = value;
                                firstAlgorithm = algorithm;

                            } else if ((lowerIsBetter && value < secondValue) ||
                                       (!lowerIsBetter && value > secondValue)) {

                                secondValue = value;
                                secondAlgorithm = algorithm;
                            }
                        }

                        // Compute difference
                        double difference = 0;
                        if (lowerIsBetter) difference = (1d - (firstValue / secondValue)) * 100d;
                        else difference = (1d - (secondValue / firstValue)) * 100d;

                        // Render and store
                        final NumberFormat df = new DecimalFormat("#");
                        line.add(noSolutionFound ? "No solution found" : firstAlgorithm + " (" + df.format(difference) + "%) " +
                                                                         secondAlgorithm);
                    }
                }

                // Add line
                String[] lineArr = new String[line.size()];
                line.toArray(lineArr);
                csv.addLine(lineArr);
            }

        }

        // Write to file
        csv.write(new File("results/table_" + variable.val.toLowerCase().replaceAll(" ", "_") + "_" +
                           metric.getName().toLowerCase().replaceAll(" ", "_") + "_" +
                           suppression.toLowerCase().replaceAll(" ", "_") + ".csv"));
    }

    /**
     * Generate the tables
     * @throws IOException
     * @throws ParseException
     */
    private static void generateTables() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));

        // for each metric
        for (Metric<?> metric : BenchmarkSetup.getMetrics()) {

            // for each suppression
            for (double suppr : BenchmarkSetup.getSuppression()) {
                String suppression = String.valueOf(suppr);

                generateTable(file, suppression, metric, VARIABLES.EXECUTION_TIME, Analyzer.ARITHMETIC_MEAN, true);
                generateTable(file, suppression, metric, VARIABLES.NUMBER_OF_CHECKS, Analyzer.VALUE, true);
                generateTable(file, suppression, metric, VARIABLES.NUMBER_OF_ROLLUPS, Analyzer.VALUE, false);
                generateTable(file, suppression, metric, VARIABLES.NUMBER_OF_SNAPSHOTS, Analyzer.VALUE, false);
                generateTable(file, suppression, metric, VARIABLES.INFORMATION_LOSS, Analyzer.VALUE, true);
            }
        }
    }

    /**
     * Returns a plot group
     * @param file
     * @param variable
     * @param measure
     * @param focus
     * @param scriteria
     * @param suppression TODO
     * @param metric
     * @return
     * @throws ParseException
     */
    private static PlotGroup getGroup(CSVFile file,
                                      List<VARIABLES> variables,
                                      List<String> measures,
                                      String focus,
                                      String scriteria,
                                      String suppression,
                                      BenchmarkDataset[] datasets,
                                      List<Algorithm> algorithms,
                                      Metric<?> metric) throws ParseException {

        // Prepare
        List<Plot<?>> plots = new ArrayList<Plot<?>>();
        Series3D series = null;

        // Collect data for all algorithms and the first variable
        VARIABLES variable = variables.get(0);
        String measure = measures.get(0);
        for (Algorithm algorithm : algorithms) {

            Series3D _series = getSeries(file,
                                         algorithm.toString(),
                                         variable.val,
                                         measure,
                                         focus,
                                         scriteria,
                                         suppression,
                                         datasets,
                                         metric);

            // Transform execution times from nanos to seconds
            if (VARIABLES.EXECUTION_TIME == variable) {
                _series.transform(new Function<Point3D>() {
                    @Override
                    public Point3D apply(Point3D t) {
                        return new Point3D(t.x, t.y, String.valueOf(Double.valueOf(t.z) / 1000000000d));
                    }
                });
            }

            // Assure that y coordinates are unique for each algorithm and variable combination
            final String algo = algorithm.toString();
            final String var = variable.val;
            if (variables.size() > 1) {
                _series.transform(new Function<Point3D>() {
                    @Override
                    public Point3D apply(Point3D t) {
                        return new Point3D(t.x, algo + " " + var, t.z);
                    }
                });
            }

            if (series == null) series = _series;
            else series.append(_series);

        }

        // Define params based on the first variable
        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = 0;
        params.printValues = true;
        params.size = 1.5;
        params.logY = false;
        params.enhance = false;

        double max = 0d;
        double padding = 0d;
        if (VARIABLES.INFORMATION_LOSS_PERCENTAGE != variables.get(0)) {
            max = getMax(series);
            padding = max * 0.3d;
        } else {
            max = 100d;
        }

        params.minY = 0d;
        params.printValuesFormatString = "%.0f";

        if (VARIABLES.EXECUTION_TIME == variables.get(0) || max < 10d) {
            params.printValuesFormatString = "%.2f";
        }

        if (max >= 10000d) {
            padding = max * 0.7d;
            params.printValuesFormatString = "%.2e";
        }

        params.maxY = max + padding;

        if (focus.equals("Criteria")) {
            params.keypos = KeyPos.AT(5, params.maxY * 1.1d, "horiz bot center");
        } else if (focus.equals(VARIABLES.QI_COUNT.val)) {
            params.keypos = KeyPos.AT(10, params.maxY * 1.1d, "horiz bot center");
        } else {
            params.keypos = KeyPos.AT(0.5, params.maxY * 1.1d, "horiz bot center");
        }

        params.ratio = 0.2d;

        // Collect data for all algorithms and the second variable, if any
        if (variables.size() > 1) {

            variable = variables.get(1);
            measure = measures.get(1);

            if (VARIABLES.INFORMATION_LOSS_PERCENTAGE != variable) {
                throw new RuntimeException("Currently only " + VARIABLES.INFORMATION_LOSS_PERCENTAGE.val +
                                           " is supported as second variable");
            }

            for (Algorithm algorithm : algorithms) {

                Series3D _series = getSeries(file,
                                             algorithm.toString(),
                                             variable.val,
                                             measure,
                                             focus,
                                             scriteria,
                                             suppression,
                                             datasets,
                                             metric);

                // Assure that y coordinates are unique for each algorithm and variable combination
                // and that the relative information loss is scaled so that 100% equals max
                final String algo = algorithm.toString();
                final String var = variable.val;
                final double maxConst = max;
                _series.transform(new Function<Point3D>() {
                    @Override
                    public Point3D apply(Point3D t) {
                        return new Point3D(t.x, algo + " " + var, String.valueOf(Double.valueOf(t.z) * maxConst / 100d));
                    }
                });

                if (series == null) series = _series;
                else series.append(_series);

            }

            params.y2tics = "('0%%' 0, '25%%' " + max / 4d + ", '50%%' " + max / 2d + ", '75%%' " + max * 3d / 4d + ",'100%%' " + max + ")";
        }

        // Make sure labels are printed correctly
        if (!focus.equals(VARIABLES.QI_COUNT.val)) {
            series.transform(new Function<Point3D>() {
                @Override
                public Point3D apply(Point3D t) {
                    return new Point3D("\"" + t.x + "\"", t.y, t.z);
                }
            });
        }

        // Create plot
        Labels labels;
        if (variables.size() > 1) {
            labels = new Labels(focus, variables.get(0).val, variables.get(1).val, "");
        } else {
            labels = new Labels(focus, variables.get(0).val);
        }
        if (focus.equals(VARIABLES.QI_COUNT.val)) {
            plots.add(new PlotLinesClustered("", labels, series));
        } else {
            plots.add(new PlotHistogramClustered("", labels, series));
        }

        // Return
        String caption = variables.get(0).val;
        if (variables.size() > 1) {
            caption += " and " + variables.get(1).val;
        }
        caption += " for criteria " + scriteria + " using information loss metric \"" + metric.getName() + "\" with " +
                   suppression + "\\%" + " suppression " + " listed by \"" + focus + "\"";
        if (focus.equals(VARIABLES.QI_COUNT.val) && datasets.length == 1) {
            caption += " for dataset \"" + datasets[0].toString().replace("_", "\\_") + "\"";
        }

        return new PlotGroup(caption, plots, params, 1.0d);
    }

    /**
     * Returns a maximum for the given series
     * @param series
     * @return
     */
    private static double getMax(Series3D series) {

        double max = -Double.MAX_VALUE;
        for (Point3D point : series.getData()) {
            max = Math.max(Double.valueOf(point.z), max);
        }

        return max;
    }

    /**
     * Returns a series<br>
     * currently only analyzers of type Double are supported
     * 
     * @param file
     * @param algorithm
     * @param variable
     * @param measure
     * @param focus
     * @param scriteria
     * @param suppression
     * @param metric
     * @return
     * @throws ParseException
     */
    private static Series3D
            getSeries(CSVFile file,
                      String algorithm,
                      String variable,
                      String measure,
                      String focus,
                      String scriteria,
                      String suppression,
                      BenchmarkDataset[] datasets,
                      Metric<?> metric) throws ParseException {

        // Select data for the given parameters

        SelectorBuilder<String[]> selectorBuilder = file.getSelectorBuilder().field(VARIABLES.ALGORITHM.val).equals(algorithm).and()
                                                        .field(VARIABLES.SUPPRESSION.val).equals(suppression).and()
                                                        .field(VARIABLES.METRIC.val).equals(metric.getName()).and()
                                                        .field(VARIABLES.CRITERIA.val).equals(scriteria).and()
                                                        .begin();

        for (int i = 0; i < datasets.length; ++i) {
            selectorBuilder.field(VARIABLES.DATASET.val).equals(datasets[i].toString());
            if (i < datasets.length - 1) {
                selectorBuilder.or();
            }
        }

        Selector<String[]> selector = selectorBuilder.end().build();

        // Create series.
        // Note that actually no aggregation using the BufferedGeometricMeanAnalyzer is performed
        // because by definition of selector each combination of x and y coordinates is unique
        // and thus only one z coordinate is being encountered.
        Series3D series = new Series3D(file, selector,
                                       new Field(focus),
                                       new Field(VARIABLES.ALGORITHM.val),
                                       new Field(variable, measure),
                                       new BufferedGeometricMeanAnalyzer());

        return series;
    }
}
