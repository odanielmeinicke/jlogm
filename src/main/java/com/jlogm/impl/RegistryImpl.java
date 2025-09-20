package com.jlogm.impl;

import com.jlogm.Filter;
import com.jlogm.Level;
import com.jlogm.Registry;
import com.jlogm.context.LogCtx;
import com.jlogm.context.Stack;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.factory.LoggerFactory.Registries;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.*;

public final class RegistryImpl implements Registry {

    private final @NotNull Level level;
    private final @NotNull Instant instant;

    private final @Nullable Every every;
    private final @Nullable StackTraceElement origin;
    private final @Nullable String suffix;
    private final @Nullable String prefix;
    private final @NotNull Formatter formatter;
    private final @Nullable Throwable cause;
    private final @NotNull Marker @NotNull [] markers;
    private final @NotNull StackFilter @NotNull [] stackFilters;
    private final @Nullable Object object;
    private final boolean suppressed;

    private final @NotNull Map<String, Object> context;
    private final @NotNull Set<String> stack;

    public RegistryImpl(@NotNull Level level, @NotNull Instant instant, @Nullable Every every, @Nullable StackTraceElement origin, @Nullable String suffix, @Nullable String prefix, @NotNull Formatter formatter, @Nullable Throwable cause, @NotNull Marker @NotNull [] markers, @NotNull StackFilter @NotNull [] stackFilters, @Nullable Object object, boolean suppressed, @NotNull Map<String, Object> context, @NotNull Set<String> stack) {
        this.level = level;
        this.instant = instant;
        this.every = every;
        this.origin = origin;
        this.suffix = suffix;
        this.prefix = prefix;
        this.formatter = formatter;
        this.cause = cause;
        this.markers = markers;
        this.stackFilters = stackFilters;
        this.object = object;
        this.suppressed = suppressed;
        this.context = context;
        this.stack = stack;
    }

    // Getters

    @Override
    public @NotNull Level getLevel() {
        return level;
    }
    @Override
    public @NotNull Instant getInstant() {
        return instant;
    }

    @Override
    public @Nullable Every getEvery() {
        return every;
    }

    @Override
    public @Nullable StackTraceElement getOrigin() {
        return origin;
    }

    @Override
    public @Nullable String getPrefix() {
        return prefix;
    }
    @Override
    public @Nullable String getSuffix() {
        return suffix;
    }

    @Override
    public @NotNull Formatter getFormatter() {
        return formatter;
    }

    @Override
    public @Nullable Throwable getCause() {
        return cause;
    }

    @Override
    public @NotNull StackFilter @NotNull [] getStackFilters() {
        return stackFilters;
    }

    @Override
    public @NotNull Marker @NotNull [] getMarkers() {
        return markers;
    }

    @Override
    public @Nullable Object getObject() {
        return object;
    }

    @Override
    public boolean isSuppressed() {
        return suppressed;
    }

    @Override
    public @NotNull Map<String, Object> getContext() {
        return context;
    }
    @Override
    public @NotNull Set<String> getStack() {
        return stack;
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof RegistryImpl)) return false;
        @NotNull RegistryImpl registry = (RegistryImpl) object;
        return isSuppressed() == registry.isSuppressed() && getLevel() == registry.getLevel() && Objects.equals(getInstant(), registry.getInstant()) && Objects.equals(getEvery(), registry.getEvery()) && Objects.equals(getOrigin(), registry.getOrigin()) && Objects.equals(getSuffix(), registry.getSuffix()) && Objects.equals(getPrefix(), registry.getPrefix()) && Objects.equals(getFormatter(), registry.getFormatter()) && Objects.equals(getCause(), registry.getCause()) && Objects.deepEquals(getMarkers(), registry.getMarkers()) && Objects.deepEquals(getStackFilters(), registry.getStackFilters()) && Objects.equals(getObject(), registry.getObject());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getLevel(), getInstant(), getEvery(), getOrigin(), getSuffix(), getPrefix(), getFormatter(), getCause(), Arrays.hashCode(getMarkers()), Arrays.hashCode(getStackFilters()), getObject(), isSuppressed());
    }

    @Override
    public @NotNull String toString() {
        return getFormatter().format(this);
    }

    // Classes

    public static final class BuilderImpl implements Builder {

        // Object

        private final @NotNull OutputStream output;

        private @NotNull Level level;
        private @NotNull Instant instant;

        private @Nullable Throwable cause;

        private transient @NotNull StackFilter @NotNull [] stackFilters;
        private final @NotNull Set<Marker> markers;

        private transient @Nullable Every every;
        private @Nullable StackTraceElement origin;

        private @Nullable String suffix;
        private @Nullable String prefix;

        private @NotNull Formatter formatter;

        private boolean suppressed = false;

        BuilderImpl(@NotNull Level level, @NotNull OutputStream output, @NotNull Formatter formatter, @NotNull Instant instant, @NotNull StackFilter @NotNull [] stackFilters, @NotNull Marker @NotNull [] markers, @Nullable Every every, @Nullable String prefix, @Nullable String suffix) {
            this.level = level;
            this.output = output;
            this.formatter = formatter;
            this.instant = instant;
            this.stackFilters = stackFilters;
            this.markers = new LinkedHashSet<>(Arrays.asList(markers));
            this.every = every;
            this.origin = Arrays.stream(Thread.currentThread().getStackTrace()).skip(1).filter(trace -> !trace.getClassName().startsWith("com.jlogm")).findFirst().orElseThrow(IllegalStateException::new);
            this.prefix = prefix;
            this.suffix = suffix;
        }

        // Getters

        @Override
        public @NotNull Builder level(@NotNull Level level) {
            this.level = level;
            return this;
        }
        @Override
        public @NotNull Level getLevel() {
            return level;
        }

        @Override
        public @NotNull Builder instant(@NotNull Instant instant) {
            this.instant = instant;
            return this;
        }
        @Override
        public @NotNull Instant getInstant() {
            return instant;
        }

        @Override
        public boolean isSuppressed() {
            return suppressed;
        }
        public void setSuppressed(boolean suppressed) {
            this.suppressed = suppressed;
        }

        @Override
        public @NotNull Builder origin(@Nullable StackTraceElement origin) {
            this.origin = origin;
            return this;
        }
        @Override
        public @Nullable StackTraceElement getOrigin() {
            return origin;
        }

        @Override
        public @NotNull Builder every(@NotNull Every every) {
            this.every = every;
            return this;
        }
        @Override
        public @Nullable Every getEvery() {
            return every;
        }

        @Override
        public @NotNull Builder prefix(@Nullable String prefix) {
            this.prefix = prefix;
            return this;
        }
        @Override
        public @Nullable String getPrefix() {
            return prefix;
        }

        @Override
        public @NotNull Builder suffix(@Nullable String suffix) {
            this.suffix = suffix;
            return this;
        }
        @Override
        public @Nullable String getSuffix() {
            return suffix;
        }

        @Override
        public @NotNull Builder formatter(@NotNull Formatter formatter) {
            this.formatter = formatter;
            return this;
        }
        @Override
        public @NotNull Formatter getFormatter() {
            return formatter;
        }

        @Override
        public @NotNull Builder cause(@NotNull Throwable throwable) {
            this.cause = throwable;
            return this;
        }
        @Override
        public @NotNull Builder cause(@NotNull Throwable throwable, @NotNull StackFilter @NotNull ... filters) {
            this.cause = throwable;
            this.stackFilters = filters;
            return this;
        }
        @Override
        public @Nullable Throwable getCause() {
            return cause;
        }

        @Override
        public @NotNull Builder stackFilters(@NotNull StackFilter @NotNull ... stackFilters) {
            this.stackFilters = stackFilters;
            return this;
        }
        @Override
        public @NotNull StackFilter @NotNull [] getStackFilters() {
            return stackFilters;
        }

        @Override
        public @NotNull Builder marker(@NotNull Marker marker) {
            this.markers.add(marker);
            return this;
        }
        @Override
        public @NotNull Builder marker(@NotNull String name) {
            this.markers.add(new SimpleMarker(name));
            return this;
        }
        @Override
        public @NotNull Builder marker(@NotNull String name, @NotNull Color color) {
            this.markers.add(new SimpleMarker(name, color));
            return this;
        }

        @Override
        public @NotNull Builder markers(@NotNull Marker @NotNull ... markers) {
            this.markers.clear();
            this.markers.addAll(Arrays.asList(markers));

            return this;
        }
        @Override
        public @NotNull Marker @NotNull [] getMarkers() {
            return markers.toArray(new Marker[0]);
        }

        // Modules

        @Override
        public @NotNull Registry log(@Nullable Object object) {
            // Object verification
            for (@NotNull Filter filter : LoggerFactory.getInstance().getFilters()) {
                object = filter.object(this, object);
            }

            // Filters and suppression
            if (LoggerFactory.getInstance().getFilters().isSuppressed(this, object)) {
                setSuppressed(true);
            } else if (every != null && !every.canLog(LoggerFactory.getInstance(), this, object)) {
                setSuppressed(true);
            }

            // Clone context and stack
            @NotNull Map<String, Object> context = new LinkedHashMap<>(LogCtx.snapshot());
            @NotNull Set<String> stack = new LinkedHashSet<>(Stack.snapshot());

            // Generate registry
            @NotNull RegistryImpl registry = new RegistryImpl(getLevel(), getInstant(), getEvery(), getOrigin(), getSuffix(), getPrefix(), getFormatter(), getCause(), getMarkers(), getStackFilters(), object, isSuppressed(), context, stack);
            
            // Generate message
            @NotNull String message = getFormatter().format(registry);

            // Save it to registries
            @Nullable Registries registries = LoggerFactory.getInstance().getRegistries();
            if (registries != null) registries.add(registry);

            // Print if not suppressed
            if (!isSuppressed()) {
                try {
                    synchronized (output) {
                        output.write(message.getBytes(getFormatter().getCharset()));
                        output.flush();
                    }
                } catch (@NotNull IOException e) {
                    throw new RuntimeException("cannot print message using jlogm", e);
                }
            }

            // Finish
            return registry;
        }

        // Implementations

        @Override
        public @NotNull String toString() {
            return "RegistryImpl{" +
                    "level=" + level +
                    ", instant=" + instant +
                    ", throwable=" + cause +
                    ", stackFilters=" + Arrays.toString(stackFilters) +
                    ", markers=" + markers +
                    ", every=" + every +
                    ", origin=" + origin +
                    ", suppressed=" + suppressed +
                    '}';
        }

    }

}
