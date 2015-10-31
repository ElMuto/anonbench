package org.deidentifier.arx.execution;

import weka.core.converters.CSVLoader;

import java.io.File;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class CalculateClassificationAccuracies {

	public static void main(String[] args) {
		Instances data = loadData("adult_comma.csv");
		
		String[] attributes = new String[] {
				"workclass",
				"education",
				"marital-status",
				"occupation",
				"native-country",
				"salary-class"
				};
		
		for (String attribute : attributes) {
			Evaluation eval = getClassificationAccuracyFor(data, attribute);

			System.out.printf("Accuracy for attribute '" + attribute + "': \t%.4f\n", eval.pctCorrect());
		}
	}

	private static Evaluation getClassificationAccuracyFor(Instances data, String attribute) {
		data.setClassIndex(data.attribute(attribute).index());
		
		Evaluation eval = null;
		try {
			eval = new Evaluation(data);
			J48 tree = new J48();
			tree.setOptions(new String[] { "-C", "0.25", "-M", "2" });
			eval.crossValidateModel(tree, data, 10, new Random(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eval;
	}

	private static Instances loadData(String filename) {
		Instances data = null;
		
		try {
			CSVLoader loader = new CSVLoader();
			loader.setSource(new File("data/" + filename));
			data = loader.getDataSet();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
}
