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
	
    private static String[] attrProps = new String[] {COLUMNS.FREQ_DEVI.toString(), COLUMNS.ENTROPY.toString()};

    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {
    	generateCriteriaSettingsPlots("analysis_loss_c4_l3_multiPage.pdf", false);
    	generateCriteriaSettingsPlots("analysis_loss_c4_l3_multiCol.pdf" , true);
    	System.out.println("done.");
    }
    
    /**
     * Generate the plots
     * @param pdfFileName TODO
     * @param condensed TODO
     * @throws IOException
     * @throws ParseException
     */
    private static void generateCriteriaSettingsPlots(String pdfFileName, boolean condensed) throws IOException, ParseException {
        
        CSVFile file = new CSVFile(new File("results/results.csv"));        

        String col1, col2, col3, col4;
        col1 = "'red'";
        col2 = "'green'";
        col3 = "'blue'";
        col4 = "'magenta'";        	
        
        double xOffset  = 0.42;
        double xSpacing = 0.03;
        double yOffset  = 0.96;

        String gnuPlotFileName = "results/commads.plg";
        String pdfFilePath = "results/" + pdfFileName;
        
        
        
        PrintWriter commandWriter = new PrintWriter(gnuPlotFileName, "UTF-8");
        commandWriter.println("set term pdf enhanced font ',5'");
        commandWriter.println("set output \"" + pdfFilePath + "\"");
        commandWriter.println("set datafile separator \";\"");
        commandWriter.println("set grid");

    	commandWriter.println("set label '1 QI'  at screen " + (xOffset + (0d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col1);
    	commandWriter.println("set label '2 QIs' at screen " + (xOffset + (1d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col2);
    	commandWriter.println("set label '3 QIs' at screen " + (xOffset + (2d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col3);
    	commandWriter.println("set label '4 QIs' at screen " + (xOffset + (3d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col4);
    	
        if (condensed) {        	
        	commandWriter.println("set multiplot title 'Loss / recursive-(c,l)-diversity mit c=4, l=3'");
        	commandWriter.println("set size 0.5,0.5");
        }
        
    	commandWriter.println("set style line 1 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col1);
    	commandWriter.println("set style line 2 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col2);
    	commandWriter.println("set style line 3 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col3);
    	commandWriter.println("set style line 4 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col4);
        
        commandWriter.println("set yrange [0:1]");


        for (String attrProp : attrProps) {
        	for (double suppFactor : BenchmarkSetup.getSuppressionFactors()){
        	String suppFactorString = String.valueOf(suppFactor);
        	String measure  = COLUMNS.SOLUTION_RATIO.toString();
                commandWriter.println();
                if (condensed) {
                	String originX = suppFactor == 0d ? "0.0" : "0.5";
                	String originY;
                	String xRange;
                	if (COLUMNS.FREQ_DEVI.toString().equals(attrProp)) {
                		originY = "0.5";
                		xRange = "[0:0.4]";
                	} else {
                		originY = "0.0";
                		xRange = "[0:1]";
                	}
                	commandWriter.println("set xrange " + xRange);
                	String origin = originX + "," + originY;
                    commandWriter.println("set origin " + origin);
                }
                commandWriter.println("set title 'suppression: " + suppFactorString + "'");
                commandWriter.println("set xlabel \"" + attrProp + "\"");
                commandWriter.println("set ylabel \"" + measure + "\"");
                for (int numQis = 1; numQis <= 4; numQis++) {
                    String lineStyle = "ls " + String.valueOf(numQis);
                    Series2D _series = getSeries(file, suppFactorString, attrProp, measure, numQis, "[lr]");

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
        if (condensed)
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
     * @param criterion TODO
     * @return
     */
    private static Series2D getSeries(CSVFile file, String suppFactor, String attrProp, String measure, int numQis, String criterion) {
        Selector<String[]> selector = null;
        try {
            selector = file.getSelectorBuilder()
                           .field(BenchmarkSetup.COLUMNS.CRITERIA.toString()).equals(criterion).and()
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
