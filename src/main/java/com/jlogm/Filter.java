package com.jlogm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Filter {
    boolean isSuppressed(@NotNull Registry registry, @Nullable Object object);
}
