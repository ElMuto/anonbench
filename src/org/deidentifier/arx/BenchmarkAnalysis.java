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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.VARIABLES;

import de.linearbits.objectselector.Selector;
import de.linearbits.subframe.io.CSVFile;
import de.linearbits.subframe.io.CSVLine;

public class BenchmarkAnalysis {
    
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
        OutputFormat of = OutputFormat.LATEX;
//        OutputFormat of = OutputFormat.CSV;
        
        try (Writer writer = new BufferedWriter(
                    new OutputStreamWriter(
                        new FileOutputStream(BenchmarkSetup.RESULTS_DIR + "/" + BenchmarkSetup.SUMMARY_FILE_STEM + of.getSuffix()), "utf-8"))) {
            writer.write(summarizeCriteriaWithDifferentSuppressionValues(of));
            writer.flush();
            writer.close();
        }
        
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
        String separString = of.getSeparator();

       // put the string representation of the criteria combinations into an arry
       String[] criteriaLabels = new String[BenchmarkSetup.getCriteria().length];
       int i= 0;
       for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
           criteriaLabels[i++] = Arrays.toString(criteria);
       }
       
       if (OutputFormat.LATEX.equals(of)) {
           result += "\\documentclass{article}\n";
           result += "\\usepackage{diagbox}\n";
           result += "\\begin{document}\n";
           result += "\\oddsidemargin = 0 pt\n";
       }
	   
	   // for each suppression factor
	   for (double suppFactor : BenchmarkSetup.getSuppressionFactors()) {

	       // print header-line
	       result += buildTableHeader(of, criteriaLabels);
	       
	       // for each dataset
	       for (BenchmarkDataset dataset : BenchmarkSetup.getDatasets()) {
	           Selector<String[]> selector = file.getSelectorBuilder()
                       .field(org.deidentifier.arx.BenchmarkSetup.VARIABLES.SUPPRESSION_FACTOR.toString())
                       .equals(String.valueOf(suppFactor))
                       .and()
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

	           String line = (dataset.toString());
	           for (String critLabel : criteriaLabels) {
	               selector = file.getSelectorBuilder()
	                       .field(org.deidentifier.arx.BenchmarkSetup.VARIABLES.SUPPRESSION_FACTOR.toString())
	                       .equals(String.valueOf(suppFactor))
	                       .and()
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
	                       Double normVal = val != BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL ? (val - minVal) / (maxVal - minVal) : BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL;
	                       String normString = normVal != BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL ? new DecimalFormat("#.###").format(normVal) : BenchmarkSetup.NO_SOULUTION_FOUND_STRING_VAL;
	                       line += (separString + normString);
	                   }
	               }
	           }
	           result += ((OutputFormat.LATEX.equals(of) ? line + " \\\\ \\hline" : line) + "\n");
	       }
	       
	       if (OutputFormat.LATEX == of) {  // print table footer
               result += buildLatexTableFooter(new DecimalFormat("###").format(suppFactor * 100));
	       }
	   }
	   if (OutputFormat.LATEX == of) {
	       result += "\\end{document}\n";
	   }	   
	   return result;
    }
    
    private static String buildTableHeader(OutputFormat of, String[] criteriaLabels) {
        String separString = of.getSeparator();
        String header = "";
        
        if (OutputFormat.LATEX == of) {
            header += "\\begin{center}\n";
            header += "\\begin{table}[htb!]\n";
            header += "\\begin{tabular}{ | l | l | c | c | c | c | c | c | c | c | c | c | c | }\n";
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

    private static String buildLatexTableFooter(String captionText) {
        String result = "";
        
        result += "\\end{tabular}\n";
        result += "\\caption{ max. suppression factor " + captionText + " \\%}\n";
        result += "\\end{table}\n";
        result += "\\end{center}\n";
        result += "";
        
        return result;
    }
}
