package com.jlogm.impl;

import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.Registry.Builder;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.DefaultFormatter;
import com.jlogm.formatter.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Marker;

import java.awt.*;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

final class LoggerImpl implements Logger {

    private final @NotNull String name;

    private @UnknownNullability Throwable throwable;

    private final @NotNull Set<StackFilter> stackFilters = new LinkedHashSet<>();
    private final @NotNull Set<Marker> markers = new LinkedHashSet<>();
    private final @NotNull List<Consumer<Builder>> consumers = new LinkedList<>();

    private @NotNull Formatter formatter = new DefaultFormatter();
    private @NotNull OutputStream output = System.out;

    private @UnknownNullability Every every;

    private @Nullable String suffix = "\n";
    private @Nullable String prefix = "- ";

    LoggerImpl(@NotNull String name) {
        this.name = name;
    }

    // Getters

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Logger getEvery(@NotNull Every every) {
        this.every = every;
        return this;
    }
    @Override
    public @NotNull Every getEvery() {
        return every;
    }

    @Override
    public @NotNull Logger marker(@NotNull Marker marker) {
        this.markers.add(marker);
        return this;
    }
    @Override
    public @NotNull Logger marker(@NotNull String name) {
        this.markers.add(new SimpleMarker(name));
        return this;
    }
    @Override
    public @NotNull Logger marker(@NotNull String name, @Nullable Color color) {
        this.markers.add(new SimpleMarker(name, color));
        return this;
    }

    @Override
    public @NotNull Logger markers(@NotNull Marker @NotNull ... markers) {
        this.markers.clear();
        this.markers.addAll(Arrays.asList(markers));

        return this;
    }
    @Override
    public @NotNull Marker @NotNull [] getMarkers() {
        return this.markers.toArray(new Marker[0]);
    }

    @Override
    public @NotNull Logger output(@NotNull OutputStream output) {
        this.output = output;
        return this;
    }
    @Override
    public @NotNull OutputStream getOutput() {
        return output;
    }

    @Override
    public @NotNull Logger formatter(@NotNull Formatter formatter) {
        this.formatter = formatter;
        return this;
    }
    @Override
    public @NotNull Formatter getFormatter() {
        return formatter;
    }

    @Override
    public @NotNull Logger prefix(@Nullable String prefix) {
        this.prefix = prefix;
        return this;
    }
    @Override
    public @Nullable String getPrefix() {
        return prefix;
    }

    @Override
    public @NotNull Logger suffix(@Nullable String suffix) {
        this.suffix = suffix;
        return this;
    }
    @Override
    public @Nullable String getSuffix() {
        return suffix;
    }

    @Override
    public @NotNull Logger stackFilters(@NotNull StackFilter @NotNull ... stackFilters) {
        this.stackFilters.clear();
        this.stackFilters.addAll(Arrays.asList(stackFilters));

        return this;
    }
    @Override
    public @NotNull Logger stackFilter(@NotNull StackFilter stackFilter) {
        this.stackFilters.add(stackFilter);
        return this;
    }
    @Override
    public @NotNull StackFilter @NotNull [] getStackFilters() {
        return stackFilters.toArray(new StackFilter[0]);
    }

    @Override
    public @NotNull Logger consumer(@NotNull Consumer<Builder> registry) {
        consumers.add(0, registry);
        return this;
    }
    @Override
    public @NotNull Consumer<Builder> @NotNull [] getConsumers() {
        //noinspection unchecked
        return consumers.toArray(new Consumer[0]);
    }

    // Modules

    @Override
    public @NotNull Builder registry(@NotNull Level level) {
        // Generate registry
        long time = System.currentTimeMillis();
        @NotNull Builder registry = new RegistryImpl.BuilderImpl(level, getOutput(), getFormatter(), Instant.ofEpochMilli(time), getStackFilters(), getMarkers(), getEvery(), getPrefix(), getSuffix());

        // Call consumers
        for (@NotNull Consumer<Builder> consumer : consumers) {
            consumer.accept(registry);
        }

        // Finish
        return registry;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull LoggerImpl logger = (LoggerImpl) object;
        return Objects.equals(getName(), logger.getName()) && Objects.equals(throwable, logger.throwable) && Objects.deepEquals(stackFilters, logger.stackFilters) && Objects.deepEquals(markers, logger.markers) && Objects.equals(every, logger.every);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName(), throwable, stackFilters, markers, every);
    }

    @Override
    public @NotNull String toString() {
        return "LoggerImpl{" +
                "name='" + name + '\'' +
                ", throwable=" + throwable +
                ", filters=" + stackFilters +
                ", markers=" + markers +
                ", every=" + every +
                '}';
    }

    // Classes

}
