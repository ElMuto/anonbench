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
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import org.deidentifier.arx.ClassificationConfig;

public class CalculateClassificationAccuracies {
	
	private static int NUM_CLASSIFICATION_CONSTELLATIONS = 4;


	public static void main(String[] args) {
		evaluateConfig(mergeConfigBlocks(configCluster), "results/CompleteComparison.csv", new String[] {
				"dataset-name",
				"attribute-name",
				"Num-distinct-attributes",
				"PA-max",
				"PA-min",
				"PA-IS-only",
				"PA-QI-only",
		});

	}

	private enum Classifier {
		J48,
		RandomForest,
		NaiveBayes
	}
	
	private static ClassificationConfig[][][] configCluster = new ClassificationConfig[][][] {
		
		buildAnalysisConfigurations(
				"Adult BS Marital",
				"adult_comma.csv",
				new String[] { "age", "occupation", "education" },
				new String[] {
						"workclass",
						"education",
						"marital-status",
						"occupation",
						"native-country",
						"salary-class",
						"race",
						"sex",
						},
				null),
		
		buildAnalysisConfigurations(
				"Adult BS Occupation",
				"adult_comma.csv",
				new String[] { "age", "sex", "race" },
				new String[] {
						"workclass",
						"education",
						"marital-status",
						"occupation",
						"native-country",
						"salary-class",
						"race",
						"sex",
						},
				null),
		
		buildAnalysisConfigurations(
				"Adult",
				"adult_comma.csv",
				new String[] { "sex", "age", "race", "marital-status" },
				new String[] {
						"workclass",
						"education",
						"marital-status",
						"occupation",
						"native-country",
						"salary-class",
						"race",
						"sex",
						},
				null),
		
		buildAnalysisConfigurations(
				"Fars",
				"fars_comma.csv",
				new String[] { "iage", "irace", "isex", "ihispanic" },
				new String[] {
						"irace",
						"ideathmon",
						"ideathday",
						"isex",
						"ihispanic",
						"istatenum",
						"iinjury"
						},
				"4"),
		
		buildAnalysisConfigurations(
				"ACS13",
				"ss13acs_essential_comma.csv",
				new String[] { "AGEP", "CIT", "COW", "SEX" },
				new String[] {
						"CIT",
						"COW",
						"SEX",
						"FER",
						"DOUT",
						"DPHY",
						"DREM",
						"SCHG",
						"SCHL"
						},
				null),
		
		buildAnalysisConfigurations(
				"Atus",
				"atus_comma.csv",
				new String[] { "Region", "Age", "Sex", "Race" },
				new String[] {
						"Region",
						"Sex",
						"Race",
						"Marital status",
						"Citizenship status",
						"Birthplace",
						"Highest level of school completed",
						"Labor force status"
						},
				null),
		
		buildAnalysisConfigurations(
				"Ihis",
				"ihis_comma.csv",
				new String[] { "REGION", "AGE", "SEX", "RACEA" },
				new String[] {
						"YEAR",
						"QUARTER",
						"REGION",
						"PERNUM",
						"MARSTAT",
						"SEX",
						"RACEA",
						"EDUC"
						},
				"1,4"),
	};

	private static ClassificationConfig[][] mergeConfigBlocks (ClassificationConfig[][][] configBlockArray) {
		
		List <ClassificationConfig[]> configList = new ArrayList<>();
		
		for (int i = 0; i < configBlockArray.length; i++) {
			
			for (int j = 0; j < configBlockArray[i].length; j++) {

				configList.add(configBlockArray[i][j]);
				
			}
						
		}
		
		ClassificationConfig[][] configArray = new ClassificationConfig[configList.size()][NUM_CLASSIFICATION_CONSTELLATIONS];
		
		configArray = configList.toArray(configArray);
		
		return configArray;
		
	}

	private static void evaluateConfig(ClassificationConfig[][] configs, String fileName, String[] header) {
		
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
				
		for (int i = 0; i < configs.length; i++) {
			
			out.print(configs[i][0].getDatasetName() + ";");
			
			out.print(configs[i][0].getWorkloadAttribute());

			System.out.println("Calculating number of distinct attributes for '" + configs[i][0].getWorkloadAttribute() + "'");
			out.print(";" + Integer.valueOf(getNumDistinctValues(configs[i][0].getDatasetName(), configs[i][0].getInputFileName(), configs[i][0].getNominalAttributes(), configs[i][0].getWorkloadAttribute())));
			
			for (int j = 0; j < configs[i].length; j++) {

				if (configs[i][j] != null ) {
					
					Instances data = loadData(configs[i][j]);

					out.printf(";%.2f", getClassificationAccuracyFor(data, configs[i][j].getWorkloadAttribute(), Classifier.J48).pctCorrect());

					System.out.printf("Accuracy for attribute '" + configs[i][j].getWorkloadAttribute() + "': \t%.4f\n", getClassificationAccuracyFor(data, configs[i][j].getWorkloadAttribute(), Classifier.J48).pctCorrect());

				} else {
					
					out.print(";");
					
				}
				
				out.flush();
			}
			
			out.print("\n");
			
		}

		out.close();
		
	}
	
	
	/**
	 * @param id
	 * @param inputFileName
	 * @param qis
	 * @param classifiers
	 * @param nominalAttributes
	 * @return
	 */
	private static ClassificationConfig[][] buildAnalysisConfigurations(String id, String inputFileName,
			String[] qis, String[] classifiers, String nominalAttributes) {

		int numClassifiers = classifiers.length;
		
		ClassificationConfig[][] configArray = new ClassificationConfig[numClassifiers][NUM_CLASSIFICATION_CONSTELLATIONS];
		
		for (int i = 0; i < numClassifiers; i++) {
			
			configArray[i][0] = new ClassificationConfig(id, inputFileName, classifiers[i], null, false, nominalAttributes);
			
			configArray[i][1] = new ClassificationConfig(id, inputFileName, classifiers[i], null, false, nominalAttributes).asBaselineConfig();
			
			if (!isClassifierQi(classifiers[i], qis)) {
				
				configArray[i][2] = new ClassificationConfig(id, inputFileName, classifiers[i], qis, false, nominalAttributes);
				
				
				configArray[i][3] = new ClassificationConfig(id, inputFileName, classifiers[i], qis, false, nominalAttributes).invertQIs();
				
			} else {
				
				configArray[i][2] = null;
				
				configArray[i][3] = null;
				
			}
			
		}
		
		return configArray;
	}


	private static boolean isClassifierQi(String classifier, String[] qis) {
		
		for (int i = 0; i < qis.length; i++) {
			
			if (classifier.equals(qis[i])) return true;
			
		}
		
		return false;
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
	
	private static int getNumDistinctValues(String datasetName, String inputFileName, String nominalAttributes, String attributeName) {
		
		Instances data = loadData(new ClassificationConfig(datasetName, inputFileName, null, null, false, nominalAttributes));
				
		return data.numDistinctValues(data.attribute(attributeName));
	}
}
