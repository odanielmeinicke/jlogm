package com.jlogm;

import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static com.jlogm.JsonUtils.escapeJson;

public interface Registry {

    @NotNull Map<String, Object> getContext();
    @NotNull Set<String> getStack();

    @NotNull Level getLevel();
    @NotNull Instant getInstant();

    @Nullable Every getEvery();
    @Nullable StackTraceElement getOrigin();

    @Nullable String getPrefix();
    @Nullable String getSuffix();

    @NotNull Formatter getFormatter();
    @Nullable Throwable getCause();

    @NotNull StackFilter @NotNull [] getStackFilters();
    @NotNull Marker @NotNull [] getMarkers();

    @Nullable Object getObject();

    boolean isSuppressed();

    @Override
    @NotNull String toString();

    default @NotNull String toJson() {
        @NotNull StringBuilder builder = new StringBuilder("{");
        builder.append("\"level\":\"").append(escapeJson(getLevel().getName())).append("\",");
        builder.append("\"date\":").append(getInstant().toEpochMilli()).append(",");

        if (getOrigin() != null) {
            builder.append("\"origin\":\"").append(escapeJson(getOrigin().toString())).append("\",");
        } if (getPrefix() != null) {
            builder.append("\"prefix\":\"").append(escapeJson(getPrefix())).append("\",");
        } if (getSuffix() != null) {
            builder.append("\"suffix\":\"").append(escapeJson(getSuffix())).append("\",");
        }

        // Serialize cause
        if (getCause() != null) {
            builder.append("\"causes\":[");

            @NotNull Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
            @Nullable Throwable curr = getCause();

            while (curr != null && seen.add(curr)) {
                builder.append("{");

                // Type
                builder.append("\"type\":\"").append(escapeJson(curr.getClass().getName())).append("\",");

                // Message
                if (curr.getMessage() == null) {
                    builder.append("\"message\":null,");
                } else {
                    builder.append("\"message\":\"").append(escapeJson(curr.getMessage())).append("\",");
                }

                // Stack trace as json array
                builder.append("\"stackTrace\":[");

                @NotNull StackTraceElement[] st = curr.getStackTrace();
                for (@NotNull StackTraceElement e : st) {
                    builder.append("\"").append(escapeJson(e.toString())).append("\",");
                }

                if (st.length > 0) builder.setLength(builder.length() - 1); // Removes last comma
                builder.append("]");

                builder.append("},");
                curr = curr.getCause();
            }

            // Removes last comma if exists
            if (builder.charAt(builder.length() - 1) == ',') {
                builder.setLength(builder.length() - 1);
            }

            // Finish
            builder.append("],");
        }

        // Context
        if (!getContext().isEmpty()) {
            builder.append("\"context\":{");

            boolean first = true;
            for (@NotNull Map.Entry<String, Object> entry : getContext().entrySet()) {
                @NotNull String key = escapeJson(entry.getKey());
                @NotNull String value = (entry.getValue() != null ? "\"" + escapeJson(entry.getValue().toString()) + "\"" : "null");

                if (!first) builder.append(",");
                builder.append("\"").append(key).append("\":").append(value);
                first = false;
            }

            // Finish
            builder.append("},");
        }

        // Stack
        if (!getStack().isEmpty()) {
            // Reverse stack collection
            @NotNull List<String> stackSet = new LinkedList<>(getStack());
            Collections.reverse(stackSet);

            // Serialize it
            builder.append("\"stack\":[");

            boolean first = true;
            for (@NotNull String stack : stackSet) {
                if (!first) builder.append(",");
                builder.append("\"").append(escapeJson(stack)).append("\"");
                first = false;
            }

            // Finish
            builder.append("],");
        }

        // Message
        if (getObject() != null) {
            builder.append("\"object\":\"").append(escapeJson(String.valueOf(getObject()))).append("\"");
        } else {
            builder.append("\"object\":null");
        }

        builder.append("}");

        // Finish
        return builder.toString();
    }

    // Classes

    interface Builder {
        @NotNull Builder level(@NotNull Level level);
        @NotNull Level getLevel();

        @NotNull Builder instant(@NotNull Instant instant);
        @NotNull Instant getInstant();

        @NotNull Builder origin(@Nullable StackTraceElement origin);
        @Nullable StackTraceElement getOrigin();

        @NotNull Builder every(@NotNull Every every);
        @Nullable Every getEvery();

        @NotNull Builder prefix(@Nullable String prefix);
        @Nullable String getPrefix();

        @NotNull Builder suffix(@Nullable String suffix);
        @Nullable String getSuffix();

        @NotNull Builder formatter(@NotNull Formatter formatter);
        @NotNull Formatter getFormatter();

        default @NotNull Builder cause(@NotNull Throwable throwable) {
            return cause(throwable, StackFilter.FULL);
        }
        @NotNull Builder cause(@NotNull Throwable throwable, @NotNull StackFilter @NotNull ... filters);
        @Nullable Throwable getCause();

        @NotNull Builder stackFilters(@NotNull StackFilter @NotNull ... stackFilters);
        @NotNull StackFilter @NotNull [] getStackFilters();

        @NotNull Builder marker(@NotNull Marker marker);
        @NotNull Builder marker(@NotNull String name);
        @NotNull Builder marker(@NotNull String name, @NotNull Color color);
        @NotNull Builder markers(@NotNull Marker @NotNull ... markers);
        @NotNull Marker @NotNull [] getMarkers();

        default @NotNull Registry log() {
            return log(null);
        }
        @NotNull Registry log(@Nullable Object object);

        boolean isSuppressed();
        void setSuppressed(boolean suppressed);
    }

}
