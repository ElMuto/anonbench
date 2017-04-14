package org.deidentifier.arx.testsuite;

import org.deidentifier.arx.testcase.TestAggregatedBetaValues;
import org.deidentifier.arx.testcase.TestMaxDelta;
import org.deidentifier.arx.testcase.TestNPE_ACS13_lr;
import org.deidentifier.arx.testcase.TestNormalization;
import org.deidentifier.arx.testcase.TestWeightedAvg;
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
//	TestAttributeStatistics.class,
})
public class Shortrunners {

}
