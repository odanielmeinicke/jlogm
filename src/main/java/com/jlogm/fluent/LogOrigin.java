package com.jlogm.fluent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the origin or source of a log entry, providing information about the class, method,
 * file, and line number where the log statement was generated.
 */
public interface LogOrigin extends Serializable {

    // Static initializers

    /**
     * Creates a new LogOrigin instance with the specified class name, file name, method name, and
     * line number.
     *
     * @param className  The name of the class where the log statement originates.
     * @param fileName   The name of the file where the log statement originates, can be null.
     * @param methodName The name of the method where the log statement originates.
     * @param line       The line number in the file where the log statement originates.
     * @return A LogOrigin instance representing the origin of the log statement.
     */
    static @NotNull LogOrigin create(@NotNull String className, @Nullable String fileName,
                                     @NotNull String methodName, int line) {
        return new LogOrigin() {

            @Override
            public @NotNull String getClassName() {
                return className;
            }

            @Override
            public @Nullable String getFileName() {
                return fileName;
            }

            @Override
            public @NotNull String getMethodName() {
                return methodName;
            }

            @Override
            public int getLineNumber() {
                return line;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof LogOrigin) {
                    @NotNull LogOrigin origin = (LogOrigin) obj;
                    return origin.getClassName().equals(getClassName()) &&
                            Objects.equals(origin.getFileName(), getFileName()) &&
                            origin.getMethodName().equals(getMethodName()) &&
                            origin.getLineNumber() == getLineNumber();
                }

                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(getClassName(), getFileName(), getMethodName(), (Integer) getLineNumber());
            }

            @Override
            public @NotNull String toString() {
                return new StackTraceElement(getClassName(), getMethodName(), getFileName(), getLineNumber()).toString();
            }
        };
    }

    // Object

    /**
     * Gets the name of the class where the log statement originates.
     *
     * @return The class name.
     */
    @NotNull String getClassName();

    /**
     * Gets the name of the file where the log statement originates.
     *
     * @return The file name which can be null.
     */
    @Nullable String getFileName();

    /**
     * Gets the name of the method where the log statement originates.
     *
     * @return The method name.
     */
    @NotNull String getMethodName();

    /**
     * Gets the line number in the file where the log statement originates.
     *
     * @return The line number.
     */
    int getLineNumber();

}