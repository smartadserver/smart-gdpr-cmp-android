package com.smartadserver.android.smartcmp.model;


import junit.framework.Assert;

import org.junit.Test;

public class PurposeTest {

    @Test
    public void testPurposeIsEquatable() {
        Purpose purpose1 = new Purpose(1, "name1", "description1");
        Purpose purpose2 = new Purpose(1, "name1", "description1");
        Purpose purpose3 = new Purpose(3, "name3", "description3");

        Assert.assertEquals(purpose1, purpose2);
        Assert.assertNotSame(purpose1, purpose3);
    }
}
