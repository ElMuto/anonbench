package org.deidentifier.arx.testcase.classification;

import static org.junit.Assert.*;

import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.util.Classificator;
import org.junit.Test;

public class TestBaseCAs {

	private String[] cAs;

	@Test
	public void CensusMs() {

		cAs = Classificator.getBaseCAs(BenchmarkDatafile.ACS13, "Marital status", false);
		assertEquals("0,4461", cAs[0]);
		assertEquals("0,7497", cAs[1]);
		assertEquals("0,3036", cAs[2]);
	}

	@Test
	public void CensusEd() {

		cAs = Classificator.getBaseCAs(BenchmarkDatafile.ACS13, "Education", false);
		assertEquals("0,1758", cAs[0]);
		assertEquals("0,3584", cAs[1]);
		assertEquals("0,1825", cAs[2]);
	}

	@Test
	public void TimeUseMs() {

		cAs = Classificator.getBaseCAs(BenchmarkDatafile.ATUS, "Marital status", false);
		assertEquals("0,3841", cAs[0]);
		assertEquals("0,5987", cAs[1]);
		assertEquals("0,2145", cAs[2]);

	}

	@Test
	public void TimeUseEd() {

		cAs = Classificator.getBaseCAs(BenchmarkDatafile.ATUS, "Highest level of school completed", false);
		assertEquals("0,2763", cAs[0]);
		assertEquals("0,3830", cAs[1]);
		assertEquals("0,1066", cAs[2]);

	}

	@Test
	public void HealthInterviewMs() {

		cAs = Classificator.getBaseCAs(BenchmarkDatafile.IHIS, "MARSTAT", false);
		assertEquals("0,2362", cAs[0]);
		assertEquals("0,6114", cAs[1]);
		assertEquals("0,3752", cAs[2]);

	}

	@Test
	public void HealthInterviewEd() {

		cAs = Classificator.getBaseCAs(BenchmarkDatafile.IHIS, "EDUC", false);
		assertEquals("0,1918", cAs[0]);
		assertEquals("0,3956", cAs[1]);
		assertEquals("0,2038", cAs[2]);

	}
}
