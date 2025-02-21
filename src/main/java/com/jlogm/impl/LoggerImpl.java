package com.jlogm.impl;

import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.Registry;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.DefaultFormatter;
import com.jlogm.formatter.Formatter;
import com.jlogm.utils.Colors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Marker;

import java.awt.*;
import java.io.OutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

final class LoggerImpl implements Logger {

    private final @NotNull String name;

    private @UnknownNullability Throwable throwable;

    private final @NotNull Set<StackFilter> stackFilters = new LinkedHashSet<>();
    private final @NotNull Set<Marker> markers = new LinkedHashSet<>();
    private final @NotNull List<Consumer<Registry>> consumers = new LinkedList<>();

    private @NotNull Formatter formatter = new DefaultFormatter();
    private @NotNull OutputStream output = System.out;

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
    public @NotNull Logger markers(@NotNull Marker @NotNull ... markers) {
        this.markers.clear();
        this.markers.addAll(Arrays.asList(markers));

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
    public @NotNull Marker @NotNull [] markers() {
        return this.markers.toArray(new Marker[0]);
    }

    @Override
    public @NotNull Logger output(@NotNull OutputStream output) {
        this.output = output;
        return this;
    }
    @Override
    public @NotNull OutputStream output() {
        return output;
    }

    @Override
    public @NotNull Logger formatter(@NotNull Formatter formatter) {
        this.formatter = formatter;
        return this;
    }
    @Override
    public @NotNull Formatter formatter() {
        return formatter;
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
    public @NotNull StackFilter @NotNull [] stackFilters() {
        return stackFilters.toArray(new StackFilter[0]);
    }

    @Override
    public @NotNull Logger consumer(@NotNull Consumer<Registry> registry) {
        consumers.add(0, registry);
        return this;
    }
    @Override
    public @NotNull Consumer<Registry> @NotNull [] consumers() {
        //noinspection unchecked
        return consumers.toArray(new Consumer[0]);
    }

    // Modules

    @Override
    public @NotNull Registry registry(@NotNull Level level) {
        // Generate registry
        @NotNull Registry registry = new RegistryImpl(level, output(), formatter(), OffsetDateTime.now(), stackFilters(), markers(), every());

        // Call consumers
        for (@NotNull Consumer<Registry> consumer : consumers) {
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

    static final class SimpleMarker implements Marker {

        private final @NotNull String name;
        private final @Nullable Color color;

        private final @NotNull Set<Marker> markers = new LinkedHashSet<>();

        public SimpleMarker(@NotNull String name) {
            this.name = name;
            this.color = null;
        }
        public SimpleMarker(@NotNull String name, @Nullable Color color) {
            this.name = name;
            this.color = color;
        }

        @Override
        public @NotNull String getName() {
            return name;
        }
        public @Nullable Color getColor() {
            return color;
        }

        @Override
        public void add(@NotNull Marker reference) {
            markers.add(reference);
        }
        @Override
        public boolean remove(@NotNull Marker reference) {
            return markers.remove(reference);
        }

        @Override
        public boolean hasChildren() {
            return hasReferences();
        }
        @Override
        public boolean hasReferences() {
            return !markers.isEmpty();
        }

        @Override
        public boolean contains(@NotNull Marker other) {
            return false;
        }
        @Override
        public boolean contains(@NotNull String name) {
            return markers.stream().anyMatch(marker -> marker.getName().equals(name));
        }

        @Override
        public @NotNull Iterator<Marker> iterator() {
            return markers.iterator();
        }

        // Implementations

        @Override
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof Marker)) return false;
            @NotNull Marker that = (Marker) object;
            return Objects.equals(getName(), that.getName());
        }
        @Override
        public int hashCode() {
            return Objects.hash(getName());
        }

        @Override
        public @NotNull String toString() {
            return (getColor() != null ? Colors.colored(getColor()) : "") + getName() + (getColor() != null ? Colors.reset() : "");
        }

    }

}
