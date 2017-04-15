package org.deidentifier.arx.execution;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.util.Classification;
import org.deidentifier.arx.util.CommandLineParser;
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
		
		BenchmarkCriterion crit = CommandLineParser.parseCritString(args[1]);

		boolean validInput = false;
		int candidateIndex = 0;
		String[] saList = new String[] { };
		if (args.length >= 3) {
			String[] allowedSAs = new String[] { "MS", "ED", "both" };		
			String saFromCommandLine = args[2];		
			validInput = false;		
			for (String s : allowedSAs) {
				if (args[1] != null && s.equals(saFromCommandLine)) {
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
			compareParameterValues(datafile, sa, crit, reverse);
		}
		System.out.println("done.");
	}



	public static void compareParameterValues(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit, boolean reverse) throws IOException {

		String outFileName = "RelCA1d-" + datafile.name() + "-" + crit + "-" + sa + (reverse ? "-REVERSE" : "") + ".csv";

		PrintStream fos = new PrintStream("results/" + outFileName);
		System.out.println("Name of output file is " + outFileName);
		

		fos.println(BenchmarkDriver.toCsvString(getCsvHeader(new BenchmarkDataset(BenchmarkDatafile.ACS13, new BenchmarkCriterion[] { BenchmarkCriterion.T_CLOSENESS_ED }, "MS")), ";"));

		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsConfigsForParameterComparison(crit, sa, reverse)) {
			
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
			
			String[] finalResultArray = (String[]) BenchmarkDriver.concat(
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

	public static String[] getCsvHeader(BenchmarkDataset dataset) {
		return (String[]) BenchmarkDriver.concat(new String[] { "datafile", "sa", "pm", "param"}, Classification.getCombinedRelPaAndDisclosureRiskHeader(dataset));
	}
}
