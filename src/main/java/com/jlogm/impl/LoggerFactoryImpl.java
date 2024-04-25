package com.jlogm.impl;

import com.jlogm.Filter;
import com.jlogm.Level;
import com.jlogm.Logger;
import com.jlogm.Registry;
import com.jlogm.factory.LoggerFactory;
import com.jlogm.fluent.LogOrigin;
import com.jlogm.fluent.StackFilter;
import com.jlogm.utils.Colors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.jlogm.utils.Colors.*;

final class LoggerFactoryImpl implements LoggerFactory {

    // Static initializers

    public static final @NotNull Level INFO = Level.create("INFO", new Color(160, 160, 160));
    public static final @NotNull Level SEVERE = Level.create("SEVERE", new Color(220, 0, 0));
    public static final @NotNull Level WARNING = Level.create("WARN", new Color(255, 255, 0));
    public static final @NotNull Level DEBUG = Level.create("DEBUG", new Color(230, 150, 175));

    @SuppressWarnings("FieldMayBeFinal")
    private static @NotNull LoggerFactory instance = new LoggerFactoryImpl();

    private static final @NotNull Map<String, Color> colors = new HashMap<String, Color>() {{
        put("loading", new Color(0, 180, 255));
        put("initializing", new Color(0, 180, 255));
        put("generating", new Color(0, 180, 255));
        put("saving", new Color(0, 180, 255));
        put("enabling", new Color(0, 180, 255));
        put("uploading", new Color(0, 180, 255));
        put("importing", new Color(0, 180, 255));
        put("localhost", new Color(0, 180, 255));
        put("127.0.0.1", new Color(0, 180, 255));
        put("running", new Color(0, 180, 255));

        put("successfully", new Color(0, 180, 0));
        put("connected", new Color(0, 180, 0));
        put("success", new Color(0, 180, 0));
        put("initialized", new Color(0, 180, 0));
        put("saved", new Color(0, 180, 0));
        put("enabled", new Color(0, 180, 0));
        put("unloaded", new Color(0, 180, 0));
        put("uploaded", new Color(0, 180, 0));
        put("imported", new Color(0, 180, 0));
        put("done", new Color(0, 180, 0));

        put("warning", Color.YELLOW);

        put("unloading", Color.ORANGE);
        put("stopping", Color.ORANGE);
        put("disabling", Color.ORANGE);

        put("error", SEVERE.getColor());
        put("failed", SEVERE.getColor());
        put("fail", SEVERE.getColor());
        put("failure", SEVERE.getColor());
        put("exception", SEVERE.getColor());
        put("issue", SEVERE.getColor());
        put("cannot", SEVERE.getColor());
    }};

    // Object

    private final @NotNull Levels levels = new LevelsImpl();
    private final @NotNull Filters filters = new FiltersImpl();
    private @Nullable Registries registries = new RegistriesImpl();

    LoggerFactoryImpl() {
    }

    // Getters

    @Override
    public @NotNull Levels getLevels() {
        return levels;
    }

    @Override
    public @NotNull Filters getFilters() {
        return filters;
    }

    @Override
    public @Nullable Registries getRegistries() {
        return registries;
    }
    public void setRegistries(@Nullable Registries registries) throws IOException {
        if (getRegistries() != null && getRegistries() != registries) {
            getRegistries().flush();
        }

        this.registries = registries;
    }

    @Override
    public @NotNull Logger create(@NotNull Level level) {
        @NotNull StackTraceElement element = Arrays.stream(Thread.currentThread().getStackTrace()).skip(3).findFirst().orElseThrow(IllegalStateException::new);
        @NotNull LogOrigin origin = LogOrigin.create(element.getClassName(), element.getFileName(), element.getMethodName(), element.getLineNumber());

        return new LoggerImpl(this, level, origin) {
            @Override
            public @NotNull Registry log(@Nullable Object object) {
                final @NotNull Instant instant = Instant.now();
                final @NotNull RegistryImpl registry = new RegistryImpl(this, instant) {
                    @Override
                    public @NotNull String format() {
                        // Message
                        @NotNull StringBuilder content = new StringBuilder();

                        @NotNull Predicate<String> url = string -> {
                            @NotNull Pattern pattern = Pattern.compile("^(http(s?)://)?(((www\\.)?[a-zA-Z0-9.\\-_]+(\\.[a-zA-Z]{2,3})+)|(\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b))(/[a-zA-Z0-9_\\-\\s./?%#&=]*)?$");
                            return pattern.matcher(string).find();
                        };
                        @NotNull Function<StackTraceElement[], StackTraceElement[]> traceFilter = elements -> {
                            @NotNull StackTraceElement[] traces = throwable.getStackTrace();
                            for (@NotNull StackFilter filter : filters) {
                                traces = filter.format(traces);
                            }

                            return traces;
                        };

                        // Content
                        if (object != null) {
                            @NotNull String[] parts = object.toString().split(" ");

                            for (int index = 0; index < parts.length; index++) {
                                @NotNull String part = parts[index];
                                @Nullable String color = null;

                                if (colors.containsKey(part.toLowerCase())) {
                                    color = Colors.colored(colors.get(part.toLowerCase()));
                                } if (url.test(part)) {
                                    color = color + Colors.underlined();
                                }

                                content.append(color != null ? color : "")
                                        .append(part)
                                        .append(color != null ? reset() : "");

                                if (index + 1 != parts.length) {
                                    content.append(" ");
                                }
                            }
                        }
                        if (throwable != null) {
                            @NotNull StackTraceElement[] traces = traceFilter.apply(throwable.getStackTrace());

                            content.append(System.lineSeparator());
                            content.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append(System.lineSeparator());

                            for (int index = 0; index < traces.length; index++) {
                                if (index > 0) content.append(System.lineSeparator());
                                content.append("\tat ").append(traces[index]);
                            }

                            @Nullable Throwable recurring = throwable.getCause();
                            while (recurring != null) {
                                traces = traceFilter.apply(recurring.getStackTrace());

                                content.append("Caused by ").append(recurring.getClass().getName()).append(": ").append(recurring.getMessage()).append(System.lineSeparator());

                                for (int index = 0; index < traces.length; index++) {
                                    if (index > 0) content.append(System.lineSeparator());
                                    content.append("\tat ").append(traces[index]);
                                }

                                recurring = recurring.getCause();
                            }
                        }

                        // Date
                        @NotNull SimpleDateFormat format = new SimpleDateFormat("yy-dd-MM hh:mm:ss.S");
                        @NotNull String date = format.format(new Date(getDate().toEpochMilli()));
                        date = String.format("%-" + 21 + "s", date);

                        // Source
                        @NotNull String[] sources = origin.getClassName().split("\\.");
                        @NotNull String source = sources[sources.length - 1] + (origin.getLineNumber() >= 0 ? ":" + origin.getLineNumber() : "");

                        return bold(colored(new Color(65, 65, 65), "| ")) + date + " " + colored(level.getColor(), level.getName()) + "  " + source + " - " + content;
                    }
                };

                if (getFilters().isSuppressed(registry)) {
                    registry.setSuppressed(true);
                } else if (every != null && !every.canLog(LoggerFactoryImpl.this, registry)) {
                    registry.setSuppressed(true);
                }

                if (getRegistries() != null) {
                    getRegistries().add(registry);
                } if (!registry.wasSuppressed()) {
                    System.out.println(registry);
                }

                return registry;
            }
        };
    }

    // Natives

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LoggerFactoryImpl that = (LoggerFactoryImpl) object;
        return Objects.equals(getLevels(), that.getLevels()) && Objects.equals(getFilters(), that.getFilters()) && Objects.equals(getRegistries(), that.getRegistries());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getLevels(), getFilters(), getRegistries());
    }

    // Classes

    private static final class LevelsImpl implements Levels {

        private final @NotNull Set<Level> levels = new HashSet<>();

        private LevelsImpl() {
            levels.add(INFO);
            levels.add(SEVERE);
            levels.add(WARNING);
            levels.add(DEBUG);
        }

        @Override
        public @NotNull Stream<Level> stream() {
            return levels.stream();
        }
        @Override
        public @NotNull Iterator<Level> iterator() {
            return levels.iterator();
        }
    }
    private static final class FiltersImpl implements Filters {

        private final @NotNull List<Filter> filters = new LinkedList<>();

        private FiltersImpl() {
        }

        @Override
        public boolean add(@NotNull Filter filter) {
            return filters.add(filter);
        }

        @Override
        public boolean remove(@NotNull Filter filter) {
            return filters.remove(filter);
        }

        @Override
        public boolean contains(@NotNull Filter filter) {
            return filters.contains(filter);
        }

        @Override
        public int size() {
            return filters.size();
        }

        @Override
        public @NotNull Stream<Filter> stream() {
            return filters.stream();
        }

        @Override
        public @NotNull Iterator<Filter> iterator() {
            return filters.iterator();
        }

    }

}
