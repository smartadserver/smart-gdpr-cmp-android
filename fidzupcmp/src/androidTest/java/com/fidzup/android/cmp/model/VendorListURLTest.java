package com.fidzup.android.cmp.model;


import junit.framework.Assert;

import org.junit.Test;

public class VendorListURLTest {

    @Test
    public void testDefaultVendorListURLCorrespondToTheLatest() {
        String expectedURL = "https://vendorlist.consensu.org/vendorlist.json";

        Assert.assertEquals(expectedURL, new VendorListURL(null).getURL());
        Assert.assertNull(new VendorListURL(null).getLocalizedURL());
    }

    @Test
    public void testVendorListURLCanCorrespondToSpecificVersion() {
        String expectedURL = "https://vendorlist.consensu.org/v-42/vendorlist.json";

        Assert.assertEquals(expectedURL, new VendorListURL(42, null).getURL());
        Assert.assertNull(new VendorListURL(42, null).getLocalizedURL());
    }

    @Test
    public void testDefaultVendorListSupportLocalization() {
        String expectedURL = "https://vendorlist.consensu.org/vendorlist.json";
        String expectedLocalizedURL = "https://vendorlist.consensu.org/purposes-fr.json";

        Assert.assertEquals(expectedURL, new VendorListURL(new Language("fr")).getURL());
        Assert.assertEquals(expectedLocalizedURL, new VendorListURL(new Language("fr")).getLocalizedURL());
    }

    @Test
    public void testVersionSpecificVendorListSupportLocalization() {
        String expectedURL = "https://vendorlist.consensu.org/v-42/vendorlist.json";
        String expectedLocalizedURL = "https://vendorlist.consensu.org/purposes-fr-42.json";

        Assert.assertEquals(expectedURL, new VendorListURL(42, new Language("fr")).getURL());
        Assert.assertEquals(expectedLocalizedURL, new VendorListURL(42, new Language("fr")).getLocalizedURL());
    }
}
