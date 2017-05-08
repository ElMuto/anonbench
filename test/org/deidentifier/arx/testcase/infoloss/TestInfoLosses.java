package org.deidentifier.arx.testcase.infoloss;

import static org.junit.Assert.*;

import org.deidentifier.arx.BenchmarkDataset;
import org.deidentifier.arx.BenchmarkSetup;
import org.deidentifier.arx.ParametrizationSetup;
import org.deidentifier.arx.PrivacyModel;
import org.deidentifier.arx.BenchmarkDataset.BenchmarkDatafile;
import org.deidentifier.arx.BenchmarkSetup.BenchmarkMeasure;
import org.junit.Test;

public class TestInfoLosses {

	private double epsilon = 1e-5;

	@Test
	public void testEntropyAcs13() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		
		double[] refErgs = new double[] {
				0.450259163,
				0.966260182,
				1d,
				0.735744226,
				0.920076835,
				0d,
				0.258093042,
				0.219581387,
				0.718663005,
				0.820363306,
		};		
		loopIt(measure, datafile, refErgs);
	}
	
	
	@Test
	public void testEntropyAtus() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.ATUS;
		
		double[] refErgs = new double[] {
				0.14080508536062,
				0.597893488278104,
				0.598053097657114,
				0.614498462901771,
				1,
				0.138984079645902,
				0.359915176174131,
				0.359909841179933,
				0.59358800488547,
				0.78113039139,

		};		
		loopIt(measure, datafile, refErgs);
	}
	
	
	@Test
	public void testEntropyIhis() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.ENTROPY;
		BenchmarkDatafile datafile = BenchmarkDatafile.IHIS;
		
		double[] refErgs = new double[] {
				0.49603,
				0.73672,
				0.73672,
				0.72379,
				1d,
				0.383562854260636,
				0.383562854260636,
				0.383562854260636,
				0.865146900839108,
				0.88347617178401
		};		
		loopIt(measure, datafile, refErgs);
	}

	@Test
	public void testLossAcs13() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.LOSS;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		
		double[] refErgs = new double[] {
				0.110379438793558,
				0.938956402613103,
				1d,
				0.296855026134502,
				0.664545944308246,
				0.0161480174686405,
				0.0219447953360598,
				0.0327193771387656,
				0.313470701623555,
				0.576403509016949
		};		
		loopIt(measure, datafile, refErgs);
	}
	
	
	@Test
	public void testLossAtus() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.LOSS;
		BenchmarkDatafile datafile = BenchmarkDatafile.ATUS;
		
		double[] refErgs = new double[] {
				0.0411573544704593,
				0.276825320808697,
				0.277721031667005,
				0.453896173227794,
				1d,
				0.0415727792716253,
				0.0758242987672024,
				0.0758374673730668,
				0.293701428270754,
				0.58740105196895
		};		
		loopIt(measure, datafile, refErgs);
	}
	
	
	@Test
	public void testLossIhis() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.LOSS;
		BenchmarkDatafile datafile = BenchmarkDatafile.IHIS;
		
		double[] refErgs = new double[] {
				0.0603368694725842,
				0.25992104989477,
				0.25992104989477,
				0.28818332090059,
				1d,
				0.0294472343858977,
				0.0294472343858977,
				0.0294472343858977,
				0.58740105196822,
				0.58740105196822
		};		
		loopIt(measure, datafile, refErgs);
	}

	@Test
	public void testSseAcs13() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.SORIA_COMAS;
		BenchmarkDatafile datafile = BenchmarkDatafile.ACS13;
		
		double[] refErgs = new double[] {
				0.12998,
				0.97095,
				1d,
				0.43911,
				0.56391,
				0.02191,
				0.02687,
				0.04514,
				0.43993,
				0.54755
		};		
		loopIt(measure, datafile, refErgs);
	}
	
	
	@Test
	public void testSseAtus() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.SORIA_COMAS;
		BenchmarkDatafile datafile = BenchmarkDatafile.ATUS;
		
		double[] refErgs = new double[] {
				0.0451,
				0.3262,
				0.32686,
				0.43619,
				1d,
				0.04574,
				0.10748,
				0.10749,
				0.34671,
				0.95826

		};		
		loopIt(measure, datafile, refErgs);
	}
	
	
	@Test
	public void testSseIhis() {
		
		BenchmarkMeasure measure = BenchmarkMeasure.SORIA_COMAS;
		BenchmarkDatafile datafile = BenchmarkDatafile.IHIS;
		
		double[] refErgs = new double[] {
				0.09047,
				0.41604,
				0.41604,
				0.43197,
				1d,
				0.04668,
				0.04668,
				0.04668,
				0.52901,
				0.95486,
		};		
		loopIt(measure, datafile, refErgs);
	}

	private void loopIt(BenchmarkMeasure measure, BenchmarkDatafile datafile, double[] refErgs) {
		int i = 0;

		// for each sensitive attribute candidate
		for (String sa : BenchmarkDataset.getSensitiveAttributeCandidates(datafile)) {

			// for each privacy model
			for (PrivacyModel privacyModel : BenchmarkSetup.getPrivacyModelsCombinedWithK()) {

				ParametrizationSetup pSetup =  new ParametrizationSetup(
						datafile,
						sa, 5, PrivacyModel.getDefaultParam2(privacyModel.getCriterion()), privacyModel.getCriterion(), measure, 0.05);
				pSetup.anonymize();
				double il = -1d;
				switch (measure) {
				case ENTROPY:
					il = pSetup.getAnonymizer().getIlRelEntr();
					break;
				case LOSS:
					il = pSetup.getAnonymizer().getIlRelLoss();
					break;
				case SORIA_COMAS:
					il = pSetup.getAnonymizer().getIlSorCom();
					break;
				default:
					throw new RuntimeException("Invalid measure: " + measure);

				}
				assertEquals(refErgs[i++], il, epsilon);
			}
		}
	}
}
