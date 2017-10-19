package org.deidentifier.arx.execution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.PrivacyModel;

public class CompareRuntimes {

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

		final int numIterations = 10;
		final String resultFileName = "results/runtimes.csv";

		BufferedWriter bw = null;

		try {
			
			bw = new BufferedWriter(new FileWriter(resultFileName));

			String header ="Measure;Dataset;SA;Model;Iteration;RuntimeMillis\n";
			System.out.print(header);
			bw.write(header);

			for (BenchmarkMeasure measure : new BenchmarkMeasure[] {
					BenchmarkMeasure.ENTROPY,
					BenchmarkMeasure.LOSS,
					BenchmarkMeasure.SORIA_COMAS}) {

				// For each dataset
				for (BenchmarkDatafile datafile : new BenchmarkDatafile[] {
//						BenchmarkDatafile.ACS13,
//						BenchmarkDatafile.ATUS ,
						BenchmarkDatafile.IHIS 
						}) {

					// for each sensitive attribute candidate
					for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

						// for each privacy model
						for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsCombinedWithK()) {

							for (int rtIt = 1; rtIt <= numIterations; rtIt++) {

								ParametrizationSetup pSetup =  new ParametrizationSetup(
										datafile,
										sa, 5, PrivacyModel.getDefaultParam2(privacyModel.getCriterion()), privacyModel.getCriterion(), measure, 0.05);
								long ts1 = System.currentTimeMillis();
								pSetup.anonymize();
								long ts2 = System.currentTimeMillis();
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
								long rt = ts2 - ts1;
								String line = String.format(new Locale ("DE", "de"), "%s;%s;%s;%s;%d;%d\n", measure, datafile, normalize(sa), 
										privacyModel.getAbbreviation(), rtIt, rt);
								System.out.print(line);
								bw.write(line);
								bw.flush();
							}
						}
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			if (bw != null) bw.close();
		}
	}

	private static String normalize(String sa) {
		
		if ("Marital status".equals(sa) || "MARSTAT".equals(sa))
			return "MARSTAT";
		
		if ("Education".equals(sa) || "Highest level of school completed".equals(sa) || "EDUC".equals(sa))
			return "EDUC";
		
		throw new IllegalArgumentException();
	}
}
