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
	
	private enum Classifier {
		J48,
		RandomForest,
		NaiveBayes
	}
	

	
	private static ClassificationConfig[][] adultConfig = new ClassificationConfig[][] {
		new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "workclass", null),
				new ClassificationConfig("adult_comma.csv", "workclass", new String[] { "age", "sex", "race" }),
				new ClassificationConfig("adult_comma.csv", "workclass", new String[] { "education", "marital-status", "occupation", "native-country", "salary-class" })
		},
		new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "education", null),
				new ClassificationConfig("adult_comma.csv", "education", new String[] { "age", "sex", "race" }),
				new ClassificationConfig("adult_comma.csv", "education", new String[] { "workclass", "marital-status", "occupation", "native-country", "salary-class" })
		},
		new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "marital-status", null),
				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "age", "sex", "race" }),
				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "workclass", "education", "occupation", "native-country", "salary-class" })
		},
		new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "occupation", null),
				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "age", "sex", "race" }),
				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "workclass", "education", "marital-status", "native-country", "salary-class" })
		},
		new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "native-country", null),
				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "age", "sex", "race" }),
				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "workclass", "education", "marital-status", "occupation", "salary-class" })
		},
		new ClassificationConfig[] {
				new ClassificationConfig("adult_comma.csv", "salary-class", null),
				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "age", "sex", "race" }),
				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "workclass", "education", "marital-status", "occupation", "native-country" })
		}
	};
	
	private static ClassificationConfig[] cupConfig = new ClassificationConfig[] {
				new ClassificationConfig("cup_comma.csv", "STATE", null),
	};
	
	private static ClassificationConfig[] atusConfig = new ClassificationConfig[] {
			new ClassificationConfig("atus_comma.csv", "Region", null),
			new ClassificationConfig("atus_comma.csv", "Age", null),
			new ClassificationConfig("atus_comma.csv", "Sex", null),
			new ClassificationConfig("atus_comma.csv", "Race", null),
			new ClassificationConfig("atus_comma.csv", "Marital status", null),
			new ClassificationConfig("atus_comma.csv", "Citizenship status", null),
			new ClassificationConfig("atus_comma.csv", "Birthplace", null),
			new ClassificationConfig("atus_comma.csv", "Highest level of school completed", null),
			new ClassificationConfig("atus_comma.csv", "Labor force status", null),
	};
	
	private static ClassificationConfig[] farsConfig = new ClassificationConfig[] {
			new ClassificationConfig("fars_comma.csv", "irace", null),
			new ClassificationConfig("fars_comma.csv", "isex", null),
			new ClassificationConfig("fars_comma.csv", "ihispanic", null),
			new ClassificationConfig("fars_comma.csv", "istatenum", null),
			new ClassificationConfig("fars_comma.csv", "iinjury", null),
			new ClassificationConfig("fars_comma.csv", "ideathmon", null),
			new ClassificationConfig("fars_comma.csv", "ideathday", null)
	};
	
	private static ClassificationConfig[] ihisConfig = new ClassificationConfig[] {
			new ClassificationConfig("ihis_comma.csv", "REGION", null),
			new ClassificationConfig("ihis_comma.csv", "MARSTAT", null),
			new ClassificationConfig("ihis_comma.csv", "SEX", null),
			new ClassificationConfig("ihis_comma.csv", "RACEA", null),
			new ClassificationConfig("ihis_comma.csv", "EDUC", null),
			new ClassificationConfig("ihis_comma.csv", "QUARTER", null)
	};
	
	private static ClassificationConfig[] acs13Config = new ClassificationConfig[] {
			new ClassificationConfig("ss13acs_essential_comma.csv", "CIT", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "COW", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SEX", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "FER", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DOUT", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DPHY", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "DREM", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SCHG", null),
			new ClassificationConfig("ss13acs_essential_comma.csv", "SCHL", null)
	};


	public static void main(String[] args) {
		
		evaluateConfig(adultConfig, "results/classificationResultsAdult.csv");
		evaluateConfig(cupConfig, "results/classificationResultsCup.csv");
		evaluateConfig(atusConfig, "results/classificationResultsAtus.csv");
		evaluateConfig(farsConfig, "results/classificationResultsFars.csv");
		evaluateConfig(acs13Config, "results/classificationResultsAcs13.csv");
		evaluateConfig(ihisConfig, "results/classificationResultsIhis.csv");
		
	}

	private static void evaluateConfig(ClassificationConfig[] configs, String resultFileName) {
		
		System.out.println("\nPreparing results for " + resultFileName);
		
		double[] results = new double[configs.length];
		
		for (int i = 0; i < configs.length; i++) {

			Instances data = loadData(configs[i].getInputFileName(), configs[i].getExcludedAttributes());
			
			results[i] = getClassificationAccuracyFor(data, configs[i].getWorkloadAttribute(), Classifier.J48).pctCorrect();
			
			System.out.printf("Accuracy for attribute '" + configs[i].getWorkloadAttribute() + "': \t%.4f\n", results[i]);
			
		}
		
		writeResultsToFile(configs, results, resultFileName);
	}

	private static void evaluateConfig(ClassificationConfig[][] adultConfig, String fileName) {
		
		double[][] results = new double[adultConfig.length][adultConfig[0].length];
		
		for (int i = 0; i < adultConfig.length; i++) {
			
			for (int j = 0; j < adultConfig[i].length; j++) {

				Instances data = loadData(adultConfig[i][j].getInputFileName(), adultConfig[i][j].getExcludedAttributes());
				
				results[i][j] = getClassificationAccuracyFor(data, adultConfig[i][j].getWorkloadAttribute(), Classifier.J48).pctCorrect();
				
				System.out.printf("Accuracy for attribute '" + adultConfig[i][j].getWorkloadAttribute() + "': \t%.4f\n", results[i][j]);
				
			}
		}
		
		writeResultsToFile(adultConfig, results, fileName);
		
	}
	
	
	/**
	 * @param filename name of <B>comma</B>-separated file
	 * @param filteredAttributes array of attribute names to be neglected for analysis
	 * @return the Weka-dataset containing all but the filtered attributes
	 */
	private static Instances loadData(String filename, String[] filteredAttributes) {
		Instances data = null;
		
		try {
			
			CSVLoader loader = new CSVLoader();
			
			loader.setSource(new File("data/" + filename));
			
			data = loader.getDataSet();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String filterNumbers = null;
		if (filteredAttributes == null || filteredAttributes.length == 0) {
			return data;
		} else {
			filterNumbers = calculateFilteredAttributeNumbers(data, filteredAttributes);
		}

		Instances filteredData = null;
		
		Remove remove = new Remove(); // new instance of filter
		
		try {
			
			remove.setOptions(new String[] { "-R", filterNumbers });
			
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


	private static String calculateFilteredAttributeNumbers(Instances data, String[] filteredAttributes) {
		String attNumString = "";
		for (int i = 0; i < filteredAttributes.length - 1; i++) {
			attNumString += String.valueOf(data.attribute(filteredAttributes[i]).index() + 1) + ",";
		}
		attNumString += String.valueOf(data.attribute(filteredAttributes[filteredAttributes.length - 1]).index() + 1) + ",";
		
		return attNumString;
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
				out.print(";" + results[i][j]);
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
			out.println(configs[i].getWorkloadAttribute() + ";" + results[i]);
		}
		
		out.close();
	}
}
