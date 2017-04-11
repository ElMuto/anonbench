package org.deidentifier.arx;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator.DisclosureRisk;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Helmut Spengler
 */
public class TestNPE_ACS13_lr {

	
	final BenchmarkCriterion[]	criteria	= new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_RECURSIVE };
	final BenchmarkDatafile		datafile	= BenchmarkDatafile.ACS13;
	final double				suppFactor	= 0.05;
	final BenchmarkMeasure		measure		= BenchmarkMeasure.ENTROPY;
	final String				sa			= "Marital status";
	final PrivacyModel			privacyModel= new PrivacyModel("lr", 5, 5d);

	private TestSetup testSetup;
	
    @Before
    public void setUp() {

    	testSetup =  new TestSetup(criteria, datafile, suppFactor, measure, sa, privacyModel);
        
    }
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void testBigestFoundBeta() {
		
		ARXResult result = testSetup.anonymizeTrafos(new int[] { 0, 0, 0 }, new int[] { 6, 1, 2 });
		result.getOutput(result.getGlobalOptimum(), false);
		
		DisclosureRisk beta = DisclosureRiskCalculator.getBeta();
		
		assert((beta.getMin() == Double.MAX_VALUE && beta.getMax() == Double.MIN_VALUE) || beta.getMin() <= beta.getMax());
	}
}
