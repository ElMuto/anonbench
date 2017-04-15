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
    private Double ilSorCom  = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;

	public Anonymizer() {
		super();
		this.arxAnonymizer = new ARXAnonymizer();
	}

	public ARXAnonymizer getArxAnonymizer() {
		return arxAnonymizer;
	}

	public ARXResult anonymize(BenchmarkDataset dataset, ARXConfiguration config) {
		ARXResult result = null;
		try {
			result = arxAnonymizer.anonymize(dataset.getArxData(), config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.difficulty = Difficulties.calculateDifficulty(result);
		calcInfLosses(dataset, result);
		calcDisclosureRisks(dataset, result, config);
		
		return result;
	}

	private void calcInfLosses(BenchmarkDataset dataset, ARXResult result) {
		    String[] header = dataset.getQuasiIdentifyingAttributes();
	    	DataConverter converter = new DataConverter();
		    Map<String, String[][]> hierarchies = converter.toMap(dataset.getInputDataDef());
			UtilityMeasureNonUniformEntropy<Double> measureNue 		= new UtilityMeasureNonUniformEntropy<Double>(header, dataset.getInputArray());
			UtilityMeasureLoss<Double> measureLoss = new UtilityMeasureLoss<Double>(header, hierarchies, org.deidentifier.arx.utility.AggregateFunction.GEOMETRIC_MEAN);
	
			
		       if (result.getGlobalOptimum() != null) {
	
		        	ARXNode arxOptimum = result.getGlobalOptimum();
	
		        	this.ilArx = Double.valueOf(arxOptimum.getLowestScore().toString());
		        	DataHandle handle = result.getOutput(arxOptimum, false);
		        	String[][]  scOutputData = converter.toArray(handle,dataset.getInputDataDef());
		        	handle.release();
		        	this.ilAbsEntr = measureNue.evaluate(scOutputData, arxOptimum.getTransformation()).getUtility();
		        	this.ilRelEntr = (this.ilAbsEntr - dataset.getMinInfoLoss(BenchmarkMeasure.ENTROPY)) / (dataset.getMaxInfoLoss(BenchmarkMeasure.ENTROPY) - dataset.getMinInfoLoss(BenchmarkMeasure.ENTROPY));
	
	//	        	ilSorCom = getOptimumsAbsIlByFullTraversal(BenchmarkMeasure.SORIA_COMAS, dataset, result);
		        } else {
		        	ilSorCom = ilRelEntr = ilArx = ilAbsEntr = ilRelEntr = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
		        }
		       
		       try {
				dataset.getHandle().release();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

		private Double getOptimumsAbsIlByFullTraversal(BenchmarkMeasure bmMeasure, BenchmarkDataset dataset, ARXResult result) {
		Double ret 					= null;      
		ARXNode scOptimumNode		= null;
		Double  scOptimumVal  		= Double.MAX_VALUE;

		ARXLattice lattice = result.getLattice();
        int minLevel = 0;
        int maxLevel = lattice.getTop().getTotalGeneralizationLevel();
        
        // Expand nodes such that all anonymous nodes are being materialized
        for (int level = minLevel; level < maxLevel; level++) {
            for (ARXNode node : lattice.getLevels()[level]) {
                node.expand();
            }
        }

        DataConverter converter = new DataConverter();
        UtilityMeasureSoriaComas measureSoriaComas = new UtilityMeasureSoriaComas((dataset.getInputArray(true)));

		for (ARXNode[] level : lattice.getLevels()) {

			// For each transformation
			for (ARXNode node : level) {
				

				// Make sure that every transformation is classified correctly
				if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
					result.getOutput(node).release();
				}				
				
				if (Anonymity.ANONYMOUS == node.getAnonymity()) {

					String[][]  scOutputData = converter.toArray(result.getOutput(node, false),dataset.getInputDataDef());
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
		return ret;
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

	public Double getIlSorCom() {
		return ilSorCom;
	}

	public double getDifficulty() {
		return difficulty;
	}
}
