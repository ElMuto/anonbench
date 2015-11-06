package org.deidentifier.arx;

public class ClassificationConfig {
	private final String datasetName;
	private final String fileName;
	private final String workloadAttribute;
	private final String[] excludedAttributes;
	private final String nominalAttributes;
	private final boolean invertedSelection;
	
	/**
	 * @param fileName
	 * @param workloadAttribute
	 * @param excludedAttributes
	 * @param invertSelection
	 * @param nominalAttributes comma-separated string with attribute indices <B>starting at 1</BR>
	 */
	public ClassificationConfig (String datasetName, String fileName, String workloadAttribute, String[] excludedAttributes, boolean invertSelection, String nominalAttributes) {
		this.datasetName = datasetName;
		this.fileName = fileName;
		this.workloadAttribute = workloadAttribute;
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

	public String getWorkloadAttribute() {
		return workloadAttribute;
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
				getWorkloadAttribute(),
				new String[] { getWorkloadAttribute() },
				true, getNominalAttributes());
	}
	
	public ClassificationConfig invertQIs() {
		
		String[] inversionArray = new String[getExcludedAttributes().length +1];
		
		for (int i = 0; i < getExcludedAttributes().length; i++) {
			
			inversionArray[i] = getExcludedAttributes()[i];
			
		}
		
		inversionArray[getExcludedAttributes().length] = getWorkloadAttribute();
		
		return new ClassificationConfig(
				getDatasetName(),
				getInputFileName(),
				getWorkloadAttribute(),
				inversionArray,
				true, getNominalAttributes());
	}
}