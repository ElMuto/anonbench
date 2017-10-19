package org.deidentifier.arx.execution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.criteria.DDisclosurePrivacy;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.EntropyLDiversity;
import org.deidentifier.arx.criteria.EqualDistanceTCloseness;
import org.deidentifier.arx.criteria.PrivacyCriterion;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;

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

			// For each dataset
			for (String datafile : new String[] {
					"ss13acs",
					"atus" ,
			"ihis" }) {

				// for each sensitive attribute candidate
				for (String sa : getSensitiveAttributeCandidates(datafile)) {

					for (PrivacyCriterion c : new PrivacyCriterion[] {
							new DistinctLDiversity(sa, 3),
							new RecursiveCLDiversity(sa, 4, 3),
							new EntropyLDiversity(sa, 3),
							new EqualDistanceTCloseness(sa, 0.2),
							new DDisclosurePrivacy(sa, 1)}) {

						Data data = createArxDataset(datafile, sa);
						ARXConfiguration config = createArxConfig(c);
						ARXAnonymizer anonymizer = new ARXAnonymizer();

						for (int rtIt = 1; rtIt <= numIterations; rtIt++) {

							long ts1 = System.currentTimeMillis();
							ARXResult result = anonymizer.anonymize(data, config);
							long ts2 = System.currentTimeMillis();
							data.getHandle().release();
							result.getOutput().release();

							long rt = ts2 - ts1;
							String line = String.format(new Locale ("DE", "de"), "%s;%s;%s;%d;%d\n", datafile, normalize(sa), 
									c, rtIt, rt);
							System.out.print(line);
							bw.write(line);
							bw.flush();
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

	static private String[] getSensitiveAttributeCandidates(String datafile) {

		if ("atus".equals(datafile))
			return new String[] { "Marital status", "Highest level of school completed",  };
		if ("ihis".equals(datafile))
			return new String[] { "MARSTAT", "EDUC",  };
		if ("ss13acs".equals(datafile))
			return new String[] { "Marital status", "Education" ,  };

		throw new RuntimeException("Invalid datafile");
	}

	private static ARXConfiguration createArxConfig(PrivacyCriterion c) {
		ARXConfiguration config = ARXConfiguration.create();
		config.setQualityModel(Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN));
		config.setMaxOutliers(0.05d);
		config.addPrivacyModel(c);
		return config;
	}

	private static String normalize(String sa) {

		if ("Marital status".equals(sa) || "MARSTAT".equals(sa))
			return "MARSTAT";

		if ("Education".equals(sa) || "Highest level of school completed".equals(sa) || "EDUC".equals(sa))
			return "EDUC";

		throw new IllegalArgumentException();
	}



	private static Data createArxDataset(String datafile, String sa) {
		Data arxData;
		String path = "data/" + datafile + ".csv";
		try {
			arxData = Data.create(path, Charset.forName("UTF-8"), ';');
		} catch (IOException e) {
			arxData = null;
			System.err.println("Unable to load dataset from file " + path);
		}
		for (String qi : getQuasiIdentifyingAttributes(datafile)) {
			arxData.getDefinition().setAttributeType(qi, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
			arxData.getDefinition().setHierarchy(qi, loadHierarchy(datafile, qi));
		}
		arxData.getDefinition().setAttributeType(sa, AttributeType.SENSITIVE_ATTRIBUTE);

		return arxData;
	}


	public static String[] getQuasiIdentifyingAttributes(String datafile) {
		if ("atus".equals(datafile))
			return new String[] { "Age", "Sex", "Race" };
		if ("ihis".equals(datafile))
			return new String[] { "AGE", "SEX", "RACEA" };
		if ("ss13acs".equals(datafile))
			return new String[] { "Age", "Sex", "Race" };
		throw new RuntimeException("Invalid datafile: " + datafile);
	}


	/**
	 * Returns the generalization hierarchy for the dataset and attribute
	 * @param attribute
	 * @param numeric if true, the numeric variant of the hierarchies is returned
	 * @return
	 */
	static Hierarchy loadHierarchy(String datafile, String attribute) {

		String path;
		path = "hierarchies/" + datafile + "_hierarchy_" + attribute + ".csv";
		try {
			return Hierarchy.create(path, Charset.forName("UTF-8"), ';');
		} catch (IOException e) {
			System.err.println("Unable to load hierarchy from file " + path);
			return null;
		}
	}
}
