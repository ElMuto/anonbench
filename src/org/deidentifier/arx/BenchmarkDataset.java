package org.deidentifier.arx;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilderIntervalBased;
import org.deidentifier.arx.aggregates.HierarchyBuilder.Type;
import org.deidentifier.arx.utility.AggregateFunction;
import org.deidentifier.arx.utility.DataConverter;
import org.deidentifier.arx.utility.UtilityMeasureAECS;
import org.deidentifier.arx.utility.UtilityMeasureDiscernibility;
import org.deidentifier.arx.utility.UtilityMeasureLoss;
import org.deidentifier.arx.utility.UtilityMeasureNonUniformEntropy;
import org.deidentifier.arx.utility.UtilityMeasurePrecision;
    

    /**
     * this class encapsulates the configuration of a dataset the location 
     * and loading of its
     * its data- and hierarchy-files from the filesystem as well as the number of QIs
     * actually used for the dataest
     * @author helmut spengler
     *
     */
    public class BenchmarkDataset {
        private final BenchmarkDatafile datafile;
        private final Integer customQiCount;
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

        /**
         * @param datafile
         * @param customQiCount
         * @param criteria
         * @param sensitiveAttribute
         */
        public BenchmarkDataset(BenchmarkDatafile datafile, Integer customQiCount, BenchmarkCriterion[] criteria, String sensitiveAttribute) {
            this.datafile = datafile;
            this.customQiCount = customQiCount;
            this.sensitiveAttribute = sensitiveAttribute;
            this.criteria = criteria;
            
            this.arxData = toArxData(criteria);
            this.inputHandle = arxData.getHandle();
            this.inputDataDef = inputHandle.getDefinition();
            

            DataConverter converter = new DataConverter();            
            this.inputArray = converter.toArray(inputHandle, inputDataDef);
            
            this.outputArray = new String[this.inputArray.length][customQiCount != null ? customQiCount : getQuasiIdentifyingAttributes().length];
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

            String inFormat =  "%13.2f";
            String outFormat = "%16.2f";
            System.out.println();
            System.out.println(datafile + " " + Arrays.toString(header));
            System.out.println("  AECS: min = " + String.format(inFormat, minAecs) + " / max = " + String.format(outFormat, maxAecs));
            System.out.println("  Disc: min = " + String.format(inFormat, minDisc) + " / max = " + String.format(outFormat, maxDisc));
            System.out.println("  Loss: min = " + String.format(inFormat, minLoss) + " / max = " + String.format(outFormat, maxLoss));
            System.out.println("  Entr: min = " + String.format(inFormat, minEntr) + " / max = " + String.format(outFormat, maxEntr));
            System.out.println("  Prec: min = " + String.format(inFormat, minPrec) + " / max = " + String.format(outFormat, maxPrec));
            System.out.println();
        }


        /**
         * @param datafile
         * @param customQiCount
         * @param criteria
         */
        public BenchmarkDataset(BenchmarkDatafile datafile, Integer customQiCount, BenchmarkCriterion[] criteria) {
        	this(datafile, customQiCount, criteria, getDefaultSensitiveAttribute(datafile));
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

        private String[] _getQuasiIdentifyingAttributes() {
            switch (datafile) {
            case ADULT:
                return customizeQis ((new String[] {    "age",
                                                        "marital-status",
                                                        "race",
                                                        "sex",
                                                        "education",
                                                        "native-country",
                                                        "salary-class",
                                                        "workclass" }),
                                        customQiCount);
            case ATUS:
                return customizeQis ((new String[] {   "Age",
                                                       "Race",
                                                       "Region",
                                                       "Sex",
                                                        "Birthplace",
                                                        "Citizenship status",
                                                        "Labor force status",
                                                        "Marital status" }),
                                         customQiCount);
            case CUP:
                return customizeQis ((new String[] {   "AGE",
                                                        "GENDER",
                                                        "STATE",
                                                        "ZIP",
                                                        "INCOME",
                                                        "MINRAMNT",
                                                        "NGIFTALL" }),
                                        customQiCount);
            case FARS:
                return customizeQis ((new String[] {   "iage",
                                                       "ihispanic",
                                                       "irace",
                                                       "isex",
                                                        "ideathday",
                                                        "ideathmon",
                                                        "iinjury" }),
                                        customQiCount);
            case IHIS:
                return customizeQis ((new String[] {   "AGE",
                                                       "RACEA",
                                                       "REGION",
                                                       "SEX",
                                                        "MARSTAT",
                                                        "PERNUM",
                                                        "QUARTER",
                                                        "YEAR" }),
                                        customQiCount);
            case ACS13:
                return customizeQis (ACS13_SEMANTIC_QI.getNames(), customQiCount);
            default:
                throw new RuntimeException("Invalid dataset");
            }
        }

        /**
         * @return the number of QIs used in the dataset
         */
        public Integer getCustomQiCount() {
            return customQiCount;
        }
        
        /**
         * @param attribute
         * @return
         */
        public Hierarchy getHierarchy(String attribute) {
        	return this.inputDataDef.getHierarchyObject(attribute);
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
        	return DataSubset.create(this.toArxData(null), Data.create(filename, ';'));
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
            case ADULT:
                return new String[] { "occupation",
                                      "education", 
                                      "salary-class", 
                                      "workclass" };
            case ATUS:
                return new String[] { "Highest level of school completed",
                                      "Birthplace", 
                                      "Citizenship status", 
                                      "Labor force status" 
                                      };
            case CUP:
                return new String[] { "RAMNTALL",
                                      "INCOME", 
                                      "MINRAMNT", 
                                      "NGIFTALL" };
            case FARS:
                return new String[] { "istatenum",
                                      "ideathday", 
                                      "ideathmon", 
                                      "iinjury" };
            case IHIS:
                return new String[] { "EDUC",
                                      "MARSTAT", 
                                      "PERNUM", 
                                      "QUARTER" };
            case ACS13:
                return new String[] { "SCHL",
                                      "PWGTP",
                                      "SCHG",
                                      "INTP" };
            default:
                throw new RuntimeException("Invalid dataset");
            }
        }
        
        @Override
        public String toString() {
            return datafile.toString();
        }
        
        /**
         * Configures and returns the dataset as <code>org.deidentifier.arx.Data</code>
         * @param dataset
         * @param criteria
         * @return
         * @throws IOException
         */
        @SuppressWarnings("incomplete-switch")
		private Data toArxData(BenchmarkCriterion[] criteria) {
        	Data arxData;

            	String path = "data/" + datafile.getBaseStringForFilename() + ".csv";
                try {
					arxData = Data.create(path, ';');
				} catch (IOException e) {
					arxData = null;
					System.err.println("Unable to load dataset from file " + path);
				}
                for (String qi : _getQuasiIdentifyingAttributes()) {
                    arxData.getDefinition().setAttributeType(qi, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
                    arxData.getDefinition().setHierarchy(qi, loadHierarchy(qi));
                }
                if (criteria != null) {
                    for (BenchmarkCriterion c : criteria) {
                        switch (c) {
                        case L_DIVERSITY_DISTINCT:
                        case L_DIVERSITY_ENTROPY:
                        case L_DIVERSITY_RECURSIVE:
                        case T_CLOSENESS:
                            String sensitive = getSensitiveAttribute();
                            arxData.getDefinition().setAttributeType(sensitive, AttributeType.SENSITIVE_ATTRIBUTE);
                            break;
                        }
                    }
                }           
            
            return arxData;
        }

        /**
         * Returns the generalization hierarchy for the dataset and attribute
         * @param dataset
         * @param attribute
         * @return
         * @throws IOException
         */
        private Hierarchy loadHierarchy(String attribute) {
            if (!datafile.equals(BenchmarkDatafile.ACS13)) {
            	String path = "hierarchies/" + datafile.getBaseStringForFilename() + "_hierarchy_" + attribute + ".csv";
                try {
					return Hierarchy.create(path, ';');
				} catch (IOException e) {
					System.err.println("Unable to load hierarchy from file " + path);
					return null;
				}
            } else {
                return loadACS13Hierarchy("hierarchies/" + datafile.getBaseStringForFilename() + "_hierarchy_", attribute);
            }
        }

        private static Hierarchy loadACS13Hierarchy(String fileBaseName, String attribute) {
            String filePath = fileBaseName + ACS13_SEMANTIC_QI.valueOf(attribute).fileBaseName();
            switch (ACS13_SEMANTIC_QI.valueOf(attribute).getType()) {
            case INTERVAL:
                filePath += ".ahs";
                HierarchyBuilder<?> loaded;
				try {
					loaded = HierarchyBuilder.create(filePath);
				} catch (IOException e) {
					loaded = null;
					System.err.println("Unable to init hierarchy builder with file " + filePath);
				}
                if (loaded.getType() == Type.INTERVAL_BASED) {
                    HierarchyBuilderIntervalBased<?> builder = (HierarchyBuilderIntervalBased<?>) loaded;
                    Data data;
                    String path = "data/" + BenchmarkDatafile.ACS13.getBaseStringForFilename() + ".csv";
					try {
						data = Data.create(path, ';');
					} catch (IOException e) {
						data = null;
						System.err.println("Unable to load dataset from file ");
					}
                    int index = data
                                    .getHandle()
                                    .getColumnIndexOf(attribute);
                    String[] dataArray = data
                                             .getHandle()
                                             .getStatistics()
                                             .getDistinctValues(index);
                    builder.prepare(dataArray);
                    return builder.build();
                } else {
                    throw new RuntimeException("Inconsistent hierarchy types. Expected: interval-based, found: " + loaded.getType());
                }
            case ORDER:
                filePath += ".csv";
                try {
					return Hierarchy.create(filePath, ';');
				} catch (IOException e) {
					System.err.println("Unable to load hierarchy from file " + filePath);
				}
            default:
                throw new RuntimeException("Invalid hierarchy Type");
            }
        }
        
        private static String[] customizeQis(String[] qis, Integer customQiCount) {
            return customQiCount == null ? qis : Arrays.copyOf(qis, customQiCount);
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

        private enum ACS13_SEMANTIC_QI {
            AGEP(HierarchyType.INTERVAL), // height 10
            CIT(HierarchyType.ORDER), // height 06
            COW(HierarchyType.ORDER), // height 06
            SEX(HierarchyType.ORDER), // height 02
            FER(HierarchyType.ORDER), // height 02
            DOUT(HierarchyType.ORDER), // height 04
            DPHY(HierarchyType.ORDER), // height 04
            DREM(HierarchyType.ORDER), // height 03
            GCL(HierarchyType.ORDER), // height 02
            HINS1(HierarchyType.ORDER), // height 02
            HINS2(HierarchyType.ORDER), // height 02
            HINS3(HierarchyType.ORDER), // height 02
            HINS4(HierarchyType.ORDER), // height 02
            HINS5(HierarchyType.ORDER), // height 02
            HINS6(HierarchyType.ORDER), // height 02
            HINS7(HierarchyType.ORDER), // height 02
            MAR(HierarchyType.ORDER), // height 02
            MARHD(HierarchyType.ORDER), // height 02
            MARHM(HierarchyType.ORDER), // height 02
            MARHW(HierarchyType.ORDER), // height 02
            MIG(HierarchyType.ORDER), // height 02
            MIL(HierarchyType.ORDER), // height 02
            PWGTP(HierarchyType.INTERVAL), // height 03
            RELP(HierarchyType.ORDER), // height 04
            SCHL(HierarchyType.ORDER), // height 02
            INTP(HierarchyType.INTERVAL), // height 02
            SCHG(HierarchyType.ORDER), // height 02
            DDRS(HierarchyType.ORDER), // height 05
            DEAR(HierarchyType.ORDER), // height 05
            DEYE(HierarchyType.ORDER), // height 05
            ;
            
            private enum HierarchyType {
                INTERVAL, // interval based
                ORDER // order based
            }

            private final HierarchyType ht;

            // constructor
            ACS13_SEMANTIC_QI(HierarchyType ht) {
                this.ht = ht;
            }

            // needed for file name generation
            public String fileBaseName() {
                final String        distinctionLetter;

                switch (ht) {
                case INTERVAL:
                    distinctionLetter = "i";
                    break;
                case ORDER:
                    distinctionLetter = "o";
                    break;
                default:
                    distinctionLetter = "x";
                }
                return (distinctionLetter + "_" + this.name());
            }

            public HierarchyType getType() {
                return ht;
            }
            
            public static String[] getNames() {
                ACS13_SEMANTIC_QI[] qis = values();
                String[] names = new String[qis.length];

                for (int i = 0; i < qis.length; i++) {
                    names[i] = qis[i].name();
                }

                return names;
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
            };
            
            private String baseStringForFilename = null;
            
            BenchmarkDatafile (String baseStringForFilename) {
                this.baseStringForFilename = baseStringForFilename;
            }
            
            /**
             * @return the string, that will be used for finding and loading the
             * datafile with its hierarchies from the filesystem
             */
            private String getBaseStringForFilename() {
                return baseStringForFilename;
            }
        }
}