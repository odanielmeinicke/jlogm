package com.jlogm.impl;

import com.jlogm.utils.Coloured;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Marker;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class SimpleMarker implements Marker {

    private final @NotNull String name;
    private final @Nullable Color color;

    private final @NotNull Set<Marker> markers = new LinkedHashSet<>();

    public SimpleMarker(@NotNull String name) {
        this.name = name;
        this.color = null;
    }

    public SimpleMarker(@NotNull String name, @Nullable Color color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    public @Nullable Color getColor() {
        return color;
    }

    @Override
    public void add(@NotNull Marker reference) {
        markers.add(reference);
    }
    @Override
    public boolean remove(@NotNull Marker reference) {
        return markers.remove(reference);
    }

    @Override
    public boolean hasChildren() {
        return hasReferences();
    }

    @Override
    public boolean hasReferences() {
        return !markers.isEmpty();
    }

    @Override
    public boolean contains(@NotNull Marker other) {
        return false;
    }

    @Override
    public boolean contains(@NotNull String name) {
        return markers.stream().anyMatch(marker -> marker.getName().equals(name));
    }

    @Override
    public @NotNull Iterator<Marker> iterator() {
        return markers.iterator();
    }

    // Implementations

    @Override
    public boolean equals(@Nullable Object object) {
        if (!(object instanceof Marker)) return false;
        @NotNull Marker that = (Marker) object;
        return Objects.equals(getName(), that.getName());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public @NotNull String toString() {
        return (getColor() != null ? Coloured.of(getName()).color(getColor()).print() : "");
    }

}
