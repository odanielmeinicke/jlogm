package com.jlogm.factory;

import com.jlogm.Filter;
import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.Registry;
import com.jlogm.impl.LoggerFactoryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Flushable;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A factory interface for creating loggers with specific configurations.
 */
public interface LoggerFactory {

    // Static initializers

    static @NotNull LoggerFactory getInstance() {
        return LoggerFactoryImpl.instance;
    }
    static void setInstance(@NotNull LoggerFactory factory) {
        LoggerFactoryImpl.instance = factory;
    }

    // Object

    /**
     * Retrieves the iterable interface of the list of levels that this logger factory has.
     *
     * @return An iterable interface for accessing the list of levels.
     */
    @NotNull Levels getLevels();

    /**
     * Retrieves the iterable interface of the list of filters that this logger factory has.
     *
     * @return An iterable interface for accessing the list of filters.
     */
    @NotNull Filters getFilters();

    /**
     * The list of registries where all the log entries sent using this logger factory is stored.
     * <p>
     * If the list of registries is null, the logger factory will not store any of the sent log entries.
     *
     * @return The list of registries, or null if no registries are available.
     */
    @Nullable Registries getRegistries();

    /**
     * Sets the list of registries. If there is a previous list of registries, it will be flushed using the #flush method.
     *
     * @param registries The new list of registries to be set. It can be null if no registries are to be set.
     * @throws IOException Thrown when cannot flush the old registries
     */
    void setRegistries(@Nullable Registries registries) throws IOException;

    // Builders

    /**
     * Creates a logger using a specific name.
     *
     * @param name The name to be associated with the logger.
     * @return A new Logger instance with the specified name.
     */
    @NotNull Logger create(@NotNull String name);

    // Classes

    /**
     * An interface for accessing levels within the logger factory.
     */
    interface Levels extends Iterable<Level> {
        /**
         * Retrieves a level by its name.
         *
         * @param name The name of the level to retrieve.
         * @return An Optional containing the level if found, or an empty Optional if not found.
         */
        default @NotNull Optional<Level> get(@NotNull String name) {
            return stream().filter(level -> level.getName().equalsIgnoreCase(name)).findFirst();
        }

        /**
         * Provides a Stream interface for accessing the levels.
         *
         * @return A Stream interface for the levels.
         */
        @NotNull Stream<Level> stream();
    }

    /**
     * An interface for accessing filters within the logger factory.
     */
    interface Filters extends Iterable<Filter> {

        /**
         * Checks if a registry with the passed object is suppressed by any of the filters.
         *
         * @param registry The registry to check.
         * @param object The object to check.
         * @return True if the registry is suppressed, otherwise false.
         */
        default boolean isSuppressed(@NotNull Registry registry, @Nullable Object object) {
            return stream().anyMatch(filter -> filter.isSuppressed(registry, object));
        }

        /**
         * Adds a filter to the list.
         *
         * @param filter The filter to add.
         * @return True if the filter was added successfully, otherwise false.
         */
        boolean add(@NotNull Filter filter);

        /**
         * Removes a filter from the list.
         *
         * @param filter The filter to remove.
         * @return True if the filter was removed successfully, otherwise false.
         */
        boolean remove(@NotNull Filter filter);

        /**
         * Checks if a filter is present in the list.
         *
         * @param filter The filter to check.
         * @return True if the filter is present, otherwise false.
         */
        boolean contains(@NotNull Filter filter);

        /**
         * Gets the number of filters in the list.
         *
         * @return The number of filters.
         */
        int size();

        /**
         * Provides a Stream interface for accessing the filters.
         *
         * @return A Stream interface for the filters.
         */
        @NotNull Stream<Filter> stream();
    }

    /**
     * An interface for accessing registries within the logger factory.
     */
    interface Registries extends Iterable<Registry>, Flushable {
        /**
         * Adds a registry to the list.
         *
         * @param registry The registry to add.
         * @return True if the registry was added successfully, otherwise false.
         */
        boolean add(@NotNull Registry registry);

        /**
         * Removes a registry from the list.
         *
         * @param registry The registry to remove.
         * @return True if the registry was removed successfully, otherwise false.
         */
        boolean remove(@NotNull Registry registry);

        /**
         * Checks if a registry is present in the list.
         *
         * @param registry The filter to check.
         * @return True if the registry is present, otherwise false.
         */
        boolean contains(@NotNull Registry registry);

        /**
         * Provides a Stream interface for accessing the registries.
         *
         * @return A Stream interface for the registries.
         */
        @NotNull Stream<Registry> stream();

        /**
         * Flushes and clears all the registries.
         *
         * @throws IOException If an I/O error occurs while flushing the registries.
         */
        @Override
        void flush() throws IOException;
    }

}