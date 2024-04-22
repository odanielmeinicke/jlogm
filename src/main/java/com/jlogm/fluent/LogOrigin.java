package com.jlogm.fluent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the origin or source of a log entry, providing information about the class, method,
 * file, and line number where the log statement was generated.
 */
public interface LogOrigin {

    // Static initializers

    /**
     * Creates a LogOrigin based on the current stack trace, representing the origin of the method call
     * that invoked this method.
     *
     * <p>This method retrieves the stack trace element corresponding to the method call that invoked
     * it, skipping two stack frames to ignore this method and the method that called it. It then
     * extracts information such as the class name, file name, method name, and line number from the
     * retrieved stack trace element to create the LogOrigin.
     *
     * <p>This method is useful for capturing the origin of a log statement in cases where explicit
     * creation of a LogOrigin is not necessary. It provides a convenient way to obtain the origin
     * dynamically from the current execution context.
     *
     * @return A LogOrigin instance representing the origin of the method call.
     * @throws IllegalStateException If the stack trace does not contain the necessary information.
     */
    static @NotNull LogOrigin create() {
        // Retrieve the stack trace element corresponding to the method call that invoked this method
        @NotNull StackTraceElement element = Arrays.stream(Thread.currentThread().getStackTrace())
                .skip(2) // Skip two stack frames to ignore this method and its caller
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        // Create a LogOrigin using information from the retrieved stack trace element
        return create(element.getClassName(), element.getFileName(), element.getMethodName(), element.getLineNumber());
    }

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