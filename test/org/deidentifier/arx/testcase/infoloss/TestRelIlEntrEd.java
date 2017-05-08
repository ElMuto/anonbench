package org.deidentifier.arx.testcase.infoloss;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.Test;

public class TestRelIlEntrEd {

	private double epsilon = 1e-5;
	private ParametrizationSetup testSetupMs;

	@Test
	public void testCensusMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0d, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}
 
	@Test
	public void testCensusMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.25809, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testCensusMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.21958, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testCensusMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.71866, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testCensusMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.82036, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}
	
	//////// Time Use ///////////////////////

	@Test
	public void testTimeUseMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.13898408, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.359915176, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.359909841, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.593588005, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.781130391, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}
	
	//////// Health Interviews ///////////////////////

	@Test
	public void testHealthInterviewMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.383562854, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.383562854, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.383562854260636, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.865146901, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.883476172, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}
}
