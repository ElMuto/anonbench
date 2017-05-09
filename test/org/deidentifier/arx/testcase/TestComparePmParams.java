package org.deidentifier.arx.testcase;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.util.Classificator;
import org.junit.Test;

public class TestComparePmParams {

	private double epsilon = 1e-3;

	@Test
	public void test_MS_DL() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.L_DIVERSITY_DISTINCT;
		String sa = "Marital status";
		
		double[][] refErgs = new double[][] {
			{1e-5, 1},
			{1d, 1},
			{2d, 0.911},
			{3d, 0.911},
			{4d, 0.911},
			{5d, 0.911}
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}

	@Test
	public void test_MS_RL() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.L_DIVERSITY_RECURSIVE;
		String sa = "Marital status";
		
		double[][] refErgs = new double[][] {
//			{0,001, 1},
			{1, 1},
			{2, 0.114},
			{3, 0.067},
			{4, 0},
			{5, 0},
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}

	@Test
	public void test_MS_EL() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.L_DIVERSITY_ENTROPY;
		String sa = "Marital status";
		
		double[][] refErgs = new double[][] {
			{1, 1},
			{2, 0.114},
			{3, 0},
			{4, 0},
			{5, 0},
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	@Test
	public void test_MS_TC() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.T_CLOSENESS_ED;
		String sa = "Marital status";
		
		double[][] refErgs = new double[][] {
			{1, 1},
			{0.8, 1},
			{0.6, 1},
			{0.4, 0.869},
			{0.2, 0.055},
			{0, 0}
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	@Test
	public void test_MS_DP() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.D_DISCLOSURE_PRIVACY;
		String sa = "Marital status";
		
		double[][] refErgs = new double[][] {
			{6, 1},
			{5, 0.114},
			{4, 0.114},
			{3, 0.055},
			{2, 0.055},
			{1, 0.055},
			{0.001, 0}
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	@Test
	public void test_ED_DL() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.L_DIVERSITY_DISTINCT;
		String sa = "Education";
		
		double[][] refErgs = new double[][] {
			{3, 0.851},
			{5, 0.758},
			{8, 0.391},
			{10, 0.384},
			{13, 0.322},
			{15, 0.322},
			{18, 0.324},
			{20, 0.322},
			{23, 0.079},
			{25, 0.044}
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	@Test
	public void test_ED_RL() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.L_DIVERSITY_RECURSIVE;
		String sa = "Education";
		
		double[][] refErgs = new double[][] {
			{3, 0.557},
			{5, 0.391},
			{8, 0.323},
			{10, 0.119},
			{13, 0.114},
			{15, 0.093},
			{18, 0.044},
			{20, 0.044},
			{23, 0},
			{25, 0}
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	@Test
	public void test_ED_EL() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.L_DIVERSITY_ENTROPY;
		String sa = "Education";
		
		double[][] refErgs = new double[][] {
			{3, 0.555},
			{5, 0.391},
			{8, 0.286},
			{10, 0.114},
			{13, 0.093},
			{15, 0.014},
			{18, 0},
			{20, 0},
			{23, 0},
			{25, 0},
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	@Test
	public void test_ED_TC() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.T_CLOSENESS_ED;
		String sa = "Education";
		
		double[][] refErgs = new double[][] {
			{1, 1},
			{0.8, 0.322},
			{0.6, 0.12},
			{0.4, 0.119},
			{0.2, 0.093},
			{0.001, 0},
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	@Test
	public void test_ED_DP() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		BenchmarkCriterion crit = BenchmarkCriterion.D_DISCLOSURE_PRIVACY;
		String sa = "Education";
		
		double[][] refErgs = new double[][] {
			{6, 1},
			{5, 0.114},
			{4, 0.114},
			{3, 0.115},
			{2, 0.024},
			{1, 0.024},
			{0.001, 0},
		};		
		loopIt(measure, datafile, refErgs, crit, sa);
	}
	
	
	

	private void loopIt(BenchmarkMeasure measure, BenchmarkDatafile datafile, double[][] refErgs, BenchmarkCriterion crit, String sa) {
		for (int i = 0; i < refErgs.length; i++) {

			ParametrizationSetup pSetup =  new ParametrizationSetup(
					datafile,
					sa, 5, refErgs[i][0], crit, measure, 0.05);
			Classificator classi = new Classificator(pSetup);
			classi.findOptimalRelCa();
			double orc = classi.getMaxRelPa() != -Double.MAX_VALUE ? classi.getMaxRelPa() : 0d;
			assertEquals(refErgs[i][1], orc, epsilon);
		}
	}
}
