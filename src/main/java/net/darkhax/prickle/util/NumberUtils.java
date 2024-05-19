package net.darkhax.prickle.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtils {

    /**
     * Checks if number x is greater than number y.
     *
     * @param x The first number.
     * @param y The second number.
     * @return If x is greater than y.
     */
    public static boolean greaterThan(Number x, Number y) {
        return compareNumber(x, y) == 1;
    }

    /**
     * Checks if number x is equal to number y.
     *
     * @param x The first number.
     * @param y The second number.
     * @return If x is equal to y.
     */
    public static boolean equals(Number x, Number y) {
        return compareNumber(x, y) == 0;
    }

    /**
     * Checks if number x is less than number y.
     *
     * @param x The first number.
     * @param y The second number.
     * @return If x is less than y.
     */
    public static boolean lessThan(Number x, Number y) {
        return compareNumber(x, y) == -1;
    }

    /**
     * Compares two numbers as a comparator.
     *
     * @param x The first number.
     * @param y The second number.
     * @return The results of the comparison.
     */
    public static int compareNumber(Number x, Number y) {
        return (isSpecialNumber(x) || isSpecialNumber(y)) ? Double.compare(x.doubleValue(), y.doubleValue()) : asBigDecimal(x).compareTo(asBigDecimal(y));
    }

    /**
     * Checks if a number requires special handling. For example NaN and infinity.
     *
     * @param number The number to test.
     * @return If the number requires special handling.
     */
    public static boolean isSpecialNumber(Number number) {
        final boolean specialDouble = number instanceof Double doubleVal && (Double.isNaN(doubleVal) || Double.isInfinite(doubleVal));
        final boolean specialFloat = number instanceof Float floatVal && (Float.isNaN(floatVal) || Float.isInfinite(floatVal));
        return specialDouble || specialFloat;
    }

    /**
     * Gets a BigDecimal representation of a number. This can be useful when comparing two numbers.
     *
     * @param number The number to represent.
     * @return A BigDecimal representation of the number.
     */
    public static BigDecimal asBigDecimal(Number number) {
        if (number instanceof BigDecimal decimalVal) {
            return decimalVal;
        }
        else if (number instanceof BigInteger bigInt) {
            return new BigDecimal(bigInt);
        }
        else if (number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long) {
            return new BigDecimal(number.longValue());
        }
        else if (number instanceof Float || number instanceof Double) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(number.toString());
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("The number '" + number + "' of class " + number.getClass().getName() + " is not supported.");
        }
    }
}
