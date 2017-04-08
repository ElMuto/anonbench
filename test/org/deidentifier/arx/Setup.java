package org.deidentifier.arx;

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class Setup {
	
	private Data arxData;
	private String sa;
	private String[] qiS;	
	private ARXConfiguration config;
	private ARXAnonymizer anonymizer;
	private BenchmarkDataset dataset;
	
	public Setup(BenchmarkCriterion[] criteria, PrivacyModel privacyModel, BenchmarkDatafile datafile,
			double suppFactor, BenchmarkMeasure measure, String sa) {
		super();
		
	   	dataset = new BenchmarkDataset(datafile, criteria, sa);

        try {
			config = BenchmarkDriver.getConfiguration(dataset, suppFactor, measure, sa, privacyModel, dataset.getCriteria());
		} catch (IOException e) {
			e.printStackTrace();
		}
        arxData = dataset.getArxData();
        qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(datafile);
        anonymizer = new ARXAnonymizer();
        this.sa = sa;
	}


	public ARXResult anonymizeTrafos() {
		return anonymizeTrafos(null, null);
	}


	public ARXResult anonymizeTrafos(int[] minLevels, int[] maxLevels) {
        DataDefinition dataDef = arxData.getDefinition();
        
        ARXResult result = null;
        
		for (int i = 0; i < qiS.length; i++) {
			String qi = qiS[i];
			if (minLevels != null) {
				dataDef.setMinimumGeneralization(qi, minLevels[i]);
			}
			if (maxLevels != null) {
				dataDef.setMaximumGeneralization(qi, maxLevels[i]);
			}
		}
		
		try {
			DisclosureRiskCalculator.prepare();
			
			result = anonymizer.anonymize(arxData, config);
			
			DisclosureRiskCalculator.summarize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result; 
	}


	public void cleanUp() {

		arxData.getHandle().release();
		
	}
	
	public DataHandle getHandle() {
		return arxData.getHandle();
	}
	
	public BenchmarkDataset getDataset() {
		return dataset;
	}


	public String getSa() {
		return sa;
	}
}
