package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.util.Classificator;
import org.junit.Test;

public class TestRelCAsCensus {

	private double epsilon = 1e-4;
	private ParametrizationSetup testSetup;

	@Test
	public void CensusMsLd() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.9114, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusMsLr() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0672, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusMsLe() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0d, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusMsTc() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0548, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusMsDp() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"MS", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0548, classi.getMaxRelPa(), epsilon);
	}
	
	@Test
	public void CensusEdLd() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.849, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusEdLr() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.5584, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusEdLe() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 3d, BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.5557, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusEdTc() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 0.2, BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0933, classi.getMaxRelPa(), epsilon);
	}

	@Test
	public void CensusEdDp() {
		testSetup =  new ParametrizationSetup(
				BenchmarkDatafile.ACS13,
				"ED", 5, 1d, BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);

		Classificator classi = new Classificator(testSetup);
		classi.findOptimalRelCa();
		assertEquals(0.0236, classi.getMaxRelPa(), epsilon);
	}
}

