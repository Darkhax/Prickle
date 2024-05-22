package net.darkhax.prickle.config.property.array;

import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.annotations.Array;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.PropertyResolver;
import net.darkhax.prickle.config.comment.IComment;
import net.darkhax.prickle.config.property.IPropertyAdapter;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * An array property that can handle Java collections.
 *
 * @param <T> The type of the collection.
 */
public class CollectionArrayProperty<T extends Collection<?>> extends AbstractArrayProperty<T> {

    /**
     * A property adapter for Java collections.
     */
    public static final Adapter ADAPTER = new Adapter();

    private final ParameterizedType paramType;

    private CollectionArrayProperty(Field field, Object parent, T defaultValue, ParameterizedType paramType, Value valueMeta, ArraySettings meta, IComment comment) {
        super(field, parent, defaultValue, valueMeta, meta, comment);
        this.paramType = paramType;
    }

    @Override
    public boolean isOverInlineThreshold(T value) {
        return value.size() > this.settings().inlineCount();
    }

    @Override
    public boolean isComplex(T value) {
        for (Object entry : value) {
            if (!AbstractArrayProperty.BASIC_TYPES.contains(entry.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEmpty(T value) {
        return value.isEmpty();
    }

    @Override
    public void writeArrayValues(T value, JsonWriter out, PropertyResolver resolver, Logger log) {
        for (Object o : value) {
            resolver.gson().toJson(o, paramType.getActualTypeArguments()[0], out);
        }
    }

    private static class Adapter implements IPropertyAdapter<CollectionArrayProperty<?>> {

        @Override
        public CollectionArrayProperty<?> toValue(PropertyResolver resolver, Field field, Object parent, Object value, Value valueMeta) throws IOException {
            if (value instanceof Collection<?> collection && field.getGenericType() instanceof ParameterizedType paramType) {
                final Array arrayMeta = field.getAnnotation(Array.class);
                final ArraySettings settings = arrayMeta != null ? new ArraySettings(arrayMeta) : ArraySettings.DEFAULT;
                return new CollectionArrayProperty<>(field, parent, collection, paramType, valueMeta, settings, resolver.toComment(field, value, valueMeta));
            }
            return null;
        }
    }
}
