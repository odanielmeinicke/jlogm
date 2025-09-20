package com.jlogm.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * NDC (Nested Diagnostic Context) — a thread-local stack of context strings used to represent nested execution scopes.
 *
 * <p>
 * Typical uses:
 * <ul>
 *     <li>Push a scope marker at the beginning of a service/operation and pop it at the end.</li>
 *     <li>Use {@link Scope} with try-with-resources to guarantee proper pop/restore.</li>
 *     <li>Capture a snapshot and wrap a {@link Runnable} / {@link Callable} to propagate the context across threads.</li>
 * </ul>
 *
 * <p><b>Design goals</b>:
 * <ul>
 *     <li>Minimal and safe API for common NDC operations (push/pop/peek/clear/snapshot).</li>
 *     <li>Helpers to avoid leaks in pooled threads ( {@link #removeThreadContext()} ).</li>
 *     <li>Formatting helpers to integrate with structured logging (asString, asJson).</li>
 *     <li>Scoped usage via {@link Scope} to avoid bugs caused by forgotten pops.</li>
 * </ul>
 *
 * <p><b>Example</b>:
 * <pre>{@code
 * Stack.push("ServiceA");
 * try (Stack.Scope s = Stack.pushScope("OperationX")) {
 *     logger.info("processing..."); // NDC shows [ServiceA, OperationX]
 * }
 * Stack.pop();
 * }</pre>
 */
public final class Stack {

    /**
     * Thread-local Stack instance — each thread holds its own Stack object which contains the internal deque.
     * This lets static fluent methods return the per-thread Stack instance for chaining.
     */
    private static final ThreadLocal<Stack> CONTEXT = ThreadLocal.withInitial(Stack::new);

    // instance field: the actual deque used as the stack (head == top)
    private final Deque<String> ndc = new ArrayDeque<>();

    // allow instantiation only for the ThreadLocal; keep constructor private
    private Stack() {}

    /* =========================
     * Basic stack operations (fluent where mutating)
     * ========================= */

    /**
     * Push a new context string onto the current thread's NDC stack and return the per-thread Stack for chaining.
     *
     * @param value non-null string to push
     * @throws NullPointerException if value is null
     * @return the per-thread Stack instance for fluent chaining
     */
    @NotNull
    public static Stack push(@NotNull String value) {
        Objects.requireNonNull(value, "value");
        CONTEXT.get().ndc.push(value);
        return CONTEXT.get();
    }

    /**
     * Pop the top value from the current thread's NDC stack.
     *
     * @return the popped value, or null if the stack was empty
     */
    @Nullable
    public static String pop() {
        Deque<String> d = CONTEXT.get().ndc;
        return d.isEmpty() ? null : d.pop();
    }

    /**
     * Peek the top value from the stack without removing it.
     *
     * @return top value, or null if the stack is empty
     */
    @Nullable
    public static String peek() {
        Deque<String> d = CONTEXT.get().ndc;
        return d.peek();
    }

    /**
     * Clear the current thread's NDC stack and return the per-thread Stack for chaining.
     *
     * @return the per-thread Stack instance for fluent chaining
     */
    @NotNull
    public static Stack clear() {
        CONTEXT.get().ndc.clear();
        return CONTEXT.get();
    }

    /**
     * Return an immutable snapshot (list) of the current NDC stack.
     * The returned list is top-first (index 0 == top of stack).
     *
     * @return an unmodifiable list representing the stack snapshot
     */
    @NotNull
    public static List<String> snapshot() {
        Deque<String> d = CONTEXT.get().ndc;
        if (d.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<>(d));
    }

    /**
     * Restore the current thread's NDC stack from a snapshot and return the per-thread Stack for chaining.
     * The provided list is interpreted top-first (index 0 = top).
     *
     * @param snap nullable snapshot (if null or empty the stack will be cleared)
     * @return the per-thread Stack instance for fluent chaining
     */
    @NotNull
    public static Stack restore(@Nullable List<String> snap) {
        Deque<String> d = CONTEXT.get().ndc;
        d.clear();
        if (snap != null && !snap.isEmpty()) {
            // push values in reverse so that index 0 becomes the top
            for (int i = snap.size() - 1; i >= 0; i--) {
                String v = snap.get(i);
                // skip nulls for safety
                if (v != null) d.push(v);
            }
        }
        return CONTEXT.get();
    }

    /**
     * Return the current depth (number of elements) in the NDC stack for this thread.
     *
     * @return depth >= 0
     */
    public static int depth() {
        return CONTEXT.get().ndc.size();
    }

    /**
     * Return true if the current thread's NDC stack is empty.
     */
    public static boolean isEmpty() {
        return CONTEXT.get().ndc.isEmpty();
    }

    /* =========================
     * Scoped operations
     * ========================= */

    /**
     * Push a single value and return a {@link Scope} which will pop the value when closed.
     * <p>
     * Example:
     * <pre>{@code
     * try (Stack.Scope s = Stack.pushScope("Operation1")) {
     *    // NDC contains Operation1 on top
     * }
     * }</pre>
     *
     * @param value non-null string to push for the scope
     * @return Scope to close (idempotent close)
     */
    @NotNull
    public static Scope pushScope(@NotNull String value) {
        Objects.requireNonNull(value, "value");
        push(value);
        return new Scope(1);
    }

    /**
     * Push multiple values at once and return a {@link Scope} that will pop the same number of entries.
     * Values are pushed in the order provided; the last element becomes the top of the stack.
     *
     * @param values non-null collection of non-null strings
     * @return Scope to close
     */
    @NotNull
    public static Scope pushScope(@NotNull Collection<String> values) {
        Objects.requireNonNull(values, "values");
        if (values.isEmpty()) return new Scope(0);
        for (String v : values) {
            Objects.requireNonNull(v, "value in values");
            push(v);
        }
        return new Scope(values.size());
    }

    /**
     * A Scope object that will pop the specified number of elements on {@link #close()}.
     * It is idempotent and safe to call multiple times.
     */
    public static final class Scope implements AutoCloseable {
        private final int count;
        private boolean closed = false;

        private Scope(int count) {
            this.count = Math.max(0, count);
        }

        /**
         * Close the scope and pop the elements it pushed.
         * This method is idempotent.
         */
        @Override
        public void close() {
            if (closed) return;
            Deque<String> d = CONTEXT.get().ndc;
            for (int i = 0; i < count; i++) {
                if (d.isEmpty()) break;
                d.pop();
            }
            closed = true;
        }
    }

    /* =========================
     * Propagation helpers
     * ========================= */

    /**
     * Wrap a Runnable capturing the current NDC snapshot so that when executed it will run under the captured context.
     * The wrapper restores the executing thread's previous NDC after execution.
     *
     * @param task non-null runnable
     * @return wrapped runnable which sets the captured NDC while running
     */
    @NotNull
    public static Runnable wrap(@NotNull Runnable task) {
        Objects.requireNonNull(task, "task");
        final List<String> snap = snapshot();
        return () -> {
            List<String> previous = snapshot();
            try {
                restore(snap);
                task.run();
            } finally {
                restore(previous);
            }
        };
    }

    /**
     * Wrap a Callable capturing the current NDC snapshot so that when executed it will run under the captured context.
     * The wrapper restores the executing thread's previous NDC after execution (both success and exception).
     *
     * @param task non-null callable
     * @param <T>  result type
     * @return wrapped callable
     */
    @NotNull
    public static <T> Callable<T> wrap(@NotNull Callable<T> task) {
        Objects.requireNonNull(task, "task");
        final List<String> snap = snapshot();
        return () -> {
            List<String> previous = snapshot();
            try {
                restore(snap);
                return task.call();
            } finally {
                restore(previous);
            }
        };
    }

    /**
     * Execute a supplier with the current NDC applied (convenience helper).
     *
     * @param supplier non-null supplier
     * @param <T>      result type
     * @return supplier result
     */
    @NotNull
    public static <T> T runWithContext(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        List<String> snap = snapshot();
        try {
            return supplier.get();
        } finally {
            restore(snap);
        }
    }

    /**
     * Completely remove the current thread's NDC stack object from ThreadLocal (useful in pooled threads).
     * After calling this method, the next access will recreate a fresh, empty stack.
     * <p>
     * Use this in finally blocks of pooled threads to avoid memory leaks.
     */
    public static void removeThreadContext() {
        CONTEXT.remove();
    }

    /**
     * Pop until the specified value is popped (inclusive). If the value is not found, no changes are made.
     *
     * @param value non-null value to popTo (inclusive)
     * @return true if value was found and popped (and hence others above it popped), false otherwise
     */
    public static boolean popTo(@NotNull String value) {
        Objects.requireNonNull(value, "value");
        Deque<String> d = CONTEXT.get().ndc;
        if (d.isEmpty()) return false;
        Deque<String> clone = new ArrayDeque<>(d);
        boolean found = false;
        Deque<String> popped = new ArrayDeque<>();
        while (!clone.isEmpty()) {
            String top = clone.pop();
            popped.push(top);
            if (value.equals(top)) {
                found = true;
                break;
            }
        }
        if (!found) return false;
        // actually pop from original
        for (String p : popped) {
            if (!d.isEmpty()) d.pop();
        }
        return true;
    }

}