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


public class Compare2d_PA {
	
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

		String outFileName = "RelCA2d-" + datafile.name() + "-" + dim2Qual + "-" + sa + ".dat";

		PrintStream fos = new PrintStream("results/" + outFileName);
		System.out.println("Name of output file is " + outFileName);

		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsConfigsForParameterComparison(dim2Qual, sa)) {
			
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
					false, privacyModel, null, null);
						
			System.out.format(new Locale("de", "de"), "%d\t%.5f\t%s\n", privacyModel.getK(), privacyModel.getDim2Val(), relPAStr[0]);
			fos       .format(new Locale("de", "de"), "%d\t%.5f\t%s\n", privacyModel.getK(), privacyModel.getDim2Val(), relPAStr[0]);
		}
		fos.close();
	}

}
