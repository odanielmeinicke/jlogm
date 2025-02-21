package com.jlogm.formatter;

import com.jlogm.Registry;
import com.jlogm.fluent.StackFilter;
import com.jlogm.impl.LoggerFactoryImpl;
import com.jlogm.utils.Colors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.jlogm.utils.Colors.*;

public final class DefaultFormatter implements Formatter {

    // Static initializers

    private static final @NotNull Map<String, Color> binds = new HashMap<String, Color>() {{
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

        put("warning", new Color(0xFFE300));

        put("unloading", new Color(0xFF9100));
        put("stopping", new Color(0xFF9100));
        put("disabling", new Color(0xFF9100));
        put("closing", new Color(0xFF9100));
        put("flushing", new Color(0xFF9100));
        put("pending", new Color(0xFF9100));
        put("inactivating", new Color(0xFF9100));
        put("ending", new Color(0xFF9100));

        put("disconnected", new Color(220, 0, 0));
        put("error", new Color(220, 0, 0));
        put("failed", new Color(220, 0, 0));
        put("fail", new Color(220, 0, 0));
        put("failure", new Color(220, 0, 0));
        put("exception", new Color(220, 0, 0));
        put("issue", new Color(220, 0, 0));
        put("cannot", new Color(220, 0, 0));
        put("won't", new Color(220, 0, 0));
        put("interrupted", new Color(220, 0, 0));
    }};

    public static @NotNull Map<String, Color> getBinds() {
        return binds;
    }

    // Object

    @Override
    public @NotNull String format(@NotNull Registry registry, @Nullable Object object) {
        // Message
        @NotNull StringBuilder content = new StringBuilder();

        @NotNull Predicate<String> url = string -> {
            @NotNull Pattern pattern = Pattern.compile("^(http(s?)://)?(((www\\.)?[a-zA-Z0-9.\\-_]+(\\.[a-zA-Z]{2,3})+)|(\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b))(/[a-zA-Z0-9_\\-\\s./?%#&=]*)?$");
            return pattern.matcher(string).find();
        };
        @NotNull Function<StackTraceElement[], StackTraceElement[]> traceFilter = elements -> {
            for (@NotNull StackFilter filter : registry.stackFilters()) {
                elements = filter.format(elements);
            }

            return elements;
        };

        // Content
        if (object != null) {
            @NotNull String[] parts = object.toString().replace("\r", "").split(" ", -1);

            for (int index = 0; index < parts.length; index++) {
                @NotNull String part = parts[index];
                @Nullable String color = null;

                if (binds.containsKey(part.toLowerCase())) {
                    color = Colors.colored(binds.get(part.toLowerCase()));
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
        if (registry.cause() != null) {
            @NotNull StackTraceElement[] traces = traceFilter.apply(registry.cause().getStackTrace());

            if (object != null) {
                content.append(System.lineSeparator());
            }

            @Nullable String message = registry.cause().getMessage() != null ? registry.cause().getMessage().replace("\r", "") : null;
            content.append(registry.cause().getClass().getName()).append(": ").append(message).append(System.lineSeparator());

            for (int index = 0; index < traces.length; index++) {
                if (index > 0) content.append(System.lineSeparator());
                content.append("\tat ").append(traces[index]);
            }

            @Nullable Throwable recurring = registry.cause().getCause();
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
        for (@NotNull Marker marker : registry.markers()) {
            markers.append(" ").append(marker);

            for (@NotNull Iterator<Marker> it = marker.iterator(); it.hasNext(); ) {
                @NotNull Marker children = it.next();
                markers.append(" ").append(children.toString());
            }

            if (row + 1 == registry.markers().length) markers.append(" ");
            row++;
        }

        // Date
        @NotNull SimpleDateFormat format = new SimpleDateFormat("yy-dd-MM HH:mm:ss.S");
        @NotNull String date = format.format(new Date(registry.getDate().toInstant().toEpochMilli()));
        date = String.format("%-" + 21 + "s", date);

        // Source
        @Nullable String source = null;

        if (registry.origin() != null) {
            @NotNull String[] sources = registry.origin().getClassName().split("\\.");
            source = sources[sources.length - 1] + (registry.origin().getLineNumber() >= 0 ? ":" + registry.origin().getLineNumber() : "");
        }

        // Colors
        @Nullable Color color = LoggerFactoryImpl.getColor(registry.getLevel());
        @NotNull String level = (color != null ? Colors.colored(color, registry.getLevel().getName()) : registry.getLevel().getName());

        // Generate message
        @NotNull String message = bold(colored(new Color(65, 65, 65), "| ")) + date + " " + level + " " + markers + (source != null ? " " + source : "") + " - " + content;

        // Finish
        return registry.prefix() + message + registry.suffix();
    }
}
