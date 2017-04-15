package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.util.Classificator;
import org.junit.Test;

public class TestRelCAs {

	private double epsilon = 1e-9;
	private ParametrizationSetup testSetupMs;

	@Test
	public void CensusMs() {
		

		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetupMs, testSetupMs.getConfig());
		classi.findOptimalRelCa();
		assertEquals(0.0327193771387656, classi.getMaxRelPa(), epsilon);
	}
}

