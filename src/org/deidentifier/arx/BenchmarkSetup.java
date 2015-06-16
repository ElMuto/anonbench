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

import java.io.IOException;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.Inclusion;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;

/**
 * This class encapsulates most of the parameters of a benchmark run
 * @author Fabian Prasser
 */
public class BenchmarkSetup {

    static String RESULTS_DIR = "results";
    static String RESULTS_FILE_STEM = "results";
    static String RESULTS_FILE= RESULTS_DIR + "/" + RESULTS_FILE_STEM + ".csv";
    static String SUMMARY_FILE_STEM="results_summary";
    static double NO_SOULUTION_FOUND_DOUBLE_VAL=-1d;
    static String NO_SOULUTION_FOUND_STRING_VAL="n.s.f.";
    
    /**
     * Returns all metrics
     * @return
     */
    public static BenchmarkMetric[] getMetrics() {        
        return new BenchmarkMetric[] {
        		BenchmarkMetric.LOSS,
        		BenchmarkMetric.ENTROPY,
        		BenchmarkMetric.AECS,
        		BenchmarkMetric.DISCERNABILITY,
        		BenchmarkMetric.PRECISION,
        		BenchmarkMetric.HEIGHT
        		};
    }
    
    /**
     * Returns all suppression factors
     * @return
     */
    public static double[] getSuppressionFactors() {        
        return new double[] { 0d, 0.05d, 0.1d, 0.5d, 1d };
//      return new double[] { 0d, 0.05d, 0.1d };
//      return new double[] { 0d };
    }

    /**
     * Returns all datasets
     * @return
     */
    public static BenchmarkDataset[] getDatasets() {
        return new BenchmarkDataset[] { 
         new BenchmarkDataset(BenchmarkDatafile.ADULT, null),
         new BenchmarkDataset(BenchmarkDatafile.CUP, null),
         new BenchmarkDataset(BenchmarkDatafile.FARS, null),
         new BenchmarkDataset(BenchmarkDatafile.ATUS, null),
         new BenchmarkDataset(BenchmarkDatafile.IHIS, null),
         new BenchmarkDataset(BenchmarkDatafile.ACS13, 10),
                                        };
    }

    /**
     * Returns all non-subset-based sets of criteria
     * @return
     */
    public static BenchmarkCriterion[][] getNonSubsetBasedCriteria() {
        return new BenchmarkCriterion[][] {
            new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY },
            new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
            new BenchmarkCriterion[] { BenchmarkCriterion.T_CLOSENESS },
            new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
            new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS },
        };
    }
    

    /**
     * Returns all sets of subset based criteria
     * @return
     */
    public static BenchmarkCriterion[][] getSubsetBasedCriteria() {
        return new BenchmarkCriterion[][] {
            new BenchmarkCriterion[] { BenchmarkCriterion.INCLUSION, BenchmarkCriterion.K_ANONYMITY },
            new BenchmarkCriterion[] { BenchmarkCriterion.INCLUSION, BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
            new BenchmarkCriterion[] { BenchmarkCriterion.INCLUSION, BenchmarkCriterion.T_CLOSENESS},
            new BenchmarkCriterion[] { BenchmarkCriterion.INCLUSION, BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
            new BenchmarkCriterion[] { BenchmarkCriterion.INCLUSION, BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS },                                           
            new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE },
            new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.D_PRESENCE },
            new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE, BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
            new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE, BenchmarkCriterion.T_CLOSENESS },
            new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.D_PRESENCE, BenchmarkCriterion.L_DIVERSITY_RECURSIVE },
            new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.D_PRESENCE, BenchmarkCriterion.T_CLOSENESS },
        };
    }
    
    public static enum VARIABLES {
        UTLITY_METRIC {
            @Override
            public String toString() {
                return "Utility Metric";
            }
        },
        DATASET {
            @Override
            public String toString() {
                return "Dataset";
            }
        },
        CRITERIA {
            @Override
            public String toString() {
                return "Criteria";
            }
        },
        UTILITY_VALUE {
            @Override
            public String toString() {
                return "Utility Value";
            }
        },
        SUPPRESSION_FACTOR {
            @Override
            public String toString() {
                return "Suppression Factor";
            }
        },
        SUBSET_NATURE {
            @Override
            public String toString() {
                return "Subset Based";
            }
        },
    }

    public static enum BenchmarkCriterion {
        K_ANONYMITY {
            @Override
            public String toString() {
                return "k";
            }
        },
        L_DIVERSITY_DISTINCT {
            @Override
            public String toString() {
                return "ld";
            }
        },
        L_DIVERSITY_ENTROPY {
            @Override
            public String toString() {
                return "le";
            }
        },
        L_DIVERSITY_RECURSIVE {
            @Override
            public String toString() {
                return "lr";
            }
        },
        T_CLOSENESS {
            @Override
            public String toString() {
                return "t";
            }
        },
        D_PRESENCE {
            @Override
            public String toString() {
                return "d";
            }
        },
        INCLUSION {
            @Override
            public String toString() {
                return "i";
            }
        },
    }
    
    static enum BenchmarkMetric {
        LOSS {
            @Override
            public String toString() {
                return "Loss";
            }
        },
        ENTROPY {
            @Override
            public String toString() {
                return "Entropy";
            }
        },
        DISCERNABILITY {
            @Override
            public String toString() {
                return "Discernability";
            }
        },
        AECS {
            @Override
            public String toString() {
                return "AECS";
            }
        },
        PRECISION {
            @Override
            public String toString() {
                return "Precision";
            }
        },
        HEIGHT {
            @Override
            public String toString() {
                return "Height";
            }
        },
    }

    /**
     * Returns a configuration for the ARX framework
     * @param dataset
     * @param criteria
     * @return
     * @throws IOException
     */
    public static ARXConfiguration getConfiguration(BenchmarkDataset dataset, double suppFactor,  BenchmarkMetric metric, BenchmarkCriterion... criteria) throws IOException {
        
        ARXConfiguration config = ARXConfiguration.create();
        
        switch (metric) {
        case ENTROPY:
            config.setMetric(Metric.createEntropyMetric(true));
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
        
        for (BenchmarkCriterion c : criteria) {
            switch (c) {
            case D_PRESENCE:
                config.addCriterion(new DPresence(0.05d, 0.15d, dataset.getResearchSubset()));
                break;
            case INCLUSION:
                config.addCriterion(new Inclusion(dataset.getResearchSubset()));
                break;
            case K_ANONYMITY:
                config.addCriterion(new KAnonymity(5));
                break;
            case L_DIVERSITY_RECURSIVE:
                String sensitive = dataset.getSensitiveAttribute();
                config.addCriterion(new RecursiveCLDiversity(sensitive, 4, 3));
                break;
            case T_CLOSENESS:
                sensitive = dataset.getSensitiveAttribute();
                config.addCriterion(new HierarchicalDistanceTCloseness(sensitive, 0.2d, dataset.loadHierarchy(sensitive)));
                break;
            default:
                throw new RuntimeException("Invalid criterion");
            }
        }
        return config;
    }
}
