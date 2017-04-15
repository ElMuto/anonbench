package org.deidentifier.arx.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class Classification {

	public static String[] getBaseCAs(BenchmarkDatafile datafile, String sa, boolean includeInsensitiveAttributes) {
		PrivacyModel pm = new PrivacyModel(BenchmarkCriterion.T_CLOSENESS_ED, 1, 1d);
		BenchmarkDataset dataset = new BenchmarkDataset(datafile, new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, pm.getCriterion() }, sa);
		BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, dataset);
		
		String[] result = new String[3];
		try {
			String[] output = driver.findOptimalRelPA(0d, dataset,
					sa,
					includeInsensitiveAttributes, pm, null, null);
			result[0] = output[2];
			result[1] = output[3];
			result[2] = output[4];
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * @param datafile
	 * @param bmMeasure
	 * @param sa
	 * @param outputStream
	 * @param includeInsensitiveAttributes
	 * @throws IOException
	 */
	public static void compareRelPAs(BenchmarkDatafile datafile, BenchmarkMeasure bmMeasure, String sa, PrintStream outputStream, boolean includeInsensitiveAttributes) throws IOException {
		String printString = "Running " + datafile.toString() + " with SA=" + sa;
		outputStream.println(printString);
		System.out.println(printString);
		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsCombinedWithK()) {
	
	
	
			BenchmarkCriterion[] criteria = null;
			if (BenchmarkCriterion.K_ANONYMITY.equals(privacyModel.getCriterion())) {
				criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
			} else {
				criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, privacyModel.getCriterion() };
			}
	
			BenchmarkDataset dataset = new BenchmarkDataset(datafile, criteria, sa);
			BenchmarkDriver driver = new BenchmarkDriver(bmMeasure, dataset);
	
			String maxPAStr[] = driver.findOptimalRelPA(0d, dataset,
					sa,
					includeInsensitiveAttributes, privacyModel, null, null);
	
			System.out.  format(new Locale("de", "DE"), "%s;%s%n", privacyModel.toString(), maxPAStr[0]);
			outputStream.format(new Locale("de", "DE"), "%s;%s%n", privacyModel.toString(), maxPAStr[0]);
		}
	}

	public static String[] getCombinedRelPaAndDisclosureRiskHeader(BenchmarkDataset dataset) {
		return (String[]) BenchmarkDriver.concat(new String[] { "RelPA", "AbsPA", "MinPA", "MaxPA", "Gain", "Trafo", "NumSuppRecs", "IL-NUE", "IL-Loss", "IL-SSE" }, DisclosureRiskCalculator.getHeader(dataset));
	}

}
