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

package org.deidentifier.arx.execution;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.deidentifier.arx.AttributeStatistics;
import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.deidentifier.arx.QiConfig;

/**
 * Main benchmark class. Run with java -Xmx4G -XX:+UseConcMarkSweepGC -jar anonbench-0.1.jar
 * 
 * @author Fabian Prasser
 */
public class CollectDatasetStats {

    /**
     * Main entry point
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        collectStats(false, 2);
    }

    /**
     * @param calcLatticeSize
     * @param verbosity <UL><LI>0: no output</LI><LI>1: loud</LI><LI>2: louder</LI></UL>
     * @throws IOException
     */
    private static void collectStats(boolean calcLatticeSize, int verbosity) throws IOException {

        BenchmarkMeasure measure = BenchmarkSetup.getMeasures()[0];
        double suppFactor = BenchmarkSetup.getSuppressionFactors()[0];
        
        // init CSV file
        PrintWriter writer = new PrintWriter("results/l-diversity_stats.csv", "UTF-8");
        Field[] asFields = null;
        try { asFields = Class.forName("org.deidentifier.arx.AttributeStatistics").getFields(); } catch (SecurityException|ClassNotFoundException e) { e.printStackTrace(); }
        String[] fieldNames = new String[asFields.length];
        writer.print("Dataset;Attribute-Type;Attribute-Name");
        for (int i = 0; i < asFields.length; i++) {
            fieldNames[i] = asFields[i].getName();
            writer.print(";" + fieldNames[i]);
        }
        writer.print("\n");

        // For each datafile
        for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

            if (calcLatticeSize) {
                BenchmarkDataset dataset = new BenchmarkDataset(datafile, new QiConfig(4), null);
                BenchmarkDriver driver = new BenchmarkDriver(measure, dataset);
                driver.anonymize(measure, suppFactor, dataset, false, 5,
                                 null, null, null, 
                                 null, null, null,
                                 null, null);
            } 
            
//            printFullDatasetStats(datafile, verbosity);
            print_l_diversity_DatasetStats(datafile, verbosity, writer, fieldNames);
        }
        writer.close();
    }
    
    /**
     * @param datafile
     * @param printDetails
     * @throws IOException
     */
    public static void printFullDatasetStats(BenchmarkDatafile datafile, int verbosity) throws IOException {

        if (verbosity >= 1) System.out.println("Full stats for dataset " + datafile.toString());
        
        BenchmarkDataset dataset;
        if (BenchmarkDatafile.ACS13.equals(datafile)) {
            dataset = new BenchmarkDataset(datafile, new QiConfig(30), null);
        } else {
                dataset = new BenchmarkDataset(datafile, null, null);
        }    
        
        if (verbosity >= 1) {
            System.out.println("  Default QIs");
            DataHandle handle = dataset.getHandle();
            for (String attr : dataset.getQuasiIdentifyingAttributes()) {
                BenchmarkDriver.analyzeAttribute(dataset, handle, attr, verbosity);
            }        

            if (verbosity >= 1)  System.out.println("  Default SA");
            String sa = dataset.getSensitiveAttribute();
            BenchmarkDriver.analyzeAttribute(dataset, handle, sa, verbosity);
        }
    }
    
    /**
     * @param datafile
     * @param writer TODO
     * @param printDetails
     * @throws IOException
     */
    public static void print_l_diversity_DatasetStats(BenchmarkDatafile datafile, int verbosity, PrintWriter writer, String[] fieldNames) throws IOException {        
        if (verbosity >= 1) System.out.println("l-diversity stats for dataset " + datafile.toString());
        
        BenchmarkDataset dataset = new BenchmarkDataset(datafile, new QiConfig(4), new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY });        
        DataHandle handle = dataset.getHandle();

        if (verbosity >= 1) {
            System.out.println("  QIs");
            for (String attr : dataset.getQuasiIdentifyingAttributes()) {
                AttributeStatistics as = BenchmarkDriver.analyzeAttribute(dataset, handle, attr, verbosity);
                writeCsvLine(writer, fieldNames, dataset, "QI", attr, as);
            }        

            System.out.println("  Sensitive attribute candidates");
            for (String attr : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {
                AttributeStatistics as = BenchmarkDriver.analyzeAttribute(dataset, handle, attr, verbosity);
                writeCsvLine(writer, fieldNames, dataset, "SA-candidate", attr, as);
            }      
        }
    }

    private static void
            writeCsvLine(PrintWriter writer, String[] fieldNames, BenchmarkDataset dataset, String attrType, String attrName, AttributeStatistics as) {
        writer.print(dataset + ";" + attrType + ";" + attrName);
        for (String fieldName : fieldNames) {
            try {
                writer.print(";" + as.getClass().getField(fieldName).get(as));
            } catch (NoSuchFieldException|SecurityException|IllegalArgumentException|IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
        }
        writer.print("\n");
    }
}
