package org.deidentifier.arx.execution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
import org.deidentifier.arx.criteria.KAnonymity;
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

		compareRuntimes();

		System.out.println("done.");
	}

	private static void compareRuntimes() throws IOException {

		final int numIterations = 5;
		final String resultFileName = "results/runtimes.csv";

		BufferedWriter bw = null;

		try {

			bw = new BufferedWriter(new FileWriter(resultFileName));

			String header ="Dataset;SA;Model;Iteration;RuntimeMillis\n";
			System.out.print(header);
			bw.write(header);

			// For each dataset
			for (String datafile : new String[] {
					"ihis",
					"ss13acs",
					"atus",
			}) {

				// for each sensitive attribute candidate
				for (String sa : getSensitiveAttributeCandidates(datafile)) {

					for (PrivacyCriterion c : new PrivacyCriterion[] {
							new DistinctLDiversity(sa, 3),
							new RecursiveCLDiversity(sa, 4, 3),
							new EntropyLDiversity(sa, 3),
							new EqualDistanceTCloseness(sa, 0.2),
							new DDisclosurePrivacy(sa, 1)}) {

						Data				data 		= createArxDataset(datafile, sa);

						for (int rtIt = 1; rtIt <= numIterations; rtIt++) {

							ARXConfiguration	config 		= createArxConfig(c);
							ARXAnonymizer		anonymizer 	= new ARXAnonymizer();
							ARXResult result = anonymizer.anonymize(data, config);
							data.getHandle().release();

							String line = String.format(new Locale ("DE", "de"), "%s;%s;%s;%d;%d\n", datafile, normalize(sa), 
									mapPmName(c), rtIt, result.getTime());
							System.out.print(line);
							bw.write(line);

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

	private static String mapPmName(PrivacyCriterion c) {

		Map<String,String> map = new HashMap<>();

		map.put("distinct-3-diversity for attribute 'EDUC'", 	"0_DL");
		map.put("distinct-3-diversity for attribute 'Education'", "0_DL");
		map.put("distinct-3-diversity for attribute 'Highest level of school completed'", "0_DL");
		map.put("distinct-3-diversity for attribute 'Marital status'", "0_DL");
		map.put("distinct-3-diversity for attribute 'MARSTAT'", "0_DL");
		map.put("recursive-(4.0,3)-diversity for attribute 'EDUC'", "1_RL");
		map.put("recursive-(4.0,3)-diversity for attribute 'Education'", "1_RL");
		map.put("recursive-(4.0,3)-diversity for attribute 'Highest level of school completed'", "1_RL");
		map.put("recursive-(4.0,3)-diversity for attribute 'Marital status'", "1_RL");
		map.put("recursive-(4.0,3)-diversity for attribute 'MARSTAT'", "1_RL");
		map.put("shannon-entropy-3.0-diversity for attribute 'EDUC'", "2_EL");
		map.put("shannon-entropy-3.0-diversity for attribute 'Education'", "2_EL");
		map.put("shannon-entropy-3.0-diversity for attribute 'Highest level of school completed'", "2_EL");
		map.put("shannon-entropy-3.0-diversity for attribute 'Marital status'", "2_EL");
		map.put("shannon-entropy-3.0-diversity for attribute 'MARSTAT'", "2_EL");
		map.put("0.2-closeness with equal ground-distance for attribute 'EDUC'", "3_TC");
		map.put("0.2-closeness with equal ground-distance for attribute 'Education'", "3_TC");
		map.put("0.2-closeness with equal ground-distance for attribute 'Highest level of school completed'", "3_TC");
		map.put("0.2-closeness with equal ground-distance for attribute 'Marital status'", "3_TC");
		map.put("0.2-closeness with equal ground-distance for attribute 'MARSTAT'", "3_TC");
		map.put("1.0-disclosure privacy for attribute 'EDUC'", "4_DP");
		map.put("1.0-disclosure privacy for attribute 'Education'", "4_DP");
		map.put("1.0-disclosure privacy for attribute 'Highest level of school completed'", "4_DP");
		map.put("1.0-disclosure privacy for attribute 'Marital status'", "4_DP");
		map.put("1.0-disclosure privacy for attribute 'MARSTAT'", "4_DP");

		return map.get(c.toString());
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
		config.addPrivacyModel(new KAnonymity(5));
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
			return new String[] { "Age", "Sex", "Race", "Citizenship status", "Region", "Birthplace" };
		if ("ihis".equals(datafile))
			return new String[] { "AGE", "SEX", "RACEA", "REGION", "YEAR", "QUARTER" };
		if ("ss13acs".equals(datafile))
			return new String[] { "Age", "Sex", "Race" , "Citizenship", "Workclass", "Mobility" };
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
