package org.deidentifier.arx;

import java.io.IOException;
import java.util.Arrays;

import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilderIntervalBased;
import org.deidentifier.arx.aggregates.HierarchyBuilder.Type;
    

    /**
     * this class encapsulates the configuration of a dataset the location 
     * and loading of its
     * its data- and hierarchy-files from the filesystem as well as the number of QIs
     * actually used for the dataest
     * @author helmut spengler
     *
     */
    public class BenchmarkDataset {
        private BenchmarkDatafile datafile = null;
        private Integer customQiCount = null;
        private String sensitiveAttribute;
        private Data arxData = null;
        private DataHandle handle = null;
        
        /**
         * @param datafile
         * @param customQiCount
         * @param sensitiveAttribute
         */
        public BenchmarkDataset(BenchmarkDatafile datafile, Integer customQiCount, String sensitiveAttribute) {
            this.datafile = datafile;
            this.customQiCount = customQiCount;
            this.sensitiveAttribute = sensitiveAttribute;
        }

        /**
         * @param datafile
         * @param customQiCount
         */
        public BenchmarkDataset(BenchmarkDatafile datafile, Integer customQiCount) {
            this.datafile = datafile;
            this.customQiCount = customQiCount;
            this.sensitiveAttribute = getDefaultSensitiveAttribute(datafile);
        }

        public BenchmarkDatafile getDatafile() {
            return datafile;
        }

        /**
         * @return the number of QIs used in the dataset
         */
        public Integer getCustomQiCount() {
            return customQiCount;
        }
        
        @Override
        public String toString() {
            return datafile.toString();
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
        
        /**
         * Configures and returns the dataset as <code>org.deidentifier.arx.Data</code>
         * @param dataset
         * @param criteria
         * @return
         * @throws IOException
         */
        @SuppressWarnings("incomplete-switch")
		public Data toArxData(BenchmarkCriterion[] criteria) throws IOException {
            
            if (this.arxData == null) {

                this.arxData = Data.create("data/" + datafile.getBaseStringForFilename() + ".csv", ';');
                for (String qi : getQuasiIdentifyingAttributes()) {
                    this.arxData.getDefinition().setAttributeType(qi, loadHierarchy(qi));
                }
                if (criteria != null) {
                    for (BenchmarkCriterion c : criteria) {
                        switch (c) {
                        case L_DIVERSITY_DISTINCT:
                        case L_DIVERSITY_ENTROPY:
                        case L_DIVERSITY_RECURSIVE:
                        case T_CLOSENESS:
                            String sensitive = getSensitiveAttribute();
                            this.arxData.getDefinition().setAttributeType(sensitive, AttributeType.SENSITIVE_ATTRIBUTE);
                            break;
                        }
                    }
                }
                if (this.handle == null) setHandle(arxData.getHandle());
            }
            
            return this.arxData;
        }

        /**
         * Returns the generalization hierarchy for the dataset and attribute
         * @param dataset
         * @param attribute
         * @return
         * @throws IOException
         */
        Hierarchy loadHierarchy(String attribute) throws IOException {
            if (!datafile.equals(BenchmarkDatafile.ACS13)) {
                return Hierarchy.create("hierarchies/" + datafile.getBaseStringForFilename() + "_hierarchy_" + attribute + ".csv", ';');
            } else {
                return loadACS13Hierarchy("hierarchies/" + datafile.getBaseStringForFilename() + "_hierarchy_", attribute);
            }
        }

        private static Hierarchy loadACS13Hierarchy(String fileBaseName, String attribute) throws IOException {
            String filePath = fileBaseName + ACS13_SEMANTIC_QI.valueOf(attribute).fileBaseName();
            switch (ACS13_SEMANTIC_QI.valueOf(attribute).getType()) {
            case INTERVAL:
                filePath += ".ahs";
                HierarchyBuilder<?> loaded = HierarchyBuilder.create(filePath);
                if (loaded.getType() == Type.INTERVAL_BASED) {
                    HierarchyBuilderIntervalBased<?> builder = (HierarchyBuilderIntervalBased<?>) loaded;
                    Data data = Data.create("data/" + BenchmarkDatafile.ACS13.getBaseStringForFilename() + ".csv", ';');
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
                return Hierarchy.create(filePath, ';');
            default:
                throw new RuntimeException("Invalid hierarchy Type");
            }
        }


        /**
         * Returns the quasi-identifiers for the dataset
         * @param dataset
         * @return
         */
        public String[] getQuasiIdentifyingAttributes() {
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
        
        private static String[] customizeQis(String[] qis, Integer customQiCount) {
            return customQiCount == null ? qis : Arrays.copyOf(qis, customQiCount);
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
        
        public DataHandle getHandle(BenchmarkCriterion[] criteria) throws IOException {
            if (this.handle == null) toArxData(criteria);
            
            return handle;
        }

        private void setHandle(DataHandle handle) {
            this.handle = handle;
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
         * Returns the default sensitive attribute for the dataset
         * @param dataset
         * @return
         */
        private String getDefaultSensitiveAttribute(BenchmarkDatafile datafile) {
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
//                return "INTP"; //nok - (464 eindeutige Werte)
            default:
                throw new RuntimeException("Invalid dataset");
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
            case ADULT:
                return new String[] { "occupation",
                                      "education", 
                                      "salary-class", 
                                      "workclass" };
            case ATUS:
                return new String[] { "Highest level of school completed",
                                      "Birthplace", 
                                      "Citizenship status", 
                                      "Labor force status" };
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
                return new String[] { "PWGTP",
                                      "SCHG", 
                                      "SCHL", 
                                      "INTP" };
            default:
                throw new RuntimeException("Invalid dataset");
            }
        }
}