package org.deidentifier.arx;

public class ClassificationConfig {
	private final String datasetName;
	private final String fileName;
	private final String classAttribute;
	private final String[] excludedAttributes;
	private final String nominalAttributes;
	private final boolean invertedSelection;
	
	/**
	 * @param datasetName
	 * @param fileName
	 * @param classAttribute
	 * @param excludedAttributes
	 * @param invertSelection
	 * @param nominalAttributes
	 */
	public ClassificationConfig (String datasetName, String fileName, String classAttribute, String[] excludedAttributes, boolean invertSelection, String nominalAttributes) {
		this.datasetName = datasetName;
		this.fileName = fileName;
		this.classAttribute = classAttribute;
		this.excludedAttributes = excludedAttributes;
		this.nominalAttributes = nominalAttributes;
		this.invertedSelection = invertSelection;
	}
	
	public String getDatasetName() {
		return datasetName;
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
	
	public ClassificationConfig asBaselineConfig() {
		return new ClassificationConfig(
				getDatasetName(),
				getInputFileName(),
				getClassAttribute(),
				new String[] { getClassAttribute() },
				true, getNominalAttributes());
	}
	
	public ClassificationConfig invertFeatureSet() {
		
		String[] inversionArray = new String[getExcludedAttributes().length +1];
		
		for (int i = 0; i < getExcludedAttributes().length; i++) {
			
			inversionArray[i] = getExcludedAttributes()[i];
			
		}
		
		inversionArray[getExcludedAttributes().length] = getClassAttribute();
		
		return new ClassificationConfig(
				getDatasetName(),
				getInputFileName(),
				getClassAttribute(),
				inversionArray,
				true, getNominalAttributes());
	}
}