package com.fidzup.android.cmp.consentstring;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fidzup.android.cmp.Constants;
import com.fidzup.android.cmp.exception.UnknownVersionNumberException;
import com.fidzup.android.cmp.model.Language;
import com.fidzup.android.cmp.model.Purpose;
import com.fidzup.android.cmp.model.Vendor;
import com.fidzup.android.cmp.model.VendorList;
import com.fidzup.android.cmp.model.Editor;
import com.fidzup.android.cmp.model.VersionConfig;
import com.fidzup.android.cmp.util.BitUtils;
import com.fidzup.android.cmp.util.BitsString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Representation of IAB consent string.
 */

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class ConsentString implements Parcelable {

    // The type of encoding of the consent string.
    public enum ConsentEncoding {
        // Bitfield or range encoding, depending on the most efficient solution.
        AUTOMATIC(0),

        // Bitfield encoding.
        BITFIELD(1),

        // Range encoding.
        RANGE(2);

        private int value;

        ConsentEncoding(int value) {
            this.value = value;
        }

        @SuppressWarnings("unused")
        public int getValue() {
            return value;
        }

        // Mapping consentEncoding to consentEncoding id
        private static final Map<Integer, ConsentEncoding> _map = new HashMap<Integer, ConsentEncoding>();
        static
        {
            for (ConsentEncoding consentEncoding : ConsentEncoding.values())
                _map.put(consentEncoding.getValue(), consentEncoding);
        }

        /**
         * Get ConsentEncoding from value
         * @param value Value
         * @return ConsentEncoding
         */
        public static ConsentEncoding from(int value)
        {
            return _map.get(value);
        }
    }

    /**
     * Representation of a range.
     */
    static private class Range {

        public int lowerBound;
        public int higherBound;

        /**
         * Create a range using 2 given int.
         *
         * @param int1 First bound of the range.
         * @param int2 Second bound of the range.
         */
        public Range(int int1, int int2) {
            if (int1 > int2) {
                lowerBound = int2;
                higherBound = int1;
            } else {
                lowerBound = int1;
                higherBound = int2;
            }
        }

        /**
         * Return the number of int containing in the range.
         *
         * @return The number of int containing in the range.
         */
        public int length() {
            return higherBound - lowerBound + 1;
        }
    }

    // The consent string version.
    private int version;

    // The consent string version configuration.
    @SuppressWarnings("NullableProblems")
    @NonNull
    private VersionConfig versionConfig;

    // The date of the first consent string creation.
    @SuppressWarnings("NullableProblems")
    @NonNull
    private Date created;

    // The date of the last consent string update.
    @SuppressWarnings("NullableProblems")
    @NonNull
    private Date lastUpdated;

    // The id of the last Consent Manager Provider that updated the consent string.
    private int cmpId;

    // The version of the Consent Manager Provider.
    private int cmpVersion;

    // The screen number in the CMP where the consent was given.
    private int consentScreen;

    // The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
    @SuppressWarnings("NullableProblems")
    @NonNull
    private Language consentLanguage;

    // The version of the vendor list used in the most recent consent string update.
    private int vendorListVersion;

    // The version of the editor used in the most recent consent string update.
    private int editorVersion;

    // The maximum vendor id id that can be found in the current vendor list.
    private int maxVendorId;

    // An array of allowed purposes id.
    @SuppressWarnings("NullableProblems")
    @NonNull
    private ArrayList<Integer> allowedPurposes;

    // An array of allowed editor purposes id.
    @SuppressWarnings("NullableProblems")
    @NonNull
    private ArrayList<Integer> editorPurposes;

    // An array of allowed vendors id.
    @SuppressWarnings("NullableProblems")
    @NonNull
    private ArrayList<Integer> allowedVendors;

    private ConsentEncoding vendorListEncoding;

    // The Base64 representation of the consent string.
    private String consentString;

    // The Base64 representation of the IAB consent string.
    private String iabConsentString;

    /**
     * Initialize a new instance of ConsentString using a vendor list version, an editor version and a max vendor id.
     *
     * @param version           The consent string version.
     * @param created           The date of the first consent string creation.
     * @param lastUpdated       The date of the last consent string update.
     * @param cmpId             The id of the last Consent Manager Provider that updated the consent string.
     * @param cmpVersion        The version of the Consent Manager Provider.
     * @param consentScreen     The screen number in the CMP where the consent was given.
     * @param consentLanguage   The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
     * @param editorVersion     The version of the editor used in the most recent consent string update.
     * @param vendorListVersion The version of the vendor list used in the most recent consent string update.
     * @param maxVendorId       The maximum vendor id id that can be found in the current vendor list.
     * @param editorPurposes    An array of editor purposes id.
     * @param allowedPurposes   An array of allowed purposes id.
     * @param allowedVendors    An array of allowed vendors id.
     */
    public ConsentString(int version,
                         @NonNull Date created,
                         @NonNull Date lastUpdated,
                         int cmpId,
                         int cmpVersion,
                         int consentScreen,
                         @NonNull Language consentLanguage,
                         int editorVersion,
                         int vendorListVersion,
                         int maxVendorId,
                         @NonNull ArrayList<Integer> editorPurposes,
                         @NonNull ArrayList<Integer> allowedPurposes,
                         @NonNull ArrayList<Integer> allowedVendors) throws UnknownVersionNumberException {

        VersionConfig versionConfig = new VersionConfig(version);
        init(versionConfig,
                created,
                lastUpdated,
                cmpId,
                cmpVersion,
                consentScreen,
                consentLanguage,
                editorVersion,
                vendorListVersion,
                maxVendorId,
                editorPurposes,
                allowedPurposes,
                allowedVendors,
                ConsentEncoding.AUTOMATIC);
    }


    /**
     * Initialize a new instance of ConsentString using a vendor list version and a max vendor id.
     *
     * @param versionConfig     The consent string version configuration.
     * @param created           The date of the first consent string creation.
     * @param lastUpdated       The date of the last consent string update.
     * @param cmpId             The id of the last Consent Manager Provider that updated the consent string.
     * @param cmpVersion        The version of the Consent Manager Provider.
     * @param consentScreen     The screen number in the CMP where the consent was given.
     * @param consentLanguage   The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
     * @param editorVersion     The version of the editor used in the most recent consent string update.
     * @param vendorListVersion The version of the vendor list used in the most recent consent string update.
     * @param maxVendorId       The maximum vendor id id that can be found in the current vendor list.
     * @param editorPurposes    An array of editor purposes id.
     * @param allowedPurposes   An array of allowed purposes id.
     * @param allowedVendors    An array of allowed vendors id.
     */
    public ConsentString(@NonNull VersionConfig versionConfig,
                         @NonNull Date created,
                         @NonNull Date lastUpdated,
                         int cmpId,
                         int cmpVersion,
                         int consentScreen,
                         @NonNull Language consentLanguage,
                         int editorVersion,
                         int vendorListVersion,
                         int maxVendorId,
                         @NonNull ArrayList<Integer> editorPurposes,
                         @NonNull ArrayList<Integer> allowedPurposes,
                         @NonNull ArrayList<Integer> allowedVendors) {

        init(versionConfig,
                created,
                lastUpdated,
                cmpId,
                cmpVersion,
                consentScreen,
                consentLanguage,
                editorVersion,
                vendorListVersion,
                maxVendorId,
                editorPurposes,
                allowedPurposes,
                allowedVendors,
                ConsentEncoding.AUTOMATIC);
    }

    /**
     * Initialize a new instance of ConsentString using a vendor list.
     *
     * @param versionConfig   The consent string version configuration.
     * @param created         The date of the first consent string creation.
     * @param lastUpdated     The date of the last consent string update.
     * @param cmpId           The id of the last Consent Manager Provider that updated the consent string.
     * @param cmpVersion      The version of the Consent Manager Provider.
     * @param consentScreen   The screen number in the CMP where the consent was given.
     * @param consentLanguage The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
     * @param editor          The editor corresponding to the consent string.
     * @param vendorList      The vendor list corresponding to the consent string.
     * @param editorPurposes  An array of editor purposes id.
     * @param allowedPurposes An array of allowed purposes id.
     * @param allowedVendors  An array of allowed vendors id.
     */
    public ConsentString(@NonNull VersionConfig versionConfig,
                         @NonNull Date created,
                         @NonNull Date lastUpdated,
                         int cmpId,
                         int cmpVersion,
                         int consentScreen,
                         @NonNull Language consentLanguage,
                         @NonNull Editor editor,
                         @NonNull VendorList vendorList,
                         @NonNull ArrayList<Integer> editorPurposes,
                         @NonNull ArrayList<Integer> allowedPurposes,
                         @NonNull ArrayList<Integer> allowedVendors) {

        int editorVersion;

        if (editor == null) {
            editorVersion = 0;
        }else {
            editorVersion = editor.getVersion();
        }

        init(versionConfig,
                created,
                lastUpdated,
                cmpId,
                cmpVersion,
                consentScreen,
                consentLanguage,
                editorVersion,
                vendorList.getVersion(),
                vendorList.getMaxVendorId(),
                editorPurposes,
                allowedPurposes,
                allowedVendors,
                ConsentEncoding.AUTOMATIC);
    }

    /**
     * Initialize a new instance of ConsentString by copying the given one.
     *
     * @param consentString The ConsentString to copy.
     */
    public ConsentString(@NonNull ConsentString consentString) {
        this(consentString.versionConfig,
                consentString.created,
                consentString.lastUpdated,
                consentString.cmpId,
                consentString.cmpVersion,
                consentString.consentScreen,
                consentString.consentLanguage,
                consentString.editorVersion,
                consentString.vendorListVersion,
                consentString.maxVendorId,
                consentString.editorPurposes,
                consentString.allowedPurposes,
                consentString.allowedVendors);
    }

    /**
     * Initialize a new instance of ConsentString.
     *
     * @param versionConfig      The consent string version configuration.
     * @param created            The date of the first consent string creation.
     * @param lastUpdated        The date of the last consent string update.
     * @param cmpId              The id of the last Consent Manager Provider that updated the consent string.
     * @param cmpVersion         The version of the Consent Manager Provider.
     * @param consentScreen      The screen number in the CMP where the consent was given.
     * @param consentLanguage    The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
     * @param editorVersion      The version of the editor used in the most recent consent string update.
     * @param vendorListVersion  The version of the vendor list used in the most recent consent string update.
     * @param maxVendorId        The maximum vendor id id that can be found in the current vendor list.
     * @param editorPurposes     An array of editor purposes id.
     * @param allowedPurposes    An array of allowed purposes id.
     * @param allowedVendors     An array of allowed vendors id.
     * @param vendorListEncoding The type of vendors encoding that should be used to generate the base64 consent string.
     */
    ConsentString(@NonNull VersionConfig versionConfig,
                  @NonNull Date created,
                  @NonNull Date lastUpdated,
                  int cmpId,
                  int cmpVersion,
                  int consentScreen,
                  @NonNull Language consentLanguage,
                  int editorVersion,
                  int vendorListVersion,
                  int maxVendorId,
                  @NonNull ArrayList<Integer> editorPurposes,
                  @NonNull ArrayList<Integer> allowedPurposes,
                  @NonNull ArrayList<Integer> allowedVendors,
                  @NonNull ConsentEncoding vendorListEncoding) {

        init(versionConfig,
                created,
                lastUpdated,
                cmpId,
                cmpVersion,
                consentScreen,
                consentLanguage,
                editorVersion,
                vendorListVersion,
                maxVendorId,
                editorPurposes,
                allowedPurposes,
                allowedVendors,
                vendorListEncoding);

    }

    /**
     * Initialize a new instance of ConsentString.
     *
     * @param versionConfig      The consent string version configuration.
     * @param created            The date of the first consent string creation.
     * @param lastUpdated        The date of the last consent string update.
     * @param cmpId              The id of the last Consent Manager Provider that updated the consent string.
     * @param cmpVersion         The version of the Consent Manager Provider.
     * @param consentScreen      The screen number in the CMP where the consent was given.
     * @param consentLanguage    The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
     * @param vendorListVersion  The version of the vendor list used in the most recent consent string update.
     * @param maxVendorId        The maximum vendor id id that can be found in the current vendor list.
     * @param editorPurposes     An array of editor purposes id.
     * @param allowedPurposes    An array of allowed purposes id.
     * @param allowedVendors     An array of allowed vendors id.
     * @param vendorListEncoding The type of vendors encoding that should be used to generate the base64 consent string.
     */
    private void init(@NonNull VersionConfig versionConfig,
                      @NonNull Date created,
                      @NonNull Date lastUpdated,
                      int cmpId,
                      int cmpVersion,
                      int consentScreen,
                      @NonNull Language consentLanguage,
                      int editorVersion,
                      int vendorListVersion,
                      int maxVendorId,
                      @NonNull ArrayList<Integer> editorPurposes,
                      @NonNull ArrayList<Integer> allowedPurposes,
                      @NonNull ArrayList<Integer> allowedVendors,
                      @NonNull ConsentEncoding vendorListEncoding) {

        this.version = versionConfig.getVersion();
        this.versionConfig = versionConfig;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.cmpId = cmpId;
        this.cmpVersion = cmpVersion;
        this.consentScreen = consentScreen;
        this.consentLanguage = consentLanguage;
        this.editorVersion = editorVersion;
        this.vendorListVersion = vendorListVersion;
        this.maxVendorId = maxVendorId;
        this.editorPurposes = editorPurposes;
        this.allowedPurposes = allowedPurposes;
        this.allowedVendors = allowedVendors;
        this.vendorListEncoding = vendorListEncoding;

        BitsString tmp = new BitsString(true, encodeToBits(versionConfig,
                created,
                lastUpdated,
                cmpId,
                cmpVersion,
                consentScreen,
                consentLanguage,
                editorVersion,
                vendorListVersion,
                maxVendorId,
                editorPurposes,
                allowedPurposes,
                allowedVendors,
                vendorListEncoding));
        this.consentString = tmp.stringValue;
        tmp = new BitsString(true, iabEncodeToBits(versionConfig,
                created,
                lastUpdated,
                cmpId,
                cmpVersion,
                consentScreen,
                consentLanguage,
                vendorListVersion,
                maxVendorId,
                allowedPurposes,
                allowedVendors,
                vendorListEncoding));
        this.iabConsentString = tmp.stringValue;
    }

    /**
     * Check if a purpose is allowed by the consent string.
     *
     * @param purposeId The purpose id which should be checked.
     * @return true if the purpose is allowed, false otherwise.
     */
    public boolean isPurposeAllowed(int purposeId) {
        return allowedPurposes.contains(purposeId);
    }

    /**
     * Check if an editor purpose is allowed by the consent string.
     *
     * @param purposeId The purpose id which should be checked.
     * @return true if the editor purpose is allowed, false otherwise.
     */
    public boolean isEditorPurposeAllowed(int purposeId) {
        return editorPurposes.contains(purposeId);
    }

    /**
     * Check if a vendor is allowed by the consent string.
     *
     * @param vendorId The vendor id which should be checked.
     * @return true if the vendor is allowed, false otherwise.
     */
    public boolean isVendorAllowed(int vendorId) {
        return allowedVendors.contains(vendorId);
    }

    /**
     * Returns 'parsed purpose consents' string that can be stored in the IABConsent_ParsedPurposeConsents key.
     *
     * @return The 'parsed purpose consents' string that can be stored in the IABConsent_ParsedPurposeConsents key.
     */
    public String parsedPurposeConsents() {
        String consents = "";

        for (int i = 1; i <= versionConfig.getAllowedPurposesBitSize(); i++) {
            consents = consents.concat(allowedPurposes.contains(i) ? "1" : "0");
        }

        return consents;
    }

    /**
     * Returns The 'parsed vendor consents' string that can be stored in the IABConsent_ParsedVendorConsents key.
     *
     * @return The 'parsed vendor consents' string that can be stored in the IABConsent_ParsedVendorConsents key.
     */
    public String parsedVendorConsents() {
        String consents = "";

        for (int i = 1; i <= maxVendorId; i++) {
            consents = consents.concat(allowedVendors.contains(i) ? "1" : "0");
        }

        return consents;
    }

    /**
     * Returns 'parsed editor purpose consents' string that can be stored in the EditorConsent_ParsedEditorPurposeConsents key.
     *
     * @return The 'parsed editor purpose consents' string that can be stored in the EditorConsent_ParsedEditorPurposeConsents key.
     */
    public String parsedEditorPurposeConsents() {
        String consents = "";

        for (int i = 1; i <= versionConfig.getEditorPurposesBitSize(); i++) {
            consents = consents.concat(editorPurposes.contains(i) ? "1" : "0");
        }

        return consents;
    }

    /**
     * Returns a new instance of ConsentString from a base64 string.
     *
     * @param base64String The base64 consent string.
     * @return a new instance of ConsentString if the string is valid.
     * @throws IllegalArgumentException      if the string is not valid.
     * @throws UnknownVersionNumberException if the ConsentString version number is not valid.
     */
    static public ConsentString fromBase64String(@NonNull String base64String) throws UnknownVersionNumberException, IllegalArgumentException {
        String bits = new BitsString(false, base64String).bitsValue;
        return ConsentString.decodeFromBits(bits);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsentString that = (ConsentString) o;

        if (version != that.version) return false;
        if (cmpId != that.cmpId) return false;
        if (cmpVersion != that.cmpVersion) return false;
        if (consentScreen != that.consentScreen) return false;
        if (editorVersion != that.editorVersion) return false;
        if (vendorListVersion != that.vendorListVersion) return false;
        if (maxVendorId != that.maxVendorId) return false;
        if (!created.equals(that.created)) return false;
        if (!lastUpdated.equals(that.lastUpdated)) return false;
        if (!consentLanguage.equals(that.consentLanguage)) return false;
        if (!editorPurposes.equals(that.editorPurposes)) return false;
        if (!allowedPurposes.equals(that.allowedPurposes)) return false;
        return allowedVendors.equals(that.allowedVendors);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{version, created, lastUpdated, cmpId, cmpVersion, consentScreen, consentLanguage, editorVersion,
                vendorListVersion, maxVendorId, editorPurposes, allowedPurposes, allowedVendors});
    }


    //////////////////
    //// Encoding ////
    //////////////////

    /**
     * Return a base64 consent string corresponding to the ConsentString instance.
     *
     * @param versionConfig      The consent string version configuration.
     * @param created            The date of the first consent string creation.
     * @param lastUpdated        The date of the last consent string update.
     * @param cmpId              The id of the last Consent Manager Provider that updated the consent string.
     * @param cmpVersion         The version of the Consent Manager Provider.
     * @param consentScreen      The screen number in the CMP where the consent was given.
     * @param consentLanguage    The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
     * @param editorVersion      The version of the editor used in the most recent consent string update.
     * @param vendorListVersion  The version of the vendor list used in the most recent consent string update.
     * @param maxVendorId        The maximum vendor id id that can be found in the current vendor list.
     * @param editorPurposes     An array of editor purposes id.
     * @param allowedPurposes    An array of allowed purposes id.
     * @param allowedVendors     An array of allowed vendors id.
     * @param vendorListEncoding The type of vendors encoding that should be used to generate the base64 consent string.
     * @return A base64 consent string corresponding to the ConsentString instance.
     * @throws IllegalArgumentException When one of the arguments can't be encoded to bits string.
     */
    static private String encodeToBits(@NonNull VersionConfig versionConfig,
                                       @NonNull Date created,
                                       @NonNull Date lastUpdated,
                                       int cmpId,
                                       int cmpVersion,
                                       int consentScreen,
                                       @NonNull Language consentLanguage,
                                       int editorVersion,
                                       int vendorListVersion,
                                       int maxVendorId,
                                       @NonNull ArrayList<Integer> editorPurposes,
                                       @NonNull ArrayList<Integer> allowedPurposes,
                                       @NonNull ArrayList<Integer> allowedVendors,
                                       ConsentEncoding vendorListEncoding) throws IllegalArgumentException {

        ArrayList<String> bitsArray = new ArrayList<>();
        bitsArray.add(BitUtils.longToBits(versionConfig.getVersion(), VersionConfig.getVersionBitSize()));
        bitsArray.add(BitUtils.dateToBits(created, versionConfig.getCreatedBitSize()));
        bitsArray.add(BitUtils.dateToBits(lastUpdated, versionConfig.getLastUpdatedBitSize()));
        bitsArray.add(BitUtils.longToBits(cmpId, versionConfig.getCmpIdBitSize()));
        bitsArray.add(BitUtils.longToBits(cmpVersion, versionConfig.getCmpVersionBitSize()));
        bitsArray.add(BitUtils.longToBits(consentScreen, versionConfig.getConsentScreenBitSize()));
        bitsArray.add(BitUtils.languageToBits(consentLanguage, versionConfig.getConsentLanguageBitSize()));
        bitsArray.add(BitUtils.longToBits(editorVersion, versionConfig.getEditorVersionBitSize()));
        bitsArray.add(BitUtils.longToBits(vendorListVersion, versionConfig.getVendorListVersionBitSize()));
        bitsArray.add(purposesEditorBitField(versionConfig, editorPurposes));
        bitsArray.add(purposesBitField(versionConfig, allowedPurposes));

        String bits = "";
        for (String str : bitsArray) {
            if (str == null) {
                throw new IllegalArgumentException("One of the arguments is illegal. Not possible to convert it to bits string.");
            }

            bits = bits.concat(str);
        }

        switch (vendorListEncoding) {
            case BITFIELD:
                bits = bits.concat(vendorListBitfield(versionConfig, maxVendorId, allowedVendors));
                break;

            case RANGE:
                bits = bits.concat(vendorListRange(versionConfig, maxVendorId, allowedVendors, false));
                break;

            case AUTOMATIC:
                String bitfield = vendorListBitfield(versionConfig, maxVendorId, allowedVendors);
                String range = vendorListRange(versionConfig, maxVendorId, allowedVendors, false);
                bits = bits.concat(bitfield.length() < range.length() ? bitfield : range); // automatic select the most efficient encoding.
                break;
        }

        return bits;
    }

    /**
     * Return a base64 consent string corresponding to the iabConsentString.
     *
     * @param versionConfig      The consent string version configuration.
     * @param created            The date of the first consent string creation.
     * @param lastUpdated        The date of the last consent string update.
     * @param cmpId              The id of the last Consent Manager Provider that updated the consent string.
     * @param cmpVersion         The version of the Consent Manager Provider.
     * @param consentScreen      The screen number in the CMP where the consent was given.
     * @param consentLanguage    The language that the CMP asked for consent in (in two-letters ISO 639-1 format).
     * @param vendorListVersion  The version of the vendor list used in the most recent consent string update.
     * @param maxVendorId        The maximum vendor id id that can be found in the current vendor list.
     * @param allowedPurposes    An array of allowed purposes id.
     * @param allowedVendors     An array of allowed vendors id.
     * @param vendorListEncoding The type of vendors encoding that should be used to generate the base64 consent string.
     * @return A base64 consent string corresponding to the ConsentString instance.
     * @throws IllegalArgumentException When one of the arguments can't be encoded to bits string.
     */
    static private String iabEncodeToBits(@NonNull VersionConfig versionConfig,
                                       @NonNull Date created,
                                       @NonNull Date lastUpdated,
                                       int cmpId,
                                       int cmpVersion,
                                       int consentScreen,
                                       @NonNull Language consentLanguage,
                                       int vendorListVersion,
                                       int maxVendorId,
                                       @NonNull ArrayList<Integer> allowedPurposes,
                                       @NonNull ArrayList<Integer> allowedVendors,
                                       ConsentEncoding vendorListEncoding) throws IllegalArgumentException {

        ArrayList<String> bitsArray = new ArrayList<>();
        bitsArray.add(BitUtils.longToBits(versionConfig.getVersion(), VersionConfig.getVersionBitSize()));
        bitsArray.add(BitUtils.dateToBits(created, versionConfig.getCreatedBitSize()));
        bitsArray.add(BitUtils.dateToBits(lastUpdated, versionConfig.getLastUpdatedBitSize()));
        bitsArray.add(BitUtils.longToBits(cmpId, versionConfig.getCmpIdBitSize()));
        bitsArray.add(BitUtils.longToBits(cmpVersion, versionConfig.getCmpVersionBitSize()));
        bitsArray.add(BitUtils.longToBits(consentScreen, versionConfig.getConsentScreenBitSize()));
        bitsArray.add(BitUtils.languageToBits(consentLanguage, versionConfig.getConsentLanguageBitSize()));
        bitsArray.add(BitUtils.longToBits(vendorListVersion, versionConfig.getVendorListVersionBitSize()));
        bitsArray.add(purposesBitField(versionConfig, allowedPurposes));

        String bits = "";
        for (String str : bitsArray) {
            if (str == null) {
                throw new IllegalArgumentException("One of the arguments is illegal. Not possible to convert it to bits string.");
            }

            bits = bits.concat(str);
        }

        switch (vendorListEncoding) {
            case BITFIELD:
                bits = bits.concat(vendorListBitfield(versionConfig, maxVendorId, allowedVendors));
                break;

            case RANGE:
                bits = bits.concat(vendorListRange(versionConfig, maxVendorId, allowedVendors, false));
                break;

            case AUTOMATIC:
                String bitfield = vendorListBitfield(versionConfig, maxVendorId, allowedVendors);
                String range = vendorListRange(versionConfig, maxVendorId, allowedVendors, false);
                bits = bits.concat(bitfield.length() < range.length() ? bitfield : range); // automatic select the most efficient encoding.
                break;
        }

        return bits;
    }

    /**
     * @return ConsentString's version.
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return ConsentString's VersionConfig.
     */
    @SuppressWarnings("unused")
    @NonNull
    public VersionConfig getVersionConfig() {
        return versionConfig;
    }

    /**
     * @return Date of creation.
     */
    @NonNull
    public Date getCreated() {
        return created;
    }

    /**
     * @return Last update date.
     */
    @SuppressWarnings("unused")
    @NonNull
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @return ConsentString's CMP id.
     */
    public int getCmpId() {
        return cmpId;
    }

    /**
     * -
     *
     * @return ConsentString's CMP version.
     */
    public int getCmpVersion() {
        return cmpVersion;
    }

    /**
     * @return ConsentString's consent screen.
     */
    public int getConsentScreen() {
        return consentScreen;
    }

    /**
     * @return ConsentString's consent language.
     */
    @NonNull
    public Language getConsentLanguage() {
        return consentLanguage;
    }

    /**
     * @return ConsentString's editor version.
     */
    public int getEditorVersion() {
        return editorVersion;
    }

    /**
     * @return ConsentString's vendor list version.
     */
    public int getVendorListVersion() {
        return vendorListVersion;
    }

    /**
     * @return ConsentString's max vendor id.
     */
    public int getMaxVendorId() {
        return maxVendorId;
    }

    /**
     * @return ConsentString's editor purposes.
     */
    @NonNull
    public ArrayList<Integer> getEditorPurposes() {
        return editorPurposes;
    }

    /**
     * @return ConsentString's allowed purposes.
     */
    @NonNull
    public ArrayList<Integer> getAllowedPurposes() {
        return allowedPurposes;
    }

    /**
     * @return ConsentString's allowed vendors.
     */
    @NonNull
    public ArrayList<Integer> getAllowedVendors() {
        return allowedVendors;
    }

    /**
     * @return The base64URL encoded consent string.
     */
    public String getConsentString() {
        return consentString;
    }

    /**
     * @return The base64URL encoded iab consent string.
     */
    public String getIABConsentString() {
        if (this.iabConsentString == null) {
            BitsString tmp = new BitsString(true, iabEncodeToBits(versionConfig,
                    created,
                    lastUpdated,
                    cmpId,
                    cmpVersion,
                    consentScreen,
                    consentLanguage,
                    vendorListVersion,
                    maxVendorId,
                    allowedPurposes,
                    allowedVendors,
                    ConsentEncoding.AUTOMATIC));
            this.iabConsentString = tmp.stringValue;
        }
        return this.iabConsentString;
    }

    /**
     * Return a bitfield string that encodes an 'allowed purposes' array.
     *
     * @param versionConfig   The consent string version configuration.
     * @param allowedPurposes An array of allowed purposes id.
     * @return A bitfield string that encodes an 'allowed purposes' array.
     */
    static private String purposesBitField(@NonNull VersionConfig versionConfig, @NonNull ArrayList<Integer> allowedPurposes) {
        String bits = "";

        for (Integer idx = 1; idx <= versionConfig.getAllowedPurposesBitSize(); idx++) {
            bits = bits.concat(allowedPurposes.contains(idx) ? "1" : "0");
        }

        return bits;
    }

    /**
     * Return a bitfield string that encodes an 'editor purposes' array.
     *
     * @param versionConfig   The consent string version configuration.
     * @param editorPurposes  An array of editor purposes id.
     * @return A bitfield string that encodes an 'editor purposes' array.
     */
    static private String purposesEditorBitField(@NonNull VersionConfig versionConfig, @NonNull ArrayList<Integer> editorPurposes) {
        String bits = "";

        for (Integer idx = 1; idx <= versionConfig.getEditorPurposesBitSize(); idx++) {
            bits = bits.concat(editorPurposes.contains(idx) ? "1" : "0");
        }

        return bits;
    }

    /**
     * Return a bitfield string that encodes an 'allowed vendors' array.
     *
     * @param versionConfig  The consent string version configuration.
     * @param maxVendorId    The maximum vendor id that can be found in the current vendor list.
     * @param allowedVendors An array of allowed vendors id.
     * @return A bitfield string that encodes an 'allowed vendors' array.
     */
    static private String vendorListBitfield(@NonNull VersionConfig versionConfig, int maxVendorId, @NonNull ArrayList<Integer> allowedVendors) throws IllegalArgumentException {
        String maxVendorIdBits = BitUtils.longToBits(maxVendorId, versionConfig.getMaxVendorIdBitSize());

        if (maxVendorIdBits == null) {
            throw new IllegalArgumentException("Illegal maxVendorId. Not possible to convert it to bits string.");
        }

        String bits = "".concat(maxVendorIdBits)
                .concat(versionConfig.getEncodingTypeBitfield());

        for (Integer idx = 1; idx <= maxVendorId; idx++) {
            bits = bits.concat(allowedVendors.contains(idx) ? "1" : "0");
        }

        return bits;
    }

    /**
     * Return a complete range string that encodes an 'allowed vendors' array.
     *
     * @param versionConfig  The consent string version configuration.
     * @param maxVendorId    The maximum vendor id that can be found in the current vendor list.
     * @param allowedVendors An array of allowed vendors id.
     * @param defaultValue   the default consent value.
     * @return A range string that encodes an 'allowed vendors' array.
     */
    static private String vendorListRange(@NonNull VersionConfig versionConfig, int maxVendorId, @NonNull ArrayList<Integer> allowedVendors, boolean defaultValue) throws IllegalArgumentException {
        String maxVendorIdBits = BitUtils.longToBits(maxVendorId, versionConfig.getMaxVendorIdBitSize());
        String defaultConsentBits = BitUtils.boolToBits(defaultValue, versionConfig.getDefaultConsentBitSize());

        ArrayList<Range> ranges = ranges(maxVendorId, allowedVendors, defaultValue);
        String numEntriesBits = BitUtils.longToBits(ranges.size(), versionConfig.getNumEntriesBitSize());

        if (maxVendorIdBits == null || defaultConsentBits == null || numEntriesBits == null) {
            throw new IllegalArgumentException("One of the arguments is illegal. Not possible to convert it to bits string.");
        }

        String bits = "".concat(maxVendorIdBits)
                .concat(versionConfig.getEncodingTypeRange())
                .concat(defaultConsentBits)
                .concat(numEntriesBits);

        for (Range range : ranges) {
            if (range.length() > 1) {
                String startVendorBits = BitUtils.longToBits(range.lowerBound, versionConfig.getStartVendorIdBitSize());
                String endVendorBits = BitUtils.longToBits(range.higherBound, versionConfig.getEndVendorIdBitSize());

                if (startVendorBits == null || endVendorBits == null) {
                    throw new IllegalArgumentException("One of the arguments is illegal. Not possible to convert it to bits string.");
                }

                bits = bits.concat(versionConfig.getRangeStartEndId())
                        .concat(startVendorBits)
                        .concat(endVendorBits);
            } else {
                String singleVendorBits = BitUtils.longToBits(range.lowerBound, versionConfig.getSingleVendorIdBitSize());

                if (singleVendorBits == null) {
                    throw new IllegalArgumentException("One of the arguments is illegal. Not possible to convert it to bits string.");
                }

                bits = bits.concat(versionConfig.getRangeSingleId())
                        .concat(singleVendorBits);
            }
        }

        return bits;
    }

    /**
     * Return an ArrayList of Ranges corresponding to an allowed vendors array, for a given default value.
     *
     * @param maxVendorId    The maximum vendor id that can be found in the current vendor list.
     * @param allowedVendors An array of allowed vendors id.
     * @param defaultValue   The default consent value.
     * @return An ArrayList of Ranges corresponding to an allowed vendors array, for a given default value.
     */
    static private ArrayList<Range> ranges(int maxVendorId, @NonNull ArrayList<Integer> allowedVendors, boolean defaultValue) {

        boolean[] vendorsArray = new boolean[maxVendorId];
        for (int i = 0; i < maxVendorId; i++) {
            vendorsArray[i] = allowedVendors.contains(i + 1);
        }

        int currentId = 1;
        Integer startId = null;
        ArrayList<Range> ranges = new ArrayList<>();

        for (boolean allowed : vendorsArray) {
            // Finding start and end bounds of each range.
            if (startId == null && allowed != defaultValue) {
                startId = currentId;
            } else if (startId != null && allowed == defaultValue) {
                ranges.add(new Range(startId, currentId - 1));
                startId = null;
            }

            currentId++;
        }

        if (startId != null) {
            // Closing the last range if needed.
            ranges.add(new Range(startId, currentId - 1));
        }

        return ranges;
    }

    //////////////////
    //// Decoding ////
    //////////////////

    /**
     * Representation of a bits buffer.
     */
    static private class BitsBuffer {

        // The bits buffer.
        @NonNull
        private String buffer;

        /**
         * Initialize a BitsBuffer using a string of bits.
         *
         * @param bits A string of bits.
         */
        public BitsBuffer(@NonNull String bits) {
            buffer = bits;
        }

        /**
         * Return a chunk of bits from a given size and remove them from the buffer.
         *
         * @param numberOfBits the number of bits to pop.
         * @return The chunk of bits removed from the buffer.
         */
        public String pop(int numberOfBits) {
            String result = buffer.substring(0, numberOfBits);
            buffer = buffer.substring(numberOfBits);
            return result;
        }
    }

    /**
     * Decode a ConsentString from a string of bits.
     *
     * @param bits A valid string of bits.
     * @return A ConsentString instance if the string of bits can be decoded, nil otherwise.
     */
    static private ConsentString decodeFromBits(@NonNull String bits) throws UnknownVersionNumberException, IllegalArgumentException {
        if (!BitsString.isValidBitsString(bits)) {
            return null;
        }

        BitsBuffer buffer = new BitsBuffer(bits);

        Long version = BitUtils.bitsToLong(buffer.pop(VersionConfig.getVersionBitSize()));
        if (version == null) {
            return null;
        }

        VersionConfig versionConfig = new VersionConfig(version.intValue());

        Date created = BitUtils.bitsToDate(buffer.pop(versionConfig.getCreatedBitSize()));
        Date lastUpdated = BitUtils.bitsToDate(buffer.pop(versionConfig.getLastUpdatedBitSize()));
        Long cmpId = BitUtils.bitsToLong(buffer.pop(versionConfig.getCmpIdBitSize()));
        Long cmpVersion = BitUtils.bitsToLong(buffer.pop(versionConfig.getCmpVersionBitSize()));
        Long consentScreen = BitUtils.bitsToLong(buffer.pop(versionConfig.getConsentScreenBitSize()));
        Language consentLanguage = BitUtils.bitsToLanguage(buffer.pop(versionConfig.getConsentLanguageBitSize()));
        Long editorVersion = BitUtils.bitsToLong(buffer.pop(versionConfig.getEditorVersionBitSize()));
        Long vendorListVersion = BitUtils.bitsToLong(buffer.pop(versionConfig.getVendorListVersionBitSize()));
        ArrayList<Integer> editorPurposes = consentArray(buffer.pop(versionConfig.getEditorPurposesBitSize()));
        ArrayList<Integer> allowedPurposes = consentArray(buffer.pop(versionConfig.getAllowedPurposesBitSize()));
        Long maxVendorId = BitUtils.bitsToLong(buffer.pop(versionConfig.getMaxVendorIdBitSize()));
        String encodingType = buffer.pop(versionConfig.getEncodingTypeBitSize());

        if (created == null
                || lastUpdated == null
                || cmpId == null
                || cmpVersion == null
                || consentScreen == null
                || consentLanguage == null
                || editorVersion == null
                || vendorListVersion == null
                || editorPurposes == null
                || allowedPurposes == null
                || maxVendorId == null
                || !(encodingType != null && encodingType.length() == versionConfig.getEncodingTypeBitSize())) {
            return null;
        }

        ArrayList<Integer> allowedVendors;

        if (encodingType.equals(versionConfig.getEncodingTypeBitfield())) {
            allowedVendors = allowedVendorsFromBitfield(buffer, maxVendorId.intValue());
        } else if (encodingType.equals(versionConfig.getEncodingTypeRange())) {
            allowedVendors = allowedVendorsFromRange(versionConfig, buffer, maxVendorId.intValue());
        } else {
            return null; // invalid encoding.
        }

        if (allowedVendors != null) {
            return new ConsentString(versionConfig,
                    created,
                    lastUpdated,
                    cmpId.intValue(),
                    cmpVersion.intValue(),
                    consentScreen.intValue(),
                    consentLanguage,
                    editorVersion.intValue(),
                    vendorListVersion.intValue(),
                    maxVendorId.intValue(),
                    editorPurposes,
                    allowedPurposes,
                    allowedVendors);
        }

        return null;
    }

    /**
     * Convert a valid bitfield into a consent array.
     *
     * @param bitfield A valid bitfield.
     * @return A consent array.
     */
    static private ArrayList<Integer> consentArray(@NonNull String bitfield) {
        ArrayList<Integer> consentArray = new ArrayList<>();

        int idx = 1;
        for (char c : bitfield.toCharArray()) {
            if (c == '1') {
                consentArray.add(idx);
            }
            idx++;
        }

        return consentArray;
    }

    /**
     * Decode an allowed vendors arrays from a bitfield encoded buffer.
     *
     * @param buffer      The BitsBuffer from where the bitfield will be retrieved.
     * @param maxVendorId The maximum vendor id that can be found in the current vendor list.
     * @return An allowed vendors array if the buffer can be decoded, null otherwise.
     */
    static private ArrayList<Integer> allowedVendorsFromBitfield(@NonNull BitsBuffer buffer, int maxVendorId) {
        String vendorConsentBits = buffer.pop(maxVendorId);
        if (vendorConsentBits.length() != maxVendorId) {
            return null;
        }

        return consentArray(vendorConsentBits);
    }

    /**
     * Decode an allowed vendors arrays from a range encoded buffer.
     *
     * @param versionConfig The consent string version configuration.
     * @param buffer        The BitsBuffer from where the range will be retrieved.
     * @param maxVendorId   The maximum vendor id that can be found in the current vendor list.
     * @return An allowed vendors array if the buffer can be decoded, null otherwise.
     */
    static private ArrayList<Integer> allowedVendorsFromRange(@NonNull VersionConfig versionConfig, @NonNull BitsBuffer buffer, int maxVendorId) {
        Boolean defaultValue = BitUtils.bitsToBool(buffer.pop(versionConfig.getDefaultConsentBitSize()));
        Long numEntries = BitUtils.bitsToLong(buffer.pop(versionConfig.getNumEntriesBitSize()));

        if (defaultValue != null && numEntries != null) {
            ArrayList<Integer> vendors = new ArrayList<>();

            // Converting every range into an array of vendor id
            for (int i = 0; i < numEntries; i++) {

                String singleOrRange = buffer.pop(versionConfig.getSingleOrRangeBitSize());
                if (singleOrRange.length() != versionConfig.getSingleOrRangeBitSize()) {
                    return null;
                }

                if (singleOrRange.equals(versionConfig.getRangeSingleId())) {
                    Long singleId = BitUtils.bitsToLong(buffer.pop(versionConfig.getSingleVendorIdBitSize()));
                    if (singleId != null) {
                        vendors.add(singleId.intValue());
                    } else {
                        return null;
                    }

                } else if (singleOrRange.equals(versionConfig.getRangeStartEndId())) {
                    Long startId = BitUtils.bitsToLong(buffer.pop(versionConfig.getStartVendorIdBitSize()));
                    Long endId = BitUtils.bitsToLong(buffer.pop(versionConfig.getEndVendorIdBitSize()));

                    if (startId != null && endId != null) {
                        for (int idx = startId.intValue(); idx <= endId.intValue(); idx++) {
                            vendors.add(idx);
                        }
                    } else {
                        return null;
                    }

                } else {
                    return null;
                }

                // Inverting the vendor id array if the consent is true by default.
                if (defaultValue) {
                    ArrayList<Integer> oldArray = vendors;
                    vendors = new ArrayList<>();
                    for (int idx = 0; idx < maxVendorId; idx++) {
                        if (!oldArray.contains(idx)) {
                            vendors.add(idx);
                        }
                    }
                }
            }

            return vendors;
        }

        return null;
    }

    ///////////////
    //// Utils ////
    ///////////////

    /**
     * Return the number of activated vendors allowed from the given vendor list.
     *
     * @param vendorList The vendor list used to calculated the vendors allowed count.
     * @return The number of vendors allowed from the given vendor list.
     */

    public int getAllowedActivatedVendorsCount(@NonNull VendorList vendorList) {
        int count = 0;
        for (Vendor vendor : vendorList.getActivatedVendor()) {
            if (isVendorAllowed(vendor.getId())) {
                count++;
            }
        }

        return count;
    }

    /**
     * Return a new consent string with no consent given for any purposes & vendors.
     *
     * @param consentScreen   The screen number in the CMP where the consent was given.
     * @param consentLanguage The language that the CMP asked for consent in.
     * @param vendorList      The vendor list corresponding to hte consent string.
     * @return A new consent string with no consent given.
     */
    static public ConsentString consentStringWithNoConsent(int consentScreen, @NonNull Language consentLanguage, Editor editor, @NonNull VendorList vendorList) {
        return consentStringWithNoConsent(consentScreen, consentLanguage, editor, vendorList, new Date());
    }

    /**
     * Return a new consent string with no consent given for any purposes & vendors.
     *
     * @param consentScreen   The screen number in the CMP where the consent was given.
     * @param consentLanguage The language that the CMP asked for consent in.
     * @param editor          The editor corresponding to the consent string.
     * @param vendorList      The vendor list corresponding to the consent string.
     * @param date            The date that will be used as create date & last updated date.
     * @return A new consent string with no consent given.
     */
    static public ConsentString consentStringWithNoConsent(int consentScreen, @NonNull Language consentLanguage, Editor editor, @NonNull VendorList vendorList, @NonNull Date date) {
        int editorVersion = 0;
        if (editor != null) {
            editorVersion = editor.getVersion();
        }
        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                date,
                date,
                Constants.CMPInfos.ID,
                Constants.CMPInfos.VERSION,
                consentScreen,
                consentLanguage,
                editorVersion,
                vendorList.getVersion(),
                vendorList.getMaxVendorId(),
                new ArrayList<Integer>(),
                new ArrayList<Integer>(),
                new ArrayList<Integer>());
    }

    /**
     * Return a new consent string with every consent given for any purposes & vendors.
     *
     * @param consentScreen   The screen number in the CMP where the consent was given.
     * @param consentLanguage The language that the CMP asked for consent in.
     * @param editor          The editor corresponding to the consent string.
     * @param vendorList      The vendor list corresponding to the consent string.
     * @return A new consent string with every consent given for any purposes & vendors.
     */
    static public ConsentString consentStringWithFullConsent(int consentScreen, @NonNull Language consentLanguage, Editor editor, @NonNull VendorList vendorList) {
        return consentStringWithFullConsent(consentScreen, consentLanguage, editor, vendorList, new Date());
    }

    /**
     * Return a new consent string with every consent given for any purposes & vendors.
     *
     * @param consentScreen   The screen number in the CMP where the consent was given.
     * @param consentLanguage The language that the CMP asked for consent in.
     * @param editor          The editor corresponding to the consent string.
     * @param vendorList      The vendor list corresponding to the consent string.
     * @param date            The date that will be used as create date & last updated date.
     * @return A new consent string with every consent given for any purposes & vendors.
     */
    static public ConsentString consentStringWithFullConsent(int consentScreen, @NonNull Language consentLanguage, Editor editor, @NonNull VendorList vendorList, @NonNull Date date) {
        ArrayList<Integer> editorPurposes = new ArrayList<>();
        int editorVersion = 0;
        if (editor != null) {
            editorVersion = editor.getVersion();
            for (Purpose purpose : editor.getPurposes()) {
                editorPurposes.add(purpose.getId());
            }
        }

        ArrayList<Integer> allowedPurposes = new ArrayList<>();
        for (Purpose purpose : vendorList.getPurposes()) {
            allowedPurposes.add(purpose.getId());
        }

        ArrayList<Integer> allowedVendors = new ArrayList<>();
        for (Vendor vendor : vendorList.getVendors()) {
            allowedVendors.add(vendor.getId());
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                date,
                date,
                Constants.CMPInfos.ID,
                Constants.CMPInfos.VERSION,
                consentScreen,
                consentLanguage,
                editorVersion,
                vendorList.getVersion(),
                vendorList.getMaxVendorId(),
                editorPurposes,
                allowedPurposes,
                allowedVendors);
    }


    /**
     * Return a new consent string which keeps info from a previous one (generated with a previous vendor list) but gives consent for all new items (purposes and vendors).
     *
     * @param vendorList            The vendor list.
     * @param previousEditor        The previous editor that has been used to generate the previous consent string.
     * @param updatedEditor         The updated editor.
     * @param previousConsentString The previous consent string.
     * @return The new consent string.
     */
    static public ConsentString consentStringFromUpdatedEditor(@NonNull VendorList vendorList, @NonNull Editor previousEditor, @NonNull Editor updatedEditor, @NonNull ConsentString previousConsentString) {
        return consentStringFromUpdatedEditor(vendorList, previousEditor, updatedEditor, previousConsentString, new Date());
    }

    /**
     * Return a new consent string which keeps info from a previous one (generated with a previous vendor list) but gives consent for all new items (purposes and vendors).
     *
     * @param vendorList            The vendor list.
     * @param previousEditor        The previous editor that has been used to generate the previous consent string.
     * @param updatedEditor         The updated editor.
     * @param previousConsentString The previous consent string.
     * @param lastUpdated           The date that will be used as last updated date.
     * @return The new consent string.
     */
    static public ConsentString consentStringFromUpdatedEditor(@NonNull VendorList vendorList, @NonNull Editor previousEditor, @NonNull Editor updatedEditor, @NonNull ConsentString previousConsentString, @NonNull Date lastUpdated) {
        // Retrieve all already allowed purposes.
        ArrayList<Integer> editorPurposes = new ArrayList<>(previousConsentString.getEditorPurposes());
        for (int idx = 0; idx < updatedEditor.getPurposes().size(); idx++) {
            Purpose purpose = updatedEditor.getPurposes().get(idx);

            // Allow purpose only if it was not in the previous vendor list.
            if (purpose.getId() > previousEditor.getPurposes().size()) {
                editorPurposes.add(purpose.getId());
            }
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                previousConsentString.getCreated(),
                lastUpdated,
                previousConsentString.getCmpId(),
                previousConsentString.getCmpVersion(),
                previousConsentString.getConsentScreen(),
                previousConsentString.getConsentLanguage(),
                updatedEditor,
                vendorList,
                editorPurposes,
                previousConsentString.getAllowedPurposes(),
                previousConsentString.getAllowedVendors());
    }

    /**
     * Return a new consent string which keeps info from a previous one (generated with a previous vendor list) but gives consent for all new items (purposes and vendors).
     *
     * @param updatedVendorList     The updated vendor list.
     * @param previousVendorList    The previous vendor list that has been used to generate the previous consent string.
     * @param previousConsentString The previous consent string.
     * @return The new consent string.
     */
    static public ConsentString consentStringFromUpdatedVendorList(@NonNull VendorList updatedVendorList, @NonNull VendorList previousVendorList, Editor editor, @NonNull ConsentString previousConsentString) {
        return consentStringFromUpdatedVendorList(updatedVendorList, previousVendorList, editor, previousConsentString, new Date());
    }

    /**
     * Return a new consent string which keeps info from a previous one (generated with a previous vendor list) but gives consent for all new items (purposes and vendors).
     *
     * @param updatedVendorList     The updated vendor list.
     * @param previousVendorList    The previous vendor list that has been used to generate the previous consent string.
     * @param previousConsentString The previous consent string.
     * @param lastUpdated           The date that will be used as last updated date.
     * @return The new consent string.
     */
    static public ConsentString consentStringFromUpdatedVendorList(@NonNull VendorList updatedVendorList, @NonNull VendorList previousVendorList, Editor editor, @NonNull ConsentString previousConsentString, @NonNull Date lastUpdated) {
        // Retrieve all already allowed purposes.
        ArrayList<Integer> allowedPurposes = new ArrayList<>(previousConsentString.getAllowedPurposes());
        for (int idx = 0; idx < updatedVendorList.getPurposes().size(); idx++) {
            Purpose purpose = updatedVendorList.getPurposes().get(idx);

            // Allow purpose only if it was not in the previous vendor list.
            if (purpose.getId() > previousVendorList.getPurposes().size()) {
                allowedPurposes.add(purpose.getId());
            }
        }

        // Retrieve all already allowed vendors.
        ArrayList<Integer> allowedVendors = new ArrayList<>(previousConsentString.getAllowedVendors());
        for (int idx = 0; idx < updatedVendorList.getVendors().size(); idx++) {
            Vendor vendor = updatedVendorList.getVendors().get(idx);

            // Allow vendor only if it was not in the previous vendor list.
            if (!previousVendorList.containsVendorWithId(vendor.getId())) {
                allowedVendors.add(vendor.getId());
            }
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                previousConsentString.getCreated(),
                lastUpdated,
                previousConsentString.getCmpId(),
                previousConsentString.getCmpVersion(),
                previousConsentString.getConsentScreen(),
                previousConsentString.getConsentLanguage(),
                editor,
                updatedVendorList,
                previousConsentString.getEditorPurposes(),
                allowedPurposes,
                allowedVendors);
    }

    /**
     * Return a new consent string identical to the one provided, with a consent given for a particular purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be added to the consent list.
     * @param consentString The consent string which should be copied.
     * @return A new consent string with a consent given for a particular purpose.
     */
    static public ConsentString consentStringByAddingPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString) {
        return consentStringByAddingPurposeConsent(purposeId, consentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent given for a particular purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be added to the consent list.
     * @param consentString The consent string which should be copied.
     * @param lastUpdated   The date that will be used as last updated date.
     * @return A new consent string with a consent given for a particular purpose.
     */
    static public ConsentString consentStringByAddingPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString, @NonNull Date lastUpdated) {
        ArrayList<Integer> allowedPurpose = new ArrayList<>(consentString.getAllowedPurposes());
        if (!allowedPurpose.contains(purposeId)) {
            allowedPurpose.add(purposeId);
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                consentString.getCreated(),
                lastUpdated,
                consentString.getCmpId(),
                consentString.getCmpVersion(),
                consentString.getConsentScreen(),
                consentString.getConsentLanguage(),
                consentString.getEditorVersion(),
                consentString.getVendorListVersion(),
                consentString.getMaxVendorId(),
                consentString.getEditorPurposes(),
                allowedPurpose,
                consentString.getAllowedVendors());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent given for a particular editor purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be added to the consent list.
     * @param consentString The consent string which should be copied.
     * @return A new consent string with a consent given for a particular purpose.
     */
    static public ConsentString consentStringByAddingEditorPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString) {
        return consentStringByAddingEditorPurposeConsent(purposeId, consentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent given for a particular purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be added to the consent list.
     * @param consentString The consent string which should be copied.
     * @param lastUpdated   The date that will be used as last updated date.
     * @return A new consent string with a consent given for a particular purpose.
     */
    static public ConsentString consentStringByAddingEditorPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString, @NonNull Date lastUpdated) {
        ArrayList<Integer> editorPurpose = new ArrayList<>(consentString.getEditorPurposes());
        if (!editorPurpose.contains(purposeId)) {
            editorPurpose.add(purposeId);
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                consentString.getCreated(),
                lastUpdated,
                consentString.getCmpId(),
                consentString.getCmpVersion(),
                consentString.getConsentScreen(),
                consentString.getConsentLanguage(),
                consentString.getEditorVersion(),
                consentString.getVendorListVersion(),
                consentString.getMaxVendorId(),
                editorPurpose,
                consentString.getAllowedPurposes(),
                consentString.getAllowedVendors());
    }

    /**
     * Return a new consent string identical to the one provided, with consents added for all purposes.
     * <p>
     * Note: this method will updated the version config and the last updated date.
     *
     * @param vendorList            The vendor list used to retrieve all purposes to consent to.
     * @param editor                The editor used to retrieve all purposes to consent to.
     * @param previousConsentString The previous consent string.
     * @return a new consent string giving consent to all purposes of the vendor list, or null if the vendor list version is not the same as the consent string's vendor list version.
     */
    static public @Nullable ConsentString consentStringByAddingAllPurposeConsents(@NonNull VendorList vendorList, @NonNull Editor editor, @NonNull ConsentString previousConsentString) {
        return consentStringByAddingAllPurposeConsents(vendorList, editor, previousConsentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with consents added for all purposes.
     * <p>
     * Note: this method will updated the version config and the last updated date.
     *
     * @param vendorList            The vendor list used to retrieve all purposes to consent to.
     * @param previousConsentString The previous consent string.
     * @param lastUpdated           The date that will be used as last updated date.
     * @return a new consent string giving consent to all purposes of the vendor list, or null if the vendor list version is not the same as the consent string's vendor list version.
     */
    static public @Nullable ConsentString consentStringByAddingAllPurposeConsents(@NonNull VendorList vendorList, @NonNull Editor editor, @NonNull ConsentString previousConsentString, @NonNull Date lastUpdated) {
        if (vendorList.getVersion() != previousConsentString.getVendorListVersion()) {
            return null;
        }

        if (editor != null && editor.getVersion() != previousConsentString.getEditorVersion()) {
            return null;
        }

        ConsentString consentString = new ConsentString(previousConsentString);

        for (Purpose purpose : vendorList.getPurposes()) {
            consentString = ConsentString.consentStringByAddingPurposeConsent(purpose.getId(), consentString, lastUpdated);
        }
        if (editor != null) {
            for (Purpose purpose : editor.getPurposes()) {
                consentString = ConsentString.consentStringByAddingEditorPurposeConsent(purpose.getId(), consentString, lastUpdated);
            }
        }

        return consentString;
    }

    /**
     * Return a new consent string identical to the one provided, with a consent removed for a particular purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be removed from the consent list.
     * @param consentString The consent string which should be copied.
     * @return A new consent string with a consent removed for a particular purpose.
     */
    static public ConsentString consentStringByRemovingPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString) {
        return consentStringByRemovingPurposeConsent(purposeId, consentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent removed for a particular purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be removed from the consent list.
     * @param consentString The consent string which should be copied.
     * @param lastUpdated   The date that will be used as last updated date.
     * @return A new consent string with a consent removed for a particular purpose.
     */
    static public ConsentString consentStringByRemovingPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString, @NonNull Date lastUpdated) {
        ArrayList<Integer> allowedPurpose = new ArrayList<>(consentString.getAllowedPurposes());
        if (allowedPurpose.contains(purposeId)) {
            allowedPurpose.remove(purposeId);
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                consentString.getCreated(),
                lastUpdated,
                consentString.getCmpId(),
                consentString.getCmpVersion(),
                consentString.getConsentScreen(),
                consentString.getConsentLanguage(),
                consentString.getEditorVersion(),
                consentString.getVendorListVersion(),
                consentString.getMaxVendorId(),
                consentString.getEditorPurposes(),
                allowedPurpose,
                consentString.getAllowedVendors());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent removed for a particular purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be removed from the consent list.
     * @param consentString The consent string which should be copied.
     * @return A new consent string with a consent removed for a particular purpose.
     */
    static public ConsentString consentStringByRemovingEditorPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString) {
        return consentStringByRemovingEditorPurposeConsent(purposeId, consentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent removed for a particular purpose.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param purposeId     The purpose id which should be removed from the consent list.
     * @param consentString The consent string which should be copied.
     * @param lastUpdated   The date that will be used as last updated date.
     * @return A new consent string with a consent removed for a particular purpose.
     */
    static public ConsentString consentStringByRemovingEditorPurposeConsent(@NonNull Integer purposeId, @NonNull ConsentString consentString, @NonNull Date lastUpdated) {
        ArrayList<Integer> editorPurposes = new ArrayList<>(consentString.getEditorPurposes());
        if (editorPurposes.contains(purposeId)) {
            editorPurposes.remove(purposeId);
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                consentString.getCreated(),
                lastUpdated,
                consentString.getCmpId(),
                consentString.getCmpVersion(),
                consentString.getConsentScreen(),
                consentString.getConsentLanguage(),
                consentString.getEditorVersion(),
                consentString.getVendorListVersion(),
                consentString.getMaxVendorId(),
                editorPurposes,
                consentString.getAllowedPurposes(),
                consentString.getAllowedVendors());
    }

    /**
     * Return a new consent string identical to the one provided, with all consent removed for the purposes.
     * <p>
     * Note: this method will updated the version config and the last updated date.
     *
     * @param vendorList            The current vendor list.
     * @param editor                The current editor.
     * @param previousConsentString The previous consent string.
     * @return A new consent string with all consent removed for purposes, or null if the vendor list version is not the same as the consent string's vendor list version.
     */
    static public @Nullable ConsentString consentStringByRemovingAllPurposeConsents(@NonNull VendorList vendorList, @NonNull Editor editor, @NonNull ConsentString previousConsentString) {
        return consentStringByRemovingAllPurposeConsents(vendorList, editor, previousConsentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with all consent removed for the purposes.
     * <p>
     * Note: this method will updated the version config and the last updated date.
     *
     * @param vendorList            The current vendor list.
     * @param editor                The current editor.
     * @param previousConsentString The previous consent string.
     * @param lastUpdated           The date that will be used as last updated date.
     * @return A new consent string with all consent removed for purposes, or null if the vendor list version is not the same as the consent string's vendor list version.
     */
    static public @Nullable ConsentString consentStringByRemovingAllPurposeConsents(@NonNull VendorList vendorList, @NonNull Editor editor, @NonNull ConsentString previousConsentString, @NonNull Date lastUpdated) {
        if (vendorList.getVersion() != previousConsentString.getVendorListVersion()) {
            return null;
        }

        ConsentString consentString = new ConsentString(previousConsentString);

        for (Purpose purpose : vendorList.getPurposes()) {
            consentString = consentStringByRemovingPurposeConsent(purpose.getId(), consentString, lastUpdated);
        }

        if (editor != null) {
            for (Purpose purpose : editor.getPurposes()) {
                consentString = consentStringByRemovingEditorPurposeConsent(purpose.getId(), consentString, lastUpdated);
            }
        }

        return consentString;
    }

    /**
     * Return a new consent string identical to the one provided, with a consent given for a particular vendor.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param vendorId      The vendor id which should be added to the consent list.
     * @param consentString The consent string which should be copied.
     * @return A new consent string with a consent given for a particular vendor.
     */
    static public ConsentString consentStringByAddingVendorConsent(@NonNull Integer vendorId, @NonNull ConsentString consentString) {
        return consentStringByAddingVendorConsent(vendorId, consentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent given for a particular vendor.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param vendorId      The vendor id which should be added to the consent list.
     * @param consentString The consent string which should be copied.
     * @param lastUpdated   The date that will be used as last updated date.
     * @return A new consent string with a consent given for a particular vendor.
     */
    static public ConsentString consentStringByAddingVendorConsent(@NonNull Integer vendorId, @NonNull ConsentString consentString, @NonNull Date lastUpdated) {
        ArrayList<Integer> allowedVendors = new ArrayList<>(consentString.getAllowedVendors());
        if (!allowedVendors.contains(vendorId)) {
            allowedVendors.add(vendorId);
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                consentString.getCreated(),
                lastUpdated,
                consentString.getCmpId(),
                consentString.getCmpVersion(),
                consentString.getConsentScreen(),
                consentString.getConsentLanguage(),
                consentString.getEditorVersion(),
                consentString.getVendorListVersion(),
                consentString.getMaxVendorId(),
                consentString.getEditorPurposes(),
                consentString.getAllowedPurposes(),
                allowedVendors);
    }

    /**
     * Return a new consent string identical to the one provided, with a consent removed for a particular vendor.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param vendorId      The vendor id which should be removed from the consent list.
     * @param consentString The consent string which should be copied.
     * @return A new consent string with a consent removed for a particular vendor.
     */
    static public ConsentString consentStringByRemovingVendorConsent(@NonNull Integer vendorId, @NonNull ConsentString consentString) {
        return consentStringByRemovingVendorConsent(vendorId, consentString, new Date());
    }

    /**
     * Return a new consent string identical to the one provided, with a consent removed for a particular vendor.
     * <p>
     * Note: this method will update the version config and the last updated date.
     *
     * @param vendorId      The vendor id which should be removed from the consent list.
     * @param consentString The consent string which should be copied.
     * @param lastUpdated   The date that will be used as last updated date.
     * @return A new consent string with a consent removed for a particular vendor.
     */
    static public ConsentString consentStringByRemovingVendorConsent(@NonNull Integer vendorId, @NonNull ConsentString consentString, @NonNull Date lastUpdated) {
        ArrayList<Integer> allowedVendors = new ArrayList<>(consentString.getAllowedVendors());
        if (allowedVendors.contains(vendorId)) {
            allowedVendors.remove(vendorId);
        }

        //noinspection ConstantConditions
        return new ConsentString(VersionConfig.getLatest(),
                consentString.getCreated(),
                lastUpdated,
                consentString.getCmpId(),
                consentString.getCmpVersion(),
                consentString.getConsentScreen(),
                consentString.getConsentLanguage(),
                consentString.getEditorVersion(),
                consentString.getVendorListVersion(),
                consentString.getMaxVendorId(),
                consentString.getEditorPurposes(),
                consentString.getAllowedPurposes(),
                allowedVendors);
    }

    /**************************
     *** Parcelable section ***
     **************************/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.version);
        dest.writeParcelable(this.versionConfig, flags);
        dest.writeLong(this.created.getTime());
        dest.writeLong(this.lastUpdated.getTime());
        dest.writeInt(this.cmpId);
        dest.writeInt(this.cmpVersion);
        dest.writeInt(this.consentScreen);
        dest.writeParcelable(this.consentLanguage, flags);
        dest.writeInt(this.editorVersion);
        dest.writeInt(this.vendorListVersion);
        dest.writeInt(this.maxVendorId);
        dest.writeList(this.editorPurposes);
        dest.writeList(this.allowedPurposes);
        dest.writeList(this.allowedVendors);
        dest.writeInt(this.vendorListEncoding.getValue());
        dest.writeString(this.consentString);
    }

    protected ConsentString(Parcel in) {
        this.version = in.readInt();
        this.versionConfig = in.readParcelable(VersionConfig.class.getClassLoader());
        long tmpCreated = in.readLong();
        this.created = new Date(tmpCreated);
        long tmpLastUpdated = in.readLong();
        this.lastUpdated = new Date(tmpLastUpdated);
        this.cmpId = in.readInt();
        this.cmpVersion = in.readInt();
        this.consentScreen = in.readInt();
        this.consentLanguage = in.readParcelable(Language.class.getClassLoader());
        this.editorVersion = in.readInt();
        this.vendorListVersion = in.readInt();
        this.maxVendorId = in.readInt();
        this.editorPurposes = new ArrayList<>();
        in.readList(this.editorPurposes, Integer.class.getClassLoader());
        this.allowedPurposes = new ArrayList<>();
        in.readList(this.allowedPurposes, Integer.class.getClassLoader());
        this.allowedVendors = new ArrayList<>();
        in.readList(this.allowedVendors, Integer.class.getClassLoader());
        this.vendorListEncoding = ConsentEncoding.from(in.readInt());
        this.consentString = in.readString();
    }

    public static final Creator<ConsentString> CREATOR = new Creator<ConsentString>() {
        @Override
        public ConsentString createFromParcel(Parcel source) {
            return new ConsentString(source);
        }

        @Override
        public ConsentString[] newArray(int size) {
            return new ConsentString[size];
        }
    };
}
