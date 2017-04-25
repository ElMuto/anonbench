package org.deidentifier.arx.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Classification metric, as proposed in:<br>
 * <br>
 * Vijay S. Iyengar, Transforming Data to Satisfy Privacy Constraints, in: International Conference on Knowledge Discovery and Data Mining (ACM), 2002, pp. 279-288
 * 
 * @author Raffael Bild
 * @author Johanna Eicher
 */
public class UtilityMeasureClassification extends UtilityMeasure<Double> {

    /** Class attribute values */
    private String[] classValues;
    /** Class attribute */
    private String classAttribute;
    /** Header */
    private String[] header;

    /**
     * Constructor
     * @param header
     * @param classAttribute
     * @param classValues
     */
    public UtilityMeasureClassification(String[] header, String classAttribute, String[] classValues) {
        this.classValues = classValues;
        this.header = header;
        this.classAttribute = classAttribute;
    }
    
    @Override
    public Utility<Double> evaluate(String[][] data, int[] transformation) {
        Map<String, Map<String, Integer>> featuresToClassToCount = new HashMap<>();
        
        for (int index = 0; index < data.length; index++) {

            String[] row = data[index];
            boolean suppressed = isSuppressed(row);
            String classValue = classValues[index];
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < row.length; i++) {
                if (!header[i].equals(classAttribute)) {
                    list.add(row[i]);
                }
            }
            String[] features = list.toArray(new String[list.size()]);
            
            if (suppressed) continue;
            
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
        
        int penalizedCount = data.length - unpenalizedCount;
        
        return new UtilityDouble(penalizedCount / (double) data.length);
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
