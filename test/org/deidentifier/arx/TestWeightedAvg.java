package org.deidentifier.arx;

import org.junit.*;

import static org.junit.Assert.*;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class TestWeightedAvg {
	
	static final BenchmarkCriterion[]	criteria	= new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS_ED };
	static final BenchmarkDatafile		datafile	= BenchmarkDatafile.ACS13;
	static final double					suppFactor	= 0.05;
	static final BenchmarkMeasure		measure		= BenchmarkMeasure.ENTROPY;
	static final String					sa			= "Marital status";
	static final PrivacyModel			privacyModel= new PrivacyModel("t", 5, 0.2);
	
	static Data arxData;
	static DataDefinition numDataDef;
	static String[] qiS;	
	static ARXConfiguration config;
	static ARXAnonymizer anonymizer;
	static BenchmarkDataset dataset;
	
    @Before
    public void setUp() {

    	dataset = new BenchmarkDataset(datafile, criteria, sa);

        try {
			config = BenchmarkDriver.getConfiguration(dataset, suppFactor, measure, sa, privacyModel, dataset.getCriteria());
		} catch (IOException e) {
			e.printStackTrace();
		}
        arxData = dataset.getArxData();
        numDataDef = arxData.getDefinition();
        qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(datafile);
        anonymizer = new ARXAnonymizer();
        
    }
    
    @After
    public void cleanUp() {
    	arxData.getHandle().release();
    }
    
	/**
	 * tests solution space with one transformation containing two ECs. One with 35,714 entries and avg=0.240240797. the other
	 * with 33011 entries and avg=0.380531163. This should result in an weighted average of 0.307627124 (and NOT 0.31038598).
	 */
	@Test
	public void testOneTransformationWithTwosECs() {
		
		int[] testTrafo = new int[] { 6, 0, 2 }; // has two ECs
		
		anonymizeTrafos(testTrafo, testTrafo);

		assertEquals(0.307627124, DisclosureRiskCalculator.getDelta().getAvg(), 1e-9);
		
		
	}

    
	/**
	 * tests solution space with two transformations containing three ECs overall.
	 */
	@Test
	public void testTwoTransformationsWithOverallFourECs() {

		int[] minLevels = new int[] { 6, 0, 2 }; // has two ECs
		int[] maxLevels = new int[] { 6, 1, 2 }; // has one EC

		anonymizeTrafos(minLevels, maxLevels);

		assertEquals(0.153813561752151, DisclosureRiskCalculator.getDelta().getAvg(), 1e-15);
	}

	private void anonymizeTrafos(int[] minLevels, int[] maxLevels) {
		for (int i = 0; i < qiS.length; i++) {
			String qi = qiS[i];
			numDataDef.setMinimumGeneralization(qi, minLevels[i]);
			numDataDef.setMaximumGeneralization(qi, maxLevels[i]);
		}
		try {
			DisclosureRiskCalculator.prepare();
			anonymizer.anonymize(arxData, config);
			DisclosureRiskCalculator.summarize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
