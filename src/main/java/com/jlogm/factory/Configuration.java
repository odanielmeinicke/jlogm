package com.jlogm.factory;

import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.Registry;
import com.jlogm.fluent.LogOrigin;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.time.Instant;

/**
 * A configuration interface used by logging factories to indicate
 * how logging should be printed to the console.
 *
 * @author Daniel Richard (Laivy)
 * @since 1.0
 */
public interface Configuration {

    /**
     * Converts the source of a log entry into a valid string representation.
     *
     * @param origin the source of the log entry
     * @return a valid string representation of the log source
     */
    @NotNull String toString(@NotNull LogOrigin origin);

    /**
     * Converts a date and time into a valid string representation.
     *
     * @param instant the date and time to be converted
     * @return a valid string representation of the date and time
     */
    @NotNull String toString(@NotNull Instant instant);

    /**
     * Converts a logging level into a valid string representation.
     *
     * @param level the logging level to be converted
     * @return a valid string representation of the logging level
     */
    @NotNull String toString(@NotNull Level level);

    /**
     * Converts a message and a logger into a valid log entry.
     *
     * @param logger the logger associated with the message
     * @param message the message to be logged
     * @return a valid log entry
     */
    @NotNull Registry toString(@NotNull Logger logger, @NotNull String message);

    /**
     * Prints a log entry.
     *
     * @param registry the log entry to be printed
     */
    void print(@NotNull Registry registry);

    /**
     * Reads a configuration file from the given input stream.
     *
     * @param stream the input stream from which the configuration file is read
     */
    void read(@NotNull InputStream stream);

}