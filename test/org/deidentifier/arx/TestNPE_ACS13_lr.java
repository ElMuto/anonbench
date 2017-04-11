package org.deidentifier.arx;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Helmut Spengler
 */
public class TestNPE_ACS13_lr {

	
	final BenchmarkCriterion[]	criteria	= new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_RECURSIVE };
	final BenchmarkDatafile		datafile	= BenchmarkDatafile.ACS13;
	final double				suppFactor	= 0.05;
	final BenchmarkMeasure		measure		= BenchmarkMeasure.ENTROPY;
	final String				sa			= "Marital status";
	final PrivacyModel			privacyModel= new PrivacyModel("lr", 5, 5d);

	private Setup testSetup;
	
    @Before
    public void setUp() {

    	testSetup =  new Setup(criteria, privacyModel, datafile, suppFactor, measure, sa);
        
    }
    
    @After
    public void cleanUp() {

    	testSetup.cleanUp();
    	
    }
    

	@Test
	public void test() {
		
		BenchmarkDataset dataset = new BenchmarkDataset(datafile, criteria, sa);
		BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, dataset);

		try {
			driver.findOptimalRelPA(0.05, dataset,
					sa,
					false, privacyModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assert(true); // if we arrived here, we had no Exception!!!!
	}
}
