package org.deidentifier.arx;

public class ClassificationConfig {
	private final String fileName;
	private final String workloadAttribute;
	private final String[] excludedAttributes;
	private final String nominalAttributes;
	private final boolean invertedSelection;
	
	public ClassificationConfig (String fileName, String workloadAttribute, String[] excludedAttributes, String nominalAttributes, boolean invertedSelection) {
		this.fileName = fileName;
		this.workloadAttribute = workloadAttribute;
		this.excludedAttributes = excludedAttributes;
		this.nominalAttributes = nominalAttributes;
		this.invertedSelection = invertedSelection;
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
}