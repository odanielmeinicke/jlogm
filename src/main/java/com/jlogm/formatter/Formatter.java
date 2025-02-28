package com.jlogm.formatter;

import com.jlogm.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@FunctionalInterface
public interface Formatter {
    @NotNull String format(@NotNull Registry registry);

    default @NotNull Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

}
