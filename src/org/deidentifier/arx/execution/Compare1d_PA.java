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
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;


/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class Compare1d_PA {
	
	/**
	 * Main entry point
	 * 
	 * @param args[0]: dataset, args[1]: dim2Qual
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		BenchmarkDatafile datafile = null;
		String dataFileName = args[0];
		if ("ACS13".equals(dataFileName)) {
			datafile = BenchmarkDatafile.ACS13;
		} else if ("ATUS".equals(dataFileName)) {
			datafile = BenchmarkDatafile.ATUS;
		} else if ("IHIS".equals(dataFileName)) {
			datafile = BenchmarkDatafile.IHIS;
		} else throw new RuntimeException("Unsupported datafile: '" + dataFileName + "'");
		
		String[] allowedInputStrings = new String[] { "ld", "lr", "le", "t", "d" };		
		String dim2Qual = args[1];		
		boolean validInput = false;		
		for (String s : allowedInputStrings) {
			if (dim2Qual != null && s.equals(dim2Qual)) {
				validInput = true;
				break;
			}
		}		
		if (!validInput) throw new RuntimeException("Unsupported input string: '" + dim2Qual + "'");

		for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {
			compareRelPAsTK(datafile, sa, dim2Qual);
		}
		System.out.println("done.");
	}

	public static void compareRelPAsTK(BenchmarkDatafile datafile, String sa, String dim2Qual) throws IOException {

		String outFileName = "SA100_RelCA1d-" + datafile.name() + "-" + dim2Qual + "-" + sa + ".dat";

		PrintStream fos = new PrintStream("results/" + outFileName);
		System.out.println("Name of output file is " + outFileName);

		Integer lastK = BenchmarkSetup.getPrivacyModelsConfigsFor_2D_Comparison(dim2Qual)[0].getK();
		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsConfigsFor_2D_Comparison(dim2Qual)) {
			BenchmarkDataset dataset = new BenchmarkDataset(datafile, new BenchmarkCriterion[] { privacyModel.getCriterion() }, sa);
			BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, dataset);
			
			double relPA = driver.calculateMaximalClassificationAccuracy(1d, dataset,
					privacyModel.getK(),
					privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
					privacyModel.getD(), null, null,
					sa, null);
			
			String fStr = "";
			if (!privacyModel.getK().equals(lastK)) fStr += "\n";
			fStr += "%.5f\t%.5f\n";
			lastK = privacyModel.getK();
			
			if (relPA == -Double.MAX_VALUE) relPA = -1d;
			
			System.out.format(new Locale("de", "de"), fStr, privacyModel.getDim2Val(), relPA);
			fos       .format(new Locale("de", "de"), fStr, privacyModel.getDim2Val(), relPA);
		}
		fos.close();
	}

}
