package org.deidentifier.arx.benchmark.spd;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides functions for distance calculation. Adopted for ARX.
 * 
 * @author Sergio Martínez (Universitat Rovira i Virgili)
 */
public class SoriaComasDistances {
    
    class SoriaComasWrapper {
        
        private int hashCode;
        private String[] record;
        
        public SoriaComasWrapper(String[] record) {
            this.record = record;
            this.hashCode = Arrays.hashCode(record);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            SoriaComasWrapper other = (SoriaComasWrapper) obj;
            if (hashCode != other.hashCode) return false;
            if (!Arrays.equals(record, other.record)) return false;
            return true;
        }
    }
	
    /** Typical dev*/
	private double[] typicalDev;
	/** Max*/
	private double max[];
	/** Min*/
	private double min[];
	/** Num attr*/
	private int numAttr;
	/** Max euclidean distance*/
    private double maxEuclideanDistance;
	
	/**
	 * Constructor
	 * @param input
	 */
	public SoriaComasDistances(List<String[]> input) {
	    initialize(input);
	}
	
	/**
	 * This function calculates the euclidean distance between a record
	 * and a generalized record.
	 * 
	 * @author Sergio Martínez (Universitat Rovira i Virgili)
	 * @throws ParseException 
	 */
	public double euclideanDistNorm(String[] c1, String[] c2) throws ParseException{
		double dis, partial, partial1, cn1, cn2;
		double min, max;
		
		dis = 0;
		partial = 0;
		for(int i=0; i<getNumAttributes(); i++){
			
		    cn1 = Double.parseDouble(c1[i]);
		    double[] minmax = parse(c2[i], i);
			min = minmax[0];
			max = minmax[1];
			if((max - cn1) > (cn1 - min)){
				cn2 = max;
			}
			else{
				cn2 = min;
			}
			if(typicalDev[i] == 0.0){
				partial1 = 0.0;
			}
			else{
				partial1 = (cn1-cn2) / typicalDev[i];
			}
			partial += (partial1 * partial1);
		}
		dis = Math.sqrt(partial);
		return dis;
	}
	

    /**
     * This function calculates the granularity of a record.
     * 
     * @throws ParseException 
     */
    public double granularity(String[] c2) throws ParseException{
        double result = 0d;
        for(int i=0; i<getNumAttributes(); i++){
            
            double[] minmax = parse(c2[i], i);
            double record = (minmax[1] - minmax[0]) / (max[i] - min[i]);
            result += record;
        }
        return result;
    }

    /**
     * This function calculates the granularity of a record.
     * 
     * @throws ParseException 
     */
    public double averageClassSize(List<String[]> data) throws ParseException{
        
        Map<SoriaComasWrapper, Integer> classes = new HashMap<>();
        for (String[] record : data) {
            SoriaComasWrapper wrapper = new SoriaComasWrapper(record);
            Integer count = classes.get(wrapper);
            classes.put(wrapper, count == null ? 1 : count + 1);
        }
        return (double)data.size() / (double)classes.size();
    }
    
	
	/**
	 * Returns the number of attributes
	 * @return
	 */
	public double getNumAttributes() {
        return this.numAttr;
    }

    /**
	 * This function calculates the typical deviation of an attribute
	 * 
	 * @author Sergio Martínez (Universitat Rovira i Virgili)
	 */
	private double calculateTypicalDeviation(double var[]){
	    
		double typicalDev, medianVar, partial;
		
		medianVar = 0;
		for(int i=0; i<var.length; i++){
			medianVar += var[i];
		}
		medianVar /= var.length;
		
		typicalDev = 0;
		for(int i=0; i<var.length; i++){
			partial = var[i] - medianVar;
			partial = partial * partial;
			typicalDev += partial;
		}
		typicalDev /= (var.length - 1);
		typicalDev = Math.sqrt(typicalDev);
		
		return typicalDev;
	}
	
	/**
	 * This function calculates the typical deviation of all attributes
	 * 
	 * @author Sergio Martínez (Universitat Rovira i Virgili)
	 */
	private void initialize(List<String[]> input){
		
	    // Basic stuff
	    numAttr = input.get(0).length;
	    typicalDev = new double[numAttr];
        max = new double[numAttr];
        min = new double[numAttr];
        
        // Min, max, dev
		for(int i=0; i<numAttr; i++){
		    double [] var = new double[input.size()];
			max[i] = Double.parseDouble(input.get(0)[i]);
			min[i] = Double.parseDouble(input.get(0)[i]);
			for(int j=0; j<input.size(); j++){
				var[j] = Double.parseDouble(input.get(j)[i]);
				if(var[j] > max[i]){
					max[i] = var[j];
				}
				if(var[j] < min[i]){
					min[i] = var[j];
				}
			}
			typicalDev[i] = calculateTypicalDeviation(var);
		}
	
		// Max distance
		this.maxEuclideanDistance = 0d;
		for (String[] record : input) {
		    double partial = 0d;
            for(int i=0; i<getNumAttributes(); i++){
                double cn1 = Double.parseDouble(record[i]);
                double cn2 = 0d;
                double min = this.min[i];
                double max = this.max[i];
                if((max - cn1) > (cn1 - min)){
                    cn2 = max;
                }
                else{
                    cn2 = min;
                }
                double partial1;
                if(typicalDev[i] == 0.0){
                    partial1 = 0.0;
                }
                else{
                    partial1 = (cn1-cn2) / typicalDev[i];
                }
                partial += (partial1 * partial1);
            }
            this.maxEuclideanDistance += Math.sqrt(partial);
		}
        this.maxEuclideanDistance /= getNumAttributes();
        this.maxEuclideanDistance /= input.size();
	}
	
	public double getMaxEuclideanDistance() {
	    return this.maxEuclideanDistance;
	}

    /**
	 * Parses different forms of transformed values
	 * @param value
	 * @return
	 * @throws ParseException 
	 */
	private double[] parse(String value, int column) throws ParseException {

	    double min, max;
	    
	    // Suppressed
	    if (value.equals("*")) {
	        min = this.min[column];
	        max = this.max[column];
	        
	    // Masked
	    } else if (value.contains("*")) {
	        min = Double.valueOf(value.replace('*', '0'));
	        max = Double.valueOf(value.replace('*', '9'));
	        
	    // Interval from ARX
	    } else if (value.startsWith("[") && value.endsWith("[")) {
            min = Double.valueOf(value.substring(1, value.indexOf(",")).trim());
            max = Double.valueOf(value.substring(value.indexOf(",") + 1, value.length() - 1).trim()) - 1d;

        // Interval from Crises
        } else if (value.startsWith("[") && value.endsWith("]")) {
            min = Double.valueOf(value.substring(1, value.indexOf(";")).trim());
            max = Double.valueOf(value.substring(value.indexOf(";") + 1, value.length() - 1).trim());

        // Set from ARX - TODO: ARX should not use sets! 
        } else if (value.startsWith("{") && value.endsWith("}")) {
            
            min = Double.MAX_VALUE;
            max = -Double.MAX_VALUE;
            value = value.replace('{', ' ').replace('}', ' ');
            for (String part : value.split(",")) {
                part = part.trim();
                if (!part.equals("")) {
                    min = Math.min(min, Double.valueOf(part));
                    max = Math.max(max, Double.valueOf(part));
                }
            }
            
        // Ungeneralized
        } else if (containsOnlyDigits(value)){
            min = Double.valueOf(value);
            max = Double.valueOf(value);
        
        // Check
        } else {
            throw new ParseException("Cannot parse: " + value, 0);
        }
	    
	    // Return
	    return new double[]{min, max};
    }

	/**
	 * Returns whether the string contains only digits
	 * @param value
	 * @return
	 */
    private boolean containsOnlyDigits(String value) {
        char[] chars = value.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
