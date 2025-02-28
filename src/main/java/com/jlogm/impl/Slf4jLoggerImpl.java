package com.jlogm.impl;

import com.jlogm.Logger;
import com.jlogm.Registry.Builder;
import com.jlogm.factory.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

import java.io.Serializable;
import java.util.Objects;

public final class Slf4jLoggerImpl implements org.slf4j.Logger, Serializable {

    private final @NotNull String name;
    private final @NotNull StackTraceElement origin;

    public Slf4jLoggerImpl(@NotNull String name, @NotNull StackTraceElement origin) {
        this.name = name;
        this.origin = origin;
    }

    // Getters

    @Override
    public @NotNull String getName() {
        return name;
    }
    public @NotNull StackTraceElement getOrigin() {
        return origin;
    }
    public @NotNull String getFullyQualifiedCallerName() {
        return getOrigin().getClassName();
    }

    // Modules

    @Override
    public boolean isTraceEnabled() {
        return true;
    }
    @Override
    public void trace(String msg) {
        handle(Level.TRACE, null, msg, new Object[0], null);
    }
    @Override
    public void trace(String format, Object arg) {
        handle(Level.TRACE, null, format, new Object[] { arg }, null);
    }
    @Override
    public void trace(String format, Object arg1, Object arg2) {
        handle(Level.TRACE, null, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void trace(String format, Object... arguments) {
        handle(Level.TRACE, null, format, arguments, null);
    }
    @Override
    public void trace(String msg, Throwable t) {
        handle(Level.TRACE, null, msg, new Object[0], t);
    }

    @Override
    public boolean isTraceEnabled(@NotNull Marker marker) {
        return true;
    }
    @Override
    public void trace(Marker marker, String msg) {
        handle(Level.TRACE, marker, msg, new Object[0], null);
    }
    @Override
    public void trace(Marker marker, String format, Object arg) {
        handle(Level.TRACE, marker, format, new Object[] { arg }, null);
    }
    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        handle(Level.TRACE, marker, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        handle(Level.TRACE, marker, format, arguments, null);
    }
    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        handle(Level.TRACE, marker, msg, new Object[0], t);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }
    @Override
    public void debug(String msg) {
        handle(Level.DEBUG, null, msg, new Object[0], null);
    }
    @Override
    public void debug(String format, Object arg) {
        handle(Level.DEBUG, null, format, new Object[] { arg }, null);
    }
    @Override
    public void debug(String format, Object arg1, Object arg2) {
        handle(Level.DEBUG, null, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void debug(String format, Object... arguments) {
        handle(Level.DEBUG, null, format, arguments, null);
    }
    @Override
    public void debug(String msg, Throwable t) {
        handle(Level.DEBUG, null, msg, new Object[0], t);
    }

    @Override
    public boolean isDebugEnabled(@NotNull Marker marker) {
        return true;
    }
    @Override
    public void debug(Marker marker, String msg) {
        handle(Level.DEBUG, marker, msg, new Object[0], null);
    }
    @Override
    public void debug(Marker marker, String format, Object arg) {
        handle(Level.DEBUG, marker, format, new Object[] { arg }, null);
    }
    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        handle(Level.DEBUG, marker, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        handle(Level.DEBUG, marker, format, arguments, null);
    }
    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        handle(Level.DEBUG, marker, msg, new Object[0], t);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }
    @Override
    public void info(String msg) {
        handle(Level.INFO, null, msg, new Object[0], null);
    }
    @Override
    public void info(String format, Object arg) {
        handle(Level.INFO, null, format, new Object[] { arg }, null);
    }
    @Override
    public void info(String format, Object arg1, Object arg2) {
        handle(Level.INFO, null, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void info(String format, Object... arguments) {
        handle(Level.INFO, null, format, arguments, null);
    }
    @Override
    public void info(String msg, Throwable t) {
        handle(Level.INFO, null, msg, new Object[0], t);
    }

    @Override
    public boolean isInfoEnabled(@NotNull Marker marker) {
        return true;
    }
    @Override
    public void info(Marker marker, String msg) {
        handle(Level.INFO, marker, msg, new Object[0], null);
    }
    @Override
    public void info(Marker marker, String format, Object arg) {
        handle(Level.INFO, marker, format, new Object[] { arg }, null);
    }
    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        handle(Level.INFO, marker, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void info(Marker marker, String format, Object... arguments) {
        handle(Level.INFO, marker, format, arguments, null);
    }
    @Override
    public void info(Marker marker, String msg, Throwable t) {
        handle(Level.INFO, marker, msg, new Object[0], t);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }
    @Override
    public void warn(String msg) {
        handle(Level.WARN, null, msg, new Object[0], null);
    }
    @Override
    public void warn(String format, Object arg) {
        handle(Level.WARN, null, format, new Object[] { arg }, null);
    }
    @Override
    public void warn(String format, Object arg1, Object arg2) {
        handle(Level.WARN, null, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void warn(String format, Object... arguments) {
        handle(Level.WARN, null, format, arguments, null);
    }
    @Override
    public void warn(String msg, Throwable t) {
        handle(Level.WARN, null, msg, new Object[0], t);
    }

    @Override
    public boolean isWarnEnabled(@NotNull Marker marker) {
        return true;
    }
    @Override
    public void warn(Marker marker, String msg) {
        handle(Level.WARN, marker, msg, new Object[0], null);
    }
    @Override
    public void warn(Marker marker, String format, Object arg) {
        handle(Level.WARN, marker, format, new Object[] { arg }, null);
    }
    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        handle(Level.WARN, marker, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        handle(Level.WARN, marker, format, arguments, null);
    }
    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        handle(Level.WARN, marker, msg, new Object[0], t);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }
    @Override
    public void error(String msg) {
        handle(Level.ERROR, null, msg, new Object[0], null);
    }
    @Override
    public void error(String format, Object arg) {
        handle(Level.ERROR, null, format, new Object[] { arg }, null);
    }
    @Override
    public void error(String format, Object arg1, Object arg2) {
        handle(Level.ERROR, null, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void error(String format, Object... arguments) {
        handle(Level.ERROR, null, format, arguments, null);
    }
    @Override
    public void error(String msg, Throwable t) {
        handle(Level.ERROR, null, msg, new Object[0], t);
    }

    @Override
    public boolean isErrorEnabled(@NotNull Marker marker) {
        return true;
    }
    @Override
    public void error(Marker marker, String msg) {
        handle(Level.ERROR, marker, msg, new Object[0], null);
    }
    @Override
    public void error(Marker marker, String format, Object arg) {
        handle(Level.ERROR, marker, format, new Object[] { arg }, null);
    }
    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        handle(Level.ERROR, marker, format, new Object[] { arg1, arg2 }, null);
    }
    @Override
    public void error(Marker marker, String format, Object... arguments) {
        handle(Level.ERROR, marker, format, arguments, null);
    }
    @Override
    public void error(Marker marker, String msg, Throwable t) {
        handle(Level.ERROR, marker, msg, new Object[0], t);
    }

    private void handle(@NotNull Level level, @Nullable Marker marker, @Nullable String messagePattern, @Nullable Object @Nullable [] arguments, @Nullable Throwable throwable) {
        @NotNull LoggerFactory factory = LoggerFactory.getInstance();

        // Get the current jlogm level
        @NotNull com.jlogm.Level jlogmlevel = level == Level.ERROR ? com.jlogm.Level.SEVERE : com.jlogm.Level.valueOf(level.name());

        // Message
        @NotNull String message = MessageFormatter.basicArrayFormat(messagePattern, arguments);

        // Prepare the logger
        @NotNull Logger logger = factory.create(getName());
        @NotNull Builder builder = logger.registry(jlogmlevel);

        // Exceptions
        if (throwable != null) builder.cause(throwable);

        // Perform
        builder.log(message);
    }

    // Modules

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Slf4jLoggerImpl)) return false;
        @NotNull Slf4jLoggerImpl that = (Slf4jLoggerImpl) object;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getOrigin(), that.getOrigin());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOrigin());
    }

    @Override
    public @NotNull String toString() {
        return "Slf4jLoggerImpl{" +
                "name='" + name + '\'' +
                ", origin=" + origin +
                '}';
    }

}
