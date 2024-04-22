package com.jlogm;

import org.jetbrains.annotations.NotNull;

public interface Filter {
    boolean isSuppressed(@NotNull Registry registry);
}
