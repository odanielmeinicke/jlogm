package com.jlogm;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Level is an indicator of the log type. It is used to categorize the logs,
 * allowing for filtering and differentiated formatting. Each log message is associated
 * with a level, which corresponds to the severity of the log.
 */
public interface Level extends Serializable {

    // Static initializers

    static @NotNull Level create(final @NotNull String name, final @NotNull Color color) {
        return new Level() {
            @Override
            public @NotNull String getName() {
                return name;
            }
            @Override
            public @NotNull Color getColor() {
                return color;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Level) {
                    @NotNull Level level = (Level) obj;
                    return level.getName().equalsIgnoreCase(name);
                }

                return false;
            }
            @Override
            public int hashCode() {
                return Objects.hash(getName().toLowerCase());
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

    /**
     * The color that will be used to print this level in the console. Different levels
     * might use different colors for better visual distinction. For example, an "ERROR"
     * level might be printed in red while an "INFO" level might be printed in blue.
     * @return The color for the level.
     */
    @NotNull Color getColor();
}
