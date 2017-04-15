package org.deidentifier.arx.testcase.normalization;

import static org.junit.Assert.assertEquals;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.criteria.ParamTransformer;
import org.junit.Test;

/**
 * @author Helmut Spengler
 * make sure that the correct values for min and max privacy guarantees are calculated
 */
public class TestDenormalization {

	private ParametrizationSetup testSetupMs;
	private AttributeStatistics statsMs;
	
	private static final double epsilon = 1e-3;
    
	/**
	 * (value * (rpgMax - rpgMin)) + rpgMin
	 */
	@Test
	public void testL() {
				
    	testSetupMs =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.L_DIVERSITY_DISTINCT);
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());
    	
		double inVal = 0d; double expOutVal = 1d;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 1d; expOutVal = 5d;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 0.6; expOutVal = (0.6 * (statsMs.getRpgLmax() - statsMs.getRpgLmin())) + statsMs.getRpgLmin();		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
	}
    
	/**
	 * 1 - value
	 */
	@Test
	public void testT() {
				
    	testSetupMs =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.T_CLOSENESS_ED);
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());
    	
		double inVal = 1d; double expOutVal = 0d;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 0d; expOutVal = 1d;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 0.2; expOutVal = 1d - inVal;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
	}
    
	/**
	 * (value * (rpgMax - rpgMin)) + rpgMin
	 */
	@Test
	public void testB() {
				
    	testSetupMs =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.BASIC_BETA_LIKENESS);
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());
    	
		double inVal = 1d; double expOutVal = 0d;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 0d; expOutVal = statsMs.getRpgBmin();
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 0.423223; expOutVal = 42d;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
	}
    
	/**
	 * (value * (rpgMax - rpgMin)) / rpgMin;
	 */
	@Test
	public void testD() {
				
    	testSetupMs =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.D_DISCLOSURE_PRIVACY);
    	statsMs = AttributeStatistics.get(testSetupMs.getDataset(), testSetupMs.getSa());
    	
		double inVal = 1d; double expOutVal = 0;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 0d; expOutVal = 7.16054748;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
    	
		inVal = 0.5810376; expOutVal = 3d;		
    	assertEquals(expOutVal, ParamTransformer.getDenormalizedParamVal(testSetupMs.getDataset(), testSetupMs.getSa(), testSetupMs.getDim2Crit(), inVal), epsilon);
	}
}