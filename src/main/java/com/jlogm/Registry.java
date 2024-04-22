package com.jlogm;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;

public interface Registry extends Serializable, CharSequence {
    @NotNull Logger getLogger();
    @NotNull Instant getDate();

    boolean wasSuppressed();
}
