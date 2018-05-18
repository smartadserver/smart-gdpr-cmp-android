package com.smartadserver.android.smartcmp.model;


import junit.framework.Assert;

import org.junit.Test;

public class FeatureTest {

    @Test
    public void testFeatureIsEquatable() {
        Feature feature1 = new Feature(1, "name1", "description1");
        Feature feature2 = new Feature(1, "name1", "description1");
        Feature feature3 = new Feature(3, "name3", "description3");

        Assert.assertEquals(feature1, feature2);
        Assert.assertNotSame(feature1, feature3);
    }
}
