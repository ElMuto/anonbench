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
public class CompareRelPAsSoriaComas {
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		PrintStream fileOutputStream = new PrintStream("results/resultsRelCAsSoriaComas_IHIS_ACS13.txt");

		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS_NUM, "MARSTAT", fileOutputStream);
		fileOutputStream.println();
		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.IHIS_NUM, "EDUC", fileOutputStream);		

		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13_NUM, BenchmarkMeasure.SORIA_COMAS, "Marital status", fileOutputStream);
		fileOutputStream.println();
		BenchmarkDriver.compareRelPAs(BenchmarkDatafile.ACS13_NUM, BenchmarkMeasure.SORIA_COMAS, "Education", fileOutputStream);
		fileOutputStream.println("\n");
		
		System.out.println("done.");
		
		fileOutputStream.close();
	}
}
