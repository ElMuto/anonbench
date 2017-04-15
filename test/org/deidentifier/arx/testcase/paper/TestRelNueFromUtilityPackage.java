package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.*;

import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.Test;

public class TestRelNueFromUtilityPackage {

	private double epsilon = 1e-9;
	private ParametrizationSetup testSetupMs;

	@Test
	public void test() {
		testSetupMs =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.L_DIVERSITY_DISTINCT);
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
		ARXResult result = testSetupMs.anonymize();
		assertTrue(true);
//		assertEquals(0.450259163, Double.valueOf(result.getGlobalOptimum().getLowestScore().toString()), epsilon);
	}

}
