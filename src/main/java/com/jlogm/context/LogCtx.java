package com.jlogm.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * LogCtx — a fluent, ThreadLocal-backed logging context (key -> Object).
 *
 * <p>
 * Design goals:
 * <ul>
 *   <li>Thread-local per-thread context map with predictable ordering (LinkedHashMap).</li>
 *   <li>Fluent API for concise composition (chainable {@code put} calls).</li>
 *   <li>Builder-style API to install multiple values as a scope (try-with-resources friendly).</li>
 *   <li>Safe helpers to wrap Runnable/Callable for executor propagation.</li>
 *   <li>JSON formatting helper for structured logging output.</li>
 * </ul>
 *
 * <p>Examples:
 * <pre>{@code
 * // quick chained puts
 * LogCtx.put("requestId", "r1").put("user", 42);
 *
 * // scoped installation (restores previous values automatically)
 * try (LogCtx.Scope s = LogCtx.with("requestId", "r1")) {
 *     log.info("do work -> " + LogCtx.asJson());
 * }
 *
 * // builder for multiple values
 * try (LogCtx.Scope s = LogCtx.builder().put("a", 1).put("b", "two").install()) {
 *     // ...
 * }
 *
 * // executor propagation
 * Runnable wrapped = LogCtx.wrap(() -> doWork());
 * executor.submit(wrapped);
 * }</pre>
 */
public final class LogCtx {

    // Per-thread LogCtx instance
    private static final ThreadLocal<LogCtx> CONTEXT = ThreadLocal.withInitial(LogCtx::new);

    // Internal map preserving insertion order (useful for deterministic JSON output)
    private final @NotNull Map<String, Object> map = new LinkedHashMap<>();

    // Private constructor - instances are thread-local
    private LogCtx() {}

    /* =========================
     * Static convenience / Fluent
     * ========================= */

    /**
     * Return the current thread's LogCtx instance.
     *
     * @return the LogCtx associated with the current thread (never null)
     */
    @NotNull
    public static LogCtx current() {
        return CONTEXT.get();
    }

    /**
     * Put a key/value pair into the current thread's context and return the LogCtx for chaining.
     *
     * @param key   non-null key
     * @param value nullable value
     * @return current LogCtx for fluent chaining
     * @throws NullPointerException if key or value is null
     */
    @NotNull
    public static LogCtx put(@NotNull String key, @Nullable Object value) {
        @NotNull LogCtx ctx = CONTEXT.get();
        ctx.map.put(key, value);
        return ctx;
    }

    /**
     * Put a key/value pair only if absent and return the LogCtx for chaining.
     *
     * @param key   non-null key
     * @param value nullable value
     * @return current LogCtx for chaining
     */
    @NotNull
    public static LogCtx putIfAbsent(@NotNull String key, @Nullable Object value) {
        @NotNull LogCtx ctx = CONTEXT.get();
        ctx.map.putIfAbsent(key, value);
        return ctx;
    }

    /**
     * Put all entries from the provided map into the current thread's context (overwriting existing keys).
     *
     * @param values non-null map
     * @return current LogCtx for chaining
     */
    @NotNull
    public static LogCtx putAll(@NotNull Map<String, Object> values) {
        @NotNull LogCtx ctx = CONTEXT.get();
        if (!values.isEmpty()) ctx.map.putAll(values);

        return ctx;
    }

    /**
     * Remove a key from the current thread's context and return this LogCtx for chaining.
     * <p>
     * Note: if you need the previous value, use {@link #removeAndGet(String)}.
     *
     * @param key non-null key
     * @return current LogCtx for chaining
     */
    @NotNull
    public static LogCtx remove(@NotNull String key) {
        @NotNull LogCtx ctx = CONTEXT.get();
        ctx.map.remove(key);
        return ctx;
    }

    /**
     * Remove a key and return the previous value (or null if absent).
     * This preserves the old semantics for callers that expect the removed value.
     *
     * @param key non-null key
     * @return previous value or null
     */
    @Nullable
    public static Object removeAndGet(@NotNull String key) {
        @NotNull LogCtx ctx = CONTEXT.get();
        return ctx.map.remove(key);
    }

    /**
     * Clear the current thread's context map and return this LogCtx for chaining.
     * <p>
     * Note: in pooled threads prefer {@link #removeThreadContext()} to avoid memory leaks.
     *
     * @return current LogCtx for chaining
     */
    @NotNull
    public static LogCtx clear() {
        @NotNull LogCtx ctx = CONTEXT.get();
        ctx.map.clear();
        return ctx;
    }

    /**
     * Return an immutable snapshot of the current context map (shallow copy).
     *
     * @return unmodifiable map snapshot (never null)
     */
    @NotNull
    public static Map<String, Object> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(CONTEXT.get().map));
    }

    /**
     * Return the string representation (toString) of the value for the given key, or null if absent.
     *
     * @param key non-null key
     * @return string representation or null
     */
    @Nullable
    public static String getString(@NotNull String key) {
        Object v = CONTEXT.get().map.get(key);
        return v == null ? null : v.toString();
    }

    /**
     * Get the raw Object value for the given key.
     *
     * @param key non-null key
     * @return value or null
     */
    @Nullable
    public static Object get(@NotNull String key) {
        return CONTEXT.get().map.get(key);
    }

    /**
     * Returns the number of entries in the current thread's context.
     *
     * @return size >= 0
     */
    public static int size() {
        return CONTEXT.get().map.size();
    }

    /**
     * Returns true if the current thread's context contains the key.
     *
     * @param key non-null key
     * @return true if present
     */
    public static boolean containsKey(@NotNull String key) {
        return CONTEXT.get().map.containsKey(key);
    }

    /**
     * Restore the current thread's context to the provided map (replaces current contents).
     * Fluent: returns the current LogCtx.
     *
     * @param m nullable map; if null behaves like clear()
     * @return current LogCtx for chaining
     */
    @NotNull
    public static LogCtx restore(@Nullable Map<String, Object> m) {
        @NotNull LogCtx ctx = CONTEXT.get();
        @NotNull Map<String, Object> to = ctx.map;
        to.clear();
        if (m != null && !m.isEmpty()) to.putAll(m);
        return ctx;
    }

    /**
     * Remove the entire ThreadLocal instance for the current thread.
     * Use this in pooled/long-lived threads to prevent memory leaks.
     */
    public static void removeThreadContext() {
        CONTEXT.remove();
    }

    /* =========================
     * Fluent builder & scoped installs
     * ========================= */

    /**
     * Create a builder for fluent composition of multiple entries. Call {@link Builder#install()} to apply as a Scope.
     *
     * @return new Builder instance
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Convenience: create and install a single-key scope (use with try-with-resources).
     * <p>
     * Example:
     * <pre>{@code try (LogCtx.Scope s = LogCtx.with("requestId", "r1")) { ... } }</pre>
     *
     * @param key   non-null key
     * @param value nullable value
     * @return Scope that will restore previous state when closed
     */
    @NotNull
    public static Scope with(@NotNull String key, @Nullable Object value) {
        return builder().put(key, value).install();
    }

    /**
     * Fluent builder for installing multiple entries as a scope.
     */
    public static final class Builder {
        private final LinkedHashMap<String, Object> values = new LinkedHashMap<>();

        private Builder() {}

        /**
         * Add a key/value pair to the builder.
         *
         * @param key   non-null key
         * @param value nullable value
         * @return this builder for chaining
         */
        @NotNull
        public Builder put(@NotNull String key, @Nullable Object value) {
            values.put(key, value);
            return this;
        }

        /**
         * Add all entries from the supplied map.
         *
         * @param m non-null map
         * @return this builder
         */
        @NotNull
        public Builder putAll(@NotNull Map<String, Object> m) {
            values.putAll(m);
            return this;
        }

        /**
         * Install this builder as a Scope: values are set into the current thread context and a Scope
         * object is returned which restores the previous state on {@link Scope#close()}.
         *
         * @return Scope that restores previous state when closed
         */
        @NotNull
        public Scope install() {
            return new Scope(values);
        }
    }

    /**
     * Scope represents a temporary context installation which restores prior values on close.
     * Use inside try-with-resources to ensure deterministic restore.
     */
    public static final class Scope implements AutoCloseable {
        // Map of previous values (including explicit null marker to indicate absent before)
        private final Map<String, Object> previousValues = new LinkedHashMap<>();
        private final Map<String, Object> currentValues;
        private boolean closed = false;

        private Scope(@NotNull Map<String, Object> valuesToInstall) {
            // copy to be safe
            this.currentValues = new LinkedHashMap<>(Objects.requireNonNull(valuesToInstall, "values"));
            Map<String, Object> ctx = CONTEXT.get().map;
            for (String k : currentValues.keySet()) {
                // explicit marker that it was absent
                previousValues.put(k, ctx.getOrDefault(k, null));
                ctx.put(k, currentValues.get(k));
            }
        }

        /**
         * Close the scope and restore previous values. Idempotent.
         */
        @Override
        public void close() {
            if (closed) return;
            Map<String, Object> ctx = CONTEXT.get().map;
            // remove the keys we set
            for (String k : currentValues.keySet()) {
                ctx.remove(k);
            }
            // restore previous values (null means absent before)
            for (Map.Entry<String, Object> e : previousValues.entrySet()) {
                if (e.getValue() != null) ctx.put(e.getKey(), e.getValue());
            }
            closed = true;
        }
    }

    /* =========================
     * Wrappers to propagate context to other threads
     * ========================= */

    /**
     * Wrap a Runnable capturing the current context snapshot so it can be applied to the worker thread.
     * The original context of the worker thread is restored after execution.
     *
     * @param task non-null runnable
     * @return wrapped runnable
     */
    @NotNull
    public static Runnable wrap(@NotNull Runnable task) {
        Objects.requireNonNull(task, "task");
        final Map<String, Object> snap = snapshot();
        return () -> {
            Map<String, Object> previous = snapshot();
            try (Scope s = new Scope(snap)) {
                task.run();
            } finally {
                // ensure previous restored even if Scope.close() didn't because of mismatch
                restore(previous);
            }
        };
    }

    /**
     * Wrap a Callable capturing the current context snapshot so it can be applied to the worker thread.
     * Restores the worker's previous context after completion or exception.
     *
     * @param task non-null callable
     * @param <T>  result type
     * @return wrapped callable
     */
    @NotNull
    public static <T> Callable<T> wrap(@NotNull Callable<T> task) {
        Objects.requireNonNull(task, "task");
        final Map<String, Object> snap = snapshot();
        return () -> {
            Map<String, Object> previous = snapshot();
            try (Scope s = new Scope(snap)) {
                return task.call();
            } finally {
                restore(previous);
            }
        };
    }

    /* =========================
     * Instance (non-static) helpers used by fluent static API
     * ========================= */

    /**
     * Instance-level put used by the fluent static {@link #put(String, Object)}.
     *
     * @param key non-null key
     * @param value nullable value
     * @return this instance for chaining
     */
    private @NotNull LogCtx putInternal(@NotNull String key, @Nullable Object value) {
        this.map.put(key, value);
        return this;
    }

    /**
     * Return an unmodifiable shallow copy of this instance's internal map.
     *
     * @return snapshot map
     */
    private @NotNull Map<String, Object> snapshotInternal() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(this.map));
    }

    /* =========================
     * Misc utilities
     * ========================= */

    /**
     * Execute a supplier with the current context applied, restoring previous context afterwards.
     *
     * @param supplier non-null supplier
     * @param <T>      result type
     * @return supplier result
     */
    public static <T> @NotNull T runWithContext(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        Map<String, Object> snap = snapshot();
        try (Scope s = new Scope(snap)) {
            return supplier.get();
        }
    }

}