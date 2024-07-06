package com.jlogm.fluent;

import com.jlogm.Registry;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.factory.LoggerFactory.Registries;
import com.jlogm.impl.RegistryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

public interface Every {

    // Static initializers

    static @NotNull Every period(@NotNull Duration duration) {
        return (factory, registry) -> {
            @Nullable Registries registries = factory.getRegistries();
            if (registries == null) {
                throw new UnsupportedOperationException("the 'times' every function can only be used on logger factories with registries");
            }

            @Nullable Registry last = registries.stream()
                    .filter(second -> !second.isSuppressed())
                    .filter(second -> Arrays.equals(second.getMarkers(), registry.getMarkers()))
                    .filter(second -> second.getLevel().equals(registry.getLevel()))
                    .filter(second -> {
                        if ((second instanceof RegistryImpl && registry instanceof RegistryImpl)) {
                            return Objects.equals(((RegistryImpl) second).getObject(), ((RegistryImpl) registry).getObject());
                        }
                        return false;
                    })
                    .filter(second -> second.getOrigin().equals(registry.getOrigin()))
                    .reduce((first, second) -> second)
                    .orElse(null);

            return last == null || last.getDate().plus(duration).isBefore(OffsetDateTime.now());
        };
    }
    static @NotNull Every times(@Range(from = 0, to = Integer.MAX_VALUE) int number) {
        return (factory, registry) -> {
            @Nullable Registries registries = factory.getRegistries();
            if (registries == null) {
                throw new UnsupportedOperationException("the 'times' every function can only be used on logger factories with registries");
            }

            long count = registries.stream()
                    .filter(second -> Arrays.equals(second.getMarkers(), registry.getMarkers()))
                    .filter(second -> second.getLevel().equals(registry.getLevel()))
                    .filter(second -> {
                        if ((second instanceof RegistryImpl && registry instanceof RegistryImpl)) {
                            return Objects.equals(((RegistryImpl) second).getObject(), ((RegistryImpl) registry).getObject());
                        }
                        return false;
                    })
                    .filter(second -> second.getOrigin().equals(registry.getOrigin()))
                    .count();

            return (count != 0 && (count - 1) % (number + 1) != 0);
        };
    }

    // Object

    boolean canLog(@NotNull LoggerFactory factory, @NotNull Registry registry);

}
