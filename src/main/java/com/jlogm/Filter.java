package com.jlogm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Filter {

    default @Nullable Object object(@NotNull Registry.Builder builder, @Nullable Object object) {
        return object;
    }

    boolean isSuppressed(@NotNull Registry.Builder registry, @Nullable Object object);
}