package org.deidentifier.arx.testcase;

import static org.junit.Assert.assertEquals;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.testutil.TestSetup;
import org.junit.Test;

public class TestAttributeStatistics {

	private TestSetup testSetupMs;
	private AttributeStatistics statsMs;
	private TestSetup testSetupEd;
	private AttributeStatistics statsEd;
	
	private static final double epsilon = 1e-3;

	@Test
	public void Census() {

    	testSetupMs =  new TestSetup(
    			BenchmarkDatafile.ACS13,
    			"MS");   
    	statsMs = AttributeStatistics.analyzeAttribute(testSetupMs.getDataset(), testSetupMs.getHandle(), testSetupMs.getSa(), 0);        

		assertEquals(5, 	statsMs.getDomainSize(),	0d);
    	assertEquals(0.014, statsMs.getMinFrequency(),	epsilon);
		assertEquals(0.446, statsMs.getMaxFrequency(),	epsilon);
		assertEquals(0.703, statsMs.getEntropy(),		epsilon);

    	testSetupEd =  new TestSetup(
    			BenchmarkDatafile.ACS13,
    			"ED");   
    	statsEd = AttributeStatistics.analyzeAttribute(testSetupEd.getDataset(), testSetupEd.getHandle(), testSetupEd.getSa(), 0);        

		assertEquals(25, 	statsEd.getDomainSize(),	0d);
    	assertEquals(0.010, statsEd.getMinFrequency(),	epsilon);
		assertEquals(0.176, statsEd.getMaxFrequency(),	epsilon);
		assertEquals(0.850, statsEd.getEntropy(),		epsilon);

	}

}
