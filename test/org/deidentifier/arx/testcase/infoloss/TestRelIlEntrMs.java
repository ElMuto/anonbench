package org.deidentifier.arx.testcase.infoloss;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.Test;

public class TestRelIlEntrMs {

	private double epsilon = 1e-9;
	private ParametrizationSetup testSetupMs;

	@Test
	public void testCensusMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.450259163, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testCensusMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.966260182129476, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testCensusMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(1d, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testCensusMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.73574422563275, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testCensusMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.920076835124536, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}
	
	//////// Time Use ///////////////////////

	@Test
	public void testTimeUseMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.14080508536062, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.597893488278104, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.598053097657114, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.614498462901771, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testTimeUseMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(1d, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}
	
	//////// Health Interviews ///////////////////////

	@Test
	public void testHealthInterviewMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.496032738059688, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.736723121958671, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.736723121958671, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.723788772617884, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}

	@Test
	public void testHealthInterviewMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);
		testSetupMs.anonymize();
		assertEquals(1d, testSetupMs.getAnonymizer().getIlRelEntr(), epsilon);
	}
}
