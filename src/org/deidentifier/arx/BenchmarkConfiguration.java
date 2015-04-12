package org.deidentifier.arx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.criteria.PrivacyCriterion;
import org.deidentifier.arx.metric.Metric;

/**
 * This class represents a BenchmarkConfiguration. Therefore, it holds a list of {@link AnonConfiguration} which each represents
 * 
 * @author eicher
 */
public class BenchmarkConfiguration {

    public class AnonConfiguration {

        private Algorithm              algorithm;
        private String                 criteria;
        private List<PrivacyCriterion> privacyCriteria;
        private BenchmarkDataset       dataset;
        private Metric<?>              decisionMetric;
        private Metric<?>              iLMetric;
        private int                    qiCount;
        private double                 suppression;

        public AnonConfiguration(Algorithm algorithm,
                                 Metric<?> decisionMetric,
                                 Metric<?> iLMetric,
                                 double suppression,
                                 String criteria, List<PrivacyCriterion> privacyCriteria,
                                 BenchmarkDataset dataset,
                                 int qiCount) {
            this.algorithm = algorithm;
            this.decisionMetric = decisionMetric;
            this.iLMetric = iLMetric;
            this.suppression = suppression;
            this.criteria = criteria;
            this.privacyCriteria = privacyCriteria;
            this.dataset = dataset;
            this.qiCount = qiCount;
        }

        public Algorithm getAlgorithm() {
            return algorithm;
        }

        public String getCriteria() {
            return criteria;
        }

        public BenchmarkDataset getDataset() {
            return dataset;
        }

        public Metric<?> getDecisionMetric() {
            return decisionMetric;
        }

        public Metric<?> getILMetric() {
            return iLMetric;
        }

        public int getQICount() {
            return qiCount;
        }

        public double getSuppression() {
            return suppression;
        }

        public String getStatusLine() {
            return algorithm.toString() +
                   " / " +
                   decisionMetric.getName() +
                   "(DM) / " +
                   (null != iLMetric ? iLMetric.toString() +
                                       "(ILM) / " : "") +
                   String.valueOf(suppression) +
                   " / " +
                   criteria +
                   " / " +
                   dataset.toString() +
                   " / " +
                   String.valueOf(qiCount) +
                   " / " +
                   (null == getAlgorithm().getTerminationConfig() ? "no Termination Limit" : getAlgorithm().getTerminationConfig()
                                                                                                           .getType()
                                                                                                           .toString() +
                                                                                             " (" +
                                                                                             getAlgorithm().getTerminationConfig()
                                                                                                           .getValue() + ")");

        }

        public String getConfigurationLine() {
            return algorithm.toString() + ";" + decisionMetric.getName() + ";" +
                   (null == iLMetric ? decisionMetric.getName() : iLMetric.getName()) + ";" + String.valueOf(suppression) +
                   ";" + criteria +
                   ";" + dataset.toString() + ";" + String.valueOf(qiCount) + ";" +
                   (null == getAlgorithm().getTerminationConfig() ? ";;" : (getAlgorithm().getTerminationConfig().getType().toString()) +
                                                                           ";" + getAlgorithm().getTerminationConfig().getValue());

        }

        public List<PrivacyCriterion> getPrivacyCriteria() {
            return privacyCriteria;
        }

    }

    private Set<Algorithm>          algorithms;
    private List<AnonConfiguration> anonConfigurations;
    private Set<String>             criteria;
    private Set<BenchmarkDataset>   datasets;
    private Set<Metric<?>>          metrics;
    private Set<Double>             suppressionSet;

    public BenchmarkConfiguration() {
        anonConfigurations = new ArrayList<BenchmarkConfiguration.AnonConfiguration>();
        algorithms = new HashSet<BenchmarkSetup.Algorithm>();
        datasets = new HashSet<BenchmarkDataset>();
        criteria = new HashSet<String>();
        metrics = new HashSet<Metric<?>>();
        suppressionSet = new HashSet<Double>();
    }

    public void addAnonConfiguration(AnonConfiguration anonConfiguration) {
        anonConfigurations.add(anonConfiguration);
    }

    /**
     * @return algorithms used for this benchmark
     */
    public List<Algorithm> getAlgorithms() {
        List<Algorithm> list = new ArrayList<BenchmarkSetup.Algorithm>(this.algorithms);
        Collections.sort(list, new Comparator<Algorithm>() {
            @Override
            public int compare(Algorithm o1, Algorithm o2) {
                return o1.getType().getPosition().compareTo(o2.getType().getPosition());
            }
        });
        return list;
    }

    public AnonConfiguration getAnonConfigurationFromLine(String line) throws IOException {
        String[] param = line.split(";", -1);

        if (param.length != 9) {
            System.out.println("Wrong number of params.");
            System.out.println("Required: 'Algorithm; DecisionMetric; [ILMetric]; Suppression; Criteria(type,param,param.../type...); Dataset; [QICount]; TerminationLimitType; [TerminationLimit]'");
            System.out.println("Actual  : " + line);
            System.exit(0);
        }

        String _algo = param[0];
        String _decisionMetric = param[1];
        String _ilMetric = param[2];
        String _suppression = param[3];
        String _criteria = param[4];
        String _dataset = param[5];
        String _qiCount = param[6];
        String _terminationLimitType = param[7];
        String _terminationLimitValue = param[8];

        Algorithm algorithm = BenchmarkSetup.getAlgorithmByName(_algo, _terminationLimitType, _terminationLimitValue);
        algorithms.add(algorithm);

        // decisionMetric
        Metric<?> decisionMetric = BenchmarkSetup.getMetricByName(_decisionMetric);
        // iLMetric
        Metric<?> ilMetric = null;
        if (!_ilMetric.isEmpty()) {
            ilMetric = BenchmarkSetup.getMetricByName(_ilMetric);
        }

        if (null == ilMetric) {
            metrics.add(decisionMetric);
        }

        // suppression
        Double suppression = null;
        try {
            suppression = Double.parseDouble(_suppression);
            if (!suppressionSet.contains(suppression)) {
                suppressionSet.add(suppression);
            }
        } catch (NumberFormatException e) {
            System.out.println("Wrong format: Suppression needs to be a Double. Found: " + _suppression);
            System.exit(0);
        }

        // dataset
        BenchmarkDataset dataset = BenchmarkDataset.fromLabel(_dataset);
        datasets.add(dataset);

        // qicount
        int qiCount = 0;
        if (_qiCount.isEmpty()) {
            System.out.println("QICount was not specified, number of qi's in dataset will be used instead.");
            qiCount = BenchmarkSetup.getQuasiIdentifyingAttributes(dataset).length;
        }
        else {
            try {
                qiCount = Integer.parseInt(_qiCount);
            } catch (NumberFormatException e) {
                System.out.println("Wrong format: QIcount needs to be an Integer. Found: " + _qiCount);
                System.exit(0);
            }
        }

        // criteria
        criteria.add(_criteria);
        List<PrivacyCriterion> privacyCriteria = BenchmarkSetup.getPrivacyCriteria(_criteria, dataset, qiCount);

        return new AnonConfiguration(algorithm,
                                     decisionMetric,
                                     ilMetric,
                                     suppression,
                                     _criteria, privacyCriteria,
                                     dataset,
                                     qiCount);
    }

    public List<AnonConfiguration> getAnonConfigurations() {
        return anonConfigurations;
    }

    public String getReadableCriteria(String _criteria) {
        String[] criteria = _criteria.split(",");
        BenchmarkCriterion type = BenchmarkCriterion.fromLabel(criteria[0]);
        switch (type) {
        case D_PRESENCE:
            return "(" + criteria[1] + "," + criteria[2] + ")-Presence";
        case K_ANONYMITY:
            return criteria[1] + "-Anonymity";
        case L_DIVERSITY:
            return "(" + criteria[1] + "," + criteria[2] + ")-Diversity";
        case RISK_BASED:
            return "Risk-Based (" + criteria[1] + ")";
        case T_CLOSENESS:
            return criteria[1] + "-Closeness";
        default:
            throw new RuntimeException("Invalid criterion");
        }
    }

    /**
     * @return criteria used for this benchmark
     */
    public List<String> getCriteria() {
        return new ArrayList<String>(criteria);
    }

    /**
     * @return datasets used for this benchmark
     */
    public BenchmarkDataset[] getDatasets() {
        List<BenchmarkDataset> list = new ArrayList<BenchmarkDataset>(datasets);
        Collections.sort(list);
        return list.toArray(new BenchmarkDataset[list.size()]);
    }

    /**
     * @return metrics used for this benchmark
     */
    public Metric<?>[] getMetrics() {
        ArrayList<Metric<?>> m = new ArrayList<Metric<?>>(metrics);
        Collections.sort(m, new Comparator<Metric<?>>() {
            @Override
            public int compare(Metric<?> m1, Metric<?> m2) {
                return m1.getName().compareTo(m2.getName());
            }
        });
        return m.toArray(new Metric<?>[m.size()]);
    }

    /**
     * @return suppression used for this benchmark
     */
    public Double[] getSuppression() {
        List<Double> list = new ArrayList<Double>(suppressionSet);
        Collections.sort(list);
        return list.toArray(new Double[list.size()]);
    }

    /**
     * 
     * 
     * @param benchmarkConfigurationFile
     * @return
     * @throws IOException
     */
    public ArrayList<AnonConfiguration> readBenchmarkConfiguration(final String benchmarkConfigurationFile) throws IOException {
        final ArrayList<AnonConfiguration> lines = new ArrayList<AnonConfiguration>();
        final BufferedReader br = new BufferedReader(new FileReader(benchmarkConfigurationFile));
        try {
            String line = br.readLine();
            while (line != null) {
                String trimedLine = line.trim();
                if (trimedLine.length() > 0) {
                    if (!trimedLine.startsWith("#")) {
                        lines.add(getAnonConfigurationFromLine(trimedLine));
                    }
                }
                line = br.readLine();
            }
        } finally {
            br.close();
        }

        if (lines.size() == 0) {
            throw new RuntimeException("Worklist empty!");
        }
        anonConfigurations = lines;
        return lines;
    }

    /**
     * 
     * 
     * @param benchmarkConfigurationFile
     * @param anonConfigurationList
     * @throws IOException
     */
    public void
            saveBenchmarkConfiguration(final String benchmarkConfigurationFile, final List<AnonConfiguration> anonConfigurationList) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(benchmarkConfigurationFile));
        try {
            for (final AnonConfiguration configuration : anonConfigurationList) {
                bw.write(configuration.getConfigurationLine());
                bw.write(System.lineSeparator());
            }
        } finally {
            bw.close();
        }
    }

}
