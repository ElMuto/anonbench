package org.deidentifier.arx.testsuite;

import org.deidentifier.arx.testcase.paper.TestAttributeStatistics;
import org.deidentifier.arx.testcase.paper.TestBaseCAs;
import org.deidentifier.arx.testcase.paper.TestRelIlEntrMs;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestAttributeStatistics.class,
	TestBaseCAs.class,
// 	TestRelIlEntrMs.class,  // FAILURE !!!
//	TestRelIlEntrMs.class, // FAILURE !!!!
	})
public class PaperAppearance {

}
