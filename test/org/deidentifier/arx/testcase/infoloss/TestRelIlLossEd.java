package org.deidentifier.arx.testcase.infoloss;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.Test;

public class TestRelIlLossEd {

	private double epsilon = 1e-9;
	private ParametrizationSetup testSetupMs;

	@Test
	public void testCensusMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0161480174686405, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testCensusMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0219447953360598, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testCensusMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0327193771387656, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testCensusMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.313470701623555, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testCensusMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.576403509016949, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}
	
	//////// Time Use ///////////////////////

	@Test
	public void testTimeUseMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0415727792716253, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testTimeUseMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0758242987672024, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testTimeUseMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0758374673730668, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testTimeUseMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.293701428270754, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testTimeUseMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.58740105196895, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}
	
	//////// Health Interviews ///////////////////////

	@Test
	public void testHealthInterviewMsLd() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0294472343858977, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testHealthInterviewMsLr() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0294472343858977, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testHealthInterviewMsLe() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.0294472343858977, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testHealthInterviewMsTc() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.58740105196822, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}

	@Test
	public void testHealthInterviewMsDp() {
		testSetupMs =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.LOSS, 0.05);
		testSetupMs.anonymize();
		assertEquals(0.58740105196822, testSetupMs.getAnonymizer().getIlRelLoss(), epsilon);
	}
}
