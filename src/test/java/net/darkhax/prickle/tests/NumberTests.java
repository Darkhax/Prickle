package net.darkhax.prickle.tests;

import net.darkhax.prickle.util.NumberUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberTests {

    @Test
    public void greaterThan() {
        // int
        Assertions.assertTrue(NumberUtils.greaterThan(5, 1));
        Assertions.assertFalse(NumberUtils.greaterThan(10, 555));

        // long
        Assertions.assertTrue(NumberUtils.greaterThan((long) 100, (long) 32));
        Assertions.assertFalse(NumberUtils.greaterThan((long) 5, (long) 321));

        // float
        Assertions.assertTrue(NumberUtils.greaterThan(1.23f, 0.32f));
        Assertions.assertFalse(NumberUtils.greaterThan(5.32f, 123.4f));

        // double
        Assertions.assertTrue(NumberUtils.greaterThan(100.432d, 5.44d));
        Assertions.assertFalse(NumberUtils.greaterThan(2.22d, 8.54d));

        // mixed
        Assertions.assertTrue(NumberUtils.greaterThan(100f, 22));
        Assertions.assertFalse(NumberUtils.greaterThan(1.23f, (long) 888));
    }

    @Test
    public void lessThan() {
        // int
        Assertions.assertFalse(NumberUtils.lessThan(5, 1));
        Assertions.assertTrue(NumberUtils.lessThan(10, 555));

        // long
        Assertions.assertFalse(NumberUtils.lessThan((long) 100, (long) 32));
        Assertions.assertTrue(NumberUtils.lessThan((long) 5, (long) 321));

        // float
        Assertions.assertFalse(NumberUtils.lessThan(1.23f, 0.32f));
        Assertions.assertTrue(NumberUtils.lessThan(5.32f, 123.4f));

        // double
        Assertions.assertFalse(NumberUtils.lessThan(100.432d, 5.44d));
        Assertions.assertTrue(NumberUtils.lessThan(2.22d, 8.54d));

        // mixed
        Assertions.assertFalse(NumberUtils.lessThan(100f, 22));
        Assertions.assertTrue(NumberUtils.lessThan(1.23f, (long) 888));
    }

    @Test
    public void equal() {
        // int
        Assertions.assertTrue(NumberUtils.equals(5, 5));
        Assertions.assertFalse(NumberUtils.equals(10, 555));

        // long
        Assertions.assertTrue(NumberUtils.equals(100L, 100L));
        Assertions.assertFalse(NumberUtils.equals(5L, 321L));

        // float
        Assertions.assertTrue(NumberUtils.equals(1.23f, 1.23f));
        Assertions.assertFalse(NumberUtils.equals(5.32f, 123.4f));

        // double
        Assertions.assertTrue(NumberUtils.equals(100.432d, 100.432d));
        Assertions.assertFalse(NumberUtils.equals(2.22d, 8.54d));

        // mixed
        Assertions.assertTrue(NumberUtils.equals(100f, 100));
        Assertions.assertFalse(NumberUtils.equals(1.23f, 888L));
    }

    @Test
    public void isSpecial() {
        // int
        Assertions.assertFalse(NumberUtils.isSpecialNumber(123));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(Integer.MAX_VALUE));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(Integer.MIN_VALUE));

        // long
        Assertions.assertFalse(NumberUtils.isSpecialNumber(123L));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(Long.MAX_VALUE));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(Long.MIN_VALUE));

        // float
        Assertions.assertFalse(NumberUtils.isSpecialNumber(3.45f));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(Math.PI));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(Float.MAX_VALUE));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(-Float.MAX_VALUE));
        Assertions.assertTrue(NumberUtils.isSpecialNumber(Float.NaN));
        Assertions.assertTrue(NumberUtils.isSpecialNumber(Float.POSITIVE_INFINITY));
        Assertions.assertTrue(NumberUtils.isSpecialNumber(Float.NEGATIVE_INFINITY));

        // double
        Assertions.assertFalse(NumberUtils.isSpecialNumber(5.67d));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(Double.MAX_VALUE));
        Assertions.assertFalse(NumberUtils.isSpecialNumber(-Double.MAX_VALUE));
        Assertions.assertTrue(NumberUtils.isSpecialNumber(Double.NaN));
        Assertions.assertTrue(NumberUtils.isSpecialNumber(Double.POSITIVE_INFINITY));
        Assertions.assertTrue(NumberUtils.isSpecialNumber(Double.NEGATIVE_INFINITY));
    }
}
