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

package org.deidentifier.arx.test;

import java.io.IOException;
import java.util.Arrays;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.classification.ARXWeka;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.test.TestData.Dataset;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing classification with weka
 * 
 * @author Fabian Prasser
 */
public class TestClassification {
    
    private static final double ADULT_MIN_ACCURACY = 13.387706385518202d;
    private static final double ADULT_MAX_ACCURACY = 31.987268748756712d;

    private static final double ATUS_MIN_ACCURACY  = 38.41425082475202d;
    private static final double ATUS_MAX_ACCURACY  = 74.68294103138972d;

    private static final double FARS_MIN_ACCURACY  = 42.78411286247857d;
    private static final double FARS_MAX_ACCURACY  = 65.79648691758226d;

    private static final double IHIS_MIN_ACCURACY  = 23.619527039708288d;
    private static final double IHIS_MAX_ACCURACY  = 77.88603976191115d;
    
    public static void main(String[] args) throws IOException {
        for (Dataset dataset : new Dataset[] { Dataset.IHIS }) {
            DataHandle input = TestData.getData(dataset).getHandle();
            int columns = input.getNumColumns();
            for (int column = 0; column < columns; column++) {
                String attribute = input.getAttributeName(column);
                
                try {
                    double minimum = getAccuracyBaseline(dataset, attribute);
                    double maximum = getAccuracyOriginal(dataset, attribute);
                    double anonymized = getAccuracyKAnonymized(dataset, attribute);
                    System.out.println(anonymized);
                    
                    System.out.print("initBounds(Dataset.");
                    System.out.print(dataset.toString().toUpperCase());
                    System.out.print(", \"");
                    System.out.print(attribute);
                    System.out.print("\", ");
                    System.out.print(minimum);
                    System.out.print(", ");
                    System.out.print(maximum);
                    System.out.println(");");
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Normalizes the resulting accuracy for the given dataset
     * @param dataset
     * @param accuracy
     * @return
     */
    private static final double getRelativeAccuracy(Dataset dataset, double accuracy) {
        switch (dataset) {
        case ADULT:
            return (accuracy - ADULT_MIN_ACCURACY) / (ADULT_MAX_ACCURACY - ADULT_MIN_ACCURACY);
        case ATUS:
            return (accuracy - ATUS_MIN_ACCURACY) / (ATUS_MAX_ACCURACY - ATUS_MIN_ACCURACY);
        case FARS:
            return (accuracy - FARS_MIN_ACCURACY) / (FARS_MAX_ACCURACY - FARS_MIN_ACCURACY);
        case IHIS:
            return (accuracy - IHIS_MIN_ACCURACY) / (IHIS_MAX_ACCURACY - IHIS_MIN_ACCURACY);
        default:
            throw new IllegalArgumentException("Dataset " + dataset + " can not be classified");
        }
    }

    /**
     * Returns the target attribute for the given dataset
     * @param dataset
     * @return
     */
    private static final String getTargetAttribute(Dataset dataset) {
        switch (dataset) {
        case ADULT:
            return "occupation";
        case ATUS:
            return "Marital status";
        case FARS:
            return "iinjury";
        case IHIS:
            return "MARSTAT";
        default:
            throw new IllegalArgumentException("Dataset " + dataset + " can not be classified");
        }
    }

    @Test
    public void testAdult() throws IOException {
        test(Dataset.ADULT, 0.8212121212121212d);
    }

    
    @Test
    public void testAtus() throws IOException {
        test(Dataset.ATUS, 0.951687289088864d);
    }

    @Test
    public void testFars() throws IOException {
        test(Dataset.FARS, 0.9358532805235058d);
    }

    @Test
    public void testIhis() throws IOException {
        test(Dataset.IHIS, 0.6753624128225198d);
    }

    @SuppressWarnings("unused")
    private static double getAccuracyBaseline(Dataset dataset) throws IOException {
        return getAccuracyBaseline(dataset, getTargetAttribute(dataset));
    }

    /**
     * Returns baseline accuracy
     * @param dataset
     * @param attribute
     * @return
     * @throws IOException
     */
    private static double getAccuracyBaseline(Dataset dataset, String clazz) throws IOException {

        // Data and class
        Data input = TestData.getData(dataset);
        DataHandle handle = input.getHandle();

        // Classify and return accuracy
        return ARXWeka.getClassificationAccuracy(handle, Arrays.asList(new String[]{clazz}), clazz);
    }

    /**
     * Returns the prediction accuracy for a k-anonymized file
     * @param dataset
     * @return
     * @throws IOException
     */
    private static double getAccuracyKAnonymized(Dataset dataset) throws IOException {
        return getAccuracyKAnonymized(dataset, getTargetAttribute(dataset));
    }

    /**
     * Returns the prediction accuracy for a k-anonymized file
     * @param dataset
     * @param attribute
     * @return
     * @throws IOException
     */
    private static double getAccuracyKAnonymized(Dataset dataset, String target) throws IOException {

        // Data and class
        Data input = TestData.getData(dataset);

        // We do not want to transform the class attribute
        input.getDefinition().setAttributeType(target, AttributeType.INSENSITIVE_ATTRIBUTE);

        // Prepare
        ARXConfiguration config = ARXConfiguration.create();
        config.setAttributeTypeSuppressed(AttributeType.IDENTIFYING_ATTRIBUTE, true);
        config.setAttributeTypeSuppressed(AttributeType.QUASI_IDENTIFYING_ATTRIBUTE, true);
        config.setAttributeTypeSuppressed(AttributeType.SENSITIVE_ATTRIBUTE, true);
        config.setQualityModel(Metric.createLossMetric());
        config.setMaxOutliers(1d);
        config.addPrivacyModel(new KAnonymity(5));

        // Anonymize
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(input, config);
        DataHandle handle = result.getOutput();

        // Classify and return accuracy
        return ARXWeka.getClassificationAccuracy(handle, target);
    }

    @SuppressWarnings("unused")
    private static double getAccuracyOriginal(Dataset dataset) throws IOException {
        return getAccuracyOriginal(dataset, getTargetAttribute(dataset));
    }

    /**
     * Gets the accuracy on the original dataset
     * @param dataset
     * @param attribute
     * @return
     * @throws IOException
     */
    private static double getAccuracyOriginal(Dataset dataset, String clazz) throws IOException {

        // Data and class
        Data input = TestData.getData(dataset);
        DataHandle handle = input.getHandle();

        // Classify and return accuracy
        return ARXWeka.getClassificationAccuracy(handle, clazz);
    }
    
    private void test(Dataset dataset, double result) throws IOException {

        double accuracy = getAccuracyKAnonymized(dataset);
        double relativeaccuracy = getRelativeAccuracy(dataset, accuracy);
        System.out.println(dataset.toString() + " Accuracy: " + accuracy + " Relative: " + relativeaccuracy * 100d + "[%]");
        Assert.assertEquals("Accuracy", relativeaccuracy, result, 0.0000001d);
    }
}
