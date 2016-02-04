package org.deidentifier.arx.execution;
import java.util.Set;

import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.ClassificationConfig;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.ClassificationConfig.Classifier;

/**
 * @author work
 *
 */
public class DetermineDependencies {
	
	private static final Classifier standardClassifier = Classifier.J48;

	public static void main(String[] args) {
		
		evaluateConfigs("results/CompleteComparison" + standardClassifier.toString() + ".csv", new String[] {
				"dataset-name",
				"numFeatures",
				"features",
				"target",
				"PA-min (Zero-R)",
				"PA-max",
				"distance"
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

		BenchmarkDatafile[] datafiles = new BenchmarkDatafile[] {
//				BenchmarkDatafile.ADULT,
//				BenchmarkDatafile.FARS,
//				BenchmarkDatafile.ACS13,
				BenchmarkDatafile.ATUS,
				BenchmarkDatafile.IHIS,
				};
		
		for (BenchmarkDatafile datafile : datafiles) {

			String[] attributesArray = BenchmarkSetup.getAllAttributes(datafile);
			Set<String> attributes = new HashSet<>(Arrays.asList(attributesArray));
			String nominalAttributes = null;
			if (datafile.equals(BenchmarkDatafile.ADULT)) nominalAttributes = "2";
			if (datafile.equals(BenchmarkDatafile.CUP))   nominalAttributes = "1,2,4,6,7,8";
			if (datafile.equals(BenchmarkDatafile.FARS))  nominalAttributes = "1,4";
			if (datafile.equals(BenchmarkDatafile.ACS13))  nominalAttributes = "1,3,4";
			if (datafile.equals(BenchmarkDatafile.ATUS)) nominalAttributes =  "2";
			if (datafile.equals(BenchmarkDatafile.IHIS))  nominalAttributes = "1,4,5";

			for (String classAttribute : attributes) {

				Set<String> restAttributes = new HashSet<>(attributes);
				restAttributes.remove(classAttribute);

				Set<Set<String>> limitedPowerSet = DetermineDependencies.getLimitedPowerset(restAttributes, 7);
				for (Set<String> features : limitedPowerSet) {
					System.out.printf("datset=%s, class=%s, features=%s\n", datafile.toString(), classAttribute, features.toString());
					String[] featureArray = features.toArray(new String[features.size()]);

					// classify
					ClassificationConfig classificationConfig = new ClassificationConfig(
							"FullAttributeSet", standardClassifier, datafile.getBaseStringForFilename() + "_comma.csv",
							classAttribute, featureArray, nominalAttributes).invertExclusionSet();
					ClassificationConfig baselineConfig = classificationConfig.asBaselineConfig();

					double baselineAccuracy       = getClassificationAccuracyFor(loadData(baselineConfig), baselineConfig.getClassAttribute(), baselineConfig.getClassifier()).pctCorrect();
					double classificationAccuracy = getClassificationAccuracyFor(loadData(classificationConfig), classificationConfig.getClassAttribute(), classificationConfig.getClassifier()).pctCorrect();

				out.printf("%s;%d;%s;%s;%.4f;%.4f;%.4f\n",
						datafile.toString(),
						featureArray.length,
						Arrays.toString(featureArray),
						classAttribute,
						baselineAccuracy,
						classificationAccuracy,
						classificationAccuracy - baselineAccuracy
						);
				out.flush();
			}
		}
		}

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
	
	public static <T> Set<Set<T>> getLimitedPowerset(Set<T> elements, int maxSubsetCardinality) {
		Set<Set<T>> powerSet = new HashSet<>();
		if (elements.size() == 1) {
			powerSet.add(elements);
		} else {			
			for (T element : elements) {
				Set<T> subElements = new HashSet<>(elements);
				subElements.remove(element);
				Set<Set<T>> subPowerSet = getLimitedPowerset(subElements, maxSubsetCardinality);
				powerSet.addAll(subPowerSet);
			}
			if (elements.size() <= maxSubsetCardinality) powerSet.add(elements);
		}
		
		return powerSet;
	}
}
