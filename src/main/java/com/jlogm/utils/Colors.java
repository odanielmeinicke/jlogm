package com.jlogm.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public final class Colors {

    // Static initializers

    public static @NotNull String colored(@NotNull Color color) {
        return "\033[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }
    public static @NotNull String colored(@NotNull Color color, @NotNull String text) {
        return colored(color) + text + reset();
    }

    public static @NotNull String colored(@Nullable Color foregroundColor, @NotNull Color backgroundColor, @NotNull String text) {
        if (foregroundColor == null) {
            double luminance = (0.299 * backgroundColor.getRed() + 0.587 * backgroundColor.getGreen() + 0.114 * backgroundColor.getBlue()) / 255;
            foregroundColor = luminance > 0.5 ? Color.BLACK : Color.WHITE;
        }

        return colored(foregroundColor) +
                "m\033[48;2;" + backgroundColor.getRed() + ";" + backgroundColor.getGreen() + ";" + backgroundColor.getBlue() +
                "m" + text + reset();
    }

    public static @NotNull String underlined(@NotNull String text) {
        return underlined() + text + reset();
    }
    public static @NotNull String underlined() {
        return "\033[4m";
    }

    public static @NotNull String bold(@NotNull String text) {
        return "\033[1m" + text + reset();
    }
    public static @NotNull String italic(@NotNull String text) {
        return "\033[3m" + text + reset();
    }
    public static @NotNull String strikethrough(@NotNull String text) {
        return "\033[9m" + text + reset();
    }
    public static @NotNull String reset() {
        return "\033[0m";
    }

    // Object

    private Colors() {
        throw new UnsupportedOperationException();
    }

}
