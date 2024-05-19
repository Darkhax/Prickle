package net.darkhax.prickle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to mark a field for inclusion in the config schema. Fields without this annotation will not
 * be included in the config file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Value {

    /**
     * Defines an alternative name to use when serializing the property. This allows you to use names that do not comply
     * with Java conventions like snake case in your file without using those names for your fields.
     *
     * @return The name to use for the property.
     */
    String name() default "";

    /**
     * Defines a comment for the property. Long comments will automatically be wrapped when the comment is written.
     *
     * @return The comment to attach to the property.
     */
    String comment() default "";

    /**
     * Defines a link to an online resource the reader can reference when deciding the value of the property.
     *
     * @return A link to an online reference.
     */
    String reference() default "";

    /**
     * Should the default value be written to the config as a decorator? This should generally be disabled for objects
     * and large arrays.
     *
     * @return If default values should be written.
     */
    boolean writeDefault() default true;
}