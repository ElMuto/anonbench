package org.deidentifier.arx.execution;

import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class CalculateClassificationAccuracies {

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
			
			Evaluation eval = getClassificationAccuracyFor(data, workloadAttribute);
			
			System.out.printf("Accuracy for attribute '" + workloadAttribute + "': \t%.4f\n", eval.pctCorrect());
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
