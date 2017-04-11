package org.deidentifier.arx;

import org.junit.*;

import static org.junit.Assert.*;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class TestWeightedAvg {
	
	final BenchmarkCriterion[]	criteria	= new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS_ED };
	final BenchmarkDatafile		datafile	= BenchmarkDatafile.ACS13;
	final double					suppFactor	= 0.05;
	final BenchmarkMeasure		measure		= BenchmarkMeasure.ENTROPY;
	final String					sa			= "Marital status";
	final PrivacyModel			privacyModel= new PrivacyModel("t", 5, 0.2);
	
	private ComparisonSetup testSetup;
	
    @Before
    public void setUp() {

    	testSetup =  new ComparisonSetup(criteria, privacyModel, datafile, suppFactor, measure, sa);
        
    }
    
    @After
    public void cleanUp() {
    	
    	testSetup.cleanUp();
    	
    }
    
	/**
	 * tests solution space with one transformation containing two ECs. One with 35,714 entries and avg=0.240240797. the other
	 * with 33011 entries and avg=0.380531163. This should result in an weighted average of 0.307627124 (and NOT 0.31038598).
	 */
	@Test
	public void testOneTransformationWithTwosECs() {
		
		int[] testTrafo = new int[] { 6, 0, 2 }; // has two ECs
		
		testSetup.anonymizeTrafos(testTrafo, testTrafo);

		assertEquals(0.307627124, DisclosureRiskCalculator.getDelta().getAvg(), 1e-9);		
	}

    
	/**
	 * tests solution space with two transformations containing three ECs overall.
	 */
	@Test
	public void testTwoTransformationsWithOverallFourECs() {

		int[] minLevels = new int[] { 6, 0, 2 }; // has two ECs
		int[] maxLevels = new int[] { 6, 1, 2 }; // has one EC

		testSetup.anonymizeTrafos(minLevels, maxLevels);

		assertEquals(0.153813561752151, DisclosureRiskCalculator.getDelta().getAvg(), 1e-15);
	}
}
