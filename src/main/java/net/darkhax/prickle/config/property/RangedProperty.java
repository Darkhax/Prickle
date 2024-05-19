package net.darkhax.prickle.config.property;

import com.google.gson.stream.JsonWriter;
import net.darkhax.prickle.annotations.RangedDouble;
import net.darkhax.prickle.annotations.RangedFloat;
import net.darkhax.prickle.annotations.RangedInt;
import net.darkhax.prickle.annotations.RangedLong;
import net.darkhax.prickle.annotations.Value;
import net.darkhax.prickle.config.property.adapter.IPropertyAdapter;
import net.darkhax.prickle.util.NumberUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Represents a number property that can only fall within the specified range.
 *
 * @param <T> The type of number.
 */
public class RangedProperty<T extends Number> extends ObjectProperty<T> {

    /**
     * The adapter for ranged number properties.
     */
    public static final IPropertyAdapter<?> ADAPTER = new Adapter();

    /**
     * The lowest possible value. If the minimum is null the minimum check is disabled.
     */
    @Nullable
    private final T min;

    /**
     * The highest possible value. If the maximum is null the maximum check is disabled.
     */
    @Nullable
    private final T max;

    private RangedProperty(Field field, Object parent, T defaultValue, T min, T max, Value valueMeta) {
        super(field, parent, defaultValue, valueMeta);
        this.min = min;
        this.max = max;
        if (!this.validate(defaultValue)) {
            String errorMessage = "Value " + defaultValue + " is not within the specified range!";
            if (min != null) {
                errorMessage += " min=" + min;
            }
            if (max != null) {
                errorMessage += " max=" + max;
            }
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Nullable
    private T min() {
        return this.min;
    }

    @Nullable
    public T max() {
        return this.max;
    }

    @Override
    public void writeAdditionalComments(JsonWriter writer, PropertyResolver resolver, Logger logger) throws IOException {
        final StringBuilder range = new StringBuilder();

        if (this.min() != null) {
            range.append(">=" + this.min());
        }
        if (this.max() != null) {
            if (!range.isEmpty()) {
                range.append(" AND ");
            }
            range.append("<=" + this.max());
        }
        if (!range.isEmpty()) {
            writer.name("//range");
            writer.value(range.toString());
        }
    }

    @Override
    public boolean validate(T value) throws IllegalArgumentException {

        if (value == null) {
            throw new IllegalArgumentException("Number values must not be null!");
        }

        if (this.min() != null && NumberUtils.lessThan(value, this.min())) {
            throw new IllegalArgumentException("Value '" + value + "' is less than the minimum value '" + this.min() + "'.");
        }

        if (this.max() != null && NumberUtils.greaterThan(value, this.max())) {
            throw new IllegalArgumentException("Value '" + value + "' is greater than the maximum value '" + this.max() + "'.");
        }

        return (this.min() == null || !NumberUtils.lessThan(value, this.min())) && (this.max() == null || !NumberUtils.greaterThan(value, this.max()));
    }

    private static class Adapter implements IPropertyAdapter<RangedProperty<?>> {

        @Override
        public RangedProperty<?> toValue(PropertyResolver resolver, Field field, Object parent, Object value, Value valueMeta) throws IOException {

            if (value instanceof Integer intVal) {
                final RangedInt ranged = field.getAnnotation(RangedInt.class);
                if (ranged != null) {
                    final Integer min = ranged.min() != Integer.MIN_VALUE ? ranged.min() : null;
                    final Integer max = ranged.max() != Integer.MAX_VALUE ? ranged.max() : null;
                    return new RangedProperty<>(field, parent, intVal, min, max, valueMeta);
                }
            }

            if (value instanceof Long longVal) {
                final RangedLong ranged = field.getAnnotation(RangedLong.class);
                if (ranged != null) {
                    final Long min = ranged.min() != Long.MIN_VALUE ? ranged.min() : null;
                    final Long max = ranged.max() != Long.MAX_VALUE ? ranged.max() : null;
                    return new RangedProperty<>(field, parent, longVal, min, max, valueMeta);
                }
            }

            if (value instanceof Float floatVal) {
                final RangedFloat ranged = field.getAnnotation(RangedFloat.class);
                if (ranged != null) {
                    final Float min = ranged.min() != -Float.MAX_VALUE ? ranged.min() : null;
                    final Float max = ranged.max() != Float.MAX_VALUE ? ranged.max() : null;
                    return new RangedProperty<>(field, parent, floatVal, min, max, valueMeta);
                }
            }

            if (value instanceof Double doubleVal) {
                final RangedDouble ranged = field.getAnnotation(RangedDouble.class);
                if (ranged != null) {
                    final Double min = ranged.min() != -Double.MAX_VALUE ? ranged.min() : null;
                    final Double max = ranged.max() != Double.MAX_VALUE ? ranged.max() : null;
                    return new RangedProperty<>(field, parent, doubleVal, min, max, valueMeta);
                }
            }

            return null;
        }
    }
}