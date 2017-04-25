/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2015 Florian Kohlmayer, Fabian Prasser
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

package org.deidentifier.arx.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.deidentifier.arx.DataHandle;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * This class performs classification using weka with the J48 classifier
 *  
 * @author Fabian Prasser
 */
public class ARXWeka {
    
    /**
     * Prefix for the temporary file
     */
    private static final String PREFIX = "arxweka";

    /**
     * Performs a classification on the given data handle. Returns the classification accuracy as a value in [0,1].
     * All values apart from the given attribute are used as source attributes.
     * 
     * @param handle        The handle
     * @param target        The target attribute
     * @return              The classification accuracy
     * @throws IOException
     */
    public static double getClassificationAccuracy(DataHandle handle, String target) throws IOException{
        
        // Extract predictors
        boolean found = false;
        List<String> predictors = new ArrayList<String>();
        for (int i=0; i<handle.getNumColumns(); i++) {
            String attribute = handle.getAttributeName(i);
            if (!attribute.equals(target)) {
                predictors.add(attribute);
            } else {
                found = true;
            }
        }
        
        // Check
        if (!found) {
            throw new IllegalArgumentException("Attribute '" + target + "' is unknown");
        }
        
        // Classify
        return getClassificationAccuracy(handle, predictors, target);
    }

    /**
     * Performs a classification on the given data handle. Returns the classification accuracy as a value in [0,1].
     * 
     * @param handle        The handle
     * @param sources       The source attributes
     * @param target        The target attribute
     * @return              The classification accuracy
     * @throws IOException
     */
    public static double getClassificationAccuracy(DataHandle handle,
                                                   List<String> sources,
                                                   String target) throws IOException {
        
        // Check
        for (String predictor : sources) {
            if (handle.getColumnIndexOf(predictor) == -1) {
                throw new IllegalArgumentException("Attribute '" + predictor + "' is unknown");
            }
        }
        if (handle.getColumnIndexOf(target) == -1) {
            throw new IllegalArgumentException("Attribute '" + target + "' is unknown");
        }
        
        // Create temporary file
        File temp = File.createTempFile(PREFIX, ".csv");
        
        // Write to file
        handle.save(temp, ',');
        
        // Fix bug in WEKA loader, it cannot handle '--'
        File temp2 = File.createTempFile(PREFIX, ".csv");
        BufferedReader reader = new BufferedReader(new FileReader(temp));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp2));
        String line = reader.readLine();
        while (line != null) {
            writer.write(line.replace("'", ""));
            writer.write("\n");
            line = reader.readLine();
        }
        reader.close();
        writer.close();
        temp.delete();
        
        // Read file into WEKA
        CSVLoader loader = new CSVLoader();
        loader.setSource(temp2);
        Instances data = loader.getDataSet();
        
        // Extract indices of predictors and attribute
        Set<Integer> list = new HashSet<Integer>();
        list.add(data.attribute(target).index());
        for (String source : sources) {
            list.add(data.attribute(source).index());
        }
        int[] array = new int[list.size()];
        int index = 0;
        for (Integer value : list) {
            array[index++] = value;
        }
        Arrays.sort(array);
        
        // Filter predictors and attribute
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(array);
        remove.setInvertSelection(true);
        try {
            remove.setInputFormat(data);
            data = Filter.useFilter(data, remove);
        } catch (Exception e) {
            throw new IllegalStateException("Error filtering attributes", e);
        }

        // Set class index
        data.setClassIndex(data.attribute(target).index());

        // Run J48 classifier
        J48 tree = new J48();
        tree.setConfidenceFactor(0.25f);
        tree.setMinNumObj(2);

        try {
            
            // Perform 10-fold cross-validation
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(tree, data, 10, new Random(0xDEADBEEF));
            
            // Free resources
            temp2.delete();
            
            // Return prediction accuracy
            return eval.pctCorrect();
        } catch (Exception e) {
            throw new IllegalStateException("Error executing WEKA", e);
        }
    }
}
