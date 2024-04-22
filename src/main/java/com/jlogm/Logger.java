package com.jlogm;

import com.jlogm.fluent.Every;
import com.jlogm.fluent.LogOrigin;
import com.jlogm.fluent.StackFilter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public interface Logger extends Serializable {

    @NotNull Level getLevel();

    @Contract("_ -> this")
    @NotNull Logger withOrigin(@NotNull LogOrigin origin);

    @Contract("_ -> this")
    @NotNull Logger every(@NotNull Every every);

    @Contract("_ -> this")
    default @NotNull Logger withCause(@NotNull Throwable throwable) {
        return withCause(throwable, StackFilter.FULL);
    }
    @Contract("_,_ -> this")
    @NotNull Logger withCause(@NotNull Throwable throwable, @NotNull StackFilter @NotNull ... filters);

    @Nullable Registry log();
    @Nullable Registry log(@Nullable Object object);

}
