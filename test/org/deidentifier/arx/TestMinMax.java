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
public class TestMinMax {

	
	final BenchmarkCriterion	criterion	= BenchmarkCriterion.L_DIVERSITY_DISTINCT;
	final BenchmarkDatafile		datafile	= BenchmarkDatafile.ACS13;
	final double				suppFactor	= 0.05;
	final BenchmarkMeasure		measure		= BenchmarkMeasure.ENTROPY;
	final String				sa			= "Education";
	final PrivacyModel			privacyModel= new PrivacyModel("ld", 5, 18d);

	private ComparisonSetup testSetup;
	
    @Before
    public void setUp() {

    	testSetup =  new ComparisonSetup(criterion, datafile, suppFactor, 1, measure, sa, PrivacyModel.getDefaultParam2(criterion));
        
    }
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void testDelta() {
		
		ARXResult result = testSetup.anonymizeTrafos(new int[] { 0, 0, 0 }, new int[] { 6, 1, 2 });
		result.getOutput(result.getGlobalOptimum(), false);
		
		DisclosureRisk delta = DisclosureRiskCalculator.getDelta();
		
		assertTrue("delta-max = " + delta.getMax(), delta.getMax() <= 9.99708812);
	}
}
