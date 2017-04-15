package org.deidentifier.arx;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;
import org.deidentifier.arx.util.Anonymizer;

public class ParametrizationSetup {
	
	private Data arxData;
	private String[] qiS;	
	private ARXConfiguration config;
	private Anonymizer anonymizer;
	private BenchmarkDataset dataset;
	private final BenchmarkCriterion dim2Crit;
	
	public ParametrizationSetup(BenchmarkDatafile datafile, String sa) {
		this(datafile, sa, BenchmarkCriterion.T_CLOSENESS_ED);
	}
	
	/**
	 * @param datafile
	 * @param sa
	 * @param crit
	 */
	public ParametrizationSetup(BenchmarkDatafile datafile, String sa, BenchmarkCriterion crit) {
		this(datafile, sa, 1, 0d, crit, BenchmarkMeasure.ENTROPY, 0d);
	}
	
	/**
	 * @param datafile
	 * @param sa
	 * @param k
	 * @param param2Val
	 * @param criterion
	 * @param measure
	 * @param suppFactor
	 */
	public ParametrizationSetup(BenchmarkDatafile datafile, String sa, Integer k,
			Double param2Val, BenchmarkCriterion criterion, BenchmarkMeasure measure, double suppFactor) {
		super();
		
		this.dim2Crit = criterion;
		
	   	dataset = new BenchmarkDataset(datafile, new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, criterion }, sa);

        try {
			config = BenchmarkDriver.getConfiguration(
					dataset, suppFactor, measure, dataset.getSensitiveAttribute(),
					new PrivacyModel(
							criterion,
							k,
							param2Val),
					dataset.getCriteria());
		} catch (IOException e) {
			e.printStackTrace();
		}
        arxData = dataset.getArxData();
        qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(datafile);
        anonymizer = new Anonymizer();
	}


	public ARXResult anonymize() {
		return anonymize(null, null);
	}


	public ARXResult anonymize(int[] minLevels, int[] maxLevels) {
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

		result = anonymizer.anonymize(dataset, config);


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


	/**
	 * @param resultString TODO
	 * @param
	 * @return resultString[0] = IL-NUE, resultString[1] = IL-Loss, resultString[2] = IL-SSE, resultString[3] = Delta-max, resultString[4] = Beta-max,
	 * resultString[5] = t-max, resultString[6] = t-max-norm, resultString[7] = Beta-max-norm, resultString[8] = lMin, , resultString[9] = lMin-norm,
	 * resultString[10] = dMax-norm
	 */
	public double[] convertResults(String[] resultString) {
		
		double[] doubleArray = null;
		
		NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
		try {
			doubleArray = new double[] {
					format.parse(resultString[7]).doubleValue(),
					format.parse(resultString[8]).doubleValue(),
					format.parse(resultString[9]).doubleValue(),
					"NaN".equals(resultString[24]) ? Double.NaN : format.parse(resultString[24]).doubleValue(),
					"NaN".equals(resultString[30]) ? Double.NaN : format.parse(resultString[30]).doubleValue(),
					"NaN".equals(resultString[18]) ? Double.NaN : format.parse(resultString[18]).doubleValue(),
					"NaN".equals(resultString[21]) ? Double.NaN : format.parse(resultString[21]).doubleValue(),
					"NaN".equals(resultString[33]) ? Double.NaN : format.parse(resultString[33]).doubleValue(),
					"NaN".equals(resultString[10]) ? Double.NaN : format.parse(resultString[10]).doubleValue(),
					"NaN".equals(resultString[13]) ? Double.NaN : format.parse(resultString[13]).doubleValue(),
					"NaN".equals(resultString[27]) ? Double.NaN : format.parse(resultString[27]).doubleValue(),
			};
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return doubleArray;
	}


	public String getSa() {
		return getDataset().getSensitiveAttribute();
	}

	public BenchmarkCriterion getDim2Crit() {
		return dim2Crit;
	}

	public Anonymizer getAnonymizer() {
		return anonymizer;
	}

}
