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
import de.linearbits.subframe.graph.PlotHistogram;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Series2D;
import de.linearbits.subframe.graph.Series3D;
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
        	Series2D arithMeanSeries2D = new Series2D();
        	Series2D geomMeanSeries2D = new Series2D();
        	Series2D stdDevSeries2D = new Series2D();
    		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModels()) {
    			String pmString = privacyModel.toString().replaceAll("\\.0", "").replaceAll(" ", "");
    			
    			// get data
    			Series2D _2Dseries = get2dSeries(file, BenchmarkDriver.assemblePrivacyModelString(privacyModel, sf));
    			Series3D _3Dseries = get3dSeries(file, BenchmarkDriver.assemblePrivacyModelString(privacyModel, sf));
    			
                // initialize stats package
                DescriptiveStatistics stats2D = new DescriptiveStatistics();
                DescriptiveStatistics geoMeanStats2D = new DescriptiveStatistics();
                
                // read values into stats package
    			List<Point2D> pointsList = _2Dseries.getData();
    			for (int i = 0; i < pointsList.size(); i++) {
                    try {
                        stats2D.addValue(Double.parseDouble(pointsList.get(i).y));
                        geoMeanStats2D.addValue(Double.parseDouble(pointsList.get(i).y) + 1d);
                    } catch (java.lang.NumberFormatException e) { /* just ignore those entries */ }
    			}

    			// add aggregated values into data structure for plotting
    			
    			arithMeanSeries2D.getData().add(new Point2D(pmString, String.valueOf(stats2D.getMean())));
    			geomMeanSeries2D.getData().add(new Point2D(pmString, String.valueOf(geoMeanStats2D.getGeometricMean() - 1)));
    			stdDevSeries2D.getData().add(new Point2D(pmString, String.valueOf(stats2D.getStandardDeviation())));
    		}

    		//Render the plot
    		GnuPlotParams params = new GnuPlotParams();
    		params.rotateXTicks = -90;
    		params.keypos = KeyPos.TOP_LEFT;
    		params.size = 1d;
    		params.minY = 0d;
    		params.maxY = 1d;

    		GnuPlot.plot(new PlotHistogram("Arithmetic mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), arithMeanSeries2D),
    				params, "results/" + "Plot_Arithmetic_mean_SF" + sf);
    		GnuPlot.plot(new PlotHistogram("Geometric mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), geomMeanSeries2D), 
    				params, "results/" + "Plot_Geometric_mean_SF" + sf);
    		GnuPlot.plot(new PlotHistogram("Standard deviation - SF " + sf, new Labels("Privacy Model", "Difficulty"), stdDevSeries2D),
    				params, "results/" + "Plot_Standard_deviation_SF" + sf);
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
	private static Series2D get2dSeries(CSVFile file, String privacyModelString) {
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

	/**
	 * @param file
	 * @param privacyModelString
	 * @return
	 * 
	 */
	private static Series3D get3dSeries(CSVFile file, String privacyModelString) {
        Selector<String[]> selector = null;
        try {
            selector = file.getSelectorBuilder()
                           .field(BenchmarkSetup.COLUMNS.PRIVACY_MODEL.toString()).equals(privacyModelString)
                           .build();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create series
        Series3D series = new Series3D(	file, selector,
        								new Field("", BenchmarkSetup.COLUMNS.PRIVACY_MODEL.toString()),
        								new Field(BenchmarkSetup.COLUMNS.DIFFICULTY.toString(), Analyzer.VALUE),
        								new Field(BenchmarkSetup.COLUMNS.DATASET.toString()));
        
		return series;
	}
}
