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
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkSetup.COLUMNS;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Series2D;
import de.linearbits.subframe.io.CSVFile;

public class Compare_Difficulties extends GnuPlotter {
	
    private static String[]  attrProps = new String[] {COLUMNS.FREQ_DEVI.toString(), COLUMNS.NORM_ENTROPY.toString()};
    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {
    	generateDifficultyComparisonPlots("Plots_difficultyComparison.pdf");
    	System.out.println("done.");
    }
    
    /**
     * Generate the plots
     * @param pdfFileName TODO
     * @param condensed TODO
     * @throws IOException
     * @throws ParseException
     */
    private static void generateDifficultyComparisonPlots(String pdfFileName) throws IOException, ParseException {

    	CSVFile file = new CSVFile(new File("results/results.csv"));        

    	String col1, col2, col3, col4, col5;
    	col1 = "'red'";
    	col2 = "'green'";
    	col3 = "'blue'";
    	col4 = "'magenta'";      
    	col5 = "'black'";   

    	String pdfFilePath = "results/" + pdfFileName; 

    	PrintWriter commandWriter = new PrintWriter(gnuPlotFileName, "UTF-8");
    	commandWriter.println("set term pdf enhanced font ',5'");
    	commandWriter.println("set output \"" + pdfFilePath + "\"");
    	commandWriter.println("set datafile separator \";\"");
    	commandWriter.println("set grid");

    	String pointsFileName = "results/points_aggregatesDifficultyComparison.csv";
    	PrintWriter pointsWriter = new PrintWriter(pointsFileName, "UTF-8");
    	for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModels()) {
       		for (double suppFactor : BenchmarkSetup.getSuppressionFactors()){
    		fileBucket.add(new File(pointsFileName));
    		Series2D _series = getSeries(file, String.valueOf(suppFactor),
    				privacyModel.getCriterion().toString(), privacyModel.getK(), privacyModel.getC(), privacyModel.getL(), privacyModel.getT());
    		for (Point2D point : _series.getData()) {
    			pointsWriter.println(point.x + ";" + point.y);
    		}
    	}
    	}
    	pointsWriter.close();

        executeGnuplot(gnuPlotFileName);

        deleteFilesFromBucket();

    }

	private static Series2D getSeries(CSVFile file, String suppFactor, String criterion, Integer k, Double c, Integer l, Double t) {
    	String bracketedCriterionString = "[" + criterion + "]";
        Selector<String[]> selector = null;
        try {
            selector = file.getSelectorBuilder()
                           .field(BenchmarkSetup.COLUMNS.PARAM_K.toString()).equals(k != null ? String.valueOf(k) : "").and()
                           .field(BenchmarkSetup.COLUMNS.PARAM_C.toString()).equals(c != null ? String.valueOf(c) : "").and()
                           .field(BenchmarkSetup.COLUMNS.PARAM_L.toString()).equals(l != null ? String.valueOf(l) : "").and()
                           .field(BenchmarkSetup.COLUMNS.PARAM_T.toString()).equals(t != null ? String.valueOf(t) : "").and()
                           .field(BenchmarkSetup.COLUMNS.CRITERIA.toString()).equals(bracketedCriterionString).and()
                           .field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(suppFactor).and()
                           .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create series
//        Series2D series = new Series2D(file, selector, new Field(attrProp, "Value"), new Field(measure, "Value"));
        
		return null;
	}
}
