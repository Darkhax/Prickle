package net.darkhax.prickle.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this annotation is used on a String the value will be validated using the provided regex pattern.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Regex {

    /**
     * A regex pattern that will be used to validate the property.
     *
     * @return The regex pattern used to validate the property.
     */
    String value();
}