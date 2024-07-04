package com.jlogm.impl;

import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.Registry;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.LogOrigin;
import com.jlogm.fluent.StackFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;
import java.util.Objects;

abstract class LoggerImpl implements Logger {

    private final @NotNull String name;
    private final @NotNull Level level;

    public boolean thrown = false;

    // Exceptions
    public @UnknownNullability Throwable throwable;
    public @NotNull StackFilter @UnknownNullability [] filters;

    // Others
    public @UnknownNullability Every every;
    public @NotNull LogOrigin origin;

    LoggerImpl(@NotNull String name, @NotNull Level level, @NotNull LogOrigin origin) {
        this.name = name;
        this.level = level;
        this.origin = origin;
    }

    // Getters

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public final @NotNull Level getLevel() {
        return level;
    }

    @Override
    public @Nullable Throwable getCause() {
        return throwable;
    }
    @Override
    public @NotNull StackFilter @Nullable [] getStackFilters() {
        return filters;
    }
    @Override
    public @Nullable Every getEvery() {
        return every;
    }

    @Override
    public @NotNull LogOrigin getOrigin() {
        return origin;
    }

    @Override
    public final @NotNull Logger withOrigin(@NotNull LogOrigin origin) {
        checkThrown();
        return this;
    }

    @Override
    public final @NotNull Logger every(@NotNull Every every) {
        checkThrown();
        this.every = every;
        return this;
    }

    @Override
    public final @NotNull Logger withCause(@NotNull Throwable throwable) {
        return Logger.super.withCause(throwable);
    }

    @Override
    public final @NotNull Logger withCause(@NotNull Throwable throwable, @NotNull StackFilter @NotNull ... filters) {
        checkThrown();

        this.throwable = throwable;
        this.filters = filters;

        return this;
    }

    @Override
    public final @Nullable Registry log() {
        return log(null);
    }

    // Utilities

    private void checkThrown() {
        if (thrown) {
            throw new UnsupportedOperationException("you cannot modify a thrown logger");
        }
    }

    // Equals

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LoggerImpl logger = (LoggerImpl) object;
        return Objects.equals(getLevel(), logger.getLevel()) && Objects.equals(throwable, logger.throwable) && Objects.deepEquals(filters, logger.filters) && Objects.equals(every, logger.every);
    }
    @Override
    public int hashCode() {
        return Objects.hash(getLevel(), throwable, Arrays.hashCode(filters), every);
    }

}
