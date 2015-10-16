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
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.PrivacyModel;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.PlotHistogram;
import de.linearbits.subframe.graph.PlotHistogramClustered;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Point3D;
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
    	generateDifficultyComparisonPlots();
    	System.out.println("done.");
    }
    
    /**
     * Generate the plots
     * @param pdfFileName TODO
     * @param condensed TODO
     * @throws IOException
     * @throws ParseException
     */
    static void generateDifficultyComparisonPlots() throws IOException, ParseException {

    	CSVFile file = new CSVFile(new File("results/results.csv"));

    	for (double sf : BenchmarkSetup.getSuppressionFactors()){
    		System.out.println("\nGenerating stats for SF " + sf);
        	Series2D arithMeanSeries2D = new Series2D();
        	Series2D geomMeanSeries2D = new Series2D();
        	Series2D stdDevSeries2D = new Series2D();
			
        	Series3D arithMeanSeries3D = new Series3D();
        	Series3D geomMeanSeries3D = new Series3D();
        	Series3D stdDevSeries3D = new Series3D();
			
    		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModels()) {
    			String pmString = privacyModel.toString().replaceAll("\\.0", "").replaceAll(" ", "");
				
				for (BenchmarkDatafile df : BenchmarkSetup.getDatafiles()) {
					
					// get data
					Series2D _ClusterSeries = getClusteredSeries(file, BenchmarkDriver.assemblePrivacyModelString(privacyModel, sf), df.toString());
    			
					// initialize stats package
					DescriptiveStatistics stats3D = new DescriptiveStatistics();
					DescriptiveStatistics geoMeanStats3D = new DescriptiveStatistics();
					
					// read values into stats package
					List<Point2D> pointsList4Cluster = _ClusterSeries.getData();
					for (int i = 0; i < pointsList4Cluster.size(); i++) {
						try {
							stats3D.addValue(Double.parseDouble(pointsList4Cluster.get(i).y));
							geoMeanStats3D.addValue(Double.parseDouble(pointsList4Cluster.get(i).y) + 1d);
						} catch (java.lang.NumberFormatException e) { /* just ignore those entries */ }
					}

					// add aggregated values into data structure for plotting					
					arithMeanSeries3D.getData().add(new Point3D(pmString, df.toString(), String.valueOf(stats3D.getMean())));
					geomMeanSeries3D.getData().add(new Point3D (pmString, df.toString(), String.valueOf(geoMeanStats3D.getGeometricMean() - 1)));
					stdDevSeries3D.getData().add(new Point3D   (pmString, df.toString(), String.valueOf(stats3D.getStandardDeviation())));
				}
    			
    			// get data
    			Series2D _2Dseries = get2dSeries(file, BenchmarkDriver.assemblePrivacyModelString(privacyModel, sf));
    			
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
    		params.keypos = KeyPos.TOP_RIGHT;
    		params.size = 1d;
    		params.minY = 0d;
    		params.maxY = 1d;
    		params.colorize = true;
    		params.height = 4.4d;

    		System.out.println("Gnuplotting unclustered plots");
    		GnuPlot.plot(new PlotHistogram("Arithmetic mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), arithMeanSeries2D),
    				params, "results/" + "Plot_Arithmetic_mean_SF" + sf);
    		GnuPlot.plot(new PlotHistogram("Geometric mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), geomMeanSeries2D), 
    				params, "results/" + "Plot_Geometric_mean_SF" + sf);
    		GnuPlot.plot(new PlotHistogram("Standard deviation - SF " + sf, new Labels("Privacy Model", "Difficulty"), stdDevSeries2D),
    				params, "results/" + "Plot_Standard_deviation_SF" + sf);

    		System.out.println("Gnuplotting clustered plots");
    		GnuPlot.plot(new PlotHistogramClustered("Arithmetic mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), arithMeanSeries3D),
    				params, "results/" + "Plot_Clustered_Arithmetic_mean_SF" + sf);
    		GnuPlot.plot(new PlotHistogramClustered("Geometric mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), geomMeanSeries3D), 
    				params, "results/" + "Plot_Clustered_Geometric_mean_SF" + sf);
    		GnuPlot.plot(new PlotHistogramClustered("Standard deviation - SF " + sf, new Labels("Privacy Model", "Difficulty"), stdDevSeries3D),
    				params, "results/" + "Plot_Clustered_Standard_deviation_SF" + sf);
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
	private static Series2D getClusteredSeries(CSVFile file, String privacyModelString, String dsString) {
        Selector<String[]> selector = null;
        try {
            selector = file.getSelectorBuilder()
                    .field(BenchmarkSetup.COLUMNS.DATASET.toString()).equals(dsString).and()
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