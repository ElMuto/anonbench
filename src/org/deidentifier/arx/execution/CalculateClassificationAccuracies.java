package org.deidentifier.arx.execution;

import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import org.deidentifier.arx.ClassificationConfig;

public class CalculateClassificationAccuracies {


	public static void main(String[] args) {
		evaluateConfig(completeCompare, "results/CompleteComparison.csv", new String[] {
				"dataset-name",
				"attribute-name",
				"PA-max",
				"PA-min",
				"PA-IS-only",
				"PA-QI-only"
		});

	}

	private enum Classifier {
		J48,
		RandomForest,
		NaiveBayes
	}

	private static ClassificationConfig[][] completeCompare = new ClassificationConfig[][] {
		new ClassificationConfig[] {
				new ClassificationConfig("Adult", "adult_comma.csv", "workclass", null, false, null),
				new ClassificationConfig("Adult", "adult_comma.csv", "workclass", null, false, null).asBaselineConfig(),
				new ClassificationConfig("Adult", "adult_comma.csv", "workclass", new String[] { "age", "sex", "race" }, false, null),
				new ClassificationConfig("Adult", "adult_comma.csv", "workclass", new String[] { "age", "sex", "race", "workclass" }, true, null)
		}//, new ClassificationConfig[] {
//				new ClassificationConfig("adult_comma.csv", "education", null, false, null),
//				new ClassificationConfig("adult_comma.csv", "education", null, false, null).asBaselineConfig(),
//				new ClassificationConfig("adult_comma.csv", "education", new String[] { "age", "sex", "race" }, false, null),
//				new ClassificationConfig("adult_comma.csv", "education", new String[] { "age", "sex", "race", "education" }, true, null)
//		}, new ClassificationConfig[] {
//				new ClassificationConfig("adult_comma.csv", "marital-status", null, false, null),
//				new ClassificationConfig("adult_comma.csv", "marital-status", null, false, null).asBaselineConfig(),
//				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "age", "sex", "race" }, false, null),
//				new ClassificationConfig("adult_comma.csv", "marital-status", new String[] { "age", "sex", "race", "marital-status" }, true, null)
//		}, new ClassificationConfig[] {
//				new ClassificationConfig("adult_comma.csv", "occupation", null, false, null),
//				new ClassificationConfig("adult_comma.csv", "occupation", null, false, null).asBaselineConfig(),
//				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "age", "sex", "race" }, false, null),
//				new ClassificationConfig("adult_comma.csv", "occupation", new String[] { "age", "sex", "race", "occupation" }, true, null)
//		}, new ClassificationConfig[] {
//				new ClassificationConfig("adult_comma.csv", "native-country", null, false, null),
//				new ClassificationConfig("adult_comma.csv", "native-country", null, false, null).asBaselineConfig(),
//				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "age", "sex", "race" }, false, null),
//				new ClassificationConfig("adult_comma.csv", "native-country", new String[] { "age", "sex", "race", "native-country" }, true, null)
//		}, new ClassificationConfig[] {
//				new ClassificationConfig("adult_comma.csv", "salary-class", null, false, null),
//				new ClassificationConfig("adult_comma.csv", "salary-class", null, false, null).asBaselineConfig(),
//				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "age", "sex", "race" }, false, null),
//				new ClassificationConfig("adult_comma.csv", "salary-class", new String[] { "age", "sex", "race", "salary-class" }, true, null)
//		}
	};
	
	private static int getNumDistinctValues(String datasetName, String inputFileName, String nominalAttributes, String attributeName) {
		
		Instances data = loadData(new ClassificationConfig(datasetName, inputFileName, null, null, false, nominalAttributes));
				
		return data.numDistinctValues(data.attribute(attributeName));
	}

	private static void evaluateConfig(ClassificationConfig[][] configs, String fileName, String[] header) {
		
		double[][] results = new double[configs.length][configs[0].length];
		
		for (int i = 0; i < configs.length; i++) {
			
			for (int j = 0; j < configs[i].length; j++) {

				Instances data = loadData(configs[i][j]);
				
				results[i][j] = getClassificationAccuracyFor(data, configs[i][j].getWorkloadAttribute(), Classifier.J48).pctCorrect();
				
				System.out.printf("Accuracy for attribute '" + configs[i][j].getWorkloadAttribute() + "': \t%.4f\n", results[i][j]);
				
			}
		}
		
		writeResultsToFile(configs, results, fileName, header);
		
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
	
	private static void writeResultsToFile(ClassificationConfig[][] configs, double[][] results, String fileName, String[] header) {
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
		for (int i = 0; i < configs.length; i ++) {
			out.print(configs[i][0].getDatasetName() + ";");
			out.print(configs[i][0].getWorkloadAttribute());
			for (int j = 0; j < results[i].length; j++) {
				out.printf(";%.2f", results[i][j]);
			}
			out.print(getNumDistinctValues(configs[i][0].getDatasetName(), configs[i][0].getInputFileName(), configs[i][0].getNominalAttributes(), configs[i][0].getWorkloadAttribute()));
			out.print("\n");
		}
		
		out.close();
	}
}
