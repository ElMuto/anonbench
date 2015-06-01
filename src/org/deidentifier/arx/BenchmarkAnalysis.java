/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal 
 *      methods for the de-identification of biomedical data"
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
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;

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
    
    /** The variables */
    private static final String[] VARIABLES = {"Number of checks", "Number of rollups", "Execution time"};

    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {

//        generateTables();
//        generatePlots();
        printFlashComparisonTableTexCode();
    }
    
    private static void printFlashComparisonTableTexCode() throws IOException, ParseException {
        CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));



       BenchmarkDataset[] datasets = BenchmarkSetup.getDatasets();
       
       String[] criteriaLabels = new String[BenchmarkSetup.getCriteria().length];
       int i= 0;
       for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
           criteriaLabels[i++] = Arrays.toString(criteria);
       }
       System.out.println("done");

//       if (suppressions.length == 0 || algorithms.size() == 0 || datasets.length == 0) {
//           return;
//       }
//
//       // for each criteria
//       for (String scriteria : criteria) {
//
//           // for each metric
//           for (Metric<?> metric : metrics) {
//
//               System.out.println("\\begin{table}[htb!]");
//               System.out.println("\\center");
//               System.out.println("\\footnotesize");
//               System.out.println("\\tabcolsep 2pt");
//               System.out.println("\\begin{tabular}{|l|r|r|r|r|r|r|r|r|}\\hhline{~--------}");
//               System.out.println("\\multicolumn{1}{c}{} &  \\multicolumn{4}{c}{0\\% Suppression limit} & \\multicolumn{4}{c}{100\\% Suppression limit}\\\\\\hhline{~--------}");
//               System.out.println("Dataset & Flash [s] & Total [s] & Discovery [s] & Utility [\\%] & Flash [s] & Total [s] & Discovery [s] & Utility [\\%] \\\\\\hline");
//
//               // for each dataset
//               for (BenchmarkDataset dataset : datasets) {
//
//                   Selector<String[]> selector = file.getSelectorBuilder()
//                                                     .field(VARIABLES.DATASET.val)
//                                                     .equals(dataset.toString())
//                                                     .and()
//                                                     .field(VARIABLES.CRITERIA.val)
//                                                     .equals(scriteria)
//                                                     .and()
//                                                     .field(VARIABLES.METRIC.val)
//                                                     .equals(metric.getName())
//                                                     .build();
//
//                   String flashRuntimeZero = null;
//                   String heuraklesTotalTimeZero = null;
//                   String discoveryTimeZero = null;
//                   String utilityZero = null;
//                   String flashRuntimeFull = null;
//                   String heuraklesTotalTimeFull = null;
//                   String discoveryTimeFull = null;
//                   String utilityFull = null;
//
//                   Iterator<CSVLine> iter = file.iterator();
//                   while (iter.hasNext()) {
//                       CSVLine csvline = iter.next();
//                       String[] line = csvline.getData();
//
//                       if (selector.isSelected(line)) {
//                           if (csvline.get("", VARIABLES.ALGORITHM.val).equals("Flash")) {
//                               if (csvline.get("", VARIABLES.SUPPRESSION.val).equals("0.0")) {
//                                   flashRuntimeZero = csvline.get(VARIABLES.EXECUTION_TIME.val, "Arithmetic Mean");
//                               } else {
//                                   flashRuntimeFull = csvline.get(VARIABLES.EXECUTION_TIME.val, "Arithmetic Mean");
//                               }
//                           } else {
//                               if (csvline.get("", VARIABLES.SUPPRESSION.val).equals("0.0")) {
////                                   heuraklesTotalTimeZero = csvline.get(VARIABLES.EXHAUSTIVE_SEARCH_TIME.val, "Arithmetic Mean");
//                                   discoveryTimeZero = csvline.get(VARIABLES.SOLUTION_DISCOVERY_TIME.val, "Arithmetic Mean");
//                                   utilityZero = csvline.get(VARIABLES.INFORMATION_LOSS_ADDITIONAL.val, "Value");
//                               } else {
////                                   heuraklesTotalTimeFull = csvline.get(VARIABLES.EXHAUSTIVE_SEARCH_TIME.val, "Arithmetic Mean");
//                                   discoveryTimeFull = csvline.get(VARIABLES.SOLUTION_DISCOVERY_TIME.val, "Arithmetic Mean");
//                                   utilityFull = csvline.get(VARIABLES.INFORMATION_LOSS_ADDITIONAL.val, "Value");
//                               }
//                           }
//                       }
//                   }
//
//                   System.out.println(dataset.toString() + " & "
//                                      + (flashRuntimeZero == null ? "---" : round(Double.valueOf(flashRuntimeZero) / 1E9, 3)) + " & "
////                                      + (heuraklesTotalTimeZero == null ? "---" : round(Double.valueOf(heuraklesTotalTimeZero) / 1E9, 3))
//                                      + " & "
//                                      + (discoveryTimeZero == null ? "---" : round(Double.valueOf(discoveryTimeZero) / 1E9, 3)) + " & "
//                                      + (utilityZero == null ? "---" : 100d - Double.valueOf(utilityZero)) + " & "
//                                      + (flashRuntimeFull == null ? "---" : round(Double.valueOf(flashRuntimeFull) / 1E9, 3)) + " & "
////                                      + (heuraklesTotalTimeFull == null ? "---" : round(Double.valueOf(heuraklesTotalTimeFull) / 1E9, 3))
//                                      + " & "
//                                      + (discoveryTimeFull == null ? "---" : round(Double.valueOf(discoveryTimeFull) / 1E9, 3)) + " & "
//                                      + (utilityFull == null ? "---" : 100d - Double.valueOf(utilityFull)) + " \\\\");
//                   // System.out.println(dataset + "   & 0.054         &   2.183       & ---           & ---       & 1.169         &   2.009   & 0.077         & 100");
//                   // System.out.println("Cup     & 0.039         &   1.188       & ---           & ---       & 15.574        &   13.292  & 7.722         & 100 \\\\");
//                   // System.out.println("Fars    & 0.065         &   4.321       & ---           & ---       & 2.935         &   4.305   & 0.107         & 100 \\\\");
//                   // System.out.println("Atus    & 0.379         &   106.117     & ---           & ---       & 49.782        &   105.996 & 0.437         & 100 \\\\");
//                   // System.out.println("Ihis    & 1.284         &   55.469      & ---           & ---       & 43.449        &   56.915  & 2.829         & 100 \\\\");
//                   // System.out.println("SS13ACS & 0.196         &   $>$600      & 0.171         & 86.22     & 19.652        &   $>$600  & 0.023         & 100 \\\\\\hline");
//
//               }
//
//               System.out.println("\\end{tabular}");
//               System.out.println("\\caption{Comparison of Flash and Heurakles for " + criteria.toString() + " and " + metric.toString() +
//                                  " utility measure}");
//               System.out.println("\\label{tab:optimal_loss}");
//               System.out.println("\\end{table}");
//               System.out.println("");
//           }
//       }
   }
}
