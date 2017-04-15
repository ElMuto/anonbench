package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.Test;

public class TestDifficulties {

	private double epsilon = 1e-9;
	private ParametrizationSetup testSetupMs;

	@Test
	public void testCensusMsLd() {
		testSetupMs =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS",
				5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT,
				BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.428571429, testSetupMs.getAnonymizer().getDifficulty(), epsilon);
	}

	@Test
	public void testCensusMsLr() {
		testSetupMs =  new ParametrizationSetup( BenchmarkDatafile.ACS13, "MS",
				5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE,
				BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.952380952, testSetupMs.getAnonymizer().getDifficulty(), epsilon);
	}

	@Test
	public void testCensusMsLe() {
		testSetupMs =  new ParametrizationSetup( BenchmarkDatafile.ACS13, "MS",
				5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY,
				BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.976190476, testSetupMs.getAnonymizer().getDifficulty(), epsilon);
	}

	@Test
	public void testCensusMsTc() {
		testSetupMs =  new ParametrizationSetup( BenchmarkDatafile.ACS13, "MS",
				5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED,
				BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.785714286, testSetupMs.getAnonymizer().getDifficulty(), epsilon);
	}

	@Test
	public void testCensusMsDp() {
		testSetupMs =  new ParametrizationSetup( BenchmarkDatafile.ACS13, "MS",
				5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY,
				BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.928571429, testSetupMs.getAnonymizer().getDifficulty(), epsilon);
	}
}
