package com.jlogm;

import org.jetbrains.annotations.NotNull;

/**
 * A Level is an indicator of the log type. It is used to categorize the logs,
 * allowing for filtering and differentiated formatting. Each log message is associated
 * with a level, which corresponds to the severity of the log.
 *
 * <p>This enum defines a set of standard logging levels with default values.
 * The levels provided are:</p>
 * <ul>
 *   <li>{@code TRACE} - Detailed tracing messages for in-depth debugging.</li>
 *   <li>{@code INFO} - Informational messages that highlight the progress of the application.</li>
 *   <li>{@code SEVERE} - Indicates a serious failure or error condition.</li>
 *   <li>{@code WARN} - Potentially harmful situations that are not immediately fatal.</li>
 *   <li>{@code DEBUG} - Fine-grained messages used for debugging purposes.</li>
 * </ul>
 *
 * <p>Each level has an inherent name which can be retrieved using the {@code getName()} method.
 * The {@code toString()} method is overridden to return the uppercase name of the level,
 * ensuring consistency in log outputs.</p>
 */
public enum Level {

    /**
     * Trace level logging. Intended for very fine-grained and detailed tracing messages.
     */
    TRACE,

    /**
     * Info level logging. Used for informational messages that communicate the general progress of the application.
     */
    INFO,

    /**
     * Severe level logging. Represents serious error conditions that may prevent the application from continuing.
     */
    SEVERE,

    /**
     * Warning level logging. Used for potentially harmful situations that should be noted but do not necessarily
     * require immediate intervention.
     */
    WARN,

    /**
     * Debug level logging. Provides detailed diagnostic information useful during development and debugging.
     */
    DEBUG;

    // Getters

    /**
     * Returns the name/identifier of the log level.
     * This method provides the standard designation of the level as a String.
     * For example, calling {@code getName()} on {@link Level#INFO} returns {@code "INFO"}.
     *
     * @return a non-null String representing the name of the level.
     */
    @NotNull
    public String getName() {
        return name();
    }

    // Implementations

    /**
     * Returns a string representation of the log level.
     * The returned value is the uppercase name of the level, ensuring consistency in log output.
     *
     * @return a non-null uppercase String representation of the level.
     */
    @Override
    public String toString() {
        return name();
    }

}