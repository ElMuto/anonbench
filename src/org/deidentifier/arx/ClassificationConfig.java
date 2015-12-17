package org.deidentifier.arx;

public class ClassificationConfig {
	
	public enum Classifier {
		J48,
		RandomForest,
		NaiveBayes,
		Zero_R
	}
	
	private final Classifier classifier; 
	private final String id;
	private final String fileName;
	private final String classAttribute;
	private final String[] excludedAttributes;
	private final String nominalAttributes;
	private final boolean invertedSelection;
	
	public ClassificationConfig (String id, Classifier classifier, String fileName, String classAttribute, String[] excludedAttributes, String nominalAttributes) {
		this (id, classifier, fileName, classAttribute, excludedAttributes, false, nominalAttributes);
	}
	
	/**
	 * @param id
	 * @param classifier
	 * @param fileName
	 * @param classAttribute
	 * @param excludedAttributes
	 * @param invertSelection
	 * @param nominalAttributes
	 */
	private ClassificationConfig (String id, Classifier classifier, String fileName, String classAttribute, String[] excludedAttributes, boolean invertSelection, String nominalAttributes) {
		this.id = id;
		this.classifier = classifier;
		this.fileName = fileName;
		this.classAttribute = classAttribute;
		this.excludedAttributes = excludedAttributes;
		this.nominalAttributes = nominalAttributes;
		this.invertedSelection = invertSelection;
	}
	
	public Classifier getClassifier() {
		return classifier;
	}

	public String getId() {
		return id;
	}

	public String getInputFileName() {
		return fileName;
	}

	public String getClassAttribute() {
		return classAttribute;
	}

	public String[] getExcludedAttributes() {
		return excludedAttributes;
	}

	public String getNominalAttributes() {
		return nominalAttributes;
	}

	public boolean isInvertedSelection() {
		return invertedSelection;
	}
	
	/** Create a Zero-R classification-configuration
	 * @return
	 */
	public ClassificationConfig asBaselineConfig() {
		return new ClassificationConfig(
				getId(),
				Classifier.Zero_R,
				getInputFileName(),
				getClassAttribute(),
				new String[] { getClassAttribute() },
				true, getNominalAttributes());
	}
	
	/** If we only tell Weka to invert the set of excluded attributes (i.e. the dataset should contain <b>only</b>
	 * these attributes), the dataset would be missing the class attribute. Therefore, we need to make sure to add
	 * this attribute to the dataset additionally to the inversion done by the Weka-API
	 * @return
	 */
	public ClassificationConfig invertExclusionSet() {
		
		String[] inversionArray = new String[getExcludedAttributes().length +1];
		
		for (int i = 0; i < getExcludedAttributes().length; i++) {
			
			inversionArray[i] = getExcludedAttributes()[i];
			
		}
		
		inversionArray[getExcludedAttributes().length] = getClassAttribute();
		
		return new ClassificationConfig(
				getId(),
				getClassifier(),
				getInputFileName(),
				getClassAttribute(),
				inversionArray,
				true, getNominalAttributes());
	}
}