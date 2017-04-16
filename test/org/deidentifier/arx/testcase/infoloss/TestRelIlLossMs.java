package org.deidentifier.arx.testcase.infoloss;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.Test;

public class TestRelIlLossMs {

	private double epsilon = 1e-9;
	private ParametrizationSetup testSetupMs;

//	@Test
//	public void testCensusMsLd() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ACS13,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.110379438793558, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testCensusMsLr() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ACS13,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.938956402613103, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testCensusMsLe() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ACS13,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(1d, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testCensusMsTc() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ACS13,
//				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.296855026134502, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testCensusMsDp() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ACS13,
//				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.664545944308246, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
	
	//////// Time Use ///////////////////////

	@Test
	public void testTimeUseMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0411573544704593, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

//	@Test
//	public void testTimeUseMsLr() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ATUS,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.276825320808697, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testTimeUseMsLe() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ATUS,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.277721031667005, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testTimeUseMsTc() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ATUS,
//				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.453896173227794, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testTimeUseMsDp() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.ATUS,
//				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(1d, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
	
//	//////// Health Interviews ///////////////////////
//
//	@Test
//	public void testHealthInterviewMsLd() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.IHIS,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.0603368694725842, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testHealthInterviewMsLr() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.IHIS,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.25992104989477, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testHealthInterviewMsLe() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.IHIS,
//				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.25992104989477, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testHealthInterviewMsTc() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.IHIS,
//				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(0.28818332090059, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
//
//	@Test
//	public void testHealthInterviewMsDp() {
//		testSetupMs =  new ParametrizationSetup(
//				BenchmarkDatafile.IHIS,
//				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.LOSS, 0.05);
//		testSetupMs.anonymize();
//		assertEquals(1d, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
//	}
}
