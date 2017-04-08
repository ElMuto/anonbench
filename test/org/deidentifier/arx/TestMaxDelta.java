package org.deidentifier.arx;

import static org.junit.Assert.*;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator.DisclosureRisk;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author spengler
 * Checks, why Beta-min == 1, Beta-avg == NaN, and Beta-max == 0
 */
public class TestMaxDelta {

	
	final BenchmarkCriterion[]	criteria	= new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_DISTINCT };
	final BenchmarkDatafile		datafile	= BenchmarkDatafile.ACS13;
	final double				suppFactor	= 0.05;
	final BenchmarkMeasure		measure		= BenchmarkMeasure.ENTROPY;
	final String				sa			= "Education";
	final PrivacyModel			privacyModel= new PrivacyModel("ld", 5, 18d);

	private Setup testSetup;
	
    @Before
    public void setUp() {

    	testSetup =  new Setup(criteria, datafile, suppFactor, measure, sa, privacyModel);
        
    }
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void testMinMax() {
		
		ARXResult result = testSetup.anonymizeTrafos(new int[] { 0, 0, 0 }, new int[] { 6, 1, 2 });
		result.getOutput(result.getGlobalOptimum(), false);
		
		DisclosureRisk delta = DisclosureRiskCalculator.getDelta();
		
		assertTrue("delta-max = " + delta.getMax(), delta.getMax() <= 9.4);
	}
}
