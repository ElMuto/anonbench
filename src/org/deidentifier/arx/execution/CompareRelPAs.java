package org.deidentifier.arx.execution;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.util.Classificator;
import org.deidentifier.arx.PrivacyModel;


public class CompareRelPAs {
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		Boolean MS = null;
		Boolean ED = null;
		
		if (args.length != 1) {
			throw new RuntimeException("missing SA specification");
		} else {
			if (args[0].equals("MS")) {
					MS = true;
					ED = false;
			} else if (args[0].equals("ED")) {
				ED = true;
				MS = false;
			} else if (args[0].equals("both")) {
				ED = MS = true;
			} else {
				throw new RuntimeException("invalid SA specification");
			}
		}

		
		if (MS) {
			PrintStream fileOutputStreamMS = new PrintStream("results/resultsRelCAsMS.txt");
			
			compareRelPAs(BenchmarkDatafile.ACS13, BenchmarkMeasure.ENTROPY, "Marital status", fileOutputStreamMS);
			fileOutputStreamMS.println();
			
			compareRelPAs(BenchmarkDatafile.ATUS, BenchmarkMeasure.ENTROPY, "Marital status", fileOutputStreamMS);
			fileOutputStreamMS.println();
			
			compareRelPAs(BenchmarkDatafile.IHIS, BenchmarkMeasure.ENTROPY, "MARSTAT", fileOutputStreamMS);
			fileOutputStreamMS.println();
			

			fileOutputStreamMS.close();
		}

		if (ED) {
			PrintStream fileOutputStreamED = new PrintStream("results/resultsRelCAsED.txt");
			
			compareRelPAs(BenchmarkDatafile.ACS13, BenchmarkMeasure.ENTROPY, "Education", fileOutputStreamED);
			fileOutputStreamED.println("\n");

			compareRelPAs(BenchmarkDatafile.ATUS, BenchmarkMeasure.ENTROPY, "Highest level of school completed", fileOutputStreamED);
			fileOutputStreamED.println("\n");

			fileOutputStreamED.println();
			compareRelPAs(BenchmarkDatafile.IHIS, BenchmarkMeasure.ENTROPY, "EDUC", fileOutputStreamED);
			

			fileOutputStreamED.close();
		}
		
		System.out.println("done.");
		
	}

	private static void compareRelPAs(BenchmarkDatafile datafile, BenchmarkMeasure bm, String sa,
			PrintStream fos) {

		for (PrivacyModel pm : BenchmarkSetup.getPrivacyModelsCombinedWithK())  {
			ParametrizationSetup pSetup =  new ParametrizationSetup(
					datafile,
					sa, 5, pm.getDim2Val(), pm.getCriterion(), bm, 0.05);
			Classificator classi = new Classificator(pSetup);
			classi.findOptimalRelCa();
			double orc = classi.getMaxRelPa() != -Double.MAX_VALUE ? classi.getMaxRelPa() : 0d;
			fos.format(new Locale("de", "DE") ,"%s;%s;%s;%.9f\n", datafile, sa, pm.getCriterion(), orc);
			fos.flush();
		}
	}
}
