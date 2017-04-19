package org.deidentifier.arx.testsuite;

import org.deidentifier.arx.testcase.TestAggregatedBetaValues;
import org.deidentifier.arx.testcase.TestAttributeStatistics;
import org.deidentifier.arx.testcase.TestFindOptimalTrafoForMinPrivGuarantee;
import org.deidentifier.arx.testcase.TestNPE_ACS13_lr;
import org.deidentifier.arx.testcase.TestWeightedAvg;
import org.deidentifier.arx.testcase.classification.TestBaseCAs;
import org.deidentifier.arx.testcase.classification.TestRelCAsCensus;
import org.deidentifier.arx.testcase.classification.TestRelCAsHealthInterview;
import org.deidentifier.arx.testcase.classification.TestRelCAsTimeUse;
import org.deidentifier.arx.testcase.infoloss.TestRelIlEntrMs;
import org.deidentifier.arx.testcase.infoloss.TestRelIlLossMs;
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
	TestAttributeStatistics.class,
	TestNormalization.class,
	TestDenormalization.class,
 	TestRelIlEntrMs.class,
	TestRelIlLossMs.class,
	
	// longrunners
	TestRelCAsCensus.class,
	TestRelCAsTimeUse.class,
	TestRelCAsHealthInterview.class,
	TestFindOptimalTrafoForMinPrivGuarantee.class,
	TestBaseCAs.class,
	})
public class AllTests {

}
