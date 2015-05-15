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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.algorithm.TerminationConfiguration;
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
import de.linearbits.subframe.graph.Point3D;
import de.linearbits.subframe.graph.Series3D;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;
import de.linearbits.subframe.render.LaTeX;
import de.linearbits.subframe.render.PlotGroup;

public class BenchmarkAnalysis {
    /**
     * Wrapper class for plot group data.
     */
    private static class PlotGroupData {
        public Series3D      series;
        public GnuPlotParams params;

        PlotGroupData(Series3D series, GnuPlotParams params) {
            this.series = series;
            this.params = params;
        }
    }

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
        TOTAL_LATTICE_SIZE("Total size of lattice"),
        INFORMATION_LOSS_MINIMUM("Information loss minimum"),
        INFORMATION_LOSS_MINIMUM_TRANSFORMATION("Information loss minimum (Transformation)"),
        INFORMATION_LOSS_MAXIMUM("Information loss maximum"),
        INFORMATION_LOSS_MAXIMUM_TRANSFORMATION("Information loss maximum (Transformation)"),
        INFORMATION_LOSS_ADDITIONAL("Additional information loss"),
        INFORMATION_LOSS_RELATIVE("Relative information loss"),
        INFORMATION_LOSS_TRANSFORMATION("Information loss (Transformation)"),
        QI_COUNT("QI count"),
        ALGORITHM("Algorithm"),
        DATASET("Dataset"),
        CRITERIA("Criteria"),
        METRIC("Metric"),
        SUPPRESSION("Suppression"),
        TERMINATION_LIMIT("Termination Limit"),
        SOLUTION_DISCOVERY_TIME("Solution discovery time"),
        EXHAUSTIVE_SEARCH_TIME("Exhaustive search time"),
        LATTICE_COMPLETED("Lattice completed");

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
        // generateTables();
        // generateConventionalPlots();
        // generateHeuristicsComparisonGeoMean();
        // generateHeuristicsComparison();
        // generateQICountScalingPlots();
        printFlashComparisonTableTexCode();
        // generateHeuraklesSelfComparisonPlots();
    }

    private static void generateHeuristicsComparisonGeoMean() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE_GEOMEAN));

        BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();
        try {
            benchmarkConfiguration.readBenchmarkConfiguration(BenchmarkSetup.DEFAULT_CONFIGURAITON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Algorithm> algorithms = benchmarkConfiguration.getAlgorithms();
        VARIABLES[] variables = new VARIABLES[] { VARIABLES.INFORMATION_LOSS_RELATIVE };

        String focus = VARIABLES.CRITERIA.val;

        // create one file with several plots
        List<PlotGroup> groups = new ArrayList<PlotGroup>();

        // for each metric
        for (Metric<?> metric : benchmarkConfiguration.getMetrics()) {

            // for each suppression
            for (double suppr : benchmarkConfiguration.getSuppression()) {
                String suppression = String.valueOf(suppr);

                for (VARIABLES variable : variables) {
                    boolean xGroupPercent = false;
                    PlotGroupData data = getGroupData2(file,
                                                       new VARIABLES[] { variable },
                                                       new String[] { Analyzer.VALUE },
                                                       null,
                                                       null,
                                                       focus,
                                                       suppression,
                                                       benchmarkConfiguration.getCriteria(),
                                                       algorithms,
                                                       metric,
                                                       1.5,
                                                       xGroupPercent,
                                                       null);

                    Labels labels = new Labels(focus, variable.val);
                    List<Plot<?>> plots = new ArrayList<Plot<?>>();
                    plots.add(new PlotHistogramClustered("", labels, data.series));
                    String caption = variable.val +
                                     " using information loss metric \"" +
                                     metric.getName() +
                                     "\" with " + suppression + "\\%" + " suppression" +
                                     getTerminationLimitCaption(benchmarkConfiguration.getAlgorithms().get(0).getTerminationConfig()) +
                                     " listed by \"" + focus + "\".";

                    groups.add(new PlotGroup(caption, plots, data.params, 1.0d));
                }
            }
        }
        if (!groups.isEmpty()) {
            LaTeX.plot(groups, "results/results");
        }

    }

    private static void generateHeuristicsComparison() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));

        BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();
        try {
            benchmarkConfiguration.readBenchmarkConfiguration(BenchmarkSetup.DEFAULT_CONFIGURAITON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BenchmarkDataset[] datasets = benchmarkConfiguration.getDatasets();
        List<Algorithm> algorithms = benchmarkConfiguration.getAlgorithms();
        VARIABLES[] variables = new VARIABLES[] { VARIABLES.INFORMATION_LOSS_RELATIVE };

        if (datasets.length == 0 || algorithms.size() == 0) {
            return;
        }

        String focus = VARIABLES.DATASET.val;

        // create one file with several plots
        List<PlotGroup> groups = new ArrayList<PlotGroup>();

        // for each metric
        for (Metric<?> metric : benchmarkConfiguration.getMetrics()) {

            // For each combination of criteria
            for (String criteria : benchmarkConfiguration.getCriteria()) {

                // for each suppression
                for (double suppr : benchmarkConfiguration.getSuppression()) {
                    String suppression = String.valueOf(suppr);

                    // FIXME temporary fix
                    if (metric.getName().equals("Average equivalence class size") && suppr == 0.1 || metric.getName().equals("Loss") &&
                        suppr == 0.0) continue;

                    for (VARIABLES variable : variables) {
                        boolean xGroupPercent = false;
                        PlotGroupData data = getGroupData(file,
                                                          new VARIABLES[] { variable },
                                                          new String[] { Analyzer.VALUE },
                                                          null,
                                                          null,
                                                          focus,
                                                          criteria,
                                                          suppression,
                                                          datasets,
                                                          algorithms,
                                                          metric,
                                                          1.5,
                                                          xGroupPercent,
                                                          null);

                        Labels labels = new Labels(focus, variable.val);
                        List<Plot<?>> plots = new ArrayList<Plot<?>>();
                        plots.add(new PlotHistogramClustered("", labels, data.series));
                        String caption = variable.val + " for criteria " + benchmarkConfiguration.getReadableCriteria(criteria) +
                                         " using information loss metric \"" +
                                         metric.getName() +
                                         "\" with " + suppression + "\\%" + " suppression" +
                                         getTerminationLimitCaption(benchmarkConfiguration.getAlgorithms().get(0).getTerminationConfig()) +
                                         " listed by \"" + focus + "\".";

                        groups.add(new PlotGroup(caption, plots, data.params, 1.0d));
                    }
                }
            }

        }
        if (!groups.isEmpty()) {
            LaTeX.plot(groups, "results/results");
        }
    }

    private static String getTerminationLimitCaption(TerminationConfiguration tC) {
        String tL = "";
        if (null != tC) {
            switch (tC.getType()) {
            case CHECKS:
                tL = " and termination limit of " + tC.getValue() + " checks";
                break;
            case TIME:
                tL = " and termination limit of " + tC.getValue() + " ms";
            default:
                break;
            }
        }
        return tL;
    }

    /**
     * Generate the conventional plots
     * @throws IOException
     * @throws ParseException
     */
    // private static void generateConventionalPlots() throws IOException, ParseException {
    //
    // CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));
    //
    // BenchmarkDataset[] datasets = BenchmarkSetup.getConventionalDatasets();
    // List<Algorithm> algorithms = BenchmarkSetup.getAlgorithms(false, false);
    //
    // if (datasets.length == 0 || algorithms.size() == 0) {
    // return;
    // }
    //
    // String focus = VARIABLES.DATASET.val;
    //
    // // for each metric
    // for (Metric<?> metric : BenchmarkSetup.getMetrics()) {
    //
    // // create one file with several plots
    // List<PlotGroup> groups = new ArrayList<PlotGroup>();
    //
    // // For each combination of criteria
    // for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
    // String scriteria = Arrays.toString(criteria);
    //
    // // for each suppression
    // for (double suppr : BenchmarkSetup.getSuppression()) {
    // String suppression = String.valueOf(suppr);
    //
    // for (VARIABLES variable : new VARIABLES[] {
    // VARIABLES.EXECUTION_TIME,
    // VARIABLES.NUMBER_OF_CHECKS,
    // VARIABLES.NUMBER_OF_ROLLUPS,
    // VARIABLES.NUMBER_OF_SNAPSHOTS,
    // VARIABLES.INFORMATION_LOSS /* , VARIABLES.INFORMATION_LOSS_PERCENTAGE */}) {
    // String measure = (variable == VARIABLES.EXECUTION_TIME) ? Analyzer.ARITHMETIC_MEAN : Analyzer.VALUE;
    // boolean xGroupPercent = false;
    // PlotGroupData data = getGroupData(file,
    // new VARIABLES[] { variable },
    // new String[] { measure },
    // null,
    // null,
    // focus,
    // scriteria,
    // suppression,
    // datasets,
    // algorithms,
    // metric,
    // 1.5,
    // xGroupPercent,
    // null);
    //
    // Labels labels = new Labels(focus, variable.val);
    // List<Plot<?>> plots = new ArrayList<Plot<?>>();
    // plots.add(new PlotHistogramClustered("", labels, data.series));
    // String caption = variable.val + " for criteria " + scriteria + " using information loss metric \"" +
    // metric.getName() +
    // "\" with " + suppression + "\\%" + " suppression " + " listed by \"" + focus + "\".";
    //
    // groups.add(new PlotGroup(caption, plots, data.params, 1.0d));
    // }
    // }
    // }
    //
    // if (!groups.isEmpty()) {
    // LaTeX.plot(groups, "results/results_" + metric.getName().toLowerCase().replaceAll(" ", "_"));
    // }
    // }
    // }

    private static void generateConventionalPlotsByConfiguration() throws IOException, ParseException {

        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));

        BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();
        try {
            benchmarkConfiguration.readBenchmarkConfiguration(BenchmarkSetup.DEFAULT_CONFIGURAITON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BenchmarkDataset[] datasets = benchmarkConfiguration.getDatasets();
        List<Algorithm> algorithms = benchmarkConfiguration.getAlgorithms();

        if (datasets.length == 0 || algorithms.size() == 0) {
            return;
        }

        String focus = VARIABLES.DATASET.val;

        // for each metric
        for (Metric<?> metric : benchmarkConfiguration.getMetrics()) {

            // create one file with several plots
            List<PlotGroup> groups = new ArrayList<PlotGroup>();

            // For each combination of criteria
            for (String criteria : benchmarkConfiguration.getCriteria()) {
                // for each suppression
                for (double suppr : benchmarkConfiguration.getSuppression()) {
                    String suppression = String.valueOf(suppr);

                    for (VARIABLES variable : new VARIABLES[] {
                            VARIABLES.EXECUTION_TIME,
                            VARIABLES.NUMBER_OF_CHECKS,
                            VARIABLES.NUMBER_OF_ROLLUPS,
                            VARIABLES.NUMBER_OF_SNAPSHOTS,
                            VARIABLES.INFORMATION_LOSS /* , VARIABLES.INFORMATION_LOSS_PERCENTAGE */}) {
                        String measure = (variable == VARIABLES.EXECUTION_TIME) ? Analyzer.ARITHMETIC_MEAN : Analyzer.VALUE;
                        boolean xGroupPercent = false;
                        PlotGroupData data = getGroupData(file,
                                                          new VARIABLES[] { variable },
                                                          new String[] { measure },
                                                          null,
                                                          null,
                                                          focus,
                                                          criteria,
                                                          suppression,
                                                          datasets,
                                                          algorithms,
                                                          metric,
                                                          1.5,
                                                          xGroupPercent,
                                                          null);

                        Labels labels = new Labels(focus, variable.val);
                        List<Plot<?>> plots = new ArrayList<Plot<?>>();
                        plots.add(new PlotHistogramClustered("", labels, data.series));
                        String caption = variable.val + " for criteria " + criteria + " using information loss metric \"" +
                                         metric.getName() +
                                         "\" with " + suppression + "\\%" + " suppression " + " listed by \"" + focus + "\".";

                        groups.add(new PlotGroup(caption, plots, data.params, 1.0d));
                    }
                }
            }

            if (!groups.isEmpty()) {
                LaTeX.plot(groups, "results/results_" + metric.getName().toLowerCase().replaceAll(" ", "_"));
            }
        }
    }

    // /**
    // * Generate the plots for the flash comparison benchmark
    // * @throws IOException
    // * @throws ParseException
    // */
    // private static void generateFlashComparisonPlots() throws IOException, ParseException {
    // CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));
    //
    // BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();
    // try {
    // benchmarkConfiguration.readBenchmarkConfiguration(BenchmarkSetup.DEFAULT_CONFIGURAITON_FILE);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    //
    // Double[] suppressions = benchmarkConfiguration.getSuppression();
    // BenchmarkDataset[] datasets = benchmarkConfiguration.getDatasets();
    // Metric<?>[] metrics = benchmarkConfiguration.getMetrics();
    // List<String> criteria = benchmarkConfiguration.getCriteria();
    //
    // List<Algorithm> algorithms = new ArrayList<Algorithm>();
    // algorithms.add(new BenchmarkSetup.Algorithm(AlgorithmType.HEURAKLES_DFS, null));
    //
    // if (suppressions.length == 0 || algorithms.size() == 0 || datasets.length == 0) {
    // return;
    // }
    //
    // String focus = VARIABLES.DATASET.val;
    //
    // // create one file with several plots
    // List<PlotGroup> groups = new ArrayList<PlotGroup>();
    //
    // // for each suppression
    // for (double suppr : suppressions) {
    // String suppression = String.valueOf(suppr);
    //
    // // for each criteria
    // for (String scriteria : criteria) {
    //
    // // for each metric
    // for (Metric<?> metric : metrics) {
    //
    // boolean xGroupPercent = false;
    // PlotGroupData data = getGroupData(file,
    // new VARIABLES[] {
    // // VARIABLES.EXHAUSTIVE_SEARCH_TIME,
    // VARIABLES.EXECUTION_TIME,
    // VARIABLES.SOLUTION_DISCOVERY_TIME },
    // new String[] {
    // // Analyzer.ARITHMETIC_MEAN,
    // Analyzer.ARITHMETIC_MEAN,
    // Analyzer.ARITHMETIC_MEAN },
    // VARIABLES.INFORMATION_LOSS_ADDITIONAL,
    // Analyzer.VALUE,
    // focus,
    // scriteria,
    // suppression,
    // datasets,
    // algorithms,
    // metric,
    // 2.0d,
    // xGroupPercent,
    // null);
    //
    // Labels labels = new Labels(focus, VARIABLES.EXECUTION_TIME.val, "Additional information loss", "");
    // List<Plot<?>> plots = new ArrayList<Plot<?>>();
    // plots.add(new PlotHistogramClustered("", labels, data.series));
    // String caption = "Execution time of Flash, Solution discovery time and Additional information loss of Heurakles for criterium 5-anonymity " +
    // " using information loss metric \"" +
    // metric.getName() +
    // "\" with " +
    // Double.valueOf(suppression) *
    // 100d +
    // "\\%" +
    // " suppression " +
    // "listed by \"" +
    // focus +
    // "\". The QI Count used for the dataset " +
    // BenchmarkDataset.SS13ACS_SEMANTIC.toString().replaceAll("_", "\\\\_") + " was 10.";
    //
    // groups.add(new PlotGroup(caption, plots, data.params, 1.0d));
    // }
    // }
    // }
    //
    // if (!groups.isEmpty()) {
    // LaTeX.plot(groups, "results/results", true);
    // }
    // }

    private static void printFlashComparisonTableTexCode() throws IOException, ParseException {
        // CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));
        CSVFile file = new CSVFile(new File("resultsHeuraklesBFSDFS.csv"));

        BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();
        try {
            // benchmarkConfiguration.readBenchmarkConfiguration(BenchmarkSetup.DEFAULT_CONFIGURAITON_FILE);
            benchmarkConfiguration.readBenchmarkConfiguration("worklistHeuraklesBFSDFS.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Double[] suppressions = benchmarkConfiguration.getSuppression();
        BenchmarkDataset[] datasets = benchmarkConfiguration.getDatasets();
        Metric<?>[] metrics = benchmarkConfiguration.getMetrics();
        List<String> criteria = benchmarkConfiguration.getCriteria();
        List<Algorithm> algorithms = benchmarkConfiguration.getAlgorithms();

        if (suppressions.length == 0 || algorithms.size() == 0 || datasets.length == 0) {
            return;
        }

        // for each criteria
        for (String scriteria : criteria) {

            // for each metric
            for (Metric<?> metric : metrics) {

                System.out.println("\\begin{table}[htb!]");
                System.out.println("\\center");
                System.out.println("\\footnotesize");
                System.out.println("\\tabcolsep 2pt");
                System.out.println("\\begin{tabular}{|l|r|r|r|r|r|r|r|r|}\\hhline{~--------}");
                System.out.println("\\multicolumn{1}{c}{} &  \\multicolumn{4}{c}{0\\% Suppression limit} & \\multicolumn{4}{c}{100\\% Suppression limit}\\\\\\hhline{~--------}");
                System.out.println("Dataset & Flash [s] & Total [s] & Discovery [s] & Utility [\\%] & Flash [s] & Total [s] & Discovery [s] & Utility [\\%] \\\\\\hline");

                // for each dataset
                for (BenchmarkDataset dataset : datasets) {

                    Selector<String[]> selector = file.getSelectorBuilder()
                                                      .field(VARIABLES.DATASET.val)
                                                      .equals(dataset.toString())
                                                      .and()
                                                      .field(VARIABLES.CRITERIA.val)
                                                      .equals(scriteria)
                                                      .and()
                                                      .field(VARIABLES.METRIC.val)
                                                      .equals(metric.getName())
                                                      .build();

                    String flashRuntimeZero = null;
                    String heuraklesTotalTimeZero = null;
                    String discoveryTimeZero = null;
                    String utilityZero = null;
                    String flashRuntimeFull = null;
                    String heuraklesTotalTimeFull = null;
                    String discoveryTimeFull = null;
                    String utilityFull = null;

                    Iterator<CSVLine> iter = file.iterator();
                    while (iter.hasNext()) {
                        CSVLine csvline = iter.next();
                        String[] line = csvline.getData();

                        if (selector.isSelected(line)) {
                            if (csvline.get("", VARIABLES.ALGORITHM.val).equals("Flash")) {
                                if (csvline.get("", VARIABLES.SUPPRESSION.val).equals("0.0")) {
                                    flashRuntimeZero = csvline.get(VARIABLES.EXECUTION_TIME.val, "Arithmetic Mean");
                                } else {
                                    flashRuntimeFull = csvline.get(VARIABLES.EXECUTION_TIME.val, "Arithmetic Mean");
                                }
                            } else {
                                if (csvline.get("", VARIABLES.SUPPRESSION.val).equals("0.0")) {
//                                    heuraklesTotalTimeZero = csvline.get(VARIABLES.EXHAUSTIVE_SEARCH_TIME.val, "Arithmetic Mean");
                                    discoveryTimeZero = csvline.get(VARIABLES.SOLUTION_DISCOVERY_TIME.val, "Arithmetic Mean");
                                    utilityZero = csvline.get(VARIABLES.INFORMATION_LOSS_ADDITIONAL.val, "Value");
                                } else {
//                                    heuraklesTotalTimeFull = csvline.get(VARIABLES.EXHAUSTIVE_SEARCH_TIME.val, "Arithmetic Mean");
                                    discoveryTimeFull = csvline.get(VARIABLES.SOLUTION_DISCOVERY_TIME.val, "Arithmetic Mean");
                                    utilityFull = csvline.get(VARIABLES.INFORMATION_LOSS_ADDITIONAL.val, "Value");
                                }
                            }
                        }
                    }

                    System.out.println(dataset.toString() + " & "
                                       + (flashRuntimeZero == null ? "---" : round(Double.valueOf(flashRuntimeZero) / 1E9, 3)) + " & "
//                                       + (heuraklesTotalTimeZero == null ? "---" : round(Double.valueOf(heuraklesTotalTimeZero) / 1E9, 3))
                                       + " & "
                                       + (discoveryTimeZero == null ? "---" : round(Double.valueOf(discoveryTimeZero) / 1E9, 3)) + " & "
                                       + (utilityZero == null ? "---" : 100d - Double.valueOf(utilityZero)) + " & "
                                       + (flashRuntimeFull == null ? "---" : round(Double.valueOf(flashRuntimeFull) / 1E9, 3)) + " & "
//                                       + (heuraklesTotalTimeFull == null ? "---" : round(Double.valueOf(heuraklesTotalTimeFull) / 1E9, 3))
                                       + " & "
                                       + (discoveryTimeFull == null ? "---" : round(Double.valueOf(discoveryTimeFull) / 1E9, 3)) + " & "
                                       + (utilityFull == null ? "---" : 100d - Double.valueOf(utilityFull)) + " \\\\");
                    // System.out.println(dataset + "   & 0.054         &   2.183       & ---           & ---       & 1.169         &   2.009   & 0.077         & 100");
                    // System.out.println("Cup     & 0.039         &   1.188       & ---           & ---       & 15.574        &   13.292  & 7.722         & 100 \\\\");
                    // System.out.println("Fars    & 0.065         &   4.321       & ---           & ---       & 2.935         &   4.305   & 0.107         & 100 \\\\");
                    // System.out.println("Atus    & 0.379         &   106.117     & ---           & ---       & 49.782        &   105.996 & 0.437         & 100 \\\\");
                    // System.out.println("Ihis    & 1.284         &   55.469      & ---           & ---       & 43.449        &   56.915  & 2.829         & 100 \\\\");
                    // System.out.println("SS13ACS & 0.196         &   $>$600      & 0.171         & 86.22     & 19.652        &   $>$600  & 0.023         & 100 \\\\\\hline");

                }

                System.out.println("\\end{tabular}");
                System.out.println("\\caption{Comparison of Flash and Heurakles for " + criteria.toString() + " and " + metric.toString() +
                                   " utility measure}");
                System.out.println("\\label{tab:optimal_loss}");
                System.out.println("\\end{table}");
                System.out.println("");
            }
        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static void generateHeuraklesSelfComparisonPlots() throws IOException, ParseException {
        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));

        BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();
        try {
            benchmarkConfiguration.readBenchmarkConfiguration(BenchmarkSetup.DEFAULT_CONFIGURAITON_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Double[] suppressions = benchmarkConfiguration.getSuppression();
        Metric<?>[] metrics = benchmarkConfiguration.getMetrics();
        List<Algorithm> algorithms = benchmarkConfiguration.getAlgorithms();

        if (suppressions.length == 0 || metrics.length == 0) {
            return;
        }

        // create one file with several plots
        List<PlotGroup> groups = new ArrayList<PlotGroup>();

        for (Metric<?> metric : metrics) {

            // for both normal and logarithmical X-Axes
            for (boolean logX : new boolean[] { false /* , true */}) {

                GnuPlotParams params = new GnuPlotParams();
                params.rotateXTicks = 0;
                params.printValues = false;
                params.size = 1.5;
                params.logX = logX;
                params.logY = false;
                params.enhance = false;
                params.ratio = 0.2d;
                params.minY = 0d;
                params.printValuesFormatString = "%.0f";
                params.maxY = 100d;
                params.keypos = KeyPos.TOP_RIGHT;
                params.colorize = true;
                params.lineStyle = GnuPlotParams.LineStyle.STEPS;

                // for each suppression
                for (double suppr : suppressions) {

                    String suppression = String.valueOf(suppr);

                    Selector<String[]> timeSelector = file.getSelectorBuilder()
                                                          .field(VARIABLES.SUPPRESSION.val).equals(suppression).and()
                                                          .field(VARIABLES.METRIC.val).equals(metric.getName())
                                                          .build();

                    Series3D series = new Series3D(file,
                                                   timeSelector,
                                                   new Field("", VARIABLES.QI_COUNT.val),
                                                   new Field(VARIABLES.SOLUTION_DISCOVERY_TIME.val, Analyzer.VALUE),
                                                   new Field(VARIABLES.INFORMATION_LOSS.val, Analyzer.VALUE));

                    // extract all distinct discovery time values and QIs

                    Set<Long> dTimeNanos = new TreeSet<Long>();
                    Set<Long> qis = new TreeSet<Long>();

                    for (Point3D p : series.getData()) {
                        qis.add(Long.valueOf(p.x));

                        String timeString = p.y.substring(1);
                        timeString = timeString.substring(0, timeString.length() - 1);
                        String times[] = timeString.split(",");

                        for (int i = 0; i < times.length; ++i) {
                            // Long millis = Long.valueOf(times[i].replaceAll(" ", "")) / 1000000;
                            Long nano = Long.valueOf(times[i].replaceAll(" ", ""));
                            dTimeNanos.add(nano);
                        }
                    }

                    dTimeNanos.add(0L);

                    series.getData().clear();

                    // for each algorithm
                    for (Algorithm algorithm : algorithms) {

                        Selector<String[]> selector = file.getSelectorBuilder()
                                                          .field(VARIABLES.SUPPRESSION.val).equals(suppression).and()
                                                          .field(VARIABLES.METRIC.val).equals(metric.getName()).and()
                                                          .field(VARIABLES.ALGORITHM.val).equals(algorithm.toString())
                                                          .build();

                        Series3D algorithmSeries = new Series3D(file,
                                                                selector,
                                                                new Field("", VARIABLES.QI_COUNT.val),
                                                                new Field(VARIABLES.SOLUTION_DISCOVERY_TIME.val, Analyzer.VALUE),
                                                                new Field(VARIABLES.INFORMATION_LOSS.val, Analyzer.VALUE));

                        // create Points for each discovery time resp. information loss value for the given algorithm

                        List<Point3D> algorithmData = new ArrayList<Point3D>();

                        for (Point3D p : algorithmSeries.getData()) {
                            String timeString = p.y.substring(1);
                            timeString = timeString.substring(0, timeString.length() - 1);
                            String times[] = timeString.split(",");

                            String ilString = p.z.substring(1);
                            ilString = ilString.substring(0, ilString.length() - 1);
                            String ilValues[] = ilString.split(",");

                            for (int i = 0; i < ilValues.length; ++i) {
                                // Long millis = Long.valueOf(times[i].replaceAll(" ", "")) / 1000000;
                                Long nano = Long.valueOf(times[i].replaceAll(" ", ""));
                                Point3D pNew = new Point3D(String.valueOf(nano), p.x, ilValues[i].replaceAll(" ", ""));
                                algorithmData.add(pNew);
                            }
                        }

                        // scale relative
                        List<Point3D> relativeData = new ArrayList<Point3D>();
                        for (Long qi : qis) {
                            double min = Double.MAX_VALUE;
                            double max = Double.MIN_VALUE;

                            for (Point3D p : algorithmData) {
                                if (p.y.equals(String.valueOf(qi))) {
                                    double value = Double.valueOf(p.z);
                                    if (value > max) max = value;
                                    if (value < min) min = value;
                                }
                            }

                            for (Point3D p : algorithmData) {
                                if (p.y.equals(String.valueOf(qi))) {
                                    double percent = (max != min) ? ((Double.valueOf(p.z) - min) / (max - min)) * 100.0d : 0d;
                                    relativeData.add(new Point3D(p.x, p.y, String.valueOf(percent)));
                                }
                            }
                        }
                        algorithmData = relativeData;

                        // insert missing values

                        List<Point3D> missingValues = new ArrayList<Point3D>();
                        for (Long qi : qis) {
                            Iterator<Long> iter = dTimeNanos.iterator();
                            Long dTimeNano = iter.next();
                            String lastValue = "100";

                            for (Point3D p : algorithmData) {
                                if (p.y.equals(String.valueOf(qi))) {
                                    while (dTimeNano < Long.valueOf(p.x)) {
                                        Point3D pMissing = new Point3D(String.valueOf(dTimeNano), p.y, lastValue);
                                        missingValues.add(pMissing);
                                        dTimeNano = iter.next();
                                    }
                                    lastValue = p.z;
                                    if (iter.hasNext()) dTimeNano = iter.next();
                                }
                            }
                        }

                        for (Point3D p : missingValues) {
                            algorithmData.add(p);
                        }

                        Collections.sort(algorithmData, new Comparator<Point3D>() {
                            public int compare(Point3D p1, Point3D p2)
                            {
                                if (!p1.y.equals(p2.y)) return Long.valueOf(p1.y).compareTo(Long.valueOf(p2.y));
                                return Long.valueOf(p1.x).compareTo(Long.valueOf(p2.x));
                            }
                        });

                        for (Point3D p : algorithmData) {
                            Point3D p2 = new Point3D(p.x, algorithm.toString() + " " + p.y, p.z);
                            series.getData().add(p2);
                        }
                    }

                    PlotGroupData data = new PlotGroupData(series, params);

                    Labels labels = new Labels("Runtime", "Relative information loss");
                    List<Plot<?>> plots = new ArrayList<Plot<?>>();
                    plots.add(new PlotLinesClustered("", labels, data.series));
                    String caption = "Relative information loss for criterium 5-anonymity using information loss metric \"" +
                                     metric.toString() +
                                     "\" with " + Double.valueOf(suppression) * 100d + "\\%" + " suppression listed by runtime" +
                                     (logX ? " in logarithmic scaling" : "") +
                                     " for dataset \"SS13ACS\\_SEMANTIC\". Data points required for plotting before a solution has been found have been set to 100\\%.";

                    groups.add(new PlotGroup(caption, plots, data.params, 1.0d));
                }
            }
        }

        if (!groups.isEmpty()) {
            LaTeX.plot(groups, "results/results", true);
        }
    }

    /**
     * Generate the plots for the QI count scaling benchmark
     * @throws IOException
     * @throws ParseException
     */
    // private static void generateQICountScalingPlots() throws IOException, ParseException {
    //
    // CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));
    //
    // Algorithm flash = null;
    // for (Algorithm algorithm : BenchmarkSetup.getAlgorithms(false, false)) {
    // if (algorithm.getType() == AlgorithmType.FLASH) {
    // flash = algorithm;
    // }
    // }
    // if (flash == null) {
    // throw new RuntimeException("Flash algorithm not found");
    // }
    //
    // String focus = VARIABLES.QI_COUNT.val;
    //
    // // for each Qi count scaling dataset
    // for (BenchmarkDataset dataset : BenchmarkSetup.getQICountScalingDatasets()) {
    //
    // // create one file with several plots
    // List<PlotGroup> groups = new ArrayList<PlotGroup>();
    //
    // // for each metric
    // for (Metric<?> metric : BenchmarkSetup.getMetrics()) {
    //
    // // For each combination of criteria
    // for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
    // String scriteria = Arrays.toString(criteria);
    //
    // // for each suppression
    // for (double suppr : BenchmarkSetup.getSuppression()) {
    // String suppression = String.valueOf(suppr);
    //
    // // For each algorithm of type heurakles
    // for (Algorithm heurakles : BenchmarkSetup.getAlgorithms(false, false)) {
    // if (heurakles.getType() != AlgorithmType.HEURAKLES) continue;
    //
    // boolean xGroupPercent = false;
    // PlotGroupData data = getGroupData(file,
    // new VARIABLES[] { VARIABLES.EXECUTION_TIME },
    // new String[] { Analyzer.ARITHMETIC_MEAN },
    // null,
    // null,
    // focus,
    // scriteria,
    // suppression,
    // new BenchmarkDataset[] { dataset },
    // Arrays.asList(new Algorithm[] { flash, heurakles }),
    // metric,
    // 1.5,
    // xGroupPercent,
    // null);
    //
    // Labels labels = new Labels(focus, VARIABLES.EXECUTION_TIME.val);
    // List<Plot<?>> plots = new ArrayList<Plot<?>>();
    // plots.add(new PlotLinesClustered("", labels, data.series));
    // String caption = VARIABLES.EXECUTION_TIME.val +
    // " for criteria " + scriteria + " using information loss metric \"" + metric.getName() +
    // "\" with " + suppression + "\\%" + " suppression " + " listed by \"" + focus + "\".";
    //
    // groups.add(new PlotGroup(caption, plots, data.params, 1.0d));
    // }
    // }
    // }
    // }
    //
    // if (!groups.isEmpty()) {
    // LaTeX.plot(groups, "results/results_QI_count_scaling_" + dataset.toString());
    // }
    // }
    // }

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
    // private static void
    // generateTable(CSVFile file, String suppression, Metric<?> metric, VARIABLES variable, String measure, boolean lowerIsBetter) throws ParseException,
    // IOException {
    //
    // // Create csv header
    // ArrayList<String> header = new ArrayList<String>();
    // header.add("");
    // for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {
    // int minQI = BenchmarkSetup.getMinQICount(dataset);
    // int maxQI = minQI;
    // for (Algorithm algorithm : BenchmarkSetup.getAlgorithms(false, false)) {
    // maxQI = Math.max(BenchmarkSetup.getMaxQICount(algorithm, dataset), maxQI);
    // }
    //
    // for (int numQI = minQI; numQI <= maxQI; ++numQI) {
    // header.add(dataset.toString() + " " + numQI + " QIs");
    // }
    // }
    //
    // String[] header2 = new String[header.size()];
    // header.toArray(header2);
    // String[] header1 = new String[header.size()];
    // Arrays.fill(header1, "");
    //
    // // Create csv
    // CSVFile csv = new CSVFile(header1, header2);
    //
    // // For each criterion
    // for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
    //
    // // For each algorithm of type heurakles
    // for (Algorithm heurakles : BenchmarkSetup.getAlgorithms(false, false)) {
    // if (heurakles.getType() != AlgorithmType.HEURAKLES) continue;
    //
    // // The current line
    // String scriteria = Arrays.toString(criteria);
    // ArrayList<String> line = new ArrayList<String>();
    // line.add(scriteria);
    //
    // // For each dataset and QI count combination
    // for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {
    // int minQI = BenchmarkSetup.getMinQICount(dataset);
    // int maxQI = minQI;
    // for (Algorithm algorithm : BenchmarkSetup.getAlgorithms(false, false)) {
    // maxQI = Math.max(BenchmarkSetup.getMaxQICount(algorithm, dataset), maxQI);
    // }
    //
    // for (int numQI = minQI; numQI <= maxQI; ++numQI) {
    //
    // // Init
    // String firstAlgorithm = null;
    // String secondAlgorithm = null;
    // double firstValue = Double.MAX_VALUE;
    // double secondValue = Double.MAX_VALUE;
    // if (!lowerIsBetter) {
    // firstValue = Double.MIN_VALUE;
    // secondValue = Double.MIN_VALUE;
    // }
    //
    // // Select data for the given data point
    // Selector<String[]> selector = file.getSelectorBuilder()
    // .field(VARIABLES.DATASET.val)
    // .equals(dataset.toString())
    // .and()
    // .field(VARIABLES.QI_COUNT.val)
    // .equals(String.valueOf(numQI))
    // .and()
    // .field(VARIABLES.CRITERIA.val)
    // .equals(scriteria)
    // .and()
    // .field(VARIABLES.METRIC.val)
    // .equals(metric.getName())
    // .and()
    // .field(VARIABLES.SUPPRESSION.val)
    // .equals(suppression)
    // .and()
    // .begin()
    // .field(VARIABLES.ALGORITHM.val)
    // .equals(heurakles.toString())
    // .or()
    // .field(VARIABLES.ALGORITHM.val)
    // .equals(AlgorithmType.FLASH.toString())
    // .end()
    // .build();
    //
    // // Create series
    // Series2D series = new Series2D(file, selector,
    // new Field(VARIABLES.ALGORITHM.val),
    // new Field(variable.val, measure));
    //
    // boolean noSolutionFound = false;
    //
    // // Select from series
    // for (Point2D point : series.getData()) {
    //
    // // Read
    // double value = Double.valueOf(point.y);
    // if (variable == VARIABLES.INFORMATION_LOSS && value == (double) BenchmarkDriver.NO_SOLUTION_FOUND) noSolutionFound = true;
    // String algorithm = point.x;
    //
    // // Check
    // if ((lowerIsBetter && value < firstValue) ||
    // (!lowerIsBetter && value > firstValue)) {
    //
    // secondValue = firstValue;
    // secondAlgorithm = firstAlgorithm;
    //
    // firstValue = value;
    // firstAlgorithm = algorithm;
    //
    // } else if ((lowerIsBetter && value < secondValue) ||
    // (!lowerIsBetter && value > secondValue)) {
    //
    // secondValue = value;
    // secondAlgorithm = algorithm;
    // }
    // }
    //
    // // Compute difference
    // double difference = 0;
    // if (lowerIsBetter) difference = (1d - (firstValue / secondValue)) * 100d;
    // else difference = (1d - (secondValue / firstValue)) * 100d;
    //
    // // Render and store
    // final NumberFormat df = new DecimalFormat("#");
    // line.add(noSolutionFound ? "No solution found" : firstAlgorithm + " (" + df.format(difference) + "%) " +
    // secondAlgorithm);
    // }
    // }
    //
    // // Add line
    // String[] lineArr = new String[line.size()];
    // line.toArray(lineArr);
    // csv.addLine(lineArr);
    // }
    //
    // }
    //
    // // Write to file
    // csv.write(new File("results/table_" + variable.val.toLowerCase().replaceAll(" ", "_") + "_" +
    // metric.getName().toLowerCase().replaceAll(" ", "_") + "_" +
    // suppression.toLowerCase().replaceAll(" ", "_") + ".csv"));
    // }

    /**
     * Generate the tables
     * @throws IOException
     * @throws ParseException
     */
    // private static void generateTables() throws IOException, ParseException {
    //
    // CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));
    //
    // // for each metric
    // for (Metric<?> metric : BenchmarkSetup.getMetrics()) {
    //
    // // for each suppression
    // for (double suppr : BenchmarkSetup.getSuppression()) {
    // String suppression = String.valueOf(suppr);
    //
    // generateTable(file, suppression, metric, VARIABLES.EXECUTION_TIME, Analyzer.ARITHMETIC_MEAN, true);
    // generateTable(file, suppression, metric, VARIABLES.NUMBER_OF_CHECKS, Analyzer.VALUE, true);
    // generateTable(file, suppression, metric, VARIABLES.NUMBER_OF_ROLLUPS, Analyzer.VALUE, false);
    // generateTable(file, suppression, metric, VARIABLES.NUMBER_OF_SNAPSHOTS, Analyzer.VALUE, false);
    // generateTable(file, suppression, metric, VARIABLES.INFORMATION_LOSS, Analyzer.VALUE, true);
    // }
    // }
    // }

    /**
     * Returns a plot group
     * @param file
     * @param variable1
     * @param measure1
     * @param variable2
     * @param measure2
     * @param focus
     * @param scriteria
     * @param suppression TODO
     * @param metric
     * @return
     * @throws ParseException
     */
    private static PlotGroupData getGroupData(CSVFile file,
                                              VARIABLES[] variables,
                                              String[] measures,
                                              VARIABLES variableY2,
                                              String measureY2,
                                              String focus,
                                              String scriteria,
                                              String suppression,
                                              BenchmarkDataset[] datasets,
                                              List<Algorithm> algorithms,
                                              Metric<?> metric,
                                              double keyPosX,
                                              boolean xGroupPercent,
                                              Double missingAlgorithmDummyValue) throws ParseException {
        // Sanity check
        if (variables.length != measures.length) {
            throw new RuntimeException("For each variable, exactly one measure is required");
        }

        // Prepare
        Series3D series = null;

        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = 0;
        params.printValues = true;
        params.size = 1.5;
        params.logY = false;
        params.enhance = false;
        params.ratio = 0.2d;
        params.minY = 0d;
        params.printValuesFormatString = "%.0f";

        double max = 0d;
        double padding = 0d;

        boolean allVariablesRelative = true;
        boolean allVariablesTimes = true;

        // Collect data for all algorithms and the variables array
        boolean includeVariableInYCoordinates = variables.length > 1 || variableY2 != null;
        for (int i = 0; i < variables.length; ++i) {
            Series3D _series = collectData(file,
                                           variables[i],
                                           measures[i],
                                           focus,
                                           scriteria,
                                           suppression,
                                           datasets,
                                           algorithms,
                                           metric,
                                           includeVariableInYCoordinates,
                                           null);

            _series.sort(new Comparator<Point3D>() {
                @Override
                public int compare(Point3D o1, Point3D o2) {
                    return o1.x.compareTo(o2.x);
                }
            });
            if (series == null) {
                series = _series;
            } else {
                series.append(_series);
            }

            max = Math.max(getMax(series.getData()), max);
            padding = max * 0.3d;

            if (variables[i] != VARIABLES.INFORMATION_LOSS_ADDITIONAL) {
                allVariablesRelative = false;
            }
            if (variables[i] != VARIABLES.EXECUTION_TIME && variables[i] != VARIABLES.SOLUTION_DISCOVERY_TIME &&
                variables[i] != VARIABLES.EXHAUSTIVE_SEARCH_TIME) {
                allVariablesTimes = false;
            }
        }

        if (allVariablesRelative) {
            max = 100d;
        }

        if (allVariablesTimes || max < 10d) {
            params.printValuesFormatString = "%.2f";
        }

        if (max >= 10000d || variables[0] == VARIABLES.INFORMATION_LOSS_RELATIVE) {
            padding = max * 0.7d;
            params.printValuesFormatString = "%.2e";
        }

        params.maxY = max + padding;
        params.keypos = KeyPos.AT(keyPosX, params.maxY * 1.1d, "horiz bot center");

        // Collect data for all algorithms and variableY2, if any
        if (variableY2 != null) {
            Double relativeScalingMax = new Double(max);

            series.append(collectData(file,
                                      variableY2,
                                      measureY2,
                                      focus,
                                      scriteria,
                                      suppression,
                                      datasets,
                                      algorithms,
                                      metric,
                                      true,
                                      relativeScalingMax));

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

        if (xGroupPercent) {
            List<Point3D> data = series.getData();

            Map<String, List<Point3D>> byXValue = new HashMap<String, List<Point3D>>();
            for (Point3D point : data) {
                if (!byXValue.containsKey(point.x)) byXValue.put(point.x, new ArrayList<Point3D>());
                byXValue.get(point.x).add(point);
            }

            data.clear();

            String[] keyArray = byXValue.keySet().toArray(new String[byXValue.keySet().size()]);
            Arrays.sort(keyArray, new Comparator<String>() {
                public int compare(String s1, String s2)
                {
                    return Long.valueOf(s1).compareTo(Long.valueOf(s2));
                }
            });

            for (String key : keyArray) {
                List<Point3D> points = byXValue.get(key);
                double minVal = getMin(points);
                double maxVal = getMax(points);
                for (Point3D point : points) {
                    double percent = ((Double.valueOf(point.z) - minVal) / (maxVal - minVal)) * 100.0d;
                    if (maxVal == minVal) {
                        percent = 0d;
                    }
                    data.add(new Point3D(point.x, point.y, Double.toString(percent)));
                }
            }

            params.minY = 0d;
            params.maxY = 120d;

            params.printValuesFormatString = "%.0f";

            params.keypos = KeyPos.AT(keyPosX, params.maxY * 1.1d, "horiz bot center");
        }

        if (missingAlgorithmDummyValue != null) {
            List<Point3D> data = series.getData();
            Map<String, List<Point3D>> byXValue = new HashMap<String, List<Point3D>>();
            for (Point3D point : data) {
                if (!byXValue.containsKey(point.x)) byXValue.put(point.x, new ArrayList<Point3D>());
                byXValue.get(point.x).add(point);
            }

            Map<String, Map<String, Point3D>> byXAndYValue = new HashMap<String, Map<String, Point3D>>();
            for (Point3D point : data) {
                if (!byXAndYValue.containsKey(point.x)) byXAndYValue.put(point.x, new HashMap<String, Point3D>());
                byXAndYValue.get(point.x).put(point.y, point);
            }

            for (Entry<String, Map<String, Point3D>> entry : byXAndYValue.entrySet()) {
                String xValue = entry.getKey();
                Map<String, Point3D> byYvalue = entry.getValue();
                for (Algorithm algorithm : algorithms) {
                    String algorithmString = algorithm.toString();
                    if (!byYvalue.containsKey(algorithmString)) {
                        byYvalue.put(algorithmString, new Point3D(xValue, algorithmString, Double.toString(missingAlgorithmDummyValue)));
                    }
                }
            }

            data.clear();

            for (Map<String, Point3D> byYValue : byXAndYValue.values()) {
                for (Point3D point : byYValue.values()) {
                    data.add(point);
                }
            }

            Collections.sort(data, new Comparator<Point3D>() {
                public int compare(Point3D p1, Point3D p2)
                {
                    int xCompare = Double.valueOf(p1.x).compareTo(Double.valueOf(p2.x));
                    if (xCompare != 0) return xCompare;
                    int yLengthCompare = p1.y.length() - p2.y.length();
                    if (yLengthCompare != 0) return yLengthCompare;
                    return p1.y.compareTo(p2.y);
                }
            });
        }

        return new PlotGroupData(series, params);
    }

    private static PlotGroupData getGroupData2(CSVFile file,
                                               VARIABLES[] variables,
                                               String[] measures,
                                               VARIABLES variableY2,
                                               String measureY2,
                                               String focus,
                                               String suppression,
                                               List<String> criteria,
                                               List<Algorithm> algorithms,
                                               Metric<?> metric,
                                               double keyPosX,
                                               boolean xGroupPercent,
                                               Double missingAlgorithmDummyValue) throws ParseException {
        // Sanity check
        if (variables.length != measures.length) {
            throw new RuntimeException("For each variable, exactly one measure is required");
        }

        // Prepare
        Series3D series = null;

        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = 0;
        params.printValues = true;
        params.size = 1.5;
        params.logY = false;
        params.enhance = false;
        params.ratio = 0.2d;
        params.minY = 0d;
        params.printValuesFormatString = "%.0f";

        double max = 0d;
        double padding = 0d;

        boolean allVariablesRelative = true;
        boolean allVariablesTimes = true;

        // Collect data for all algorithms and the variables array
        boolean includeVariableInYCoordinates = variables.length > 1 || variableY2 != null;
        for (int i = 0; i < variables.length; ++i) {
            Series3D _series = collectData2(file,
                                            variables[i],
                                            measures[i],
                                            focus,
                                            criteria,
                                            suppression,
                                            algorithms,
                                            metric,
                                            includeVariableInYCoordinates,
                                            null);

            _series.sort(new Comparator<Point3D>() {
                @Override
                public int compare(Point3D o1, Point3D o2) {
                    return o1.x.compareTo(o2.x);
                }
            });
            if (series == null) {
                series = _series;
            } else {
                series.append(_series);
            }

            max = Math.max(getMax(series.getData()), max);
            padding = max * 0.3d;

            if (variables[i] != VARIABLES.INFORMATION_LOSS_ADDITIONAL) {
                allVariablesRelative = false;
            }
            if (variables[i] != VARIABLES.EXECUTION_TIME && variables[i] != VARIABLES.SOLUTION_DISCOVERY_TIME &&
                variables[i] != VARIABLES.EXHAUSTIVE_SEARCH_TIME) {
                allVariablesTimes = false;
            }
        }

        if (allVariablesRelative) {
            max = 100d;
        }

        if (allVariablesTimes || max < 10d) {
            params.printValuesFormatString = "%.2f";
        }

        if (max >= 10000d || variables[0] == VARIABLES.INFORMATION_LOSS_RELATIVE) {
            padding = max * 0.7d;
            params.printValuesFormatString = "%.2e";
        }

        params.maxY = max + padding;
        params.keypos = KeyPos.AT(keyPosX, params.maxY * 1.1d, "horiz bot center");

        // Collect data for all algorithms and variableY2, if any
        if (variableY2 != null) {
            Double relativeScalingMax = new Double(max);

            series.append(collectData2(file,
                                       variableY2,
                                       measureY2,
                                       focus,
                                       criteria,
                                       suppression,
                                       algorithms,
                                       metric,
                                       true,
                                       relativeScalingMax));

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

        if (xGroupPercent) {
            List<Point3D> data = series.getData();

            Map<String, List<Point3D>> byXValue = new HashMap<String, List<Point3D>>();
            for (Point3D point : data) {
                if (!byXValue.containsKey(point.x)) byXValue.put(point.x, new ArrayList<Point3D>());
                byXValue.get(point.x).add(point);
            }

            data.clear();

            String[] keyArray = byXValue.keySet().toArray(new String[byXValue.keySet().size()]);
            Arrays.sort(keyArray, new Comparator<String>() {
                public int compare(String s1, String s2)
                {
                    return Long.valueOf(s1).compareTo(Long.valueOf(s2));
                }
            });

            for (String key : keyArray) {
                List<Point3D> points = byXValue.get(key);
                double minVal = getMin(points);
                double maxVal = getMax(points);
                for (Point3D point : points) {
                    double percent = ((Double.valueOf(point.z) - minVal) / (maxVal - minVal)) * 100.0d;
                    if (maxVal == minVal) {
                        percent = 0d;
                    }
                    data.add(new Point3D(point.x, point.y, Double.toString(percent)));
                }
            }

            params.minY = 0d;
            params.maxY = 120d;

            params.printValuesFormatString = "%.0f";

            params.keypos = KeyPos.AT(keyPosX, params.maxY * 1.1d, "horiz bot center");
        }

        if (missingAlgorithmDummyValue != null) {
            List<Point3D> data = series.getData();
            Map<String, List<Point3D>> byXValue = new HashMap<String, List<Point3D>>();
            for (Point3D point : data) {
                if (!byXValue.containsKey(point.x)) byXValue.put(point.x, new ArrayList<Point3D>());
                byXValue.get(point.x).add(point);
            }

            Map<String, Map<String, Point3D>> byXAndYValue = new HashMap<String, Map<String, Point3D>>();
            for (Point3D point : data) {
                if (!byXAndYValue.containsKey(point.x)) byXAndYValue.put(point.x, new HashMap<String, Point3D>());
                byXAndYValue.get(point.x).put(point.y, point);
            }

            for (Entry<String, Map<String, Point3D>> entry : byXAndYValue.entrySet()) {
                String xValue = entry.getKey();
                Map<String, Point3D> byYvalue = entry.getValue();
                for (Algorithm algorithm : algorithms) {
                    String algorithmString = algorithm.toString();
                    if (!byYvalue.containsKey(algorithmString)) {
                        byYvalue.put(algorithmString, new Point3D(xValue, algorithmString, Double.toString(missingAlgorithmDummyValue)));
                    }
                }
            }

            data.clear();

            for (Map<String, Point3D> byYValue : byXAndYValue.values()) {
                for (Point3D point : byYValue.values()) {
                    data.add(point);
                }
            }

            Collections.sort(data, new Comparator<Point3D>() {
                public int compare(Point3D p1, Point3D p2)
                {
                    int xCompare = Double.valueOf(p1.x).compareTo(Double.valueOf(p2.x));
                    if (xCompare != 0) return xCompare;
                    int yLengthCompare = p1.y.length() - p2.y.length();
                    if (yLengthCompare != 0) return yLengthCompare;
                    return p1.y.compareTo(p2.y);
                }
            });
        }

        return new PlotGroupData(series, params);
    }

    private static Series3D collectData(CSVFile file,
                                        VARIABLES variable,
                                        String measure,
                                        String focus,
                                        String scriteria,
                                        String suppression,
                                        BenchmarkDataset[] datasets,
                                        List<Algorithm> algorithms,
                                        Metric<?> metric,
                                        boolean includeVariableInYCoordinates,
                                        Double relativeScalingMax) throws ParseException {
        // Prepare
        Series3D series = null;

        // Collect data for all algorithms and the first variable
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

            if (includeVariableInYCoordinates) {
                // Assure that y coordinates are unique for each algorithm and variable combination
                final String var = variable.val;
                final String algo = algorithm.toString();
                _series.transform(new Function<Point3D>() {
                    @Override
                    public Point3D apply(Point3D t) {
                        return new Point3D(t.x, algo + " " + var, t.z);
                    }
                });
            }

            if (relativeScalingMax != null) {
                // Assure that z coordinates are scaled so that 100% equals max
                final double maxConst = relativeScalingMax;
                _series.transform(new Function<Point3D>() {
                    @Override
                    public Point3D apply(Point3D t) {
                        return new Point3D(t.x, t.y, String.valueOf(Double.valueOf(t.z) * maxConst / 100d));
                    }
                });
            } else if (VARIABLES.EXECUTION_TIME == variable || VARIABLES.SOLUTION_DISCOVERY_TIME == variable ||
                       VARIABLES.EXHAUSTIVE_SEARCH_TIME == variable) {
                // Transform times from nanos to seconds
                _series.transform(new Function<Point3D>() {
                    @Override
                    public Point3D apply(Point3D t) {
                        return new Point3D(t.x, t.y, String.valueOf(Double.valueOf(t.z) / 1000000000d));
                    }
                });
            }

            if (series == null) series = _series;
            else series.append(_series);

        }

        return series;
    }

    private static Series3D collectData2(CSVFile file,
                                         VARIABLES variable,
                                         String measure,
                                         String focus,
                                         List<String> scriteria,
                                         String suppression,
                                         List<Algorithm> algorithms,
                                         Metric<?> metric,
                                         boolean includeVariableInYCoordinates,
                                         Double relativeScalingMax) throws ParseException {
        // Prepare
        Series3D series = null;

        // Collect data for all algorithms and the first variable
        for (Algorithm algorithm : algorithms) {

            for (String criteria : scriteria) {

                Series3D _series = getSeries(file,
                                             algorithm.toString(),
                                             variable.val,
                                             measure,
                                             focus,
                                             criteria,
                                             suppression,
                                             null,
                                             metric);

                if (includeVariableInYCoordinates) {
                    // Assure that y coordinates are unique for each algorithm and variable combination
                    final String var = variable.val;
                    final String algo = algorithm.toString();
                    _series.transform(new Function<Point3D>() {
                        @Override
                        public Point3D apply(Point3D t) {
                            return new Point3D(t.x, algo + " " + var, t.z);
                        }
                    });
                }

                if (relativeScalingMax != null) {
                    // Assure that z coordinates are scaled so that 100% equals max
                    final double maxConst = relativeScalingMax;
                    _series.transform(new Function<Point3D>() {
                        @Override
                        public Point3D apply(Point3D t) {
                            return new Point3D(t.x, t.y, String.valueOf(Double.valueOf(t.z) * maxConst / 100d));
                        }
                    });
                } else if (VARIABLES.EXECUTION_TIME == variable || VARIABLES.SOLUTION_DISCOVERY_TIME == variable ||
                           VARIABLES.EXHAUSTIVE_SEARCH_TIME == variable) {
                    // Transform times from nanos to seconds
                    _series.transform(new Function<Point3D>() {
                        @Override
                        public Point3D apply(Point3D t) {
                            return new Point3D(t.x, t.y, String.valueOf(Double.valueOf(t.z) / 1000000000d));
                        }
                    });
                }

                if (series == null) series = _series;
                else series.append(_series);

            }
        }

        return series;
    }

    /**
     * Returns a maximum for the given list of points
     * @param points
     * @return
     */
    private static double getMax(List<Point3D> points) {

        double max = -Double.MAX_VALUE;
        for (Point3D point : points) {
            max = Math.max(Double.valueOf(point.z), max);
        }

        return max;
    }

    /**
     * Returns a minimum for the given list of points
     * @param points
     * @return
     */
    private static double getMin(List<Point3D> points) {

        double min = Double.MAX_VALUE;
        for (Point3D point : points) {
            min = Math.min(Double.valueOf(point.z), min);
        }

        return min;
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

        Selector<String[]> selector = null;
        if (null != datasets) {

            SelectorBuilder<String[]> selectorBuilder = file.getSelectorBuilder().field(VARIABLES.ALGORITHM.val).equals(algorithm).and()
                                                            .field(VARIABLES.SUPPRESSION.val).equals(suppression).and()
                                                            .field(VARIABLES.METRIC.val).equals(metric.getName()).and()
                                                            .field(VARIABLES.CRITERIA.val).equals(scriteria)
                                                            .and().begin();

            for (int i = 0; i < datasets.length; ++i) {
                selectorBuilder.field(VARIABLES.DATASET.val).equals(datasets[i].toString());
                if (i < datasets.length - 1) {
                    selectorBuilder.or();
                }
            }
            selector = selectorBuilder.end().build();
        } else {
            selector = file.getSelectorBuilder().field(VARIABLES.ALGORITHM.val).equals(algorithm).and()
                           .field(VARIABLES.SUPPRESSION.val).equals(suppression).and()
                           .field(VARIABLES.METRIC.val).equals(metric.getName()).and()
                           .field(VARIABLES.CRITERIA.val).equals(scriteria).build();

        }

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

    // private static Series3D
    // getSeries(CSVFile file,
    // String variable,
    // String measure,
    // String focus,
    // String scriteria,
    // String suppression,
    // BenchmarkDataset[] datasets,
    // Metric<?> metric) throws ParseException {
    //
    // // Select data for the given parameters
    //
    // SelectorBuilder<String[]> selectorBuilder = file.getSelectorBuilder().field(VARIABLES.SUPPRESSION.val).equals(suppression).and()
    // .field(VARIABLES.METRIC.val).equals(metric.getName()).and()
    // .field(VARIABLES.CRITERIA.val).equals(scriteria).and()
    // .begin();
    //
    // for (int i = 0; i < datasets.length; ++i) {
    // selectorBuilder.field(VARIABLES.DATASET.val).equals(datasets[i].toString());
    // if (i < datasets.length - 1) {
    // selectorBuilder.or();
    // }
    // }
    //
    // Selector<String[]> selector = selectorBuilder.end().build();
    //
    // // Create series.
    // // Note that actually no aggregation using the BufferedGeometricMeanAnalyzer is performed
    // // because by definition of selector each combination of x and y coordinates is unique
    // // and thus only one z coordinate is being encountered.
    // Series3D series = new Series3D(file, selector,
    // new Field(focus),
    // new Field(VARIABLES.METRIC.val),
    // new Field(variable, measure),
    // new BufferedGeometricMeanAnalyzer());
    //
    // return series;
    // }
}
