package org.deidentifier.arx.testcase.normalization;

import static org.junit.Assert.*;

import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.junit.Test;

/**
 * @author Helmut Spengler
 * make sure that the correct values for min and max privacy guarantees are calculated
 */
public class TestParamArray {

	private ParametrizationSetup testSetup;
	
	@Test
	public void testL2() {				
    	testSetup =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.L_DIVERSITY_DISTINCT);
    	Double[] ergArray = BenchmarkSetup.createParamArray(testSetup.getDim2Crit(), testSetup.getSa(),
    			testSetup.getDataset().getDatafile(), 2);
    	assertArrayEquals(new Double[] { 1d, 5d }, ergArray);
	}
	
	@Test
	public void testL3() {				
    	testSetup =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.L_DIVERSITY_DISTINCT);
    	Double[] ergArray = BenchmarkSetup.createParamArray(testSetup.getDim2Crit(), testSetup.getSa(),
    			testSetup.getDataset().getDatafile(), 3);
    	assertArrayEquals(new Double[] { 1d, 3d, 5d }, ergArray);
	}
	
	@Test
	public void testB2() {				
    	testSetup =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.BASIC_BETA_LIKENESS);
    	Double[] ergArray = BenchmarkSetup.createParamArray(testSetup.getDim2Crit(), testSetup.getSa(),
    			testSetup.getDataset().getDatafile(), 2);
    	assertArrayEquals(new Double[] { 72.81847475832438, 0d }, ergArray);
	}
	
	@Test
	public void testB3() {				
    	testSetup =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.BASIC_BETA_LIKENESS);
    	Double[] ergArray = BenchmarkSetup.createParamArray(testSetup.getDim2Crit(), testSetup.getSa(),
    			testSetup.getDataset().getDatafile(), 3);
    	assertArrayEquals(new Double[] { 72.81847475832438, 36.40923737916219, 0d }, ergArray);
	}
	
	@Test
	public void testT2() {				
    	testSetup =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.T_CLOSENESS_ED);
    	Double[] ergArray = BenchmarkSetup.createParamArray(testSetup.getDim2Crit(), testSetup.getSa(),
    			testSetup.getDataset().getDatafile(), 2);
    	assertArrayEquals(new Double[] { 1d, 0d }, ergArray);
	}
	
	@Test
	public void testT11() {				
    	testSetup =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.T_CLOSENESS_ED);
    	Double[] ergArray = BenchmarkSetup.createParamArray(testSetup.getDim2Crit(), testSetup.getSa(),
    			testSetup.getDataset().getDatafile(), 11);
    	assertArrayEquals(new Double[] { 1d, 0.9, 0.8, 0.7, 0.6, 0.5,
    			0.3999999999999999, 0.29999999999999993, 0.19999999999999996, 0.09999999999999998, 0d }, ergArray);
	}
	
	@Test
	public void testD2() {				
    	testSetup =  new ParametrizationSetup(BenchmarkDatafile.ACS13, "MS", BenchmarkCriterion.D_DISCLOSURE_PRIVACY);
    	Double[] ergArray = BenchmarkSetup.createParamArray(testSetup.getDim2Crit(), testSetup.getSa(),
    			testSetup.getDataset().getDatafile(), 2);
    	assertArrayEquals(new Double[] { 7.160547482323879, 0d }, ergArray);
	}
}
