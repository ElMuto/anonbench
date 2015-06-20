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

import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.Inclusion;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;

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
                                                    Integer k, Integer l, Integer c,
                                                    Double t, Double dMin, Double dMax,
                                                    String sa,
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
                config.addCriterion(new DPresence(dMin, dMax, dataset.getResearchSubset()));
                break;
            case INCLUSION:
                config.addCriterion(new Inclusion(dataset.getResearchSubset()));
                break;
            case K_ANONYMITY:
                config.addCriterion(new KAnonymity(k));
                break;
            case L_DIVERSITY_RECURSIVE:
                config.addCriterion(new RecursiveCLDiversity(sa, l, c));
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
    


	public static void anonymize(
			BenchmarkMeasure metric,
			double suppFactor, BenchmarkDataset dataset,
			BenchmarkCriterion[] criteria, boolean subsetBased,
			Integer k, Integer l, Integer c,
			Double t, Double dMin, Double dMax,
			String sa, Integer ssNum
			) throws IOException {

        Data arxData = dataset.toArxData(criteria);
        ARXConfiguration config = getConfiguration(dataset, suppFactor, metric, k, l, c, t, dMin, dMax, sa, criteria);
        ARXAnonymizer anonymizer = new ARXAnonymizer();

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
        
        if (result.getGlobalOptimum() != null) {
        	BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS, Double.valueOf(result.getGlobalOptimum().getMinimumInformationLoss().toString()));
        } else {
        	System.out.println("No solution found");
        	BenchmarkSetup.BENCHMARK.addValue(BenchmarkSetup.INFO_LOSS, BenchmarkSetup.NO_SOULUTION_FOUND_DOUBLE_VAL);
        }

		// Write results incrementally
		BenchmarkSetup.BENCHMARK.getResults().write(new File("results/results.csv"));
	}
}
