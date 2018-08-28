package com.fidzup.android.cmp.model;


import junit.framework.Assert;

import org.junit.Test;

public class LanguageTest {

    @Test
    public void testLanguageCanBeCreated() {
        try {
            Language language1 = new Language("en");
            Assert.assertEquals("en", language1.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.fail("language1 was not created.");
        }

        try {
            Language language2 = new Language("fr");
            Assert.assertEquals("fr", language2.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.fail("language2 was not created.");
        }

        try {
            Language language3 = new Language("EN");
            Assert.assertEquals("en", language3.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.fail("language3 was not created.");
        }
    }

    @Test
    public void testInvalidLanguageAreRejected() {
        try {
            new Language("eng");
            Assert.fail("language1 was created.");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new Language("e");
            Assert.fail("language2 was created.");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new Language("f+");
            Assert.fail("language3 was created.");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    @Test
    public void testLanguageAreEquatable() {
        try {
            Language language1 = new Language("en");
            Language language2 = new Language("en");
            Language language3 = new Language("fr");
            Language language4 = new Language("EN");

            Assert.assertEquals(language1, language2);
            Assert.assertNotSame(language1, language3);
            Assert.assertEquals(language1, language4);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Assert.fail("One of the languages was not created.");
        }
    }
}
