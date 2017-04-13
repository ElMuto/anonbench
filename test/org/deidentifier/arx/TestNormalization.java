package org.deidentifier.arx;

import static org.junit.Assert.*;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Helmut Spengler
 * make sure that the correct values for min and max privacy guarantees are calculated
 */
public class TestNormalization {
	
	private final double epsilon = 0.01;

	private static ComparisonSetup testSetup;
	private static BenchmarkDriver driver;
	private static PrivacyModel privacyModel;
	static String[] result = null;
	static double[] doubleResults;
	
	@BeforeClass
	public static void setUp() {

		privacyModel = new PrivacyModel("t", 5, 0.2);

		testSetup =  new ComparisonSetup(
				new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS_ED },
				BenchmarkDatafile.ACS13,
				0.05,
				BenchmarkMeasure.ENTROPY,
				"Education");

		driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, testSetup.getDataset());


		try {
			result = driver.findOptimalRelPA(0.05, testSetup.getDataset(), testSetup.getSa(), false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		doubleResults = testSetup.convertResults(result);
		
	}
    
    @AfterClass
    public static void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    
	@Test
	public void testT() {
		assertEquals(0.199, doubleResults[5], epsilon);
		assertEquals(doubleResults[5], 1d - doubleResults[6], epsilon);
	}
    
	@Test
	public void testL() {
		assertEquals(22d, doubleResults[8], epsilon);
		assertEquals(0.875, doubleResults[9], epsilon);
	}
    
	@Test
	public void testD() {
		assertEquals(3.542, doubleResults[3], epsilon);
		assertEquals(0.443, doubleResults[10], epsilon);
	}
    
	@Test
	public void testB() {
		assertEquals(1.345, doubleResults[4], epsilon);
		assertEquals(0.982, doubleResults[7], epsilon);
	}
}
