package org.deidentifier.arx.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Variation of the Classification metric which assigns a penalty of 1/5 to suppressed records
 * 
 * @author Raffael Bild
 * @author Johanna Eicher
 */
public class UtilityMeasureClassificationSuppressedHalfPenalty extends UtilityMeasure<Double> {

    /** Class attribute values */
    private String[] classValues;
    /** Class attribute */
    private String classAttribute;
    /** Header */
    private String[] header;
    /** Penalty factor for suppressed records */
    private double suppPenaltyFactor;

    /**
     * Constructor
     * @param header
     * @param classAttribute
     * @param classValues
     */
    public UtilityMeasureClassificationSuppressedHalfPenalty(String[] header, String classAttribute, String[] classValues) {
        this.classValues = classValues;
        this.header = header;
        this.classAttribute = classAttribute;
        this.suppPenaltyFactor = 0.5d;
    }
    
    @Override
    public Utility<Double> evaluate(String[][] data, int[] transformation) {
        Map<String, Map<String, Integer>> featuresToClassToCount = new HashMap<>();
        
        int suppressedCount = 0;
        
        for (int index = 0; index < data.length; index++) {

            String[] row = data[index];
            
            if(isSuppressed(row)) {
                suppressedCount++;
                continue;
            }
            
            String classValue = classValues[index];
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < row.length; i++) {
                if (!header[i].equals(classAttribute)) {
                    list.add(row[i]);
                }
            }
            String[] features = list.toArray(new String[list.size()]);
            
            String featureString = Arrays.toString(features);
            
            Map<String, Integer> classToCount = featuresToClassToCount.get(featureString);
            if (classToCount == null) {
                classToCount = new HashMap<>();
                classToCount.put(classValue, 1);
            } else {
                int count = classToCount.containsKey(classValue) ? classToCount.get(classValue) + 1 : 1;
                classToCount.put(classValue, count);
            }
            featuresToClassToCount.put(featureString, classToCount);
        }
        
        int unpenalizedCount = 0;
        for (Map<String, Integer> classToCount : featuresToClassToCount.values()) {
            int maxCount = 0;
            for (int count : classToCount.values()) {
                maxCount = Math.max(maxCount, count);
            }
            unpenalizedCount += maxCount;
        }

        double nonSuppPenalty = (double)(data.length - suppressedCount - unpenalizedCount);
        double suppPenalty = (double)suppressedCount * suppPenaltyFactor;
        
        return new UtilityDouble((suppPenalty + nonSuppPenalty) / (double)data.length);
    }

    private boolean isSuppressed(String[] row) {
        for (int i = 0; i < row.length; i++) {
            if (!row[i].equals("*")) {
                return false;
            }
        }
        return true;
    }
}
