package net.darkhax.prickle.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.Prickle;
import net.darkhax.prickle.config.comment.CommentTypeAdapter;
import net.darkhax.prickle.config.comment.ICommentResolver;
import net.darkhax.prickle.config.comment.WrappedComment;
import net.darkhax.prickle.config.comment.WrappedCommentResolver;
import net.darkhax.prickle.config.property.IPropertyAdapter;
import net.darkhax.prickle.config.property.RangedProperty;
import net.darkhax.prickle.config.property.RegexStringProperty;
import net.darkhax.prickle.config.property.array.ArrayProperty;
import net.darkhax.prickle.config.property.array.CollectionArrayProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigManager<T> {

    /**
     * The path to the config file.
     */
    private final Path filePath;

    /**
     * A logger instance used by the config manager and all of its property adapters.
     */
    private final Logger log;

    /**
     * A config serializer unique to the object being managed.
     */
    private final ConfigObjectSerializer<T> configSerializer;

    /**
     * The config object being managed.
     */
    private final T obj;

    private ConfigManager(Path filePath, Logger log, T obj, PropertyResolver resolver) {

        this.filePath = filePath;
        this.log = log;
        this.obj = obj;
        this.configSerializer = new ConfigObjectSerializer<>(resolver, this.obj);
    }

    /**
     * Loads the config file from disk. If the file does not exist it will try to generate one using the schema that was
     * mapped out when building the config manager.
     */
    public void load() {

        if (!Files.exists(this.filePath)) {
            this.save();
        }

        try (JsonReader reader = new JsonReader(Files.newBufferedReader(this.filePath))) {
            configSerializer.read(reader);
        }
        catch (IOException e) {
            this.log.error("Unable to load config file from {}!", this.filePath);
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the config object to the specified file path. If the file does not exist one will be created.
     */
    public void save() {

        if (!Files.exists(this.filePath)) {
            try {
                Files.createFile(this.filePath);
            }
            catch (IOException e) {
                this.log.error("Unable to create config file at {}!", this.filePath);
                throw new RuntimeException(e);
            }
        }

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(this.filePath, StandardCharsets.UTF_8))) {
            writer.setIndent(Prickle.DEFAULT_INDENT);
            this.configSerializer.write(writer);
        }

        catch (IOException e) {
            this.log.error("Could not save config file to {}!", this.filePath);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the object being managed.
     *
     * @return The config object being managed.
     */
    public T get() {
        return this.obj;
    }

    /**
     * A builder for {@link ConfigManager} objects.
     *
     * @param <T> Type of the object being managed.
     */
    public static class Builder<T> {

        private final Path filePath;
        private final List<IPropertyAdapter<?>> propertyAdapters = new LinkedList<>();
        private final Map<Class<?>, IPropertyAdapter<?>> adapterOverrideCache = new HashMap<>();
        private final List<Function<GsonBuilder, GsonBuilder>> gsonConfigs = new LinkedList<>();
        private ICommentResolver commentResolver;
        private Logger logger = null;

        public Builder(Path filePath) {
            this.filePath = filePath;

            this.gsonConfig(builder -> {
                builder
                        // Tells GSON to use indents when serializing values. While the JsonWriter
                        // already has an indent this GSON instance may be used to write values into
                        // the writer and will need their own indents.
                        .setPrettyPrinting()

                        // Our configuration files are not being embedded as HTML. There is no need for
                        // us to escape HTML characters. Disabling this option helps ensure values are
                        // written as the developer and user would expect.
                        .disableHtmlEscaping()

                        // Allows special double like NaN and Infinity to be serialized. While special
                        // floating point values are not permitted in standard JSON they are often
                        // deserialized regardless.
                        .serializeSpecialFloatingPointValues()

                        // Disables some weird number serialization quirks like scientific notation
                        // being used for long numbers.
                        .setNumberToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)

                        // Registers a type adapter to handle our implementation of comments.
                        .registerTypeAdapter(WrappedComment.class, CommentTypeAdapter.INSTANCE);
            });
            this.adapter(RegexStringProperty.ADAPTER);
            this.adapter(RangedProperty.ADAPTER);
            this.adapter(ArrayProperty.ADAPTER);
            this.adapter(CollectionArrayProperty.ADAPTER);
            this.commentResolver(WrappedCommentResolver.DEFAULT);
        }

        /**
         * Sets the logger to use when outputting errors and warnings. If a logger is not set one will be created using
         * the class of the config object.
         *
         * @param logger The logger to use for errors and warnings.
         * @return The same builder instance.
         */
        public Builder<T> logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Registers a new property adapter for the config object.
         *
         * @param adapter The property adapter to register.
         * @return The same builder instance.
         */
        public Builder<T> adapter(IPropertyAdapter<?> adapter) {
            this.propertyAdapters.add(adapter);
            return this;
        }

        /**
         * Registers a consumer that can be used to set up the underlying Gson instance.
         *
         * @param config A consumer that accepts the GsonBuilder and configures it.
         * @return The same builder instance.
         */
        public Builder<T> gsonConfig(Consumer<GsonBuilder> config) {
            return this.gsonConfig(gson -> {
                config.accept(gson);
                return gson;
            });
        }

        /**
         * Registers a consumer that can be used to set up or replace the underlying Gson instance.
         *
         * @param config A function that accepts the GsonBuilder and then returns a GsonBuilder that will replace the
         *               previous one.
         * @return The same builder instance.
         */
        public Builder<T> gsonConfig(Function<GsonBuilder, GsonBuilder> config) {
            this.gsonConfigs.add(config);
            return this;
        }

        /**
         * Sets a new comment resolver that should handle creating comments.
         *
         * @param resolver The new comment resolver.
         * @return The same builder instance.
         */
        public Builder<T> commentResolver(ICommentResolver resolver) {
            this.commentResolver = resolver;
            return this;
        }

        /**
         * Builds the config manager.
         *
         * @param cfgData The object attach to the config manager. This object will be mapped to a config schema and its
         *                properties will be updated as the config is reloaded.
         * @return The newly built config manager.
         */
        public ConfigManager<T> build(T cfgData) {

            if (cfgData == null) {
                throw new IllegalStateException("Can not create a config from a null value!");
            }

            if (this.filePath == null) {
                throw new IllegalStateException("Config manager can not be built without a file path!");
            }

            if (this.logger == null) {
                this.logger = LoggerFactory.getLogger(cfgData.getClass());
            }

            GsonBuilder gsonBuilder = new GsonBuilder();
            for (Function<GsonBuilder, GsonBuilder> gsonConfig : this.gsonConfigs) {
                gsonBuilder = gsonConfig.apply(gsonBuilder);
            }
            final Gson gson = gsonBuilder.create();

            return new ConfigManager<T>(this.filePath, this.logger, cfgData, new PropertyResolver(gson, this.logger, this.propertyAdapters, this.commentResolver));
        }
    }
}