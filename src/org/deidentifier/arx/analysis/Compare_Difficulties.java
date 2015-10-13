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
import java.text.ParseException;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.PrivacyModel;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.Plot;
import de.linearbits.subframe.graph.PlotHistogram;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Series2D;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.render.GnuPlot;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;

public class Compare_Difficulties extends GnuPlotter {


	
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

    	for (double sf : BenchmarkSetup.getSuppressionFactors()){
        	Series2D arithMeanSeries = new Series2D();
        	Series2D geomMeanSeries = new Series2D();
        	Series2D stdDevSeries = new Series2D();
    		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModels()) {
                // initialize stats package and read values
                DescriptiveStatistics stats = new DescriptiveStatistics();
    			Series2D _series = getSeries(file, BenchmarkDriver.assemblePrivacyModelString(privacyModel, sf));
    			List<Point2D> pointsList = _series.getData();
    			for (int i = 0; i < pointsList.size(); i++) {
                    try {
                        stats.addValue(Double.parseDouble(pointsList.get(i).y));
                    } catch (java.lang.NumberFormatException e) { /* just ignore those entries */ }
    			}

    			String pmString = privacyModel.toString().replaceAll("\\.0", "").replaceAll(" ", "");
    			
    			String arithMeanString = String.valueOf(stats.getMean());
    			String geomMeanString = String.valueOf(stats.getGeometricMean());
    			String stdDevString = String.valueOf(stats.getStandardDeviation());

    			arithMeanSeries.getData().add(new Point2D(pmString, arithMeanString));
    			geomMeanSeries.getData().add(new Point2D(pmString, geomMeanString));
    			stdDevSeries.getData().add(new Point2D(pmString, stdDevString));
    		}
    		Plot<?> arithMeanPlot = new PlotHistogram("Arithmetic mean SF " + sf, 
    				new Labels("Privacy Model", "Difficulty"),
    				arithMeanSeries);
    		Plot<?> geomMeanPlot = new PlotHistogram("Geometric mean SF " + sf, 
    				new Labels("Privacy Model", "Difficulty"),
    				geomMeanSeries);
    		Plot<?> stdDevPlot = new PlotHistogram("Standard deviation SF " + sf, 
    				new Labels("Privacy Model", "Difficulty"),
    				stdDevSeries);

    		//Render the plot
    		GnuPlotParams params = new GnuPlotParams();
    		params.rotateXTicks = -90;
    		params.keypos = KeyPos.TOP_LEFT;
    		params.size = 1d;
    		params.minY = 0d;
    		params.maxY = 1d;

    		GnuPlot.plot(arithMeanPlot, params, "results/" + "Arithmetic mean SF" + sf + " ComparedDifficulties");
    		GnuPlot.plot(geomMeanPlot,  params, "results/" + "Geometric mean SF" + sf + " ComparedDifficulties");
    		GnuPlot.plot(stdDevPlot,    params, "results/" + "Standard deviation SF" + sf + " ComparedDifficulties");
    	}
    	
    	// Geclustered nach datensatz
//    	Series3D series3d = new Series3D();
//    	series3d.getData().add(new Point3D("dataset", "x", "y"));
    	//PlotHistogramClustered
    }

	/**
	 * @param file
	 * @param privacyModelString
	 * @return
	 * 
	 */
	private static Series2D getSeries(CSVFile file, String privacyModelString) {
        Selector<String[]> selector = null;
        try {
            selector = file.getSelectorBuilder()
                           .field(BenchmarkSetup.COLUMNS.PRIVACY_MODEL.toString()).equals(privacyModelString)
                           .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create series
        Series2D series = new Series2D(	file, selector,
        								new Field("", BenchmarkSetup.COLUMNS.PRIVACY_MODEL.toString()),
        								new Field(BenchmarkSetup.COLUMNS.DIFFICULTY.toString(), Analyzer.VALUE));
        
		return series;
	}
}
