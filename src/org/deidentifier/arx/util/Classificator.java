package org.deidentifier.arx.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXLogisticRegressionConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.aggregates.StatisticsClassification;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXLattice.Anonymity;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;

public class Classificator {
	
	private Double maxRelPa = null;
	private ParametrizationSetup setup;
	private ARXConfiguration config;
	private BenchmarkDataset dataset;
	
	public Classificator (ParametrizationSetup setup) {
		this.setup = setup;
		this.config = setup.getConfig();
		this.dataset = setup.getDataset();
	}

	public static String[] getBaseCAs(BenchmarkDatafile datafile, String sa, boolean includeInsensitiveAttributes) {
		PrivacyModel pm = new PrivacyModel(BenchmarkCriterion.T_CLOSENESS_ED, 1, 1d);
		BenchmarkDataset dataset = new BenchmarkDataset(datafile, new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, pm.getCriterion() }, sa);
		BenchmarkDriver driver = new BenchmarkDriver(BenchmarkMeasure.ENTROPY, dataset);
		
		String[] result = new String[3];
		try {
			String[] output = driver.findOptimalRelPA(0d, dataset,
					sa,
					includeInsensitiveAttributes, pm, null, null);
			result[0] = output[2];
			result[1] = output[3];
			result[2] = output[4];
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void findOptimalRelCa() {
		ARXAnonymizer anonymizer = new ARXAnonymizer();
		
		ARXResult result = null;
		try {
			result = anonymizer.anonymize(dataset.getArxData(), config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		this.maxRelPa = -Double.MAX_VALUE;
		double absPA = -Double.MAX_VALUE;

		ARXLattice lattice = result.getLattice();
		
        // Expand nodes such that all anonymous nodes are being materialized
        for (int level = 0; level < lattice.getLevels().length; level++) {
            for (ARXNode node : lattice.getLevels()[level]) {
                node.expand();
            }
        }

        DataHandle handle = null;
//		boolean baselineValuesCaptured = false;
		
		int[] exampleTrafo = new int[] { 1, 1, 0 };
		boolean onlyVisitExampleTrafo = false;
		
        for (ARXNode[] level : lattice.getLevels()) {

        	for (ARXNode node : level) {

        		if (!onlyVisitExampleTrafo || Arrays.equals(exampleTrafo, node.getTransformation())) {

        			// Make sure that every transformation is classified correctly
        			if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
        				result.getOutput(node).release();
        			}				
        			if (Anonymity.ANONYMOUS == node.getAnonymity() && this.maxRelPa != 1d) {
        				
        				try {

        					handle = result.getOutput(node);
        					List<String> predictingAttrs = new ArrayList<String>(handle.getDefinition().getQuasiIdentifyingAttributes());
 
        			
        					StatisticsClassification stats = handle.getStatistics().getClassificationPerformance(
        							predictingAttrs.toArray(new String[predictingAttrs.size()]), setup.getSa(), ARXLogisticRegressionConfiguration.create()
        							.setNumFolds(3).setMaxRecords(Integer.MAX_VALUE).setSeed(0xDEADBEEF));

        					double epsilon = 0.05;
        					double absAccuracy = stats.getAccuracy();
        					double relAccuracy = (absAccuracy - stats.getZeroRAccuracy() ) / (stats.getOriginalAccuracy() - stats.getZeroRAccuracy());
        					if (!Double.isNaN(relAccuracy) && !Double.isInfinite(relAccuracy)) {
        						absPA = Math.max(absAccuracy, absPA);
        						this.maxRelPa = Math.max(relAccuracy, this.maxRelPa);
        						if (this.maxRelPa < 0d && this.maxRelPa > 0d - epsilon) this.maxRelPa = 0d;
        						if (this.maxRelPa > 1d && this.maxRelPa <= 1d + epsilon) this.maxRelPa = 1d;
        					}


        					handle.release();

        				} catch (ParseException e) {
        					throw new RuntimeException(e);
        				}
        			}
        		}
        	}
        }
        dataset.getArxData().getHandle().release();
	}

	public static String[] getCombinedRelPaAndDisclosureRiskHeader(BenchmarkDataset dataset) {
		return (String[]) BenchmarkDriver.concat(new String[] { "RelPA", "AbsPA", "MinPA", "MaxPA", "Gain", "Trafo", "NumSuppRecs", "IL-NUE", "IL-Loss", "IL-SSE" }, DisclosureRiskCalculator.getHeader(dataset));
	}

	public Double getMaxRelPa() {
		return maxRelPa;
	}

	public ParametrizationSetup getSetup() {
		return setup;
	}

}
