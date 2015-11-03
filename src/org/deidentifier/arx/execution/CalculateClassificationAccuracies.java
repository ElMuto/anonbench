package org.deidentifier.arx.execution;

import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import org.deidentifier.arx.ClassificationConfig;

public class CalculateClassificationAccuracies {


	public static void main(String[] args) {

		getDistinctValues("adult_comma.csv",             null, "results/numDistinctValuesAdult.csv");
		getDistinctValues("ss13acs_essential_comma.csv", null, "results/numDistinctValuesAcs13.csv");
		getDistinctValues("atus_comma.csv",              null, "results/numDistinctValuesAtus.csv");
		getDistinctValues("fars_comma.csv",               "4", "results/numDistinctValuesFars.csv");
		getDistinctValues("ihis_comma.csv",               "4", "results/numDistinctValuesIhis.csv");

		evaluateConfig(adultBaseline, "results/baselineAdult.csv");
		evaluateConfig(acs13Baseline, "results/baselineAcs13.csv");
		evaluateConfig(atusBaseline, "results/baselineAtus.csv");
		evaluateConfig(farsBaseline, "results/baselineFars.csv");
		evaluateConfig(ihisBaseline, "results/baselineIhis.csv");
				
		evaluateConfig(adultConfig, "results/classificationResultsAdult.csv");
		evaluateConfig(acs13Config, "results/classificationResultsAcs13.csv");
		evaluateConfig(atusConfig, "results/classificationResultsAtus.csv");
		evaluateConfig(farsConfig, "results/classificationResultsFars.csv");
		evaluateConfig(ihisConfig, "results/classificationResultsIhis.csv");
		
		evaluateConfig(adultConfigShmatikovCompare, "results/ShmatikovComparison.csv");
		
	}
	
	private enum Classifier {
		J48,
		RandomForest,
		NaiveBayes
	}
		
	private static ClassificationConfig[][] adultConfigShmatikovCompare = new ClassificationConfig[][] {
		new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "workclass", null, false, null),
				new ClassificationConfig("adult_comma.csv", "workclass", new String[] { "age", "sex", "race" }, false, null),
				new ClassificationConfig("adult_comma.csv", "workclass", new String[] { "age", "sex", "race", "workclass" }, true, null)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "education", null, false, null),
				new ClassificationConfig("adult_comma.csv", "education", new String[] { "age", "sex", "race" }, false, null),
				new ClassificationConfig("adult_comma.csv", "education", new String[] { "age", "sex", "race", "education" }, true, null)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "marital-status", null, false, null),
				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "age", "sex", "race" }, false, null),
				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "age", "sex", "race", "marital-status" }, true, null)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "occupation", null, false, null),
				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "age", "sex", "race" }, false, null),
				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "age", "sex", "race", "occupation" }, true, null)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "native-country", null, false, null),
				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "age", "sex", "race" }, false, null),
				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "age", "sex", "race", "native-country" }, true, null)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "salary-class", null, false, null),
				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "age", "sex", "race" }, false, null),
				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "age", "sex", "race", "salary-class" }, true, null)
		}
	};
	
	private static ClassificationConfig[] adultConfig = new ClassificationConfig[] {
			new ClassificationConfig("adult_comma.csv", "workclass", null, false, null),
			new ClassificationConfig("adult_comma.csv", "education", null, false, null),
			new ClassificationConfig("adult_comma.csv", "marital-status", null, false, null),
			new ClassificationConfig("adult_comma.csv", "occupation", null, false, null),
			new ClassificationConfig("adult_comma.csv", "native-country", null, false, null),
			new ClassificationConfig("adult_comma.csv", "salary-class", null, false, null),
			new ClassificationConfig("adult_comma.csv", "race", null, false, null),
			new ClassificationConfig("adult_comma.csv", "sex", null, false, null)};
	private static ClassificationConfig[] adultBaseline = convertToBaselineConfig(adultConfig);
	
	private static ClassificationConfig[] acs13Config = new ClassificationConfig[] {
			new ClassificationConfig("ss13acs_essential_comma.csv", "CIT", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "COW", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SEX", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "FER", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DOUT", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DPHY", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DREM", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SCHG", null, false, null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SCHL", null, false, null)};
	private static ClassificationConfig[] acs13Baseline = convertToBaselineConfig(acs13Config);

	private static ClassificationConfig[] atusConfig = new ClassificationConfig[] {
			new ClassificationConfig("atus_comma.csv", "Region", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Age", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Sex", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Race", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Marital status", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Citizenship status", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Birthplace", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Highest level of school completed", null, false, null),
			new ClassificationConfig("atus_comma.csv", "Labor force status", null, false, null)};
	private static ClassificationConfig[] atusBaseline = convertToBaselineConfig(atusConfig);

	private static ClassificationConfig[] farsConfig = new ClassificationConfig[] {
			new ClassificationConfig("fars_comma.csv", "irace", null, false, "4"),
			new ClassificationConfig("fars_comma.csv", "isex", null, false, "4"),
			new ClassificationConfig("fars_comma.csv", "ihispanic", null, false, "4"),
			new ClassificationConfig("fars_comma.csv", "istatenum", null, false, "4"),
			new ClassificationConfig("fars_comma.csv", "iinjury", null, false, "4"),
			new ClassificationConfig("fars_comma.csv", "ideathmon", null, false, "4"),
			new ClassificationConfig("fars_comma.csv", "ideathday", null, false, "4")};
	private static ClassificationConfig[] farsBaseline = convertToBaselineConfig(farsConfig);
	
	private static ClassificationConfig[] ihisConfig = new ClassificationConfig[] {
			new ClassificationConfig("ihis_comma.csv", "PERNUM", null, false, "4"),
//			new ClassificationConfig("ihis_comma.csv", "REGION", null, false, "4"),
//			new ClassificationConfig("ihis_comma.csv", "MARSTAT", null, false, "4"),
//			new ClassificationConfig("ihis_comma.csv", "SEX", null, false, "4"),
//			new ClassificationConfig("ihis_comma.csv", "RACEA", null, false, "4"),
//			new ClassificationConfig("ihis_comma.csv", "EDUC", null, false, "4"),
/*			new ClassificationConfig("ihis_comma.csv", "QUARTER", null, false, "4")*/};
	private static ClassificationConfig[] ihisBaseline = convertToBaselineConfig(ihisConfig);
	
	
	private static void getDistinctValues(String inputFileName, String nominalAttributes, String resultFileName) {

		System.out.println("\nPreparing results for " + resultFileName);
		

		PrintWriter out = null;
		try {
			out = new PrintWriter(resultFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		out.println("Attribute;NumDistinctValues");
		
		Instances data = loadData(new ClassificationConfig(inputFileName, null, null, false, nominalAttributes));

		for (int i = 0; i < data.numAttributes(); i++) {

			if (data.attribute(i).isNominal()) {
				int numDistinctValues = data.numDistinctValues(i);
				out.println(data.attribute(i).name() + ": " + numDistinctValues);
				System.out.println("NumDistinctValues for attribute '" + data.attribute(i).name() + "': \t" + numDistinctValues);
			}
		}
		out.close();
	}

	private static void evaluateConfig(ClassificationConfig[] configs, String resultFileName) {
		
		System.out.println("\nPreparing results for " + resultFileName);
		
		double[] results = new double[configs.length];
		
		for (int i = 0; i < configs.length; i++) {

			Instances data = loadData(configs[i]);
			
			results[i] = getClassificationAccuracyFor(data, configs[i].getWorkloadAttribute(), Classifier.J48).pctCorrect();
			
			System.out.printf("Accuracy for attribute '" + configs[i].getWorkloadAttribute() + "': \t%.2f\n", results[i]);
			
		}
		
		writeResultsToFile(configs, results, resultFileName);
	}

	private static ClassificationConfig[] convertToBaselineConfig(ClassificationConfig[] configArray) {
		
		ClassificationConfig[] baselineConfig = new ClassificationConfig[configArray.length];
		
		for (int i = 0; i < configArray.length; i++) {
			baselineConfig[i] = new ClassificationConfig(
					configArray[i].getInputFileName(),
					configArray[i].getWorkloadAttribute(),
					new String[] { configArray[i].getWorkloadAttribute() },
					true, configArray[i].getNominalAttributes());
		}
		return baselineConfig;
	}

	private static void evaluateConfig(ClassificationConfig[][] configs, String fileName) {
		
		double[][] results = new double[configs.length][configs[0].length];
		
		for (int i = 0; i < configs.length; i++) {
			
			for (int j = 0; j < configs[i].length; j++) {

				Instances data = loadData(configs[i][j]);
				
				results[i][j] = getClassificationAccuracyFor(data, configs[i][j].getWorkloadAttribute(), Classifier.J48).pctCorrect();
				
				System.out.printf("Accuracy for attribute '" + configs[i][j].getWorkloadAttribute() + "': \t%.4f\n", results[i][j]);
				
			}
		}
		
		writeResultsToFile(configs, results, fileName);
		
	}
	
	
	/**
	 * @param classificationConfig TODO
	 * @return the Weka-dataset containing all but the filtered attributes
	 */
	private static Instances loadData(ClassificationConfig classificationConfig) {
		Instances data = null;
		String[] filteredAttributes = classificationConfig.getExcludedAttributes();
		String nominalAttributes = classificationConfig.getNominalAttributes();
		
		try {
			
			CSVLoader loader = new CSVLoader();
			
			loader.setSource(new File("data/" + classificationConfig.getInputFileName()));
			
			if (nominalAttributes != null) loader.setNominalAttributes(nominalAttributes);
			
			data = loader.getDataSet();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int[] filterNumbers;
		if (filteredAttributes == null || filteredAttributes.length == 0) {
			return data;
		} else {
			filterNumbers = calcAttrNumbersFromStringArray(data, filteredAttributes);
		}

		Instances filteredData = null;
		
		Remove remove = new Remove(); // new instance of filter
		
		try {
			
			remove.setAttributeIndicesArray(filterNumbers);
			
			remove.setInvertSelection(classificationConfig.isInvertedSelection()); // invert selection, if necessary

			remove.setInputFormat(data); // inform filter about dataset
			
			filteredData = Filter.useFilter(data, remove); // apply filter
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filteredData;
	}

	/**
	 * @param data the Weka dataset
	 * @param attribute the classifying attribute
	 * @param classifier the classifier to be used
	 * @return
	 */
	private static Evaluation getClassificationAccuracyFor(Instances data, String attribute, Classifier classifier) {
		
		data.setClassIndex(data.attribute(attribute).index());
		Evaluation eval = null;
		try {
			eval = new Evaluation(data);
			
			weka.classifiers.Classifier tree = null;
			switch (classifier) {
			case J48:
				J48 j48Tree = new J48();
				j48Tree.setConfidenceFactor(0.25f);
				j48Tree.setMinNumObj(2);
				tree = j48Tree;
				break;
			case NaiveBayes:
				tree = new NaiveBayes();
				break;
			case RandomForest:
				RandomForest rfTree = new RandomForest();
				rfTree.setNumTrees(100);
				rfTree.setNumFeatures(0);
				rfTree.setSeed(1);
				tree = rfTree;
				break;
			default:
				break;
			}
			
			// perform the actual classification using a fixed random seed for reproducability
			eval.crossValidateModel(tree, data, 10, new Random(1)); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eval;
	}


	private static int[] calcAttrNumbersFromStringArray(Instances data, String[] filteredAttributes) {
		
		if (filteredAttributes == null) return null;
		
		int[] attNumArray = new int[filteredAttributes.length];
		
		for (int i = 0; i < filteredAttributes.length; i++) {
			attNumArray[i] = data.attribute(filteredAttributes[i]).index();
		}
		
		return attNumArray;
	}
	
	private static void writeResultsToFile(ClassificationConfig[][] configs, double[][] results, String fileName) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		out.print("Attribute");
		for (int j = 0; j < configs[0].length; j++) {
			out.print(";" + Arrays.toString(configs[0][j].getExcludedAttributes()));
		}
		out.print("\n");
		for (int i = 0; i < configs.length; i ++) {
			out.print(configs[i][0].getWorkloadAttribute());
			for (int j = 0; j < configs[i].length; j++) {
				out.printf(";%.2f", results[i][j]);
			}
			out.print("\n");
		}
		
		out.close();
	}
	
	private static void writeResultsToFile(ClassificationConfig[] configs, double[] results, String fileName) {
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		out.println("Attribute;Accuracy");
		for (int i = 0; i < configs.length; i ++) {
			out.printf(configs[i].getWorkloadAttribute() + ";%.2f\n", results[i]);
		}
		
		out.close();
	}
}
