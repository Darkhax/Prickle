package net.darkhax.prickle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this annotation is used on a collection or an array additional properties and constraints can be applied.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Array {

    /**
     * The threshold used when deciding if the collection should be inlined. Inlined collections are written as arrays
     * without newlines between their values.
     *
     * @return The threshold for deciding if the collection should be inlined.
     */
    int inlineCount() default 5;

    /**
     * Determines if collections containing complex objects can be inlined. By default, only JSON primitives will be
     * inlinable.
     *
     * @return If complex objects can be inlined.
     */
    boolean inlineComplex() default false;

    /**
     * Determines if the collection is allowed to be empty.
     *
     * @return If the collection may be empty.
     */
    boolean allowEmpty() default true;
}