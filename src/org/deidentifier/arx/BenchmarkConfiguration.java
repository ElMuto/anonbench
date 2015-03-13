package org.deidentifier.arx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.BenchmarkSetup.Algorithm;
import org.deidentifier.arx.BenchmarkSetup.AlgorithmType;
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

    class AnonConfiguration {

        private Algorithm            algorithm;
        private BenchmarkCriterion[] criteria;
        private BenchmarkDataset     dataset;
        private Metric<?>            metric;
        private int                  qiCount;
        private double               suppression;
        private Integer              terminationLimit;

        public AnonConfiguration(Algorithm algorithm,
                                 Metric<?> metric,
                                 double suppression,
                                 BenchmarkCriterion[] criteria,
                                 BenchmarkDataset dataset,
                                 int qiCount, Integer terminationLimit) {
            this.algorithm = algorithm;
            this.metric = metric;
            this.suppression = suppression;
            this.criteria = criteria;
            this.dataset = dataset;
            this.qiCount = qiCount;
            this.terminationLimit = terminationLimit;
        }

        public Algorithm getAlgorithm() {
            return algorithm;
        }

        public BenchmarkCriterion[] getBenchmarkCriteria() {
            return criteria;
        }

        public BenchmarkDataset getDataset() {
            return dataset;
        }

        public Metric<?> getMetric() {
            return metric;
        }

        public int getQICount() {
            return qiCount;
        }

        public double getSuppression() {
            return suppression;
        }

        public Integer getTerminationLimit() {
            return terminationLimit;
        }

        public String toString() {
            return algorithm.toString() + ";" + metric.getName() + ";" + String.valueOf(suppression) + ";" + Arrays.toString(criteria) +
                   ";" + dataset.toString() + ";" + String.valueOf(qiCount) + ";" +
                   (null == terminationLimit ? "" : terminationLimit.toString());
        }
    }

    private List<AnonConfiguration> anonConfigurations;

    public BenchmarkConfiguration() {
        anonConfigurations = new ArrayList<BenchmarkConfiguration.AnonConfiguration>();
    }

    public void addAnonConfiguration(AnonConfiguration anonConfiguration) {
        anonConfigurations.add(anonConfiguration);

    }

    public AnonConfiguration getAnonConfigurationFromLine(String line) {
        String[] param = line.split(";", -1);

        if (param.length != 7) {
            System.out.println("Wrong number of params.");
            System.out.println("Required: 'Algorithm; Metric; Suppression; Criteria; Dataset; [QICount]; [TerminationLimit]'");
            System.out.println("Actual  : " + line);
            System.exit(0);
        }

        // algorithm
        Algorithm algorithm = BenchmarkSetup.getAlgorithmByName(param[0], param[6]);
        // metric
        Metric<?> metric = BenchmarkSetup.getMetricByName(param[1]);
        // suppression
        Double suppression = null;
        try {
            suppression = Double.parseDouble(param[2]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong format: Suppression needs to be a Double. Found: " + param[2]);
            System.exit(0);
        }
        // criteria
        BenchmarkCriterion[] criteria = BenchmarkSetup.getCriteriaFromString(param[3]);
        // dataset
        BenchmarkDataset dataset = BenchmarkDataset.fromLabel(param[4]);
        // qicount
        String qiCountString = param[5];
        int qiCount = 0;
        if (qiCountString.isEmpty()) {
            System.out.println("QICount was not specified, number of qi's in dataset will be used instead.");
            qiCount = BenchmarkSetup.getQuasiIdentifyingAttributes(dataset).length;
        }
        else {
            try {
                qiCount = Integer.parseInt(param[5]);
            } catch (NumberFormatException e) {
                System.out.println("Wrong format: QIcount needs to be an Integer. Found: " + qiCountString);
                System.exit(0);
            }
        }

        return new AnonConfiguration(algorithm,
                                     metric,
                                     suppression,
                                     criteria,
                                     dataset,
                                     qiCount,
                                     AlgorithmType.HEURAKLES == algorithm.getType() ? algorithm.getTerminationConfig().getValue() : null);
    }

    public List<AnonConfiguration> getAnonConfigurations() {
        return anonConfigurations;
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
