package org.deidentifier.arx;

import java.io.IOException;
import java.nio.charset.Charset;
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

        private final double minAecs; private final double maxAecs;
        private final double minDisc; private final double maxDisc;
        private final double minLoss; private final double maxLoss;
        private final double minEntr; private final double maxEntr;
        private final double minPrec; private final double maxPrec;
        
        public void cleanUp () {
        	inputHandle.release();
        	inputArray = null;
        	outputArray = null;
        }


        /**
         * @param datafile
         * @param criteria
         * @param sensitiveAttribute
         * @param customQiCount
         */
        public BenchmarkDataset(BenchmarkDatafile datafile, BenchmarkCriterion[] criteria, String sensitiveAttribute) {
            this.datafile = datafile;
            this.sensitiveAttribute = sensitiveAttribute;
            this.criteria = criteria;
            
            this.arxData = toArxData(criteria);
            this.inputHandle = arxData.getHandle();
            this.inputDataDef = inputHandle.getDefinition();
            

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
            this.minAecs = new UtilityMeasureAECS().evaluate(inputArray).getUtility();
            this.minDisc = new UtilityMeasureDiscernibility().evaluate(inputArray).getUtility();
            this.minLoss = new UtilityMeasureLoss<Double>(header, hierarchies, AggregateFunction.GEOMETRIC_MEAN).evaluate(inputArray).getUtility();
            this.minEntr = new UtilityMeasureNonUniformEntropy<Double>(header, inputArray).evaluate(inputArray).getUtility();
            this.minPrec = new UtilityMeasurePrecision<Double>(header, hierarchies).evaluate(inputArray).getUtility();

            // Compute for output
            this.maxAecs = new UtilityMeasureAECS().evaluate(outputArray).getUtility();
            this.maxDisc = new UtilityMeasureDiscernibility().evaluate(outputArray).getUtility();
            this.maxLoss = new UtilityMeasureLoss<Double>(header, hierarchies, AggregateFunction.GEOMETRIC_MEAN).evaluate(outputArray).getUtility();
            this.maxEntr = new UtilityMeasureNonUniformEntropy<Double>(header, inputArray).evaluate(outputArray).getUtility();
            this.maxPrec = new UtilityMeasurePrecision<Double>(header, hierarchies).evaluate(outputArray).getUtility();

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
                return new String[] { "Marital status", "Highest level of school completed" };
            case IHIS:
                return new String[] { "MARSTAT", "EDUC" };
            case ACS13:
                return new String[] { "Marital status", "Grade level" };
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
                if (criteria != null) {
                    for (BenchmarkCriterion c : criteria) {
                        switch (c) {
                        case L_DIVERSITY_DISTINCT:
                        case L_DIVERSITY_ENTROPY:
                        case L_DIVERSITY_RECURSIVE:
                        case T_CLOSENESS_HD:
                        case T_CLOSENESS_ED:
                        case D_DISCLOSURE_PRIVACY:
                            String sensitive = getSensitiveAttribute();
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
                return new String[] { "Age", "Sex", "Race" };
            case IHIS:
                return new String[] { "AGE", "SEX", "RACEA" };
            case ACS13:
            	return new String[] { "Age", "Sex", "Workclass" };
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
            ADULT ("adult"){
                @Override
                public String toString() {
                    return "Adult";
                }
            },
            CUP ("cup") {
                @Override
                public String toString() {
                    return "Cup";
                }
            },
            FARS ("fars"){
                @Override
                public String toString() {
                    return "Fars";
                }
            },
            ATUS ("atus"){
                @Override
                public String toString() {
                    return "Atus";
                }
            },
            IHIS ("ihis"){
                @Override
                public String toString() {
                    return "Ihis";
                }
            },
            ACS13 ("ss13acs"){
                @Override
                public String toString() {
                    return "ACS13";
                }
            },
            DUMMY ("dummy"){
                @Override
                public String toString() {
                    return "DUMMY";
                }
            };
            
            private String baseStringForFilename = null;
            
            BenchmarkDatafile (String baseStringForFilename) {
                this.baseStringForFilename = baseStringForFilename;
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