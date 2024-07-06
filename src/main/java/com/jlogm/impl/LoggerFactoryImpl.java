package com.jlogm.impl;

import com.jlogm.Filter;
import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.fluent.LogOrigin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.ILoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;

public final class LoggerFactoryImpl implements LoggerFactory, ILoggerFactory {

    // Static initializers

    public static final @NotNull Level TRACE = Level.create("TRACE", new Color(123, 123, 123));
    public static final @NotNull Level INFO = Level.create("INFO", new Color(160, 160, 160));
    public static final @NotNull Level SEVERE = Level.create("SEVERE", new Color(220, 0, 0));
    public static final @NotNull Level WARN = Level.create("WARN", new Color(255, 255, 0));
    public static final @NotNull Level DEBUG = Level.create("DEBUG", new Color(230, 150, 175));

    @SuppressWarnings("FieldMayBeFinal")
    public static @NotNull LoggerFactory instance = new LoggerFactoryImpl();

    // Object

    private final @NotNull Levels levels = new LevelsImpl();
    private final @NotNull Filters filters = new FiltersImpl();
    private @Nullable Registries registries = new RegistriesImpl();

    LoggerFactoryImpl() {
    }

    // Getters

    @Override
    public @NotNull Levels getLevels() {
        return levels;
    }

    @Override
    public @NotNull Filters getFilters() {
        return filters;
    }

    @Override
    public @Nullable Registries getRegistries() {
        return registries;
    }
    public void setRegistries(@Nullable Registries registries) throws IOException {
        if (getRegistries() != null && getRegistries() != registries) {
            getRegistries().flush();
        }

        this.registries = registries;
    }

    @Override
    public @NotNull Logger create(@NotNull String name) {
        return new LoggerImpl(name);
    }

    // SLF4J

    @Override
    public @NotNull org.slf4j.Logger getLogger(@NotNull String name) {
        @NotNull StackTraceElement element = Arrays.stream(Thread.currentThread().getStackTrace()).skip(3).findFirst().orElseThrow(IllegalStateException::new);
        @NotNull LogOrigin origin = LogOrigin.create(element.getClassName(), element.getFileName(), element.getMethodName(), element.getLineNumber());

        return new Slf4jLoggerImpl(name, origin);
    }

    // Implementations

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LoggerFactoryImpl that = (LoggerFactoryImpl) object;
        return Objects.equals(getLevels(), that.getLevels()) && Objects.equals(getFilters(), that.getFilters()) && Objects.equals(getRegistries(), that.getRegistries());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getLevels(), getFilters(), getRegistries());
    }

    // Classes

    private static final class LevelsImpl implements Levels {

        private final @NotNull Set<Level> levels = new HashSet<>();

        private LevelsImpl() {
            levels.add(INFO);
            levels.add(SEVERE);
            levels.add(WARN);
            levels.add(DEBUG);
            levels.add(TRACE);
        }

        @Override
        public @NotNull Stream<Level> stream() {
            return levels.stream();
        }
        @Override
        public @NotNull Iterator<Level> iterator() {
            return levels.iterator();
        }
    }
    private static final class FiltersImpl implements Filters {

        private final @NotNull List<Filter> filters = new LinkedList<>();

        private FiltersImpl() {
        }

        @Override
        public boolean add(@NotNull Filter filter) {
            return filters.add(filter);
        }

        @Override
        public boolean remove(@NotNull Filter filter) {
            return filters.remove(filter);
        }

        @Override
        public boolean contains(@NotNull Filter filter) {
            return filters.contains(filter);
        }

        @Override
        public int size() {
            return filters.size();
        }

        @Override
        public @NotNull Stream<Filter> stream() {
            return filters.stream();
        }

        @Override
        public @NotNull Iterator<Filter> iterator() {
            return filters.iterator();
        }

    }

}
