package com.smartadserver.android.smartcmp.consentstring;


import android.support.test.InstrumentationRegistry;

import com.smartadserver.android.smartcmp.Constants;
import com.smartadserver.android.smartcmp.exception.UnknownVersionNumberException;
import com.smartadserver.android.smartcmp.model.Language;
import com.smartadserver.android.smartcmp.model.VendorList;
import com.smartadserver.android.smartcmp.model.VersionConfig;
import com.smartadserver.android.smartcmp.util.DateUtils;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class ConsentStringTest {

    private VendorList vendorList;
    private VendorList updatedVendorList;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private JSONObject getJSON(String fileName) {
        JSONObject json = null;

        try {
            InputStream is = InstrumentationRegistry.getContext().getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            json = new JSONObject(new String(buffer, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }

    private VendorList getVendorList() {
        if (vendorList == null) {
            try {
                vendorList = new VendorList(getJSON("vendors.json"));
            } catch (Exception e) {
                e.printStackTrace();
                // Should never happen
            }
        }
        return vendorList;
    }

    private VendorList getUpdatedVendorList() {
        if (updatedVendorList == null) {
            try {
                updatedVendorList = new VendorList(getJSON("vendors_updated.json"));
            } catch (Exception e) {
                e.printStackTrace();
                // Should never happen
            }
        }
        return updatedVendorList;
    }

    @Test
    public void testConsentStringEquatable() throws IllegalArgumentException, UnknownVersionNumberException{
        ConsentString consentString1 = new ConsentString(new VersionConfig(1),
                new Date(111111111),
                new Date(222222222),
                4,
                5,
                6,
                new Language("en"),
                8,
                9,
                new ArrayList<>(Collections.singletonList(1)),
                new ArrayList<>(Collections.singletonList(2)));

        ConsentString consentString2 = new ConsentString(new VersionConfig(1),
                new Date(111111111),
                new Date(222222222),
                4,
                5,
                6,
                new Language("en"),
                8,
                9,
                new ArrayList<>(Collections.singletonList(1)),
                new ArrayList<>(Collections.singletonList(2)));

        ConsentString consentString3 = new ConsentString(new VersionConfig(1),
                new Date(222222222),
                new Date(333333333),
                4,
                5,
                6,
                new Language("fr"),
                9,
                10,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(2, 5)));

        Assert.assertEquals(consentString1, consentString2);
        Assert.assertNotSame(consentString1, consentString3);
    }

    @Test
    public void testConsentStringEncodingWithBitfield() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)),
                ConsentString.ConsentEncoding.BITFIELD);

        Assert.assertEquals("BOEFBi5OEFBi5ABACDENABwAAAAAZoA", consentString.getConsentString());
    }

    @Test
    public void testConsentStringEncodedFromVersion() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(1,
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        Assert.assertEquals("BOEFBi5OEFBi5ABACDENABwAAAAAZoA", consentString.getConsentString());
    }

    @Test
    public void testConsentStringEncodedFromVendorList() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        Assert.assertEquals("BOEFBi5OEFBi5ABACDENAGwAAAACdoAAAAAA", consentString.getConsentString());
    }

    @Test
    public void testConsentStringCreationFailsForInvalidVersions() throws IllegalArgumentException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        try {
            new ConsentString(99999,
                    date,
                    date,
                    1,
                    2,
                    3,
                    new Language("en"),
                    1,
                    6,
                    new ArrayList<>(Arrays.asList(1, 2)),
                    new ArrayList<>(Arrays.asList(1, 2, 4)));

            Assert.fail("ConsentString was created.");
        } catch (UnknownVersionNumberException e) {
            // ok
        }
    }

    @Test
    public void testConsentStringEncodingWithRange() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)),
                ConsentString.ConsentEncoding.RANGE);

        Assert.assertEquals("BOEFBi5OEFBi5ABACDENABwAAAAAaACgACAAQABA", consentString.getConsentString());
    }

    @Test
    public void testEmptyConsentStringEncodingWithBitfieldAndDecoded() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)),
                ConsentString.ConsentEncoding.BITFIELD);

        ConsentString resultConsentString = ConsentString.fromBase64String(consentString.getConsentString());

        Assert.assertNotNull(resultConsentString);
        Assert.assertEquals(consentString, resultConsentString);
    }

    @Test
    public void testEmptyConsentStringEncodingWithRangeAndDecoded() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)),
                ConsentString.ConsentEncoding.RANGE);

        ConsentString resultConsentString = ConsentString.fromBase64String(consentString.getConsentString());

        Assert.assertNotNull(resultConsentString);
        Assert.assertEquals(consentString, resultConsentString);
    }

    @Test
    public void testConsentStringEncodingWithBitfieldAndUnorderedArrays() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(2, 1)),
                new ArrayList<>(Arrays.asList(4, 2, 1)),
                ConsentString.ConsentEncoding.BITFIELD);

        Assert.assertEquals("BOEFBi5OEFBi5ABACDENABwAAAAAZoA", consentString.getConsentString());
    }

    @Test
    public void testConsentStringEncodingWithRangeAndUnorderedArrays() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(4, 2, 1)),
                ConsentString.ConsentEncoding.RANGE);

        Assert.assertEquals("BOEFBi5OEFBi5ABACDENABwAAAAAaACgACAAQABA", consentString.getConsentString());
    }

    @Test
    public void testConsentStringEncodingAutomatic() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)),
                ConsentString.ConsentEncoding.AUTOMATIC);

        // Bitfield encoding will be chosen as it leads to a shorter consent string.
        Assert.assertEquals("BOEFBi5OEFBi5ABACDENABwAAAAAZoA", consentString.getConsentString());
    }

    @Test
    public void testConsentStringDecodingFromBitfield() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString expected = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString consentString = ConsentString.fromBase64String("BOEFBi5OEFBi5ABACDENABwAAAAAZoA");

        Assert.assertNotNull(consentString);
        Assert.assertEquals(expected, consentString);
    }

    @Test
    public void testConsentStringDecodingFromRange() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString expected = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString consentString = ConsentString.fromBase64String("BOEFBi5OEFBi5ABACDENABwAAAAAaACgACAAQABA");

        Assert.assertNotNull(consentString);
        Assert.assertEquals(expected, consentString);
    }

    @Test
    public void testInvalidConsentStringCantBeDecoded() {
        try {
            ConsentString.fromBase64String("BOEFBi5OEFBi5ABACDENABwAAAAAaACgADDAAQABA");
            Assert.fail("Should have raised an exception.");
        } catch (Exception e) {
            // ok
        }

        try {
            ConsentString.fromBase64String("BOEFBi5OEFBi5ABACDENABwABAAZoA");
            Assert.fail("Should have raised an exception.");
        } catch (Exception e) {
            // ok
        }
    }

    @Test
    public void testPurposePermissionCanBeChecked() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        Assert.assertTrue(consentString.isPurposeAllowed(1));
        Assert.assertTrue(consentString.isPurposeAllowed(2));

        Assert.assertFalse(consentString.isPurposeAllowed(3));
        Assert.assertFalse(consentString.isPurposeAllowed(4));
    }

    @Test
    public void testVendorPermissionCanBeChecked() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                    date,
                    date,
                    1,
                    2,
                    3,
                    new Language("en"),
                    1,
                    6,
                    new ArrayList<>(Arrays.asList(1, 2)),
                    new ArrayList<>(Arrays.asList(1, 2, 4)));

            Assert.assertTrue(consentString.isVendorAllowed(1));
            Assert.assertTrue(consentString.isVendorAllowed(2));
            Assert.assertTrue(consentString.isVendorAllowed(4));

            Assert.assertFalse(consentString.isVendorAllowed(3));
            Assert.assertFalse(consentString.isVendorAllowed(5));
            Assert.assertFalse(consentString.isVendorAllowed(6));
    }

    @Test
    public void testParsedPurposeConsents() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        Assert.assertEquals("110000000000000000000000", consentString.parsedPurposeConsents());
    }

    @Test
    public void testParsedVendorConsents() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        Assert.assertEquals("110100", consentString.parsedVendorConsents());
    }

    @Test
    public void testConsentStringCanBeCopied() throws IllegalArgumentException, UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                1,
                6,
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString consentStringCopy = new ConsentString(consentString);

        Assert.assertEquals(consentString, consentStringCopy);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testConsentStringWithNoConsent() {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString noConsentString = ConsentString.consentStringWithNoConsent(3, new Language("en"), getVendorList(), date);

        ConsentString expectedConsentString = new ConsentString(VersionConfig.getLatest(),
                date,
                date,
                Constants.CMPInfos.ID,
                Constants.CMPInfos.VERSION,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<Integer>(),
                new ArrayList<Integer>());

        Assert.assertEquals(expectedConsentString, noConsentString);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testConsentStringWithFullConsent() {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        ConsentString fullConsentString = ConsentString.consentStringWithFullConsent(3, new Language("en"), getVendorList(), date);

        ConsentString expectedConsentString = new ConsentString(VersionConfig.getLatest(),
                date,
                date,
                Constants.CMPInfos.ID,
                Constants.CMPInfos.VERSION,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5)),
                new ArrayList<>(Arrays.asList(8, 12, 28, 9, 27, 25, 26, 1, 6, 30, 24, 29, 39, 11, 15, 4, 7)));

        Assert.assertEquals(expectedConsentString, fullConsentString);
    }

    @Test
    public void testConsentStringFromUpdatedVendorList() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");
        Date updatedDate = DateUtils.dateFromString("2018-11-07T18:59:04.9Z");

        if (date == null || updatedDate == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString expectedConsentString = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getUpdatedVendorList(),
                new ArrayList<>(Arrays.asList(1, 2, 6, 7)),
                new ArrayList<>(Arrays.asList(1, 2, 4, 40, 41, 42)));

        ConsentString updatedConsentString = ConsentString.consentStringFromUpdatedVendorList(getUpdatedVendorList(), getVendorList(), consentString, updatedDate);

        Assert.assertEquals(expectedConsentString, updatedConsentString);
    }

    @Test
    public void testConsentStringByAddingPurpose() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");
        Date updatedDate = DateUtils.dateFromString("2018-11-07T18:59:04.9Z");

        if (date == null || updatedDate == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString expectedConsentString1 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2, 4)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString expectedConsentString2 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString idDoesNotExistsConsentString = ConsentString.consentStringByAddingPurposeConsent(4, consentString, updatedDate);
        ConsentString idExistsConsentString = ConsentString.consentStringByAddingPurposeConsent(1, consentString, updatedDate);

        Assert.assertEquals(expectedConsentString1, idDoesNotExistsConsentString);
        Assert.assertEquals(expectedConsentString2, idExistsConsentString);
    }

    @Test
    public void testConsentStringByRemovingPurpose() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");
        Date updatedDate = DateUtils.dateFromString("2018-11-07T18:59:04.9Z");

        if (date == null || updatedDate == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString expectedConsentString1 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Collections.singletonList(2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString expectedConsentString2 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString idDoesNotExistsConsentString = ConsentString.consentStringByRemovingPurposeConsent(4, consentString, updatedDate);
        ConsentString idExistsConsentString = ConsentString.consentStringByRemovingPurposeConsent(1, consentString, updatedDate);

        Assert.assertEquals(expectedConsentString1, idExistsConsentString);
        Assert.assertEquals(expectedConsentString2, idDoesNotExistsConsentString);
    }

    @Test
    public void testConsentStringByAddingVendor() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");
        Date updatedDate = DateUtils.dateFromString("2018-11-07T18:59:04.9Z");

        if (date == null || updatedDate == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString expectedConsentString1 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4, 6)));

        ConsentString expectedConsentString2 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString idDoesNotExistsConsentString = ConsentString.consentStringByAddingVendorConsent(6, consentString, updatedDate);
        ConsentString idExistsConsentString = ConsentString.consentStringByAddingVendorConsent(4, consentString, updatedDate);

        Assert.assertEquals(expectedConsentString1, idDoesNotExistsConsentString);
        Assert.assertEquals(expectedConsentString2, idExistsConsentString);
    }

    @Test
    public void testConsentStringByRemovingVendor() throws UnknownVersionNumberException {
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");
        Date updatedDate = DateUtils.dateFromString("2018-11-07T18:59:04.9Z");

        if (date == null | updatedDate == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString = new ConsentString(new VersionConfig(1),
                date,
                date,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString expectedConsentString1 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2)));

        ConsentString expectedConsentString2 = new ConsentString(new VersionConfig(1),
                date,
                updatedDate,
                1,
                2,
                3,
                new Language("en"),
                getVendorList(),
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>(Arrays.asList(1, 2, 4)));

        ConsentString idDoesNotExistsConsentString = ConsentString.consentStringByRemovingVendorConsent(6, consentString, updatedDate);
        ConsentString idExistsConsentString = ConsentString.consentStringByRemovingVendorConsent(4, consentString, updatedDate);

        Assert.assertEquals(expectedConsentString1, idExistsConsentString);
        Assert.assertEquals(expectedConsentString2, idDoesNotExistsConsentString);
    }
}
