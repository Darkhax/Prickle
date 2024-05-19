package net.darkhax.prickle.config.property;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.ConfigObjectSerializer;
import net.darkhax.prickle.config.property.adapter.IPropertyAdapter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * A config property that contains an object of sub-properties.
 *
 * @param <T> The type of the config property.
 */
public class ConfigObjectProperty<T> extends ObjectProperty<T> {

    /**
     * The serializer for the sub-properties object.
     */
    private final ConfigObjectSerializer<?> serializer;

    private ConfigObjectProperty(Field field, Object parent, T defaultValue, Value valueMeta, ConfigObjectSerializer<?> serializer) {
        super(field, parent, defaultValue, valueMeta);
        this.serializer = serializer;
    }

    @Override
    public void writeValue(T value, JsonWriter writer, PropertyResolver resolver, Logger logger) throws IOException {
        this.serializer.write(writer);
    }

    @Override
    public void read(JsonReader reader, PropertyResolver resolver, Logger logger) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            if ("value".equals(reader.nextName())) {
                this.serializer.read(reader);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    /**
     * Creates a new adapter using a specific property resolver. This ensures the GSON instance and the type adapter
     * options are passed to the sub properties.
     *
     * @param resolver The property resolver.
     * @return An adapter that can handle config properties that are held as sub properties.
     */
    public static IPropertyAdapter<?> adapter(PropertyResolver resolver) {
        return new Adapter(resolver);
    }

    private record Adapter(PropertyResolver resolver) implements IPropertyAdapter<ConfigObjectProperty<?>> {

        @Override
        public @Nullable ConfigObjectProperty<?> toValue(PropertyResolver resolver, Field field, Object parent, Object value, Value valueMeta) throws IOException {

            if (isConfigObject(value)) {
                final ConfigObjectSerializer<?> serializer = new ConfigObjectSerializer<>(resolver, value);
                return new ConfigObjectProperty<>(field, parent, value, valueMeta, serializer);
            }

            return null;
        }

        private static boolean isConfigObject(Object value) {
            for (Field subField : value.getClass().getDeclaredFields()) {
                if (subField.getAnnotation(Value.class) != null) {
                    return true;
                }
            }
            return false;
        }
    }
}