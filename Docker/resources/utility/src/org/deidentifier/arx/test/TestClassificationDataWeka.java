package org.deidentifier.arx.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.deidentifier.arx.test.TestData.Dataset;

public class TestClassificationDataWeka {
    
    private static final Map<Dataset, Map<String, Double>> MINIMA = new HashMap<>();
    private static final Map<Dataset, Map<String, Double>> MAXIMA = new HashMap<>();
    
    static {
        
        initBounds(Dataset.ADULT, "sex", 67.56846362973278, 79.64657516079835);
        initBounds(Dataset.ADULT, "race", 85.97904648232876, 88.22359259996021);
        initBounds(Dataset.ADULT, "marital-status", 46.63152310854718, 69.21291691532392);
        initBounds(Dataset.ADULT, "education", 32.62383131092103, 41.333465950533785);
        initBounds(Dataset.ADULT, "native-country", 91.18758703003779, 91.71142497181884);
        initBounds(Dataset.ADULT, "workclass", 73.88767323121809, 74.90882567469001);
        initBounds(Dataset.ADULT, "occupation", 13.387706385518202, 31.987268748756712);
        initBounds(Dataset.ADULT, "salary-class", 75.10775147536636, 82.806179961541);
        
        initBounds(Dataset.ATUS, "Region", 36.04152410834988d, 39.27525669769106d);
        initBounds(Dataset.ATUS, "Age", 48.171081106641964d, 52.79340124208859d);
        initBounds(Dataset.ATUS, "Sex", 48.171081106641964d, 76.94699890403948d);
        initBounds(Dataset.ATUS, "Race", 77.97471687686485d, 82.0258765366164d);
        initBounds(Dataset.ATUS, "Marital status", 38.41425082475202d, 74.68294103138972d);
        initBounds(Dataset.ATUS, "Citizenship status", 85.05247073266166d, 95.68940738391812d);
        initBounds(Dataset.ATUS, "Birthplace", 85.04839101497812d, 92.41172510862248d);
        initBounds(Dataset.ATUS, "Highest level of school completed", 27.634524054571788d, 49.34882142519374d);
        initBounds(Dataset.ATUS, "Labor force status", 81.6824384843478d, 84.15029679946147d);
        
        initBounds(Dataset.FARS, "irace", 57.21588713752143d, 93.24231946659798d);
        initBounds(Dataset.FARS, "ideathmon", 57.21588713752143d, 62.35275468856812d);
        initBounds(Dataset.FARS, "isex", 65.58249204949622d, 66.72478872960362d);
        initBounds(Dataset.FARS, "ihispanic", 57.21588713752143d, 94.19737063713009d);
        initBounds(Dataset.FARS, "istatenum", 10.80971298929035d, 12.864460009709026d);
        initBounds(Dataset.FARS, "iinjury", 42.78411286247857d, 65.79648691758226d);
        
        initBounds(Dataset.IHIS, "QUARTER", 26.24113534600638, 28.307068933158163);
        initBounds(Dataset.IHIS, "REGION", 35.95312625680349, 36.98688902592702);
        initBounds(Dataset.IHIS, "MARSTAT", 23.619527039708288, 77.88603976191115);
        initBounds(Dataset.IHIS, "SEX", 51.69148993216613, 60.80842628093412);
        initBounds(Dataset.IHIS, "RACEA", 76.97963308040862, 77.07724481861813);
        initBounds(Dataset.IHIS, "EDUC", 19.175805024532803, 38.23120827412392);
        
        initBounds(Dataset.SS13ACS, "Marital status", 44.611131320480176, 80.3259367042561);
        initBounds(Dataset.SS13ACS, "Relationship", 38.32229901782466, 64.31429610767552);
        initBounds(Dataset.SS13ACS, "Workclass", 40.027646416878866, 65.74317933794107);
        initBounds(Dataset.SS13ACS, "Education", 17.581666060385594, 35.30156420516551);
        initBounds(Dataset.SS13ACS, "Sex", 51.966533284830845, 61.038923244816296);
        initBounds(Dataset.SS13ACS, "Citizenship", 83.92579119679884, 84.2168061113132);
    }
    
    public static void main(String[] args) {
        
        for (Dataset key : MINIMA.keySet()) {
            System.out.println(key);
            for (String attribute : MINIMA.get(key).keySet()) {
                double range = MAXIMA.get(key).get(attribute) - MINIMA.get(key).get(attribute);
                System.out.println(" - " + attribute + " -> " + range);
            }
        }
        
    }
    
    /**
     * Returns the target attributes for the given dataset
     * @param dataset
     * @return
     */
    public static final String[] getAttributes(Dataset dataset) {
        Object[] objArray = MINIMA.get(dataset).keySet().toArray();
        return Arrays.copyOf(objArray, objArray.length, String[].class);
    }
    
    /**
     * Normalizes the resulting accuracy for the given dataset
     * @param dataset
     * @param accuracy
     * @param attribute
     * @return
     */
    public static final double getRelativeAccuracy(Dataset dataset, String attribute, double accuracy) {
        double minimum = MINIMA.get(dataset).get(attribute);
        double maximum = MAXIMA.get(dataset).get(attribute);
        return (accuracy - minimum) / (maximum - minimum);
    }
    
    /**
     * Stores bounds for a given dataset
     * @param dataset
     * @param attribute
     * @param minimum
     * @param maximum
     */
    private static void initBounds(Dataset dataset, String attribute, double minimum, double maximum) {
        if (!MAXIMA.containsKey(dataset)) {
            MAXIMA.put(dataset, new LinkedHashMap<String, Double>());
        }
        if (!MINIMA.containsKey(dataset)) {
            MINIMA.put(dataset, new LinkedHashMap<String, Double>());
        }
        
        MINIMA.get(dataset).put(attribute, minimum);
        MAXIMA.get(dataset).put(attribute, maximum);
    }
}
