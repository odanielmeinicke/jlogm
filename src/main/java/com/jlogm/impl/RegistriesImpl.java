package com.jlogm.impl;

import com.jlogm.Registry;
import com.jlogm.factory.LoggerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

final class RegistriesImpl implements LoggerFactory.Registries {

    private final @NotNull List<Registry> registries = new LinkedList<>();

    @Override
    public boolean add(@NotNull Registry registry) {
        return registries.add(registry);
    }

    @Override
    public boolean remove(@NotNull Registry registry) {
        return registries.remove(registry);
    }

    @Override
    public boolean contains(@NotNull Registry registry) {
        return registries.contains(registry);
    }

    @Override
    public @NotNull Stream<Registry> stream() {
        return registries.stream();
    }

    @Override
    public void flush() throws IOException {
        registries.clear();
    }

    @Override
    public @NotNull Iterator<Registry> iterator() {
        return registries.iterator();
    }
}
