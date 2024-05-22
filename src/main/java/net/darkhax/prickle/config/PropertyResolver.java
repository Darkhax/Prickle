package net.darkhax.prickle.config;

import com.google.gson.Gson;
import net.darkhax.prickle.annotations.Adapter;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.comment.IComment;
import net.darkhax.prickle.config.comment.ICommentResolver;
import net.darkhax.prickle.config.property.ConfigObjectProperty;
import net.darkhax.prickle.config.property.IConfigProperty;
import net.darkhax.prickle.config.property.IPropertyAdapter;
import net.darkhax.prickle.config.property.ObjectProperty;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles properties and settings related to resolving properties and JSON values.
 */
public class PropertyResolver {

    /**
     * A list of registered property adapters.
     */
    private final List<IPropertyAdapter<?>> propertyAdapters;

    /**
     * A cache of property adapters constructed using their class. This is used in conjunction with the {@link Adapter}
     * annotation.
     */
    private final Map<Class<?>, IPropertyAdapter<?>> adapterCache = new HashMap<>();

    /**
     * A property adapter used for sub-properties that are also config properties.
     */
    private final IPropertyAdapter<?> configObjectAdapter;

    /**
     * The configured gson instance.
     */
    private final Gson gson;

    private final ICommentResolver commentResolver;

    /**
     * A logger for errors and warnings.
     */
    private final Logger logger;

    public PropertyResolver(Gson gson, Logger logger, List<IPropertyAdapter<?>> propertyAdapters, ICommentResolver commentResolver) {
        this.propertyAdapters = propertyAdapters;
        this.gson = gson;
        this.logger = logger;
        this.configObjectAdapter = ConfigObjectProperty.adapter(this);
        this.commentResolver = commentResolver;
    }

    /**
     * Gets a configured GSON instance that should be used when serializing properties.
     *
     * @return The GSON instance to use for serializing.
     */
    public Gson gson() {
        return gson;
    }

    /**
     * Attempts to resolve a comment for a config property.
     *
     * @param field     The field to resolve.
     * @param value     The value of the field.
     * @param valueMeta The Value annotation that was on the field.
     * @return The comment that was resolved. If null no comment was specified.
     * @throws IOException An IOException may be raised when the resolver encounters a fatal error.
     */
    @Nullable
    public IComment toComment(Field field, Object value, Value valueMeta) throws IOException {
        return this.commentResolver.resolve(field, value, valueMeta);
    }

    /**
     * Gets a logger instance that can be used when serializing properties.
     *
     * @return The logger used for errors and warnings related to the config file.
     */
    public Logger logger() {
        return logger;
    }

    /**
     * Maps a Java field to a config property. This process will first look for the {@link Adapter} annotation on the
     * field, then try the registered property adapters, then try the sub-object fallback for config objects, and lastly
     * default to a normal Gson object.
     *
     * @param field    The field to map.
     * @param parent   The object that holds the field.
     * @param cfgValue The Value annotation that was on the field.
     * @return The mapped config property.
     * @throws IOException            An IOException may be raised when a property adapter encounters a fatal error.
     * @throws IllegalAccessException This exception may be raised if the field is not accessible.
     */
    public IConfigProperty<?> toProperty(Field field, Object parent, Value cfgValue) throws IOException, IllegalAccessException {

        final Object propertyValue = field.get(parent);

        // Use field specific adapter override first
        final Adapter adapterOverride = field.getAnnotation(Adapter.class);
        if (adapterOverride != null) {
            return this.adapterCache.computeIfAbsent(adapterOverride.value(), clazz -> {
                try {
                    final Object adapterObj = clazz.getConstructor().newInstance();
                    if (adapterObj instanceof IPropertyAdapter<?> adapterInst) {
                        return adapterInst;
                    }
                    throw new IllegalArgumentException("Adapter override on field '" + field.getName() + "' must implement IPropertyAdapter!");
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toValue(this, field, parent, propertyValue, cfgValue);
        }

        // Try the provided adapters
        for (IPropertyAdapter<?> adapter : this.propertyAdapters) {
            try {
                final IConfigProperty<?> property = adapter.toValue(this, field, parent, propertyValue, cfgValue);
                if (property != null) {
                    return property;
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Sub-Object fallback
        final IConfigProperty<?> configObjProperty = this.configObjectAdapter.toValue(this, field, parent, propertyValue, cfgValue);
        if (configObjProperty != null) {
            return configObjProperty;
        }

        // Use fallback
        return ObjectProperty.FALLBACK_ADAPTER.toValue(this, field, parent, propertyValue, cfgValue);
    }
}
