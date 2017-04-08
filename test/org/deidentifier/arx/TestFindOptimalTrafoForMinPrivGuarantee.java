package org.deidentifier.arx;

import static org.junit.Assert.*;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.After;
import org.junit.Test;

/**
 * @author Helmut Spengler
 * make sure that the correct values for min and max privacy guarantees are calculated
 */
public class TestFindOptimalTrafoForMinPrivGuarantee {

	private Setup testSetup;
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void test() {

    	testSetup =  new Setup(
    			new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_DISTINCT },
    			BenchmarkDatafile.ACS13,
    			0.05,
    			BenchmarkMeasure.ENTROPY,
    			"Education",
    			new PrivacyModel("ld", 5, 18d));
		
		ARXResult result = testSetup.anonymizeTrafos();
		
		assertArrayEquals(new int[] { 0, 0, 0 }, result.getGlobalOptimum().getTransformation());
	}
}
