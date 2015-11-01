package org.deidentifier.arx;

public class ClassificationConfig {
	private final String fileName;
	private final String workloadAttribute;
	private final String[] excludedAttributes;
	
	public ClassificationConfig (String fileName, String workloadAttribute, String[] excludedAttributes) {
		this.fileName = fileName;
		this.workloadAttribute = workloadAttribute;
		this.excludedAttributes = excludedAttributes;
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
}