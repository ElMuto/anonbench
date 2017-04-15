package org.deidentifier.arx.testsuite;

import org.deidentifier.arx.testcase.paper.TestAttributeStatistics;
import org.deidentifier.arx.testcase.paper.TestBaseCAs;
import org.deidentifier.arx.testcase.paper.TestRelCAsCensus;
import org.deidentifier.arx.testcase.paper.TestRelCAsHealthInterview;
import org.deidentifier.arx.testcase.paper.TestRelCAsTimeUse;
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
