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
package org.jasig.cas.web;

import org.apache.commons.collections.functors.TruePredicate;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.perf4j.log4j.GraphingStatisticsAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Scott Battaglia
 * @since 3.3.5
 */
public final class StatisticsController extends AbstractController {

    private static final int NUMBER_OF_MILLISECONDS_IN_A_DAY = 86400000;

    private static final int NUMBER_OF_MILLISECONDS_IN_AN_HOUR = 3600000;

    private static final int NUMBER_OF_MILLISECONDS_IN_A_MINUTE = 60000;

    private static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;

    private static final int NUMBER_OF_BYTES_IN_A_KILOBYTE = 1024;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Date upTimeStartDate = new Date();

    private String casTicketSuffix;

    private String viewPath = "/WEB-INF/view/jsp/monitoring/viewStatistics.jsp";

    private final CentralAuthenticationService centralAuthenticationService;

    /**
     * Instantiates a new statistics controller.
     *
     * @param centralAuthenticationService the CAS service layer
     */
    public StatisticsController(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    public void setCasTicketSuffix(final String casTicketSuffix) {
        this.casTicketSuffix = casTicketSuffix;
    }

    public void setViewPath(final String viewPath){
        this.viewPath = viewPath;
    }

    @Override
    protected ModelAndView handleRequestInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
                throws Exception {
        final ModelAndView modelAndView = new ModelAndView(new InternalResourceView(viewPath));
        modelAndView.addObject("startTime", this.upTimeStartDate);
        final double difference = System.currentTimeMillis() - this.upTimeStartDate.getTime();

        modelAndView.addObject("upTime", calculateUptime(difference, new LinkedList<Integer>(
                        Arrays.asList(NUMBER_OF_MILLISECONDS_IN_A_DAY, NUMBER_OF_MILLISECONDS_IN_AN_HOUR,
                        NUMBER_OF_MILLISECONDS_IN_A_MINUTE, NUMBER_OF_MILLISECONDS_IN_A_SECOND, 1)),
                        new LinkedList<String>(Arrays.asList("day", "hour", "minute", "second", "millisecond"))));

        modelAndView.addObject("totalMemory", convertToMegaBytes(Runtime.getRuntime().totalMemory()));
        modelAndView.addObject("maxMemory", convertToMegaBytes(Runtime.getRuntime().maxMemory()));
        modelAndView.addObject("freeMemory", convertToMegaBytes(Runtime.getRuntime().freeMemory()));
        modelAndView.addObject("availableProcessors", Runtime.getRuntime().availableProcessors());
        modelAndView.addObject("serverHostName", httpServletRequest.getServerName());
        modelAndView.addObject("serverIpAddress", httpServletRequest.getLocalAddr());
        modelAndView.addObject("casTicketSuffix", this.casTicketSuffix);

        int unexpiredTgts = 0;
        int unexpiredSts = 0;
        int expiredTgts = 0;
        int expiredSts = 0;

        try {
            final Collection<Ticket> tickets = this.centralAuthenticationService.getTickets(TruePredicate.getInstance());

            for (final Ticket ticket : tickets) {
                if (ticket instanceof ServiceTicket) {
                    if (ticket.isExpired()) {
                        expiredSts++;
                    } else {
                        unexpiredSts++;
                    }
                } else {
                    if (ticket.isExpired()) {
                        expiredTgts++;
                    } else {
                        unexpiredTgts++;
                    }
                }
            }
        } catch (final UnsupportedOperationException e) {
            logger.trace("The ticket registry doesn't support this information.");
        }

        final Collection<GraphingStatisticsAppender> appenders = GraphingStatisticsAppender.getAllGraphingStatisticsAppenders();

        modelAndView.addObject("unexpiredTgts", unexpiredTgts);
        modelAndView.addObject("unexpiredSts", unexpiredSts);
        modelAndView.addObject("expiredTgts", expiredTgts);
        modelAndView.addObject("expiredSts", expiredSts);
        modelAndView.addObject("pageTitle", modelAndView.getViewName());
        modelAndView.addObject("graphingStatisticAppenders", appenders);

        return modelAndView;
    }

    /**
     * Convert to megabytes from bytes.
     * @param bytes the total number of bytes
     * @return value converted to MB
     */
    private double convertToMegaBytes(final double bytes) {
        return bytes / NUMBER_OF_BYTES_IN_A_KILOBYTE / NUMBER_OF_BYTES_IN_A_KILOBYTE;
    }
    /**
     * Calculates the up time.
     *
     * @param difference the difference
     * @param calculations the calculations
     * @param labels the labels
     * @return the uptime as a string.
     */
    protected String calculateUptime(final double difference, final Queue<Integer> calculations, final Queue<String> labels) {
        if (calculations.isEmpty()) {
            return "";
        }

        final int value = calculations.remove();
        final double time = Math.floor(difference / value);
        final double newDifference = difference - time * value;
        final String currentLabel = labels.remove();
        final String label = time == 0 || time > 1 ? currentLabel + "s" : currentLabel;

        return Integer.toString((int) time) + " " + label + " " + calculateUptime(newDifference, calculations, labels);
    }
}
