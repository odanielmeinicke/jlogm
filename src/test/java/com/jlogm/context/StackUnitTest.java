package com.jlogm.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for com.jlogm.context.NestedInfo
 * <p>
 * Requires JUnit 5 (org.junit.jupiter).
 */
public class StackUnitTest {

    @AfterEach
    public void tearDown() {
        // Ensure thread-local is cleaned between tests
        Stack.removeThreadContext();
    }

    @Test
    public void testPushPopPeekClear() {
        assertTrue(Stack.isEmpty());
        assertNull(Stack.peek());
        assertNull(Stack.pop());

        Stack.push("ServiceA");
        assertFalse(Stack.isEmpty());
        assertEquals(1, Stack.depth());
        assertEquals("ServiceA", Stack.peek());

        Stack.push("Op1");
        assertEquals(2, Stack.depth());
        assertEquals("Op1", Stack.peek());

        String popped = Stack.pop();
        assertEquals("Op1", popped);
        assertEquals(1, Stack.depth());
        assertEquals("ServiceA", Stack.peek());

        Stack.clear();
        assertTrue(Stack.isEmpty());
        assertNull(Stack.peek());
    }

    @Test
    public void testScopePushScopeAutoPop() {
        Stack.clear();
        Stack.push("S0");
        assertEquals(1, Stack.depth());
        try (Stack.Scope s = Stack.pushScope("S1")) {
            assertEquals(2, Stack.depth());
            assertEquals("S1", Stack.peek());
        }
        // automatically popped
        assertEquals(1, Stack.depth());
        assertEquals("S0", Stack.peek());
    }

    @Test
    public void testSnapshotAndRestoreImmutability() {
        Stack.clear();
        Stack.push("Top");
        Stack.push("Mid");
        List<String> snap = Stack.snapshot();

        assertEquals(2, snap.size());
        // modifying snapshot should not affect internal stack (snapshot should be immutable)
        assertThrows(UnsupportedOperationException.class, () -> snap.add("X"));

        // restore to a different value
        Stack.restore(Collections.singletonList("Restored"));
        assertEquals(1, Stack.depth());
        assertEquals("Restored", Stack.peek());

        // restore empty clears
        Stack.restore(Collections.emptyList());
        assertTrue(Stack.isEmpty());
    }

    @Test
    public void testWrapRunnablePropagatesAndRestoresWorkerContext() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            // ensure the worker thread has a preexisting NDC value
            Future<Void> init = executor.submit(() -> {
                Stack.push("worker-initial");
                return null;
            });
            init.get(1, TimeUnit.SECONDS);

            // main thread pushes its own context to be propagated
            Stack.push("main-ctx");

            Runnable wrapped = Stack.wrap(() -> {
                // inside worker while wrapped: should see the propagated main context on top
                assertEquals("main-ctx", Stack.peek());
                // worker's original value may have been replaced while running, but will be restored later
                assertTrue(Stack.depth() >= 1);
            });

            Future<?> f = executor.submit(wrapped);
            f.get(1, TimeUnit.SECONDS);

            // after wrapped task finishes, worker's original context must be restored
            Future<String> check = executor.submit(Stack::peek);
            String workerAfter = check.get(1, TimeUnit.SECONDS);
            assertEquals("worker-initial", workerAfter);
        } finally {
            // cleanup both main thread and worker thread context
            Stack.removeThreadContext();
            executor.shutdownNow();

            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testWrapCallableExceptionRestoresPrevious() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            // set worker initial
            executor.submit(() -> {
                Stack.push("W");
                return null;
            }).get(1, TimeUnit.SECONDS);

            Stack.push("REQ");

            Callable<Void> failing = () -> {
                assertEquals("REQ", Stack.peek());
                throw new IllegalStateException("boom");
            };

            Callable<Void> wrapped = Stack.wrap(failing);
            Future<Void> fut = executor.submit(wrapped);
            ExecutionException ex = assertThrows(ExecutionException.class, () -> fut.get(2, TimeUnit.SECONDS));
            assertInstanceOf(IllegalStateException.class, ex.getCause());

            // worker context should remain intact after exception
            Future<String> check = executor.submit(Stack::peek);
            assertEquals("W", check.get(1, TimeUnit.SECONDS));
        } finally {
            Stack.removeThreadContext();
            executor.shutdownNow();

            //noinspection ResultOfMethodCallIgnored
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testDepthAndIsEmpty() {
        Stack.clear();
        assertTrue(Stack.isEmpty());
        assertEquals(0, Stack.depth());
        Stack.push("one");
        assertFalse(Stack.isEmpty());
        assertEquals(1, Stack.depth());
        Stack.push("two");
        assertEquals(2, Stack.depth());
    }

    @Test
    public void testConcurrencyIsolationBetweenThreads() throws Exception {
        int threads = 6;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        try {
            List<Callable<Boolean>> tasks = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                final int idx = i;
                tasks.add(() -> {
                    String value = "v" + idx;
                    Stack.push(value);
                    // small sleep to increase chance of interleaving
                    TimeUnit.MILLISECONDS.sleep(10);
                    boolean ok = value.equals(Stack.peek());
                    // cleanup
                    Stack.removeThreadContext();
                    return ok;
                });
            }
            List<Future<Boolean>> results = exec.invokeAll(tasks);
            for (Future<Boolean> r : results) {
                assertTrue(r.get(2, TimeUnit.SECONDS));
            }
        } finally {
            exec.shutdownNow();

            //noinspection ResultOfMethodCallIgnored
            exec.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testRemoveThreadContext() {
        Stack.push("a");
        assertFalse(Stack.isEmpty());
        Stack.removeThreadContext();
        assertTrue(Stack.isEmpty());
        assertEquals(0, Stack.depth());
    }

    @Test
    public void testRunWithContextConvenience() {
        Stack.clear();
        Stack.push("ctx");
        String result = Stack.runWithContext((Supplier<String>) () -> {
            assertEquals("ctx", Stack.peek());
            return "ok";
        });
        assertEquals("ok", result);
    }
}