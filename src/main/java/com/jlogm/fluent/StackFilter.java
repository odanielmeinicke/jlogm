package com.jlogm.fluent;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * This interface represents a stack option modifier for exceptions, removing unnecessary stack traces by filtering them.
 */
public interface StackFilter {

    // Static initializers

    /**
     * Represents a full stack option, preserving all stack trace elements.
     */
    @NotNull StackFilter FULL = elements -> elements;

    /**
     * Represents an empty stack option, removing all stack trace elements.
     */
    @NotNull StackFilter NONE = elements -> new StackTraceElement[0];

    /**
     * The NATIVE stack option filters and removes only stack traces with native methods.
     */
    @NotNull StackFilter NATIVE = elements -> Arrays.stream(elements)
            .filter(element -> !element.isNativeMethod())
            .toArray(StackTraceElement[]::new);

    /**
     * The SMALL stack option limits the stack size only for the first 7 traces
     */
    @NotNull StackFilter SMALL = elements -> Arrays.stream(elements)
            .limit(7)
            .toArray(StackTraceElement[]::new);

    /**
     * The MEDIUM stack option limits the stack size only for the first 12 traces
     */
    @NotNull StackFilter MEDIUM = elements -> Arrays.stream(elements)
            .limit(12)
            .toArray(StackTraceElement[]::new);

    /**
     * The LARGE stack option limits the stack size only for the first 17 traces
     */
    @NotNull StackFilter LARGE = elements -> Arrays.stream(elements)
            .limit(17)
            .toArray(StackTraceElement[]::new);

    // Object

    /**
     * Formats the given array of stack trace elements according to the stack size rules.
     *
     * @param elements The array of stack trace elements to be formatted.
     * @return The formatted array of stack trace elements.
     */
    @NotNull StackTraceElement @NotNull [] format(@NotNull StackTraceElement @NotNull [] elements);

}

