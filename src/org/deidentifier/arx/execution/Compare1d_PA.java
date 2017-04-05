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
//			datafile = BenchmarkDatafile.ACS13_NUM;
		} else if ("ATUS".equals(dataFileName)) {
			datafile = BenchmarkDatafile.ATUS;
//			datafile = BenchmarkDatafile.ATUS_NUM;
		} else if ("IHIS".equals(dataFileName)) {
			datafile = BenchmarkDatafile.IHIS;
//			datafile = BenchmarkDatafile.IHIS_NUM;
		} else throw new RuntimeException("Unsupported datafile: '" + dataFileName + "'");
		
		String[] allowedPrivacyMeasures = new String[] { "ld", "lr", "le", "t", "d", "b" };		
		String dim2Qual = args[1];		
		boolean validInput = false;		
		for (String s : allowedPrivacyMeasures) {
			if (dim2Qual != null && s.equals(dim2Qual)) {
				validInput = true;
				break;
			}
		}		
		if (!validInput) throw new RuntimeException("Unsupported input string: '" + dim2Qual + "'");

		int candidateIndex = 0;
		String[] saList = new String[] { };
		if (args.length >= 3) {
			String[] allowedSAs = new String[] { "MS", "ED", "both" };		
			String saFromCommandLine = args[2];		
			validInput = false;		
			for (String s : allowedSAs) {
				if (dim2Qual != null && s.equals(saFromCommandLine)) {
					validInput = true;
					break;
				}
				candidateIndex++;
			}		
			if (!validInput) throw new RuntimeException("Unsupported sensitive attribute: '" + saFromCommandLine + "'");
			
			if (candidateIndex == allowedSAs.length -1) {
				saList = BenchmarkDataset.getSensitiveAttributeCandidates(datafile);
			} else {
				saList = new String[] { BenchmarkDataset.getSensitiveAttributeCandidates(datafile) [candidateIndex] };
			}
			
		} else {
			saList = BenchmarkDataset.getSensitiveAttributeCandidates(datafile);
		}
		
		boolean reverse = false;
		if (args.length >= 4 && "reverse".equals(args[3])) {
			reverse = true;
		}

		for (String sa : saList) {
			compareParameterValues(datafile, sa, dim2Qual, reverse);
		}
		System.out.println("done.");
	}

	public static void compareParameterValues(BenchmarkDatafile datafile, String sa, String dim2Qual, boolean reverse) throws IOException {

		String outFileName = "RelCA1d-" + datafile.name() + "-" + dim2Qual + "-" + sa + (reverse ? "-REVERSE" : "") + ".csv";

		PrintStream fos = new PrintStream("results/" + outFileName);
		System.out.println("Name of output file is " + outFileName);
		

		fos.println(BenchmarkDriver.toCsvString(getCsvHeader(), ";"));

		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsConfigsForParameterComparison(dim2Qual, sa, reverse)) {
			
			BenchmarkCriterion[] criteria = null;
			if (BenchmarkCriterion.K_ANONYMITY.equals(privacyModel.getCriterion())) {
				criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
			} else {
				criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, privacyModel.getCriterion() };
			}
			BenchmarkDataset dataset = new BenchmarkDataset(datafile, criteria, sa);
			BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, dataset);

			String[] relPAStr = driver.findOptimalRelPA(0.05, dataset,
					sa,
					false, privacyModel);
			
			String[] finalResultArray = BenchmarkDriver.concat(
					new String[] {
							datafile.name(),
							sa,
							privacyModel.getCriterion().toString(),
							String.format(Locale.GERMAN, "%f", privacyModel.getDim2Val())
					},
					relPAStr);
					
			
			
			fos.println(BenchmarkDriver.toCsvString(finalResultArray, ";"));
		}
		fos.close();
	}

	public static String[] getCsvHeader() {
		return BenchmarkDriver.concat(new String[] { "datafile", "sa", "pm", "param"}, BenchmarkDriver.getCombinedRelPaAndDisclosureRiskHeader());
	}
}
