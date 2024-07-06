package com.jlogm.impl;

import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.Registry;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Marker;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

final class LoggerImpl implements Logger {

    private final @NotNull String name;

    private @UnknownNullability Throwable throwable;

    private @NotNull StackFilter @NotNull [] stackFilters = new StackFilter[0];
    private @NotNull Marker @NotNull [] markers = new Marker[0];

    private @UnknownNullability Every every;

    LoggerImpl(@NotNull String name) {
        this.name = name;
    }

    // Getters

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Logger every(@NotNull Every every) {
        this.every = every;
        return this;
    }
    @Override
    public @NotNull Every every() {
        return every;
    }

    @Override
    public @NotNull Logger marker(@NotNull Marker @NotNull ... markers) {
        this.markers = markers;
        return this;
    }
    @Override
    public @NotNull Marker @NotNull [] marker() {
        return this.markers;
    }

    @Override
    public @NotNull Logger stackFilters(@NotNull StackFilter @NotNull ... stackFilters) {
        this.stackFilters = stackFilters;
        return this;
    }
    @Override
    public @NotNull StackFilter @NotNull [] stackFilters() {
        return stackFilters;
    }

    // Modules

    @Override
    public @NotNull Registry registry(@NotNull Level level) {
        return new RegistryImpl(level, OffsetDateTime.now(), stackFilters, markers, every);
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
        return Objects.hash(getName(), throwable, Arrays.hashCode(stackFilters), Arrays.hashCode(markers), every);
    }

    @Override
    public @NotNull String toString() {
        return "LoggerImpl{" +
                "name='" + name + '\'' +
                ", throwable=" + throwable +
                ", filters=" + Arrays.toString(stackFilters) +
                ", markers=" + Arrays.toString(markers) +
                ", every=" + every +
                '}';
    }

}
