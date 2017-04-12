package org.deidentifier.arx;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class ComparisonSetup {
	
	private Data arxData;
	private String sa;
	private String[] qiS;	
	private ARXConfiguration config;
	private ARXAnonymizer anonymizer;
	private BenchmarkDataset dataset;
	private PrivacyModel privacyModel;
	
	public ComparisonSetup(BenchmarkCriterion[] criteria, PrivacyModel privacyModel, BenchmarkDatafile datafile,
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
        this.privacyModel = privacyModel;
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
			DisclosureRiskCalculator.prepare(getDataset().getDatafile(), getSa());
			
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


	/**
	 * @param resultString TODO
	 * @param
	 * @return resultString[0] = IL-NUE, resultString[1] = IL-Loss, resultString[2] = IL-SSE, resultString[3] = Delta-max, resultString[4] = Beta-max
	 */
	double[] convertResults(String[] resultString) {
		
		double[] doubleArray = null;
		
		NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
		try {
			doubleArray = new double[] {
					format.parse(resultString[7]).doubleValue(),
					format.parse(resultString[8]).doubleValue(),
					format.parse(resultString[9]).doubleValue(),
					format.parse(resultString[18]).doubleValue(),
					format.parse(resultString[21]).doubleValue(),
					};
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return doubleArray;
	}


	public PrivacyModel getPrivacyModel() {
		return privacyModel;
	}
}
