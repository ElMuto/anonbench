package org.deidentifier.arx.execution;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestCommandLineParams {

	@Test
	public void test() {
		try {
			Compare1d_PA.main(new String[] {"ACS13", "ld", "ED", "reverse"});
		} catch (IOException e) {
			e.printStackTrace();
		}
		fail("Not yet implemented");
	}

}
