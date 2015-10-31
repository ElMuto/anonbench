package org.deidentifier.arx.execution;

import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class CalculateClassificationAccuracies {
	
	private enum Classifier {
		J48,
		RandomForest,
		NaiveBayes
	}
	
	private class Configuration {
		private final String fileName;
		private final String workloadAttribute;
		private final String[] excludedAttributes;
		
		Configuration (String fileName, String workloadAttribute, String[] excludedAttributes) {
			this.fileName = fileName;
			this.workloadAttribute = workloadAttribute;
			this.excludedAttributes = excludedAttributes;
		}

		public String getFileName() {
			return fileName;
		}

		public String getWorkloadAttribute() {
			return workloadAttribute;
		}

		public String[] getExcludedAttributes() {
			return excludedAttributes;
		}
	}

	public static void main(String[] args) {
		
		String[][] configurations = new String[][] {
			new String[] { "adult_comma.csv", "workclass"                            },
			new String[] { "adult_comma.csv", "workclass",      "age", "sex", "race" },
			new String[] { "adult_comma.csv", "workclass",      "education", "marital-status", "occupation", "native-country", "salary-class" },

			new String[] { "adult_comma.csv", "education",                           },
			new String[] { "adult_comma.csv", "education",      "age", "sex", "race" },
			new String[] { "adult_comma.csv", "education",      "workclass", "marital-status", "occupation", "native-country", "salary-class" },

			new String[] { "adult_comma.csv", "marital-status",                      },
			new String[] { "adult_comma.csv", "marital-status", "age", "sex", "race" },
			new String[] { "adult_comma.csv", "marital-status", "workclass", "education", "occupation", "native-country", "salary-class" },

			new String[] { "adult_comma.csv", "occupation",                          },
			new String[] { "adult_comma.csv", "occupation",     "age", "sex", "race" },
			new String[] { "adult_comma.csv", "occupation",     "workclass", "education", "marital-status", "native-country", "salary-class" },

			new String[] { "adult_comma.csv", "native-country",                      },
			new String[] { "adult_comma.csv", "native-country", "age", "sex", "race" },
			new String[] { "adult_comma.csv", "native-country", "workclass", "education", "marital-status", "occupation", "salary-class" },

			new String[] { "adult_comma.csv", "salary-class",                        },
			new String[] { "adult_comma.csv", "salary-class",   "age", "sex", "race" },
			new String[] { "adult_comma.csv", "salary-class",   "workclass", "education", "marital-status", "occupation", "native-country" },

		};
		
		for (String[] configuration : configurations) {
			
			String fileName = configuration[0];
			String workloadAttribute = configuration[1];
			String[] filteredAttributes = new String[configuration.length - 2];
			
			for (int i = 2; i < configuration.length; i++) {
				filteredAttributes[i - 2] = configuration[i];
			}
			
			Instances data = loadData(fileName, filteredAttributes);
			
			Evaluation eval = getClassificationAccuracyFor(data, workloadAttribute, Classifier.J48);
			System.out.printf("Accuracy for attribute '" + workloadAttribute + "': \t%.4f\n", eval.pctCorrect());
		}
	}

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
			eval.crossValidateModel(tree, data, 10, new Random(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eval;
	}

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

	private static String calculateFilteredAttributeNumbers(Instances data, String[] filteredAttributes) {
		String attNumString = "";
		for (int i = 0; i < filteredAttributes.length - 1; i++) {
			attNumString += String.valueOf(data.attribute(filteredAttributes[i]).index() + 1) + ",";
		}
		attNumString += String.valueOf(data.attribute(filteredAttributes[filteredAttributes.length - 1]).index() + 1) + ",";
		
		return attNumString;
	}
}
