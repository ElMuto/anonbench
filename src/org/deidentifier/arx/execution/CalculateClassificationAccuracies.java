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
	
	private static final int NUM_CLASSIFICATION_CONSTELLATIONS = 4;
	private static final Classifier standardClassifier = Classifier.J48;

	public static void main(String[] args) {
		
		evaluateConfigs(mergeConfigBlocks(configCluster), "results/CompleteComparison" + Classifier.J48.toString() + ".csv", new String[] {
				"dataset-name",
				"attribute-name",
				"Num-distinct-attributes",
				"PA-max",
				"PA-min (Zero-R)",
				"PA-IS-only",
				"PA-QI-only"
		});
	}
	
	private static ClassificationConfig[][][] configCluster = new ClassificationConfig[][][] {
		
		buildClassificationConfigurations(
				"Adult BS Marital",
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
				"adult_comma.csv",
				null, standardClassifier),
		
		buildClassificationConfigurations(
				"Adult BS Occupation",
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
				"adult_comma.csv",
				null, standardClassifier),
		
		buildClassificationConfigurations(
				"Adult",
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
				"adult_comma.csv",
				null, standardClassifier),
		
		buildClassificationConfigurations(
				"Fars",
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
				"fars_comma.csv",
				"4", standardClassifier),
		
		buildClassificationConfigurations(
				"ACS13",
				new String[] { "Age", "Citizenship", "Married", "Sex" },
				new String[] {
						"Citizenship",
						"Married",
						"Sex",
						"Childbirth",
						"Independent living",
						"Ambulatory",
						"Cognitive",
						"Grade level",
						"Education"
						},
				"ss13acs_comma.csv",
				null, standardClassifier),
		
		buildClassificationConfigurations(
				"Atus",
				new String[] { "Marital status", "Age", "Sex", "Race" },
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
				"atus_comma.csv",
				null, standardClassifier),
		
		buildClassificationConfigurations(
				"Ihis",
				new String[] { "MARSTAT", "AGE", "SEX", "RACEA" },
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
				"ihis_comma.csv",
				"1,4", standardClassifier),
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

	private static void evaluateConfigs(ClassificationConfig[][] configs, String fileName, String[] header) {
		
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
			
			out.print(configs[i][0].getId() + ";");
			
			out.print(configs[i][0].getClassAttribute());

			System.out.println("Calculating number of distinct attributes for dataset '" + configs[i][0].getId() + "' / class attribute '" + configs[i][0].getClassAttribute() + "'");
			out.print(";" + Integer.valueOf(getNumDistinctValues(configs[i][0].getId(), configs[i][0].getInputFileName(), configs[i][0].getNominalAttributes(), configs[i][0].getClassAttribute())));
			
			for (int j = 0; j < configs[i].length; j++) {

				if (configs[i][j] != null ) {
					
					Instances data = loadData(configs[i][j]);

					out.printf(";%.2f", getClassificationAccuracyFor(data, configs[i][j].getClassAttribute(), configs[i][j].getClassifier()).pctCorrect());

					System.out.println("Accuracy for attribute '" + configs[i][j].getClassAttribute() + "' calculated");

				} else {
					
					out.print(";");
					
				}
				
			}
			
			out.print("\n");
			out.flush();
			
		}

		out.close();
		
	}
	
	
	/** Builds a matrix of classification configurations. The rows iterate over the different <code>classAttributes</code>.
	 * The columns represent 4 different predictor settings for each class attribute based on the supplied list of <code>
	 * qis</code>:<ol>
	 * <li>use all available attributes as predictors</li>
	 * <li>use Zero-R for classification</li>
	 * <li>use all attributes except the <code>qis</code> as predictors</li>
	 * <li>use only the <code>qis</code> as predictors</li>
	 * </ol>
	 * @param id used for identifying the configuration
	 * @param qis list of qis that determine the predictor configurations
	 * @param classAttributes
	 * @param inputFileName
	 * @param nominalAttributes explicitly treat these attributes as nominal
	 * @param classifier one of the available classification algorithms
	 * @return
	 */
	private static ClassificationConfig[][] buildClassificationConfigurations(String id, String[] qis,
			String[] classAttributes, String inputFileName, String nominalAttributes, Classifier classifier) {

		int numClassAttributes = classAttributes.length;
		
		ClassificationConfig[][] configArray = new ClassificationConfig[numClassAttributes][NUM_CLASSIFICATION_CONSTELLATIONS];
		
		for (int i = 0; i < numClassAttributes; i++) {
			
			configArray[i][0] = new ClassificationConfig(id, classifier, inputFileName, classAttributes[i], null, nominalAttributes);
			
			configArray[i][1] = new ClassificationConfig(id, classifier, inputFileName, classAttributes[i], null, nominalAttributes).asBaselineConfig();
			
			if (!isQi(classAttributes[i], qis)) {
				
				configArray[i][2] = new ClassificationConfig(id, classifier, inputFileName, classAttributes[i], qis, nominalAttributes);
								
				configArray[i][3] = new ClassificationConfig(id, classifier, inputFileName, classAttributes[i], qis, nominalAttributes).invertExclusionSet();
				
			} else {
				
				configArray[i][2] = null;
				
				configArray[i][3] = null;
				
			}
			
		}
		
		return configArray;
	}


	private static boolean isQi(String classAttribute, String[] qis) {
		
		for (int i = 0; i < qis.length; i++) {
			
			if (classAttribute.equals(qis[i])) return true;
			
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
	
	private static int getNumDistinctValues(String id, String inputFileName, String nominalAttributes, String attributeName) {
		
		Instances data = loadData(new ClassificationConfig(id, Classifier.Zero_R, inputFileName, null, null, nominalAttributes));
				
		return data.numDistinctValues(data.attribute(attributeName));
	}
}
