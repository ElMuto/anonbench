package org.deidentifier.arx;

import org.junit.*;

import java.io.IOException;
import java.util.Set;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.DataType.ARXDate;

public class TestAggregation {
	
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
    
	@Test
	public void test() {
		int[] testTrafo = new int[] { 6, 0, 2 };

		Data arxData = dataset.getArxData();
    	DataDefinition numDataDef = arxData.getDefinition();
    	String[] qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(datafile);
    	for (int i = 0; i < qiS.length; i++) {
    		String qi = qiS[i];
    		numDataDef.setMinimumGeneralization(qi, testTrafo[i]);
    		numDataDef.setMaximumGeneralization(qi, testTrafo[i]);
    	}
        
        try {
			result = anonymizer.anonymize(arxData, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert(result != null);
	}

}
