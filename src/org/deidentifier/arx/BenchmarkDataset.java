package org.deidentifier.arx;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.utility.AggregateFunction;
import org.deidentifier.arx.utility.DataConverter;
import org.deidentifier.arx.utility.UtilityMeasureAECS;
import org.deidentifier.arx.utility.UtilityMeasureDiscernibility;
import org.deidentifier.arx.utility.UtilityMeasureNonUniformEntropy;
import org.deidentifier.arx.utility.UtilityMeasurePrecision;
import org.deidentifier.arx.utility.UtilityMeasureSoriaComas;
import org.deidentifier.arx.utility.UtilityMeasureLoss;
    

    /**
     * this class encapsulates the configuration of a dataset the location 
     * and loading of its
     * its data- and hierarchy-files from the filesystem as well as the number of QIs
     * actually used for the dataest
     * @author helmut spengler
     *
     */
    /**
     * @author spengler
     *
     */
    public class BenchmarkDataset {
        private final BenchmarkDatafile datafile;
        private final String sensitiveAttribute;
        private final BenchmarkCriterion[] criteria;
        
        private final Data arxData;
        private final DataHandle inputHandle;
        private final DataDefinition inputDataDef;
        
        private String[][] inputArray;
        private String[][] outputArray;

        private final double minAecs;         private final double maxAecs;
        private final double minDisc;         private final double maxDisc;
        private final double minLoss;         private final double maxLoss;
        private final double minEntr;         private final double maxEntr;
        private final double minPrec;         private final double maxPrec;
        private final double minSoriaComas;   private final double maxSoriaComas;
        
        private final boolean isNumCoded;
        
        public void cleanUp () {
        	inputHandle.release();
        	inputArray = null;
        	outputArray = null;
        }


        /**
         * @param datafile
         * @param criteria
         * @param sensitiveAttribute
         * @param isNumCoded TODO
         * @param customQiCount
         */
        public BenchmarkDataset(BenchmarkDatafile datafile, BenchmarkCriterion[] criteria, String sensitiveAttribute) {
            this.datafile = datafile;
            this.sensitiveAttribute = sensitiveAttribute;
            this.criteria = criteria;
            
            this.arxData = toArxData(criteria);
            this.inputHandle = arxData.getHandle();
            this.inputDataDef = inputHandle.getDefinition();
            
            this.isNumCoded = datafile.isNumCoded();
            

            DataConverter converter = new DataConverter();            
            this.inputArray = converter.toArray(inputHandle, inputDataDef);
            
            this.outputArray = new String[this.inputArray.length][getQuasiIdentifyingAttributes().length];
            for (int i = 0; i < this.inputArray.length; i++) {
                for (int j = 0; j < this.inputArray[0].length; j++) {
                	this.outputArray[i][j] = "*";
                }
            }
            Map<String, String[][]> hierarchies = converter.toMap(inputDataDef);
            String[] header                     = getQuasiIdentifyingAttributes();
            
            // Compute for input
            this.minAecs       = new UtilityMeasureAECS().evaluate(inputArray).getUtility();
            this.minDisc       = new UtilityMeasureDiscernibility().evaluate(inputArray).getUtility();
            this.minLoss       = new UtilityMeasureLoss<Double>(header, hierarchies, AggregateFunction.GEOMETRIC_MEAN).evaluate(inputArray).getUtility();
            this.minEntr       = new UtilityMeasureNonUniformEntropy<Double>(header, inputArray).evaluate(inputArray).getUtility();
            this.minPrec       = new UtilityMeasurePrecision<Double>(header, hierarchies).evaluate(inputArray).getUtility();
            if (isNumCoded) {
            	this.minSoriaComas = new UtilityMeasureSoriaComas(inputArray).evaluate(inputArray).getUtility();
            } else {
            	this.minSoriaComas = Double.NaN;
            }

            // Compute for output
            this.maxAecs         = new UtilityMeasureAECS().evaluate(outputArray).getUtility();
            this.maxDisc         = new UtilityMeasureDiscernibility().evaluate(outputArray).getUtility();
            this.maxLoss         = new UtilityMeasureLoss<Double>(header, hierarchies, AggregateFunction.GEOMETRIC_MEAN).evaluate(outputArray).getUtility();
            this.maxEntr         = new UtilityMeasureNonUniformEntropy<Double>(header, inputArray).evaluate(outputArray).getUtility();
            this.maxPrec         = new UtilityMeasurePrecision<Double>(header, hierarchies).evaluate(outputArray).getUtility();
            if (isNumCoded) {
            	this.maxSoriaComas   = new UtilityMeasureSoriaComas(inputArray).evaluate(outputArray).getUtility();
            } else {
            	this.maxSoriaComas = Double.NaN;
            }

//            String inFormat =  "%13.2f";
//            String outFormat = "%16.2f";
//            System.out.println();
//            System.out.println(datafile + " " + Arrays.toString(header) + ", SA=" + sensitiveAttribute);
//            System.out.println("  AECS: min = " + String.format(inFormat, minAecs) + " / max = " + String.format(outFormat, maxAecs));
//            System.out.println("  Disc: min = " + String.format(inFormat, minDisc) + " / max = " + String.format(outFormat, maxDisc));
//            System.out.println("  Loss: min = " + String.format(inFormat, minLoss) + " / max = " + String.format(outFormat, maxLoss));
//            System.out.println("  Entr: min = " + String.format(inFormat, minEntr) + " / max = " + String.format(outFormat, maxEntr));
//            System.out.println("  Prec: min = " + String.format(inFormat, minPrec) + " / max = " + String.format(outFormat, maxPrec));
//            System.out.println();
        }


        /**
         * @param datafile
         * @param customQiCount
         * @param criteria
         */
        public BenchmarkDataset(BenchmarkDatafile datafile, BenchmarkCriterion[] criteria) {
        	this(datafile, criteria, getDefaultSensitiveAttribute(datafile));
        }

        public BenchmarkDatafile getDatafile() {
            return datafile;
        }
        
        public Data getArxData() {
        	return arxData;
        }
        
        public BenchmarkCriterion[] getCriteria() {
        	return criteria;
        }
        
        public String [] getQuasiIdentifyingAttributes() {
        	Set<String> qis = this.inputDataDef.getQuasiIdentifyingAttributes();
        	return qis.toArray(new String[qis.size()]);
        }
        
        /**
         * @param attribute
         * @return
         */
        public Hierarchy getHierarchy(String attribute) {
        	return loadHierarchy(attribute);
        }
        
        /**
         * @return
         */
        public String[][] getInputArray() {
			return inputArray;
		}

		/**
		 * @return
		 */
		public DataDefinition getInputDataDef() {
			return inputDataDef;
		}

		/**
         * @param measure
         * @return
         */
        public double getMinInfoLoss(BenchmarkSetup.BenchmarkMeasure measure) {
        	switch(measure) {
			case AECS:
				return this.minAecs;
			case DISCERNABILITY:
				return this.minDisc;
			case ENTROPY:
				return this.minEntr;
			case LOSS:
				return this.minLoss;
			case PRECISION:
				return this.minPrec;
			case SORIA_COMAS:
				return this.minSoriaComas;
			default:
				throw new RuntimeException("Invalid measure");
        	}
        }
        
        /**
         * @param measure
         * @return
         */
        public double getMaxInfoLoss(BenchmarkSetup.BenchmarkMeasure measure) {
        	switch(measure) {
			case AECS:
				return this.maxAecs;
			case DISCERNABILITY:
				return this.maxDisc;
			case ENTROPY:
				return this.maxEntr;
			case LOSS:
				return this.maxLoss;
			case PRECISION:
				return this.maxPrec;
			case SORIA_COMAS:
				return this.maxSoriaComas;
			default:
				throw new RuntimeException("Invalid measure");
        	}
        }
        
        public DataHandle getHandle() throws IOException {
            return inputHandle;
        }

 
        /** Returns the research subset for the dataset
         * @param ssNum
         * @param customQiCount
         * @return
         * @throws IOException
         */
        public DataSubset getResearchSubset(Integer ssNum) throws IOException {
        	String filename;
        	String baseName = getDatafile().baseStringForFilename;
        	if (ssNum == null) {
        		filename = "data/" + baseName + "_subset.csv";        		
        	} else {
        		filename = "data/subsets_" + baseName + "/" + baseName + "_subset_" + ssNum + ".csv";       
        	}
        	return DataSubset.create(this.toArxData(null), Data.create(filename, Charset.forName("UTF-8"), ';'));
        }

        /**
         * Returns the sensitive attribute for the dataset
         * @param dataset
         * @return
         */
        public String getInSensitiveAttribute() {
        	String[] saCandidatesArr = getSensitiveAttributeCandidates(getDatafile());
        	List<String> saCandidatesList = new ArrayList<>(Arrays.asList(saCandidatesArr));
        	
        	for (int i = 0; i < saCandidatesList.size(); i++) {
        		if (saCandidatesList.get(i).equals(getSensitiveAttribute())){
        			saCandidatesList.remove(i);
        		}
        	}
        	
        	if (saCandidatesList.size() != 1) {
        		throw new RuntimeException("This should not happen");
        	} else {
        		return saCandidatesList.get(0);
        	}
        }
        /**
         * Returns the sensitive attribute for the dataset
         * @param dataset
         * @return
         */
        public String getSensitiveAttribute() {
            return sensitiveAttribute;
        }

        /**
         * Returns the sensitive attribute for the dataset
         * @param dataset
         * @return
         */
        public static String[] getSensitiveAttributeCandidates(BenchmarkDatafile datafile) {

            switch (datafile) {
            case ATUS:
            case ATUS_NUM:
                return new String[] { "Marital status", "Highest level of school completed" };
            case IHIS:
            case IHIS_NUM:
                return new String[] { "MARSTAT", "EDUC" };
            case ACS13:
            case ACS13_NUM:
                return new String[] { "Marital status", "Education" };
            default:
                throw new RuntimeException("Invalid dataset");
            }
        }
        
        @Override
        public String toString() {
            return datafile.toString();
        }

        /**
         * Returns the generalization hierarchy for the dataset and attribute
         * @param dataset
         * @param attribute
         * @return
         * @throws IOException
         */
        Hierarchy loadHierarchy(String attribute) {
        	String path = "hierarchies/" + datafile.getBaseStringForFilename() + "_hierarchy_" + attribute + ".csv";
        	try {
        		return Hierarchy.create(path, Charset.forName("UTF-8"), ';');
        	} catch (IOException e) {
        		System.err.println("Unable to load hierarchy from file " + path);
        		return null;
        	}
        }
        

        /**
         * Configures and returns the dataset as <code>org.deidentifier.arx.Data</code>
         * @param criteria
         * @param customQiCount
         * 
         * @return
         */
        @SuppressWarnings("incomplete-switch")
		private Data toArxData(BenchmarkCriterion[] criteria) {
        	Data arxData;

            	String path = "data/" + datafile.getBaseStringForFilename() + ".csv";
                try {
					arxData = Data.create(path, Charset.forName("UTF-8"), ';');
				} catch (IOException e) {
					arxData = null;
					System.err.println("Unable to load dataset from file " + path);
				}
                for (String qi : getQuasiIdentifyingAttributesPrivate()) {
                    arxData.getDefinition().setAttributeType(qi, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
                    arxData.getDefinition().setHierarchy(qi, loadHierarchy(qi));
                }
                String sensitive = getSensitiveAttribute();
                arxData.getDefinition().setAttributeType(sensitive, AttributeType.INSENSITIVE_ATTRIBUTE);
                if (criteria != null) {
                    for (BenchmarkCriterion c : criteria) {
                        switch (c) {
                        case L_DIVERSITY_DISTINCT:
                        case L_DIVERSITY_ENTROPY:
                        case L_DIVERSITY_RECURSIVE:
                        case T_CLOSENESS_HD:
                        case T_CLOSENESS_ED:
                        case D_DISCLOSURE_PRIVACY:
                        case BASIC_BETA_LIKENESS:
                            arxData.getDefinition().setAttributeType(sensitive, AttributeType.SENSITIVE_ATTRIBUTE);
                            break;
                        }
                    }
                }           
            
            return arxData;
        }


        private  String[] getQuasiIdentifyingAttributesPrivate() {
        	return getQuasiIdentifyingAttributes(datafile);
        }
        

        public static String[] getQuasiIdentifyingAttributes(BenchmarkDatafile _datafile) {
            switch (_datafile) {
            case ATUS:
            case ATUS_NUM:
                return new String[] { "Age", "Sex", "Race" };
            case IHIS:
            case IHIS_NUM:
                return new String[] { "AGE", "SEX", "RACEA" };
            case ACS13:
            case ACS13_NUM:
            	return new String[] { "Age", "Sex", "Race" };
            default:
                throw new RuntimeException("Invalid dataset: " + _datafile);
            }
        }

        /**
         * Returns the default sensitive attribute for the dataset
         * @param dataset
         * @return
         */
        private static String getDefaultSensitiveAttribute(BenchmarkDatafile datafile) {
            switch (datafile) {
            case ADULT:
                return "occupation";
            case ATUS:
                return "Highest level of school completed";
            case CUP:
                return "RAMNTALL";
            case FARS:
                return "istatenum";
            case IHIS:
                return "EDUC";
            case ACS13:
                return "SCHG";
            default:
                throw new RuntimeException("Invalid dataset");
            }
        }

   


        public static enum BenchmarkDatafile {
            ADULT ("adult", false){
                @Override
                public String toString() {
                    return "Adult";
                }
            },
            CUP ("cup", false) {
                @Override
                public String toString() {
                    return "Cup";
                }
            },
            FARS ("fars", false){
                @Override
                public String toString() {
                    return "Fars";
                }
            },
            ATUS ("atus", false){
                @Override
                public String toString() {
                    return "Atus";
                }
            },
            ATUS_NUM ("atus_numcoded", true){
                @Override
                public String toString() {
                    return "Atus numcoded";
                }
            },
            IHIS ("ihis", false){
                @Override
                public String toString() {
                    return "Ihis";
                }
            },
            IHIS_NUM ("ihis_numcoded", true){
                @Override
                public String toString() {
                    return "Ihis numcoded";
                }
            },
            ACS13 ("ss13acs", false){
                @Override
                public String toString() {
                    return "ACS13";
                }
            },
            ACS13_NUM ("ss13acs_numcoded", true){
                @Override
                public String toString() {
                    return "ACS13 numcoded";
                }
            },
            DUMMY ("dummy", false){
                @Override
                public String toString() {
                    return "DUMMY";
                }
            };
            
            private String baseStringForFilename = null;
            private boolean isNumCoded;
            
            BenchmarkDatafile (String baseStringForFilename, boolean isNumCoded) {
            	this.isNumCoded = isNumCoded;
                this.baseStringForFilename = baseStringForFilename;
            }
            
            public boolean isNumCoded() {
				return isNumCoded;
			}

			/**
             * @return the string, that will be used for finding and loading the
             * datafile with its hierarchies from the filesystem
             */
            public String getBaseStringForFilename() {
                return baseStringForFilename;
            }
        }
}