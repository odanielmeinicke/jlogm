package com.jlogm.impl;

import com.jlogm.Level;
import com.jlogm.Registry;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.factory.LoggerFactory.Registries;
import com.jlogm.fluent.Every;
import com.jlogm.fluent.LogOrigin;
import com.jlogm.fluent.StackFilter;
import com.jlogm.utils.Colors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Marker;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.jlogm.utils.Colors.*;

public final class RegistryImpl implements Registry {

    // Static initializers

    private static final @NotNull Map<String, Color> colors = new HashMap<String, Color>() {{
        put("loading", new Color(0, 180, 255));
        put("initializing", new Color(0, 180, 255));
        put("generating", new Color(0, 180, 255));
        put("saving", new Color(0, 180, 255));
        put("enabling", new Color(0, 180, 255));
        put("uploading", new Color(0, 180, 255));
        put("importing", new Color(0, 180, 255));
        put("localhost", new Color(0, 180, 255));
        put("127.0.0.1", new Color(0, 180, 255));
        put("running", new Color(0, 180, 255));
        put("loaded", new Color(0, 180, 255));

        put("successfully", new Color(0, 180, 0));
        put("connected", new Color(0, 180, 0));
        put("success", new Color(0, 180, 0));
        put("initialized", new Color(0, 180, 0));
        put("saved", new Color(0, 180, 0));
        put("enabled", new Color(0, 180, 0));
        put("unloaded", new Color(0, 180, 0));
        put("uploaded", new Color(0, 180, 0));
        put("imported", new Color(0, 180, 0));
        put("done", new Color(0, 180, 0));

        put("warning", Color.YELLOW);

        put("unloading", Color.ORANGE);
        put("stopping", Color.ORANGE);
        put("disabling", Color.ORANGE);

        put("disconnected", new Color(220, 0, 0));
        put("error", new Color(220, 0, 0));
        put("failed", new Color(220, 0, 0));
        put("fail", new Color(220, 0, 0));
        put("failure", new Color(220, 0, 0));
        put("exception", new Color(220, 0, 0));
        put("issue", new Color(220, 0, 0));
        put("cannot", new Color(220, 0, 0));
    }};

    // Object

    private final @NotNull Level level;

    private final @NotNull OffsetDateTime date;

    private @UnknownNullability Throwable throwable;

    private transient @NotNull StackFilter @NotNull [] stackFilters;
    private @NotNull Marker @NotNull [] markers;

    private transient @UnknownNullability Every every;
    private @NotNull LogOrigin origin;

    private @Nullable Object object;

    private boolean suppressed = false;

    RegistryImpl(@NotNull Level level, @NotNull OffsetDateTime date, @NotNull StackFilter @NotNull [] stackFilters, @NotNull Marker @NotNull [] markers, @UnknownNullability Every every) {
        this.level = level;
        this.date = date;
        this.stackFilters = stackFilters;
        this.markers = markers;
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
    public @NotNull Registry withOrigin(@NotNull LogOrigin origin) {
        this.origin = origin;
        return this;
    }
    @Override
    public @NotNull LogOrigin getOrigin() {
        return origin;
    }

    @Override
    public @NotNull Registry every(@NotNull Every every) {
        this.every = every;
        return this;
    }
    @Override
    public @NotNull Every getEvery() {
        return every;
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
    public @NotNull Throwable getCause() {
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
    public @NotNull Registry markers(@NotNull Marker @NotNull ... markers) {
        this.markers = markers;
        return this;
    }
    @Override
    public @NotNull Marker @NotNull [] getMarkers() {
        return markers;
    }

    public @Nullable Object getObject() {
        return object;
    }

    // Modules

    @Override
    public void log(@Nullable Object object) {
        // Message
        @NotNull StringBuilder content = new StringBuilder();

        @NotNull Predicate<String> url = string -> {
            @NotNull Pattern pattern = Pattern.compile("^(http(s?)://)?(((www\\.)?[a-zA-Z0-9.\\-_]+(\\.[a-zA-Z]{2,3})+)|(\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b))(/[a-zA-Z0-9_\\-\\s./?%#&=]*)?$");
            return pattern.matcher(string).find();
        };
        @NotNull Function<StackTraceElement[], StackTraceElement[]> traceFilter = elements -> {
            for (@NotNull StackFilter filter : stackFilters) {
                elements = filter.format(elements);
            }

            return elements;
        };

        // Content
        if (object != null) {
            @NotNull String[] parts = object.toString().replace("\r", "").split(" ");

            for (int index = 0; index < parts.length; index++) {
                @NotNull String part = parts[index];
                @Nullable String color = null;

                if (colors.containsKey(part.toLowerCase())) {
                    color = Colors.colored(colors.get(part.toLowerCase()));
                } if (url.test(part)) {
                    color = color + Colors.underlined();
                }

                content.append(color != null ? color : "")
                        .append(part)
                        .append(color != null ? reset() : "");

                if (index + 1 != parts.length) {
                    content.append(" ");
                }
            }
        }
        if (throwable != null) {
            @NotNull StackTraceElement[] traces = traceFilter.apply(throwable.getStackTrace());

            if (object != null) {
                content.append(System.lineSeparator());
            }

            @Nullable String message = throwable.getMessage() != null ? throwable.getMessage().replace("\r", "") : null;
            content.append(throwable.getClass().getName()).append(": ").append(message).append(System.lineSeparator());

            for (int index = 0; index < traces.length; index++) {
                if (index > 0) content.append(System.lineSeparator());
                content.append("\tat ").append(traces[index]);
            }

            @Nullable Throwable recurring = throwable.getCause();
            while (recurring != null) {
                traces = traceFilter.apply(recurring.getStackTrace());

                content.append(System.lineSeparator());

                message = recurring.getMessage() != null ? recurring.getMessage().replace("\r", "") : null;
                content.append("Caused by ").append(recurring.getClass().getName()).append(": ").append(message).append(System.lineSeparator());

                for (int index = 0; index < traces.length; index++) {
                    if (index > 0) content.append(System.lineSeparator());
                    content.append("\tat ").append(traces[index]);
                }

                recurring = recurring.getCause();
            }
        }

        // Markers
        @NotNull StringBuilder markers = new StringBuilder();
        int row = 0;
        for (@NotNull Marker marker : this.markers) {
            markers.append(Colors.underlined(marker.toString())).append(" ");

            for (@NotNull Iterator<Marker> it = marker.iterator(); it.hasNext(); ) {
                @NotNull Marker children = it.next();
                markers.append(Colors.underlined(children.toString()));
                if (it.hasNext()) markers.append(" ");
            }

            if (row + 1 >= this.markers.length) markers.append(" ");
            row++;
        }

        // Date
        @NotNull SimpleDateFormat format = new SimpleDateFormat("yy-dd-MM HH:mm:ss.S");
        @NotNull String date = format.format(new Date(getDate().toInstant().toEpochMilli()));
        date = String.format("%-" + 21 + "s", date);

        // Source
        @NotNull String[] sources = origin.getClassName().split("\\.");
        @NotNull String source = sources[sources.length - 1] + (origin.getLineNumber() >= 0 ? ":" + origin.getLineNumber() : "");

        // Generate message
        @NotNull String message = bold(colored(new Color(65, 65, 65), "| ")) + date + " " + level + "  " + markers + source + " - " + content;

        // Filters and suppression
        this.object = object;

        if (LoggerFactory.getInstance().getFilters().isSuppressed(this)) {
            setSuppressed(true);
        } else if (every != null && !every.canLog(LoggerFactory.getInstance(), this)) {
            setSuppressed(true);
        }

        @Nullable Registries registries = LoggerFactory.getInstance().getRegistries();
        if (registries != null) {
            registries.add(this);
        }

        // Finish
        if (isSuppressed()) return;
        System.out.println(message);
    }

    // Implementations

    @Override
    public @NotNull String toString() {
        return "RegistryImpl{" +
                "level=" + level +
                ", date=" + date +
                ", throwable=" + throwable +
                ", stackFilters=" + Arrays.toString(stackFilters) +
                ", markers=" + Arrays.toString(markers) +
                ", every=" + every +
                ", origin=" + origin +
                ", suppressed=" + suppressed +
                '}';
    }

}
