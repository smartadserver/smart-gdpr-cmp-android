package com.fidzup.android.cmp.util;

import com.fidzup.android.cmp.model.Language;

import junit.framework.Assert;

import org.junit.Test;

public class BitUtilsTest {

    @Test
    public void testLongToBits() {
        Assert.assertEquals("0", BitUtils.longToBits(0, 1));
        Assert.assertEquals("1", BitUtils.longToBits(1, 1));
        Assert.assertEquals("10", BitUtils.longToBits(2, 1));
        Assert.assertEquals("0010", BitUtils.longToBits(2, 4));
        Assert.assertEquals("00101010", BitUtils.longToBits(42, 8));
    }

    @Test
    public void testBoolToBits() {
        Assert.assertEquals("0", BitUtils.boolToBits(false, 1));
        Assert.assertEquals("1", BitUtils.boolToBits(true, 1));
        Assert.assertEquals("00", BitUtils.boolToBits(false, 2));
        Assert.assertEquals("001", BitUtils.boolToBits(true, 3));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testDateToBits() {
        Assert.assertEquals("1110000100000101000001100010111001", BitUtils.dateToBits(DateUtils.dateFromString("2017-11-07T18:59:04.900Z"), 1));
        Assert.assertEquals("0000001110000100000101000001100010111001", BitUtils.dateToBits(DateUtils.dateFromString("2017-11-07T18:59:04.900Z"), 40));
    }

    @Test
    public void testLetterToBits() {
        Assert.assertEquals("0", BitUtils.letterToBits("a", 1));
        Assert.assertEquals("1010", BitUtils.letterToBits("K", 1));
        Assert.assertEquals("11001", BitUtils.letterToBits("z", 1));
        Assert.assertEquals("000000", BitUtils.letterToBits("a", 6));
        Assert.assertEquals("001010", BitUtils.letterToBits("K", 6));
        Assert.assertEquals("011001", BitUtils.letterToBits("z", 6));
    }

    @Test
    public void testLanguageToBits() throws IllegalArgumentException {
        Assert.assertEquals("000100001101", BitUtils.languageToBits(new Language("en"), 12));
        Assert.assertEquals("000100001101", BitUtils.languageToBits(new Language("EN"), 12));
        Assert.assertEquals("000101010001", BitUtils.languageToBits(new Language("fr"), 12));
        Assert.assertEquals("000101010001", BitUtils.languageToBits(new Language("FR"), 12));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testBitsToLong() {
        Assert.assertEquals(BitUtils.bitsToLong("0").longValue(), 0);
        Assert.assertEquals(BitUtils.bitsToLong("1").longValue(), 1);
        Assert.assertEquals(BitUtils.bitsToLong("10").longValue(), 2);
        Assert.assertEquals(BitUtils.bitsToLong("00101010").longValue(), 42);
        Assert.assertNull(BitUtils.bitsToLong("001010a10"));
    }

    @Test
    public void testBitsToBool() {
        Boolean bool1 = BitUtils.bitsToBool("0");
        Assert.assertNotNull(bool1);
        Assert.assertFalse(bool1);

        Boolean bool2 = BitUtils.bitsToBool("1");
        Assert.assertNotNull(bool2);
        Assert.assertTrue(bool2);

        Assert.assertNull(BitUtils.bitsToBool("a"));
        Assert.assertNull(BitUtils.bitsToBool("00"));
        Assert.assertNull(BitUtils.bitsToBool("01"));
    }

    @Test
    public void testBitsToDate() {
        Assert.assertEquals(DateUtils.dateFromString("2017-11-07T18:59:04.9Z"), BitUtils.bitsToDate("1110000100000101000001100010111001"));
        Assert.assertEquals(DateUtils.dateFromString("2017-11-07T18:59:04.9Z"), BitUtils.bitsToDate("0000001110000100000101000001100010111001"));
        Assert.assertNull(BitUtils.bitsToDate("111a0000100000101000001100010111001"));
    }

    @Test
    public void testBitsToLetter() {
        Assert.assertEquals(BitUtils.bitsToLetter("0"), "a");
        Assert.assertEquals(BitUtils.bitsToLetter("1010"), "k");
        Assert.assertEquals(BitUtils.bitsToLetter("11001"), "z");
        Assert.assertEquals(BitUtils.bitsToLetter("000000"), "a");
        Assert.assertEquals(BitUtils.bitsToLetter("001010"), "k");
        Assert.assertEquals(BitUtils.bitsToLetter("011001"), "z");
        Assert.assertNull(BitUtils.bitsToLetter(""));
        Assert.assertNull(BitUtils.bitsToLetter("a"));
        Assert.assertNull(BitUtils.bitsToLetter("011010"));
        Assert.assertNull(BitUtils.bitsToLetter("111111"));
    }

    @Test
    public void testBitsToLanguage() throws IllegalArgumentException {
        Assert.assertEquals(BitUtils.bitsToLanguage("000100001101"), new Language("en"));
        Assert.assertEquals(BitUtils.bitsToLanguage("000101010001"), new Language("fr"));
        Assert.assertNull(BitUtils.bitsToLanguage("0001010100a1"));
    }
}
