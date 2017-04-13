package org.deidentifier.arx.execution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.ComparisonSetup;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class CalcBetaMaxAndDeltaMax {

	public static void main(String[] args) {
		


		String outFileName = "MaxParams.csv";

		PrintStream fos = null;
		try {
			fos = new PrintStream("results/" + outFileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		System.out.println("Name of output file is " + outFileName);

		String header = "Datafile;sa;Delta-max;Beta-max";
		System.out.println(header);
		fos.       println(header);

		for (BenchmarkDatafile datafile : new BenchmarkDatafile[] { BenchmarkDatafile.ACS13, BenchmarkDatafile.ATUS, BenchmarkDatafile.IHIS }) {

			for (String sa : new String[] { "MS", "ED" } ) {

				ComparisonSetup compSetup =  createCompSetup(datafile, sa, 0d, BenchmarkCriterion.BASIC_BETA_LIKENESS);

				ARXConfiguration config = null;
				try {
					config = BenchmarkDriver.getConfiguration(
							compSetup.getDataset(), 0d, BenchmarkMeasure.ENTROPY, sa, 
							compSetup.getPrivacyModel(), compSetup.getPrivacyModel().getCriterion());
				} catch (IOException e) {
					e.printStackTrace();
				}

				DataDefinition dataDef = compSetup.getDataset().getArxData().getDefinition();
				String[] qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(compSetup.getDataset().getDatafile());
				for (int i = 0; i < qiS.length; i++) {
					String qi = qiS[i];
					dataDef.setMinimumGeneralization(qi, 0);
					dataDef.setMaximumGeneralization(qi, 0);
				}
				
				ARXAnonymizer anonymizer = new ARXAnonymizer();

	            DisclosureRiskCalculator.prepare(compSetup.getDataset().getDatafile(), sa);
	        	try {
					anonymizer.anonymize(compSetup.getDataset().getArxData(true), config);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            DisclosureRiskCalculator.summarize();
	            
	            System.out.println(DisclosureRiskCalculator.getBeta());
			}

		}

		fos.close();

	}
	
	static ComparisonSetup createCompSetup(BenchmarkDatafile datafile, String sa, double sf, BenchmarkCriterion criterion) {
		
		PrivacyModel privacyModel = new PrivacyModel(criterion, 5, 1d);
		
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
    			new BenchmarkCriterion[] {BenchmarkCriterion.K_ANONYMITY, criterion },
    			privacyModel,
    			datafile,
    			sf,
    			BenchmarkMeasure.ENTROPY,
    			convertedSA);
	}
	


	public static String[] getCsvHeader() {
		return (String[]) BenchmarkDriver.concat(new String[] { "datafile", "sa", "pm", "param"}, BenchmarkDriver.getCombinedRelPaAndDisclosureRiskHeader());
	}

}
