package org.deidentifier.arx.execution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.ComparisonSetup;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;

public class ParametrizationChecker {

	public static void main(String[] args) {
		


		String outFileName = "MaxParams.csv";

		PrintStream fos = null;
		try {
			fos = new PrintStream("results/" + outFileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		System.out.println("Name of output file is " + outFileName);


		System.out.println(BenchmarkDriver.toCsvString(getCsvHeader(), ";"));
		fos.       println(BenchmarkDriver.toCsvString(getCsvHeader(), ";"));

		for (BenchmarkDatafile datafile : new BenchmarkDatafile[] { BenchmarkDatafile.ACS13, BenchmarkDatafile.ATUS, BenchmarkDatafile.IHIS }) {

				for (String sa : new String[] { "MS", "ED" } ) {

					ComparisonSetup compSetup =  createCompSetup(datafile, sa);
					BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, compSetup.getDataset());

					String[] relPAStr = null;
					try {
						relPAStr = driver.findOptimalRelPA(0.05, compSetup.getDataset(), compSetup.getSa(), false, compSetup.getPrivacyModel(),
								new int[] { 0, 0, 0 }, new int[] { 0, 0, 0 });
					} catch (IOException e) {
						e.printStackTrace();
					}

					String[] finalResultArray = BenchmarkDriver.concat(
							new String[] {
									compSetup.getDataset().getDatafile().name(),
									compSetup.getSa(),
									compSetup.getPrivacyModel().getCriterion().toString(),
									String.format(Locale.GERMAN, "%f", compSetup.getPrivacyModel().getDim2Val())
							},
							relPAStr);



					fos.       println(BenchmarkDriver.toCsvString(finalResultArray, ";"));	
					System.out.println(BenchmarkDriver.toCsvString(finalResultArray, ";"));		

				}
		}

		fos.close();

	}
	
	static ComparisonSetup createCompSetup(BenchmarkDatafile datafile, String sa) {
		
		PrivacyModel privacyModel = new PrivacyModel("t", 5, 1d);
		
		String convertedSA = null;
		if (datafile.equals(BenchmarkDatafile.ACS13)) {
			if ("MS".equals(sa)) {
				convertedSA = "Marital status";
			} else if ("ED".equals(sa)) {
				convertedSA = "Education";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
		} else if (datafile.equals(BenchmarkDatafile.ATUS)) {
			if ("MS".equals(sa)) {
				convertedSA = "Marital status";
			} else if ("ED".equals(sa)) {
				convertedSA = "Highest level of school completed";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
		}  else if (datafile.equals(BenchmarkDatafile.IHIS)) {
			if ("MS".equals(sa)) {
				convertedSA = "MARSTAT";
			} else if ("ED".equals(sa)) {
				convertedSA = "EDUC";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
		} else {
			throw new RuntimeException("Invalid datafile: " + datafile);
		}
		
		return new  ComparisonSetup(
    			new BenchmarkCriterion[] {BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS_ED },
    			privacyModel,
    			datafile,
    			0.05,
    			BenchmarkMeasure.ENTROPY,
    			convertedSA);
	}
	


	public static String[] getCsvHeader() {
		return BenchmarkDriver.concat(new String[] { "datafile", "sa", "pm", "param"}, BenchmarkDriver.getCombinedRelPaAndDisclosureRiskHeader());
	}

}
