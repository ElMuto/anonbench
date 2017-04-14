package org.deidentifier.arx.testcase;

import static org.junit.Assert.*;

import java.io.IOException;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.testutil.TestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author spengler
 * make sure that transformation [0, 0, 0] returned when a privacy model parameter with its minimal privacy guarantee is chosen
 */
public class TestGetMinMaxPrivacyGuarantee {

	private TestSetup testSetup = null;	
	AttributeStatistics stats = null;
	
    @Before
    public void setUp() {

    	testSetup =  new TestSetup(
    			BenchmarkDatafile.ACS13,
    			"Education",
    			1,
    			PrivacyModel.getDefaultParam2(BenchmarkCriterion.L_DIVERSITY_DISTINCT),
    			BenchmarkCriterion.L_DIVERSITY_DISTINCT, BenchmarkMeasure.ENTROPY, 0.05);
    	
		try {
			stats = AttributeStatistics.analyzeAttribute(testSetup.getDataset(), testSetup.getHandle(), testSetup.getSa(), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}        
    }
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void testL() {

		assertEquals(1d, stats.getMinGuarantee(BenchmarkCriterion.L_DIVERSITY_DISTINCT), 1e-15);
		assertEquals(1d, stats.getMinGuarantee(BenchmarkCriterion.L_DIVERSITY_RECURSIVE), 1e-15);
		assertEquals(1d, stats.getMinGuarantee(BenchmarkCriterion.L_DIVERSITY_ENTROPY), 1e-15);

		assertEquals(25d, stats.getMaxGuarantee(BenchmarkCriterion.L_DIVERSITY_DISTINCT), 1e-15);
		assertEquals(25d, stats.getMaxGuarantee(BenchmarkCriterion.L_DIVERSITY_RECURSIVE), 1e-15);
		assertEquals(25d, stats.getMaxGuarantee(BenchmarkCriterion.L_DIVERSITY_ENTROPY), 1e-15);
	}
    

	@Test
	public void testT() {

		assertEquals(1d, stats.getMinGuarantee(BenchmarkCriterion.T_CLOSENESS_ED), 1e-15);

		assertEquals(0d, stats.getMaxGuarantee(BenchmarkCriterion.T_CLOSENESS_ED), 1e-15);
	}
    

	@Test
	public void testD() {

		assertEquals(14d, Math.ceil(stats.getMinGuarantee(BenchmarkCriterion.D_DISCLOSURE_PRIVACY)), 1e-15);

		assertEquals(0d, stats.getMaxGuarantee(BenchmarkCriterion.D_DISCLOSURE_PRIVACY), 1e-15);
	}
    

	@Test
	public void testB() {

		assertEquals(95d, Math.ceil(stats.getMinGuarantee(BenchmarkCriterion.BASIC_BETA_LIKENESS)), 1e-15);

		assertEquals(0d, stats.getMaxGuarantee(BenchmarkCriterion.BASIC_BETA_LIKENESS), 1e-15);
	}
}
