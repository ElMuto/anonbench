import org.junit.*;
import java.util.*;

import org.deidentifier.arx.execution.CalculateClassificationAccuracies;

public class TestCombinations {
	
	
	
	@Test
    public void testComboSingleElement() {
		String[] testString = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
		Set<String> testList = new HashSet<>(Arrays.asList(testString));
		
		Set<Set<String>> powerSet = CalculateClassificationAccuracies.getLimitedPowerset(testList, 10);
		
		for (Set<String> subset : powerSet) {
	        System.out.println(subset.toString());
		}
		Assert.assertEquals(powOf2(testString.length) - 1, powerSet.size());
    }
	
	private long powOf2(int x) {
		long result = 1;
		for (int i = 0; i < x; i++) {
			result *= 2;
		}
		return result;
	}
}
