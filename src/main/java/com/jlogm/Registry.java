package com.jlogm;

import com.jlogm.fluent.Every;
import com.jlogm.fluent.LogOrigin;
import com.jlogm.fluent.StackFilter;
import com.jlogm.formatter.Formatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.io.Serializable;
import java.time.OffsetDateTime;

public interface Registry extends Serializable {
    @NotNull Level getLevel();
    @NotNull OffsetDateTime getDate();

    @NotNull Registry withOrigin(@Nullable LogOrigin origin);
    @Nullable LogOrigin getOrigin();

    @NotNull Registry every(@NotNull Every every);
    @Nullable Every getEvery();

    @NotNull Registry prefix(@NotNull String prefix);
    @NotNull String getPrefix();

    @NotNull Registry suffix(@NotNull String suffix);
    @NotNull String getSuffix();

    @NotNull Registry formatter(@NotNull Formatter formatter);
    @NotNull Formatter getFormatter();

    default @NotNull Registry withCause(@NotNull Throwable throwable) {
        return withCause(throwable, StackFilter.FULL);
    }
    @NotNull Registry withCause(@NotNull Throwable throwable, @NotNull StackFilter @NotNull ... filters);
    @Nullable Throwable getCause();

    @NotNull Registry stackFilters(@NotNull StackFilter @NotNull ... stackFilters);
    @NotNull StackFilter @NotNull [] getStackFilters();

    @NotNull Registry marker(@NotNull Marker marker);
    @NotNull Registry markers(@NotNull Marker @NotNull ... markers);
    @NotNull Marker @NotNull [] getMarkers();

    default void log() {
        log(null);
    }
    void log(@Nullable Object object);

    boolean isSuppressed();
    void setSuppressed(boolean suppressed);

}
