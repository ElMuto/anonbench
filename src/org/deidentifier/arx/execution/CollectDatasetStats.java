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

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkDriver;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkCriterion;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;

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

        collectStats(false, 1);
    }

    /**
     * @param calcLatticeSize
     * @param verbosity <UL><LI>0: no output</LI><LI>1: loud</LI><LI>2: louder</LI></UL>
     * @throws IOException
     */
    private static void collectStats(boolean calcLatticeSize, int verbosity) throws IOException {

        BenchmarkMeasure metric = BenchmarkSetup.getMeasures()[0];
        double suppFactor = BenchmarkSetup.getSuppressionFactors()[0];

        // For each datafile
        for (BenchmarkDatafile datafile : BenchmarkSetup.getDatafiles()) {

            if (calcLatticeSize) {
            BenchmarkDataset dataset = new BenchmarkDataset(datafile, 4);
            BenchmarkCriterion[] criteria = new BenchmarkCriterion[] { BenchmarkCriterion.K_ANONYMITY };
            Data arxData = dataset.toArxData(criteria);
            BenchmarkDriver.anonymize(metric, suppFactor, dataset, arxData, criteria,
                                      false, 5, null, 
                                      null, null, null,
                                      null, null, null);
            } 
            printDatasetStats(datafile, verbosity);
        }
    }
    
    /**
     * @param datafile
     * @param printDetails
     * @throws IOException
     */
    public static void printDatasetStats(BenchmarkDatafile datafile, int verbosity) throws IOException {

        if (verbosity >= 1) System.out.println("Getting stats for dataset " + datafile.toString());
        BenchmarkDataset dataset;
        if (BenchmarkDatafile.ACS13.equals(datafile)) {
            dataset = new BenchmarkDataset(datafile, 30);} else {
                dataset = new BenchmarkDataset(datafile, null);
            }
        
        DataHandle handle = dataset.toArxData().getHandle();
        if (verbosity >= 1) System.out.println("  Default QIs");
        for (String sa : dataset.getQuasiIdentifyingAttributes()) {
            BenchmarkDriver.processSA(dataset, handle, sa, verbosity);
        }
        

        if (verbosity >= 1)  System.out.println("  Default SA");
        String sa = dataset.getSensitiveAttribute();
        BenchmarkDriver.processSA(dataset, handle, sa, verbosity);
    }
}
