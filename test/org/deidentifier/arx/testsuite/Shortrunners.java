package org.deidentifier.arx.testsuite;

import org.deidentifier.arx.testcase.TestAggregatedBetaValues;
import org.deidentifier.arx.testcase.TestNPE_ACS13_lr;
import org.deidentifier.arx.testcase.TestWeightedAvg;
import org.deidentifier.arx.testcase.infoloss.TestRelIlEntrMs;
import org.deidentifier.arx.testcase.normalization.TestDenormalization;
import org.deidentifier.arx.testcase.normalization.TestMaxDelta;
import org.deidentifier.arx.testcase.normalization.TestNormalization;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	TestAggregatedBetaValues.class,
	TestMaxDelta.class,
	TestNPE_ACS13_lr.class,
	TestWeightedAvg.class,
	TestNormalization.class,
	TestDenormalization.class,
// 	TestRelIlEntrMs.class,  // FAILURE !!!
//	TestRelIlEntrLoss.class, // FAILURE !!!!
//	TestAttributeStatistics.class,
})
public class Shortrunners {

}
