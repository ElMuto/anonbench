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

package org.deidentifier.arx.analysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.BenchmarkSetup.COLUMNS;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedGeometricMeanAnalyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.Plot;
import de.linearbits.subframe.graph.PlotLinesClustered;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Point3D;
import de.linearbits.subframe.graph.Series2D;
import de.linearbits.subframe.graph.Series3D;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.LaTeX;
import de.linearbits.subframe.render.PlotGroup;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;

public class Analyze_L {

    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {
    	generateCriteriaSettingsPlots();
    	System.out.println("done.");
    }
    
    /**
     * Generate the plots
     * @throws IOException
     * @throws ParseException
     */
    private static void generateCriteriaSettingsPlots() throws IOException, ParseException {
        
        CSVFile file = new CSVFile(new File("results/results.csv"));


//        for (double suppFactor : BenchmarkSetup.getSuppressionFactors()){
            for (double suppFactor : new Double[] { 0d }){
                String suppFactorString = String.valueOf(suppFactor);
                for (String attrProp : new String[] {COLUMNS.ENTROPY.toString()/*, COLUMNS.EFD_SCORE.toString(), COLUMNS.FREQ_DEVI.toString()*/}) {
                    for (String measure : new String[] {COLUMNS.SOLUTION_RATIO.toString()/*, COLUMNS.IL_REL_VALUE.toString()*/}) {
                        Series2D _series = getSeries(file, suppFactorString, attrProp, measure);
                        
                        String pointsFileName = "results/points suppr" + suppFactorString + " attrProp" + attrProp + " measure" + measure + ".csv";
                        PrintWriter writer = new PrintWriter(pointsFileName, "UTF-8");
                        for (Point2D point : _series.getData()) {
                            writer.println(point.x + ";" + point.y);
                        }
                        writer.close();

                        String gnuPlotFileName = "results/points suppr" + suppFactorString + " attrProp" + attrProp + " measure" + measure + ".plg";
                        String pdfFileName = "results/points suppr" + suppFactorString + " attrProp" + attrProp + " measure" + measure + ".pdf";
                        String titleString = "suppression: " + suppFactorString;
                        writer = new PrintWriter(gnuPlotFileName, "UTF-8");
                        writer.println("set term pdf enhanced font 'Verdana,6'");
                        writer.println("set output \"" + pdfFileName + "\"");
                        writer.println("set datafile separator \";\"");
                        writer.println("set xlabel \"" + attrProp + "\"");
                        writer.println("set ylabel \"" + measure + "\"");
                        writer.println("set grid");
                        writer.println("set title \"" + titleString + "\"");
                        writer.println("plot '" + pointsFileName + "' with dots notitle");
                        writer.close();

                        ProcessBuilder b = new ProcessBuilder();
                        Process p;
                        if (new File(pointsFileName).exists() && new File(gnuPlotFileName).exists()) {
                            b.command("gnuplot", gnuPlotFileName);
                            p = b.start();
                            StreamReader output = new StreamReader(p.getInputStream());
                            StreamReader error = new StreamReader(p.getErrorStream());
                            new Thread(output).start();
                            new Thread(error).start();
                            try {
                                p.waitFor();
                            } catch (final InterruptedException e) {
                                throw new IOException(e);
                            }

                            if (p.exitValue() != 0) {
                                throw new IOException("Error executing gnuplot: " + error.getString() + System.lineSeparator() + output.getString());
                            }
                        } else {
                            System.err.println("Files not existent");
                        }
                    }
                }
            }
    }
    private static Series2D getSeries(CSVFile file, String suppFactor, String attrProp, String measure) {
        // Select data for the given algorithm
        Selector<String[]> selector = null;
        try {
            selector = file.getSelectorBuilder()
                                              .field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(suppFactor)
                                              .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create series
        Series2D series = new Series2D(file, selector, new Field(attrProp, "Value"), new Field(measure, "Value"));
        
        return series;
    }
}
