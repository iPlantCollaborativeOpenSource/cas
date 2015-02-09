/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.slf4j.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.ticket.InvalidTicketException;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.Util;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public class CasLoggerFactoryTests {

    private static final File LOG_FILE = new File("target", "slf4j.log");

    private static final String ID1 = "TGT-1-B0tjWgMIhUU4kgCZdXbxnWccTFYpTbRbArjaoutXnlNMbIShEu-cas";
    private static final String ID2 = "PGT-1-B0tjWgMIhUU4kgCZd32xnWccTFYpTbRbArjaoutXnlNMbIShEu-cas";

    private Logger logger;

    @BeforeClass
    public static void beforeClass() throws IOException {
        if (LOG_FILE.exists()) {
            Util.report("Initializing log file " + LOG_FILE.getCanonicalPath());
            FileUtils.write(LOG_FILE, "", false);
        }
    }

    @After
    public void after() throws IOException {
        FileUtils.write(LOG_FILE, "", false);
    }

    @Before
    public void beforeTest() {
        logger = LoggerFactory.getLogger(CasLoggerFactoryTests.class);
    }

    @Test
    public void verifyLoggerSelectedCorrectly() {
        assertTrue(logger instanceof CasDelegatingLogger);
    }

    @Test
    public void verifyLogging1() {
        logger.trace(mock(Marker.class), getMessageToLogWithParams(), null, null);
        validateLogData();
    }

    @Test
    public void verifyLogging2() {
        logger.trace(mock(Marker.class), getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging3() {
        logger.trace(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging4() {
        logger.trace(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging5() {
        logger.trace(mock(Marker.class), getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging6() {
        logger.trace(getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging7() {
        logger.trace(getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging8() {
        logger.trace(getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging9() {
        logger.trace(getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging21() {
        logger.debug(mock(Marker.class), getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging31() {
        logger.debug(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging41() {
        logger.debug(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging51() {
        logger.debug(mock(Marker.class), getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging61() {
        logger.debug(getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging771() {
        final TicketGrantingTicket t = mock(TicketGrantingTicket.class);
        when(t.getId()).thenReturn(ID1);
        when(t.toString()).thenReturn(ID1);

        logger.debug(getMessageToLogWithParams(), ID2, t);
        validateLogData();
    }

    @Test
    public void verifyLogging71() {
        logger.debug(getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging81() {
        logger.debug(getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging91() {
        logger.debug(getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging211() {
        logger.info(mock(Marker.class), getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging311() {
        logger.info(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging411() {
        logger.info(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging511() {
        logger.info(mock(Marker.class), getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging611() {
        logger.info(getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging711() {
        logger.info(getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging811() {
        logger.info(getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging911() {
        logger.info(getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging2111() {
        logger.warn(mock(Marker.class), getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging3111() {
        logger.warn(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging4111() {
        logger.warn(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging5111() {
        logger.warn(mock(Marker.class), getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging6111() {
        logger.warn(getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging7111() {
        logger.warn(getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging8111() {
        logger.warn(getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging9111() {
        logger.warn(getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging21110() {
        logger.error(mock(Marker.class), getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging31110() {
        logger.error(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging41110() {
        logger.error(mock(Marker.class), getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging51110() {
        logger.error(mock(Marker.class), getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    @Test
    public void verifyLogging61110() {
        logger.error(getMessageToLog());
        validateLogData();
    }

    @Test
    public void verifyLogging71110() {
        logger.error(getMessageToLogWithParams(), ID2, ID1);
        validateLogData();
    }

    @Test
    public void verifyLogging81110() {
        logger.error(getMessageToLogWithParams(), ID2, ID1, ID2);
        validateLogData();
    }

    @Test
    public void verifyLogging91110() {
        logger.error(getMessageToLog(), new RuntimeException(ID1, new InvalidTicketException(ID2)));
        validateLogData();
    }

    private String getMessageToLog() {
        return String.format("Here is one %s and here is another %s", ID1, ID2);
    }

    private String getMessageToLogWithParams() {
        return "Here is one {} and here is another {}";
    }

    private void validateLogData() {
        try {
            assertTrue("Log file " + LOG_FILE.getCanonicalPath() + " does not exist", LOG_FILE.exists());

            final String data = FileUtils.readFileToString(LOG_FILE);
            assertTrue("Logged buffer data is blank in " + LOG_FILE.getCanonicalPath(), StringUtils.isNotBlank(data));
            assertFalse("Logged buffer data should not contain " + ID1, data.contains(ID1));
            assertFalse("Logged buffer data should not contain " + ID2, data.contains(ID2));
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }
}

