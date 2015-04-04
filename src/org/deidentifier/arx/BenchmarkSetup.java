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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deidentifier.arx.ARXPopulationModel.Region;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.BenchmarkAnalysis.VARIABLES;
import org.deidentifier.arx.BenchmarkConfiguration.AnonConfiguration;
import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilder.Type;
import org.deidentifier.arx.aggregates.HierarchyBuilderIntervalBased;
import org.deidentifier.arx.algorithm.TerminationConfiguration;
import org.deidentifier.arx.criteria.DPresence;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.PopulationUniqueness;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.Metric.AggregateFunction;

/**
 * This class encapsulates most of the parameters of a benchmark run
 * @author Fabian Prasser
 */
public class BenchmarkSetup {

    public static class Algorithm {
        private String                   name;
        private TerminationConfiguration terminationConfig;
        private AlgorithmType            type;

        public Algorithm(AlgorithmType type, TerminationConfiguration terminationConfig) {
            this.type = type;
            this.terminationConfig = terminationConfig;
            if (null != terminationConfig) {
                this.name = null != terminationConfig ? type.toString() + String.valueOf(terminationConfig.getValue()) : type.toString();
            }
        }

        public String getName() {
            return this.name;
        }

        public String getStatusSuffix() {
            return type.equals(AlgorithmType.HEURAKLES) ?
                    terminationConfig.getValue() + (BenchmarkSetup.TERMINATION_TYPE == TerminationConfiguration.Type.CHECKS ?
                            " checks" :
                            " milliseconds") :
                    "no termination limits";
        }

        public TerminationConfiguration getTerminationConfig() {
            return terminationConfig;
        }

        public AlgorithmType getType() {
            return type;
        }

        @Override
        public String toString() {
            return type.toString();
        }
    }

    public static enum AlgorithmType {
        FLASH("Flash", 1),
        HEURAKLES("Heurakles", 2),
        INFORMATION_LOSS_BOUNDS("InformationLossBounds", 0),
        DATAFLY("DataFly", 3),
        IMPROVED_GREEDY("ImprovedGreedy", 4);

        public static AlgorithmType fromLabel(String label) {
            if (label != null) {
                for (AlgorithmType aT : AlgorithmType.values()) {
                    if (label.equalsIgnoreCase(aT.label)) {
                        return aT;
                    }
                }
            }
            System.out.println("Unsupported Algorithm: " + label);
            System.exit(0);
            return null;
        }

        private String  label;
        private Integer position;

        private AlgorithmType(String label, Integer position) {
            this.label = label;
            this.position = position;
        }

        public Integer getPosition() {
            return this.position;
        }

        public String toString() {
            return this.label;
        }
    }

    public static enum BenchmarkCriterion {
        D_PRESENCE("d"),
        K_ANONYMITY("k"),
        L_DIVERSITY("l"),
        RISK_BASED("r"),
        T_CLOSENESS("t");

        public static BenchmarkCriterion fromLabel(String label) {
            if (label != null) {
                for (BenchmarkCriterion bC : BenchmarkCriterion.values()) {
                    if (label.equalsIgnoreCase(bC.label)) {
                        return bC;
                    }
                }
            }
            System.out.println("Unsupported Criterion: " + label);
            System.exit(0);
            return null;
        }

        private String label;

        private BenchmarkCriterion(String label) {
            this.label = label;
        }

        public String toString() {
            return this.label;
        }

    }

    public static enum BenchmarkDataset {
        ADULT("Adult"),
        ATUS("Atus"),
        CUP("Cup"),
        FARS("Fars"),
        IHIS("Ihis"),
        SS13ACS_SEMANTIC("SS13ACS_SEMANTIC"),
        SS13PMA_FIVE_LEVEL("SS13PMA_FIVE_LEVEL"),
        SS13PMA_TWO_LEVEL("SS13PMA_TWO_LEVEL");

        public static BenchmarkDataset fromLabel(String label) {
            if (label != null) {
                for (BenchmarkDataset bD : BenchmarkDataset.values()) {
                    if (label.equalsIgnoreCase(bD.label)) {
                        return bD;
                    }
                }
            }
            System.out.println("Unsupported Dataset: " + label);
            System.exit(0);
            return null;
        }

        private String label;

        private BenchmarkDataset(String label) {
            this.label = label;
        }

        public String toString() {
            return this.label;
        }

    }

    private enum SS13PMA_SEMANTIC_QI {
        AGEP(HierarchyType.INTERVAL), // height 10
        CIT(HierarchyType.ORDER), // height 06
        COW(HierarchyType.ORDER), // height 06
        DDRS(HierarchyType.ORDER), // height 05
        DEAR(HierarchyType.ORDER), // height 05
        DEYE(HierarchyType.ORDER), // height 05
        DOUT(HierarchyType.ORDER), // height 04
        DPHY(HierarchyType.ORDER), // height 04
        DREM(HierarchyType.ORDER), // height 03
        FER(HierarchyType.ORDER), // height 02
        GCL(HierarchyType.ORDER), // height 02
        HINS1(HierarchyType.ORDER), // height 02
        HINS2(HierarchyType.ORDER), // height 02
        HINS3(HierarchyType.ORDER), // height 02
        HINS4(HierarchyType.ORDER), // height 02
        HINS5(HierarchyType.ORDER), // height 02
        HINS6(HierarchyType.ORDER), // height 02
        HINS7(HierarchyType.ORDER), // height 02
        INTP(HierarchyType.INTERVAL), // height 02
        MAR(HierarchyType.ORDER), // height 02
        MARHD(HierarchyType.ORDER), // height 02
        MARHM(HierarchyType.ORDER), // height 02
        MARHW(HierarchyType.ORDER), // height 02
        MIG(HierarchyType.ORDER), // height 02
        MIL(HierarchyType.ORDER), // height 02
        PWGTP(HierarchyType.INTERVAL), // height 03
        RELP(HierarchyType.ORDER), // height 04
        SCHG(HierarchyType.ORDER), // height 02
        SCHL(HierarchyType.ORDER), // height 02
        SEX(HierarchyType.ORDER), // height 02
        ;

        private enum HierarchyType {
            INTERVAL, // interval based
            ORDER // order based
        }

        private final String        distinctionLetter;
        private final HierarchyType ht;

        // constructor
        SS13PMA_SEMANTIC_QI(HierarchyType ht) {
            this.ht = ht;

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
        }

        // needed for file name generation
        public String fileBaseName() {
            return (distinctionLetter + "_" + this.name());
        }

        public HierarchyType getType() {
            return ht;
        }
    }

    public static final String                           DEFAULT_CONFIGURAITON_FILE = "cluster-worklists/defaultConfiguration.csv";

    public static final String                           INFORMATION_LOSS_FILE      = "cluster-results/informationLossBounds.csv";

    private static Map<String, Algorithm>                name2Algorithm;

    private static HashMap<String, BenchmarkCriterion[]> name2Criteria;

    private static Map<String, Metric<?>>                name2Metric;

    public static final String                           RESULTS_FILE               = "cluster-results/results.csv";

    protected static final TerminationConfiguration.Type TERMINATION_TYPE           = TerminationConfiguration.Type.ANONYMITY;

    /**
     * Create {@link BenchmarkConfiguration} from set parameters and save to file.
     * @param configurationFile
     */
    public static void createAndSaveDefaultBenchmarkConfiguration(String configurationFile) {
        BenchmarkConfiguration benchmarkConfiguration = new BenchmarkConfiguration();

        // For each algorithm (flash, ilbounds, heurakles, datafly, improvedGreedy)
        for (Algorithm algorithm : BenchmarkSetup.getAlgorithms(true, true)) {
            // For each metric
            for (Metric<?> metric : BenchmarkSetup.getMetrics()) {
                Metric<?> decisionMetric = null;
                Metric<?> iLMetric = null;

                switch (algorithm.getType()) {
                case FLASH:
                case HEURAKLES:
                case INFORMATION_LOSS_BOUNDS:
                    decisionMetric = iLMetric = metric;
                    break;
                case DATAFLY:
                    decisionMetric = BenchmarkSetup.getMetricByName("DataFly");
                    iLMetric = metric;
                    break;
                case IMPROVED_GREEDY:
                    decisionMetric = BenchmarkSetup.getMetricByName("ImprovedGreedy");
                    iLMetric = metric;
                default:
                    break;
                }

                // For each suppression factor
                for (double suppression : BenchmarkSetup.getSuppression()) {
                    // For each combination of criteria
                    for (BenchmarkCriterion[] criteria : BenchmarkSetup.getCriteria()) {
                        // For each dataset
                        BenchmarkDataset[] datasets = (algorithm.getType() == AlgorithmType.DATAFLY || algorithm.getType() == AlgorithmType.IMPROVED_GREEDY) ? BenchmarkSetup.getConventionalDatasets() : BenchmarkSetup.getDatasets();
                        for (BenchmarkDataset data : datasets) {
                            // For each qiCount
                            for (int qiCount = BenchmarkSetup.getMinQICount(data); qiCount <= BenchmarkSetup.getMaxQICount(algorithm, data); qiCount++) {

                                AnonConfiguration c = benchmarkConfiguration.new AnonConfiguration(algorithm,
                                                                                                   decisionMetric,
                                                                                                   iLMetric,
                                                                                                   suppression,
                                                                                                   criteria,
                                                                                                   data,
                                                                                                   qiCount);
                                benchmarkConfiguration.addAnonConfiguration(c);
                            }
                        }
                    }
                }
            }
        }

        try {
            benchmarkConfiguration.saveBenchmarkConfiguration(configurationFile, benchmarkConfiguration.getAnonConfigurations());
        } catch (IOException e) {
            e.printStackTrace();
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

    /**
     * Returns an {@link Algorithm} instance based on params name and terminationLimitString.
     * @param name of the algorithm
     * @param terminationLimitString String representation of the termination limit, null when no limit needs to be specified
     * @return
     */
    public static Algorithm getAlgorithmByName(String name, String _terminationLimitType, String _terminationLimitValue) {

        AlgorithmType algorithmType = AlgorithmType.fromLabel(name);
        TerminationConfiguration terminationConfiguration = null;

        if (AlgorithmType.FLASH == algorithmType && (!_terminationLimitType.isEmpty() || !_terminationLimitValue.isEmpty())) {
            System.out.println("Algorithm " + name + " cannot be used with termination limits. Parameters [" + _terminationLimitType + ";" +
                               _terminationLimitValue + " ] will be ignored.");
        }
        else {
            TerminationConfiguration.Type terminationLimitType = TerminationConfiguration.Type.fromLabel(_terminationLimitType);
            int terminationLimit = 0;
            if (TerminationConfiguration.Type.CHECKS == terminationLimitType || TerminationConfiguration.Type.TIME == terminationLimitType) {
                try {
                    terminationLimit = Integer.parseInt(_terminationLimitValue);
                } catch (NumberFormatException e) {
                    System.out.println("Wrong format: Termination limit needs to be an Integer. Found: " + _terminationLimitValue);
                    System.exit(0);
                }
            }

            terminationConfiguration = new TerminationConfiguration(terminationLimitType, terminationLimit);
        }

        // algorithm
        Algorithm algorithm = new Algorithm(algorithmType, terminationConfiguration);

        if (null == name2Algorithm) {
            name2Algorithm = new HashMap<String, Algorithm>();
        }
        if (!name2Algorithm.containsKey(algorithm.getName())) {
            name2Algorithm.put(algorithm.getName(), algorithm);
        }
        return name2Algorithm.get(algorithm.getName());
    }

    /**
     * Returns all algorithms used in the benchmark
     * @param includeILBounds true if the {@link AlgorithmType#INFORMATION_LOSS_BOUNDS} algorithm shall be included, false otherwise
     * @param includeHeuristics true if the {@link AlgorithmType#DATAFLY} and {@link AlgorithmType#IMPROVED_GREEDY} shall be included, false otherwise
     * @return list of all used algorithms
     */
    public static List<Algorithm> getAlgorithms(boolean includeILBounds, boolean includeHeuristics) {

        List<Algorithm> benchmarkAlgorithmList = new ArrayList<Algorithm>();
        for (AlgorithmType aT : AlgorithmType.values()) {
            switch (aT) {
            case FLASH:
                benchmarkAlgorithmList.add(new Algorithm(AlgorithmType.FLASH, null));
                break;
            case INFORMATION_LOSS_BOUNDS:
                if (includeILBounds) {
                    benchmarkAlgorithmList.add(new Algorithm(AlgorithmType.INFORMATION_LOSS_BOUNDS, null));
                }
                break;
            case HEURAKLES:
                for (Integer tLimit : getTerminationLimits()) {
                    benchmarkAlgorithmList.add(new Algorithm(AlgorithmType.HEURAKLES,
                                                             new TerminationConfiguration(TERMINATION_TYPE, tLimit)));
                }
                break;
            case DATAFLY:
            case IMPROVED_GREEDY:
                if (includeHeuristics) {
                    TerminationConfiguration tC = new TerminationConfiguration(TERMINATION_TYPE.ANONYMITY, 0);
                    benchmarkAlgorithmList.add(new Algorithm(AlgorithmType.DATAFLY, tC));
                    benchmarkAlgorithmList.add(new Algorithm(AlgorithmType.IMPROVED_GREEDY, tC));
                }
            default:
                break;
            }
        }
        return benchmarkAlgorithmList;
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
            case RISK_BASED:
                config.addCriterion(new PopulationUniqueness(0.01d, ARXPopulationModel.create(Region.USA)));
            default:
                throw new RuntimeException("Invalid criterion");
            }
        }
        return config;
    }

    /**
     * Returns all datasets for the conventional benchmark
     * @return
     */
    public static BenchmarkDataset[] getConventionalDatasets() {
        return new BenchmarkDataset[] {
                BenchmarkDataset.IHIS,
                BenchmarkDataset.ADULT,
                BenchmarkDataset.CUP,
                BenchmarkDataset.FARS,
                BenchmarkDataset.ATUS
        };
    }

    /**
     * Returns all sets of criteria
     * @return
     */
    public static BenchmarkCriterion[][] getCriteria() {
        BenchmarkCriterion[][] result = new BenchmarkCriterion[1][];
        result[0] = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
        return result;
    }

    public static BenchmarkCriterion[] getCriteriaFromString(String criteria) {

        criteria = criteria.replace("[", "");
        criteria = criteria.replace("]", "");
        String key = criteria;
        String[] criteriaArray = criteria.split(",");

        BenchmarkCriterion[] result = new BenchmarkCriterion[criteriaArray.length];
        for (int i = 0; i < criteriaArray.length; i++) {
            result[i] = BenchmarkCriterion.fromLabel(criteriaArray[i].trim());
        }

        if (null == name2Criteria) {
            name2Criteria = new HashMap<String, BenchmarkCriterion[]>();
        }
        if (!name2Criteria.containsKey(key)) {
            name2Criteria.put(key, result);
        }

        return name2Criteria.get(key);
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
     * Returns the dataset
     * @param dataset
     * @return
     * @throws IOException
     */
    public static Data getData(BenchmarkDataset dataset, int qiCount) throws IOException {
        return getData(dataset, null, qiCount);
    }

    /**
     * Returns all datasets
     * @return
     */
    public static BenchmarkDataset[] getDatasets() {
        BenchmarkDataset[] conventional = getConventionalDatasets();
        BenchmarkDataset[] scaling = getQICountScalingDatasets();
        BenchmarkDataset[] result = (BenchmarkDataset[]) Arrays.copyOf(conventional, conventional.length + scaling.length);
        System.arraycopy(scaling, 0, result, conventional.length, scaling.length);
        return result;
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
            // return "data/ss13acs_05829Recs_Wyoming_edited.csv";
            // return "data/ss13acs_10619Recs_RhodeIsland_edited.csv";
            return "data/ss13acs_68726Recs_Massachusetts_edited.csv";

        default:
            throw new RuntimeException("Invalid dataset");
        }
    }

    public static String[] getHeader() {
        return new String[] {
                VARIABLES.ALGORITHM.val,
                VARIABLES.DATASET.val,
                VARIABLES.CRITERIA.val,
                VARIABLES.METRIC.val,
                VARIABLES.SUPPRESSION.val,
                VARIABLES.QI_COUNT.val,
                VARIABLES.TERMINATION_LIMIT.val };
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
            String filePath = "hierarchies/ss13acs_hierarchy_" + SS13PMA_SEMANTIC_QI.valueOf(attribute).fileBaseName();
            switch (SS13PMA_SEMANTIC_QI.valueOf(attribute).getType()) {
            case INTERVAL:
                filePath += ".ahs";
                HierarchyBuilder<?> loaded = HierarchyBuilder.create(filePath);
                if (loaded.getType() == Type.INTERVAL_BASED) {
                    HierarchyBuilderIntervalBased<?> builder = (HierarchyBuilderIntervalBased<?>) loaded;
                    Data data = Data.create(getFilePath(dataset), ';');
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
                    throw new RuntimeException("Inconsistent Hierarchy types: expected: interval-based, found: " + loaded.getType());
                }
            case ORDER:
                filePath += ".csv";
                return Hierarchy.create(filePath, ';');
            default:
                break;

            }
        }
        default:
            return createTwoLevelHierarchy(dataset, attribute);
        }
    }

    /**
     * Returns the maximal number of QIs to use for a given algorithm and dataset
     * @param algorithm
     * @param dataset
     * @return
     */
    public static int getMaxQICount(Algorithm algorithm, BenchmarkDataset dataset) {
        if (dataset == BenchmarkDataset.SS13PMA_TWO_LEVEL) {
            if (algorithm.getType() == AlgorithmType.FLASH) {
                return 23;
            }
        } else if (dataset == BenchmarkDataset.SS13PMA_FIVE_LEVEL) {
            if (algorithm.getType() == AlgorithmType.FLASH) {
                return 10;
            }
            else if (algorithm.getType() == AlgorithmType.HEURAKLES || algorithm.getType() == AlgorithmType.INFORMATION_LOSS_BOUNDS) {
                return 16;
            }
        } else if (dataset == BenchmarkDataset.SS13ACS_SEMANTIC) {
            if (algorithm.getType() == AlgorithmType.FLASH) {
                return 10;
            }
            else if (algorithm.getType() == AlgorithmType.HEURAKLES || algorithm.getType() == AlgorithmType.INFORMATION_LOSS_BOUNDS) {
                return 11;
            }
        }
        return getQuasiIdentifyingAttributes(dataset).length;
    }

    /**
     * Returns a {@link Metric} instance based on the param name.
     * @param name of the metric
     * @return
     */
    public static Metric<?> getMetricByName(String name) {
        // cache metrics
        if (null == name2Metric) {
            name2Metric = new HashMap<String, Metric<?>>();

            // Loss
            Metric<?> metric = Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN);
            name2Metric.put(metric.getName(), metric);

            // Non-monotonic non-uniform entropy
            metric = Metric.createEntropyMetric(false);
            name2Metric.put(metric.getName(), metric);

            // Non-monotonic discernability
            metric = Metric.createDiscernabilityMetric(false);
            name2Metric.put(metric.getName(), metric);

            // Average equivalence class size
            metric = Metric.createAECSMetric();
            name2Metric.put(metric.getName(), metric);

            // DataFly
            metric = Metric.createDataFlyMetric();
            name2Metric.put(metric.getName(), metric);

            // Improved Greedy
            metric = Metric.createImprovedGreedyMetric();
            name2Metric.put(metric.getName(), metric);
        }

        if (!name2Metric.containsKey(name)) {
            System.out.println("Unsupported Metric: " + name);
            System.exit(0);
        }

        return name2Metric.get(name);
    }

    /**
     * Returns all metrics
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Metric[] getMetrics() {
        return new Metric[] {
                Metric.createLossMetric(AggregateFunction.GEOMETRIC_MEAN),
        // Metric.createEntropyMetric(false),
        // Metric.createDiscernabilityMetric(false),
        // Metric.createAECSMetric(),
        };
    }

    /**
     * Returns the minimal number of QIs to use for a given algorithm and dataset
     * @param dataset
     * @return
     */
    public static int getMinQICount(BenchmarkDataset dataset) {
        if (dataset == BenchmarkDataset.SS13PMA_FIVE_LEVEL) {
            return 5;
        } else if (dataset == BenchmarkDataset.SS13PMA_TWO_LEVEL) {
            return 1;
        } else if (dataset == BenchmarkDataset.SS13ACS_SEMANTIC) {
            return 10;
        }
        else return getQuasiIdentifyingAttributes(dataset).length;
    }

    /**
     * Returns all datasets for the QI count scaling benchmark
     * @return
     */
    public static BenchmarkDataset[] getQICountScalingDatasets() {
        return new BenchmarkDataset[] {
                // BenchmarkDataset.SS13PMA_TWO_LEVEL,
                // BenchmarkDataset.SS13PMA_FIVE_LEVEL,
                BenchmarkDataset.SS13ACS_SEMANTIC
        };
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
     * Returns the research subset for the dataset
     * @param dataset
     * @return
     * @throws IOException
     */
    public static DataSubset getResearchSubset(BenchmarkDataset dataset, int qiCount) throws IOException {
        // FIXME extend to include all data sets
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
     * Returns all suppression parameters
     * @return
     */
    public static double[] getSuppression() {
        return new double[] { 0d /* , 0.02d, 0.05d, 0.1d, 1.0d */};
    }

    public static Integer[] getTerminationLimits() {
        return new Integer[] { 2000 /* , 5000, 10000, 20000 */};
    }
}
