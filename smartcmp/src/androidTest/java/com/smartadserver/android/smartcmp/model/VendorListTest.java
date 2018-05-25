package com.smartadserver.android.smartcmp.model;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;

import com.smartadserver.android.smartcmp.util.DateUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class VendorListTest {

    private JSONObject vendorsJSON;
    private JSONObject localizedVendorsJSON;
    private JSONObject updatedVendorsJSON;

    private JSONObject getVendorsJSON() {
        if (vendorsJSON == null) {
            vendorsJSON = getJSON("vendors.json");
        }

        return vendorsJSON;
    }

    private JSONObject getLocalizedVendorsJSON() {
        if (localizedVendorsJSON == null) {
            localizedVendorsJSON = getJSON("vendors_localized.json");
        }

        return localizedVendorsJSON;
    }

    private JSONObject getUpdatedVendorsJSON() {
        if (updatedVendorsJSON == null) {
            updatedVendorsJSON = getJSON("vendors_updated.json");
        }

        return updatedVendorsJSON;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private JSONObject getJSON(String fileName) {
        try {
            InputStream is = InstrumentationRegistry.getContext().getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            return new JSONObject(new String(buffer, "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Test
    public void testVendorListCanBeCreatedFromJSON() throws JSONException, MalformedURLException {
        VendorList vendorList = new VendorList(getVendorsJSON());

        Assert.assertEquals(6, vendorList.getVersion());
        Assert.assertEquals(DateUtils.dateFromString("2018-04-23T16:03:22Z"), vendorList.getLastUpdated());

        Assert.assertEquals(5, vendorList.getPurposes().size());
        Assert.assertEquals(3, vendorList.getPurposes().get(2).getId());
        Assert.assertEquals("Ad selection, delivery, reporting", vendorList.getPurposes().get(2).getName());
        Assert.assertEquals("The collection of information, and combination with previously collected information, to select and deliver advertisements for you, and to measure the delivery and effectiveness of such advertisements. This includes using previously collected information about your interests to select ads, processing data about what advertisements were shown, how often they were shown, when and where they were shown, and whether you took any action related to the advertisement, including for example clicking an ad or making a purchase. ", vendorList.getPurposes().get(2).getDescription());

        Assert.assertEquals(3, vendorList.getFeatures().size());
        Assert.assertEquals(2, vendorList.getFeatures().get(1).getId());
        Assert.assertEquals("Linking Devices", vendorList.getFeatures().get(1).getName());
        Assert.assertEquals("Allow processing of a user's data to connect such user across multiple devices.", vendorList.getFeatures().get(1).getDescription());

        Assert.assertEquals(17, vendorList.getVendors().size());
        Assert.assertEquals(27, vendorList.getVendors().get(4).getId());
        Assert.assertEquals("ADventori SAS", vendorList.getVendors().get(4).getName());
        Assert.assertEquals(new URL("https://www.adventori.com/with-us/legal-notice/"), vendorList.getVendors().get(4).getPolicyURL());
        Assert.assertEquals(new ArrayList<Integer>() {{ add(2); }}, vendorList.getVendors().get(4).getPurposes());
        Assert.assertEquals(new ArrayList<Integer>() {{ add(1); add(3); add(4); add(5); }}, vendorList.getVendors().get(4).getLegitimatePurposes());
        Assert.assertEquals(new ArrayList<Integer>(), vendorList.getVendors().get(4).getFeatures());
    }

    @Test
    public void testEmptyJSONIsRejectedByTheParser() throws MalformedURLException {
        try {
            new VendorList(new JSONObject(""));
            Assert.fail("Should have raised an JSONException.");
        } catch (JSONException e) {
            // ok
        }

        try {
            new VendorList(new JSONObject("{}"));
            Assert.fail("Should have raised an JSONException.");
        } catch (JSONException e) {
            // ok
        }
    }

    @Test
    public void testFindingMaxVendorId() throws JSONException, MalformedURLException {
        VendorList vendorList = new VendorList(getVendorsJSON());
        Assert.assertEquals(39, vendorList.getMaxVendorId());
    }

    @Test
    public void testFindingVendorCount() throws JSONException, MalformedURLException {
        VendorList vendorList = new VendorList(getUpdatedVendorsJSON());
        Assert.assertEquals(20, vendorList.getVendors().size());
    }

    @Test
    public void testFindingActivatedVendorCount() throws JSONException, MalformedURLException {
        VendorList vendorList = new VendorList(getUpdatedVendorsJSON());
        Assert.assertEquals(19, vendorList.getActivatedVendor().size());
    }

    @Test
    public void testVendorListIsEquatable() throws JSONException, MalformedURLException {
        VendorList vendorList1 = new VendorList(getVendorsJSON());
        VendorList vendorList2 = new VendorList(getVendorsJSON());
        VendorList vendorList3 = new VendorList(getUpdatedVendorsJSON());

        Assert.assertEquals(vendorList1, vendorList2);
        Assert.assertNotSame(vendorList1, vendorList3);
    }

    @Test
    public void testVendorListCantBeCreatedFromTranslatedListOnly() throws MalformedURLException {
        try {
            new VendorList(getLocalizedVendorsJSON());
            Assert.fail("Should have raised an exception.");
        } catch (JSONException e) {
            // it's ok
        }

    }

    @Test
    public void testVendorListCanBeLocalized() throws JSONException, MalformedURLException {
        VendorList vendorList = new VendorList(getVendorsJSON(), getLocalizedVendorsJSON());

        Assert.assertEquals(6, vendorList.getVersion());
        Assert.assertEquals(DateUtils.dateFromString("2018-04-23T16:03:22Z"), vendorList.getLastUpdated());

        Assert.assertEquals(5, vendorList.getPurposes().size());
        Assert.assertEquals(3, vendorList.getPurposes().get(2).getId());
        Assert.assertEquals("Purpose 3 name translated", vendorList.getPurposes().get(2).getName());
        Assert.assertEquals("Purpose 3 description translated", vendorList.getPurposes().get(2).getDescription());
        Assert.assertEquals(4, vendorList.getPurposes().get(3).getId());
        Assert.assertEquals("Content selection, delivery, reporting", vendorList.getPurposes().get(3).getName());
        Assert.assertEquals("The collection of information, and combination with previously collected information, to select and deliver content for you, and to measure the delivery and effectiveness of such content. This includes using previously collected information about your interests to select content, processing data about what content was shown, how often or how long it was shown, when and where it was shown, and whether the you took any action related to the content, including for example clicking on content. ", vendorList.getPurposes().get(3).getDescription());
        Assert.assertEquals(5, vendorList.getPurposes().get(4).getId());
        Assert.assertEquals("Purpose 5 name translated", vendorList.getPurposes().get(4).getName());
        Assert.assertEquals("Purpose 5 description translated", vendorList.getPurposes().get(4).getDescription());

        Assert.assertEquals(3, vendorList.getFeatures().size());
        Assert.assertEquals(2, vendorList.getFeatures().get(1).getId());
        Assert.assertEquals("Feature 2 name translated", vendorList.getFeatures().get(1).getName());
        Assert.assertEquals("Feature 2 description translated", vendorList.getFeatures().get(1).getDescription());
        Assert.assertEquals(3, vendorList.getFeatures().get(2).getId());
        Assert.assertEquals("Precise Geographic Location Data", vendorList.getFeatures().get(2).getName());
        Assert.assertEquals("Allow processing of a user's precise geographic location data in support of a purpose for which that certain third party has consent.", vendorList.getFeatures().get(2).getDescription());

        Assert.assertEquals(17, vendorList.getVendors().size());
        Assert.assertEquals(27, vendorList.getVendors().get(4).getId());
        Assert.assertEquals("ADventori SAS", vendorList.getVendors().get(4).getName());
        Assert.assertEquals(new URL("https://www.adventori.com/with-us/legal-notice/"), vendorList.getVendors().get(4).getPolicyURL());
        Assert.assertEquals(new ArrayList<Integer>() {{ add(2); }}, vendorList.getVendors().get(4).getPurposes());
        Assert.assertEquals(new ArrayList<Integer>() {{ add(1); add(3); add(4); add(5); }}, vendorList.getVendors().get(4).getLegitimatePurposes());
        Assert.assertEquals(new ArrayList<Integer>(), vendorList.getVendors().get(4).getFeatures());
    }
}
