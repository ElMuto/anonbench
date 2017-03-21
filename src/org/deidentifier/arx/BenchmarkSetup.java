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

import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;

import de.linearbits.subframe.Benchmark;
import de.linearbits.subframe.analyzer.ValueBuffer;

/**
 * This class encapsulates most of the parameters of a benchmark run
 * @author Fabian Prasser
 */
public class BenchmarkSetup {

	/** The benchmark instance - datapoints */
	public static final Benchmark BENCHMARK    = new Benchmark(new String[] {
			COLUMNS.PRIVACY_MODEL.toString(),
			COLUMNS.IL_MEASURE.toString(),
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
            COLUMNS.NUM_QIS.toString(),
			COLUMNS.SS_NUM.toString(),
			COLUMNS.PA_SE_SE.toString(),
			COLUMNS.PA_QI_SE.toString(),
			COLUMNS.EXP_TYPE.toString(),
	});

	/* measures */
    public static final int INFO_LOSS_ARX           = BENCHMARK.addMeasure(COLUMNS.IL_ARX_VALUE.toString());
    public static final int INFO_LOSS_ABS           = BENCHMARK.addMeasure(COLUMNS.IL_ABS_VALUE.toString());
    public static final int INFO_LOSS_REL           = BENCHMARK.addMeasure(COLUMNS.IL_REL_VALUE.toString());
    public static final int INFO_LOSS_MIN           = BENCHMARK.addMeasure(COLUMNS.IL_MIN.toString());
    public static final int INFO_LOSS_MAX           = BENCHMARK.addMeasure(COLUMNS.IL_MAX.toString());
    public static final int DIFFICULTY              = BENCHMARK.addMeasure(COLUMNS.DIFFICULTY.toString());
    public static final int NUM_VALUES              = BENCHMARK.addMeasure(COLUMNS.NUM_VALUES.toString());
    public static final int SKEWNESS                = BENCHMARK.addMeasure(COLUMNS.SKEWNESS.toString());
    public static final int KUROTSIS                = BENCHMARK.addMeasure(COLUMNS.KUROTSIS.toString());
    public static final int FREQ_DEVI               = BENCHMARK.addMeasure(COLUMNS.FREQ_DEVI.toString());  // standard deviation of the frequencies
    public static final int FREQ_SPAN               = BENCHMARK.addMeasure(COLUMNS.FREQ_SPAN.toString());  // span of the frequencies
    public static final int STAND_DEVIATION         = BENCHMARK.addMeasure(COLUMNS.STAND_DEVI.toString());
    public static final int VARIATION_COEFF         = BENCHMARK.addMeasure(COLUMNS.VARI_COEFF.toString());
    public static final int QUARTIL_COEFF           = BENCHMARK.addMeasure(COLUMNS.QUARTI_COEFF.toString());
    public static final int ENTROPY                 = BENCHMARK.addMeasure(COLUMNS.NORM_ENTROPY.toString());
    public static final int EFD_SCORE               = BENCHMARK.addMeasure(COLUMNS.EFD_SCORE.toString());

	static {
        BENCHMARK.addAnalyzer(INFO_LOSS_ARX, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFO_LOSS_ABS, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFO_LOSS_REL, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFO_LOSS_MIN, new ValueBuffer());
        BENCHMARK.addAnalyzer(INFO_LOSS_MAX, new ValueBuffer());
        BENCHMARK.addAnalyzer(DIFFICULTY, new ValueBuffer());
        BENCHMARK.addAnalyzer(NUM_VALUES, new ValueBuffer());
        BENCHMARK.addAnalyzer(SKEWNESS, new ValueBuffer());
        BENCHMARK.addAnalyzer(KUROTSIS, new ValueBuffer());
        BENCHMARK.addAnalyzer(STAND_DEVIATION, new ValueBuffer());
        BENCHMARK.addAnalyzer(VARIATION_COEFF, new ValueBuffer());
        BENCHMARK.addAnalyzer(FREQ_DEVI, new ValueBuffer());
        BENCHMARK.addAnalyzer(FREQ_SPAN, new ValueBuffer());
        BENCHMARK.addAnalyzer(QUARTIL_COEFF, new ValueBuffer());
        BENCHMARK.addAnalyzer(ENTROPY, new ValueBuffer());
        BENCHMARK.addAnalyzer(EFD_SCORE, new ValueBuffer());
	}

    public static final String RESULTS_DIR = "results";
    public static final String RESULTS_FILE_STEM = "results";
    public static final String RESULTS_FILE= RESULTS_DIR + "/" + RESULTS_FILE_STEM + ".csv";
    public static final String SUMMARY_FILE_STEM="results_summary";
    public static final double NO_RESULT_FOUND_DOUBLE_VAL=Double.POSITIVE_INFINITY;
    public static final String NO_RESULT_FOUND_STRING_VAL="n.s.f.";
	
    public static PrivacyModel[] getPrivacyModelsCombinedWithK() {
    	return new PrivacyModel[] {
    			new PrivacyModel(BenchmarkCriterion.L_DIVERSITY_DISTINCT,  5, null, 3,    null, null),
    			new PrivacyModel(BenchmarkCriterion.L_DIVERSITY_RECURSIVE, 5, 4.0d, 3,    null, null),
    			new PrivacyModel(BenchmarkCriterion.L_DIVERSITY_ENTROPY,   5, null, 3,    null, null),
    			new PrivacyModel(BenchmarkCriterion.T_CLOSENESS_ED,        5, null, null, 0.2d, null),
    			new PrivacyModel(BenchmarkCriterion.D_DISCLOSURE_PRIVACY,  5, null, null, null, 1d),
    	};
    }
	
    public static PrivacyModel[] getPrivacyModelsConfigsFor_2D_Comparison(String dim2Qualifier) {
    	
		Integer[] dim1Vals =     { /*1, 2, 3, 4,*/ 5 };
		Double [] dim2ValsForL = { 1d, 2d, 3d, 4d, 5d, 6d, 8d, 10d, 15d, 20d, 25d, 30d };
		Double [] dim2ValsForT = { 1d, 0.75, 0.5, 0.25, 0d };
		Double [] dim2ValsForD = { 6d, 5d, 4d, 3d, 2d, 1d, 0d };
		
		Double[] dim2Vals = null;

		if ("t".equals(dim2Qualifier)) {
			dim2Vals = dim2ValsForT;
		} else if ("ld".equals(dim2Qualifier) || "lr".equals(dim2Qualifier) || "le".equals(dim2Qualifier)) {
			dim2Vals = dim2ValsForL;
		} else if ("d".equals(dim2Qualifier)) {
			dim2Vals = dim2ValsForD;
		} else {
			throw new RuntimeException("Invalid dim2Qalifier: '" + dim2Qualifier + "'");
		}
		
		PrivacyModel[] pmArr = new PrivacyModel[dim1Vals.length * dim2Vals.length];

		for (int ki = 0; ki < dim1Vals.length; ki++) {
			for (int ti = 0; ti < dim2Vals.length; ti++) {
				pmArr[ki * dim2Vals.length + ti] = new PrivacyModel(dim2Qualifier, dim1Vals[ki], dim2Vals[ti]);
			}
		}
    	
    	return pmArr;
    }
    
    public static PrivacyModel[] getDifficultyRelevantPrivacyModels() {
    	List<PrivacyModel> _saBasedModelsList = new ArrayList<>();
    	for (PrivacyModel privacyModel : getPrivacyModelsCombinedWithK()) {
    		if (privacyModel.isSaBased()) _saBasedModelsList.add(privacyModel);
    	}    	
    	return _saBasedModelsList.toArray(new PrivacyModel[_saBasedModelsList.size()]);
    }
    
    public static PrivacyModel[] getNonSaBasedPrivacyModels() {
    	List<PrivacyModel> _saBasedModelsList = new ArrayList<>();
    	for (PrivacyModel privacyModel : getPrivacyModelsCombinedWithK()) {
    		if (!privacyModel.isSaBased()) _saBasedModelsList.add(privacyModel);
    	}    	
    	return _saBasedModelsList.toArray(new PrivacyModel[_saBasedModelsList.size()]);
    }
    
    /**
     * Returns all suppression factors
     * @return
     */
    public static double[] getSuppressionFactors() {        
        return new double[] { 0.05d };
    }

    /**
     * Returns all datasets
     * @return
     */
    public static BenchmarkDataset[] getDatasets(BenchmarkCriterion[] criteria) {
        
        BenchmarkDataset[] datasetArr = new BenchmarkDataset[getDatafiles().length];
        for (int i = 0; i < getDatafiles().length; i++) {
            datasetArr[i] = new BenchmarkDataset(
                                    getDatafiles()[i],
                                    criteria
                                );
        }
        
        return datasetArr;
    }
    
    public static String[] getAllAttributes(BenchmarkDatafile datafile) {
    	switch (datafile) {
		case ACS13:
			return new String[] {
					"Age",
                    "Sex",
                    "Weight",
					"Income",
                    "Marital status",
                    "Education",
                    "Citizenship",
                    "Workclass"
                    };
		case ADULT:
			return new String[] {    
					"age",
		            "marital-status",
		            "race",
		            "sex",
		            "education",
		            "native-country",
		            "salary-class",
		            "workclass",
		            "occupation"};
		case ATUS:
			return new String[] {   
					"Age",
                    "Race",
                    "Marital status",
                    "Sex",
                    "Birthplace",
                    "Citizenship status",
                    "Labor force status",
                    "Region",
                    "Highest level of school completed"};
		case CUP:
			return new String[] {  
					"AGE",
                    "GENDER",
                    "STATE",
                    "ZIP",
                    "INCOME",
                    "MINRAMNT",
                    "NGIFTALL",
                    "RAMNTALL" };
		case FARS:
			return new String[] {
					"iage",
                    "ihispanic",
                    "irace",
                    "isex",
                    "ideathday",
                    "ideathmon",
                    "iinjury",
                    "istatenum"};
		case IHIS:
			return new String[] {
					"AGE",
                    "RACEA",
                    "MARSTAT",
                    "SEX",
                    "PERNUM",
                    "QUARTER",
                    "YEAR",
                    "EDUC",
                    "REGION"};
		default:
			throw new RuntimeException("invalid datafile: " + datafile);
    	
    	}
    }

    /**
     * Returns all datasets
     * @return
     */
    public static BenchmarkDatafile[] getDatafiles() {
        return new BenchmarkDatafile[] {
         BenchmarkDatafile.ACS13,
         BenchmarkDatafile.ATUS,
         BenchmarkDatafile.IHIS,
                                        };
    }

    /**
     * Returns all datasets
     * @return
     */
    public static BenchmarkDatafile[] getDatafilesSoriaComas() {
        return new BenchmarkDatafile[] {
                BenchmarkDatafile.ATUS_NUM,
                BenchmarkDatafile.IHIS_NUM,
                BenchmarkDatafile.ACS13_NUM,
                                        };
    }
    
    public static String getSuppressionConfigString (double d) {
    	return d == 0d ? "Generalization only" : "Generalization and suppression";
    }
    
    public static enum COLUMNS {
        IL_MEASURE {
            @Override
            public String toString() {
                return "Information-loss measure";
            }
        },
        DATASET {
            @Override
            public String toString() {
                return "Dataset";
            }
        },
        PRIVACY_MODEL {
            @Override
            public String toString() {
                return "Privacy Model";
            }
        },
        CRITERIA {
            @Override
            public String toString() {
                return "Criteria";
            }
        },
        IL_SORIA_COMAS {
            @Override
            public String toString() {
                return "Relative information-loss according to Soria-Comas";
            }
        },
        IL_ABS_VALUE {
            @Override
            public String toString() {
                return "Information-loss absolut value from utility framework";
            }
        },
        IL_REL_VALUE {
            @Override
            public String toString() {
                return "Information-loss relative value from utility framework";
            }
        },
        IL_MIN {
            @Override
            public String toString() {
                return "Minimum information loss for dataset";
            }
        },
        IL_MAX {
            @Override
            public String toString() {
                return "Maximum information loss for dataset";
            }
        },
        IL_ARX_VALUE {
            @Override
            public String toString() {
                return "Information-loss value from ARX framework";
            }
        },
        DIFFICULTY {
            @Override
            public String toString() {
                return "Difficulty";
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
        NUM_QIS {
            @Override
            public String toString() {
                return "Number of QIs";
            }
        },
        SS_NUM {
            @Override
            public String toString() {
                return "Subset-Nr";
            }
        },
        PA_SE_SE {
            @Override
            public String toString() {
                return "PA(SE->SE)";
            }
        },
        PA_QI_SE {
            @Override
            public String toString() {
                return "PA(QI->SE)";
            }
        },
        EXP_TYPE {
            @Override
            public String toString() {
                return "Experiment-Type";
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
                return "Deviation of frequencies";
            }
        },
        FREQ_SPAN {
            @Override
            public String toString() {
                return "Span of frequencies";
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
        NORM_ENTROPY {
            @Override
            public String toString() {
                return "Normalized entropy";
            }
        },
        EFD_SCORE {
            @Override
            public String toString() {
                return "Product of Entropy and Standard dev of frequs";
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
                return "DL";
            }
        },
        L_DIVERSITY_ENTROPY {
            @Override
            public String toString() {
                return "RE";
            }
        },
        L_DIVERSITY_RECURSIVE {
            @Override
            public String toString() {
                return "RL";
            }
        },
        T_CLOSENESS_HD {
            @Override
            public String toString() {
                return "HT";
            }
        },
        T_CLOSENESS_ED {
            @Override
            public String toString() {
                return "ET";
            }
        },
        D_PRESENCE {
            @Override
            public String toString() {
                return "dpres";
            }
        },
        INCLUSION {
            @Override
            public String toString() {
                return "i";
            }
        },
        D_DISCLOSURE_PRIVACY {
            @Override
            public String toString() {
                return "DP";
            }
        }
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
        SORIA_COMAS {
            @Override
            public String toString() {
                return "Soria-Comas";
            }
        },
        OPT_WITH_LOSS_MEAS_SORIA_COMAS {
            @Override
            public String toString() {
                return "Optimize with Loss, Measure with Soria-Comas";
            }
        },
    }
}
