package org.eu.dabrowski.aidev.configuration;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingRetryer implements Retryer {
    private final Retryer retryer;
    private final long period;
    private final long maxPeriod;
    private final int maxAttempts;

    public LoggingRetryer(long period, long maxPeriod, int maxAttempts) {
        this.period = period;
        this.maxPeriod = maxPeriod;
        this.maxAttempts = maxAttempts;
        this.retryer = new Default(period, maxPeriod, maxAttempts);
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        log.info("Feign Client failed, retrying....", e);
        retryer.continueOrPropagate(e);
    }

    @Override
    public Retryer clone() {
        return new LoggingRetryer(period, maxPeriod, maxAttempts);
    }
}