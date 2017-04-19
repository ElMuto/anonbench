package org.deidentifier.arx.util;

import java.io.IOException;
import java.util.Map;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataHandle;

import org.deidentifier.arx.utility.DataConverter;
import org.deidentifier.arx.utility.UtilityMeasure;
import org.deidentifier.arx.utility.UtilityMeasureLoss;
import org.deidentifier.arx.utility.UtilityMeasureNonUniformEntropy;
import org.deidentifier.arx.utility.UtilityMeasureSoriaComas;

import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXLattice.Anonymity;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class Anonymizer {
	private final ARXAnonymizer arxAnonymizer;
	
	private double difficulty;
	
    private Double ilArx = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
    private Double ilAbsEntr = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
    private Double ilRelEntr = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
    private Double ilAbsLoss = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
    private Double ilRelLoss = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
    private Double ilSorCom  = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;

	public Anonymizer() {
		super();
		this.arxAnonymizer = new ARXAnonymizer();
	}

	public ARXAnonymizer getArxAnonymizer() {
		return arxAnonymizer;
	}

	public ARXResult anonymize(BenchmarkDataset dataset, ARXConfiguration config) {
		ARXResult resultNom = null;
		ARXResult resultNum = null;
		try {
			resultNom = arxAnonymizer.anonymize(dataset.getArxData(false), config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.difficulty = Difficulties.calculateDifficulty(resultNom);
		calcInfLosses(dataset, resultNom, resultNum, config);
		calcDisclosureRisks(dataset, resultNom, config);
		
		return resultNom;
	}

	private void calcInfLosses(BenchmarkDataset dataset, ARXResult resultNom, ARXResult resultNum, ARXConfiguration config) {
		       if (resultNom.getGlobalOptimum() != null) {
	
		        	ARXNode arxOptimum = resultNom.getGlobalOptimum();
	
		        	this.ilArx = Double.valueOf(arxOptimum.getLowestScore().toString());
		        	DataHandle handle = resultNom.getOutput(arxOptimum, false);
		        	DataConverter converter = new DataConverter();
		        	String[][]  scOutputData = converter.toArray(handle,dataset.getInputDataDef());
		        	handle.release();
		        	
		        	ilRelEntr = performEvaluationWithUtilityPackage(dataset, BenchmarkMeasure.ENTROPY, scOutputData);
		        	ilRelLoss = performEvaluationWithUtilityPackage(dataset, BenchmarkMeasure.LOSS, scOutputData);	
		        	ilSorCom  = getIlSorComByFullTraversal(dataset, config);
		        	
		        } else {
		        	ilSorCom = ilRelEntr = ilArx = ilAbsEntr = ilRelEntr = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
		        }
		       
		       try {
				dataset.getHandle().release();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	private double performEvaluationWithUtilityPackage(BenchmarkDataset dataset, BenchmarkMeasure bMeasure,
			String[][] scOutputData) {
		
	    String[] header = dataset.getQuasiIdentifyingAttributes();
    	DataConverter converter = new DataConverter();
	    Map<String, String[][]> hierarchies = converter.toMap(dataset.getInputDataDef());
		

		UtilityMeasure<Double> uMeasure = null;
		switch (bMeasure) {
		case ENTROPY:
			uMeasure = new UtilityMeasureNonUniformEntropy<Double>(header, dataset.getInputArray());
			break;
		case LOSS:
			uMeasure = new UtilityMeasureLoss<Double>(header, hierarchies, org.deidentifier.arx.utility.AggregateFunction.GEOMETRIC_MEAN);
			break;
		default:
			throw new IllegalArgumentException("Unsupported BenchmarkMeasure: " + bMeasure);	
		}
		
		double ilAbs = uMeasure.evaluate(scOutputData).getUtility();
		double ilRel = (ilAbs - dataset.getMinInfoLoss(bMeasure)) / (dataset.getMaxInfoLoss(bMeasure) - dataset.getMinInfoLoss(bMeasure));

		return ilRel;
		
	}

	private Double getIlSorComByFullTraversal(BenchmarkDataset dataset, ARXConfiguration config) {
		Double ret 					= null;      
		ARXNode scOptimumNode		= null;
		Double  scOptimumVal  		= Double.MAX_VALUE;
		
		BenchmarkDataset datasetNum = new BenchmarkDataset(dataset.getDatafile(), dataset.getCriteria(), dataset.getInSensitiveAttribute());
		ARXResult resultNum = null;
		try {
			resultNum = arxAnonymizer.anonymize(dataset.getArxData(true), config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		ARXLattice lattice = resultNum.getLattice();
	    int minLevel = 0;
	    int maxLevel = lattice.getTop().getTotalGeneralizationLevel();
	    
	    // Expand nodes such that all anonymous nodes are being materialized
	    for (int level = minLevel; level < maxLevel; level++) {
	        for (ARXNode node : lattice.getLevels()[level]) {
	            node.expand();
	        }
	    }
	
	    DataConverter converter = new DataConverter();
	    UtilityMeasureSoriaComas measureSoriaComas = new UtilityMeasureSoriaComas((datasetNum.getInputArray(true)));
	
		for (ARXNode[] level : lattice.getLevels()) {
	
			// For each transformation
			for (ARXNode node : level) {
	
				// Make sure that every transformation is classified correctly
				if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
					
					resultNum.getOutput(node, false).release();
				}				
				
				if (Anonymity.ANONYMOUS == node.getAnonymity()) {
	
					String[][]  scOutputData = converter.toArray(resultNum.getOutput(node, false),datasetNum.getInputDataDef(true));
					//result.getOutput(node).release();
					Double il = measureSoriaComas.evaluate(scOutputData, node.getTransformation()).getUtility();
	
					if (il < scOptimumVal) {
						scOptimumNode = node;
						scOptimumVal = il;
					}
	
				}
	
			}
		}
		if (scOptimumNode != null) {
			ret = scOptimumVal;
		}
		try {
			datasetNum.getHandle().release();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private void calcDisclosureRisks(BenchmarkDataset dataset, ARXResult result, ARXConfiguration config) {
		ARXNode optNode = result.getGlobalOptimum();
		if (optNode != null) {

			Data arxData = dataset.getArxData();
			// calculate SSE
			DataDefinition numDataDef = arxData.getDefinition();
			String[] qiS = optNode.getQuasiIdentifyingAttributes();
			for (int i = 0; i < qiS.length; i++) {
				String qi = qiS[i];
				numDataDef.setMinimumGeneralization(qi, optNode.getTransformation()[i]);
				numDataDef.setMaximumGeneralization(qi, optNode.getTransformation()[i]);
			}

			DisclosureRiskCalculator.prepare(dataset);
			try {
				arxAnonymizer.anonymize(arxData, config);
			} catch (IOException e) {
				e.printStackTrace();
			}
			DisclosureRiskCalculator.summarize();
		} else {
			DisclosureRiskCalculator.prepare(dataset);
			DisclosureRiskCalculator.summarize();
		}
		
		try {
			dataset.getHandle().release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		public Double getIlArx() {
		return ilArx;
	}

	public Double getIlAbsEntr() {
		return ilAbsEntr;
	}

	public Double getIlRelEntr() {
		return ilRelEntr;
	}

	public Double getIlAbsLoss() {
		return ilAbsLoss;
	}

	public Double getIlRelLoss() {
		return ilRelLoss;
	}

	public Double getIlSorCom() {
		return ilSorCom;
	}

	public double getDifficulty() {
		return difficulty;
	}
}
