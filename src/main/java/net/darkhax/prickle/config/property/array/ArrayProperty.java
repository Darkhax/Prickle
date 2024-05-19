package net.darkhax.prickle.config.property.array;

import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.annotations.Array;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.property.PropertyResolver;
import net.darkhax.prickle.config.property.adapter.IPropertyAdapter;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * An array property that can handle Java arrays.
 *
 * @param <T> The type of the array.
 */
public class ArrayProperty<T> extends AbstractArrayProperty<Object> {

    /**
     * The property adapter for Java arrays.
     */
    public static final Adapter ADAPTER = new Adapter();

    private ArrayProperty(Field field, Object parent, T defaultValue, Value valueMeta, ArraySettings meta) {
        super(field, parent, defaultValue, valueMeta, meta);
    }

    @Override
    public boolean isOverInlineThreshold(Object value) {
        return java.lang.reflect.Array.getLength(value) > this.settings().inlineCount();
    }

    @Override
    public boolean isComplex(Object value) {
        for (int i = 0; i < java.lang.reflect.Array.getLength(value); i++) {
            final Object entry = java.lang.reflect.Array.get(value, i);
            if (!AbstractArrayProperty.BASIC_TYPES.contains(entry.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty(Object value) {
        return java.lang.reflect.Array.getLength(value) != 0;
    }

    @Override
    public void writeArrayValues(Object value, JsonWriter out, PropertyResolver resolver, Logger log) {
        for (int i = 0; i < java.lang.reflect.Array.getLength(value); i++) {
            final Object entry = java.lang.reflect.Array.get(value, i);
            resolver.gson().toJson(entry, entry.getClass(), out);
        }
    }

    private static class Adapter implements IPropertyAdapter<ArrayProperty<?>> {

        @Override
        public ArrayProperty<?> toValue(PropertyResolver resolver, Field field, Object parent, Object value, Value valueMeta) throws IOException {
            if (field.getType().isArray()) {
                final Array arrayMeta = field.getAnnotation(Array.class);
                final ArraySettings settings = arrayMeta != null ? new ArraySettings(arrayMeta) : ArraySettings.DEFAULT;
                return new ArrayProperty<>(field, parent, value, valueMeta, settings);
            }
            return null;
        }
    }
}
