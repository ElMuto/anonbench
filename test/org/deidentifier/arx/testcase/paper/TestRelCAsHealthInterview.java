package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.util.Classificator;
import org.junit.Test;

public class TestRelCAsHealthInterview {

	private double epsilon = 1e-3;
	private ParametrizationSetup testSetup;

	@Test
	public void HealthInterviewMsXLd() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.6944, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewMsLr() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0843, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewMsLe() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0843, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewMsTc() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0751, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewMsDp() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0d, classi.getMaxRelPa(), epsilon);
	}
	
	@Test
	public void HealthInterviewEdXLd() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.4269, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewEdLr() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.4269, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewEdLe() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.4269, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewEdTc() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0063, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void HealthInterviewEdDp() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.IHIS,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0d, classi.getMaxRelPa(), epsilon);
	}
}

