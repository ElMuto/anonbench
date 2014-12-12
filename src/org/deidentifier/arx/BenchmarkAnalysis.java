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
import java.util.List;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.metric.Metric;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
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

    /** The variables */
    // TODO refactor to use of enum
    private static final String[] VARIABLES = {
                                            "Execution time",
                                            "Number of checks",
                                            "Number of rollups",
                                            "Number of snapshots",
                                            "Information loss" };

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

        List<PlotGroup> groups = new ArrayList<PlotGroup>();

        // for each metric
        for (Metric<?> metric : BenchmarkSetup.getMetrics())
        {
            // for each suppression
            for (double suppr : BenchmarkSetup.getSuppression()) {
                String suppression = String.valueOf(suppr);

                // For each variable

                // execution time
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.EXECUTION_TIME)) {
                    groups.add(getGroup(file, VARIABLES[0], "Dataset", suppression, metric, analyzer));
                }
                // number of checks
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.NUMBER_OF_CHECKS)) {
                    groups.add(getGroup(file, VARIABLES[1], "Dataset", suppression, metric, analyzer));
                }
                // number of rollups
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.NUMBER_OF_ROLLUPS)) {
                    groups.add(getGroup(file, VARIABLES[2], "Dataset", suppression, metric, analyzer));
                }
                // number of snapshots
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.NUMBER_OF_SNAPSHOTS)) {
                    groups.add(getGroup(file, VARIABLES[3], "Dataset", suppression, metric, analyzer));
                }
                // information loss
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.INFORMATION_LOSS)) {
                    groups.add(getGroup(file, VARIABLES[4], "Dataset", suppression, metric, analyzer));
                }

            }
        }
        LaTeX.plot(groups, "results/results");
    }

    /**
     * Generates a single table
     * @param file
     * @param variable
     * @param lowerIsBetter
     * @param suppression
     * @param metric
     * @param analzyer
     * @throws ParseException
     * @throws IOException
     */
    private static void
            generateTable(CSVFile file, String variable, boolean lowerIsBetter, String suppression, Metric<?> metric, Analyzer<?> analyzer) throws ParseException,
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
                                                  .field("Criteria")
                                                  .equals(scriteria)
                                                  .and()
                                                  .field("Dataset")
                                                  .equals(dataset)
                                                  .and()
                                                  .field("Suppression")
                                                  .equals(suppression)
                                                  .and()
                                                  .field("Metric")
                                                  .equals(metric.getName())
                                                  .build();

                // Create series
                Series2D series = new Series2D(file, selector,
                                               new Field("Algorithm"),
                                               new Field(variable, analyzer.getLabel()));

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
        csv.write(new File("results/table_" + variable.toLowerCase().replaceAll(" ", "_") +
                           suppression.toLowerCase().replaceAll(" ", "_") + "_" +
                           metric.getName().toLowerCase().replaceAll(" ", "_") + "_" +
                           analyzer.getLabel().toLowerCase().replaceAll(" ", "_") + ".csv"));
    }

    /**
     * Generate the tables
     * @throws IOException
     * @throws ParseException
     */
    private static void generateTables() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File("results/results.csv"));

        // for each metric
        for (Metric<?> metric : BenchmarkSetup.getMetrics())
        {
            // for each suppression
            for (double suppr : BenchmarkSetup.getSuppression()) {
                String suppression = String.valueOf(suppr);

                // For each variable

                // execution time
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.EXECUTION_TIME)) {
                    generateTable(file, VARIABLES[0], true, suppression, metric, analyzer);
                }
                // number of checks
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.NUMBER_OF_CHECKS)) {
                    generateTable(file, VARIABLES[1], true, suppression, metric, analyzer);
                }
                // number of rollups
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.NUMBER_OF_ROLLUPS)) {
                    generateTable(file, VARIABLES[2], false, suppression, metric, analyzer);
                }
                // number of snapshots
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.NUMBER_OF_SNAPSHOTS)) {
                    generateTable(file, VARIABLES[3], false, suppression, metric, analyzer);
                }
                // information loss
                for (Analyzer<?> analyzer : BenchmarkMain.BENCHMARK.getAnalyzers().get(BenchmarkMain.INFORMATION_LOSS)) {
                    generateTable(file, VARIABLES[4], true, suppression, metric, analyzer);
                }
            }
        }
    }

    /**
     * Returns a plot group
     * @param file
     * @param focus
     * @param suppression TODO
     * @param metric TODO
     * @param analyzer TODO
     * @return
     * @throws ParseException
     */
    private static PlotGroup getGroup(CSVFile file,
                                      String variable,
                                      String focus,
                                      String suppression,
                                      Metric<?> metric,
                                      Analyzer<?> analyzer) throws ParseException {

        // Prepare
        List<Plot<?>> plots = new ArrayList<Plot<?>>();
        Series3D series = null;

        // Collect data for all algorithms
        for (BenchmarkAlgorithm algorithm : BenchmarkSetup.getAlgorithms()) {

            Series3D _series = getSeries(file, algorithm.toString(), variable, focus, analyzer, suppression, metric);
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
        if (variable.equals("Execution time")) {
            series.transform(new Function<Point3D>() {
                @Override
                public Point3D apply(Point3D t) {
                    return new Point3D(t.x, t.y, String.valueOf(Double.valueOf(t.z) / 1000000000d));
                }
            });
        }

        // Create plot
        // TODO adjust y-axis label according to used analyzer
        plots.add(new PlotHistogramClustered("",
                                             new Labels(focus, "Geom. mean of " + variable.toLowerCase()),
                                             series));
        // TODO add heading indicating the combination of used params

        // Define params
        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = 0;
        params.printValues = true;
        params.size = 1.5;
        if (variable.equals("Execution time")) {
            params.minY = 0.001d;
            params.maxY = getMax(series, 2);
            params.printValuesFormatString = "%.2f";
        } else if (variable.equals("Number of rollups")) {
            params.minY = 1d;
            if (focus.equals("Criteria")) {
                params.maxY = getMax(series, 0);
            } else {
                params.maxY = getMax(series, 1);
            }
            params.printValuesFormatString = "%.0f";
        }
        else if (variable.equals("Number of snapshots")) {
            params.minY = 1d;
            params.maxY = getMax(series, 1);
            params.printValuesFormatString = "%.0f";
        } else if (variable.equals("Number of checks")) {
            params.minY = 1d;
            params.maxY = getMax(series, 1);
            params.printValuesFormatString = "%.0f";
        } else if (variable.equals("Size of lattice")) {
            params.minY = 1d;
            params.maxY = getMax(series, 1);
            params.printValuesFormatString = "%.0f";
        }
        // TODO check why some plots do not have values
        else if (variable.equals("Information loss")) {
            params.minY = 1d;
            params.maxY = getMax(series, 1);
            params.printValuesFormatString = "%.0f";
        }
        if (focus.equals("Criteria")) {
            params.keypos = KeyPos.AT(5, params.maxY * 1.1d, "horiz bot center");
        } else {
            params.keypos = KeyPos.AT(2, params.maxY * 1.1d, "horiz bot center");
        }
        params.logY = true;
        params.ratio = 0.2d;

        // Return
        return new PlotGroup(variable + " grouped by \"" + focus + "\"", plots, params, 1.0d);
    }

    /**
     * Returns a maximum for the given series
     * @param series
     * @param offset Additional exponent
     * @return
     */
    private static double getMax(Series3D series, int offset) {

        double max = -Double.MAX_VALUE;
        for (Point3D point : series.getData()) {
            max = Math.max(Double.valueOf(point.z), max);
        }

        // Find smallest exponent larger than max
        for (int i = 0; i < 20; i++) {
            double limit = Math.pow(10d, i);
            if (limit >= max) {
                return Math.pow(10d, i + offset);
            }
        }

        throw new IllegalStateException("Cannot determine upper bound");
    }

    /**
     * Returns a series<br>
     * currently only analyzers of type Double are supported
     * 
     * @param file
     * @param algorithm
     * @param focus
     * @param analyzer needs to be of type {@link Double}
     * @param suppression
     * @param metric
     * @param dataset
     * @return
     * @throws ParseException
     */
    private static Series3D
            getSeries(CSVFile file,
                      String algorithm,
                      String variable,
                      String focus, Analyzer<?> analyzer, String suppression, Metric<?> metric) throws ParseException {

        // Select data for the given algorithm
        Selector<String[]> selector = file.getSelectorBuilder().field("Algorithm").equals(algorithm).and()
                                          .field("Suppression").equals(suppression)
                                          .and()
                                          .field("Metric").equals(metric.getName())
                                          .build();

        // Create series
        Series3D series = new Series3D(file, selector,
                                       new Field(focus),
                                       new Field("Algorithm"),
                                       new Field(variable, analyzer.getLabel()),
                                       (Analyzer<Double>) analyzer);

        return series;
    }
}
