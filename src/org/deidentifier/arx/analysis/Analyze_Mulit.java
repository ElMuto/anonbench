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
import java.util.Arrays;
import java.util.Iterator;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;

public class Analyze_Mulit {
        
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
    	analyzeInterCriteriaComparison();
    	System.out.println("done.");
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
		for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {
			Selector<String[]> selector = file.getSelectorBuilder()
					.field(BenchmarkSetup.COLUMNS.IL_MEASURE.toString()).equals(String.valueOf(metric)).and()
					.field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(String.valueOf(suppFactor)).and()
					.field(BenchmarkSetup.COLUMNS.SUBSET_NATURE.toString()).equals(Boolean.toString(subsetBased)).and()
					.field(BenchmarkSetup.COLUMNS.DATASET.toString()).equals(datafile.toString())
					.build();

			// get min and max of the dataset
			Double minVal = null;
			Double maxVal = null;
			Iterator<CSVLine> iter = file.iterator();
			while (iter.hasNext()) {
				CSVLine csvline = iter.next();
				String[] csvLine = csvline.getData();

				if (selector.isSelected(csvLine)) {
					Double val = Double.valueOf(csvline.get(BenchmarkSetup.COLUMNS.IL_VALUE.toString(), "Arithmetic Mean"));
					if (val != BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL) {
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
			String line = (datafile.toString());
			for (String critLabel : criteriaLabels) {

			    // build selector
				selector = file.getSelectorBuilder()
						.field(BenchmarkSetup.COLUMNS.IL_MEASURE.toString()).equals(String.valueOf(metric)).and()
						.field(BenchmarkSetup.COLUMNS.SUPPRESSION_FACTOR.toString()).equals(String.valueOf(suppFactor)).and()
						.field(BenchmarkSetup.COLUMNS.SUBSET_NATURE.toString()).equals(Boolean.toString(subsetBased)).and()
						.field(BenchmarkSetup.COLUMNS.DATASET.toString()).equals(datafile.toString()).and()
						.field(BenchmarkSetup.COLUMNS.CRITERIA.toString()).equals(critLabel)
						.build();

				// find the entry in the CSV file, that we need to fill the cell
				iter = file.iterator();
				while (iter.hasNext()) {
					CSVLine csvline = iter.next();
					String[] csvLine = csvline.getData();

					if (selector.isSelected(csvLine)) {
						Double val = Double.valueOf(csvline.get(BenchmarkSetup.COLUMNS.IL_VALUE.toString(), "Arithmetic Mean"));
						Double normVal = val != BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL ? (val - minVal) / (maxVal - minVal) : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
						String normString = normVal != BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL ? new DecimalFormat("0.0000").format(normVal): BenchmarkSetup.NO_RESULT_FOUND_STRING_VAL;
						String colorCode = "";
						if (OutputFormat.LATEX.equals(of)) { // color formatting of cells
							if (val.equals(minVal)) {
								colorCode = "\\cellcolor{green!25}";
							} else if (val.equals(maxVal)) {
								colorCode = "\\cellcolor{red!25}";
							}else if (normVal.equals(BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL)) {
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
            for (@SuppressWarnings("unused") String dummy : criteriaLabels) {
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
