package net.darkhax.prickle.config.property;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.PropertyResolver;
import net.darkhax.prickle.config.comment.IComment;
import net.darkhax.prickle.config.comment.WrappedComment;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;

public class ObjectProperty<T> implements IConfigProperty<T> {

    /**
     * The property adapter that is used when a field does not have an explicit property adapter.
     */
    public static final IPropertyAdapter<ObjectProperty<?>> FALLBACK_ADAPTER = new FallbackAdapter();

    /**
     * The field mapped to the property.
     */
    private final Field field;

    /**
     * The parent that holds the field.
     */
    private final Object parent;

    /**
     * An optional comment that is added to the value.
     */
    private final IComment comment;

    /**
     * The default value for the property. This is generally the property that the field is initialized with.
     */
    private final T defaultValue;

    /**
     * Determines if the default value should be added as a decorator.
     */
    private final boolean writeDefault;

    /**
     * A link to an online resource the reader may want to reference when evaluating the property.
     */
    private final String reference;

    public ObjectProperty(Field field, Object parent, T defaultValue, Value valueMeta, IComment comment) {
        this.field = field;
        this.parent = parent;
        this.comment = comment;
        this.defaultValue = defaultValue;
        this.writeDefault = valueMeta.writeDefault();
        this.reference = valueMeta.reference();
    }

    /**
     * Gets the comment for the property.
     *
     * @return The comment for the property.
     */
    public IComment comment() {
        return this.comment;
    }

    /**
     * Gets the default value for the property. This is generally the value that the field was initialized with.
     *
     * @return The default value for the property.
     */
    public T defaultValue() {
        return this.defaultValue;
    }

    /**
     * Checks if the default value should be written as a decorator.
     *
     * @return Should the default value be written?
     */
    public boolean writeDefaultValue() {
        return this.writeDefault;
    }

    /**
     * Gets the current value of the property.
     *
     * @return The current value of the property.
     */
    @Override
    public T value() {
        try {
            return (T) this.field.get(parent);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void read(JsonReader reader, PropertyResolver resolver, Logger logger) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            if ("value".equals(reader.nextName())) {
                final T readValue = this.readValue(reader, resolver, logger);
                if (this.validate(readValue)) {
                    try {
                        this.field.set(parent, readValue);
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    @Override
    public void write(JsonWriter writer, PropertyResolver resolver, Logger logger) throws IOException {
        writer.beginObject();

        // Write the comment
        if (this.comment() != null) {
            writer.name("//");
            resolver.gson().toJson(this.comment(), WrappedComment.class, writer);
        }

        // Write the decorators
        this.writeAdditionalComments(writer, resolver, logger);

        // Write the reference
        if (this.reference != null && !this.reference.isBlank()) {
            writer.name("//reference");
            writer.value(this.reference);
        }

        // Write the default
        if (this.writeDefaultValue() && this.defaultValue != null) {
            this.writeDefaultValue(writer, resolver, logger);
        }

        // Write the value.
        writer.name("value");
        this.writeValue(this.value(), writer, resolver, logger);
        writer.endObject();
    }

    /**
     * Writes a value to the JSON writer.
     *
     * @param value    The value to be written.
     * @param writer   A writer to write JSON data to.
     * @param resolver Resolves properties with GSON or config properties.
     * @param logger   A log instance used to log warnings and errors encountered when saving the value.
     * @throws IOException A fatal exception may be thrown if the value could not be written.
     */
    public void writeValue(T value, JsonWriter writer, PropertyResolver resolver, Logger logger) throws IOException {
        resolver.gson().toJson(value, this.field.getGenericType(), writer);
    }

    /**
     * Reads a value from the JSON reader.
     *
     * @param reader   A reader containing a stream of JSON data.
     * @param resolver Resolves properties with GSON or config properties.
     * @param logger   A log instance used to log warnings and errors encountered when the value is read.
     * @return The value that was read from the reader.
     * @throws IOException A fatal exception may be thrown if the value could not be read.
     */
    public T readValue(JsonReader reader, PropertyResolver resolver, Logger logger) throws IOException {
        return resolver.gson().fromJson(reader, this.field.getGenericType());
    }

    /**
     * Handles writing the default value to the JSON writer.
     *
     * @param writer   A writer to write JSON data to.
     * @param resolver Resolves properties with GSON or config properties.
     * @param logger   A log instance used to log warnings and errors encountered when saving the value.
     * @throws IOException A fatal exception may be thrown if the value could not be written.
     */
    public void writeDefaultValue(JsonWriter writer, PropertyResolver resolver, Logger logger) throws IOException {
        writer.name("//default");
        this.writeValue(this.defaultValue(), writer, resolver, logger);
    }

    /**
     * Writes additional decorators to the JSON writer.
     *
     * @param writer   A writer to write JSON data to.
     * @param resolver Resolves properties with GSON or config properties.
     * @param logger   A log instance used to log warnings and errors encountered when saving the value.
     * @throws IOException A fatal exception may be thrown if the decorators could not be written.
     */
    public void writeAdditionalComments(JsonWriter writer, PropertyResolver resolver, Logger logger) throws IOException {
        // No-op
    }

    @Override
    public boolean validate(T value) throws IllegalArgumentException {
        // Everything is valid because this property type has no validation criteria.
        return true;
    }

    private static class FallbackAdapter implements IPropertyAdapter<ObjectProperty<?>> {

        @Override
        public ObjectProperty<?> toValue(PropertyResolver resolver, Field field, Object parent, Object value, Value valueMeta) throws IOException {

            return new ObjectProperty<>(field, parent, value, valueMeta, resolver.toComment(field, value, valueMeta));
        }
    }
}
