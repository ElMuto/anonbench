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
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;


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
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13_NUM, BenchmarkMeasure.ENTROPY, "Marital status", fileOutputStreamMS, false);
			fileOutputStreamMS.println();
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ATUS_NUM, BenchmarkMeasure.ENTROPY, "Marital status", fileOutputStreamMS, false);
			fileOutputStreamMS.println();
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS_NUM, BenchmarkMeasure.ENTROPY, "MARSTAT", fileOutputStreamMS, false);
			fileOutputStreamMS.println();
			

			fileOutputStreamMS.close();
		}

		if (ED) {
			PrintStream fileOutputStreamED = new PrintStream("results/resultsRelCAsED.txt");
			
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13_NUM, BenchmarkMeasure.ENTROPY, "Education", fileOutputStreamED, false);
			fileOutputStreamED.println("\n");

			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ATUS_NUM, BenchmarkMeasure.ENTROPY, "Highest level of school completed", fileOutputStreamED, false);
			fileOutputStreamED.println("\n");

			fileOutputStreamED.println();
			BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS_NUM, BenchmarkMeasure.ENTROPY, "EDUC", fileOutputStreamED, false);
			

			fileOutputStreamED.close();
		}
		
		System.out.println("done.");
		
	}
}
