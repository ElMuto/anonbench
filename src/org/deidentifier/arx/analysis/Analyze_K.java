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

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedGeometricMeanAnalyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.Plot;
import de.linearbits.subframe.graph.PlotLinesClustered;
import de.linearbits.subframe.graph.Point3D;
import de.linearbits.subframe.graph.Series3D;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.LaTeX;
import de.linearbits.subframe.render.PlotGroup;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;

public class Analyze_K {

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

        List<PlotGroup> groups = new ArrayList<PlotGroup>();

        for (BenchmarkSetup.BenchmarkMeasure measure : BenchmarkSetup.getMeasures()) {
            for (double suppFactor : BenchmarkSetup.getSuppressionFactors()){
                groups.add(getGroup(file, measure.toString(), String.valueOf(suppFactor), BenchmarkSetup.COLUMNS.UTILITY_VALUE.toString(), BenchmarkCriterion.K_ANONYMITY.toString()));
            }
        }
        LaTeX.plot(groups, "results/results");
    }
    


    /**
     * Returns a plot group
     * @param file
     * @param focus
     * @return
     * @throws ParseException 
     */
    private static PlotGroup getGroup(CSVFile file, String measure, String suppFactor, String value_type, String focus) throws ParseException {

        // Prepare
        List<Plot<?>> plots = new ArrayList<Plot<?>>();
        Series3D series = null;

        // Collect data for all datasets
        for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {

            Series3D _series = getSeries(file, measure, suppFactor, dataset.toString(), value_type, focus);
            if (series == null) series = _series;
            else series.append(_series);
        }

        // Create plot
        plots.add(new PlotLinesClustered("",
                                             new Labels(focus, value_type.toLowerCase()),
                                             series));

        // Define params
        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = -90;
        params.printValues = false;
        params.size = 1.5d;
        params.minY = 0.001d;
        params.maxY = getMax(series, 0);
        params.printValuesFormatString= "%.2f";
        params.keypos = KeyPos.AT(50, params.maxY * 1.1d, "horiz bot center");        
        params.logY = false;
        params.ratio = 0.3d;
        params.colorize = true;
        params.lineStyle = GnuPlotParams.LineStyle.STEPS;
        
        // Return
        return new PlotGroup(value_type.toLowerCase() + " for measure \""+measure+"\" and suppression factor " + suppFactor, plots, params, 1.0d);
    }

    /**
     * Returns a maximum for the given series
     * @param series
     * @param offset Additional exponent
     * @return
     */
    private static double getMax(Series3D series, int offset) {
        
        double max = -Double.MAX_VALUE;
        for (Point3D point : series.getData()) {
            max = Math.max(Double.valueOf(point.z), max);
        }

        return Double.valueOf(Math.ceil(max)).doubleValue();
    }

    /**
     * Returns a series
     * 
     * @param file
     * @param dataset
     * @param focus
     * @return
     * @throws ParseException
     */
    private static Series3D getSeries(CSVFile file,
                                      String measure,
                                      String suppFactor,
                                      String dataset,
                                      String variable,
                                      String focus) throws ParseException {

        // Select data for the given algorithm
        Selector<String[]> selector = file.getSelectorBuilder()
                                          .field(BenchmarkSetup.COLUMNS.UTLITY_MEASURE.toString()).equals(measure).and()
                                          .field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(suppFactor).and()
                                          .field(BenchmarkSetup.COLUMNS.DATASET.toString()).equals(dataset)
                                          .build();

        // Create series
        Series3D series = new Series3D(file, selector, 
                                       new Field(focus),
                                       new Field(BenchmarkSetup.COLUMNS.DATASET.toString()),
                                       new Field(variable, Analyzer.ARITHMETIC_MEAN),
                                       new BufferedGeometricMeanAnalyzer());
        
        return series;
    }
}
