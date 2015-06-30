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

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.buffered.BufferedArithmeticMeanAnalyzer;

/**
 * This class encapsulates most of the parameters of a benchmark run
 * @author Fabian Prasser
 */
public class BenchmarkSetup {

	/** The benchmark instance */
	public static final Benchmark BENCHMARK    = new Benchmark(new String[] {
			COLUMNS.UTLITY_MEASURE.toString(),
			COLUMNS.SUPPRESSION_FACTOR.toString(),
			COLUMNS.DATASET.toString(),
			COLUMNS.CRITERIA.toString(),
			COLUMNS.SUBSET_NATURE.toString(),
			COLUMNS.PARAM_K.toString(),
			COLUMNS.PARAM_L.toString(),
			COLUMNS.PARAM_C.toString(),
			COLUMNS.PARAM_T.toString(),
			COLUMNS.PARAM_DMIN.toString(),
			COLUMNS.PARAM_DMAX.toString(),
			COLUMNS.SENS_ATTR.toString(),
			COLUMNS.QI_SET.toString(),
			COLUMNS.SS_NUM.toString(),
	});

    public static final int INFO_LOSS            = BENCHMARK.addMeasure(COLUMNS.UTILITY_VALUE.toString());
    public static final int NUM_VALUES           = BENCHMARK.addMeasure(COLUMNS.NUM_VALUES.toString());
    public static final int SKEWNESS             = BENCHMARK.addMeasure(COLUMNS.SKEWNESS.toString());
    public static final int KUROTSIS             = BENCHMARK.addMeasure(COLUMNS.KUROTSIS.toString());
    public static final int FREQ_DEVI            = BENCHMARK.addMeasure(COLUMNS.FREQ_DEVI.toString());  // standard deviation of the frequencies
    public static final int STAND_DEVIATION      = BENCHMARK.addMeasure(COLUMNS.STAND_DEVI.toString());
    public static final int VARIATION_COEFF      = BENCHMARK.addMeasure(COLUMNS.VARI_COEFF.toString());
    public static final int NORMALIZED_DEVIATION = BENCHMARK.addMeasure(COLUMNS.DEVI_NORM.toString());
    public static final int QUARTIL_COEFF        = BENCHMARK.addMeasure(COLUMNS.QUARTI_COEFF.toString());

	static {
        BENCHMARK.addAnalyzer(INFO_LOSS, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(NUM_VALUES, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(SKEWNESS, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(KUROTSIS, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(STAND_DEVIATION, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(VARIATION_COEFF, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(NORMALIZED_DEVIATION, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(FREQ_DEVI, new BufferedArithmeticMeanAnalyzer(1));
        BENCHMARK.addAnalyzer(QUARTIL_COEFF, new BufferedArithmeticMeanAnalyzer(1));
	}

    public static final String RESULTS_DIR = "results";
    public static final String RESULTS_FILE_STEM = "results";
    public static final String RESULTS_FILE= RESULTS_DIR + "/" + RESULTS_FILE_STEM + ".csv";
    public static final String SUMMARY_FILE_STEM="results_summary";
    public static final double NO_RESULT_FOUND_DOUBLE_VAL=Double.POSITIVE_INFINITY;
    public static final String NO_RESULT_FOUND_STRING_VAL="n.s.f.";
    
    /**
     * Returns all metrics
     * @return
     */
    public static BenchmarkMeasure[] getMeasures() {        
        return new BenchmarkMeasure[] {
        		BenchmarkMeasure.LOSS,
        		BenchmarkMeasure.AECS,
//        		BenchmarkMeasure.ENTROPY,
//        		BenchmarkMeasure.DISCERNABILITY,
        		};
    }
    
    /**
     * Returns all suppression factors
     * @return
     */
    public static double[] getSuppressionFactors() {        
        return new double[] { 0d, 0.05d, 0.1d, 0.5d, 1d };
//      return new double[] { 0.1, 1d };
//      return new double[] { 0.1 };
    }

    /**
     * Returns all datasets
     * @return
     */
    public static BenchmarkDataset[] getDatasets() {
        
        BenchmarkDataset[] datasetArr = new BenchmarkDataset[getDatafiles().length];
        for (int i = 0; i < getDatafiles().length; i++) {
            
            BenchmarkDatafile datafile = getDatafiles()[i];
            
            Integer numQis = null;
            if (BenchmarkDatafile.ACS13.equals(datafile)) numQis = 8;
            
            datasetArr[i] = new BenchmarkDataset(datafile, numQis);
        }
        
        return datasetArr;
    }

    /**
     * Returns all datasets
     * @return
     */
    public static BenchmarkDatafile[] getDatafiles() {
        return new BenchmarkDatafile[] {
         BenchmarkDatafile.ACS13,
         BenchmarkDatafile.ADULT,
         BenchmarkDatafile.CUP,
         BenchmarkDatafile.FARS,
         BenchmarkDatafile.ATUS,
         BenchmarkDatafile.IHIS,
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
    
    public static int[] get_k_values() {
        return new int[] {
                          2, 3, 4, 5, 6, 7, 8, 9, 10,
                          15, 20, 25, 30, 35, 45, 50,
                          60, 70, 80, 90, 100
        };
    }
    
    public static double[][] get_d_values() {
        return new double[][] {
        		new double[] {0d,   0.05},
        		new double[] {0d,   0.07},
        		new double[] {0d,   0.1},
        		new double[] {0d,   0.2},
        		new double[] {0d,   0.5},
        		new double[] {0d,   0.7},
        		new double[] {0.05, 0.2},
        		new double[] {0.05, 0.7},
        		new double[] {0.1,  0.2},
        		new double[] {0.1,  0.7},
        };
    }
    
    public static enum COLUMNS {
        UTLITY_MEASURE {
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
        PARAM_K {
            @Override
            public String toString() {
                return "k";
            }
        },
        PARAM_L {
            @Override
            public String toString() {
                return "l";
            }
        },
        PARAM_C {
            @Override
            public String toString() {
                return "c";
            }
        },
        PARAM_T {
            @Override
            public String toString() {
                return "t";
            }
        },
        PARAM_DMIN {
            @Override
            public String toString() {
                return "dMin";
            }
        },
        PARAM_DMAX {
            @Override
            public String toString() {
                return "dMax";
            }
        },
        SENS_ATTR {
            @Override
            public String toString() {
                return "Sens. Attr.";
            }
        },
        QI_SET {
            @Override
            public String toString() {
                return "QIs";
            }
        },
        SS_NUM {
            @Override
            public String toString() {
                return "Subset-Nr";
            }
        },
        LATTICE_SIZE {
            @Override
            public String toString() {
                return "Lattice-Size";
            }
        },
        NUM_VALUES {
            @Override
            public String toString() {
                return "Number of distinct SA values";
            }
        },
        VARIANCE {
            @Override
            public String toString() {
                return "Variance";
            }
        },
        SKEWNESS {
            @Override
            public String toString() {
                return "Skewness";
            }
        },
        KUROTSIS {
            @Override
            public String toString() {
                return "Kurtosis";
            }
        },
        FREQ_DEVI {
            @Override
            public String toString() {
                return "Standard deviation of frequencies";
            }
        },
        STAND_DEVI {
            @Override
            public String toString() {
                return "Standard deviation";
            }
        },
        DEVI_REL {
            @Override
            public String toString() {
                return "Relative deviation";
            }
        },
        DEVI_NORM {
            @Override
            public String toString() {
                return "Normalized standard deviation";
            }
        },
        VARI_COEFF {
            @Override
            public String toString() {
                return "Coefficient of variation";
            }
        },
        QUARTI_COEFF {
            @Override
            public String toString() {
                return "Quartile coefficient of dispersion";
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
    
    public static enum BenchmarkMeasure {
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
}
