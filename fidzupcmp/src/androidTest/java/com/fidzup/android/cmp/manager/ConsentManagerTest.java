package com.fidzup.android.cmp.manager;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;

import com.fidzup.android.cmp.Constants;
import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.exception.UnknownVersionNumberException;
import com.fidzup.android.cmp.model.Language;
import com.fidzup.android.cmp.model.VersionConfig;
import com.fidzup.android.cmp.util.DateUtils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ConsentManagerTest {

    @Before
    public void setUp() {
        ConsentManager.getSharedInstance().setContext(InstrumentationRegistry.getContext());
        cleanSharedPreferences();
    }

    @After
    public void cleanUp() {
        cleanSharedPreferences();
    }

    private void cleanSharedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getContext());
        SharedPreferences.Editor editor = prefs.edit();
        String[] keys = {Constants.IABConsentKeys.ConsentString, Constants.IABConsentKeys.ParsedPurposeConsent, Constants.IABConsentKeys.ParsedVendorConsent, Constants.IABConsentKeys.SubjectToGDPR, Constants.AdvertisingConsentStatus.Key};
        for (String key : keys) {
            editor.remove(key);
        }

        editor.apply();
    }

    private String getStringForSharedPreferences(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getContext());
        return prefs.getString(key, null);
    }

    @Test
    public void testSaveConsentString() {
        // First value is null
        String initialValue = getStringForSharedPreferences(Constants.IABConsentKeys.ConsentString);
        Assert.assertNull(initialValue);

        // Save string to SharedPreferences
        String newValue = "BOEFBi5OEFBi5ABACDENABwAAAAAZoA";
        ConsentManager.getSharedInstance().setConsentString(newValue);

        String readValue = getStringForSharedPreferences(Constants.IABConsentKeys.ConsentString);
        Assert.assertEquals(newValue, readValue);
    }

    @Test
    public void testInvalidConsentStringIsNotSaved() {
        // First value is null
        String initialValue = getStringForSharedPreferences(Constants.IABConsentKeys.ConsentString);
        Assert.assertNull(initialValue);

        // try to save invalid consent string
        String newValue = "invalidConsentString";
        ConsentManager.getSharedInstance().setConsentString(newValue);

        String readValue = getStringForSharedPreferences(Constants.IABConsentKeys.ConsentString);
        Assert.assertNull(readValue);
    }

    @Test
    public void testSaveGDPRStatus() {
        // First value is null
        String initialValue = getStringForSharedPreferences(Constants.IABConsentKeys.SubjectToGDPR);
        Assert.assertNull(initialValue);

        // Set new value
        ConsentManager.getSharedInstance().setSubjectToGDPR(true);

        // Get value from sharedPreferences
        String readValue = getStringForSharedPreferences(Constants.IABConsentKeys.SubjectToGDPR);
        Assert.assertEquals("1", readValue);
    }

    @Test
    public void testSavePurpose() throws UnknownVersionNumberException {
        // First value is null
        String initialValue = getStringForSharedPreferences(Constants.IABConsentKeys.ParsedPurposeConsent);
        Assert.assertNull(initialValue);

        // Set new value
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

        ConsentManager.getSharedInstance().setConsentString(consentString);

        // Get value from sharedPreferences
        String readValue = getStringForSharedPreferences(Constants.IABConsentKeys.ParsedPurposeConsent);
        Assert.assertEquals("110000000000000000000000", readValue);
    }

    @Test
    public void testSaveVendor() throws UnknownVersionNumberException {
        // First value is null
        String initialValue = getStringForSharedPreferences(Constants.IABConsentKeys.ParsedVendorConsent);
        Assert.assertNull(initialValue);

        // Set new value
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

        ConsentManager.getSharedInstance().setConsentString(consentString);

        // Get value from sharedPreferences
        String readValue = getStringForSharedPreferences(Constants.IABConsentKeys.ParsedVendorConsent);
        Assert.assertEquals("110100", readValue);
    }

    @Test
    public void testSaveAdvertisingConsentStatus() throws UnknownVersionNumberException {
        // First value is null
        String initialValue = getStringForSharedPreferences(Constants.IABConsentKeys.ParsedVendorConsent);
        Assert.assertNull(initialValue);

        // Save a consent string that do not consent to advertising purpose
        Date date = DateUtils.dateFromString("2017-11-07T18:59:04.9Z");

        if (date == null) {
            Assert.fail("Date is null");
        }

        ConsentString consentString1 = new ConsentString(new VersionConfig(1),
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

        ConsentManager.getSharedInstance().setConsentString(consentString1);

        // Check saved value
        String readValue1 = getStringForSharedPreferences(Constants.AdvertisingConsentStatus.Key);
        Assert.assertEquals("0", readValue1);

        // Set a consent string that consent to advertising purpose.
        ConsentString consentString2 = ConsentString.consentStringByAddingPurposeConsent(3, consentString1);
        ConsentManager.getSharedInstance().setConsentString(consentString2);

        // Check saved value
        String readValue2 = getStringForSharedPreferences(Constants.AdvertisingConsentStatus.Key);
        Assert.assertEquals("1", readValue2);
    }

}
