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
import java.util.ArrayList;
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

public class Analyze_Compared_Difficulties extends GnuPlotter {


	
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
		List<PlotGroup> groups = new ArrayList<>();

    	for (double sf : BenchmarkSetup.getSuppressionFactors()){
    		System.out.println("\nGenerating stats for SF " + sf);
        	Series3D arithMeanSeriesUnclustered = new Series3D();
        	Series3D geomMeanSeriesUnclustered = new Series3D();
        	Series3D stdDevSeriesUnclustered = new Series3D();
			
        	Series3D arithMeanSeriesClustered = new Series3D();
        	Series3D geomMeanSeriesClustered = new Series3D();
        	Series3D stdDevSeriesClustered = new Series3D();
			
    		for (PrivacyModel privacyModel : BenchmarkSetup.getNon_K_PrivacyModels()) {
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
					arithMeanSeriesClustered.getData().add(new Point3D(df.toString(), pmString, String.valueOf(stats3D.getMean())));
					geomMeanSeriesClustered.getData().add(new Point3D (df.toString(), pmString, String.valueOf(geoMeanStats3D.getGeometricMean() - 1)));
					stdDevSeriesClustered.getData().add(new Point3D   (df.toString(), pmString, String.valueOf(stats3D.getStandardDeviation())));
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
    			arithMeanSeriesUnclustered.getData().add(new Point3D("AverageOverAllDatasets", pmString, String.valueOf(stats2D.getMean())));
    			geomMeanSeriesUnclustered.getData().add(new Point3D("AverageOverAllDatasets", pmString, String.valueOf(geoMeanStats2D.getGeometricMean() - 1)));
    			stdDevSeriesUnclustered.getData().add(new Point3D("AverageOverAllDatasets", pmString, String.valueOf(stats2D.getStandardDeviation())));
    		}

    		//Render the plot
    		GnuPlotParams clusteredParams = new GnuPlotParams();
    		GnuPlotParams unclusteredParams = new GnuPlotParams();
    		
    		unclusteredParams.rotateXTicks = clusteredParams.rotateXTicks = -00;
    		unclusteredParams.keypos = clusteredParams.keypos = KeyPos.OUTSIDE;
    		unclusteredParams.size = clusteredParams.size = 1d;
    		unclusteredParams.minY = clusteredParams.minY = 0d;
    		unclusteredParams.maxY = clusteredParams.maxY = 1d;
    		unclusteredParams.height = clusteredParams.height = 4.4d;
    		unclusteredParams.width = clusteredParams.width = 7.5d;
    		unclusteredParams.colorize = clusteredParams.colorize = true;
    		clusteredParams.colors                  = new String[] {
					 "B6EED8", "92CDDC", "31849B",									// distinct
                     "FDE9D9", "FBD4B4", "FABF8F", "F5801F", "C35D09", "984806",	// recursive
                     "E6EED6", "C2D69B", "76923C",									// entropy
                     "FAFFB3", "D8E617",											// equal
                     "B3E6A1", "5FE62E",											// hierarchical
                     "E6ACE6", "E68AE6", "E65CE6", "E617E6",						// k-anonymity
    		};
    		unclusteredParams.colors                  = new String[] {
					 "B6EED8", "92CDDC", "31849B",									// distinct
                     "FDE9D9", "FBD4B4", "FABF8F", "F5801F", "C35D09", "984806",	// recursive
                     "E6EED6", "C2D69B", "76923C",									// entropy
                     "FAFFB3", "D8E617",											// equal
                     "B3E6A1", "5FE62E",											// hierarchical
                     "E6ACE6", "E68AE6", "E65CE6", "E617E6",						// k-anonymity
    		};

    		
    		System.out.println("Gnuplotting unclustered plots");
    		List<Plot<?>> unclusteredPlots = new ArrayList<Plot<?>>();
    		unclusteredPlots.add(new PlotHistogramClustered("Arithmetic mean - SF " + sf, new Labels("", "Difficulty"), arithMeanSeriesUnclustered));
    		unclusteredPlots.add(new PlotHistogramClustered("Geometric mean - SF " + sf, new Labels("", "Difficulty"), geomMeanSeriesUnclustered));
    		unclusteredPlots.add(new PlotHistogramClustered("Standard deviation - SF " + sf, new Labels("", "Difficulty"), stdDevSeriesUnclustered));    		
    		groups.add(new PlotGroup("Unclustered plots for SF " + sf, unclusteredPlots, unclusteredParams, 0.7d));

    		System.out.println("Gnuplotting clustered plots");
    		List<Plot<?>> clusteredPlots = new ArrayList<Plot<?>>();
    		clusteredPlots.add(new PlotHistogramClustered("Arithmetic mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), arithMeanSeriesClustered));
    		clusteredPlots.add(new PlotHistogramClustered("Geometric mean - SF " + sf, new Labels("Privacy Model", "Difficulty"), geomMeanSeriesClustered));
    		clusteredPlots.add(new PlotHistogramClustered("Standard deviation - SF " + sf, new Labels("Privacy Model", "Difficulty"), stdDevSeriesClustered));
    		groups.add(new PlotGroup("Clustered plots for SF " + sf, clusteredPlots, clusteredParams, 0.7d));
    		
    		LaTeX.plot(groups, "results/Plots_compareDifficulties", false);
    	}
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