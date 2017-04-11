package org.deidentifier.arx.execution;

import java.io.IOException;
import java.io.PrintStream;

import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;


/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar pc-bench.jar
 * 
 * @author Helmut Spengler
 */
public class CalcClassificationBaseline {
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		PrintStream fileOutputStream = new PrintStream("results/resultsBaselineCAs.txt");

//		BenchmarkDriver.getBasePAs(BenchmarkDatafile.ACS13, "Marital status", fileOutputStream);
//		fileOutputStream.println();
//		BenchmarkDriver.getBasePAs(BenchmarkDatafile.ACS13, "Education", fileOutputStream);
//		fileOutputStream.println("\n");
		
		BenchmarkDriver.getBasePAs(BenchmarkDatafile.ATUS, "Marital status", fileOutputStream, false);
		fileOutputStream.println();
		BenchmarkDriver.getBasePAs(BenchmarkDatafile.ATUS, "Highest level of school completed", fileOutputStream, false);
		fileOutputStream.println("\n");

//		BenchmarkDriver.getBasePAs(BenchmarkDatafile.IHIS, "MARSTAT", fileOutputStream, true);
//		fileOutputStream.println();
//		BenchmarkDriver.getBasePAs(BenchmarkDatafile.IHIS, "EDUC", fileOutputStream, true);
		
		System.out.println("done.");
		
		fileOutputStream.close();
	}
}
