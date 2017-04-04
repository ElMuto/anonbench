package org.deidentifier.arx;

import org.junit.*;

import static org.junit.Assert.*;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class TestWeightedAvg {
	
	static BenchmarkCriterion[]	criteria		= new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS_ED };
	static BenchmarkDatafile	datafile		= BenchmarkDatafile.ACS13;
	static double				suppFactor		= 0.05;
	static BenchmarkMeasure		measure			= BenchmarkMeasure.ENTROPY;
	static String				sa				= "Marital status";
	static PrivacyModel			privacyModel	= new PrivacyModel("t", 5, 0.2);
	
	static ARXConfiguration config;
	static ARXAnonymizer anonymizer;
	static BenchmarkDataset dataset;
	static ARXResult result;
	
    @BeforeClass
    public static void setUp() {

    	dataset = new BenchmarkDataset(datafile, criteria, sa);

        try {
			config = BenchmarkDriver.getConfiguration(dataset, suppFactor, measure, sa, privacyModel, dataset.getCriteria());
		} catch (IOException e) {
			e.printStackTrace();
		}
        anonymizer = new ARXAnonymizer();
        
    }
    
	/**
	 * tests solution space with one transformation containing two ECs. One with 35,714 entries and avg=0.240240797. the other
	 * with 33011 entries and avg=0.380531163. This should result in an weighted average of 0.307627124 (and NOT 0.31038598).
	 */
	@Test
	public void testOneTransformationWithTwosECs() {
		
		int[] testTrafo = new int[] { 6, 0, 2 };

		Data arxData = dataset.getArxData();
    	DataDefinition numDataDef = arxData.getDefinition();
    	String[] qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(datafile);
    	for (int i = 0; i < qiS.length; i++) {
    		String qi = qiS[i];
    		numDataDef.setMinimumGeneralization(qi, testTrafo[i]);
    		numDataDef.setMaximumGeneralization(qi, testTrafo[i]);
    	}
        
        DisclosureRiskCalculator.prepare();
        try {
			result = anonymizer.anonymize(arxData, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
        DisclosureRiskCalculator.done();
		assertEquals(DisclosureRiskCalculator.getDelta().getAvg(), 0.307627124, 1e-9);
	}

}
