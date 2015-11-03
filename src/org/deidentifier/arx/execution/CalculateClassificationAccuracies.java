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
				new ClassificationConfig("adult_comma.csv", "workclass", null, null, false),
				new ClassificationConfig("adult_comma.csv", "workclass", new String[] { "age", "sex", "race" }, null, false),
				new ClassificationConfig("adult_comma.csv", "workclass", new String[] { "age", "sex", "race", "workclass" }, null, true)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "education", null, null, false),
				new ClassificationConfig("adult_comma.csv", "education", new String[] { "age", "sex", "race" }, null, false),
				new ClassificationConfig("adult_comma.csv", "education", new String[] { "age", "sex", "race", "education" }, null, true)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "marital-status", null, null, false),
				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "age", "sex", "race" }, null, false),
				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "age", "sex", "race", "marital-status" }, null, true)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "occupation", null, null, false),
				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "age", "sex", "race" }, null, false),
				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "age", "sex", "race", "occupation" }, null, true)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "native-country", null, null, false),
				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "age", "sex", "race" }, null, false),
				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "age", "sex", "race", "native-country" }, null, true)
		}, new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "salary-class", null, null, false),
				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "age", "sex", "race" }, null, false),
				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "age", "sex", "race", "salary-class" }, null, true)
		}
	};
	
	private static ClassificationConfig[] adultConfig = new ClassificationConfig[] {
			new ClassificationConfig("adult_comma.csv", "workclass", null, null, false),
			new ClassificationConfig("adult_comma.csv", "education", null, null, false),
			new ClassificationConfig("adult_comma.csv", "marital-status", null, null, false),
			new ClassificationConfig("adult_comma.csv", "occupation", null, null, false),
			new ClassificationConfig("adult_comma.csv", "native-country", null, null, false),
			new ClassificationConfig("adult_comma.csv", "salary-class", null, null, false),
			new ClassificationConfig("adult_comma.csv", "race", null, null, false),
			new ClassificationConfig("adult_comma.csv", "sex", null, null, false)};
	private static ClassificationConfig[] adultBaseline = convertToBaselineConfig(adultConfig);
	
	private static ClassificationConfig[] acs13Config = new ClassificationConfig[] {
			new ClassificationConfig("ss13acs_essential_comma.csv", "CIT", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "COW", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SEX", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "FER", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DOUT", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DPHY", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DREM", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SCHG", null, null, false),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SCHL", null, null, false)};
	private static ClassificationConfig[] acs13Baseline = convertToBaselineConfig(acs13Config);

	private static ClassificationConfig[] atusConfig = new ClassificationConfig[] {
			new ClassificationConfig("atus_comma.csv", "Region", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Age", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Sex", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Race", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Marital status", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Citizenship status", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Birthplace", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Highest level of school completed", null, null, false),
			new ClassificationConfig("atus_comma.csv", "Labor force status", null, null, false)};
	private static ClassificationConfig[] atusBaseline = convertToBaselineConfig(atusConfig);

	private static ClassificationConfig[] farsConfig = new ClassificationConfig[] {
			new ClassificationConfig("fars_comma.csv", "irace", null, "4", false),
			new ClassificationConfig("fars_comma.csv", "isex", null, "4", false),
			new ClassificationConfig("fars_comma.csv", "ihispanic", null, "4", false),
			new ClassificationConfig("fars_comma.csv", "istatenum", null, "4", false),
			new ClassificationConfig("fars_comma.csv", "iinjury", null, "4", false),
			new ClassificationConfig("fars_comma.csv", "ideathmon", null, "4", false),
			new ClassificationConfig("fars_comma.csv", "ideathday", null, "4", false)};
	private static ClassificationConfig[] farsBaseline = convertToBaselineConfig(farsConfig);
	
	private static ClassificationConfig[] ihisConfig = new ClassificationConfig[] {
			new ClassificationConfig("ihis_comma.csv", "REGION", null, null, false),
			new ClassificationConfig("ihis_comma.csv", "MARSTAT", null, null, false),
			new ClassificationConfig("ihis_comma.csv", "SEX", null, null, false),
			new ClassificationConfig("ihis_comma.csv", "RACEA", null, null, false),
			new ClassificationConfig("ihis_comma.csv", "EDUC", null, null, false),
			new ClassificationConfig("ihis_comma.csv", "QUARTER", null, null, false)};
	private static ClassificationConfig[] ihisBaseline = convertToBaselineConfig(ihisConfig);

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
					configArray[i].getNominalAttributes(), true);
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
