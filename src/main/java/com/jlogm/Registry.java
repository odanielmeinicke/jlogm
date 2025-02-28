package com.jlogm;

import com.jlogm.fluent.Every;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.io.Serializable;
import java.time.OffsetDateTime;

public interface Registry {

    @NotNull Level getLevel();
    @NotNull OffsetDateTime getDate();

    @Nullable Every getEvery();
    @Nullable StackTraceElement getOrigin();

    @NotNull String getPrefix();
    @NotNull String getSuffix();

    @NotNull Formatter getFormatter();
    @Nullable Throwable getCause();

    @NotNull StackFilter @NotNull [] getStackFilters();
    @NotNull Marker @NotNull [] getMarkers();

    @Nullable Object getObject();

    boolean isSuppressed();

    @Override
    @NotNull String toString();

    // Classes

    interface Builder {
        @NotNull Builder level(@NotNull Level level);
        @NotNull Level getLevel();

        @NotNull Builder date(@NotNull OffsetDateTime date);
        @NotNull OffsetDateTime getDate();

        @NotNull Builder origin(@Nullable StackTraceElement origin);
        @Nullable StackTraceElement getOrigin();

        @NotNull Builder every(@NotNull Every every);
        @Nullable Every getEvery();

        @NotNull Builder prefix(@NotNull String prefix);
        @NotNull String getPrefix();

        @NotNull Builder suffix(@NotNull String suffix);
        @NotNull String getSuffix();

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
