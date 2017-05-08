package org.deidentifier.arx.execution;

import java.io.IOException;
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.PrivacyModel;

public class CompareInformationLosses {
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		comparePrivacyModels();
		
		System.out.println("done.");
	}

	private static void comparePrivacyModels() throws IOException {

		for (BenchmarkMeasure measure : new BenchmarkMeasure[] {
				BenchmarkMeasure.ENTROPY,
				BenchmarkMeasure.LOSS,
				BenchmarkMeasure.SORIA_COMAS}) {

			// For each dataset
			for (BenchmarkDatafile datafile : new BenchmarkDatafile[] {
					BenchmarkDatafile.ACS13,
					BenchmarkDatafile.ATUS ,
					BenchmarkDatafile.IHIS }) {

				// for each sensitive attribute candidate
				for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

					// for each privacy model
					for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsCombinedWithK()) {
						
						ParametrizationSetup pSetup =  new ParametrizationSetup(
								datafile,
								sa, 5, PrivacyModel.getDefaultParam2(privacyModel.getCriterion()), privacyModel.getCriterion(), measure, 0.05);
						pSetup.anonymize();
						double il = -1d;
						switch (measure) {
						case ENTROPY:
							il = pSetup.getAnonymizer().getIlRelEntr();
							break;
						case LOSS:
							il = pSetup.getAnonymizer().getIlRelLoss();
							break;
						case SORIA_COMAS:
							il = pSetup.getAnonymizer().getIlSorCom();
							break;
						default:
							throw new RuntimeException("Invalid measure: " + measure);
						
						}
						System.out.format(new Locale ("DE", "de"), "%s;%s;%s;%.5f\n", measure, datafile, sa, il);
					}
				}
			}
		}
	}
}
