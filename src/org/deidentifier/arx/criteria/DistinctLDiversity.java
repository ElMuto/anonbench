/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2017 Fabian Prasser, Florian Kohlmayer and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deidentifier.arx.criteria;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.certificate.elements.ElementData;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.lattice.Transformation;

/**
 * The distinct l-diversity privacy criterion.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class DistinctLDiversity extends LDiversity{

    /** SVUID */
    private static final long serialVersionUID = -7973221140269608088L;

    /**
     * Creates a new instance of the distinct l-diversity privacy criterion as proposed in
     * Machanavajjhala A, Kifer D, Gehrke J.
     * l-diversity: Privacy beyond k-anonymity.
     * Transactions on Knowledge Discovery from Data (TKDD). 2007;1(1):3.
     *
     * @param attribute
     * @param l
     */
    public DistinctLDiversity(String attribute, int l){
        super(attribute, l, true, true);
    }

    @Override
    public DistinctLDiversity clone() {
        return new DistinctLDiversity(this.getAttribute(), (int)this.getL());
    }

    public static void prepareBeta() {
        numBeta = 0;
        avgBeta = 0;
        minBeta = Double.MAX_VALUE;
        maxBeta = -Double.MAX_VALUE;
    }

    public static double numBeta = 0;
    public static double avgBeta = 0;
    public static double minBeta = Double.MAX_VALUE;
    public static double maxBeta = -Double.MAX_VALUE;

    public static void doneBeta() {
        avgBeta /= numBeta;
    }

	@Override
    public boolean isAnonymous(Transformation node, HashGroupifyEntry entry) {
	
	    // Init
        int[] buckets = entry.distributions[index].getBuckets();
        double count = entry.count;

        // For each value in c
        double beta = 0d;
        double numBetas = 0d;
        for (int i = 0; i < buckets.length; i += 2) {
            if (buckets[i] != -1) { // bucket not empty
                double frequencyInT = distribution[buckets[i]];
                double frequencyInC = (double) buckets[i + 1] / count;
                double value = (frequencyInC - frequencyInT) / frequencyInT;
                beta += value;
                numBetas++;
            }
        }

        // Average beta for this class
        beta /= numBetas;
        avgBeta += beta;
        numBeta ++;
        minBeta = Math.min(minBeta,  beta);
        maxBeta = Math.max(maxBeta,  beta);

	
	    // Distinct l-diversity
        return entry.distributions[index].size() >= minSize; // minSize=(int)l;
    }
	
    private double[] distribution;

    @Override
    public void initialize(DataManager manager, ARXConfiguration config) {
        super.initialize(manager, config);
        this.distribution = manager.getDistribution(attribute);
    }

    @Override
    public boolean isLocalRecodingSupported() {
        return true;
    }
    
    @Override
    public ElementData render() {
    	throw new RuntimeException("this method is only existing to get the code compiled and should not be entered");
    }

    @Override
	public String toString() {
		return "distinct-"+minSize+"-diversity for attribute '"+attribute+"'";
	}
}
