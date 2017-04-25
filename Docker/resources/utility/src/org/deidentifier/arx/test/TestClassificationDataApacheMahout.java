package org.deidentifier.arx.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.deidentifier.arx.test.TestData.Dataset;

public class TestClassificationDataApacheMahout {
    
    private static final Map<Dataset, Map<String, Double>> MINIMA = new HashMap<>();
    private static final Map<Dataset, Map<String, Double>> MAXIMA = new HashMap<>();
    
    static {
        
        initBounds(Dataset.ADULT, "marital-status", 0.46640141467727675, 0.6365679339817271);
        initBounds(Dataset.ADULT, "occupation", 0.12072649572649573, 0.21728558797524314);
        initBounds(Dataset.ADULT, "age", 0.022730621868552903, 0.042035072207486);
        initBounds(Dataset.ADULT, "education", 0.3240495137046861, 0.338491010904804);
        initBounds(Dataset.ADULT, "sex", 0.6767978190391983, 0.6813660477453581);
        initBounds(Dataset.ADULT, "native-country", 0.9101458885941645, 0.9101458885941645);
        initBounds(Dataset.ADULT, "race", 0.8593059239610964, 0.8593059239610964);
        initBounds(Dataset.ADULT, "workclass", 0.7403846153846154, 0.7399056881815502);
        initBounds(Dataset.ADULT, "salary-class", 0.7516209843796051, 0.7498894783377542);
        
        initBounds(Dataset.FARS, "irace", 0.5714851877538888, 0.8219337934982441);
        initBounds(Dataset.FARS, "ihispanic", 0.5714851877538888, 0.7690808811387431);
        initBounds(Dataset.FARS, "iinjury", 0.42851481224611115, 0.5976639475103758);
        initBounds(Dataset.FARS, "istatenum", 0.1076653786452657, 0.1219987450047888);
        initBounds(Dataset.FARS, "iage", 0.032112465184891616, 0.04120567610114821);
        initBounds(Dataset.FARS, "isex", 0.6555588581745324, 0.6555918843643009);
        initBounds(Dataset.FARS, "ideathmon", 0.5714851877538888, 0.5714851877538888);
        initBounds(Dataset.FARS, "ideathday", 0.5714851877538888, 0.5714851877538888);
        
        initBounds(Dataset.CUP, "STATE", 0.1679977581616926, 0.22225725094577553);
        initBounds(Dataset.CUP, "NGIFTALL", 0.09748493764887207, 0.12964130587081407);
        initBounds(Dataset.CUP, "RAMNTALL", 0.037498248563822334, 0.05135210872915791);
        initBounds(Dataset.CUP, "INCOME", 0.21404301527252348, 0.2243414599971977);
        initBounds(Dataset.CUP, "AGE", 0.024660221381532857, 0.030404932044276307);
        initBounds(Dataset.CUP, "ZIP", 2.627154266498529E-4, 4.90402129746392E-4);
        initBounds(Dataset.CUP, "MINRAMNT", 0.37039372285273925, 0.37048129466162255);
        initBounds(Dataset.CUP, "GENDER", 0.5453972257250945, 0.5276201485217878);
        
        initBounds(Dataset.ATUS, "Sex", 0.48173285942409727, 0.7163838664812239);
        initBounds(Dataset.ATUS, "Marital status", 0.38376345747694846, 0.4792541080719106);
        initBounds(Dataset.ATUS, "Highest level of school completed", 0.2762210889610055, 0.3391892031113172);
        initBounds(Dataset.ATUS, "Citizenship status", 0.8503971565445835, 0.8839025395353629);
        initBounds(Dataset.ATUS, "Birthplace", 0.8503621284706124, 0.8715479317982795);
        initBounds(Dataset.ATUS, "Age", 0.48173285942409727, 0.4930180806675939);
        initBounds(Dataset.ATUS, "Labor force status", 0.817060732498841, 0.8193870087055066);
        initBounds(Dataset.ATUS, "Region", 0.3608241899757894, 0.36164013805182094);
        initBounds(Dataset.ATUS, "Race", 0.7798217689177356, 0.7787420800494514);
        
        initBounds(Dataset.IHIS, "MARSTAT", 0.2360117302052786, 0.4168477400735465);
        initBounds(Dataset.IHIS, "EDUC", 0.19165758972210586, 0.2294074384396965);
        initBounds(Dataset.IHIS, "AGE", 0.017556207233626588, 0.04368105013266304);
        initBounds(Dataset.IHIS, "PERNUM", 0.38672997253642416, 0.40355350742447516);
        initBounds(Dataset.IHIS, "YEAR", 0.08436438113857468, 0.09323278871665969);
        initBounds(Dataset.IHIS, "SEX", 0.5167434715821813, 0.5215686822138436);
        initBounds(Dataset.IHIS, "QUARTER", 0.2624586882651399, 0.26349671833542804);
        initBounds(Dataset.IHIS, "RACEA", 0.7697295536005213, 0.7697295536005213);
        initBounds(Dataset.IHIS, "REGION", 0.3596555415910255, 0.35596425080296046);
        
        initBounds(Dataset.SS13ACS, "Workclass", 0.4004333203983961, 0.5524511706118226);
        initBounds(Dataset.SS13ACS, "Marital status", 0.4400303971025741, 0.5824602250679084);
        initBounds(Dataset.SS13ACS, "Age", 0.01710645453369551, 0.041230112533954213);
        initBounds(Dataset.SS13ACS, "Education", 0.1758828094683741, 0.19843810632518433);
        initBounds(Dataset.SS13ACS, "Sex", 0.5198712973742078, 0.5249967662656836);
        initBounds(Dataset.SS13ACS, "Citizenship", 0.8397361272797826, 0.8397361272797826);
        initBounds(Dataset.SS13ACS, "Weight", 0.016944767817876082, 0.016960936489458026);
        initBounds(Dataset.SS13ACS, "Relationship", 0.3842484801448713, 0.3815321433191049);
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
