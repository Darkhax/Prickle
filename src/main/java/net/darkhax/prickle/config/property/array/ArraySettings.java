package net.darkhax.prickle.config.property.array;

import net.darkhax.prickle.annotations.Array;

/**
 * Settings for an array property. These are modified using the {@link Array} annotation on the field of the value.
 */
public class ArraySettings {

    /**
     * Default settings that are used when the {@link Array} annotation was not found.
     */
    public static final ArraySettings DEFAULT = new ArraySettings();

    /**
     * The inline threshold for the array. Arrays with values over this threshold will not be inlined.
     */
    private final int inlineCount;

    /**
     * Determines if arrays containing complex entries can be inlined.
     */
    private final boolean inlineComplex;

    /**
     * Determines if the array can be empty or not.
     */
    private final boolean allowEmpty;

    /**
     * Creates a settings object from the {@link Array} annotation.
     *
     * @param meta The metadata specified using the annotation.
     */
    public ArraySettings(Array meta) {
        this.inlineCount = meta.inlineCount();
        this.inlineComplex = meta.inlineComplex();
        this.allowEmpty = meta.allowEmpty();
    }

    private ArraySettings() {
        this.inlineCount = 5;
        this.inlineComplex = false;
        this.allowEmpty = true;
    }

    /**
     * Gets the inline threshold for the array. If the length of the array does not surpass this value it can be inlined
     * to a single string.
     *
     * @return The inline threshold for the array.
     */
    public int inlineCount() {
        return inlineCount;
    }

    /**
     * Checks if complex objects can be inlined.
     *
     * @return If complex values can be inlined.
     */
    public boolean inlineComplex() {
        return inlineComplex;
    }

    /**
     * Checks if the array can be empty.
     *
     * @return If the array can be empty.
     */
    public boolean allowEmpty() {
        return allowEmpty;
    }
}