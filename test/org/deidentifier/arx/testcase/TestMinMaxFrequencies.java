package org.deidentifier.arx.testcase;

import static org.junit.Assert.*;

import java.io.IOException;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.testutil.TestSetup;
import org.junit.Before;
import org.junit.Test;

public class TestMinMaxFrequencies {
	
	private static TestSetup testSetup;
	private AttributeStatistics stats;
	
    @Before
    public void setUp() {

    	testSetup =  new TestSetup(
    			BenchmarkDatafile.ACS13,
    			"MS",
    			5,
    			3d,
    			BenchmarkCriterion.L_DIVERSITY_DISTINCT,
    			BenchmarkMeasure.ENTROPY,
    			0d);
    	
		try {
			stats = AttributeStatistics.analyzeAttribute(testSetup.getDataset(), testSetup.getHandle(), testSetup.getSa(), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}        
    }

	@Test
	public void test() {
//		AttributeStatistics stats = AttributeStatistics.analyzeAttribute(BenchmarkDataset., handle, attr, verbosity)
	}

}
