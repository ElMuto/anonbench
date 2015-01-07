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

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.metric.Metric;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedGeometricMeanAnalyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Function;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.Plot;
import de.linearbits.subframe.graph.PlotHistogramClustered;
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
        INFORMATION_LOSS_PERCENTAGE("Relative information loss");

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
    }

    /**
     * Generate the plots
     * @throws IOException
     * @throws ParseException
     */
    private static void generatePlots() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File("results/results.csv"));

        // for each metric
        for (Metric<?> metric : BenchmarkSetup.getMetrics()) {
            List<PlotGroup> groups = new ArrayList<PlotGroup>();

            // For each combination of criteria
            for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
                String scriteria = Arrays.toString(criteria);

                // for each suppression
                for (double suppr : BenchmarkSetup.getSuppression()) {
                    String suppression = String.valueOf(suppr);

                    groups.add(getGroup(file,
                                        VARIABLES.EXECUTION_TIME,
                                        Analyzer.ARITHMETIC_MEAN,
                                        "Dataset",
                                        scriteria,
                                        suppression,
                                        metric));
                    groups.add(getGroup(file, VARIABLES.NUMBER_OF_CHECKS, Analyzer.VALUE, "Dataset", scriteria, suppression, metric));
                    groups.add(getGroup(file, VARIABLES.NUMBER_OF_ROLLUPS, Analyzer.VALUE, "Dataset", scriteria, suppression, metric));
                    groups.add(getGroup(file, VARIABLES.NUMBER_OF_SNAPSHOTS, Analyzer.VALUE, "Dataset", scriteria, suppression, metric));
                    groups.add(getGroup(file, VARIABLES.INFORMATION_LOSS, Analyzer.VALUE, "Dataset", scriteria, suppression, metric));
                }
            }
            LaTeX.plot(groups, "results/results_" + metric.getName().toLowerCase().replaceAll(" ", "_"));
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
        String[] header1 = new String[BenchmarkSetup.getDatasets().length + 1];
        Arrays.fill(header1, "");
        String[] header2 = new String[header1.length];
        header2[0] = "";
        for (int i = 1; i < header2.length; i++) {
            header2[i] = BenchmarkSetup.getDatasets()[i - 1].toString();
        }

        // Create csv
        CSVFile csv = new CSVFile(header1, header2);

        // For each criterion
        for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {

            // The current line
            String scriteria = Arrays.toString(criteria);
            String[] line = new String[header1.length];
            line[0] = scriteria;

            // For each dataset
            for (int i = 1; i < header1.length; i++) {

                // Init
                String dataset = BenchmarkSetup.getDatasets()[i - 1].toString();
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
                                                  .field("Criteria").equals(scriteria).and()
                                                  .field("Dataset").equals(dataset).and()
                                                  .field("Suppression").equals(suppression).and()
                                                  .field("Metric").equals(metric.getName())
                                                  .build();

                // Create series
                Series2D series = new Series2D(file, selector,
                                               new Field("Algorithm"),
                                               new Field(variable.val, measure));

                // Select from series
                for (Point2D point : series.getData()) {

                    // Read
                    double value = Double.valueOf(point.y);
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
                line[i] = firstAlgorithm + " (" + df.format(difference) + "%) " + secondAlgorithm;
            }

            // Add line
            csv.addLine(line);
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

        CSVFile file = new CSVFile(new File("results/results.csv"));

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
                                      VARIABLES variable,
                                      String measure,
                                      String focus,
                                      String scriteria,
                                      String suppression,
                                      Metric<?> metric) throws ParseException {

        // Prepare
        List<Plot<?>> plots = new ArrayList<Plot<?>>();
        Series3D series = null;

        // Collect data for all algorithms
        for (BenchmarkAlgorithm algorithm : BenchmarkSetup.getAlgorithms()) {

            Series3D _series = getSeries(file, algorithm.toString(), variable.val, measure, focus, scriteria, suppression, metric);
            if (series == null) series = _series;
            else series.append(_series);
        }

        // Make sure labels are printed correctly
        series.transform(new Function<Point3D>() {
            @Override
            public Point3D apply(Point3D t) {
                return new Point3D("\"" + t.x + "\"", t.y, t.z);
            }
        });

        // Transform execution times from nanos to seconds
        if (VARIABLES.EXECUTION_TIME == variable) {
            series.transform(new Function<Point3D>() {
                @Override
                public Point3D apply(Point3D t) {
                    return new Point3D(t.x, t.y, String.valueOf(Double.valueOf(t.z) / 1000000000d));
                }
            });
        }

        // Create plot
        plots.add(new PlotHistogramClustered("",
                                             new Labels(focus, variable.val),
                                             series));

        // Define params
        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = 0;
        params.printValues = true;
        params.size = 1.5;
        params.logY = false;

        double max = getMax(series);
        double padding = max * 0.3d;

        params.minY = 0d;
        params.printValuesFormatString = "%.0f";

        if (VARIABLES.EXECUTION_TIME == variable || max < 10d) {
            params.printValuesFormatString = "%.2f";
        }

        if (max >= 10000d) {
            padding = max * 0.7d;
            params.printValuesFormatString = "%.2e";
        }

        params.maxY = max + padding;

        if (focus.equals("Criteria")) {
            params.keypos = KeyPos.AT(5, params.maxY * 1.1d, "horiz bot center");
        } else {
            params.keypos = KeyPos.AT(2, params.maxY * 1.1d, "horiz bot center");
        }

        params.ratio = 0.2d;

        // Return
        String caption = variable.val + " for criteria " + scriteria + " using information loss metric \"" + metric.getName() + "\" with " +
                         suppression + "\\%" + " suppression " + " listed by \"" + focus + "\"";
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
                      Metric<?> metric) throws ParseException {

        // Select data for the given algorithm
        Selector<String[]> selector = file.getSelectorBuilder().field("Algorithm").equals(algorithm).and()
                                          .field("Suppression").equals(suppression).and()
                                          .field("Metric").equals(metric.getName()).and()
                                          .field("Criteria").equals(scriteria)
                                          .build();

        // Create series.
        // Note that actually no aggregation using the BufferedGeometricMeanAnalyzer is performed
        // because by definition of selector each combination of x and y coordinates is unique
        // and thus only one z coordinate is being encountered.
        Series3D series = new Series3D(file, selector,
                                       new Field(focus),
                                       new Field("Algorithm"),
                                       new Field(variable, measure),
                                       new BufferedGeometricMeanAnalyzer());

        return series;
    }
}
