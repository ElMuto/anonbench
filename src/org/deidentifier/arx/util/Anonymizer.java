package org.deidentifier.arx.util;

import java.io.IOException;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.Data;

public class Anonymizer {
	private final ARXAnonymizer arxAnonymizer;
	
	private double difficulty;

	public Anonymizer() {
		super();
		this.arxAnonymizer = new ARXAnonymizer();
	}

	public ARXAnonymizer getArxAnonymizer() {
		return arxAnonymizer;
	}

	public ARXResult anonymize(Data arxData, ARXConfiguration config) {
		ARXResult result = null;
		try {
			result = arxAnonymizer.anonymize(arxData, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.difficulty = Difficulties.calculateDifficulty(result);
		
		return result;
	}

	public double getDifficulty() {
		return difficulty;
	}
}
