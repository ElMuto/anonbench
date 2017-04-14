/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal 
 *      methods for the de-identification of biomedical data"
 *      
 * Copyright (C) 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.deidentifier.arx;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXLattice.Anonymity;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.aggregates.StatisticsClassification;
import org.deidentifier.arx.criteria.DisclosureRiskCalculator;
import org.deidentifier.arx.criteria.BasicBLikeness;
import org.deidentifier.arx.criteria.DDisclosurePrivacy;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.EqualDistanceTCloseness;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;
import org.deidentifier.arx.utility.DataConverter;
import org.deidentifier.arx.utility.UtilityMeasure;
import org.deidentifier.arx.utility.UtilityMeasureAECS;
import org.deidentifier.arx.utility.UtilityMeasureDiscernibility;
import org.deidentifier.arx.utility.UtilityMeasureLoss;
import org.deidentifier.arx.utility.UtilityMeasureNonUniformEntropy;
import org.deidentifier.arx.utility.UtilityMeasurePrecision;
import org.deidentifier.arx.utility.UtilityMeasureSoriaComas;


/**
 * This class implements the main benchmark driver
 * @author Fabian Prasser
 */
/**
 * @author spengler
 *
 */
/**
 * @author spengler
 *
 */
public class BenchmarkDriver {

    private final UtilityMeasure<Double> measure;
    private final UtilityMeasureSoriaComas measureSoriaComas;
    private final UtilityMeasureNonUniformEntropy<Double> measureNue;
    private final UtilityMeasureLoss<Double> measureLoss;
    private final DataConverter converter;
    private final BenchmarkSetup.BenchmarkMeasure benchmarkMeasure;
    private final String[] header;
    private final Map<String, String[][]> hierarchies;
    

    private final NumberFormat formatter = NumberFormat.getInstance(Locale.GERMAN);

    public BenchmarkDriver(BenchmarkSetup.BenchmarkMeasure benchmarkMeasure, BenchmarkDataset dataset) {
    	this.benchmarkMeasure = benchmarkMeasure;
    	this.converter = new DataConverter();;
        String[][] inputArray = dataset.getInputArray();
		header = dataset.getQuasiIdentifyingAttributes();
		hierarchies =this.converter.toMap(dataset.getInputDataDef());

	    formatter.setMinimumFractionDigits(5);
	    formatter.setMaximumFractionDigits(5);

        switch (benchmarkMeasure) {
		case AECS:
		this.	measure = new UtilityMeasureAECS();
			break;
		case DISCERNABILITY:			this.	measure = new UtilityMeasureDiscernibility();
			break;
		case ENTROPY:			this.	measure = new UtilityMeasureNonUniformEntropy<Double>(header, inputArray);
			break;
		case LOSS:			this.	measure = new UtilityMeasureLoss<Double>(header, hierarchies, org.deidentifier.arx.utility.AggregateFunction.GEOMETRIC_MEAN);
			break;
		case PRECISION:
			this.measure = 	new UtilityMeasurePrecision<Double>(header, hierarchies);
			break;
		case SORIA_COMAS:
			this.measure = 	new UtilityMeasureSoriaComas(inputArray);
			break;
		default:
			throw new RuntimeException("Invalid measure");
        }

        this.measureSoriaComas	= new UtilityMeasureSoriaComas(dataset.getInputArray(true));
        this.measureNue 		= new UtilityMeasureNonUniformEntropy<Double>(header, inputArray);
        this.measureLoss 		= new UtilityMeasureLoss<Double>(header, hierarchies, org.deidentifier.arx.utility.AggregateFunction.GEOMETRIC_MEAN);
	}

	/**
     * Returns a configuration for the ARX framework
     * @param dataset
	 * @param suppFactor
	 * @param metric
	 * @param sa
	 * @param privacyModel TODO
	 * @param criteria
	 * @param customQiCount TODO
	 * @return
     * @throws IOException
     */
    public static ARXConfiguration getConfiguration(BenchmarkDataset dataset, Double suppFactor,  BenchmarkMeasure metric,
                                                     String sa, PrivacyModel privacyModel, BenchmarkCriterion... criteria) throws IOException {

        ARXConfiguration config = ARXConfiguration.create();

        switch (metric) {
        case ENTROPY:
        case SORIA_COMAS:
            config.setQualityModel(Metric.createEntropyMetric());
            break;
        case LOSS:
            config.setQualityModel(Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN));
            break;
        case AECS:
            config.setQualityModel(Metric.createAECSMetric());
            break;
        case DISCERNABILITY:
            config.setQualityModel(Metric.createDiscernabilityMetric());
            break;
        case PRECISION:
            config.setQualityModel(Metric.createPrecisionMetric());
            break;
        case HEIGHT:
            config.setQualityModel(Metric.createHeightMetric());
            break;
        default:
            throw new RuntimeException("Invalid benchmark metric");
        }

        config.setMaxOutliers(suppFactor);

        for (BenchmarkCriterion crit : criteria) {
            switch (crit) {
            case K_ANONYMITY:
            	if (privacyModel.getK() > 1) config.addPrivacyModel(new KAnonymity(privacyModel.getK()));
                break;
            case L_DIVERSITY_DISTINCT:
                config.addPrivacyModel(new DistinctLDiversity(sa, privacyModel.getL()));
                break;
            case L_DIVERSITY_ENTROPY:
                config.addPrivacyModel(new org.deidentifier.arx.criteria.EntropyLDiversity(sa, privacyModel.getL()));
                break;
            case L_DIVERSITY_RECURSIVE:
                config.addPrivacyModel(new RecursiveCLDiversity(sa, privacyModel.getC(), privacyModel.getL()));
                break;
            case T_CLOSENESS_HD:
                config.addPrivacyModel(new HierarchicalDistanceTCloseness(sa, privacyModel.getT(), dataset.loadHierarchy(sa)));
                break;
            case T_CLOSENESS_ED:
                config.addPrivacyModel(new EqualDistanceTCloseness(sa, privacyModel.getT()));
                break;
            case D_DISCLOSURE_PRIVACY:
                config.addPrivacyModel(new DDisclosurePrivacy(sa, privacyModel.getD()));
                break;
            case BASIC_BETA_LIKENESS:
                config.addPrivacyModel(new BasicBLikeness(sa, privacyModel.getB()));
                break;
            default:
                throw new RuntimeException("Invalid criterion");
            }
        }
        return config;
    }
    


    /**
     * @param measure
     * @param suppFactor
     * @param dataset
     * @param subsetBased
     * @param k
     * @param l
     * @param c
     * @param t
     * @param d
     * @param b
     * @param dMin
     * @param dMax
     * @param sa
     * @param ssNum
     * @param resultsFileName
     * @param calcBeta TODO
     * @param privacyModel TODO
     * @throws IOException
     */
    public void anonymize(BenchmarkMeasure measure,
                         double suppFactor, BenchmarkDataset dataset,
                         boolean subsetBased, Integer k,
                         Integer l, Double c, Double t,
                         Double d, Double b, Double dMin,
                         Double dMax, String sa, Integer ssNum, String resultsFileName, boolean calcBeta, PrivacyModel privacyModel) throws IOException
    {
    							anonymize (		measure, suppFactor,  dataset,
					                subsetBased,  k,
					                l,  c,  t,
					                d,  b,  dMin,
					                dMax,  sa,
					                ssNum, new Double[] { null }, resultsFileName, privacyModel);
    }


    /**
     * @param measure
     * @param suppFactor
     * @param dataset
     * @param subsetBased
     * @param k
     * @param l
     * @param c
     * @param t
     * @param d
     * @param b
     * @param dMin
     * @param dMax
     * @param sa
     * @param ssNum
     * @param accuracies
     * @param resultsFileName
     * @param privacyModel TODO
     * @throws IOException
     */
    public void anonymize(
                                 BenchmarkMeasure measure,
                                 double suppFactor, BenchmarkDataset dataset,
                                 boolean subsetBased, Integer k,
                                 Integer l, Double c, Double t,
                                 Double d, Double b, Double dMin,
                                 Double dMax, String sa, Integer ssNum, Double[] accuracies, String resultsFileName, PrivacyModel privacyModel
            ) throws IOException {

        ARXConfiguration config = BenchmarkDriver.getConfiguration(dataset, suppFactor, measure, sa, privacyModel, dataset.getCriteria());
        ARXAnonymizer anonymizer = new ARXAnonymizer();
//        anonymizer.setMaxTransformations(210000);
        
        String ss_string = "";
        String qs_string = "";
        String expType   = "";
        if (accuracies[0] != null && accuracies[1] != null) {
        	ss_string = accuracies[0].toString();
        	qs_string = accuracies[1].toString();
        	expType = accuracies[1] - accuracies[0] > 5d ? "A" : "B";
        }

        // Benchmark
        
        BenchmarkCriterion bc = dataset.getCriteria()[dataset.getCriteria().length - 1];
        String bcName = assemblePrivacyModelString(bc, k, c, l, t, suppFactor, d, b);
        BenchmarkSetup.BENCHMARK.addRun(bcName,
        								measure.toString(),
                                        String.valueOf(suppFactor),
                                        dataset.toString(),
                                        Arrays.toString(dataset.getCriteria()),
                                        Boolean.toString(subsetBased),
                                        k != null ? k.toString() : "", l != null ? l.toString() : "", c != null ? c.toString() : "",
                                        t != null ? t.toString() : "", dMin != null ? dMin.toString() : "", dMax != null ? dMax.toString() : "",
                                        sa != null ? sa.toString() : "",
                                        Arrays.toString(dataset.getQuasiIdentifyingAttributes()),
                                        String.valueOf(dataset.getQuasiIdentifyingAttributes().length),
                                        ssNum != null ? ssNum.toString() : "",
                                        ss_string,
                                        qs_string,
                                        expType
                                        );
        

        ARXResult result = anonymizer.anonymize(dataset.getArxData(), config);

        AttributeStatistics attrStats = null;
        DataHandle handle = dataset.getHandle();
        if (sa != null) attrStats = AttributeStatistics.get(dataset, sa);



        Double il_arx = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        Double il_abs_from_utility = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        Double il_rel = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        Double il_sc  = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;

        if (result.getGlobalOptimum() != null) {

        	ARXNode arxOptimum = result.getGlobalOptimum();

        	il_arx = Double.valueOf(arxOptimum.getLowestScore().toString());

        	String[][]  scOutputData = this.converter.toArray(result.getOutput(arxOptimum),dataset.getInputDataDef());
        	result.getOutput(arxOptimum).release();
        	il_abs_from_utility = this.measure.evaluate(scOutputData, arxOptimum.getTransformation()).getUtility();
        	il_rel = (il_abs_from_utility - dataset.getMinInfoLoss(this.benchmarkMeasure)) / (dataset.getMaxInfoLoss(this.benchmarkMeasure) - dataset.getMinInfoLoss(this.benchmarkMeasure));

        	il_sc = this.measureSoriaComas.evaluate(scOutputData, arxOptimum.getTransformation()).getUtility();
        } else {
        	il_sc = il_rel = il_arx = il_abs_from_utility = il_rel = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        }


        if (BenchmarkMeasure.SORIA_COMAS.equals(this.benchmarkMeasure)) {
        	il_sc = getOptimumsAbsIlByFullTraversal(measure, dataset, result);
        }
        

        
        // put info-losses into results-file
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_SORIA_COMAS, formatter.format(il_sc));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_ARX,         formatter.format(il_arx));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_ABS,         formatter.format(il_abs_from_utility));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_REL,         formatter.format(il_rel));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_MIN,         formatter.format(dataset.getMinInfoLoss(this.benchmarkMeasure)));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_MAX,         formatter.format( dataset.getMaxInfoLoss(this.benchmarkMeasure)));
        
        // report solution ratio
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.DIFFICULTY, calculateDifficulty(result));
        
        // put stats for sensitive attributes into results-file
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.NUM_VALUES, sa != null && attrStats.getDomainSize() != null ?
                attrStats.getDomainSize().doubleValue() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.SKEWNESS, sa != null && attrStats.getSkewness() != null ?
                attrStats.getSkewness() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.KUROTSIS, sa != null && attrStats.getKurtosis() != null ?
                attrStats.getKurtosis() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.STAND_DEVIATION, sa != null && attrStats.getStandDeviation() != null ?
                attrStats.getStandDeviation() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.VARIATION_COEFF, sa != null && attrStats.getVariance_coeff() != null ?
                attrStats.getVariance_coeff() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.FREQ_DEVI, sa != null ?
                (attrStats.getFrequencyDeviation() != null ?attrStats.getFrequencyDeviation() : attrStats.getDeviation_norm()) : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.QUARTIL_COEFF, sa != null && attrStats.getQuartil_coeff() != null ?
                attrStats.getQuartil_coeff() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.ENTROPY, sa != null ?
                attrStats.getEntropy() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.EFD_SCORE, sa != null  && attrStats.getFrequencyDeviation() != null ?
                attrStats.getEntropy() * attrStats.getFrequencyDeviation() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.FREQ_SPAN, sa != null  && attrStats.getFrequencySpan() != null ?
                attrStats.getFrequencySpan() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);

        // Write results incrementally
        BenchmarkSetup.BENCHMARK.getResults().write(new File(resultsFileName));
        
        handle.release();
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


		for (ARXNode[] level : lattice.getLevels()) {

			// For each transformation
			for (ARXNode node : level) {
				

				// Make sure that every transformation is classified correctly
				if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
					result.getOutput(node).release();
				}				
				
				if (Anonymity.ANONYMOUS == node.getAnonymity()) {

					String[][]  scOutputData = this.converter.toArray(result.getOutput(node, false),dataset.getInputDataDef());
					//result.getOutput(node).release();
					Double il = this.measure.evaluate(scOutputData, node.getTransformation()).getUtility();

					if (il < scOptimumVal) {
						scOptimumNode = node;
						scOptimumVal = il;
					}

				}

			}
		}
		if (scOptimumNode != null) {
			ret = scOptimumVal;
			System.out.println(";" + Arrays.toString(scOptimumNode.getTransformation()) + ";" + formatter.format(ret));
		}
		return ret;
	}

	private static String assemblePrivacyModelString(BenchmarkCriterion criterion, Integer k, Double c, Integer l, Double t, double suppFactor,
			Double d, Double b) {
		return new PrivacyModel(criterion, k, c, l, t, d, b).toString();
	}

	/**
     * @param pm
     * @param suppFactor
     * @return
     */
    public static String assemblePrivacyModelString(PrivacyModel pm, double suppFactor) {
		return assemblePrivacyModelString(pm.getCriterion(), pm.getK(), pm.getC(), pm.getL(), pm.getT(), new Double(suppFactor), pm.getD(), pm.getB());
	}

	private double calculateDifficulty(ARXResult result) {
        int numSolutions = 0;
        ARXLattice lattice = result.getLattice();
        for (ARXNode[] level : lattice.getLevels()) {
            for (ARXNode node : level) {
         
            	// Make sure that every transformation is classified correctly
            	if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
            		result.getOutput(node).release();
            	}
            	
            	if (node.getAnonymity() == Anonymity.ANONYMOUS) {
            		numSolutions++;
            		
            	// Sanity check
            	} else if (node.getAnonymity() != Anonymity.NOT_ANONYMOUS) {
	            	throw new RuntimeException("Solution space is still not classified completely");   
                }
            }
        }
        
        return 1- (((double) numSolutions) / ((double) lattice.getSize()));
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

	/**
	 * @param datafile
	 * @param bmMeasure
	 * @param sa
	 * @param outputStream
	 * @param includeInsensitiveAttributes
	 * @throws IOException
	 */
	public static void compareRelPAs(BenchmarkDatafile datafile, BenchmarkMeasure bmMeasure, String sa, PrintStream outputStream, boolean includeInsensitiveAttributes) throws IOException {
		String printString = "Running " + datafile.toString() + " with SA=" + sa;
		outputStream.println(printString);
		System.out.println(printString);
		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsCombinedWithK()) {



			BenchmarkCriterion[] criteria = null;
			if (BenchmarkCriterion.K_ANONYMITY.equals(privacyModel.getCriterion())) {
				criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
			} else {
				criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, privacyModel.getCriterion() };
			}

			BenchmarkDataset dataset = new BenchmarkDataset(datafile, criteria, sa);
			BenchmarkDriver driver = new BenchmarkDriver(bmMeasure, dataset);

			String maxPAStr[] = driver.findOptimalRelPA(0.05, dataset,
					sa,
					includeInsensitiveAttributes, privacyModel, null, null);

			System.out.  format(new Locale("de", "DE"), "%s;%s%n", privacyModel.toString(), maxPAStr[0]);
			outputStream.format(new Locale("de", "DE"), "%s;%s%n", privacyModel.toString(), maxPAStr[0]);
		}
	}
	
	
	/**
	 * @param suppFactor
	 * @param dataset
	 * @param sa
	 * @param includeInsensitiveAttribute
	 * @param privacyModel
	 * @param minLevels TODO
	 * @param maxLevels TODO
	 * @return
	 * @throws IOException
	 */
	public String[] findOptimalRelPA(
			double suppFactor, BenchmarkDataset dataset,
			String sa,
			boolean includeInsensitiveAttribute, PrivacyModel privacyModel, int[] minLevels, int[] maxLevels
			) throws IOException {
		
//		System.out.println(config.getPrivacyModels());
	
		ARXConfiguration config = BenchmarkDriver.getConfiguration(dataset, suppFactor, this.benchmarkMeasure, sa, privacyModel,dataset.getCriteria());
		ARXAnonymizer anonymizer = new ARXAnonymizer();
		
        DataDefinition dataDef = dataset.getArxData().getDefinition();
        
        String[] qiS = BenchmarkDataset.getQuasiIdentifyingAttributes(dataset.getDatafile());
		for (int i = 0; i < qiS.length; i++) {
			String qi = qiS[i];
			if (minLevels != null) {
				dataDef.setMinimumGeneralization(qi, minLevels[i]);
			}
			if (maxLevels != null) {
				dataDef.setMaximumGeneralization(qi, maxLevels[i]);
			}
		}
	
		ARXResult result = anonymizer.anonymize(dataset.getArxData(), config);
		
		ARXNode optNode = null;
		double relPA = -Double.MAX_VALUE;
		double absPA = -Double.MAX_VALUE;

		ARXLattice lattice = result.getLattice();
		
        // Expand nodes such that all anonymous nodes are being materialized
        for (int level = 0; level < lattice.getLevels().length; level++) {
            for (ARXNode node : lattice.getLevels()[level]) {
                node.expand();
            }
        }

        DataHandle handle = null;
        String minPAStr = "NaN";
        String maxPAStr = "NaN";
        String gainStr = "NaN";
        
		boolean baselineValuesCaptured = false;
		
		int[] exampleTrafo = new int[] { 1, 1, 0 };
		boolean onlyVisitExampleTrafo = false;
		
        for (ARXNode[] level : lattice.getLevels()) {

        	for (ARXNode node : level) {

        		if (!onlyVisitExampleTrafo || Arrays.equals(exampleTrafo, node.getTransformation())) {

        			// Make sure that every transformation is classified correctly
        			if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
        				result.getOutput(node).release();
        			}				
        			if (Anonymity.ANONYMOUS == node.getAnonymity() && relPA != 1d) {
        				
        				try {

        					handle = result.getOutput(node);
        					List<String> predictingAttrs = new ArrayList<String>(handle.getDefinition().getQuasiIdentifyingAttributes());
        					if (includeInsensitiveAttribute) {
        						predictingAttrs.add(dataset.getInSensitiveAttribute());
        					}
        					StatisticsClassification stats = handle.getStatistics().getClassificationPerformance(
        							predictingAttrs.toArray(new String[predictingAttrs.size()]), sa, ARXLogisticRegressionConfiguration.create()
        							.setNumFolds(3).setMaxRecords(Integer.MAX_VALUE).setSeed(0xDEADBEEF));

        					if (!baselineValuesCaptured) {
        						minPAStr = String.format(new Locale("de", "DE"), "%.4f", stats.getZeroRAccuracy());
        						maxPAStr = String.format(new Locale("de", "DE"), "%.4f", stats.getOriginalAccuracy());
        						gainStr  = String.format(new Locale("de", "DE"), "%.4f", stats.getOriginalAccuracy() - stats.getZeroRAccuracy());
        					}        				
        					baselineValuesCaptured = true;

        					double epsilon = 0.05;
        					double absAccuracy = stats.getAccuracy();
        					double relAccuracy = (absAccuracy - stats.getZeroRAccuracy() ) / (stats.getOriginalAccuracy() - stats.getZeroRAccuracy());
        					if (!Double.isNaN(relAccuracy) && !Double.isInfinite(relAccuracy)) {
        						if (relAccuracy > relPA) {
        							optNode = node;
        						}
        						absPA = Math.max(absAccuracy, absPA);
        						relPA = Math.max(relAccuracy, relPA);
        						if (relPA < 0d && relPA > 0d - epsilon) relPA = 0d;
        						if (relPA > 1d && relPA <= 1d + epsilon) relPA = 1d;
        					}


        					handle.release();

        				} catch (ParseException e) {
        					throw new RuntimeException(e);
        				}
        			}
        		}
        	}
        }

        String trafoStr = Arrays.toString((optNode != null ? optNode.getTransformation() : new int[] {}));

        String[][] outputArray = null;
        String ilNueStr = "NaN";
        String ilLossStr = "NaN";
        String ilScStr  = "NaN";
        DataHandle outHandle = null;
        int numOfsuppressedRecords = -1;
        
        String[] disclosureRiskResults = null;
        if (optNode != null) {
        	outHandle = result.getOutput(optNode, false);
        	outputArray = this.converter.toArray(outHandle, dataset.getInputDataDef());
        	numOfsuppressedRecords = outHandle.getStatistics().getEquivalenceClassStatistics().getNumberOfOutlyingTuples();
        	Locale deLoc = new Locale ("DE", "de");

        	ilNueStr  = getRelativeInfoLoss(dataset, optNode, outputArray, deLoc, BenchmarkMeasure.ENTROPY);
        	ilLossStr = getRelativeInfoLoss(dataset, optNode, outputArray, deLoc, BenchmarkMeasure.LOSS);

        	// calculate SSE
        	DataDefinition numDataDef = dataset.getArxData(true).getDefinition();
        	qiS = optNode.getQuasiIdentifyingAttributes();
        	for (int i = 0; i < qiS.length; i++) {
        		String qi = qiS[i];
        		numDataDef.setMinimumGeneralization(qi, optNode.getTransformation()[i]);
        		numDataDef.setMaximumGeneralization(qi, optNode.getTransformation()[i]);
        	}

            DisclosureRiskCalculator.prepare(dataset.getDatafile(), sa);
        	ARXResult numResult = anonymizer.anonymize(dataset.getArxData(true), config);
            DisclosureRiskCalculator.summarize();
            disclosureRiskResults = DisclosureRiskCalculator.toStringArray();
        	DataHandle numOutHandle = numResult.getOutput(optNode, false);
        	String[][] numOutputArray = this.converter.toArray(numOutHandle, dataset.getInputDataDef(true));
        	ilScStr  = String.format(deLoc, "%.3f", this.measureSoriaComas.evaluate(numOutputArray, optNode.getTransformation()).getUtility());        	
        } else {
        	DisclosureRiskCalculator.prepare(dataset.getDatafile(), sa);
            DisclosureRiskCalculator.summarize();
            disclosureRiskResults = DisclosureRiskCalculator.toStringArray();
        }
        if (optNode != null) {
        	outHandle.release();
        }    		

        if (relPA == -Double.MAX_VALUE) relPA = Double.NaN;
        if (absPA == -Double.MAX_VALUE) absPA = Double.NaN;
        String absPAStr = String.format(new Locale("DE", "de"), "%.3f", absPA);
        String relPAStr = String.format(new Locale("DE", "de"), "%.3f", relPA);
        String[] ilMeasureValues = new String[] { ilNueStr, ilLossStr, ilScStr };


        String numSupRecsStr = String.valueOf(numOfsuppressedRecords);
        
        
        return (String[]) BenchmarkDriver.concat(
        		BenchmarkDriver.concat(
        				new String[] { relPAStr, absPAStr, minPAStr, maxPAStr, gainStr, trafoStr,  numSupRecsStr },
        				ilMeasureValues),
        		disclosureRiskResults);
	}

	public static String[] getCombinedRelPaAndDisclosureRiskHeader() {
		return (String[]) BenchmarkDriver.concat(new String[] { "RelPA", "AbsPA", "MinPA", "MaxPA", "Gain", "Trafo", "NumSuppRecs", "IL-NUE", "IL-Loss", "IL-SSE" }, DisclosureRiskCalculator.getHeader());
	}

	private String getRelativeInfoLoss(BenchmarkDataset dataset, ARXNode optNode, String[][] outputArray,
			Locale deLoc, BenchmarkMeasure bmMeasure) {
		
		UtilityMeasure<Double> utilityMeasure = null;
		
		switch (bmMeasure) {
		case ENTROPY:
			utilityMeasure = measureNue;
			break;
		case LOSS:
			utilityMeasure = measureLoss;
			break;
		default:
			break;
		
		}
		
		String ilStr;
		double ilAbs = utilityMeasure.evaluate(outputArray, optNode.getTransformation()).getUtility();
		double ilRel =                      (ilAbs - dataset.getMinInfoLoss(bmMeasure)) /
				(dataset.getMaxInfoLoss(bmMeasure) - dataset.getMinInfoLoss(bmMeasure));
		ilStr = String.format(deLoc, "%.3f", ilRel);
		return ilStr;
	}

	public static double calcNormalizedEntropy(double[] freqs) {
		return calcEntropy(freqs) / log2(freqs.length);
	}

	public static double calcEntropy(double[] freqs) {
		// calculate entropy
		double entropy = 0d;
		for (int i = 0; i < freqs.length; i++) {
			if (freqs[i] != 0d) entropy += freqs[i] * log2(freqs[i]);
		}
		entropy *= -1d;
		return entropy;
	}
    
    /** calculate the base-2 logarithm
     * @param r
     * @return
     */
    private static double log2(double r) {
    	return Math.log(r) / Math.log(2d);
    }

	public NumberFormat getFormatter() {
		return formatter;
	}
    
    public static Object[] concat(Object[] first, Object[] second) {
	    List<Object> both = new ArrayList<Object>(first.length + second.length);
	    Collections.addAll(both, first);
	    Collections.addAll(both, second);
	    return both.toArray(new String[both.size()]);
	}

	public static String toCsvString(String[] stringArr, String sep) {
    	
    	String ret = "";
    	
    	for (int i = 0; i < stringArr.length - 1; i++) {
    		ret += stringArr[i] + sep;
    	}
    	
    	ret += stringArr[stringArr.length - 1];
    	
    	return ret;
    }
}
