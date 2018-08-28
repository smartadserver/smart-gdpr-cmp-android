package com.fidzup.android.cmp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fidzup.android.cmp.exception.UnknownVersionNumberException;

/**
 * Configuration for a given version of the consent string.
 */

public class VersionConfig implements Parcelable {

    // the version of the consent string.
    private int version;

    // The number of bits used to encode the version field.
    static private final int versionBitSize = 6;

    // The number of bits used to encode the created field.
    private int createdBitSize;

    // The number of bits used to encode the lastUpdated field.
    private int lastUpdatedBitSize;

    // The number if bits used to encode cmpId field.
    private int cmpIdBitSize;

    // The number of bits used to encode the cmpVersion field.
    private int cmpVersionBitSize;

    // The number of bits used to encode the consentScreen field.
    private int consentScreenBitSize;

    // The number of bits used to encode the consentLanguage field.
    private int consentLanguageBitSize;

    // The number of bits used to encode the vendorListVersion field.
    private int vendorListVersionBitSize;

    // The number of bits used to encode the allowedPurposes field.
    private int allowedPurposesBitSize;

    // The number of bits used to encode the maxVendorId field.
    private int maxVendorIdBitSize;

    // The number of bits used to encode the encodingType field.
    private int encodingTypeBitSize;

    // The number of bits used to encode the defaultConsent field.
    private int defaultConsentBitSize;

    // The number of bits used to encode the numEntries field.
    private int numEntriesBitSize;

    // The number of bits used to encode the singleOrRange field.
    private int singleOrRangeBitSize;

    // The number of bits used to encode the singleVendorId field.
    private int singleVendorIdBitSize;

    // The number of bits used to encode startVendorId field.
    private int startVendorIdBitSize;

    // The number of bits used to encode endVendorId field.
    private int endVendorIdBitSize;

    // The bit representing a bitfield encoding.
    private String encodingTypeBitfield;

    // The bit representing a range encoding.
    private String encodingTypeRange;

    // The bit representing a single vendor id for a range encoding.
    private String rangeSingleId;

    // The bit representing a start/end range vendor id for a range encoding.
    private String rangeStartEndId;

    /**
     * Initialize a consent string version config instance from a version number.
     *
     * @param version The consent string version number.
     * @throws UnknownVersionNumberException If the version number is invalid.
     */
    public VersionConfig(int version) throws UnknownVersionNumberException {
        this.version = version;

        switch (version) {
            case 1:

                createdBitSize = 36;
                lastUpdatedBitSize = 36;
                cmpIdBitSize = 12;
                cmpVersionBitSize = 12;
                consentScreenBitSize = 6;
                consentLanguageBitSize = 12;
                vendorListVersionBitSize = 12;
                allowedPurposesBitSize = 24;
                maxVendorIdBitSize = 16;
                encodingTypeBitSize = 1;
                defaultConsentBitSize = 1;
                numEntriesBitSize = 12;
                singleOrRangeBitSize = 1;
                singleVendorIdBitSize = 16;
                startVendorIdBitSize = 16;
                endVendorIdBitSize = 16;

                encodingTypeBitfield = "0";
                encodingTypeRange = "1";

                rangeSingleId = "0";
                rangeStartEndId = "1";

                break;
            default:
                throw new UnknownVersionNumberException();
        }
    }

    /**
     * @return The version number.
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return The number of bits used to encode the version field.
     */
    public static int getVersionBitSize() {
        return versionBitSize;
    }

    /**
     * @return The number of bits used to encode the created field.
     */
    public int getCreatedBitSize() {
        return createdBitSize;
    }

    /**
     * @return The number of bits used to encode the lastUpdated field.
     */
    public int getLastUpdatedBitSize() {
        return lastUpdatedBitSize;
    }

    /**
     * @return The number if bits used to encode cmpId field.
     */
    public int getCmpIdBitSize() {
        return cmpIdBitSize;
    }

    /**
     * @return The number of bits used to encode the cmpVersion field.
     */
    public int getCmpVersionBitSize() {
        return cmpVersionBitSize;
    }

    /**
     * @return The number of bits used to encode the consentScreen field.
     */
    public int getConsentScreenBitSize() {
        return consentScreenBitSize;
    }

    /**
     * @return The number of bits used to encode the consentLanguage field.
     */
    public int getConsentLanguageBitSize() {
        return consentLanguageBitSize;
    }

    /**
     * @return The number of bits used to encode the vendorListVersion field.
     */
    public int getVendorListVersionBitSize() {
        return vendorListVersionBitSize;
    }

    /**
     * @return The number of bits used to encode the allowedPurposes field.
     */
    public int getAllowedPurposesBitSize() {
        return allowedPurposesBitSize;
    }

    /**
     * @return The number of bits used to encode the maxVendorId field.
     */
    public int getMaxVendorIdBitSize() {
        return maxVendorIdBitSize;
    }

    /**
     * @return The number of bits used to encode the encodingType field.
     */
    public int getEncodingTypeBitSize() {
        return encodingTypeBitSize;
    }

    /**
     * @return The number of bits used to encode the defaultConsent field.
     */
    public int getDefaultConsentBitSize() {
        return defaultConsentBitSize;
    }

    /**
     * @return The number of bits used to encode the numEntries field.
     */
    public int getNumEntriesBitSize() {
        return numEntriesBitSize;
    }

    /**
     * @return The number of bits used to encode the singleOrRange field.
     */
    public int getSingleOrRangeBitSize() {
        return singleOrRangeBitSize;
    }

    /**
     * @return The number of bits used to encode the singleVendorId field.
     */
    public int getSingleVendorIdBitSize() {
        return singleVendorIdBitSize;
    }

    /**
     * @return The number of bits used to encode startVendorId field.
     */
    public int getStartVendorIdBitSize() {
        return startVendorIdBitSize;
    }

    /**
     * @return The number of bits used to encode endVendorId field.
     */
    public int getEndVendorIdBitSize() {
        return endVendorIdBitSize;
    }

    /**
     * @return The bit representing a bitfield encoding.
     */
    public String getEncodingTypeBitfield() {
        return encodingTypeBitfield;
    }

    /**
     * @return The bit representing a range encoding.
     */
    public String getEncodingTypeRange() {
        return encodingTypeRange;
    }

    /**
     * @return The bit representing a single vendor id for a range encoding.
     */
    public String getRangeSingleId() {
        return rangeSingleId;
    }

    /**
     * @return The bit representing a start/end range vendor id for a range encoding.
     */
    public String getRangeStartEndId() {
        return rangeStartEndId;
    }

    /**
     * @return A new instance of VersionConfig with the latest version.
     */
    static public VersionConfig getLatest() {
        try {
            return new VersionConfig(1);
        } catch (UnknownVersionNumberException e) {
            // should never happen.
            return null;
        }
    }

    ////////////////////////////
    //// Parcelable section ////
    ////////////////////////////

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.version);
        dest.writeInt(this.createdBitSize);
        dest.writeInt(this.lastUpdatedBitSize);
        dest.writeInt(this.cmpIdBitSize);
        dest.writeInt(this.cmpVersionBitSize);
        dest.writeInt(this.consentScreenBitSize);
        dest.writeInt(this.consentLanguageBitSize);
        dest.writeInt(this.vendorListVersionBitSize);
        dest.writeInt(this.allowedPurposesBitSize);
        dest.writeInt(this.maxVendorIdBitSize);
        dest.writeInt(this.encodingTypeBitSize);
        dest.writeInt(this.defaultConsentBitSize);
        dest.writeInt(this.numEntriesBitSize);
        dest.writeInt(this.singleOrRangeBitSize);
        dest.writeInt(this.singleVendorIdBitSize);
        dest.writeInt(this.startVendorIdBitSize);
        dest.writeInt(this.endVendorIdBitSize);
        dest.writeString(this.encodingTypeBitfield);
        dest.writeString(this.encodingTypeRange);
        dest.writeString(this.rangeSingleId);
        dest.writeString(this.rangeStartEndId);
    }

    @SuppressWarnings("WeakerAccess")
    protected VersionConfig(Parcel in) {
        this.version = in.readInt();
        this.createdBitSize = in.readInt();
        this.lastUpdatedBitSize = in.readInt();
        this.cmpIdBitSize = in.readInt();
        this.cmpVersionBitSize = in.readInt();
        this.consentScreenBitSize = in.readInt();
        this.consentLanguageBitSize = in.readInt();
        this.vendorListVersionBitSize = in.readInt();
        this.allowedPurposesBitSize = in.readInt();
        this.maxVendorIdBitSize = in.readInt();
        this.encodingTypeBitSize = in.readInt();
        this.defaultConsentBitSize = in.readInt();
        this.numEntriesBitSize = in.readInt();
        this.singleOrRangeBitSize = in.readInt();
        this.singleVendorIdBitSize = in.readInt();
        this.startVendorIdBitSize = in.readInt();
        this.endVendorIdBitSize = in.readInt();
        this.encodingTypeBitfield = in.readString();
        this.encodingTypeRange = in.readString();
        this.rangeSingleId = in.readString();
        this.rangeStartEndId = in.readString();
    }

    public static final Creator<VersionConfig> CREATOR = new Creator<VersionConfig>() {
        @Override
        public VersionConfig createFromParcel(Parcel source) {
            return new VersionConfig(source);
        }

        @Override
        public VersionConfig[] newArray(int size) {
            return new VersionConfig[size];
        }
    };
}
