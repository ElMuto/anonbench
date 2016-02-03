package org.deidentifier.arx.execution;

import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import org.deidentifier.arx.ClassificationConfig;
import org.deidentifier.arx.ClassificationConfig.Classifier;

/**
 * @author work
 *
 */
public class CalculateClassificationAccuracies {
	
	private static final int NUM_CLASSIFICATION_CONSTELLATIONS = 2;
	private static final Classifier standardClassifier = Classifier.J48;

	public static void main(String[] args) {
		
		evaluateConfigs("results/CompleteComparison" + Classifier.J48.toString() + ".csv", new String[] {
				"dataset-name",
				"attribute-name",
				"PA-min (Zero-R)",
				"PA-max"
		});
	}

	private static void evaluateConfigs(String fileName, String[] header) {
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.print(header[0]);
		for (int j = 1; j < header.length; j++) {
			out.print(";" + header[j]);
		}
		out.print("\n");

		ClassificationConfig classificationConfig = new ClassificationConfig(null, null, null, null, null, null);

		Instances data = loadData(classificationConfig);

		out.printf(";%.2f", getClassificationAccuracyFor(data, classificationConfig.getClassAttribute(), classificationConfig.getClassifier()).pctCorrect());

		System.out.println("Accuracy for attribute '" + classificationConfig.getClassAttribute() + "' calculated");

		out.print("\n");
		out.flush();

		out.close();

	}
	

	/**
	 * @param classificationConfig
	 * @return the Weka-dataset containing all but the filtered attributes
	 */
	public static Instances loadData(ClassificationConfig classificationConfig) {
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
	}

	/**
	 * @param data the Weka dataset
	 * @param attribute the classifying attribute
	 * @param classifier the classifier to be used
	 * @return
	 */
	public static Evaluation getClassificationAccuracyFor(Instances data, String attribute, Classifier classifier) {

		data.setClassIndex(data.attribute(attribute).index());
		Evaluation eval = null;
		try {
			eval = new Evaluation(data);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

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
		case Zero_R:
			tree = new ZeroR();
			break;
		default:
			throw new RuntimeException("Unsupported method: " + classifier.toString());
		}

		try {
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
}
