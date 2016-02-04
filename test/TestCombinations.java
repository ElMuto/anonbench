import org.junit.*;
import java.util.*;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.execution.CalculateClassificationAccuracies;

public class TestCombinations {
	
	@Test
    public void test0Element() {
		String[] testString = new String[] {  };
		Set<String> testList = new HashSet<>(Arrays.asList(testString));
		
		Set<Set<String>> powerSet = CalculateClassificationAccuracies.getLimitedPowerset(testList, 1);
		
		Assert.assertEquals(1, powerSet.size());
    }
	
	@Test
    public void test1Element() {
		String[] testString = new String[] { "A" };
		Set<String> testList = new HashSet<>(Arrays.asList(testString));
		
		Set<Set<String>> powerSet = CalculateClassificationAccuracies.getLimitedPowerset(testList, 1);
		
		Assert.assertEquals(powOf2(testString.length) - 1, powerSet.size());
    }
	
	@Test
    public void testCombo9Elements() {
		String[] testString = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
		Set<String> testList = new HashSet<>(Arrays.asList(testString));
		
		Set<Set<String>> powerSet = CalculateClassificationAccuracies.getLimitedPowerset(testList, 9);
		
		Assert.assertEquals(powOf2(testString.length) - 1, powerSet.size());
    }
	
	@Test
    public void testComboFullAdult() {
		String[] testString = BenchmarkSetup.getAllAttributes(BenchmarkDatafile.ADULT);
		Set<String> testList = new HashSet<>(Arrays.asList(testString));
		
		Set<Set<String>> powerSet = CalculateClassificationAccuracies.getLimitedPowerset(testList, 8);
		
//		for (Set<String> subset : powerSet) {
//			System.out.println(subset);
//		}
		
		Assert.assertEquals(powOf2(testString.length) - 1, powerSet.size());
    }
	
	@Test
    public void testLimitedAdult() {
		String[] testString = BenchmarkSetup.getAllAttributes(BenchmarkDatafile.ADULT);
		Set<String> testList = new HashSet<>(Arrays.asList(testString));
		
		Set<Set<String>> powerSet = CalculateClassificationAccuracies.getLimitedPowerset(testList, 4);
		
//		for (Set<String> subset : powerSet) {
//			System.out.println(subset);
//		}
		
		Assert.assertEquals(binCoeff(8, 4) + binCoeff(8, 3) + binCoeff(8, 2) + binCoeff(8, 1), powerSet.size());
    }
	
	@Test
	public void testMainLoop() {
		String[] testAttributesArray = BenchmarkSetup.getAllAttributes(BenchmarkDatafile.ADULT);
		Set<String> testAttributes = new HashSet<>(Arrays.asList(testAttributesArray));

		int i = 0;
		for (String classAttribute : testAttributes) {
			Set<String> restAttributes = new HashSet<>(testAttributes);
			restAttributes.remove(classAttribute);

			Set<Set<String>> limitedPowerSet = CalculateClassificationAccuracies.getLimitedPowerset(restAttributes, 4);
			for (Set<String> features : limitedPowerSet) {
				// classify

				System.out.println("Class = " + classAttribute + ", features = " + features);

				i++;
			}
		}
		
		Assert.assertEquals(8 * (binCoeff(7, 4) + binCoeff(7, 3) + binCoeff(7, 2) + binCoeff(7, 1)), i);
	}
	
	@Test
    public void testBinCoeff4over2() {		
		Assert.assertEquals(6, binCoeff(4, 2));
    }
	
	@Test
    public void testBinCoeff8over7() {		
		Assert.assertEquals(8, binCoeff(8, 7));
    }
	
	@Test
    public void testBinCoeff8over1() {		
		Assert.assertEquals(8, binCoeff(8, 1));
    }
	
	@Test
    public void testBinCoeff8over8() {		
		Assert.assertEquals(1, binCoeff(8, 8));
    }
	
	@Test
    public void testBinCoeff8over0() {		
		Assert.assertEquals(1, binCoeff(8, 0));
    }
	
	private long powOf2(int x) {
		if (x < 0) throw new IllegalArgumentException("Invalid value for x: " + x);
		
		long result = 1;
		for (int i = 0; i < x; i++) {
			result *= 2;
		}
		return result;
	}
	
	private long fac(long x) {
		if (x < 0) throw new IllegalArgumentException("Invalid value for x: " + x);
		
		long result = 1;
		
		for (int i = 1; i <= x; i++) {
			result *= i;
		}
		return result;
	}
	
	private long binCoeff(long n, long k) {
		if (n < 0 || k < 0 || k > n) throw new IllegalArgumentException("Invalid values for n/k. n: " + n + ", k: " + k);
		
		return (fac(n) / (fac(k) * fac(n - k)));
	}
}
