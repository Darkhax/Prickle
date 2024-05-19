package net.darkhax.prickle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this annotation is used on an integer property it will validate that the value is within the range, inclusive of
 * the minimum and maximum value.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RangedInt {

    /**
     * Gets the lowest value permitted for the property.
     *
     * @return The lowest permitted value.
     */
    int min() default Integer.MIN_VALUE;

    /**
     * Gets the highest value permitted for the property.
     *
     * @return The highest permitted value.
     */
    int max() default Integer.MAX_VALUE;
}