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

	public static void main(String[] args) {
		
		Instances data = loadData("adult_comma.csv", new String[] { "age", "sex", "race" });
		
		String[] workloadAttributes = new String[] {
				"workclass",
				"education",
				"marital-status",
				"occupation",
				"native-country",
				"salary-class"
				};
		
		for (String workloadAttribute : workloadAttributes) {
			
			Evaluation eval = getClassificationAccuracyFor(data, workloadAttribute, Classifier.RandomForest);
			
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
		if (filteredAttributes == null) {
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
