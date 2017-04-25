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
import java.util.Map;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.test.TestData.Dataset;
import org.deidentifier.arx.test.TestData.UtilityMeasure;
import org.deidentifier.arx.utility.AggregateFunction;
import org.deidentifier.arx.utility.DataConverter;
import org.deidentifier.arx.utility.UtilityMeasureAECS;
import org.deidentifier.arx.utility.UtilityMeasureAmbiguity;
import org.deidentifier.arx.utility.UtilityMeasureDiscernibility;
import org.deidentifier.arx.utility.UtilityMeasureGenericNonUniformEntropyWithLowerBound;
import org.deidentifier.arx.utility.UtilityMeasureKLDivergence;
import org.deidentifier.arx.utility.UtilityMeasureLoss;
import org.deidentifier.arx.utility.UtilityMeasureNonUniformEntropyWithLowerBound;
import org.deidentifier.arx.utility.UtilityMeasureNonUniformEntropyWithLowerBoundNormalized;
import org.deidentifier.arx.utility.UtilityMeasurePrecision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Class for testing implementations of utility measures in arx and in this utility project.
 * @author eicher
 *
 */
public class TestUtility {
    
    private final DataConverter converter = new DataConverter();
    
    private void doComparison(ARXConfiguration config, UtilityMeasure type) throws IOException {
        
        // data
        Data input = TestData.getData(Dataset.ADULT);
        DataHandle inputHandle = input.getHandle();
        String[][] inputArray = converter.toArray(input.getHandle());
        String[] header = converter.getHeader(inputHandle);
        Map<String, String[][]> hierarchies = converter.toMap(inputHandle.getDefinition());
        inputHandle.release();
        
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(input, config);
        ARXNode[][] levels = result.getLattice().getLevels();
        for (ARXNode[] level : levels) {
            for (ARXNode arxNode : level) {
                DataHandle handle = result.getOutput(arxNode);
                String[][] outputArray = converter.toArray(handle);
                handle.release();
                
                switch (type) {
                case AECS:
                case AMBIGUITY_METRIC:
                case DISCERNABILITY:
                case KL_DIVERGENCE:
                    double valFlash = Double.valueOf(arxNode.getMaximumInformationLoss().toString());
                    double valUtil;
                    switch (type) {
                    case AECS:
                        valUtil = new UtilityMeasureAECS().evaluate(outputArray).getUtility();
                        break;
                    case AMBIGUITY_METRIC:
                        valUtil = new UtilityMeasureAmbiguity(header, hierarchies).evaluate(outputArray, arxNode.getTransformation()).getUtility();
                        break;
                    case DISCERNABILITY:
                        valUtil = new UtilityMeasureDiscernibility().evaluate(outputArray).getUtility();
                        break;
                    case KL_DIVERGENCE:
                        valUtil = new UtilityMeasureKLDivergence(header, inputArray, hierarchies).evaluate(outputArray, arxNode.getTransformation()).getUtility();
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown Type: " + type.toString());
                    }
                    Assert.assertEquals("Transformation: " + Arrays.toString(arxNode.getTransformation()) + " -anonymity: " + arxNode.getAnonymity(), valFlash, valUtil, 0d);
                    break;
                case PRECISION:
                case LOSS:
                case ENTROPY_NORMALIZED:
                case GENERIC_ENTROPY:
                case ENTROPY:
                    double[] valsFlash = (double[]) arxNode.getMaximumInformationLoss().getValue();
                    double[] valsUtil;
                    switch (type) {
                    case PRECISION:
                        valsUtil = new UtilityMeasurePrecision<Double>(header, hierarchies, AggregateFunction.SUM).evaluateAggregatable(outputArray, arxNode.getTransformation());
                        break;
                    case LOSS:
                        valsUtil = new UtilityMeasureLoss<Double>(header, hierarchies, AggregateFunction.SUM).evaluateAggregatable(outputArray, arxNode.getTransformation());
                        break;
                    case GENERIC_ENTROPY:
                        valsUtil = new UtilityMeasureGenericNonUniformEntropyWithLowerBound<Double>(header, inputArray, hierarchies, AggregateFunction.SUM).evaluateAggregatable(outputArray, arxNode.getTransformation());
                        break;
                    case ENTROPY_NORMALIZED:
                        valsUtil = new UtilityMeasureNonUniformEntropyWithLowerBoundNormalized<Double>(header, inputArray, hierarchies, AggregateFunction.SUM).evaluateAggregatable(outputArray, arxNode.getTransformation());
                        break;
                    case ENTROPY:
                        valsUtil = new UtilityMeasureNonUniformEntropyWithLowerBound<Double>(header, inputArray, hierarchies, AggregateFunction.SUM).evaluateAggregatable(outputArray, arxNode.getTransformation());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown Type: " + type.toString());
                    }
                    
                    if (valsFlash.length != valsUtil.length) {
                        Assert.fail("Length mismatch. Should be: " + valsFlash.length + " Is: " + valsUtil.length);
                    }
                    for (int i = 0; i < valsFlash.length; i++) {
                        Assert.assertEquals("Transformation: " + Arrays.toString(arxNode.getTransformation()) + " -anonymity: " + arxNode.getAnonymity(), valsFlash[i], valsUtil[i], 0.0000001d);
                    }
                    
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Type: " + type.toString());
                }
            }
        }
    }
    
    private ARXConfiguration getConfiguration(Metric<?> metric) {
        ARXConfiguration config = ARXConfiguration.create();
        config.setQualityModel(metric);
        config.setMaxOutliers(0d);
        config.addPrivacyModel(new KAnonymity(5));
        return config;
    }
    
    @Test
    public void testAECS() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createAECSMetric());
        doComparison(config, UtilityMeasure.AECS);
    }
    
    @Test
    public void testAmbiguity() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createAmbiguityMetric());
        doComparison(config, UtilityMeasure.AMBIGUITY_METRIC);
    }
    
    @Test
    public void testDiscernability() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createDiscernabilityMetric());
        doComparison(config, UtilityMeasure.DISCERNABILITY);
    }

    @Test
    public void testGenericEntropy() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createEntropyMetric(false));
        doComparison(config, UtilityMeasure.GENERIC_ENTROPY);
    }
    
    @Test
    public void testEntropy() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createEntropyMetric(false));
        doComparison(config, UtilityMeasure.ENTROPY);
    }
    
    @Test
    public void testEntropyNormalized() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createNormalizedEntropyMetric());
        doComparison(config, UtilityMeasure.ENTROPY_NORMALIZED);
    }
    
    @Test
    public void testKLDivergence() throws IOException {
        
        // config
        ARXConfiguration config = getConfiguration(Metric.createKLDivergenceMetric());
        doComparison(config, UtilityMeasure.KL_DIVERGENCE);
    }
    
    @Test
    public void testLoss() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createLossMetric());
        doComparison(config, UtilityMeasure.LOSS);
    }
    
    @Test
    public void testPrecision() throws IOException {
        // config
        ARXConfiguration config = getConfiguration(Metric.createPrecisionMetric());
        doComparison(config, UtilityMeasure.PRECISION);
    }
    
}
