package org.deidentifier.arx.testcase;

import static org.junit.Assert.*;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.testutil.TestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Helmut Spengler
 * make sure that the correct values for min and max privacy guarantees are calculated
 */
public class TestFindOptimalTrafoForMinPrivGuarantee {
	
	private final double epsilon = 0.01;

	private TestSetup testSetup;
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

		testSetup =  new TestSetup(
				BenchmarkDatafile.ACS13,
				"Education",
				privacyModel.getK(),
				privacyModel.getDim2Val(),
				BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);
		
		assertEquals("[0, 0, 0]", result[5]);
		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}
    

	@Test
	public void testLR() {


		privacyModel = new PrivacyModel("lr", 5, 1d);

		testSetup =  new TestSetup(
				BenchmarkDatafile.ACS13,
				"Education",
				5,
				1d,
				BenchmarkCriterion.L_DIVERSITY_RECURSIVE, BenchmarkMeasure.ENTROPY, 0.05);

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);
		
		assertEquals("[0, 0, 0]", result[5]);
		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}

    

	@Test
	public void testLE() {

		privacyModel = new PrivacyModel("le", 5, 1d);

		testSetup =  new TestSetup(
				BenchmarkDatafile.ACS13,
				"Education",
				1,
				PrivacyModel.getDefaultParam2(BenchmarkCriterion.L_DIVERSITY_ENTROPY),
				BenchmarkCriterion.L_DIVERSITY_ENTROPY, BenchmarkMeasure.ENTROPY, 0.05);

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals("[0, 0, 0]", result[5]);
		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}


	@Test
	public void testT() {

		privacyModel = new PrivacyModel("t", 5, 1d);

		testSetup =  new TestSetup(
				BenchmarkDatafile.ACS13,
				"Education",
				1,
				PrivacyModel.getDefaultParam2(BenchmarkCriterion.T_CLOSENESS_ED),
				BenchmarkCriterion.T_CLOSENESS_ED, BenchmarkMeasure.ENTROPY, 0.05);

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals("[0, 0, 0]", result[5]);
		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}


	@Test
	public void testD() {

		privacyModel = new PrivacyModel("d", 5, 14d);

		testSetup =  new TestSetup(
				 BenchmarkDatafile.ACS13,
				"Education",
				1,
				PrivacyModel.getDefaultParam2(BenchmarkCriterion.D_DISCLOSURE_PRIVACY),
				BenchmarkCriterion.D_DISCLOSURE_PRIVACY, BenchmarkMeasure.ENTROPY, 0.05);

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals("[0, 0, 0]", result[5]);
		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(0d, doubleResults[2], epsilon);
	}


	@Test
	public void testB() {

		privacyModel = new PrivacyModel("b", 5, 95d);

		testSetup =  new TestSetup(
				BenchmarkDatafile.ACS13,
				"Education",
				1,
				PrivacyModel.getDefaultParam2(BenchmarkCriterion.BASIC_BETA_LIKENESS),
				BenchmarkCriterion.BASIC_BETA_LIKENESS, BenchmarkMeasure.ENTROPY, 0.05);

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());

		String[] result = null;

		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[] doubleResults = testSetup.convertResults(result);

		assertEquals("[0, 0, 0]", result[5]);
		assertEquals(0d, doubleResults[0], epsilon);
		assertEquals(0d, doubleResults[1], epsilon);
		assertEquals(6.362, doubleResults[3], epsilon);
		assertEquals(75.361, doubleResults[4], epsilon);
	}
}
