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

	private ComparisonSetup testSetup;
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

		testSetup =  new ComparisonSetup(
				BenchmarkCriterion.L_DIVERSITY_DISTINCT,
				BenchmarkDatafile.ACS13,
				0.05,
				privacyModel.getK(),
				BenchmarkMeasure.ENTROPY, "Education", privacyModel.getDim2Val());

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


		testSetup =  new ComparisonSetup(
				BenchmarkCriterion.L_DIVERSITY_RECURSIVE,
				BenchmarkDatafile.ACS13,
				0.05,
				5,
				BenchmarkMeasure.ENTROPY, "Education", 1d);

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

		testSetup =  new ComparisonSetup(
				BenchmarkCriterion.L_DIVERSITY_ENTROPY,
				BenchmarkDatafile.ACS13,
				0.05,
				1,
				BenchmarkMeasure.ENTROPY, "Education", PrivacyModel.getDefaultParam2(BenchmarkCriterion.L_DIVERSITY_ENTROPY));

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

		testSetup =  new ComparisonSetup(
				BenchmarkCriterion.T_CLOSENESS_ED,
				BenchmarkDatafile.ACS13,
				0.05,
				1,
				BenchmarkMeasure.ENTROPY, "Education", PrivacyModel.getDefaultParam2(BenchmarkCriterion.T_CLOSENESS_ED));

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

		testSetup =  new ComparisonSetup(
				 BenchmarkCriterion.D_DISCLOSURE_PRIVACY,
				BenchmarkDatafile.ACS13,
				0.05,
				1,
				BenchmarkMeasure.ENTROPY, "Education", PrivacyModel.getDefaultParam2(BenchmarkCriterion.D_DISCLOSURE_PRIVACY));

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

		testSetup =  new ComparisonSetup(
				BenchmarkCriterion.BASIC_BETA_LIKENESS,
				BenchmarkDatafile.ACS13,
				0.05,
				1,
				BenchmarkMeasure.ENTROPY, "Education", PrivacyModel.getDefaultParam2(BenchmarkCriterion.BASIC_BETA_LIKENESS));

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
