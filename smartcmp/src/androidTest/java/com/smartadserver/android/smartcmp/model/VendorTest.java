package com.smartadserver.android.smartcmp.model;


import junit.framework.Assert;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class VendorTest {

    @Test
    public void testVendorIsEquatable() throws MalformedURLException {
        Vendor vendor1 = new Vendor(1, "name1", new ArrayList<>(Collections.singletonList(1)), new ArrayList<>(Collections.singletonList(2)), new ArrayList<>(Collections.singletonList(3)), new URL("http://url1"), null);
        Vendor vendor2 = new Vendor(1, "name1", new ArrayList<>(Collections.singletonList(1)), new ArrayList<>(Collections.singletonList(2)), new ArrayList<>(Collections.singletonList(3)), new URL("http://url1"), null);
        Vendor vendor3 = new Vendor(3, "name3", new ArrayList<>(Collections.singletonList(3)), new ArrayList<>(Collections.singletonList(4)), new ArrayList<>(Collections.singletonList(5)), new URL("http://url3"), null);
        Vendor vendor4 = new Vendor(1, "name1", new ArrayList<>(Collections.singletonList(1)), new ArrayList<>(Collections.singletonList(2)), new ArrayList<>(Collections.singletonList(3)), new URL("http://url1"), new Date());

        Assert.assertEquals(vendor1, vendor2);
        Assert.assertNotSame(vendor1, vendor3);
        Assert.assertNotSame(vendor1, vendor4);
    }
}
