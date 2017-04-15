package org.deidentifier.arx.testsuite;

import org.deidentifier.arx.testcase.paper.TestAttributeStatistics;
import org.deidentifier.arx.testcase.paper.TestBaseCAs;
import org.deidentifier.arx.testcase.paper.TestRelNueFromUtilityPackage;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestAttributeStatistics.class,
	TestBaseCAs.class,
	TestRelNueFromUtilityPackage.class,
	})
public class PaperAppearance {

}
