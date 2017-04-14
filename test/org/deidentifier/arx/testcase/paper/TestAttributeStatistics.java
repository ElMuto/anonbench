package org.deidentifier.arx.testcase.paper;

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
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());        

		assertEquals(5, 	statsMs.getDomainSize(),	0d);
    	assertEquals(0.014, statsMs.getMinFrequency(),	epsilon);
		assertEquals(0.446, statsMs.getMaxFrequency(),	epsilon);
		assertEquals(0.703, statsMs.getEntropy(),		epsilon);

    	testSetupEd =  new TestSetup(
    			BenchmarkDatafile.ACS13,
    			"ED");   
    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());        

		assertEquals(25, 	statsEd.getDomainSize(),	0d);
    	assertEquals(0.010, statsEd.getMinFrequency(),	epsilon);
		assertEquals(0.176, statsEd.getMaxFrequency(),	epsilon);
		assertEquals(0.850, statsEd.getEntropy(),		epsilon);
	}

	@Test
	public void TimeUse() {

    	testSetupMs =  new TestSetup(
    			BenchmarkDatafile.ATUS,
    			"MS");   
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());        

		assertEquals(7, 	statsMs.getDomainSize(),	0d);
    	assertEquals(0.010, statsMs.getMinFrequency(),	epsilon);
		assertEquals(0.384, statsMs.getMaxFrequency(),	epsilon);
		assertEquals(0.755, statsMs.getEntropy(),		epsilon);

    	testSetupEd =  new TestSetup(
    			BenchmarkDatafile.ATUS,
    			"ED");   
    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());        

		assertEquals(18, 	statsEd.getDomainSize(),	0d);
    	assertEquals(0.003, statsEd.getMinFrequency(),	epsilon);
		assertEquals(0.276, statsEd.getMaxFrequency(),	epsilon);
		assertEquals(0.782, statsEd.getEntropy(),		epsilon);
	}


	@Test
	public void HealthInterviews() {

    	testSetupMs =  new TestSetup(
    			BenchmarkDatafile.IHIS,
    			"MS");   
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());        

		assertEquals(10, 	statsMs.getDomainSize(),	0d);
    	assertEquals(0.000, statsMs.getMinFrequency(),	epsilon);
		assertEquals(0.236, statsMs.getMaxFrequency(),	epsilon);
		assertEquals(0.774, statsMs.getEntropy(),		epsilon);

    	testSetupEd =  new TestSetup(
    			BenchmarkDatafile.IHIS,
    			"ED");   
    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());        

		assertEquals(26, 	statsEd.getDomainSize(),	0d);
    	assertEquals(0.001, statsEd.getMinFrequency(),	epsilon);
		assertEquals(0.192, statsEd.getMaxFrequency(),	epsilon);
		assertEquals(0.871, statsEd.getEntropy(),		epsilon);
	}
}
