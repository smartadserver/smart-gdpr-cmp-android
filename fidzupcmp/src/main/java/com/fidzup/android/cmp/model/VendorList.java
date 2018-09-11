package com.fidzup.android.cmp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fidzup.android.cmp.util.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Representation of a vendor list.
 */

@SuppressWarnings("WeakerAccess")
public class VendorList implements Parcelable {

    /**
     * class storing all JSON keys used to parse the vendors list.
     */
    private static class JSONKey {
        static public final String VENDOR_LIST_VERSION              = "vendorListVersion";
        static public final String LAST_UPDATED                     = "lastUpdated";

        class Purposes {
            static public final String PURPOSES = "purposes";
            static public final String ID = "id";
            static public final String NAME = "name";
            static public final String DESCRIPTION = "description";
        }

        class Features {
            static public final String FEATURES = "features";
            static public final String ID = "id";
            static public final String NAME = "name";
            static public final String DESCRIPTION = "description";
        }

        class Vendors {
            static public final String VENDORS = "vendors";
            static public final String ID = "id";
            static public final String NAME = "name";
            static public final String PURPOSE_IDS = "purposeIds";
            static public final String LEGITIMATE_PURPOSE_IDS = "legIntPurposeIds";
            static public final String FEATURE_IDS = "featureIds";
            static public final String POLICY_URL = "policyUrl";
            static public final String DELETED_DATE = "deletedDate";
        }
    }

    // The vendor list version.
    private int version;

    // The date of the last vendor list update.
    @NonNull
    private Date lastUpdated;

    // A list of purposes.
    @NonNull
    private ArrayList<Purpose> purposes;

    // A list of features.
    @NonNull
    private ArrayList<Feature> features;

    // A list of vendors.
    @NonNull
    private ArrayList<Vendor> vendors;

    // A list of activated vendors.
    private ArrayList<Vendor> activatedVendors;


    /**
     * Initialize a list of vendors using direct parameters.
     *
     * @param version     The version of the vendor list.
     * @param lastUpdated The date of the last vendor list update.
     * @param purposes    A list of purposes.
     * @param features    A list of features.
     * @param vendors     A list of vendors.
     */
    public VendorList(int version, @NonNull Date lastUpdated, @NonNull ArrayList<Purpose> purposes, @NonNull ArrayList<Feature> features, @NonNull ArrayList<Vendor> vendors) {
        this.version = version;
        this.lastUpdated = lastUpdated;
        this.purposes = purposes;
        this.features = features;
        this.vendors = vendors;
    }

    /**
     * Initialize a list of vendors from a vendor list JSON (if valid).
     *
     * @param JSON The data representation of the vendor list JSON.
     * @throws JSONException is JSON is invalid.
     */
    public VendorList(@NonNull JSONObject JSON) throws JSONException, MalformedURLException {
        this(JSON, null);
    }

    /**
     * Initialize a localized list of vendors from a vendor list JSON and a localized vendor list JSON.
     *
     * @param JSON          The data representation of the vendor list JSON.
     * @param localizedJSON The data representation of the localized vendor list JSON.
     */
    public VendorList(@NonNull JSONObject JSON, @Nullable JSONObject localizedJSON) throws JSONException {
        this(JSON, localizedJSON,null);
    }

    /**
     * Initialize a localized list of vendors from a vendor list JSON, a localized vendor list JSON and a sub vendor list JSON.
     *
     * @param JSON          The data representation of the vendor list JSON.
     * @param localizedJSON The data representation of the localized vendor list JSON.
     */
    public VendorList(@NonNull JSONObject JSON, @Nullable JSONObject localizedJSON, @Nullable JSONObject subJSON) throws JSONException {
        version = JSON.getInt(JSONKey.VENDOR_LIST_VERSION);

        Date lastUpdated = DateUtils.dateFromString(JSON.getString(JSONKey.LAST_UPDATED));
        if (lastUpdated == null) {
            throw new JSONException("lastUpdated date format invalid.");
        }
        this.lastUpdated = lastUpdated;

        JSONArray rawLocalizedPurposesArray = null;
        JSONArray rawLocalizedFeaturesArray = null;

        if (localizedJSON != null) {
            try {
                rawLocalizedPurposesArray = localizedJSON.getJSONArray(JSONKey.Purposes.PURPOSES);
            } catch (JSONException e) {
                // do nothing
            }

            try {
                rawLocalizedFeaturesArray = localizedJSON.getJSONArray(JSONKey.Features.FEATURES);
            } catch (JSONException e) {
                // do nothing
            }
        }

        JSONArray subVendorsArray = null;
        JSONArray subPurposesArray = null;
        JSONArray subFeaturesArray = null;

        if (subJSON != null) {
            //Mandatory to support pubvendors.json specification version 1.0
            try {
                subVendorsArray = subJSON.getJSONArray(JSONKey.Vendors.VENDORS);
            } catch (JSONException e) {
                // do nothing
            }

            //Experimental to support future pubvendors.json specification version 1.1
            try {
                subPurposesArray = subJSON.getJSONArray(JSONKey.Purposes.PURPOSES);
            } catch (JSONException e) {
                // do nothing
            }

            try {
                subFeaturesArray = subJSON.getJSONArray(JSONKey.Features.FEATURES);
            } catch (JSONException e) {
                // do nothing
            }

        }

        purposes = parsePurposes(JSON.getJSONArray(JSONKey.Purposes.PURPOSES), rawLocalizedPurposesArray, subPurposesArray);
        features = parseFeatures(JSON.getJSONArray(JSONKey.Features.FEATURES), rawLocalizedFeaturesArray, subFeaturesArray);
        vendors = parseVendors(JSON.getJSONArray(JSONKey.Vendors.VENDORS),subVendorsArray);
    }

    /**
     * @return The maximum vendor id used in the vendor list.
     */
    public int getMaxVendorId() {
        int maxId = 0;
        for (Vendor vendor : vendors) {
            maxId = maxId < vendor.getId() ? vendor.getId() : maxId;
        }

        return maxId;
    }

    /**
     * @return The version number of the vendor list.
     */
    public int getVersion() {
        return version;
    }

    /**
     * @return The Date of the last update.
     */
    @NonNull
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @return An ArrayList of purposes.
     */
    @NonNull
    public ArrayList<Purpose> getPurposes() {
        return purposes;
    }

    /**
     * @return An ArrayList of features.
     */
    @NonNull
    public ArrayList<Feature> getFeatures() {
        return features;
    }

    /**
     * @return An ArrayList of vendors.
     */
    @NonNull
    public ArrayList<Vendor> getVendors() {
        return vendors;
    }

    /**
     * Get the purpose with the given id.
     *
     * @param id The id of the wanted purpose.
     * @return The wanted purpose if it exists, null otherwise.
     */
    public Purpose getPurposeWithId(int id) {
        for (Purpose purpose : purposes) {
            if (purpose.getId() == id) {
                return purpose;
            }
        }
        return null;
    }

    /**
     * Get the feature with the given id.
     *
     * @param id The id of the wanted feature.
     * @return The wanted feature if it exists, null otherwise.
     */
    public Feature getFeatureWithId(int id) {
        for (Feature feature : features) {
            if (feature.getId() == id) {
                return feature;
            }
        }
        return null;
    }

    /**
     * Whether or not the vendor list contains a specific vendor.
     *
     * @param id The id of the researched vendor.
     * @return whether or not the vendor list contains a specific vendor.
     */
    public boolean containsVendorWithId(int id) {
        for (Vendor vendor : vendors) {
            if (vendor.getId() == id) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return An ArrayList of activated vendors.
     */
    public ArrayList<Vendor> getActivatedVendor() {
        if (activatedVendors == null) {
            activatedVendors = new ArrayList<>();
            for (Vendor vendor : vendors) {
                if (vendor.isActivated()) {
                    activatedVendors.add(vendor);
                }
            }
        }

        return activatedVendors;
    }

    /**
     * Parse a collection of purposes.
     *
     * @param rawPurposesArray A collection of purposes in JSON format.
     * @return An ArrayList of purposes, or throw an exception if the JSON is invalid.
     * @throws JSONException if JSON is invalid.
     */
    static private ArrayList<Purpose> parsePurposes(@NonNull JSONArray rawPurposesArray, @Nullable JSONArray rawLocalizedPurposesArray, @Nullable JSONArray subPurposesArray) throws JSONException {
        ArrayList<Purpose> purposes = new ArrayList<>();

        for (int i = 0; i < rawPurposesArray.length(); i++) {
            JSONObject rawPurpose = (JSONObject) rawPurposesArray.get(i);

            int id = rawPurpose.getInt(JSONKey.Purposes.ID);
            String name = rawPurpose.getString(JSONKey.Purposes.NAME);
            String description = rawPurpose.getString(JSONKey.Purposes.DESCRIPTION);

            if (rawLocalizedPurposesArray != null) {
                try {
                    // try to get the localization. Do not throw exception if something is missing.
                    for (int idx = 0; idx < rawLocalizedPurposesArray.length(); idx++) {
                        JSONObject rawLocalizedPurpose = (JSONObject) rawLocalizedPurposesArray.get(idx);

                        if (rawLocalizedPurpose.getInt(JSONKey.Purposes.ID) == id) {
                            try {
                                name = rawLocalizedPurpose.getString(JSONKey.Purposes.NAME);
                            } catch (Exception e) {
                                // do nothing
                            }

                            try {
                                description = rawLocalizedPurpose.getString(JSONKey.Purposes.DESCRIPTION);
                            } catch (Exception e) {
                                // do nothing
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }

            if ( subPurposesArray != null) {
                try {
                    for (int idx = 0; idx < subPurposesArray.length(); idx++) {
                        JSONObject subPurpose = (JSONObject) subPurposesArray.get(idx);

                        if (subPurpose.getInt(JSONKey.Purposes.ID) == id) {
                            purposes.add(new Purpose(id, name, description));
                            break;
                        }
                    }
                } catch (Exception e) {
                    // if unable to parse purpose JSON, accept purpose by default
                    purposes.add(new Purpose(id, name, description));
                }
            } else {
                purposes.add(new Purpose(id, name, description));
            }
        }

        return purposes;
    }

    /**
     * Parse a collection of features.
     *
     * @param rawFeaturesArray A collection of purposes in JSON format.
     * @return An ArrayList of features, or throw an exception if the JSON is invalid.
     * @throws JSONException if JSON is invalid.
     */
    static private ArrayList<Feature> parseFeatures(@NonNull JSONArray rawFeaturesArray, @Nullable JSONArray rawLocalizedFeaturesArray, @Nullable JSONArray subFeaturesArray) throws JSONException {
        ArrayList<Feature> features = new ArrayList<>();

        for (int i = 0; i < rawFeaturesArray.length(); i++) {
            JSONObject rawFeature = (JSONObject) rawFeaturesArray.get(i);

            int id = rawFeature.getInt(JSONKey.Features.ID);
            String name = rawFeature.getString(JSONKey.Features.NAME);
            String description = rawFeature.getString(JSONKey.Features.DESCRIPTION);

            if (rawLocalizedFeaturesArray != null) {
                try {
                    // try to get the localization. Do not throw exception if something is missing.
                    for (int idx = 0; idx < rawLocalizedFeaturesArray.length(); idx++) {
                        JSONObject rawLocalizedFeature = (JSONObject) rawLocalizedFeaturesArray.get(idx);

                        if (rawLocalizedFeature.getInt(JSONKey.Features.ID) == id) {
                            try {
                                name = rawLocalizedFeature.getString(JSONKey.Features.NAME);
                            } catch (Exception e) {
                                // do nothing
                            }

                            try {
                                description = rawLocalizedFeature.getString(JSONKey.Features.DESCRIPTION);
                            } catch (Exception e) {
                                // do nothing
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }

            if ( subFeaturesArray != null) {
                try {
                    for (int idx = 0; idx < subFeaturesArray.length(); idx++) {
                        JSONObject subFeature = (JSONObject) subFeaturesArray.get(idx);

                        if (subFeature.getInt(JSONKey.Features.ID) == id) {
                            features.add(new Feature(id, name, description));
                            break;
                        }
                    }
                } catch (Exception e) {
                    // if unable to parse feature JSON, accept feature by default
                    features.add(new Feature(id, name, description));
                }
            } else {
                features.add(new Feature(id, name, description));
            }
        }

        return features;
    }

    /**
     * Parse a collection of vendors.
     *
     * @param rawVendorsArray A collection of vendors in JSON format.
     * @param subVendorsArray A collection of id of vendors in JSON format.
     * @return An ArrayList of vendors, or throw an exception if the JSON is invalid.
     * @throws JSONException if JSON is invalid.
     */
    static private ArrayList<Vendor> parseVendors(@NonNull JSONArray rawVendorsArray, @Nullable JSONArray subVendorsArray) throws JSONException {
        ArrayList<Vendor> vendors = new ArrayList<>();
        ArrayList<Integer> whitelistedVendor = new ArrayList<>();

        if (subVendorsArray != null){
            for (int i = 0; i < subVendorsArray.length(); i++) {
                whitelistedVendor.add(Integer.valueOf(((JSONObject)subVendorsArray.get(Integer.valueOf(i))).getInt("id")));
            }
        }

        for (int i = 0; i < rawVendorsArray.length(); i++) {
            JSONObject rawVendor = (JSONObject) rawVendorsArray.get(i);

            int id = rawVendor.getInt(JSONKey.Vendors.ID);
            if (subVendorsArray != null) {
                if (!whitelistedVendor.contains(Integer.valueOf(id))) {
                    continue;
                }
            }
            String name = rawVendor.getString(JSONKey.Vendors.NAME);

            String policyURLString = rawVendor.getString(JSONKey.Vendors.POLICY_URL);
            URL policyURL = null;
            try {
                policyURL = new URL(policyURLString);
            } catch (MalformedURLException e) {
                // The privacy policy URL is optional, no need to throw exception if the URL is malformed.
            }

            JSONArray purposesJSON = rawVendor.getJSONArray(JSONKey.Vendors.PURPOSE_IDS);
            ArrayList<Integer> purposes = new ArrayList<>();
            ArrayList<Integer> subPurposes = new ArrayList<>();

            //Experimental support of the version 1.1 of pubvendor.json
            if (subVendorsArray != null) {
                JSONArray subPurposesArray = ((JSONObject)subVendorsArray.get(Integer.valueOf(i))).getJSONArray(JSONKey.Vendors.PURPOSE_IDS);
                for (int j = 0; j < subPurposesArray.length(); j++) {
                    subPurposes.add(Integer.valueOf(subPurposesArray.getInt(j)));
                }
            }
            for (int idx = 0; idx < purposesJSON.length(); idx++) {
                if (subPurposes.size() > 0) {
                    if (subPurposes.contains(idx)) {
                        purposes.add(purposesJSON.getInt(idx));
                    }
                } else {
                    purposes.add(purposesJSON.getInt(idx));
                }
            }

            JSONArray legPurposesJSON = rawVendor.getJSONArray(JSONKey.Vendors.LEGITIMATE_PURPOSE_IDS);
            ArrayList<Integer> legPurposes = new ArrayList<>();
            for (int idx = 0; idx < legPurposesJSON.length(); idx++) {
                legPurposes.add(legPurposesJSON.getInt(idx));
            }

            JSONArray featuresJSON = rawVendor.getJSONArray(JSONKey.Vendors.FEATURE_IDS);
            ArrayList<Integer> features = new ArrayList<>();
            for (int idx = 0; idx < featuresJSON.length(); idx++) {
                features.add(featuresJSON.getInt(idx));
            }

            Date deletedDate = null;
            try {
                deletedDate = DateUtils.dateFromString(rawVendor.getString(JSONKey.Vendors.DELETED_DATE));
            } catch (JSONException e) {
                // deletedDate can be undefined. No need to throw an exception.
            }

            vendors.add(new Vendor(id, name, purposes, legPurposes, features, policyURL, deletedDate));
        }

        return vendors;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VendorList that = (VendorList) o;

        if (version != that.version) return false;
        if (!lastUpdated.equals(that.lastUpdated)) return false;
        if (!purposes.equals(that.purposes)) return false;
        if (!features.equals(that.features)) return false;
        return vendors.equals(that.vendors);
    }

    @Override
    public int hashCode() {
        int result = version;
        result = 31 * result + lastUpdated.hashCode();
        result = 31 * result + purposes.hashCode();
        result = 31 * result + features.hashCode();
        result = 31 * result + vendors.hashCode();
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.version);
        dest.writeLong(this.lastUpdated.getTime());
        dest.writeTypedList(this.purposes);
        dest.writeTypedList(this.features);
        dest.writeTypedList(this.vendors);
    }

    protected VendorList(Parcel in) {
        this.version = in.readInt();
        long tmpLastUpdated = in.readLong();
        this.lastUpdated = new Date(tmpLastUpdated);
        this.purposes = in.createTypedArrayList(Purpose.CREATOR);
        this.features = in.createTypedArrayList(Feature.CREATOR);
        this.vendors = in.createTypedArrayList(Vendor.CREATOR);
    }

    public static final Creator<VendorList> CREATOR = new Creator<VendorList>() {
        @Override
        public VendorList createFromParcel(Parcel source) {
            return new VendorList(source);
        }

        @Override
        public VendorList[] newArray(int size) {
            return new VendorList[size];
        }
    };
}
