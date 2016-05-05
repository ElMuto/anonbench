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

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.BenchmarkSetup.COLUMNS;

import de.linearbits.objectselector.Selector;
import de.linearbits.objectselector.SelectorBuilder;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Point2D;
import de.linearbits.subframe.graph.Series2D;
import de.linearbits.subframe.io.CSVFile;

public class Analyze_InfluenceOfSingleAttrOnDiff extends GnuPlotter {

    private static String[]  attrProps  = new String[] {COLUMNS.FREQ_DEVI.toString(), COLUMNS.FREQ_SPAN.toString()};
    private static BenchmarkMeasure[]  ilMeasures = new BenchmarkMeasure[] {BenchmarkMeasure.LOSS, BenchmarkMeasure.AECS};
    private static final String correlationsFile = "results/correlations.csv"; 
    
    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {

        
        PrintWriter correlationsWriter = new PrintWriter (correlationsFile, "UTF-8");
        correlationsWriter.println("Criterion;QiNum;ILMeasure;SF;attrProp;SpearmanCC");
        
    	for (BenchmarkMeasure ilMeasure : ilMeasures) {
    		generateDifficultyInfluencePlots("Plots_influenceOfSingleAttrOnDifficulty" + ilMeasure.toString() + ".pdf", null, true, ilMeasure, correlationsWriter);
    	}
    	

        correlationsWriter.close();
    	System.out.println("done.");
    }
    
 
    /**
     * @param pdfFileName
     * @param expType "Class A" or "Class B" experiment
     * @param condensed
     * @param ilMeasure TODO
     * @param correlationsWriter TODO
     * @throws IOException
     * @throws ParseException
     */
    static void generateDifficultyInfluencePlots(String pdfFileName, String expType, boolean condensed, BenchmarkMeasure ilMeasure, PrintWriter correlationsWriter) throws IOException, ParseException {
        
        CSVFile resultsFile = new CSVFile(new File("results/results.csv"));
        

        String col1, col2, col3, col4, col5;
        col1 = "'red'";
        col2 = "'green'";
        col3 = "'blue'";
        col4 = "'magenta'";      
        col5 = "'black'";        	
        
        double xOffset  = 0.42;
        double xSpacing = 0.03;
        double yOffset  = 0.96;
        
        String pdfFilePath = "results/" + pdfFileName; 
        
        PrintWriter commandWriter = new PrintWriter(gnuPlotFileName, "UTF-8");
        fileBucket.add(new File(gnuPlotFileName));
        
        commandWriter.println("set term pdf enhanced font ',5'");
        commandWriter.println("set output \"" + pdfFilePath + "\"");
        commandWriter.println("set datafile separator \";\"");
        commandWriter.println("set grid");

        commandWriter.println("set label '1 QI'  at screen " + (xOffset + (0d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col1);
        commandWriter.println("set label '2 QIs' at screen " + (xOffset + (1d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col2);
        commandWriter.println("set label '3 QIs' at screen " + (xOffset + (2d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col3);
        commandWriter.println("set label '4 QIs' at screen " + (xOffset + (3d * xSpacing)) + ", screen " + yOffset + " textcolor rgb " + col4);

        for (PrivacyModel privacyModel : BenchmarkSetup.getSaBasedPrivacyModels()) {
        	System.out.println("Processing " + privacyModel);
        	if (condensed) {        	
        		commandWriter.println("set multiplot title '"+ privacyModel + "'");
        		commandWriter.println("set size 0.5,0.5");
        	}

        	commandWriter.println("set style line 1 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col1);
        	commandWriter.println("set style line 2 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col2);
        	commandWriter.println("set style line 3 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col3);
        	commandWriter.println("set style line 4 lt 2 lw 2 pt 3 ps 0.05 lc rgb " + col4);
        	commandWriter.println("set style line 5 lt 2 lw 1 pt 3 ps 0.05 lc rgb " + col5);

        	commandWriter.println("set yrange [0:1]");


        	for (String attrProp : attrProps) {
        		for (double suppFactor : BenchmarkSetup.getSuppressionFactors()){
        			String suppFactorString = String.valueOf(suppFactor);
        			String difficultyMeasure  = COLUMNS.DIFFICULTY.toString();
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
        			commandWriter.println("set title '" + BenchmarkSetup.getSuppressionConfigString(suppFactor) + "'");
        			commandWriter.println("set xlabel \"" + attrProp + "\"");
        			commandWriter.println("set ylabel \"" + difficultyMeasure + "\"");
        			String pointsFileName = "results/points_" + privacyModel.toString() + "_" +
        					"suppr" + suppFactorString + " attrProp" + attrProp + ".csv";
        			PrintWriter pointsWriter = new PrintWriter(pointsFileName, "UTF-8");
        			fileBucket.add(new File(pointsFileName));
        			for (int numQis = 1; numQis <= 4; numQis++) {
        				String lineStyle = "ls " + String.valueOf(numQis);
        				Series2D _series = getSeriesForSingleQiCount(resultsFile, suppFactorString, attrProp, difficultyMeasure, numQis, privacyModel.getCriterion().toString(),
        						privacyModel.getK(), privacyModel.getC(), privacyModel.getL(), privacyModel.getT(), expType, ilMeasure);
           		        calcSpearman(correlationsWriter, String.valueOf(suppFactor), attrProp, _series, privacyModel.toString(), String.valueOf(numQis), ilMeasure.toString());

        				String qiSpecificPointsFileName = "results/points_" + privacyModel.toString() + "_" +
        						"suppr" + suppFactorString + " attrProp" + attrProp +
        						" numQis" + numQis + ".csv";
        				fileBucket.add(new File(qiSpecificPointsFileName));
        				PrintWriter qiSpecificPointsWriter = new PrintWriter(qiSpecificPointsFileName, "UTF-8");
        				for (Point2D point : _series.getData()) {
        					qiSpecificPointsWriter.println(point.x + ";" + point.y);
        					pointsWriter.println(point.x + ";" + point.y);
        				}
        				qiSpecificPointsWriter.close();
        				commandWriter.println("plot '" + qiSpecificPointsFileName + "' " + lineStyle + " notitle");
        			}
       				Series2D _seriesAllQis = getSeriesForAllQiCounts(resultsFile, correlationsWriter, suppFactorString, attrProp, difficultyMeasure,
    						privacyModel.getCriterion().toString(), privacyModel.getK(), privacyModel.getC(), privacyModel.getL(), privacyModel.getT(), expType, ilMeasure);

       		        
       		        calcSpearman(correlationsWriter, String.valueOf(suppFactor), attrProp, _seriesAllQis, privacyModel.toString(), "all", ilMeasure.toString());

        			pointsWriter.close();
        			commandWriter.println("f(x) = m*x + b");
        			commandWriter.println("fit f(x) '" + pointsFileName + "' using 1:2 via m,b");
        			commandWriter.println("plot f(x) title 'Line Fit' ls 5");

        		}
        	}
        if (condensed)
        	commandWriter.println("unset multiplot");
    	}
        commandWriter.close();
        
        executeGnuplot(gnuPlotFileName);
        
        deleteFilesFromBucket();
    }


    /**
     * @param file
     * @param suppFactor
     * @param xAxis
     * @param yAxis
     * @param numQis
     * @param criterion
     * @param k
     * @param c
     * @param l
     * @param t
     * @param expType
     * @param ilMeasure
     * @return
     */
    protected static Series2D getSeriesForSingleQiCount(CSVFile file, String suppFactor, String xAxis, String yAxis, int numQis, String criterion,
    		Integer k, Double c, Integer l, Double t, String expType, BenchmarkMeasure ilMeasure) {
    	String bracketedCriterionString = "[" + criterion + "]";
        Selector<String[]> selector = null;
        try {
        	
        	SelectorBuilder<String[]> preSelectorBuilder = file.getSelectorBuilder()
                    .field(BenchmarkSetup.COLUMNS.PARAM_K.toString()).equals(k != null ? String.valueOf(k) : "").and()
                    .field(BenchmarkSetup.COLUMNS.PARAM_C.toString()).equals(c != null ? String.valueOf(c) : "").and()
                    .field(BenchmarkSetup.COLUMNS.PARAM_L.toString()).equals(l != null ? String.valueOf(l) : "").and()
                    .field(BenchmarkSetup.COLUMNS.PARAM_T.toString()).equals(t != null ? String.valueOf(t) : "").and()
                    .field(BenchmarkSetup.COLUMNS.CRITERIA.toString()).equals(bracketedCriterionString).and()
                    .field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(suppFactor).and()
                    .field(BenchmarkSetup.COLUMNS.IL_MEASURE.toString()).equals(ilMeasure.toString()).and()
                    .field(BenchmarkSetup.COLUMNS.NUM_QIS.toString()).equals(String.valueOf(numQis));
        	
        	SelectorBuilder<String[]> selectorBuilder = null;
        	if (expType != null) {
        		selectorBuilder = preSelectorBuilder.and().field(BenchmarkSetup.COLUMNS.EXP_TYPE.toString()).equals(expType);
        	} else {
        		selectorBuilder = preSelectorBuilder;
        	}
        	selector = selectorBuilder.build();
        	
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create series
        Series2D series = new Series2D(file, selector, new Field(xAxis, "Value"), new Field(yAxis, "Value"));
        
        return series;
    }
    
    protected static Series2D getSeriesForAllQiCounts(CSVFile resultsFile, PrintWriter correlationsWriter, String suppFactor, String attrProp, String yAxis,
    		String criterion, Integer k, Double c, Integer l, Double t, String expType, BenchmarkMeasure ilMeasure) {
    	String bracketedCriterionString = "[" + criterion + "]";
        Selector<String[]> selector = null;
        try {
        	
        	SelectorBuilder<String[]> preSelectorBuilder = resultsFile.getSelectorBuilder()
                    .field(BenchmarkSetup.COLUMNS.PARAM_K.toString()).equals(k != null ? String.valueOf(k) : "").and()
                    .field(BenchmarkSetup.COLUMNS.PARAM_C.toString()).equals(c != null ? String.valueOf(c) : "").and()
                    .field(BenchmarkSetup.COLUMNS.PARAM_L.toString()).equals(l != null ? String.valueOf(l) : "").and()
                    .field(BenchmarkSetup.COLUMNS.PARAM_T.toString()).equals(t != null ? String.valueOf(t) : "").and()
                    .field(BenchmarkSetup.COLUMNS.CRITERIA.toString()).equals(bracketedCriterionString).and()
                    .field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(suppFactor).and()
                    .field(BenchmarkSetup.COLUMNS.IL_MEASURE.toString()).equals(ilMeasure.toString());
        	
        	SelectorBuilder<String[]> selectorBuilder = null;
        	if (expType != null) {
        		selectorBuilder = preSelectorBuilder.and().field(BenchmarkSetup.COLUMNS.EXP_TYPE.toString()).equals(expType);
        	} else {
        		selectorBuilder = preSelectorBuilder;
        	}
        	selector = selectorBuilder.build();
        	
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create series
        Series2D series = new Series2D(resultsFile, selector, new Field(attrProp, "Value"), new Field(yAxis, "Value"));
        
        return series;
    }


	/**
	 * @param correlationsWriter
	 * @param suppFactor
	 * @param attrProp
	 * @param series
	 * @param criterion
	 * @param qiNum
	 * @param ilMeasure TODO
	 */
	private static void calcSpearman(PrintWriter correlationsWriter, String suppFactor, String attrProp,
			Series2D series, String criterion, String qiNum, String ilMeasure) {
		int seriesLength = series.getData().size();
        
        double []x = new double[seriesLength];
        double []y = new double[seriesLength];
        
        for (int i = 0; i < seriesLength; i++) {
        	x[i] = Double.parseDouble(series.getData().get(i).x);
        	y[i] = Double.parseDouble(series.getData().get(i).y);
        }
        
        correlationsWriter.println(criterion + ";" + qiNum + ";" +  ilMeasure + ";" + suppFactor + ";" + attrProp + ";" + new SpearmansCorrelation().correlation(x, y));
	}
}
