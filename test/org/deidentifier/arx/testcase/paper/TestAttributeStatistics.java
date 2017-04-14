package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.assertEquals;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.junit.Test;

public class TestAttributeStatistics {

	private ParametrizationSetup testSetupMs;
	private AttributeStatistics statsMs;
	private ParametrizationSetup testSetupEd;
	private AttributeStatistics statsEd;
	
	private static final double epsilon = 1e-3;

	@Test
	public void Census() {

    	testSetupMs =  new ParametrizationSetup(
    			BenchmarkDatafile.ACS13,
    			"MS");   
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());        

		assertEquals(5, 	    statsMs.getDomainSize(),	0d);
    	assertEquals(0.014,     statsMs.getMinFrequency(),	epsilon);
		assertEquals(0.446,     statsMs.getMaxFrequency(),	epsilon);
		assertEquals(0.703,     statsMs.getEntropy(),		epsilon);
		assertEquals(1, 		statsMs.getRpgLmin(),	    epsilon);
		assertEquals(5, 		statsMs.getRpgLmax(),	    epsilon);
		assertEquals(1, 		statsMs.getRpgTmin(),	    epsilon);
		assertEquals(0, 		statsMs.getRpgTmax(),	    epsilon);
		assertEquals(72.8185,	statsMs.getRpgBmin(),	    epsilon);
		assertEquals(0, 		statsMs.getRpgBmax(),	    epsilon);
		assertEquals(7.1605,	statsMs.getRpgDmin(),	    epsilon);
		assertEquals(0, 		statsMs.getRpgDmax(),	    epsilon);

    	testSetupEd =  new ParametrizationSetup(
    			BenchmarkDatafile.ACS13,
    			"ED");   
    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());        

		assertEquals(25, 	    statsEd.getDomainSize(),	0d);
    	assertEquals(0.010,     statsEd.getMinFrequency(),	epsilon);
		assertEquals(0.176,     statsEd.getMaxFrequency(),	epsilon);
		assertEquals(0.850,     statsEd.getEntropy(),		epsilon);
		assertEquals(1, 		statsEd.getRpgLmin(),       epsilon);
		assertEquals(25, 		statsEd.getRpgLmax(),       epsilon);
		assertEquals(1, 		statsEd.getRpgTmin(),       epsilon);
		assertEquals(0, 		statsEd.getRpgTmax(),       epsilon);
		assertEquals(94.4514,	statsEd.getRpgBmin(),       epsilon);
		assertEquals(0, 		statsEd.getRpgBmax(),       epsilon);
		assertEquals(6.5767,	statsEd.getRpgDmin(),       epsilon);
		assertEquals(0d, 		statsEd.getRpgDmax(),       epsilon);
	}

//	@Test
//	public void TimeUse() {
//
//    	testSetupMs =  new ParametrizationSetup(
//    			BenchmarkDatafile.ATUS,
//    			"MS");   
//    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());        
//
//		assertEquals(7, 	    statsMs.getDomainSize(),	0d);
//    	assertEquals(0.010,     statsMs.getMinFrequency(),	epsilon);
//		assertEquals(0.384,     statsMs.getMaxFrequency(),	epsilon);
//		assertEquals(0.755,     statsMs.getEntropy(),		epsilon);
//		assertEquals(1, 		statsMs.getRpgLmin(),       epsilon);
//		assertEquals(7, 		statsMs.getRpgLmax(),       epsilon);
//		assertEquals(1, 		statsMs.getRpgTmin(),       epsilon);
//		assertEquals(0, 		statsMs.getRpgTmax(),       epsilon);
//		assertEquals(99.9837,	statsMs.getRpgBmin(),       epsilon);
//		assertEquals(0, 		statsMs.getRpgBmax(),       epsilon);
//		assertEquals(9.6972,	statsMs.getRpgDmin(),       epsilon);
//		assertEquals(0, 		statsMs.getRpgDmax(),       epsilon);
//
//    	testSetupEd =  new ParametrizationSetup(
//    			BenchmarkDatafile.ATUS,
//    			"ED");   
//    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());        
//
//		assertEquals(18, 	    statsEd.getDomainSize(),	0d);
//    	assertEquals(0.003,     statsEd.getMinFrequency(),	epsilon);
//		assertEquals(0.276,     statsEd.getMaxFrequency(),	epsilon);
//		assertEquals(0.782,     statsEd.getEntropy(),		epsilon);   
//		assertEquals(1, 		statsEd.getRpgLmin(),    	epsilon);
//		assertEquals(18, 		statsEd.getRpgLmax(),    	epsilon);
//		assertEquals(1, 		statsEd.getRpgTmin(),    	epsilon);
//		assertEquals(0, 		statsEd.getRpgTmax(),    	epsilon);
//		assertEquals(335.4024,	statsEd.getRpgBmin(),    	epsilon);
//		assertEquals(0, 		statsEd.getRpgBmax(),    	epsilon);
//		assertEquals(9.0936,	statsEd.getRpgDmin(),    	epsilon);
//		assertEquals(0d, 		statsEd.getRpgDmax(),    	epsilon);
//	}
//
//
//	@Test
//	public void HealthInterviews() {
//
//    	testSetupMs =  new ParametrizationSetup(
//    			BenchmarkDatafile.IHIS,
//    			"MS");   
//    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());        
//
//		assertEquals(10, 	    statsMs.getDomainSize(),	0d);
//    	assertEquals(0.000,     statsMs.getMinFrequency(),	epsilon);
//		assertEquals(0.236,     statsMs.getMaxFrequency(),	epsilon);
//		assertEquals(0.774,     statsMs.getEntropy(),		epsilon);
//		assertEquals(1, 		statsMs.getRpgLmin(),	    epsilon);
//		assertEquals(10, 		statsMs.getRpgLmax(),	    epsilon);
//		assertEquals(1, 		statsMs.getRpgTmin(),	    epsilon);
//		assertEquals(0, 		statsMs.getRpgTmax(),	    epsilon);
//		assertEquals(683.34862,	statsMs.getRpgBmin(),	    epsilon);
//		assertEquals(0, 		statsMs.getRpgBmax(),	    epsilon);
//		assertEquals(10.68043,	statsMs.getRpgDmin(),	    epsilon);
//		assertEquals(0, 		statsMs.getRpgDmax(),	    epsilon);
//
//    	testSetupEd =  new ParametrizationSetup(
//    			BenchmarkDatafile.IHIS,
//    			"ED");   
//    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());        
//
//		assertEquals(26, 	    statsEd.getDomainSize(),	0d);
//    	assertEquals(0.001,     statsEd.getMinFrequency(),	epsilon);
//		assertEquals(0.192,     statsEd.getMaxFrequency(),	epsilon);
//		assertEquals(0.871,     statsEd.getEntropy(),		epsilon);
//		assertEquals(1, 		statsEd.getRpgLmin(),	    epsilon);
//		assertEquals(26, 		statsEd.getRpgLmax(),	    epsilon);
//		assertEquals(1, 		statsEd.getRpgTmin(),	    epsilon);
//		assertEquals(0, 		statsEd.getRpgTmax(),	    epsilon);
//		assertEquals(807.0596,	statsEd.getRpgBmin(),	    epsilon);
//		assertEquals(0, 		statsEd.getRpgBmax(),	    epsilon);
//		assertEquals(9.6583,	statsEd.getRpgDmin(),	    epsilon);
//		assertEquals(0d, 		statsEd.getRpgDmax(),	    epsilon);
//	}
}
