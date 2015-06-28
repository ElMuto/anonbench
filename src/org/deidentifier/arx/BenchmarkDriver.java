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
import java.util.HashMap;

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
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class implements the main benchmark driver
 * @author Fabian Prasser
 */
public class BenchmarkDriver {

    private final static HashMap<String, AttributeStatistics> statsCache = new HashMap<String, AttributeStatistics>();

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
     * @param arxData TODO
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
                                 Data arxData, BenchmarkCriterion[] criteria,
                                 boolean subsetBased, Integer k, Integer l,
                                 Double c, Double t, Double dMin,
                                 Double dMax, String sa, Integer ssNum
            ) throws IOException {

//        Data oldArxData = dataset.toArxData(criteria);
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
        DataHandle handle = arxData.getHandle();
        if (sa != null) attrStats = processSA(dataset, handle, sa, 0);

        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS, result.getGlobalOptimum() != null ?
                Double.valueOf(result.getGlobalOptimum().getMinimumInformationLoss().toString()) :
                    BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
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
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.NORMALIZED_DEVIATION, sa != null && attrStats.getDeviation_norm() != null ?
                attrStats.getDeviation_norm() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.FREQ_DEVI, sa != null && attrStats.getFrequencyDeviation() != null ?
                attrStats.getFrequencyDeviation() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);
        BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.QUARTIL_COEFF, sa != null && attrStats.getQuartil_coeff() != null ?
                attrStats.getQuartil_coeff() : BenchmarkSetup.NO_RESULT_FOUND_DOUBLE_VAL);

        // Write results incrementally
        BenchmarkSetup.BENCHMARK.getResults().write(new File("results/results.csv"));
        
        handle.release();
    }



    public static AttributeStatistics processSA(BenchmarkDataset dataset, DataHandle handle, String sa, int verbosity) throws IOException {
        String statsKey = dataset.toString() + "-" + sa;
        if (statsCache.containsKey(statsKey)) {
            return statsCache.get(statsKey);
        } else {
            Integer numValues = null;
            Double  frequencyDeviation = null;
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

            int saColIndex = handle.getColumnIndexOf(sa);
            String[] distinctValues = handle.getStatistics().getDistinctValues(saColIndex);
            numValues = distinctValues.length;
            if (verbosity >= 1) System.out.print("    " + sa + ": " + distinctValues.length + " different values");
            if (
                    BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "PWGTP".equals(sa.toString()) ||
                    BenchmarkDatafile.ACS13.equals(dataset.getDatafile()) && "INTP".equals(sa.toString()) ||
                    BenchmarkDatafile.ADULT.equals(dataset.getDatafile()) && "age".equals(sa.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "INCOME".equals(sa.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "MINRAMNT".equals(sa.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "NGIFTALL".equals(sa.toString()) ||
                    BenchmarkDatafile.CUP.equals(dataset.getDatafile()) && "RAMNTALL".equals(sa.toString()) ||
                    BenchmarkDatafile.FARS.equals(dataset.getDatafile()) && "ideathday".equals(sa.toString()) ||
                    BenchmarkDatafile.IHIS.equals(dataset.getDatafile()) && "YEAR".equals(sa.toString())
                    ) {

                // initialize stats package and read values
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (int rowNum = 0; rowNum < handle.getNumRows(); rowNum++) {
                    stats.addValue(Double.parseDouble(handle.getValue(rowNum, saColIndex)));
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
                handle.getStatistics().getSummaryStatistics(false);

                // print
                if (verbosity >= 1) {
                    System.out.println("\n      variance          = " + variance);
                    System.out.println(  "      skewness          = " + skewness);
                    System.out.println(  "      kurtosis          = " + kurtosis);
                    System.out.println(  "      stand. dev.       = " + standDeviation);
                    System.out.println(  "      variance_coeff    = " + variance_coeff);
                    System.out.println(  "      stand. dev. norm. = " + deviation_norm);
                    System.out.println(  "      quartil coeff.    = " + quartil_coeff);
                    System.out.println(  "      arith. mean       = " + mean_arith);
                    System.out.println(  "      geom. mean        = " + mean_geom);
                    System.out.println(  "      median            = " + median);
                }
            } else {
            	
            	// get the frequencies of attribute instatiations
                double[] freqs  = handle.getStatistics().getFrequencyDistribution(handle.getColumnIndexOf(sa)).frequency;
                
                // initialize stats package and read values
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (int i = 0; i < freqs.length; i++) {
                    stats.addValue(freqs[i]);
                }
                frequencyDeviation = stats.getStandardDeviation();
                
                if (verbosity >= 1) System.out.println(", variance of frequencies = " + frequencyDeviation);
                if (verbosity >= 2) {
                    System.out.println("      " + Arrays.toString(distinctValues));
                    System.out.println("      " + Arrays.toString(freqs));
                }
            }
            
            statsCache.put(statsKey, new AttributeStatistics(numValues, frequencyDeviation,
                                           variance, skewness, kurtosis,
                                           standDeviation, variance_coeff, deviation_norm,
                                           quartil_coeff, mean_arith,
                                           mean_geom, median));
            
            return statsCache.get(statsKey);
        }
    }
}
