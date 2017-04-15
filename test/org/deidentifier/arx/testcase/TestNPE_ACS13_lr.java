package org.deidentifier.arx.testcase;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.ParametrizationSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Helmut Spengler
 */
public class TestNPE_ACS13_lr {

	
	final BenchmarkCriterion	criterion	= BenchmarkCriterion.L_DIVERSITY_RECURSIVE;
	final BenchmarkDatafile		datafile	= BenchmarkDatafile.ACS13;
	final double				suppFactor	= 0.05;
	final BenchmarkMeasure		measure		= BenchmarkMeasure.ENTROPY;
	final String				sa			= "Marital status";
	final PrivacyModel			privacyModel= new PrivacyModel("lr", 5, 5d);

	private ParametrizationSetup testSetup;
	
    @Before
    public void setUp() {

    	testSetup =  new ParametrizationSetup(datafile, sa, 1, PrivacyModel.getDefaultParam2(criterion), criterion, measure, suppFactor);
        
    }
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void test() {
		
		BenchmarkDataset dataset = new BenchmarkDataset(datafile, new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, criterion }, sa);
		BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, dataset);

		try {
			driver.findOptimalRelPA(0.05, dataset,
					sa,
					false, privacyModel, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assert(true); // if we arrived here, we had no Exception!!!!
	}
}