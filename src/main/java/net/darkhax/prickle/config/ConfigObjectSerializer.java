package net.darkhax.prickle.config;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.property.IConfigProperty;
import net.darkhax.prickle.config.property.PropertyResolver;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A serializer that will map an object to config property adapters and make the serializable.
 *
 * @param <T> The type of object being serialized.
 */
public class ConfigObjectSerializer<T> {

    /**
     * A resolver for config properties and GSON data.
     */
    private final PropertyResolver propertyResolver;

    /**
     * The logger used for warnings and errors.
     */
    private final Logger log;

    /**
     * The properties that were mapped from the config object.
     */
    private final Map<String, SchemaEntry> properties;

    public ConfigObjectSerializer(PropertyResolver propertyResolver, T dataObj) {
        this.propertyResolver = propertyResolver;
        this.log = propertyResolver.logger();
        this.properties = mapSchema(dataObj);
    }

    /**
     * Scans the fields of an object and maps them to a config properties.
     *
     * @param dataObj The config object to map.
     * @return The mapped out config schema.
     */
    private Map<String, SchemaEntry> mapSchema(T dataObj) {

        final Map<String, SchemaEntry> schema = new LinkedHashMap<>();

        for (Field field : dataObj.getClass().getDeclaredFields()) {

            final Value valueMeta = field.getAnnotation(Value.class);
            if (valueMeta != null) {
                field.setAccessible(true);
                final String propertyName = (valueMeta.name() == null || valueMeta.name().isBlank()) ? field.getName() : valueMeta.name();

                if (schema.containsKey(propertyName)) {
                    throw new IllegalStateException("The property name '" + propertyName + "' has already been found to property '" + schema.get(propertyName) + "'. Check " + dataObj.getClass().getName() + "#" + field.getName());
                }
                try {
                    final IConfigProperty<?> property = this.propertyResolver.toProperty(field, dataObj, valueMeta);
                    schema.put(propertyName, new SchemaEntry(field, propertyName, valueMeta, property));
                    this.log.info("{} = {}", propertyName, property);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (schema.isEmpty()) {
            throw new RuntimeException("Invalid cfg class!");
        }

        return Collections.unmodifiableMap(schema);
    }

    /**
     * Writes the config object to a JSON writer.
     *
     * @param out The writer that data should be written to.
     * @throws IOException This may be raised if a fatal error is encountered while writing the data.
     */
    public void write(JsonWriter out) throws IOException {
        out.beginObject();
        for (Map.Entry<String, SchemaEntry> entry : this.properties.entrySet()) {
            out.name(entry.getKey());
            entry.getValue().property().write(out, this.propertyResolver, log);
        }
        out.endObject();
    }

    /**
     * Reads JSON data and updates the properties on the config object.
     *
     * @param in The input reader.
     * @throws IOException This may be raised if a fatal error is encountered while reading the data.
     */
    public void read(JsonReader in) throws IOException {

        in.beginObject();

        while (in.hasNext()) {

            final String propertyKey = in.nextName();
            final SchemaEntry entry = this.properties.get(propertyKey);

            if (entry != null) {
                entry.field().setAccessible(true);
                entry.property().read(in, this.propertyResolver, log);
            }
            else {
                this.log.warn("Skipping unknown property '{}' found in user JSON.", propertyKey);
                in.skipValue();
            }
        }

        in.endObject();
    }

    /**
     * Represents a mapped out entry in the config schema.
     *
     * @param field          The field that was mapped.
     * @param serializedName The name to use when serializing the JSON data.
     * @param valueMeta      The value meta annotation.
     * @param property       The config property wrapper that was determined for the field.
     */
    private record SchemaEntry(Field field, String serializedName, Value valueMeta, IConfigProperty<?> property) {
    }
}