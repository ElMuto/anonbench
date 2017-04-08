package org.deidentifier.arx;

import static org.junit.Assert.*;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Helmut Spengler
 * make sure that the correct values for min and max privacy guarantees are calculated
 */
public class TestFindOptimalTrafoForMinPrivGuarantee {
	
	private final double epsilon = 0.01;

	private Setup testSetup;
	private BenchmarkDriver driver;
	private PrivacyModel privacyModel;
	
    @Before
    public void setUp() {
	 
	}
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void testLD() {

		privacyModel = new PrivacyModel("ld", 5, 1d);

		testSetup =  new Setup(
				new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_DISTINCT },
				privacyModel,
				BenchmarkDatafile.ACS13,
				0.05,
				BenchmarkMeasure.ENTROPY,
				"Education");

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}
    

	@Test
	public void testLR() {

		privacyModel = new PrivacyModel("lr", 5, 1d);

		testSetup =  new Setup(
				new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
				privacyModel,
				BenchmarkDatafile.ACS13,
				0.05,
				BenchmarkMeasure.ENTROPY,
				"Education");

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}

    

	@Test
	public void testLE() {

		privacyModel = new PrivacyModel("le", 5, 1d);

		testSetup =  new Setup(
				new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_ENTROPY },
				privacyModel,
				BenchmarkDatafile.ACS13,
				0.05,
				BenchmarkMeasure.ENTROPY,
				"Education");

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}


	@Test
	public void testT() {

		privacyModel = new PrivacyModel("t", 5, 1d);

		testSetup =  new Setup(
				new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS_ED },
				privacyModel,
				BenchmarkDatafile.ACS13,
				0.05,
				BenchmarkMeasure.ENTROPY,
				"Education");

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}


	@Test
	public void testD() {

		privacyModel = new PrivacyModel("d", 5, 14d);

		testSetup =  new Setup(
				new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.D_DISCLOSURE_PRIVACY },
				privacyModel,
				BenchmarkDatafile.ACS13,
				0.05,
				BenchmarkMeasure.ENTROPY,
				"Education");

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}


	@Test
	public void testB() {

		privacyModel = new PrivacyModel("b", 5, 95d);

		testSetup =  new Setup(
				new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.BASIC_BETA_LIKENESS },
				privacyModel,
				BenchmarkDatafile.ACS13,
				0.05,
				BenchmarkMeasure.ENTROPY,
				"Education");

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}
}
