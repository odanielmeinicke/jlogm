package com.jlogm.fluent;

import com.jlogm.Registry;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.factory.LoggerFactory.Registries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.time.Instant;

public interface Every {

    // Static initializers

    static @NotNull Every period(@NotNull Duration duration) {
        return new Every() {
            @Override
            public boolean canLog(@NotNull LoggerFactory factory, @NotNull Registry registry) {
                @Nullable Registries registries = factory.getRegistries();
                if (registries == null) {
                    throw new UnsupportedOperationException("the 'times' every function can only be used on logger factories with registries");
                }

                @Nullable Registry last = registries.stream()
                        .filter(second -> !second.wasSuppressed())
                        .filter(second -> second.getLogger().equals(registry.getLogger()))
                        .reduce((first, second) -> second)
                        .orElse(null);

                return last == null || last.getDate().plus(duration).isBefore(Instant.now());
            }

            @Override
            public boolean equals(@Nullable Object obj) {
                return super.equals(obj) || obj instanceof Every;
            }
        };
    }
    static @NotNull Every times(@Range(from = 0, to = Integer.MAX_VALUE) int number) {
        return new Every() {
            @Override
            public boolean canLog(@NotNull LoggerFactory factory, @NotNull Registry registry) {
                @Nullable Registries registries = factory.getRegistries();
                if (registries == null) {
                    throw new UnsupportedOperationException("the 'times' every function can only be used on logger factories with registries");
                }

                long count = registries.stream().filter(second -> second.getLogger().equals(registry.getLogger())).count();
                return (count != 0 && (count - 1) % (number + 1) != 0);
            }

            @Override
            public boolean equals(@Nullable Object obj) {
                return super.equals(obj) || obj instanceof Every;
            }
        };
    }

    // Object

    boolean canLog(@NotNull LoggerFactory factory, @NotNull Registry registry);

}
