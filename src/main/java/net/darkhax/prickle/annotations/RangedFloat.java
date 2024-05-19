package net.darkhax.prickle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this annotation is used on a float property it will validate that the value is within the range, inclusive of
 * the minimum and maximum values.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RangedFloat {

    /**
     * Gets the lowest value permitted for the property.
     *
     * @return The lowest permitted value.
     */
    float min() default -Float.MAX_VALUE;

    /**
     * Gets the highest value permitted for the property.
     *
     * @return The highest permitted value.
     */
    float max() default Float.MAX_VALUE;
}