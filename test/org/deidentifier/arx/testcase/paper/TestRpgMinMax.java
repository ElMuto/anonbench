package org.deidentifier.arx.testcase.paper;

import static org.junit.Assert.*;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.testutil.TestSetup;
import org.junit.Test;

public class TestRpgMinMax {

	private TestSetup testSetupMs;
	private AttributeStatistics statsMs;

	private TestSetup testSetupEd;
	private AttributeStatistics statsEd;
	
	private static final double epsilon = 1e-4;

	@Test
	public void Census() {

    	testSetupMs =  new TestSetup(
    			BenchmarkDatafile.ACS13,
    			"MS");   
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());     
		assertEquals(1, 		statsMs.getRpgLmin(),	epsilon);
		assertEquals(5, 		statsMs.getRpgLmax(),	epsilon);
		assertEquals(1, 		statsMs.getRpgTmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgTmax(),	epsilon);
		assertEquals(72.8185,	statsMs.getRpgBmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgBmax(),	epsilon);
		assertEquals(7.1605,	statsMs.getRpgDmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgDmax(),	epsilon);

    	testSetupEd =  new TestSetup(
    			BenchmarkDatafile.ACS13,
    			"ED");   
    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());     
		assertEquals(1, 		statsEd.getRpgLmin(),	epsilon);
		assertEquals(25, 		statsEd.getRpgLmax(),	epsilon);
		assertEquals(1, 		statsEd.getRpgTmin(),	epsilon);
		assertEquals(0, 		statsEd.getRpgTmax(),	epsilon);
		assertEquals(94.4514,	statsEd.getRpgBmin(),	epsilon);
		assertEquals(0, 		statsEd.getRpgBmax(),	epsilon);
		assertEquals(6.5767,	statsEd.getRpgDmin(),	epsilon);
		assertEquals(0d, 		statsEd.getRpgDmax(),	epsilon);
	}

	@Test
	public void TimeUse() {

    	testSetupMs =  new TestSetup(
    			BenchmarkDatafile.ATUS,
    			"MS");   
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());     
		assertEquals(1, 		statsMs.getRpgLmin(),	epsilon);
		assertEquals(7, 		statsMs.getRpgLmax(),	epsilon);
		assertEquals(1, 		statsMs.getRpgTmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgTmax(),	epsilon);
		assertEquals(99.9837,	statsMs.getRpgBmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgBmax(),	epsilon);
		assertEquals(9.6972,	statsMs.getRpgDmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgDmax(),	epsilon);

    	testSetupEd =  new TestSetup(
    			BenchmarkDatafile.ATUS,
    			"ED");   
    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());     
		assertEquals(1, 		statsEd.getRpgLmin(),	epsilon);
		assertEquals(18, 		statsEd.getRpgLmax(),	epsilon);
		assertEquals(1, 		statsEd.getRpgTmin(),	epsilon);
		assertEquals(0, 		statsEd.getRpgTmax(),	epsilon);
		assertEquals(335.4024,	statsEd.getRpgBmin(),	epsilon);
		assertEquals(0, 		statsEd.getRpgBmax(),	epsilon);
		assertEquals(9.0936,	statsEd.getRpgDmin(),	epsilon);
		assertEquals(0d, 		statsEd.getRpgDmax(),	epsilon);
	}

	@Test
	public void HealthInterview() {

    	testSetupMs =  new TestSetup(
    			BenchmarkDatafile.IHIS,
    			"MS");   
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());     
		assertEquals(1, 		statsMs.getRpgLmin(),	epsilon);
		assertEquals(10, 		statsMs.getRpgLmax(),	epsilon);
		assertEquals(1, 		statsMs.getRpgTmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgTmax(),	epsilon);
		assertEquals(683.34862,	statsMs.getRpgBmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgBmax(),	epsilon);
		assertEquals(10.68043,	statsMs.getRpgDmin(),	epsilon);
		assertEquals(0, 		statsMs.getRpgDmax(),	epsilon);

    	testSetupEd =  new TestSetup(
    			BenchmarkDatafile.IHIS,
    			"ED");   
    	statsEd = AttributeStatistics.get(testSetupEd.getDataset(), testSetupEd.getSa());     
		assertEquals(1, 		statsEd.getRpgLmin(),	epsilon);
		assertEquals(26, 		statsEd.getRpgLmax(),	epsilon);
		assertEquals(1, 		statsEd.getRpgTmin(),	epsilon);
		assertEquals(0, 		statsEd.getRpgTmax(),	epsilon);
		assertEquals(807.0596,	statsEd.getRpgBmin(),	epsilon);
		assertEquals(0, 		statsEd.getRpgBmax(),	epsilon);
		assertEquals(9.6583,	statsEd.getRpgDmin(),	epsilon);
		assertEquals(0d, 		statsEd.getRpgDmax(),	epsilon);
	}
}
