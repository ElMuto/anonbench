package org.deidentifier.arx.testsuite;

import org.deidentifier.arx.testcase.TestAttributeStatistics;
import org.deidentifier.arx.testcase.classification.TestBaseCAs;
import org.deidentifier.arx.testcase.classification.TestRelCAsCensus;
import org.deidentifier.arx.testcase.classification.TestRelCAsHealthInterview;
import org.deidentifier.arx.testcase.classification.TestRelCAsTimeUse;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestAttributeStatistics.class,
	TestRelCAsCensus.class,
	TestRelCAsTimeUse.class,
	TestRelCAsHealthInterview.class,
	TestBaseCAs.class,
// 	TestRelIlEntrMs.class,  // FAILURE !!!
//	TestRelIlEntrLoss.class, // FAILURE !!!!
	})
public class PaperAppearance {

}
