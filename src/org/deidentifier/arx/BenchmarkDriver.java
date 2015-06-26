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
import java.util.Arrays;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.Inclusion;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class implements the main benchmark driver
 * @author Fabian Prasser
 */
public class BenchmarkDriver {

    /**
     * Returns a configuration for the ARX framework
     * @param dataset
     * @param suppFactor
     * @param metric
     * @param k
     * @param l
     * @param c
     * @param t
     * @param dMin
     * @param dMax
     * @param sa
     * @param criteria
     * @return
     * @throws IOException
     */
    private static ARXConfiguration getConfiguration(BenchmarkDataset dataset, Double suppFactor,  BenchmarkMeasure metric,
                                                    Integer k, Integer l, Double c,
                                                    Double t, Double dMin, Double dMax,
                                                    String sa, Integer ssNum,
                                                    BenchmarkCriterion... criteria) throws IOException {
        
        ARXConfiguration config = ARXConfiguration.create();
        
        switch (metric) {
        case ENTROPY:
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
            case T_CLOSENESS:
                config.addCriterion(new HierarchicalDistanceTCloseness(sa, t, dataset.loadHierarchy(sa)));
                break;
            default:
                throw new RuntimeException("Invalid criterion");
            }
        }
        return config;
    }
    


	/**
	 * @param metric
	 * @param suppFactor
	 * @param dataset
	 * @param criteria
	 * @param subsetBased
	 * @param k
	 * @param l
	 * @param c
	 * @param t
	 * @param dMin
	 * @param dMax
	 * @param sa
	 * @param ssNum
	 * @param printDatasetStats TODO
	 * @throws IOException
	 */
	public static void anonymize(
			BenchmarkMeasure metric,
			double suppFactor, BenchmarkDataset dataset,
			BenchmarkCriterion[] criteria, boolean subsetBased,
			Integer k, Integer l, Double c,
			Double t, Double dMin, Double dMax,
			String sa, Integer ssNum
			) throws IOException {

        Data arxData = dataset.toArxData(criteria/*, ssNum*/);
        ARXConfiguration config = getConfiguration(dataset, suppFactor, metric, k, l, c, t, dMin, dMax, sa, ssNum, criteria);
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        anonymizer.setMaxTransformations(210000);

		// Benchmark
		BenchmarkSetup.BENCHMARK.addRun(metric.toString(),
				String.valueOf(suppFactor),
				dataset.toString(),
				Arrays.toString(criteria),
				Boolean.toString(subsetBased),
				k != null ? k.toString() : "", l != null ? l.toString() : "", c != null ? c.toString() : "",
				t != null ? t.toString() : "", dMin != null ? dMin.toString() : "", dMax != null ? dMax.toString() : "",
				sa != null ? sa.toString() : "",
				Arrays.toString(dataset.getQuasiIdentifyingAttributes()),
				ssNum != null ? ssNum.toString() : "");
        
        ARXResult result = anonymizer.anonymize(arxData, config);
        
        AttributeStatistics attrStats = null;        
        if (sa != null) attrStats = processSA(dataset, arxData.getHandle(), sa, 0);
        
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS, result.getGlobalOptimum() != null ?
                Double.valueOf(result.getGlobalOptimum().getMinimumInformationLoss().toString()) :
                BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.NUM_VALUES, sa != null && attrStats.getNumValues() != null ?
                attrStats.getNumValues().doubleValue() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.VARIANCE, sa != null && attrStats.getVariance() != null ?
                attrStats.getVariance() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.SKEWNESS, sa != null && attrStats.getSkewness() != null ?
                attrStats.getSkewness() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.KUROTSIS, sa != null && attrStats.getKurtosis() != null ?
                attrStats.getKurtosis() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.DEVI, sa != null && attrStats.getDeviation() != null ?
                attrStats.getDeviation() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.DEVI_REL, sa != null && attrStats.getDeviation_rel() != null ?
                attrStats.getDeviation_rel() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.DEVI_NORM, sa != null && attrStats.getDeviation_norm() != null ?
                attrStats.getDeviation_norm() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.FREQ_VARI, sa != null && attrStats.getFrequencyVariance() != null ?
                attrStats.getFrequencyVariance() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);

		// Write results incrementally
		BenchmarkSetup.BENCHMARK.getResults().write(new File("results/results.csv"));
	}



    public static AttributeStatistics processSA(BenchmarkDataset dataset, DataHandle handle, String sa, int verbosity) throws IOException {
        Integer numValues = null;
        Double  variance = null;
        Double  skewness = null;
        Double  kurtosis = null;
        Double  deviation = null;
        Double  deviation_rel = null;
        Double  deviation_norm = null;
        Double  frequencyVariance = null;
        
        String[] values = handle.getStatistics().getDistinctValues(handle.getColumnIndexOf(sa));
        numValues = values.length;
        if (verbosity >= 1) System.out.print("    " + sa + ": " + values.length + " different values");
        if (
                BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "PWGTP".equals(sa.toString()) ||
                BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "INTP".equals(sa.toString()) ||
                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "INCOME".equals(sa.toString()) ||
                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "MINRAMNT".equals(sa.toString()) ||
                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "NGIFTALL".equals(sa.toString()) ||
                BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "RAMNTALL".equals(sa.toString()) ||
                BenchmarkDatafile.FARS.equals(dataset.getDatafile()) && "ideathday".equals(sa.toString()) ||
                BenchmarkDatafile.IHIS.equals(dataset.getDatafile()) && "YEAR".equals(sa.toString())
                ) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            for (int i = 0; i < values.length; i++) {
                stats.addValue(Double.parseDouble(values[i]));
            }
            
            variance = stats.getVariance();
            skewness = stats.getSkewness();
            kurtosis = stats.getKurtosis();
            deviation = stats.getStandardDeviation();
            deviation_rel = deviation / stats.getMean();
            deviation_norm = deviation / (stats.getMax() - stats.getMin());
            if (verbosity >= 1) {
                System.out.println("\n      variance        = " + variance);
                System.out.println(  "      skewness        = " + skewness);
                System.out.println(  "      kurtosis        = " + kurtosis);
                System.out.println(  "      deviation       = " + deviation);
                System.out.println(  "      deviation_rel   = " + deviation_rel);
                System.out.println(  "      deviation_norm  = " + deviation_norm);
            }
        } else {
            double[] freqs  = handle.getStatistics().getFrequencyDistribution(handle.getColumnIndexOf(sa)).frequency;
            frequencyVariance = StatUtils.variance(freqs);
            if (verbosity >= 1) System.out.println(", variance of frequencies = " + frequencyVariance);
            if (verbosity >= 2) {
                System.out.println("      " + Arrays.toString(values));
                System.out.println("      " + Arrays.toString(freqs));
            }
        }
        return new AttributeStatistics(numValues, frequencyVariance, variance, skewness, kurtosis, deviation, deviation_rel, deviation_norm);
    }
}
