package org.deidentifier.arx.util;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.Data;

public class Anonymizer extends ARXAnonymizer {
	
	private double difficulty;

	public Anonymizer() {
		super();
	}

	public ARXResult anonymize(Data arxData, ARXConfiguration config) {
		ARXResult result = anonymize(arxData, config);
		
		this.difficulty = Difficulties.calculateDifficulty(result);
		
		return result;
	}

	public double getDifficulty() {
		return difficulty;
	}
}
