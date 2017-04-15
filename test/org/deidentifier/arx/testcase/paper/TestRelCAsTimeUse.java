package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.util.Classificator;
import org.junit.Test;

public class TestRelCAsTimeUse {

	private double epsilon = 1e-3;
	private ParametrizationSetup testSetup;

	@Test
	public void TimeUseMsXLd() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.9617, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseMsLr() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.3599, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseMsLe() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0442, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseMsTc() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0416, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseMsDp() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0d, classi.getMaxRelPa(), epsilon);
	}
	
	@Test
	public void TimeUseEdLd() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.8606, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseEdLr() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.5354, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseEdLe() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.5354, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseEdTc() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0d, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void TimeUseEdDp() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ATUS,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0d, classi.getMaxRelPa(), epsilon);
	}
}

