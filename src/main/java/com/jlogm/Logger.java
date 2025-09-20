package com.jlogm;

import com.jlogm.Registry.Builder;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.awt.*;
import java.io.OutputStream;
import java.util.function.Consumer;

public interface Logger {

    // Static initializers

    static @NotNull Logger create(@NotNull String name) {
        return LoggerFactory.getInstance().create(name);
    }
    static @NotNull Logger create(@NotNull Class<?> reference) {
        return LoggerFactory.getInstance().create(reference.getName());
    }

    // Object

    @NotNull String getName();

    @NotNull Logger getEvery(@NotNull Every every);
    @NotNull Every getEvery();

    @NotNull Logger marker(@NotNull Marker marker);
    @NotNull Logger marker(@NotNull String name);
    @NotNull Logger marker(@NotNull String name, @Nullable Color color);
    @NotNull Logger markers(@NotNull Marker @NotNull ... markers);
    @NotNull Marker @NotNull [] getMarkers();

    @NotNull Logger output(@NotNull OutputStream output);
    @NotNull OutputStream getOutput();

    @NotNull Logger formatter(@NotNull Formatter formatter);
    @NotNull Formatter getFormatter();

    @NotNull Logger prefix(@Nullable String prefix);
    @Nullable String getPrefix();
    @NotNull Logger suffix(@Nullable String suffix);
    @Nullable String getSuffix();

    @NotNull Logger stackFilters(@NotNull StackFilter @NotNull ... stackFilters);
    @NotNull Logger stackFilter(@NotNull StackFilter stackFilter);
    @NotNull StackFilter @NotNull [] getStackFilters();

    @NotNull Logger consumer(@NotNull Consumer<Builder> registry);
    @NotNull Consumer<Builder> @NotNull [] getConsumers();

    @NotNull Builder registry(@NotNull Level level);

    default @NotNull Builder warn() {
        return registry(Level.WARN);
    }
    default void warn(@Nullable String message) {
        warn().log(message);
    }

    default @NotNull Builder severe() {
        return registry(Level.SEVERE);
    }
    default void severe(@Nullable String message) {
        severe().log(message);
    }

    default @NotNull Builder trace() {
        return registry(Level.TRACE);
    }
    default void trace(@Nullable String message) {
        trace().log(message);
    }

    default @NotNull Builder debug() {
        return registry(Level.DEBUG);
    }
    default void debug(@Nullable String message) {
        debug().log(message);
    }

    default @NotNull Builder info() {
        return registry(Level.INFO);
    }
    default void info(@Nullable String message) {
        info().log(message);
    }

}
