package com.jlogm.impl;

import com.jlogm.Level;
import com.jlogm.Registry;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.factory.LoggerFactory.Registries;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.LogOrigin;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.io.IOException;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RegistryImpl implements Registry {

    // Object

    private final @NotNull Level level;
    private final @NotNull OutputStream output;

    private final @NotNull OffsetDateTime date;

    private @Nullable Throwable throwable;

    private transient @NotNull StackFilter @NotNull [] stackFilters;
    private final @NotNull Set<Marker> markers;

    private transient @Nullable Every every;
    private @Nullable LogOrigin origin;

    private @NotNull String suffix = "\n";
    private @NotNull String prefix = "- ";

    private @Nullable Object object;
    private @NotNull Formatter formatter;

    private boolean suppressed = false;

    RegistryImpl(@NotNull Level level, @NotNull OutputStream output, @NotNull Formatter formatter, @NotNull OffsetDateTime date, @NotNull StackFilter @NotNull [] stackFilters, @NotNull Marker @NotNull [] markers, @Nullable Every every) {
        this.level = level;
        this.output = output;
        this.formatter = formatter;
        this.date = date;
        this.stackFilters = stackFilters;
        this.markers = new LinkedHashSet<>(Arrays.asList(markers));
        this.every = every;

        // Origin
        @NotNull StackTraceElement element = Arrays.stream(Thread.currentThread().getStackTrace()).skip(1).filter(trace -> !trace.getClassName().startsWith("com.jlogm")).findFirst().orElseThrow(IllegalStateException::new);
        @NotNull LogOrigin origin = LogOrigin.create(element.getClassName(), element.getFileName(), element.getMethodName(), element.getLineNumber());

        this.origin = origin;
    }

    // Getters

    @Override
    public @NotNull Level getLevel() {
        return level;
    }
    @Override
    public @NotNull OffsetDateTime getDate() {
        return date;
    }

    @Override
    public boolean isSuppressed() {
        return suppressed;
    }
    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    @Override
    public @NotNull Registry withOrigin(@Nullable LogOrigin origin) {
        this.origin = origin;
        return this;
    }
    @Override
    public @Nullable LogOrigin getOrigin() {
        return origin;
    }

    @Override
    public @NotNull Registry every(@NotNull Every every) {
        this.every = every;
        return this;
    }
    @Override
    public @Nullable Every getEvery() {
        return every;
    }

    @Override
    public @NotNull Registry prefix(@NotNull String prefix) {
        this.prefix = prefix;
        return this;
    }
    @Override
    public @NotNull String getPrefix() {
        return prefix;
    }

    @Override
    public @NotNull Registry suffix(@NotNull String suffix) {
        this.suffix = suffix;
        return this;
    }
    @Override
    public @NotNull String getSuffix() {
        return suffix;
    }

    @Override
    public @NotNull Registry formatter(@NotNull Formatter formatter) {
        this.formatter = formatter;
        return this;
    }
    @Override
    public @NotNull Formatter getFormatter() {
        return formatter;
    }

    @Override
    public @NotNull Registry withCause(@NotNull Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override
    public @NotNull Registry withCause(@NotNull Throwable throwable, @NotNull StackFilter @NotNull ... filters) {
        this.throwable = throwable;
        this.stackFilters = filters;
        return this;
    }
    @Override
    public @Nullable Throwable getCause() {
        return throwable;
    }

    @Override
    public @NotNull Registry stackFilters(@NotNull StackFilter @NotNull ... stackFilters) {
        this.stackFilters = stackFilters;
        return this;
    }
    @Override
    public @NotNull StackFilter @NotNull [] getStackFilters() {
        return stackFilters;
    }

    @Override
    public @NotNull Registry marker(@NotNull Marker marker) {
        this.markers.add(marker);
        return this;
    }
    @Override
    public @NotNull Registry markers(@NotNull Marker @NotNull ... markers) {
        this.markers.clear();
        this.markers.addAll(Arrays.asList(markers));

        return this;
    }
    @Override
    public @NotNull Marker @NotNull [] getMarkers() {
        return markers.toArray(new Marker[0]);
    }

    public @Nullable Object getObject() {
        return object;
    }

    // Modules

    @Override
    public void log(@Nullable Object object) {
        // Filters and suppression
        this.object = object;

        if (LoggerFactory.getInstance().getFilters().isSuppressed(this, object)) {
            setSuppressed(true);
        } else if (every != null && !every.canLog(LoggerFactory.getInstance(), this)) {
            setSuppressed(true);
        }

        @Nullable Registries registries = LoggerFactory.getInstance().getRegistries();
        if (registries != null) {
            registries.add(this);
        }

        // Generate message
        @NotNull Formatter formatter = this.formatter;
        @NotNull String message = formatter.format(this, object);

        // Check suppress
        if (isSuppressed()) return;

        // Print
        try {
            synchronized (output) {
                output.write(message.getBytes(formatter.getCharset()));
                output.flush();
            }
        } catch (@NotNull IOException e) {
            throw new RuntimeException("cannot print message using jlogm", e);
        }
    }

    // Implementations

    @Override
    public @NotNull String toString() {
        return "RegistryImpl{" +
                "level=" + level +
                ", date=" + date +
                ", throwable=" + throwable +
                ", stackFilters=" + Arrays.toString(stackFilters) +
                ", markers=" + markers +
                ", every=" + every +
                ", origin=" + origin +
                ", suppressed=" + suppressed +
                '}';
    }

}
