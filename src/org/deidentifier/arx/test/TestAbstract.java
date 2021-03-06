/*
 * Source code of our CBMS 2014 paper "A benchmark of globally-optimal
 * methods for the de-identification of biomedical data"
 * 
 * Copyright (C) 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.deidentifier.arx.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract test case for anonbench
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public abstract class TestAbstract extends TestCase {

    /** The test case */
    private final TestConfiguration config;

    /**
     * Creates a new instance
     * 
     * @param config
     */
    public TestAbstract(final TestConfiguration config) {
        this.config = config;
    }

    @Override
    @Before
    public void setUp() {
        // We don't want to call super.setUp()
    }

    @Test
    public void test() throws IOException {

    }
}
