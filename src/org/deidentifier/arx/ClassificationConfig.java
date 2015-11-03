package org.deidentifier.arx;

public class ClassificationConfig {
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
	public ClassificationConfig (String fileName, String workloadAttribute, String[] excludedAttributes, boolean invertSelection, String nominalAttributes) {
		this.fileName = fileName;
		this.workloadAttribute = workloadAttribute;
		this.excludedAttributes = excludedAttributes;
		this.nominalAttributes = nominalAttributes;
		this.invertedSelection = invertSelection;
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