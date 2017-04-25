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

package org.deidentifier.arx.example;

import java.util.Map;

import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.utility.AggregateFunction;
import org.deidentifier.arx.utility.DataConverter;
import org.deidentifier.arx.utility.UtilityMeasureAECS;
import org.deidentifier.arx.utility.UtilityMeasureDiscernibility;
import org.deidentifier.arx.utility.UtilityMeasureLoss;
import org.deidentifier.arx.utility.UtilityMeasureNonUniformEntropy;
import org.deidentifier.arx.utility.UtilityMeasurePrecision;

public class ExampleSDGS {

    public static void main(String[] args) {
        
        // ARX Stuff
        DataDefinition definition = null;
        ARXResult result = null; // TODO
        DataHandle inputHandle = null; // TODO
        DataHandle outputHandle = result.getOutput();
        
        // Prepare
        DataConverter converter = new DataConverter();
        String[][] input = converter.toArray(inputHandle);
        String[][] output = converter.toArray(outputHandle, outputHandle.getDefinition(), outputHandle.getView());
        Map<String, String[][]> hierarchies = converter.toMap(definition);
        String[] header = converter.getHeader(inputHandle);

        // Compute for output
        double outputAECS = new UtilityMeasureAECS().evaluate(output).getUtility();
        double outputDiscernibility = new UtilityMeasureDiscernibility().evaluate(output).getUtility();
        double outputLoss = new UtilityMeasureLoss<Double>(header, hierarchies, AggregateFunction.GEOMETRIC_MEAN).evaluate(output).getUtility();
        double outputEntropy = new UtilityMeasureNonUniformEntropy<Double>(header, input).evaluate(output).getUtility();
        double outputPrecision = new UtilityMeasurePrecision<Double>(header, hierarchies).evaluate(output).getUtility();

        // Compute for input
        double inputAECS = new UtilityMeasureAECS().evaluate(input).getUtility();
        double inputDiscernibility = new UtilityMeasureDiscernibility().evaluate(input).getUtility();
        double inputLoss = new UtilityMeasureLoss<Double>(header, hierarchies, AggregateFunction.GEOMETRIC_MEAN).evaluate(input).getUtility();
        double inputEntropy = new UtilityMeasureNonUniformEntropy<Double>(header, input).evaluate(input).getUtility();
        double inputPrecision = new UtilityMeasurePrecision<Double>(header, hierarchies).evaluate(input).getUtility();
    }
}
