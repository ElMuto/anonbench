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

import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.BenchmarkSetup.COLUMNS;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Series2D;
import de.linearbits.subframe.io.CSVFile;

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

        String col1 = "'red'";
        String col2 = "'green'";
        String col3 = "'blue'";
        String col4 = "'magenta'";
        
        double yOffset = 0.42;
        double ySpacing = 0.03;

        String gnuPlotFileName = "results/commads.plg";
        String pdfFileName = "results/analysis_loss_c4_l3_colQiCount.pdf";
        
        
        
        PrintWriter commandWriter = new PrintWriter(gnuPlotFileName, "UTF-8");
        commandWriter.println("set term pdf enhanced font ',5'");
        commandWriter.println("set output \"" + pdfFileName + "\"");
        commandWriter.println("set datafile separator \";\"");
        commandWriter.println("set grid");
        commandWriter.println("set style line 1 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col1);
        commandWriter.println("set style line 2 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col2);
        commandWriter.println("set style line 3 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col3);
        commandWriter.println("set style line 4 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col4);
        commandWriter.println("set multiplot title 'Loss / recursive-(c,l)-diversity mit c=4, l=3'");
        commandWriter.println("set size 0.4,0.4");
        commandWriter.println("set yrange [0:1]");
        
        commandWriter.println("set label '1 QI'  at screen " + (yOffset + (0d * ySpacing)) + ", screen 0.95 textcolor rgb " + col1);
        commandWriter.println("set label '2 QIs' at screen " + (yOffset + (1d * ySpacing)) + ", screen 0.95 textcolor rgb " + col2);
        commandWriter.println("set label '3 QIs' at screen " + (yOffset + (2d * ySpacing)) + ", screen 0.95 textcolor rgb " + col3);
        commandWriter.println("set label '4 QIs' at screen " + (yOffset + (3d * ySpacing)) + ", screen 0.95 textcolor rgb " + col4);


        for (double suppFactor : BenchmarkSetup.getSuppressionFactors()){
            String suppFactorString = String.valueOf(suppFactor);
            for (String attrProp : new String[] {COLUMNS.ENTROPY.toString(), COLUMNS.FREQ_DEVI.toString()}) {
                String measure  = COLUMNS.SOLUTION_RATIO.toString();
                commandWriter.println();              
                String originX = suppFactor == 0d ? "0.0" : "0.5";
                String originY;
                String xRange;
                if (COLUMNS.FREQ_DEVI.toString().equals(attrProp)) {
                    originY = "0.5";
                    xRange = "[0:0.4]";
                } else {
                    originY = "0.0";
                    xRange = "[0:5.5]";
                }
                commandWriter.println("set xrange " + xRange);
                String origin = originX + "," + originY;
                commandWriter.println("set title 'suppression: " + suppFactorString + "'");
                commandWriter.println("set origin " + origin);
                commandWriter.println("set xlabel \"" + attrProp + "\"");
                commandWriter.println("set ylabel \"" + measure + "\"");
                for (int numQis = 1; numQis <= 4; numQis++) {
                    String lineStyle = "ls " + String.valueOf(numQis);
                    Series2D _series = getSeries(file, suppFactorString, attrProp, measure, numQis);

                    String pointsFileName = "results/points suppr" + suppFactorString + " attrProp" + attrProp +
                            " measure" + measure + " numQis" + numQis + ".csv";
                    PrintWriter pointsWriter = new PrintWriter(pointsFileName, "UTF-8");
                    for (Point2D point : _series.getData()) {
                        pointsWriter.println(point.x + ";" + point.y);
                    }
                    pointsWriter.close();
                    commandWriter.println("plot '" + pointsFileName + "' " + lineStyle + " notitle");
                }
            }
        }
        commandWriter.println("unset multiplot");
        commandWriter.close();

        ProcessBuilder b = new ProcessBuilder();
        Process p;
        if (new File(gnuPlotFileName).exists()) {
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
    
    /** return a a series of points, selected by the parameters supplied
     * @param file
     * @param suppFactor
     * @param attrProp
     * @param measure
     * @param numQis
     * @return
     */
    private static Series2D getSeries(CSVFile file, String suppFactor, String attrProp, String measure, int numQis) {
        Selector<String[]> selector = null;
        try {
            selector = file.getSelectorBuilder()
                                              .field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(suppFactor).and()
                                              .field(BenchmarkSetup.COLUMNS.NUM_QIS.toString()).equals(String.valueOf(numQis))
                                              .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create series
        Series2D series = new Series2D(file, selector, new Field(attrProp, "Value"), new Field(measure, "Value"));
        
        return series;
    }
}
