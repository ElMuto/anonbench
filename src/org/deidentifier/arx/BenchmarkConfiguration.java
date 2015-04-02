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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkDataset;
import org.deidentifier.arx.metric.Metric;

import cern.colt.Arrays;

/**
 * This class represents a BenchmarkConfiguration. Therefore, it holds a list of {@link AnonConfiguration} which each represents
 * 
 * @author eicher
 */
public class BenchmarkConfiguration {

    public class AnonConfiguration {

        private Algorithm            algorithm;
        private BenchmarkCriterion[] criteria;
        private BenchmarkDataset     dataset;
        private Metric<?>            decisionMetric;
        private Metric<?>            iLMetric;
        private int                  qiCount;
        private double               suppression;

        public AnonConfiguration(Algorithm algorithm,
                                 Metric<?> decisionMetric,
                                 Metric<?> iLMetric,
                                 double suppression,
                                 BenchmarkCriterion[] criteria,
                                 BenchmarkDataset dataset,
                                 int qiCount) {
            this.algorithm = algorithm;
            this.decisionMetric = decisionMetric;
            this.iLMetric = iLMetric;
            this.suppression = suppression;
            this.criteria = criteria;
            this.dataset = dataset;
            this.qiCount = qiCount;
        }

        public Algorithm getAlgorithm() {
            return algorithm;
        }

        public BenchmarkCriterion[] getCriteria() {
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

        public String toString() {
            return algorithm.toString() +
                   " / " +
                   decisionMetric.getName() +
                   "(DM) / " +
                   iLMetric.toString() +
                   "(ILM) / " +
                   String.valueOf(suppression) +
                   " / " +
                   Arrays.toString(criteria) +
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

    }

    private Set<Algorithm>             algorithms;
    private List<AnonConfiguration>    anonConfigurations;
    private List<BenchmarkCriterion[]> criteriaList;
    private Set<String>                criteriaNames;
    private Set<BenchmarkDataset>      datasets;
    private Set<Metric<?>>             metrics;
    private Set<Double>                suppressionSet;

    public BenchmarkConfiguration() {
        anonConfigurations = new ArrayList<BenchmarkConfiguration.AnonConfiguration>();
        algorithms = new HashSet<BenchmarkSetup.Algorithm>();
        datasets = new HashSet<BenchmarkDataset>();
        criteriaNames = new HashSet<String>();
        criteriaList = new ArrayList<BenchmarkCriterion[]>();
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
        return new ArrayList<BenchmarkSetup.Algorithm>(this.algorithms);
    }

    public AnonConfiguration getAnonConfigurationFromLine(String line) {
        String[] param = line.split(";", -1);

        if (param.length != 9) {
            System.out.println("Wrong number of params.");
            System.out.println("Required: 'Algorithm; DecisionMetric; ILMetric; Suppression; Criteria; Dataset; [QICount]; TerminationLimitType; [TerminationLimit]'");
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
        metrics.add(decisionMetric);
        // iLMetric
        Metric<?> ilMetric = BenchmarkSetup.getMetricByName(_ilMetric);

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

        // criteria
        BenchmarkCriterion[] criteria = BenchmarkSetup.getCriteriaFromString(_criteria);
        if (!criteriaNames.contains(criteria.toString())) {
            criteriaNames.add(criteria.toString());
            criteriaList.add(criteria);
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

        return new AnonConfiguration(algorithm,
                                     decisionMetric,
                                     ilMetric,
                                     suppression,
                                     criteria,
                                     dataset,
                                     qiCount);
    }

    public List<AnonConfiguration> getAnonConfigurations() {
        return anonConfigurations;
    }

    /**
     * @return criteria used for this benchmark
     */
    public BenchmarkCriterion[][] getCriteria() {
        BenchmarkCriterion[][] result = new BenchmarkCriterion[criteriaList.size()][];
        for (int i = 0; i < criteriaList.size(); i++) {
            result[i] = criteriaList.get(i);
        }
        return result;
    }

    /**
     * @return datasets used for this benchmark
     */
    public BenchmarkDataset[] getDatasets() {
        return datasets.toArray(new BenchmarkDataset[datasets.size()]);
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
    public double[] getSuppression() {
        double[] suppr = new double[suppressionSet.size()];
        Iterator<Double> iter = suppressionSet.iterator();
        for (int i = 0; i < suppressionSet.size(); i++) {
            suppr[i] = iter.next();
        }
        return suppr;
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
                bw.write(configuration.toString());
                bw.write(System.lineSeparator());
            }
        } finally {
            bw.close();
        }
    }

}
