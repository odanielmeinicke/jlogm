package com.jlogm;

import com.jlogm.factory.LoggerFactory;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.awt.*;

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

    @Contract("_->this")
    @NotNull Logger every(@NotNull Every every);
    @NotNull Every every();

    @Contract("_->this")
    @NotNull Logger markers(@NotNull Marker @NotNull ... markers);
    @NotNull Logger marker(@NotNull String name);
    @NotNull Logger marker(@NotNull String name, @Nullable Color color);
    @NotNull Marker @NotNull [] marker();

    @Contract("_->this")
    @NotNull Logger stackFilters(@NotNull StackFilter @NotNull ... stackFilters);
    @NotNull Logger stackFilter(@NotNull StackFilter stackFilter);
    @NotNull StackFilter @NotNull [] stackFilters();

    @NotNull Registry registry(@NotNull Level level);

    default @NotNull Registry warn() {
        @NotNull String name = "WARN";
        @NotNull Level level = LoggerFactory.getInstance().getLevels().get(name).orElseThrow(() -> new NullPointerException("there's no '" + name + "' level at the current logging factory"));
        return registry(level);
    }
    default void warn(@Nullable String message) {
        warn().log(message);
    }

    default @NotNull Registry severe() {
        @NotNull String name = "SEVERE";
        @NotNull Level level = LoggerFactory.getInstance().getLevels().get(name).orElseThrow(() -> new NullPointerException("there's no '" + name + "' level at the current logging factory"));
        return registry(level);
    }
    default void severe(@Nullable String message) {
        severe().log(message);
    }

    default @NotNull Registry trace() {
        @NotNull String name = "TRACE";
        @NotNull Level level = LoggerFactory.getInstance().getLevels().get(name).orElseThrow(() -> new NullPointerException("there's no '" + name + "' level at the current logging factory"));
        return registry(level);
    }
    default void trace(@Nullable String message) {
        trace().log(message);
    }

    default @NotNull Registry debug() {
        @NotNull String name = "DEBUG";
        @NotNull Level level = LoggerFactory.getInstance().getLevels().get(name).orElseThrow(() -> new NullPointerException("there's no '" + name + "' level at the current logging factory"));
        return registry(level);
    }
    default void debug(@Nullable String message) {
        debug().log(message);
    }

    default @NotNull Registry info() {
        @NotNull String name = "INFO";
        @NotNull Level level = LoggerFactory.getInstance().getLevels().get(name).orElseThrow(() -> new NullPointerException("there's no '" + name + "' level at the current logging factory"));
        return registry(level);
    }
    default void info(@Nullable String message) {
        info().log(message);
    }

}
