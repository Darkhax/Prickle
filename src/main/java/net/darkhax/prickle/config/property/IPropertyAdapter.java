package net.darkhax.prickle.config.property;

import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.PropertyResolver;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Property adapters are used to map Java fields to config properties.
 *
 * @param <T> The type of config property to map values into.
 */
public interface IPropertyAdapter<T extends IConfigProperty<?>> {

    /**
     * Attempts to map a field on the config object to a config property. If the field is not adaptable null should be
     * returned.
     *
     * @param resolver  Resolves other config properties and JSON values using the options provided when the
     *                  ConfigManager is built.
     * @param field     The field being mapped.
     * @param parent    The parent object that holds the field.
     * @param value     The current value of the field.
     * @param valueMeta Metadata related to the config value.
     * @return If the field can be mapped to a property it will be returned, otherwise null.
     * @throws IOException Errors may occur when trying to map the field to a property.
     */
    @Nullable
    T toValue(PropertyResolver resolver, Field field, Object parent, @Nullable Object value, Value valueMeta) throws IOException;
}