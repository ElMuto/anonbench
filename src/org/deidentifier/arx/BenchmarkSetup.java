/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal
 * methods for the de-identification of biomedical data"
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilderIntervalBased;
import org.deidentifier.arx.aggregates.HierarchyBuilderRedactionBased;
import org.deidentifier.arx.aggregates.HierarchyBuilder.Type;
import org.deidentifier.arx.algorithm.TerminationConfiguration;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;

/**
 * This class encapsulates most of the parameters of a benchmark run
 * @author Fabian Prasser
 */
public class BenchmarkSetup {

    protected static final TerminationConfiguration.Type TERMINATION_TYPE   = TerminationConfiguration.Type.TIME;
    private static Integer[]                             TERMINATION_LIMITS = new Integer[] { 2000 };
//    private static Integer[]                             TERMINATION_LIMITS = new Integer[] { 2000, 5000, 10000, 20000 };
    private static final boolean                         INCLUDE_FLASH      = false;

    public static enum AlgorithmType {
        FLASH {
            @Override
            public String toString() {
                return "Flash";
            }
        },
        HEURAKLES {
            @Override
            public String toString() {
                return "Heurakles";
            }
        },
        INFORMATION_LOSS_BOUNDS {
            @Override
            public String toString() {
                return "InformationLossBounds";
            }
        },
    }

    public static class Algorithm {
        private AlgorithmType            type;
        private TerminationConfiguration terminationConfig;

        public Algorithm(AlgorithmType type, TerminationConfiguration terminationConfig) {
            this.type = type;
            this.terminationConfig = terminationConfig;
        }

        @Override
        public String toString() {
            String baseString = type.toString();
            String suffix = "";
            if (terminationConfig != null) {
                switch (terminationConfig.getType()) {
                case CHECKS:
                    suffix += "_c";
                    break;
                case TIME:
                    suffix += "_t";
                    break;
                }
                suffix += terminationConfig.getValue();
            }
            return baseString + suffix;
        }

        public TerminationConfiguration getTerminationConfig() {
            return terminationConfig;
        }

        public AlgorithmType getType() {
            return type;
        }

        public String getStatusSuffix() {
            String suffix = type.equals(AlgorithmType.HEURAKLES) ?
                    terminationConfig.getValue() + (BenchmarkSetup.TERMINATION_TYPE == TerminationConfiguration.Type.CHECKS ?
                            "checks" :
                            "milliseconds") :
                    "no termination limits";
            return suffix;
        }
    }

    public static enum BenchmarkCriterion {
        K_ANONYMITY {
            @Override
            public String toString() {
                return "k";
            }
        },
        L_DIVERSITY {
            @Override
            public String toString() {
                return "l";
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
    }

    public static enum BenchmarkDataset {
        ADULT {
            @Override
            public String toString() {
                return "Adult";
            }
        },
        CUP {
            @Override
            public String toString() {
                return "Cup";
            }
        },
        FARS {
            @Override
            public String toString() {
                return "Fars";
            }
        },
        ATUS {
            @Override
            public String toString() {
                return "Atus";
            }
        },
        IHIS {
            @Override
            public String toString() {
                return "Ihis";
            }
        },
        SS13PMA_TWO_LEVEL {
            @Override
            public String toString() {
                return "SS13PMA_TWO_LEVEL";
            }
        },
        SS13PMA_FIVE_LEVEL {
            @Override
            public String toString() {
                return "SS13PMA_FIVE_LEVEL";
            }
        },
        SS13ACS_SEMANTIC {
            @Override
            public String toString() {
                return "SS13ACS_SEMANTIC";
            }
        }
    }

    /**
     * Returns all algorithms
     * @return
     */
    public static List<Algorithm> getAlgorithms() {
        List<Algorithm> benchmarkAlgorithmList = new ArrayList<Algorithm>(TERMINATION_LIMITS.length + (INCLUDE_FLASH ? 1 : 0));

        if (INCLUDE_FLASH) benchmarkAlgorithmList.add(new Algorithm(AlgorithmType.FLASH, null));
        for (Integer tLimit : TERMINATION_LIMITS) {
            benchmarkAlgorithmList.add(new Algorithm(AlgorithmType.HEURAKLES, new TerminationConfiguration(TERMINATION_TYPE, tLimit)));
        }

        return benchmarkAlgorithmList;
    }

    /**
     * Return algorithm for this type.
     * @param algorithmType
     * @return
     */
    public static Algorithm getAlgorithmByType(AlgorithmType algorithmType) {
        for (Algorithm algorithm : getAlgorithms()) {
            if (algorithm.getType() == algorithmType) {
                return algorithm;
            }
        }
        throw new RuntimeException("Algorithm with type: " + algorithmType + " not found.");
    }

    /**
     * Returns a configuration for the ARX framework
     * @param dataset
     * @param criteria
     * @param suppression
     * @param metric
     * @return
     * @throws IOException
     */
    public static ARXConfiguration getConfiguration(BenchmarkDataset dataset,
                                                    Metric<?> metric,
                                                    double suppression,
                                                    int qiCount,
                                                    BenchmarkCriterion... criteria) throws IOException {
        ARXConfiguration config = ARXConfiguration.create();
        config.setMetric(metric);
        config.setMaxOutliers(suppression);
        for (BenchmarkCriterion c : criteria) {
            switch (c) {
            case D_PRESENCE:
                config.addCriterion(new DPresence(0.05d, 0.15d, getResearchSubset(dataset, qiCount)));
                break;
            case K_ANONYMITY:
                config.addCriterion(new KAnonymity(5));
                break;
            case L_DIVERSITY:
                String sensitive = getSensitiveAttribute(dataset);
                config.addCriterion(new RecursiveCLDiversity(sensitive, 4, 3));
                break;
            case T_CLOSENESS:
                sensitive = getSensitiveAttribute(dataset);
                config.addCriterion(new HierarchicalDistanceTCloseness(sensitive, 0.2d, getHierarchy(dataset, sensitive)));
                break;
            default:
                throw new RuntimeException("Invalid criterion");
            }
        }
        return config;
    }

    /**
     * Returns all suppression parameters
     * @return
     */
    public static double[] getSuppression() {
//        return new double[] { 0d, 1d };
        return new double[] { 1d };
    }

    /**
     * Returns all metrics
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Metric[] getMetrics() {
        return new Metric[] {
                // use non-monotonic version of supporting metrics
                // Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN),
                // Metric.createEntropyMetric(),
                // Metric.createPrecisionMetric(),
                // Metric.createAECSMetric(),
                // Metric.createDiscernabilityMetric()

                // use monotonic version of supporting metrics
                Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN),
//                Metric.createEntropyMetric(false),
//                Metric.createDiscernabilityMetric(false)
        };
    }

    /**
     * Returns all sets of criteria
     * @return
     */
    public static BenchmarkCriterion[][] getCriteria() {
        BenchmarkCriterion[][] result = new BenchmarkCriterion[1][];
        result[0] = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
        // result[1] = new BenchmarkCriterion[] { BenchmarkCriterion.L_DIVERSITY };
        // result[2] = new BenchmarkCriterion[] { BenchmarkCriterion.T_CLOSENESS };
        // result[3] = new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE };
        // result[4] = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.L_DIVERSITY };
        // result[5] = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.T_CLOSENESS };
        // result[6] = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY, BenchmarkCriterion.D_PRESENCE };
        // result[7] = new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE, BenchmarkCriterion.L_DIVERSITY };
        // result[8] = new BenchmarkCriterion[] { BenchmarkCriterion.D_PRESENCE, BenchmarkCriterion.T_CLOSENESS };
        // result[9] = new BenchmarkCriterion[] {
        // BenchmarkCriterion.K_ANONYMITY,
        // BenchmarkCriterion.D_PRESENCE,
        // BenchmarkCriterion.L_DIVERSITY };
        // result[10] = new BenchmarkCriterion[] {
        // BenchmarkCriterion.K_ANONYMITY,
        // BenchmarkCriterion.D_PRESENCE,
        // BenchmarkCriterion.T_CLOSENESS };
        return result;
    }

    /**
     * Returns the dataset
     * @param dataset
     * @return
     * @throws IOException
     */
    public static Data getData(BenchmarkDataset dataset, int qiCount) throws IOException {
        return getData(dataset, null, qiCount);
    }

    /**
     * Returns the path to the file containing a dataset
     * @param dataset
     * @return
     */
    public static String getFilePath(BenchmarkDataset dataset) {
        switch (dataset) {
        case ADULT:
            return "data/adult.csv";
        case ATUS:
            return "data/atus.csv";
        case CUP:
            return "data/cup.csv";
        case FARS:
            return "data/fars.csv";
        case IHIS:
            return "data/ihis.csv";
        case SS13PMA_TWO_LEVEL:
        case SS13PMA_FIVE_LEVEL:
            return "data/ss13pma_clean.csv";
        case SS13ACS_SEMANTIC:
            return "data/ss13acs_05829Recs_Wyoming_edited.csv";
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    /**
     * Configures and returns the dataset
     * @param dataset
     * @param criteria
     * @return
     * @throws IOException
     */
    @SuppressWarnings("incomplete-switch")
    public static Data getData(BenchmarkDataset dataset, BenchmarkCriterion[] criteria, int qiCount) throws IOException {
        Data data = Data.create(getFilePath(dataset), ';');

        if (criteria != null) {
            for (String qi : Arrays.copyOf(getQuasiIdentifyingAttributes(dataset), qiCount)) {
                data.getDefinition().setAttributeType(qi, getHierarchy(dataset, qi));
            }
            for (BenchmarkCriterion c : criteria) {
                switch (c) {
                case L_DIVERSITY:
                case T_CLOSENESS:
                    String sensitive = getSensitiveAttribute(dataset);
                    data.getDefinition().setAttributeType(sensitive, AttributeType.SENSITIVE_ATTRIBUTE);
                    break;
                }
            }
        }

        return data;
    }

    /**
     * Returns all datasets for the conventional benchmark
     * @return
     */
    public static BenchmarkDataset[] getDatasets() {
        return new BenchmarkDataset[] {
//                BenchmarkDataset.IHIS,
//                BenchmarkDataset.ADULT,
//                BenchmarkDataset.CUP,
//                BenchmarkDataset.FARS,
//                BenchmarkDataset.ATUS
        };
    }

    /**
     * Returns all datasets for the QI count scaling benchmark
     * @return
     */
    public static BenchmarkDataset[] getQICountScalingDatasets() {
        return new BenchmarkDataset[] {
//                BenchmarkDataset.SS13PMA_TWO_LEVEL,
//                BenchmarkDataset.SS13PMA_FIVE_LEVEL,
                BenchmarkDataset.SS13ACS_SEMANTIC
        };
    }

    /**
     * Returns the generalization hierarchy for the dataset and attribute
     * @param dataset
     * @param attribute
     * @return
     * @throws IOException
     */
    public static Hierarchy getHierarchy(BenchmarkDataset dataset, String attribute) throws IOException {
        switch (dataset) {
        case ADULT:
            return Hierarchy.create("hierarchies/adult_hierarchy_" + attribute + ".csv", ';');
        case ATUS:
            return Hierarchy.create("hierarchies/atus_hierarchy_" + attribute + ".csv", ';');
        case CUP:
            return Hierarchy.create("hierarchies/cup_hierarchy_" + attribute + ".csv", ';');
        case FARS:
            return Hierarchy.create("hierarchies/fars_hierarchy_" + attribute + ".csv", ';');
        case IHIS:
            return Hierarchy.create("hierarchies/ihis_hierarchy_" + attribute + ".csv", ';');
        case SS13PMA_FIVE_LEVEL:
            return Hierarchy.create("hierarchies/ss13pma_hierarchy_pwgtp.csv", ';');
        case SS13ACS_SEMANTIC: {
//            return Hierarchy.create("hierarchies/ss13acs_hierarchy_" + attribute + ".ahs", ';');
            HierarchyBuilder<?> loaded = HierarchyBuilder.create("hierarchies/ss13acs_hierarchy_" + SS13PMA_SEMANTIC_QI.valueOf(attribute).fileBaseName() + ".ahs");
            if (loaded.getType() == Type.INTERVAL_BASED) {
                HierarchyBuilderIntervalBased<?> builder = (HierarchyBuilderIntervalBased<?>)loaded;
                Data data = Data.create(getFilePath(dataset), ';');
                int index = data
                        .getHandle()
                        .getColumnIndexOf(attribute);
                String[] dataArray = data
                        .getHandle()
                        .getStatistics()
                        .getDistinctValues(index);
                System.out.println("Resulting levels: "+Arrays.toString(builder.prepare(dataArray)));
                return builder.build();
            } else {
                // TODO: implement support ordering based hierarchies
                throw new RuntimeException("Only INTERVAL_BASED hierarchies supported so far");
            }
        }
        default:
            return createTwoLevelHierarchy(dataset, attribute);
        }
    }

    /**
     * Returns a dynamically created two level hierarchy
     * @param dataset
     * @param attribute
     * @return
     * @throws IOException
     */
    public static Hierarchy createTwoLevelHierarchy(BenchmarkDataset dataset, String attribute) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getFilePath(dataset)));

        String[] headerLine = reader.readLine().split(";");
        int colIdx = 0;
        for (; colIdx < headerLine.length; ++colIdx) {
            if (headerLine[colIdx].equals(attribute)) {
                break;
            }
        }
        if (colIdx == headerLine.length) {
            reader.close();
            throw new RuntimeException("Invalid attribute");
        }

        Set<String> attributeDomain = new HashSet<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            String value = (line.split(";"))[colIdx];
            attributeDomain.add(value);
        }

        reader.close();

        final String[][] hierarchy = new String[attributeDomain.size()][];
        Iterator<String> iter = attributeDomain.iterator();
        for (int i = 0; i < hierarchy.length; ++i) {
            hierarchy[i] = new String[2];
            hierarchy[i][0] = iter.next();
            hierarchy[i][1] = "*";
        }

        return Hierarchy.create(hierarchy);
    }
    
    private enum SS13PMA_SEMANTIC_QI {
        PWGTP (HIERARCHY_TYPE.i),
        AGEP  (HIERARCHY_TYPE.i),
        INTP  (HIERARCHY_TYPE.i),
//        i_JWMNP,
//        i_MARHYP,
//        i_RETP,
//        i_SEMP,
//        i_SSP,
//        i_WAGP,
//        i_WKHP,
//        e02_DDRS,
//        e02_DEAR,
//        e02_DEYE,
//        e02_DOUT,
//        e02_DPHY,
//        e02_DRATX,
//        e02_DREM,
//        e02_FER,
//        e02_GCL,
//        e02_GCR,
//        e02_HINS1,
//        e02_HINS2,
//        e02_HINS3,
//        e02_HINS4,
//        e02_HINS5,
//        e02_HINS6,
//        e02_HINS7,
//        e02_LANX,
//        e02_MARHD,
//        e02_MARHM,
//        e02_MARHW,
//        e02_SEX,
//        e03_MARHT,
//        e03_MIG,
//        e03_NWAB,
//        e03_NWLA,
//        e03_NWLK,
//        e03_NWRE,
//        e03_SCH,
//        e03_WKL,
//        e04_ENG,
//        e04_MIL,
//        e05_CIT,
//        e05_GCM,
//        e05_MAR,
//        e05_NWAV,
//        e06_DRAT,
//        e06_WKW,
//        e09_COW,
//        e10_JWRIP,
//        e12_JWTR,
//        e16_SCHG,
//        e17_RELP,
//        e24_SCHL,
        ;
        
        private enum HIERARCHY_TYPE {
            i,  // interval based
            o   // order based
        }
        private final HIERARCHY_TYPE ht;
        
        // constructor
        SS13PMA_SEMANTIC_QI (HIERARCHY_TYPE ht) {
            this.ht = ht;
        }
        
        // needed for file name generation
        public String fileBaseName() {
            return (ht.name() + "_" + this.name());
        }
    }

    /**
     * Returns the quasi-identifiers for the dataset.
     * @param dataset
     * @return
     */
    public static String[] getQuasiIdentifyingAttributes(BenchmarkDataset dataset) {
        switch (dataset) {
        case ADULT:
            return new String[] { "age",
                    "education",
                    "marital-status",
                    "native-country",
                    "race",
                    "salary-class",
                    "sex",
                    "workclass" };
        case ATUS:
            return new String[] { "Age",
                    "Birthplace",
                    "Citizenship status",
                    "Labor force status",
                    "Marital status",
                    "Race",
                    "Region",
                    "Sex" };
        case CUP:
            return new String[] { "AGE",
                    "GENDER",
                    "INCOME",
                    "MINRAMNT",
                    "NGIFTALL",
                    "STATE",
                    "ZIP" };
        case FARS:
            return new String[] { "iage",
                    "ideathday",
                    "ideathmon",
                    "ihispanic",
                    "iinjury",
                    "irace",
                    "isex" };
        case IHIS:
            return new String[] { "AGE",
                    "MARSTAT",
                    "PERNUM",
                    "QUARTER",
                    "RACEA",
                    "REGION",
                    "SEX",
                    "YEAR" };
        case SS13PMA_TWO_LEVEL:
        case SS13PMA_FIVE_LEVEL:
            return new String[] { "pwgtp1",
                    "pwgtp2",
                    "pwgtp3",
                    "pwgtp4",
                    "pwgtp5",
                    "pwgtp6",
                    "pwgtp7",
                    "pwgtp8",
                    "pwgtp9",
                    "pwgtp10",
                    "pwgtp11",
                    "pwgtp12",
                    "pwgtp13",
                    "pwgtp14",
                    "pwgtp15",
                    "pwgtp16",
                    "pwgtp17",
                    "pwgtp18",
                    "pwgtp19",
                    "pwgtp20",
                    "pwgtp21",
                    "pwgtp22",
                    "pwgtp23",
                    "pwgtp24",
                    "pwgtp25",
                    "pwgtp26",
                    "pwgtp27",
                    "pwgtp28",
                    "pwgtp29",
                    "pwgtp30"
            };
        case SS13ACS_SEMANTIC:
            ArrayList<String> al = new ArrayList<>();
            for (SS13PMA_SEMANTIC_QI qi : SS13PMA_SEMANTIC_QI.values()) {
                al.add(qi.toString());
            }
            String[] qiArr = new String[al.size()];
            qiArr = al.toArray(qiArr);
            return qiArr;
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    /**
     * Returns the minimal number of QIs to use for a given algorithm and dataset
     * @param dataset
     * @return
     */
    public static int getMinQICount(BenchmarkDataset dataset) {
        if (dataset == BenchmarkDataset.SS13PMA_FIVE_LEVEL) {
            return 5;
        } else if (dataset == BenchmarkDataset.SS13ACS_SEMANTIC) {
            return 1;
        }
        return 10;
    }

    /**
     * Returns the maximal number of QIs to use for a given algorithm and dataset
     * @param algorithm
     * @param dataset
     * @return
     */
    public static int getMaxQICount(Algorithm algorithm, BenchmarkDataset dataset) {
        if (dataset == BenchmarkDataset.SS13PMA_TWO_LEVEL) {
            if (algorithm.getType() == AlgorithmType.FLASH) return 23;
        } else if (dataset == BenchmarkDataset.SS13PMA_FIVE_LEVEL) {
            if (algorithm.getType() == AlgorithmType.FLASH) return 10;
            else if (algorithm.getType() == AlgorithmType.HEURAKLES) return 16;
        } 
        return getQuasiIdentifyingAttributes(dataset).length;
    }

    /**
     * Returns the research subset for the dataset
     * @param dataset
     * @return
     * @throws IOException
     */
    public static DataSubset getResearchSubset(BenchmarkDataset dataset, int qiCount) throws IOException {
        switch (dataset) {
        case ADULT:
            return DataSubset.create(getData(dataset, qiCount), Data.create("data/adult_subset.csv", ';'));
        case ATUS:
            return DataSubset.create(getData(dataset, qiCount), Data.create("data/atus_subset.csv", ';'));
        case CUP:
            return DataSubset.create(getData(dataset, qiCount), Data.create("data/cup_subset.csv", ';'));
        case FARS:
            return DataSubset.create(getData(dataset, qiCount), Data.create("data/fars_subset.csv", ';'));
        case IHIS:
            return DataSubset.create(getData(dataset, qiCount), Data.create("data/ihis_subset.csv", ';'));
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    /**
     * Returns the sensitive attribute for the dataset
     * @param dataset
     * @return
     */
    public static String getSensitiveAttribute(BenchmarkDataset dataset) {
        switch (dataset) {
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
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    /**
     * Specify, whether relative information loss shall be calculated. This includes
     * running a DFS algorithm over the whole lattice determining minimum and maximum
     * information loss values.
     * 
     * @return true if relative information loss shall be included in the results file,
     *         false otherwise
     */
    public static boolean includeRelativeInformationLoss() {
        return false;
    }
}
