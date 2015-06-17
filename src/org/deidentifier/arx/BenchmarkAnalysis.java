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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.BenchmarkSetup;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.analyzer.Analyzer;
import de.linearbits.subframe.analyzer.buffered.BufferedGeometricMeanAnalyzer;
import de.linearbits.subframe.graph.Field;
import de.linearbits.subframe.graph.Function;
import de.linearbits.subframe.graph.Labels;
import de.linearbits.subframe.graph.Plot;
import de.linearbits.subframe.graph.PlotHistogramClustered;
import de.linearbits.subframe.graph.Point3D;
import de.linearbits.subframe.graph.Series3D;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;
import de.linearbits.subframe.render.GnuPlotParams;
import de.linearbits.subframe.render.LaTeX;
import de.linearbits.subframe.render.PlotGroup;
import de.linearbits.subframe.render.GnuPlotParams.KeyPos;

public class BenchmarkAnalysis {
        
    private static String FONT_SIZE_CAPTION="scriptsize";
//    private static String FONT_SIZE="tiny";
    private static String FONT_SIZE="scriptsize";
//	private static String FONT_SIZE="footnotesize";
//	private static String FONT_SIZE="small";
    
    private enum OutputFormat {
        LATEX (".tex", " & "),
        CSV   (".csv", ";");
        
        private final String suffix;
        private final String separator;
        
        private OutputFormat (String suffix, String separator) {
            this.suffix = suffix;
            this.separator = separator;
        }
        
        public String getSuffix() {
        	return this.suffix;
        }

        public String getSeparator() {
        	return separator;
        }
    }

    /**
     * Main
     * @param args
     * @throws IOException
     * @throws ParseException 
     */
    public static void main(String[] args) throws IOException, ParseException {
//    	analyzeInterCriteriaComparison();
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
                groups.add(getGroup(file, measure.toString(), String.valueOf(suppFactor), BenchmarkSetup.PLOT_VARIABLES.UTILITY_VALUE.toString(), BenchmarkCriterion.K_ANONYMITY.toString()));
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

        // Make sure labels are printed correctly 
        series.transform(new Function<Point3D>(){
            @Override
            public Point3D apply(Point3D t) {
                return new Point3D("\""+t.x+"\"", t.y, t.z);
            }
        });

        // Create plot
        plots.add(new PlotHistogramClustered("",
                                             new Labels(focus, value_type.toLowerCase()),
                                             series));

        // Define params
        GnuPlotParams params = new GnuPlotParams();
        params.rotateXTicks = 0;
        params.printValues = false;
        params.size = 1.5;
        if (value_type.equals(BenchmarkSetup.PLOT_VARIABLES.UTILITY_VALUE.toString())) {
            params.minY = 0.001d;
            params.maxY = getMax(series, 0);
            params.printValuesFormatString= "%.2f";
        }
        if (focus.equals(BenchmarkCriterion.K_ANONYMITY.toString())) {
            params.keypos = KeyPos.AT(5, params.maxY * 1.1d, "horiz bot center");
        } else {
            params.keypos = KeyPos.AT(2, params.maxY * 1.1d, "horiz bot center");
        }
        params.logY = false;
        params.ratio = 0.2d;
        params.colorize = true;
        
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
                                          .field(BenchmarkSetup.PLOT_VARIABLES.UTLITY_MEASURE.toString()).equals(measure).and()
                                          .field(BenchmarkSetup.PLOT_VARIABLES.SUPPRESSION_FACTOR.toString()).equals(suppFactor).and()
                                          .field(BenchmarkSetup.PLOT_VARIABLES.DATASET.toString()).equals(dataset)
                                          .build();

        // Create series
        Series3D series = new Series3D(file, selector, 
                                       new Field(focus),
                                       new Field(BenchmarkSetup.PLOT_VARIABLES.DATASET.toString()),
                                       new Field(variable, Analyzer.ARITHMETIC_MEAN),
                                       new BufferedGeometricMeanAnalyzer());
        
        return series;
    }

    private static void analyzeInterCriteriaComparison() throws IOException,
                                                        ParseException,
                                                        UnsupportedEncodingException,
                                                        FileNotFoundException {
        OutputFormat of = OutputFormat.LATEX;
    	//        OutputFormat of = OutputFormat.CSV;

    	try (Writer writer = new BufferedWriter(
    			new OutputStreamWriter(
    					new FileOutputStream(BenchmarkSetup.RESULTS_DIR + "/" + BenchmarkSetup.SUMMARY_FILE_STEM + of.getSuffix()), "utf-8"))) {
    		writer.write(summarizeCriteriaWithDifferentSuppressionValues(of));
    		writer.flush();
    		writer.close();
    	}

    	// convert Latex to PDF
    	if (OutputFormat.LATEX.equals(of)) {
    		ProcessBuilder b = new ProcessBuilder();
    		Process p;

    		if (new File(BenchmarkSetup.RESULTS_DIR + "/" + BenchmarkSetup.SUMMARY_FILE_STEM + ".tex").exists()) {
    			b.command("pdflatex", "-interaction=errorstopmode", "-quiet", "-output-directory=" + BenchmarkSetup.RESULTS_DIR + "/", BenchmarkSetup.SUMMARY_FILE_STEM + ".tex");
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
    				throw new IOException("Error executing pdflatex: " + error.getString() + System.lineSeparator() + output.getString());
    			}
    		}
    	}
    }

    private static String summarizeCriteriaWithDifferentSuppressionValues(OutputFormat of) throws IOException, ParseException {
    	String result = "";
    	CSVFile file = new CSVFile(new File(BenchmarkSetup.RESULTS_FILE));
    	
    	// build document header for Latex
    	result = buildLatexDocumentHeader(of, result);

    	// for each metric
    	for (BenchmarkMeasure metric : BenchmarkSetup.getMeasures()) {

    		// for each suppression factor
    		for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {

                result = printTable(of, result, file, BenchmarkSetup.getNonSubsetBasedCriteria(), metric, suppFactor, false);
                result = printTable(of, result, file, BenchmarkSetup.getSubsetBasedCriteria(), metric, suppFactor, true);
    		}
    	}
	   if (OutputFormat.LATEX == of) {
	       result += "\\end{document}\n";
	   }	   
	   return result;
    }

    private static String buildLatexDocumentHeader(OutputFormat of, String result) {
        if (OutputFormat.LATEX.equals(of)) {
    		result += "\\documentclass{article}\n";
    		result += "\\usepackage{diagbox}\n";
    		result += "\\usepackage[font=" + FONT_SIZE_CAPTION + "]{caption}\n";
    		result += "\\usepackage{geometry}\n";
    		result += "\\usepackage[table]{xcolor}\n";
    		result += "\\geometry{\n";
    		result += "a4paper,\n";
    		result += "total={210mm,297mm},\n";
    		result += "left=20mm,\n";
    		result += "right=20mm,\n";
    		result += "top=5mm,\n";
    		result += "bottom=5mm,\n";
    		result += "}\n";
    		result += "\\begin{document}\n";
    	}
        return result;
    }

	private static String printTable(OutputFormat of, String result,
			CSVFile file, BenchmarkCriterion[][] criteria,
			BenchmarkMeasure metric, double suppFactor, boolean subsetBased) throws ParseException {
	    
	    // construct the criteria labels used for selecting the relevant rows
	    String[] criteriaLabels = new String[criteria.length];
	    int i= 0;
	    for (BenchmarkCriterion[] criteriaSetup : criteria) {
	        criteriaLabels[i++] = Arrays.toString(criteriaSetup);
	    }
	    
		// print header-line
		result += buildTableHeader(of, criteriaLabels);

		// for each dataset
		for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {
			Selector<String[]> selector = file.getSelectorBuilder()
					.field(BenchmarkSetup.PLOT_VARIABLES.UTLITY_MEASURE.toString()).equals(String.valueOf(metric)).and()
					.field(BenchmarkSetup.PLOT_VARIABLES.SUPPRESSION_FACTOR.toString()).equals(String.valueOf(suppFactor)).and()
					.field(BenchmarkSetup.PLOT_VARIABLES.SUBSET_NATURE.toString()).equals(Boolean.toString(subsetBased)).and()
					.field(BenchmarkSetup.PLOT_VARIABLES.DATASET.toString()).equals(dataset.toString())
					.build();

			// get min and max of the dataset
			Double minVal = null;
			Double maxVal = null;
			Iterator<CSVLine> iter = file.iterator();
			while (iter.hasNext()) {
				CSVLine csvline = iter.next();
				String[] csvLine = csvline.getData();

				if (selector.isSelected(csvLine)) {
					Double val = Double.valueOf(csvline.get(BenchmarkSetup.PLOT_VARIABLES.UTILITY_VALUE.toString(), "Arithmetic Mean"));
					if (val != BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL) {
						if (minVal == null || val < minVal) {
							minVal = val;
						}
						if (maxVal == null || val > maxVal) {
							maxVal = val;
						}
					}
				}
			}

			// iterate over the different privacy criteria and fill cells
			String line = (dataset.toString());
			for (String critLabel : criteriaLabels) {

			    // build selector
				selector = file.getSelectorBuilder()
						.field(BenchmarkSetup.PLOT_VARIABLES.UTLITY_MEASURE.toString()).equals(String.valueOf(metric)).and()
						.field(BenchmarkSetup.PLOT_VARIABLES.SUPPRESSION_FACTOR.toString()).equals(String.valueOf(suppFactor)).and()
						.field(BenchmarkSetup.PLOT_VARIABLES.SUBSET_NATURE.toString()).equals(Boolean.toString(subsetBased)).and()
						.field(BenchmarkSetup.PLOT_VARIABLES.DATASET.toString()).equals(dataset.toString()).and()
						.field(BenchmarkSetup.PLOT_VARIABLES.CRITERIA.toString()).equals(critLabel)
						.build();

				// find the entry in the CSV file, that we need to fill the cell
				iter = file.iterator();
				while (iter.hasNext()) {
					CSVLine csvline = iter.next();
					String[] csvLine = csvline.getData();

					if (selector.isSelected(csvLine)) {
						Double val = Double.valueOf(csvline.get(BenchmarkSetup.PLOT_VARIABLES.UTILITY_VALUE.toString(), "Arithmetic Mean"));
						Double normVal = val != BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL ? (val - minVal) / (maxVal - minVal) : BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL;
						String normString = normVal != BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL ? new DecimalFormat("0.0000").format(normVal): BenchmarkSetup.NO_SOULUTION_FOUND_STRING_VAL;
						String colorCode = "";
						if (OutputFormat.LATEX.equals(of)) { // color formatting of cells
							if (val.equals(minVal)) {
								colorCode = "\\cellcolor{green!25}";
							} else if (val.equals(maxVal)) {
								colorCode = "\\cellcolor{red!25}";
							}else if (normVal.equals(BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL)) {
								colorCode = "\\cellcolor{black!50}";
							}
							normString = colorCode + normString;
						}
						line += (of.getSeparator() + normString);
					}
				}
			}
			result += ((OutputFormat.LATEX.equals(of) ? line + " \\\\ \\hline" : line) + "\n");
		}

		if (OutputFormat.LATEX == of) {  // print table footer
			result += buildLatexTableFooter(metric.toString(), new DecimalFormat("###").format(suppFactor * 100));
		}
		return result;
	}
    
    private static String buildTableHeader(OutputFormat of, String[] criteriaLabels) {
        String separString = of.getSeparator();
        String header = "";
        
        if (OutputFormat.LATEX == of) {
            header += "\\begin{center}\n";
            header += "\\begin{table}[htb!]\n";
            header += "\\begin{" + FONT_SIZE + "}\n";
            header += "\\begin{tabular}{ | l | ";
            for (String dummy : criteriaLabels) {
                header += "r | ";
            }
            header += "}\n";
            header += "\\hline\n";
        }
        

        int i = 0;
        
        if (OutputFormat.LATEX.equals(of)) {
            header += "\\diagbox{data}{crit.}";
        }
        header += separString + "\n";
        for (String critLabel : criteriaLabels) {
            header += critLabel;
            if (i != (criteriaLabels.length - 1)) {
                header += separString;
            }
            i++;
        }
        
        if (OutputFormat.LATEX.equals(of)) {
            header += "\\\\ \\hline";
        }
        
        return header + "\n";
    }

    private static String buildLatexTableFooter(String metricString, String suppFactorString) {
        String result = "";
        
        result += "\\end{tabular}\n";
        result += "\\caption{ Information-loss metric: " + metricString + " / Max. suppression factor: " + suppFactorString + " \\%}\n";
        result += "\\end{" + FONT_SIZE + "}\n";
        result += "\\end{table}\n";
        result += "\\end{center}\n";
        result += "";
        
        return result;
    }
}
