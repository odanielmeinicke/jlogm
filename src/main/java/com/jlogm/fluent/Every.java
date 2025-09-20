package com.jlogm.fluent;

import com.jlogm.Registry;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.factory.LoggerFactory.Registries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public interface Every {

    // Static initializers

    static @NotNull Every period(@NotNull Duration duration) {
        return (factory, registry, object) -> {
            @Nullable Registries registries = factory.getRegistries();
            if (registries == null) {
                throw new UnsupportedOperationException("the 'times' every function can only be used on logger factories with registries");
            }

            @Nullable Registry last = registries.stream()
                    .filter(second -> !second.isSuppressed())
                    .filter(second -> Arrays.equals(second.getMarkers(), registry.getMarkers()))
                    .filter(second -> second.getLevel().equals(registry.getLevel()))
                    .filter(second -> Objects.equals(second.getObject(), object))
                    .filter(second -> Objects.equals(second.getOrigin(), registry.getOrigin()))
                    .reduce((first, second) -> second)
                    .orElse(null);

            return last == null || last.getInstant().plus(duration).isBefore(Instant.now());
        };
    }
    static @NotNull Every times(@Range(from = 0, to = Integer.MAX_VALUE) int number) {
        return (factory, registry, object) -> {
            @Nullable Registries registries = factory.getRegistries();
            if (registries == null) {
                throw new UnsupportedOperationException("the 'times' every function can only be used on logger factories with registries");
            }

            long count = registries.stream()
                    .filter(second -> Arrays.equals(second.getMarkers(), registry.getMarkers()))
                    .filter(second -> second.getLevel().equals(registry.getLevel()))
                    .filter(second -> Objects.equals(second.getObject(), object))
                    .filter(second -> Objects.equals(second.getOrigin(), registry.getOrigin()))
                    .count();

            return (count != 0 && (count - 1) % (number + 1) != 0);
        };
    }

    // Object

    boolean canLog(@NotNull LoggerFactory factory, @NotNull Registry.Builder builder, @Nullable Object object);

}
