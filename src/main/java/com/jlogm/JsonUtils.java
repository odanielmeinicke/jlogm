package com.jlogm;

import com.jlogm.utils.Coloured;
import org.jetbrains.annotations.NotNull;

final class JsonUtils {

    // Static initializers

    public static @NotNull String escapeJson(@NotNull String s) {
        // Variables
        s = Coloured.sanitize(s);
        @NotNull StringBuilder sb = new StringBuilder(s.length() + 16);

        // Escape all characters
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        // controla outros chars de controle
                        sb.append(String.format("\\u%04x", (int)c));
                    } else {
                        sb.append(c);
                    }
            }
        }

        // Finish
        return sb.toString();
    }

    // Object

    private JsonUtils() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

}
