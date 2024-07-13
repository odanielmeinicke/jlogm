package com.jlogm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Level is an indicator of the log type. It is used to categorize the logs,
 * allowing for filtering and differentiated formatting. Each log message is associated
 * with a level, which corresponds to the severity of the log.
 */
public interface Level extends Serializable {

    // Static initializers

    @NotNull Level TRACE  = create("TRACE");
    @NotNull Level INFO = create("INFO");
    @NotNull Level SEVERE = create("SEVERE");
    @NotNull Level WARN = create("WARN");
    @NotNull Level DEBUG = create("DEBUG");

    static @NotNull Level create(final @NotNull String name) {
        return new Level() {

            // Getters

            @Override
            public @NotNull String getName() {
                return name;
            }

            // Implementations

            @Override
            public boolean equals(@Nullable Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                @NotNull Level that = (Level) object;
                return Objects.equals(getName(), that.getName());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getName());
            }

            @Override
            public @NotNull String toString() {
                return getName().toUpperCase();
            }

        };
    }

    // Object

    /**
     * Returns the name/identifier of the level. This is how the level will be displayed
     * in the console. The name is typically a standard designation such as "INFO", "DEBUG",
     * "WARN", or "ERROR".
     * @return The name of the level.
     */
    @NotNull String getName();

}
