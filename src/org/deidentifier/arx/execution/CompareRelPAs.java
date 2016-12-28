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
public class CompareRelPAs {
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		PrintStream fileOutputStream = new PrintStream("results/resultsRelCAs.txt");

		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13, "Marital status", fileOutputStream);
		fileOutputStream.println();
		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13, "Education", fileOutputStream);
		fileOutputStream.println("\n");
		
		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ATUS, "Marital status", fileOutputStream);
		fileOutputStream.println();
		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ATUS, "Highest level of school completed", fileOutputStream);
		fileOutputStream.println("\n");

		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS, "MARSTAT", fileOutputStream);
		fileOutputStream.println();
		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS, "EDUC", fileOutputStream);
		
		System.out.println("done.");
		
		fileOutputStream.close();
	}
}