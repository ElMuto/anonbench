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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXLattice.Anonymity;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.aggregates.StatisticsClassification;
import org.deidentifier.arx.criteria.DDisclosurePrivacy;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.EqualDistanceTCloseness;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.Inclusion;
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
public class BenchmarkDriver {
    private final static HashMap<String, AttributeStatistics> statsCache = new HashMap<String, AttributeStatistics>();
    
    private final UtilityMeasure<Double> measure;
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

	    formatter.setMinimumFractionDigits(4);
	    formatter.setMaximumFractionDigits(4);
	    formatter.setMaximumIntegerDigits(20);

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
		case PRECISION:			this.measure = 	new UtilityMeasurePrecision<Double>(header, hierarchies);
			break;
		case SORIA_COMAS:
			this.measure = 	new UtilityMeasureSoriaComas(inputArray);
			break;
		default:
			throw new RuntimeException("Invalid measure");
        }
	}

	/**
     * Returns a configuration for the ARX framework
     * @param dataset
	 * @param suppFactor
	 * @param metric
	 * @param k
	 * @param l
	 * @param c
	 * @param t
	 * @param d TODO
	 * @param dMin
	 * @param dMax
	 * @param sa
	 * @param criteria
	 * @param customQiCount TODO
	 * @return
     * @throws IOException
     */
    private static ARXConfiguration getConfiguration(BenchmarkDataset dataset, Double suppFactor,  BenchmarkMeasure metric,
                                                     Integer k, Integer l, Double c,
                                                     Double t, Double d, Double dMin,
                                                     Double dMax, String sa,
                                                     Integer ssNum, BenchmarkCriterion... criteria) throws IOException {

        ARXConfiguration config = ARXConfiguration.create();

        switch (metric) {
        case ENTROPY:
        case SORIA_COMAS:
            config.setMetric(Metric.createEntropyMetric());
            break;
        case LOSS:
            config.setMetric(Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN));
            break;
        case AECS:
            config.setMetric(Metric.createAECSMetric());
            break;
        case DISCERNABILITY:
            config.setMetric(Metric.createDiscernabilityMetric());
            break;
        case PRECISION:
            config.setMetric(Metric.createPrecisionMetric());
            break;
        case HEIGHT:
            config.setMetric(Metric.createHeightMetric());
            break;
        default:
            throw new RuntimeException("Invalid benchmark metric");
        }

        config.setMaxOutliers(suppFactor);

        for (BenchmarkCriterion crit : criteria) {
            switch (crit) {
            case D_PRESENCE:
                config.addCriterion(new DPresence(dMin, dMax, dataset.getResearchSubset(ssNum)));
                break;
            case INCLUSION:
                config.addCriterion(new Inclusion(dataset.getResearchSubset(ssNum)));
                break;
            case K_ANONYMITY:
                config.addCriterion(new KAnonymity(k));
                break;
            case L_DIVERSITY_DISTINCT:
                config.addCriterion(new DistinctLDiversity(sa, l));
                break;
            case L_DIVERSITY_ENTROPY:
                config.addCriterion(new org.deidentifier.arx.criteria.EntropyLDiversity(sa, l));
                break;
            case L_DIVERSITY_RECURSIVE:
                config.addCriterion(new RecursiveCLDiversity(sa, c, l));
                break;
            case T_CLOSENESS_HD:
                config.addCriterion(new HierarchicalDistanceTCloseness(sa, t, dataset.loadHierarchy(sa)));
                break;
            case T_CLOSENESS_ED:
                config.addCriterion(new EqualDistanceTCloseness(sa, t));
                break;
            case D_DISCLOSURE_PRIVACY:
                config.addCriterion(new DDisclosurePrivacy(sa, d));
                break;
            default:
                throw new RuntimeException("Invalid criterion");
            }
        }
        return config;
    }
    



    public void anonymize(BenchmarkMeasure measure,
                         double suppFactor, BenchmarkDataset dataset,
                         boolean subsetBased, Integer k,
                         Integer l, Double c, Double t,
                         Double d, Double dMin, Double dMax,
                         String sa, Integer ssNum, String resultsFileName) throws IOException
    {
    							anonymize (		measure, suppFactor,  dataset,
					                subsetBased,  k,
					                l,  c,  t,
					                d,  dMin,  dMax,
					                sa,  ssNum,
					                new Double[] { null }, resultsFileName);
    }


    public void anonymize(
                                 BenchmarkMeasure measure,
                                 double suppFactor, BenchmarkDataset dataset,
                                 boolean subsetBased, Integer k,
                                 Integer l, Double c, Double t,
                                 Double d, Double dMin, Double dMax,
                                 String sa, Integer ssNum, Double[] accuracies, String resultsFileName
            ) throws IOException {

        ARXConfiguration config = getConfiguration(dataset, suppFactor, measure, k, l, c, t, d, dMin, dMax, sa, ssNum, dataset.getCriteria());
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
//        String bar = assemblePrivacyModelString(foo, k, c, l, t, suppFactor, d);
        String bcName = assemblePrivacyModelString(bc, k, c, l, t, suppFactor, d);
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
        if (sa != null) attrStats = analyzeAttribute(dataset, handle, sa, 0);
        

 
        Double il_arx = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        Double il_abs = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        Double il_rel = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        
        if (result.getGlobalOptimum() != null) {

            il_arx = Double.valueOf(result.getGlobalOptimum().getMinimumInformationLoss().toString());            
            
        } else {
        	il_rel = il_arx = il_abs = il_rel = BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL;
        }
        
        il_abs = getOptimumsAbsIlByFullTraversal(measure, dataset, result);
        il_rel = (il_abs - dataset.getMinInfoLoss(this.benchmarkMeasure)) / (dataset.getMaxInfoLoss(this.benchmarkMeasure) - dataset.getMinInfoLoss(this.benchmarkMeasure));

        
        // put info-losses into results-file
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_ARX,         formatter.format(il_arx));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_ABS,         formatter.format(il_abs));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_REL,         formatter.format(il_rel));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_MIN,         formatter.format(dataset.getMinInfoLoss(this.benchmarkMeasure)));
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS_MAX,        formatter.format( dataset.getMaxInfoLoss(this.benchmarkMeasure)));
        
        // report solution ratio
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.DIFFICULTY, calculateDifficulty(result));
        
        // put stats for sensitive attributes into results-file
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.NUM_VALUES, sa != null && attrStats.getNumValues() != null ?
                attrStats.getNumValues().doubleValue() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
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
				
				if (Anonymity.ANONYMOUS == node.getAnonymity()) {

					String[][]  scOutputData = this.converter.toArray(result.getOutput(node),dataset.getInputDataDef());
					result.getOutput(node).release();
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
			System.out.println("\t" + Arrays.toString(scOptimumNode.getTransformation()) + ": " + formatter.format(ret) + " (Utility with full traversal)");
		}
		return ret;
	}

	private static String assemblePrivacyModelString(BenchmarkCriterion criterion, Integer k, Double c, Integer l, Double t, double suppFactor,
			Double d) {
		return new PrivacyModel(criterion, k, c, l, t, d).toString();
	}

	/**
     * @param pm
     * @param suppFactor
     * @return
     */
    public static String assemblePrivacyModelString(PrivacyModel pm, double suppFactor) {
		return assemblePrivacyModelString(pm.getCriterion(), pm.getK(), pm.getC(), pm.getL(), pm.getT(), new Double(suppFactor), pm.getD());
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



    public double calculateMaximalClassificationAccuracy(
			double suppFactor, BenchmarkDataset dataset,
			Integer k,
			Integer l, Double c, Double t,
			Double d, Double dMin, Double dMax,
			String sa, Integer ssNum
			) throws IOException {
	
		ARXConfiguration config = getConfiguration(dataset, suppFactor, this.benchmarkMeasure, k, l, c, t, d, dMin, dMax, sa, ssNum, dataset.getCriteria());
		ARXAnonymizer anonymizer = new ARXAnonymizer();
	
		ARXResult result = anonymizer.anonymize(dataset.getArxData(), config);
		
		double optimalAccuracy = -Double.MAX_VALUE;
				
		ARXLattice lattice = result.getLattice();
		
		for (ARXNode[] level : lattice.getLevels()) {
			for (ARXNode node : level) {
				
	        	// Make sure that every transformation is classified correctly
	        	if (!(node.getAnonymity() == Anonymity.ANONYMOUS || node.getAnonymity() == Anonymity.NOT_ANONYMOUS)) {
	        		result.getOutput(node).release();
	        	}				
				if (Anonymity.ANONYMOUS == node.getAnonymity()) {
					try {
	
						DataHandle handle = result.getOutput(node);
						List<String> qis = new ArrayList<String>(handle.getDefinition().getQuasiIdentifyingAttributes());
						StatisticsClassification stats = handle.getStatistics().getClassificationPerformance(
								qis.toArray(new String[qis.size()]), sa, ARXLogisticRegressionConfiguration.create()
								.setNumFolds(3).setMaxRecords(Integer.MAX_VALUE).setSeed(0xDEADBEEF));
	
						double accuracy = (stats.getAccuracy() - stats.getZeroRAccuracy() ) / (stats.getOriginalAccuracy() - stats.getZeroRAccuracy());
						if (!Double.isNaN(accuracy) && !Double.isInfinite(accuracy)) {
							optimalAccuracy = Math.max(accuracy, optimalAccuracy);
							if (optimalAccuracy < 0d && optimalAccuracy > -0.05d) optimalAccuracy = 0d;
							if (optimalAccuracy > 1d && optimalAccuracy <= 1.05) optimalAccuracy = 1d;
						}
//						System.out.printf("\tstats.getZeroRAccuracy()    = %.2f", stats.getZeroRAccuracy() * 100d);
//						System.out.printf("\tstats.getOriginalAccuracy() = %.2f", stats.getOriginalAccuracy() * 100d);
	
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				}
			}
	    }
	    
		return optimalAccuracy;
	}

	public static void compareRelPAs(BenchmarkDatafile datafile, String sa, PrintStream outputStream, BenchmarkMeasure bmMeasure) throws IOException {
		String printString = "Running " + datafile.toString() + " with SA=" + sa;
		outputStream.println(printString);
		System.out.println(printString);
		// for each privacy model
		for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsCombinedWithK()) {
			BenchmarkDataset dataset = new BenchmarkDataset(datafile, new BenchmarkCriterion[] { privacyModel.getCriterion() }, sa);
			BenchmarkDriver driver = new BenchmarkDriver(bmMeasure, dataset);
				
				double maxPA = driver.calculateMaximalClassificationAccuracy(0.05, dataset,
						privacyModel.getK(),
						privacyModel.getL(), privacyModel.getC(), privacyModel.getT(), 
						privacyModel.getD(), null, null,
						sa, null);
	
				System.out.  format(new Locale("en", "US"), "%s;%.4f%n", privacyModel.toString(), maxPA);
				outputStream.format(new Locale("en", "US"), "%s;%.4f%n", privacyModel.toString(), maxPA);
		}
	}

	public static void compareRelPAs(BenchmarkDatafile datafile, String sa, PrintStream outputStream)
			throws IOException {
		BenchmarkDriver.compareRelPAs(datafile, sa, outputStream, BenchmarkMeasure.ENTROPY);
	}

	/**
     * @param dataset
     * @param handle
     * @param attr
     * @param verbosity
     * @return
     * @throws IOException
     */
    public static AttributeStatistics analyzeAttribute(BenchmarkDataset dataset, DataHandle handle, String attr, int verbosity) throws IOException {
        String statsKey = dataset.toString() + "-" + attr;
        if (statsCache.containsKey(statsKey)) {
            return statsCache.get(statsKey);
        } else {
            Integer numValues = null;
            Double  frequencyDeviation = null;
            Double  frequencySpan = null;
            Double  variance = null;
            Double  skewness = null;
            Double  kurtosis = null;
            Double  standDeviation = null;
            Double  deviation_norm = null;
            Double  variance_coeff = null;
            Double  quartil_coeff = null;
            Double  mean_arith = null;
            Double  mean_geom = null; // done
            Double  median = null; // done

            int attrColIndex = handle.getColumnIndexOf(attr);
            String[] distinctValues = handle.getStatistics().getDistinctValues(attrColIndex);
            numValues = distinctValues.length;
            if (verbosity >= 1) System.out.println("    " + attr + " (domain size: " + distinctValues.length + ")");
            
            // get the frequencies of attribute instantiations
            double[] freqs  = handle.getStatistics().getFrequencyDistribution(handle.getColumnIndexOf(attr)).frequency;
            
            double normalizedEntropy = calcNormalizedEntropy(freqs);
            
            if (
                    BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "AGEP".equals(attr.toString()) ||
                    BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "PWGTP".equals(attr.toString()) ||
                    BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "INTP".equals(attr.toString()) ||
                    BenchmarkDatafile.ADULT.equals(dataset.getDatafile()) && "age".equals(attr.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "AGE".equals(attr.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "INCOME".equals(attr.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "MINRAMNT".equals(attr.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "NGIFTALL".equals(attr.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "RAMNTALL".equals(attr.toString()) ||
                    BenchmarkDatafile.FARS.equals(dataset.getDatafile()) && "iage".equals(attr.toString()) ||
                    BenchmarkDatafile.ATUS.equals(dataset.getDatafile()) && "Age".equals(attr.toString()) ||
                    BenchmarkDatafile.IHIS.equals(dataset.getDatafile()) && "AGE".equals(attr.toString()) ||
                    BenchmarkDatafile.IHIS.equals(dataset.getDatafile()) && "YEAR".equals(attr.toString())
                    ) {

                // initialize stats package and read values
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (int rowNum = 0; rowNum < handle.getNumRows(); rowNum++) {
                    try {
                        stats.addValue(Double.parseDouble(handle.getValue(rowNum, attrColIndex)));
                    } catch (java.lang.NumberFormatException e) { /* just ignore those entries */ }
                }

                // calculate stats
                variance = stats.getVariance();
                skewness = stats.getSkewness();
                kurtosis = stats.getKurtosis();
                standDeviation = stats.getStandardDeviation();
                variance_coeff = standDeviation / stats.getMean();
                deviation_norm = standDeviation / (stats.getMax() - stats.getMin());
                mean_arith = stats.getMean();
                mean_geom = stats.getGeometricMean();
                quartil_coeff = (stats.getPercentile(75) - stats.getPercentile(25)) / (stats.getPercentile(75) + stats.getPercentile(25));
                median = stats.getPercentile(50);
                
                // print
                if (verbosity >= 2) {
                    System.out.println("      stand. dev.        = " + standDeviation);
                    System.out.println("      stand. dev. norm.  = " + deviation_norm);
                    System.out.println("      variance_coeff     = " + variance_coeff);
                    System.out.println("      quartil coeff.     = " + quartil_coeff);
                    System.out.println("      skewness           = " + skewness);
                    System.out.println("      kurtosis           = " + kurtosis);
                    System.out.println("      arith. mean        = " + mean_arith);
                    System.out.println("      geom. mean         = " + mean_geom);
                    System.out.println("      median             = " + median);
                    System.out.println("      normalized entropy = " + normalizedEntropy);
                }
            } else {
                
                // initialize stats package and read values for calculating standard deviation
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (int i = 0; i < freqs.length; i++) {
                    stats.addValue(freqs[i]);
                }
                frequencyDeviation = stats.getStandardDeviation();
                frequencySpan = stats.getMax() - stats.getMin();
                
                if (verbosity >= 2) {
                    System.out.println("      std. deviation of frequencies = " + frequencyDeviation);
                    System.out.println("      normalized entropy            = " + normalizedEntropy);
                }
                if (verbosity >= 3) {
                    System.out.println("      " + Arrays.toString(distinctValues));
                    System.out.println("      " + Arrays.toString(freqs));
                }
            }
            
            statsCache.put(statsKey, new AttributeStatistics(numValues, frequencyDeviation,
            							   frequencySpan, variance, skewness,
                                           kurtosis, standDeviation, variance_coeff,
                                           deviation_norm, quartil_coeff,
                                           mean_arith, mean_geom, median, normalizedEntropy));
            
            return statsCache.get(statsKey);
        }
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
}
