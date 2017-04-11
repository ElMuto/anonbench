package org.deidentifier.arx.execution;

import java.io.IOException;
import java.io.PrintStream;

import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;


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
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13, BenchmarkMeasure.ENTROPY, "Marital status", fileOutputStreamMS, false);
			fileOutputStreamMS.println();
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ATUS, BenchmarkMeasure.ENTROPY, "Marital status", fileOutputStreamMS, false);
			fileOutputStreamMS.println();
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS, BenchmarkMeasure.ENTROPY, "MARSTAT", fileOutputStreamMS, false);
			fileOutputStreamMS.println();
			

			fileOutputStreamMS.close();
		}

		if (ED) {
			PrintStream fileOutputStreamED = new PrintStream("results/resultsRelCAsED.txt");
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13, BenchmarkMeasure.ENTROPY, "Education", fileOutputStreamED, false);
			fileOutputStreamED.println("\n");

			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ATUS, BenchmarkMeasure.ENTROPY, "Highest level of school completed", fileOutputStreamED, false);
			fileOutputStreamED.println("\n");

			fileOutputStreamED.println();
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS, BenchmarkMeasure.ENTROPY, "EDUC", fileOutputStreamED, false);
			

			fileOutputStreamED.close();
		}
		
		System.out.println("done.");
		
	}
}
