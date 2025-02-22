package com.jlogm.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public final class Colors {

    // Static initializers

    public static @NotNull String colored(@NotNull Color color) {
        return "\033[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }
    public static @NotNull String colored(@NotNull Color color, @NotNull Object object) {
        return colored(color) + object + reset();
    }

    public static @NotNull String colored(@Nullable Color foregroundColor, @NotNull Color backgroundColor, @NotNull Object object) {
        if (foregroundColor == null) {
            double luminance = (0.299 * backgroundColor.getRed() + 0.587 * backgroundColor.getGreen() + 0.114 * backgroundColor.getBlue()) / 255;
            foregroundColor = luminance > 0.5 ? Color.BLACK : Color.WHITE;
        }

        return colored(foregroundColor) +
                "m\033[48;2;" + backgroundColor.getRed() + ";" + backgroundColor.getGreen() + ";" + backgroundColor.getBlue() +
                "m" + object + reset();
    }

    public static @NotNull String underlined(@NotNull Object object) {
        return underlined() + object + reset();
    }
    public static @NotNull String underlined() {
        return "\033[4m";
    }

    public static @NotNull String bold(@NotNull Object object) {
        return "\033[1m" + object + reset();
    }
    public static @NotNull String italic(@NotNull Object object) {
        return "\033[3m" + object + reset();
    }
    public static @NotNull String strikethrough(@NotNull Object object) {
        return "\033[9m" + object + reset();
    }
    public static @NotNull String reset() {
        return "\033[0m";
    }

    // Object

    private Colors() {
        throw new UnsupportedOperationException();
    }

}
