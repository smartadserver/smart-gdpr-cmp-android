package com.fidzup.android.cmp.util;


import junit.framework.Assert;

import org.junit.Test;

public class BitsStringTest {

    private final String SAMPLE_BITS_STRING = "000001001110001001001110011011001000010010001110001001001110011011001000010010000000000000000001000000000000000100001101000000000000111000000000000000000000000000000000101000000000000000000000";
    private final String SAMPLE_BASE64_STRING = "BOJObISOJObISAABAAENAA4AAAAAoAAA";

    @Test
    public void testBitsStringFromBase64() {
        Assert.assertEquals(SAMPLE_BITS_STRING, new BitsString(false, SAMPLE_BASE64_STRING).bitsValue);
    }

    @Test
    public void testBitsStringFromBits() {
        Assert.assertEquals(SAMPLE_BASE64_STRING, new BitsString(true, SAMPLE_BITS_STRING).stringValue);
    }

    @Test
    public void testInvalidBase64StringAreRejected() {
        try {
            new BitsString(false, "ABC/+");
            Assert.fail("Should have raised an exception.");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    @Test
    public void testInvalidBitsStringAreRejected() {
        try {
            new BitsString(true, "000001001110101100001101A");
            Assert.fail("Should have raised an exception.");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }
}
