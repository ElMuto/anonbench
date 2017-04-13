package org.deidentifier.arx.execution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.ComparisonSetup;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;
import org.deidentifier.arx.utility.DataConverter;

public class CalcBetaMaxAndDeltaMax {

	public static void main(String[] args) {

		for (BenchmarkDatafile datafile : new BenchmarkDatafile[] { BenchmarkDatafile.ACS13, BenchmarkDatafile.ATUS, BenchmarkDatafile.IHIS }) {

			for (String sa : new String[] { "MS", "ED" } ) {
				
				double sf=0d;

				ComparisonSetup compSetup =  createCompSetup(datafile, sa, sf, BenchmarkCriterion.T_CLOSENESS_ED);

				ARXConfiguration config = null;
				try {
					config = BenchmarkDriver.getConfiguration(
							compSetup.getDataset(), sf, BenchmarkMeasure.ENTROPY, compSetup.getSa(), 
							compSetup.getPrivacyModel(), compSetup.getPrivacyModel().getCriterion());
				} catch (IOException e) {
					e.printStackTrace();
				}

				Data arxData = compSetup.getDataset().getArxData(true);
				DataDefinition dataDef1 = arxData.getDefinition();
				String[] qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(compSetup.getDataset().getDatafile());
				for (int i = 0; i < qiS.length; i++) {
					String qi = qiS[i];
					dataDef1.setMinimumGeneralization(qi, 0);
					dataDef1.setMaximumGeneralization(qi, 0);
				}
				
				ARXAnonymizer anonymizer = new ARXAnonymizer();

	            DisclosureRiskCalculator.prepare(compSetup.getDataset().getDatafile(), sa);
	            ARXResult result = null;
	        	try {
					result = anonymizer.anonymize(arxData, config);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            DisclosureRiskCalculator.summarize();	            

	        	DataHandle outHandle = result.getOutput(result.getGlobalOptimum(), false);
	        	int numOfsuppressedRecords = outHandle.getStatistics().getEquivalenceClassStatistics().getNumberOfOutlyingTuples();

	            
	            System.out.printf("%s - %s:\t%s=%.4f,\t%s=%.4f,\t%s=%s,\t%s=%d\n", datafile, sa,
	            		"dMax", DisclosureRiskCalculator.getDelta().getMax(),
	            		"bMax", DisclosureRiskCalculator.getBeta().getMax(),
	            		"traFo", Arrays.toString(result.getGlobalOptimum().getTransformation()),
	            		"numSupRecs", numOfsuppressedRecords);
	            
	            outHandle.release();
			}

		}

	}
	
	static ComparisonSetup createCompSetup(BenchmarkDatafile datafile, String sa, double sf, BenchmarkCriterion criterion) {
		
		PrivacyModel privacyModel = new PrivacyModel(criterion);

		String convertedSA = null;
		switch(datafile) {
		case ACS13:
			if ("MS".equals(sa)) {
				convertedSA = "Marital status";
			} else if ("ED".equals(sa)) {
				convertedSA = "Education";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
			break;
		case ATUS:
			if ("MS".equals(sa)) {
				convertedSA = "Marital status";
			} else if ("ED".equals(sa)) {
				convertedSA = "Highest level of school completed";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
			break;
		case IHIS:
			if ("MS".equals(sa)) {
				convertedSA = "MARSTAT";
			} else if ("ED".equals(sa)) {
				convertedSA = "EDUC";
			} else {
				throw new RuntimeException("Invalid SA: " + sa);
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid datafile: " + datafile);
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
