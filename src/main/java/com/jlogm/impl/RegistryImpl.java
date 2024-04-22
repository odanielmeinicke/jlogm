package com.jlogm.impl;

import com.jlogm.Logger;
import com.jlogm.Registry;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

abstract class RegistryImpl implements Registry {

    private final @NotNull Logger logger;
    private final @NotNull Instant date;

    private boolean suppressed = false;

    RegistryImpl(@NotNull Logger logger, @NotNull Instant date) {
        this.logger = logger;
        this.date = date;
    }

    @Override
    public final @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public final @NotNull Instant getDate() {
        return date;
    }

    @Override
    public final boolean wasSuppressed() {
        return suppressed;
    }
    public final void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public abstract @NotNull String format();

    @Override
    public int length() {
        return format().length();
    }

    @Override
    public char charAt(int index) {
        return format().charAt(index);
    }

    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return format().subSequence(start, end);
    }

    @Override
    public @NotNull String toString() {
        return format();
    }

}
