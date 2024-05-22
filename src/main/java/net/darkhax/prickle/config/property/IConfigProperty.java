package net.darkhax.prickle.config.property;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.config.PropertyResolver;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Represents a field that has been mapped to a config property. The config property is responsible for serializing,
 * validating, and applying the value.
 *
 * @param <T> The type of data held by the property.
 */
public interface IConfigProperty<T> {

    /**
     * Gets the value held by the property.
     *
     * @return The value held by the property.
     */
    T value();

    /**
     * Reads the value from the JSON and applies it to the parent object.
     *
     * @param reader   A reader containing a stream of JSON data.
     * @param resolver Resolves properties with GSON or config properties.
     * @param logger   A log instance used to log warnings and errors encountered when the value is read.
     * @throws IOException Fatal errors should be thrown if invalid data is encountered.
     */
    void read(JsonReader reader, PropertyResolver resolver, Logger logger) throws IOException;

    /**
     * Writes the value to the JSON writer.
     *
     * @param writer   A writer to write JSON data to.
     * @param resolver Resolves properties with GSON or config properties.
     * @param logger   A log instance used to log warnings and errors encountered when saving the value.
     * @throws IOException Fatal errors should be thrown if the property can not be written.
     */
    void write(JsonWriter writer, PropertyResolver resolver, Logger logger) throws IOException;

    /**
     * Validates if a value is valid for the property.
     *
     * @param value The value to validate.
     * @return If the value is true or not. Invalid properties will not be applied when reading the value from JSON.
     * @throws IllegalArgumentException Generally a fatal exception should be raised when the value is invalid.
     */
    boolean validate(T value) throws IllegalArgumentException;
}
