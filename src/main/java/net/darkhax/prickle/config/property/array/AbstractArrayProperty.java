package net.darkhax.prickle.config.property.array;

import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.Prickle;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.property.ObjectProperty;
import net.darkhax.prickle.config.property.PropertyResolver;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Represents an array of values in the config object.
 *
 * @param <T> The type of value held in the array.
 */
public abstract class AbstractArrayProperty<T> extends ObjectProperty<T> {

    /**
     * A set of Java types that align with the types of JSON primitives. This is used to determine if an array contains
     * complex values or not.
     */
    public static final Set<Class<?>> BASIC_TYPES = Set.of(Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, String.class);

    /**
     * Settings for the array passed in by the optional annotation.
     */
    private final ArraySettings settings;

    public AbstractArrayProperty(Field field, Object parent, T defaultValue, Value valueMeta, ArraySettings meta) {
        super(field, parent, defaultValue, valueMeta);
        this.settings = meta;
    }

    /**
     * Checks if the length of the array value is over the inline threshold.
     *
     * @param value The array value.
     * @return If the length of the array value is over the inline threshold.
     */
    public abstract boolean isOverInlineThreshold(T value);

    /**
     * Checks if the array value contains a complex entry.
     *
     * @param value The array value.
     * @return If the array value contains a complex entry.
     */
    public abstract boolean isComplex(T value);

    /**
     * Checks if the array value is empty.
     *
     * @param value The array value.
     * @return If the array value is empty.
     */
    public abstract boolean isEmpty(T value);

    /**
     * Writes entries of the array value to a JSONWriter.
     *
     * @param value    The array value.
     * @param out      The JSON writer to write data to.
     * @param resolver A resolver for GSON objects and config properties.
     * @param log      A logger that can be used to display errors and warnings.
     */
    public abstract void writeArrayValues(T value, JsonWriter out, PropertyResolver resolver, Logger log);

    /**
     * Gets settings for the array value. These are specified using an annotation on the field.
     *
     * @return The settings for the array.
     */
    public ArraySettings settings() {
        return this.settings;
    }

    @Override
    public void writeValue(T value, JsonWriter out, PropertyResolver resolver, Logger log) throws IOException {

        if (!this.isOverInlineThreshold(value) && (this.settings.inlineComplex() || !isComplex(value))) {
            out.beginArray();
            out.setIndent("");
            this.writeArrayValues(value, out, resolver, log);
            out.endArray();
            out.setIndent(Prickle.DEFAULT_INDENT);
        }
        else {
            out.beginArray();
            this.writeArrayValues(value, out, resolver, log);
            out.endArray();
        }
    }

    @Override
    public void writeAdditionalComments(JsonWriter out, PropertyResolver resolver, Logger log) throws IOException {
        // The default presumption is that arrays can be empty.
        if (!this.settings.allowEmpty()) {
            out.name("//empty-allowed");
            out.value(false);
        }
    }

    @Override
    public boolean validate(T value) throws IllegalArgumentException {
        if (this.isEmpty(value) && !this.settings.allowEmpty()) {
            throw new IllegalArgumentException("Value must not be empty, at least one entry is required!");
        }
        return true;
    }
}
