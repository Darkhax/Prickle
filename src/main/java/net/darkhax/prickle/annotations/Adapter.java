package net.darkhax.prickle.annotations;

import net.darkhax.prickle.config.property.IPropertyAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to enforce a specific property adapter for a given field. For example, you may want
 * certain doubles to be serialized using scientific notation and not others.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Adapter {

    /**
     * The class of the adapter to use when mapping the field. This class must have an accessible constructor and
     * implement {@link IPropertyAdapter}.
     *
     * @return The property adapter to use when mapping the field.
     */
    Class<? extends IPropertyAdapter<?>> value();
}
