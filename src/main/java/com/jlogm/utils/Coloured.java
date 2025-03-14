package com.jlogm.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The {@code Coloured} class provides a fluent API for constructing strings with ANSI escape sequences
 * to style terminal output. It supports multiple text styles (such as bold, italic, underline, and strikethrough)
 * as well as the application of both foreground (text) and background colors using 24-bit true color ANSI codes.
 * Additionally, it offers an automatic contrast adjustment feature: if no foreground color is specified but a
 * background color is provided, the class can compute the luminance of the background and automatically choose
 * either black or white as the text color to ensure optimal readability.
 *
 * <p>ANSI escape sequences used include:
 * <ul>
 *   <li><code>\033[1m</code> to enable bold text.</li>
 *   <li><code>\033[3m</code> to enable italic text.</li>
 *   <li><code>\033[4m</code> to enable underlined text.</li>
 *   <li><code>\033[9m</code> to enable strikethrough text.</li>
 *   <li><code>\033[38;2;R;G;Bm</code> to set the foreground (text) color using RGB values.</li>
 *   <li><code>\033[48;2;R;G;Bm</code> to set the background color using RGB values.</li>
 *   <li><code>\033[0m</code> to reset all text attributes and colors.</li>
 * </ul>
 *
 * <p><strong>Example usage:</strong>
 * <pre>{@code
 * // Create a styled text with bold, underlined, red foreground and black background
 * Coloured styledText = Coloured.of("Hello, World!")
 *     .bold()
 *     .underlined()
 *     .color(new Color(255, 0, 0))       // Set text color to red
 *     .background(new Color(0, 0, 0));     // Set background color to black
 * System.out.println(styledText.print());
 * }</pre>
 *
 * <p>Note: The output will render correctly only in terminals that support ANSI escape sequences.
 */
public final class Coloured {

    // =========================================================================
    // Static Factory Methods
    // =========================================================================

    /**
     * Creates a new {@code Coloured} instance with the specified text.
     *
     * <p>This static factory method accepts a {@code String} and wraps it as the content to be styled.
     * The provided text will be the basis for the final output when ANSI styling is applied.
     *
     * @param text the text to be styled; must not be {@code null}.
     * @return a new {@code Coloured} instance encapsulating the provided text.
     */
    public static @NotNull Coloured of(@NotNull String text) {
        return new Coloured(text);
    }

    /**
     * Creates a new {@code Coloured} instance with the specified object.
     *
     * <p>This static factory method accepts any {@code Object}. The {@code toString()} method of the object
     * will be used to represent its content when generating the final styled string.
     *
     * @param object the object whose string representation will be styled; must not be {@code null}.
     * @return a new {@code Coloured} instance encapsulating the provided object.
     */
    public static @NotNull Coloured of(@NotNull Object object) {
        return new Coloured(object);
    }

    // =========================================================================
    // Instance Fields
    // =========================================================================

    /**
     * The content to be styled.
     *
     * <p>This field holds the object (or its {@code toString()} representation) that will be wrapped with ANSI
     * escape sequences for styling. It is declared {@code final} to ensure that the content remains constant
     * after instantiation.
     */
    private final @NotNull Object object;

    /**
     * Flag indicating whether the text should be underlined.
     *
     * <p>If {@code true}, the ANSI escape sequence for underlining (ESC[4m) will be prepended to the output.
     */
    private boolean underlined = false;

    /**
     * Flag indicating whether the text should be displayed in bold.
     *
     * <p>If {@code true}, the ANSI escape sequence for bold text (ESC[1m) will be applied.
     */
    private boolean bold = false;

    /**
     * Flag indicating whether the text should be displayed in italic.
     *
     * <p>If {@code true}, the ANSI escape sequence for italic text (ESC[3m) will be applied.
     */
    private boolean italic = false;

    /**
     * Flag indicating whether the text should have a strikethrough.
     *
     * <p>If {@code true}, the ANSI escape sequence for strikethrough text (ESC[9m) will be applied.
     */
    private boolean strikethrough = false;

    /**
     * The background color to be applied to the text.
     *
     * <p>If specified, this {@link Color} is used to generate an ANSI escape sequence for setting the background color
     * using 24-bit true color (ESC[48;2;R;G;Bm). It may be {@code null} if no background color is desired.
     */
    private @Nullable Color background;

    /**
     * The foreground (text) color to be applied.
     *
     * <p>If specified, this {@link Color} is used to generate an ANSI escape sequence for setting the text color
     * using 24-bit true color (ESC[38;2;R;G;Bm). If {@code null} and a background color is provided with contrast
     * adjustment enabled, the class computes a suitable contrasting color (either black or white) based on the
     * background's luminance.
     */
    private @Nullable Color color;

    /**
     * Flag indicating whether automatic contrast adjustment is enabled.
     *
     * <p>When {@code true} (the default), if the foreground color is not explicitly set (i.e., {@code null}) but a background color is
     * provided, the class computes the luminance of the background and selects either black or white as the text color to maximize
     * contrast and ensure legibility.
     */
    private boolean adjustContrast = true;

    // =========================================================================
    // Private Constructor
    // =========================================================================

    /**
     * Private constructor for the {@code Coloured} class.
     *
     * <p>Initializes a new instance with the provided object, which represents the content to be styled.
     * The object’s {@code toString()} method will later be invoked to obtain its string representation.
     *
     * @param object the content to style; must not be {@code null}.
     */
    private Coloured(@NotNull Object object) {
        this.object = object;
    }

    // =========================================================================
    // Builder Methods for Styling
    // =========================================================================

    /**
     * Enables the underline style for the text.
     *
     * <p>This method sets the internal flag to apply the ANSI underline escape sequence (ESC[4m)
     * when the styled string is generated.
     *
     * @return the current {@code Coloured} instance with the underline style enabled.
     */
    public @NotNull Coloured underlined() {
        this.underlined = true;
        return this;
    }

    /**
     * Enables the bold style for the text.
     *
     * <p>This method sets the internal flag to apply the ANSI bold escape sequence (ESC[1m)
     * when the styled string is generated.
     *
     * @return the current {@code Coloured} instance with the bold style enabled.
     */
    public @NotNull Coloured bold() {
        this.bold = true;
        return this;
    }

    /**
     * Enables the italic style for the text.
     *
     * <p>This method sets the internal flag to apply the ANSI italic escape sequence (ESC[3m)
     * when the styled string is generated.
     *
     * @return the current {@code Coloured} instance with the italic style enabled.
     */
    public @NotNull Coloured italic() {
        this.italic = true;
        return this;
    }

    /**
     * Enables the strikethrough style for the text.
     *
     * <p>This method sets the internal flag to apply the ANSI strikethrough escape sequence (ESC[9m)
     * when the styled string is generated.
     *
     * @return the current {@code Coloured} instance with the strikethrough style enabled.
     */
    public @NotNull Coloured strikethrough() {
        this.strikethrough = true;
        return this;
    }

    /**
     * Sets the background color for the styled text.
     *
     * <p>This method assigns the provided {@link Color} to be used as the background when generating the ANSI
     * escape sequence for background colors (ESC[48;2;R;G;Bm). If a background color is provided and no foreground
     * color is explicitly set, the contrast adjustment feature may use the background to determine an optimal text color.
     *
     * @param background the {@link Color} to use for the background; may be {@code null} to indicate no background color.
     * @return the current {@code Coloured} instance with the specified background color.
     */
    public @NotNull Coloured background(@Nullable Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the foreground (text) color for the styled text.
     *
     * <p>This method assigns the provided {@link Color} to be used as the text color when generating the ANSI
     * escape sequence for foreground colors (ESC[38;2;R;G;Bm). If set to {@code null} and a background color is provided
     * with contrast adjustment enabled, the text color may be automatically determined based on the background's luminance.
     *
     * @param foreground the {@link Color} to use for the text; may be {@code null} to enable automatic contrast adjustment.
     * @return the current {@code Coloured} instance with the specified text color.
     */
    public @NotNull Coloured color(@Nullable Color foreground) {
        this.color = foreground;
        return this;
    }

    /**
     * Configures whether automatic contrast adjustment is enabled.
     *
     * <p>If enabled, and if the foreground color is {@code null} while a background color is provided,
     * the method {@link #print()} will compute the luminance of the background and set the text color to
     * either black or white, depending on which offers higher contrast.
     *
     * @param adjustContrast a boolean flag indicating whether to enable ({@code true}) or disable ({@code false})
     *                       automatic contrast adjustment.
     * @return the current {@code Coloured} instance with the specified contrast adjustment setting.
     */
    public @NotNull Coloured adjustContrast(boolean adjustContrast) {
        this.adjustContrast = adjustContrast;
        return this;
    }

    // =========================================================================
    // Methods to Generate and Output the Styled Text
    // =========================================================================

    /**
     * Generates the fully styled string with ANSI escape sequences based on the current configuration.
     *
     * <p>This method constructs a {@link String} that embeds ANSI escape codes corresponding to:
     * <ul>
     *   <li>Text styles: underline (ESC[4m), bold (ESC[1m), italic (ESC[3m), and strikethrough (ESC[9m).</li>
     *   <li>Foreground (text) color: If a text color is specified, it is applied using the escape sequence (ESC[38;2;R;G;B;m).
     *       If no text color is set but a background color is provided with contrast adjustment enabled,
     *       the luminance of the background is computed and the text color is set to either black or white accordingly.</li>
     *   <li>Background color: If specified, it is applied using the ANSI escape sequence for background colors (ESC[48;2;R;G;B;m).</li>
     *   <li>A reset code (ESC[0m) at the end to clear all applied styles.</li>
     * </ul>
     *
     * <p><strong>Note:</strong> The method applies the foreground color escape sequence twice if a color is determined;
     * this may be intentional for ensuring proper state in some terminal emulators, although it appears redundant.
     *
     * @return a {@link String} containing the styled text with embedded ANSI escape codes.
     */
    public @NotNull String print() {
        // Variables: StringBuilder for accumulating ANSI sequences and text.
        @NotNull StringBuilder builder = new StringBuilder();

        // Apply text formatting styles based on flags.
        if (underlined) builder.append("\033[4m");       // Enable underline.
        if (bold) builder.append("\033[1m");             // Enable bold text.
        if (italic) builder.append("\033[3m");           // Enable italic text.
        if (strikethrough) builder.append("\033[9m");    // Enable strikethrough.

        // Determine and apply the foreground (text) color.
        if (color != null) {
            // Append ANSI escape code for foreground color using 24-bit RGB values.
            builder.append("\033[38;2;").append(color.getRed()).append(";").append(color.getGreen()).append(";").append(color.getBlue()).append("m");
        } else if (background != null && adjustContrast) {
            // Calculate the luminance of the background color.
            double luminance = (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;
            // Choose black or white for the text color based on luminance.
            color = luminance > 0.5 ? Color.BLACK : Color.WHITE;
        }

        // Apply the foreground (text) color again if it has been determined.
        if (color != null) {
            builder.append("\033[38;2;").append(color.getRed()).append(";").append(color.getGreen()).append(";").append(color.getBlue()).append("m");
        }
        // Apply the background color if specified.
        if (background != null) {
            builder.append("\033[48;2;").append(background.getRed()).append(";").append(background.getGreen()).append(";").append(background.getBlue()).append("m");
        }

        // Append the main content (object's string representation).
        builder.append(object);

        // Reset all styles to default.
        if (background != null || color != null || underlined || bold || italic || strikethrough) {
            builder.append("\033[0m");
        }

        // Return the fully constructed styled string.
        return builder.toString();
    }

    /**
     * Writes the fully styled string (with ANSI escape sequences) to the provided output stream.
     *
     * <p>This method converts the styled string generated by {@link #print()} into a sequence of bytes using the
     * platform's default charset, and writes it to the specified {@link OutputStream}.
     *
     * @param stream the {@link OutputStream} to which the styled string will be written; must not be {@code null}.
     * @throws IOException if an I/O error occurs while writing to the stream.
     */
    public void print(@NotNull OutputStream stream) throws IOException {
        stream.write(print().getBytes());
    }

    // Implementations

    @Override
    public @NotNull String toString() {
        return print();
    }

}