package com.jlogm.impl;

import com.jlogm.factory.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class Slf4jServiceImpl implements SLF4JServiceProvider {

    // Object

    private @Nullable ILoggerFactory loggerFactory;
    private @Nullable IMarkerFactory markerFactory;
    private @Nullable MDCAdapter mdcAdapter;

    public Slf4jServiceImpl() {
    }

    // Getters

    @Override
    public @NotNull ILoggerFactory getLoggerFactory() {
        if (loggerFactory == null) {
            throw new IllegalStateException("this service provider is not initialized");
        }
        return loggerFactory;
    }
    @Override
    public @NotNull IMarkerFactory getMarkerFactory() {
        if (markerFactory == null) {
            throw new IllegalStateException("this service provider is not initialized");
        }
        return markerFactory;
    }
    @Override
    public @NotNull MDCAdapter getMDCAdapter() {
        if (mdcAdapter == null) {
            throw new IllegalStateException("this service provider is not initialized");
        }
        return mdcAdapter;
    }

    @Override
    public @NotNull String getRequestedApiVersion() {
        return "2.0";
    }

    // Loaders

    @Override
    public void initialize() {
        loggerFactory = (ILoggerFactory) LoggerFactory.getInstance();
        markerFactory = new BasicMarkerFactory();
        mdcAdapter = new NOPMDCAdapter();
    }

}
