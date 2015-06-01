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
import java.util.Iterator;
import java.util.List;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkAlgorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup.VARIABLES;

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
import de.linearbits.subframe.io.CSVLine;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;
import de.linearbits.subframe.render.LaTeX;
import de.linearbits.subframe.render.PlotGroup;

public class BenchmarkAnalysis {

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

       
       String[] criteriaLabels = new String[BenchmarkSetup.getCriteria().length];
       int i= 0;
       for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
           criteriaLabels[i++] = Arrays.toString(criteria);
       }
       

	   i = 0;
	   String line=";";
	   for (String critLabel : criteriaLabels) {
		   line += critLabel;
		   if (i != (criteriaLabels.length - 1)) {
			   line += ";";
		   }
		   i++;
	   }
	   System.out.println(line);

       // for each dataset
       for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {
    	   Selector<String[]> selector = file.getSelectorBuilder()
    			   .field(org.deidentifier.arx.BenchmarkSetup.VARIABLES.DATASET.toString())
    			   .equals(dataset.toString())
    			   .build();

    	   // get min and max of the dataset
    	   Double minVal = null;
    	   Double maxVal = null;
    	   Iterator<CSVLine> iter = file.iterator();
    	   while (iter.hasNext()) {
    		   CSVLine csvline = iter.next();
    		   String[] csvLine = csvline.getData();

    		   if (selector.isSelected(csvLine)) {
    			   Double val = Double.valueOf(csvline.get(VARIABLES.INFO_LOSS.toString(), "Arithmetic Mean"));
    			   if (minVal == null || val < minVal) {
    				   minVal = val;
    			   }
    			   if (maxVal == null || val > maxVal) {
    				   maxVal = val;
    			   }
    		   }
    	   }
    	   
    	   line = dataset.toString();
    	   for (String critLabel : criteriaLabels) {
        	   selector = file.getSelectorBuilder()
        			   .field(org.deidentifier.arx.BenchmarkSetup.VARIABLES.DATASET.toString())
        			   .equals(dataset.toString())
        			   .and()
        			   .field(org.deidentifier.arx.BenchmarkSetup.VARIABLES.CRITERIA.toString())
        			   .equals(critLabel)
        			   .build();

        	   iter = file.iterator();
        	   while (iter.hasNext()) {
        		   CSVLine csvline = iter.next();
        		   String[] csvLine = csvline.getData();

        		   if (selector.isSelected(csvLine)) {
        			   Double val = Double.valueOf(csvline.get(VARIABLES.INFO_LOSS.toString(), "Arithmetic Mean"));
        			   Double normVal = (val - minVal) / (maxVal - minVal);
        			   if (normVal > 1.0 || normVal < 0.0)
        				   System.out.println("not in range");
        			   line += (";" + (new DecimalFormat("#.####")).format(normVal));
        		   }
        	   }
    	   }
    	   System.out.println(line);
       }
    }
}
