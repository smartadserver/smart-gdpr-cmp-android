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
import java.util.Arrays;
import java.util.Date;



/**
 * Representation of an editor.
 */

@SuppressWarnings({"WeakerAccess"})
public class Editor implements Parcelable {

    private static class JSONKey {
        static public final String EDITOR_VERSION              = "editorVersion";
        static public final String LAST_UPDATED                = "lastUpdated";

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
    }

    // The editor version.
    private int version;

    // The date of the last editor update.
    @NonNull
    private Date lastUpdated;

    // The id of the editor if applicable (0 otherwise).
    private int id;

    // The name of the editor.
    @NonNull
    private String name;

    // The list of purposes related to this editor.
    @NonNull
    private ArrayList<Purpose> purposes;

    // The list of legitimate (aka non-consentable) purposes related to this editor.
    @NonNull
    private ArrayList<Integer> legitimatePurposes;

    // The list of features related to this editor.
    @NonNull
    private ArrayList<Feature> features;

    // The privacy policy's URL for this editor.
    @Nullable
    private URL policyURL;

    /**
     * Initialize a new instance of Editor.
     *
     * @param name               The name of the editor.
     * @param purposes           The list of purposes related to this editor.
     * @param legitimatePurposes The list of legitimate (aka non-consentable) purposes related to this editor.
     * @param features           The list of features related to this editor.
     * @param policyURL          The privacy policy's URL for this vendor.
     */
    public Editor(@NonNull String name, @NonNull ArrayList<Purpose> purposes, @NonNull ArrayList<Integer> legitimatePurposes, @NonNull ArrayList<Feature> features, @Nullable URL policyURL) {
        this.id = 0;
        this.name = name;
        this.purposes = purposes;
        this.legitimatePurposes = legitimatePurposes;
        this.features = features;
        this.policyURL = policyURL;
    }

    /**
     * Initialize an editor from an editor JSON (if valid).
     *
     * @param JSON The data representation of the editor JSON.
     * @throws JSONException is JSON is invalid.
     */
    public Editor(@NonNull JSONObject JSON) throws JSONException, MalformedURLException {
        this(JSON, null);
    }

    /**
     * Initialize a localized editor from an editor JSON and a localized editor JSON.
     *
     * @param JSON          The data representation of the editor JSON.
     * @param localizedJSON The data representation of the localized editor JSON.
     */
    public Editor(@NonNull JSONObject JSON, @Nullable JSONObject localizedJSON) throws JSONException {
        version = JSON.getInt(Editor.JSONKey.EDITOR_VERSION);

        Date lastUpdated = DateUtils.dateFromString(JSON.getString(Editor.JSONKey.LAST_UPDATED));
        if (lastUpdated == null) {
            throw new JSONException("lastUpdated date format invalid.");
        }
        this.lastUpdated = lastUpdated;

        JSONArray rawLocalizedPurposesArray = null;
        JSONArray rawLocalizedFeaturesArray = null;

        if (localizedJSON != null) {
            try {
                rawLocalizedPurposesArray = localizedJSON.getJSONArray(Editor.JSONKey.Purposes.PURPOSES);
            } catch (JSONException e) {
                // do nothing
            }

            try {
                rawLocalizedFeaturesArray = localizedJSON.getJSONArray(Editor.JSONKey.Features.FEATURES);
            } catch (JSONException e) {
                // do nothing
            }
        }

        purposes = parsePurposes(JSON.getJSONArray(Editor.JSONKey.Purposes.PURPOSES), rawLocalizedPurposesArray);
        features = parseFeatures(JSON.getJSONArray(Editor.JSONKey.Features.FEATURES), rawLocalizedFeaturesArray);
    }

    /**
     * Parse a collection of purposes.
     *
     * @param rawPurposesArray A collection of purposes in JSON format.
     * @return An ArrayList of purposes, or throw an exception if the JSON is invalid.
     * @throws JSONException if JSON is invalid.
     */
    static private ArrayList<Purpose> parsePurposes(@NonNull JSONArray rawPurposesArray, @Nullable JSONArray rawLocalizedPurposesArray) throws JSONException {
        ArrayList<Purpose> purposes = new ArrayList<>();

        for (int i = 0; i < rawPurposesArray.length(); i++) {
            JSONObject rawPurpose = (JSONObject) rawPurposesArray.get(i);

            int id = rawPurpose.getInt(Editor.JSONKey.Purposes.ID);
            String name = rawPurpose.getString(Editor.JSONKey.Purposes.NAME);
            String description = rawPurpose.getString(Editor.JSONKey.Purposes.DESCRIPTION);

            if (rawLocalizedPurposesArray != null) {
                try {
                    // try to get the localization. Do not throw exception if something is missing.
                    for (int idx = 0; idx < rawLocalizedPurposesArray.length(); idx++) {
                        JSONObject rawLocalizedPurpose = (JSONObject) rawLocalizedPurposesArray.get(idx);

                        if (rawLocalizedPurpose.getInt(Editor.JSONKey.Purposes.ID) == id) {
                            try {
                                name = rawLocalizedPurpose.getString(Editor.JSONKey.Purposes.NAME);
                            } catch (Exception e) {
                                // do nothing
                            }

                            try {
                                description = rawLocalizedPurpose.getString(Editor.JSONKey.Purposes.DESCRIPTION);
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

            purposes.add(new Purpose(id, name, description));
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
    static private ArrayList<Feature> parseFeatures(@NonNull JSONArray rawFeaturesArray, @Nullable JSONArray rawLocalizedFeaturesArray) throws JSONException {
        ArrayList<Feature> features = new ArrayList<>();

        for (int i = 0; i < rawFeaturesArray.length(); i++) {
            JSONObject rawFeature = (JSONObject) rawFeaturesArray.get(i);

            int id = rawFeature.getInt(Editor.JSONKey.Features.ID);
            String name = rawFeature.getString(Editor.JSONKey.Features.NAME);
            String description = rawFeature.getString(Editor.JSONKey.Features.DESCRIPTION);

            if (rawLocalizedFeaturesArray != null) {
                try {
                    // try to get the localization. Do not throw exception if something is missing.
                    for (int idx = 0; idx < rawLocalizedFeaturesArray.length(); idx++) {
                        JSONObject rawLocalizedFeature = (JSONObject) rawLocalizedFeaturesArray.get(idx);

                        if (rawLocalizedFeature.getInt(Editor.JSONKey.Features.ID) == id) {
                            try {
                                name = rawLocalizedFeature.getString(Editor.JSONKey.Features.NAME);
                            } catch (Exception e) {
                                // do nothing
                            }

                            try {
                                description = rawLocalizedFeature.getString(Editor.JSONKey.Features.DESCRIPTION);
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

            features.add(new Feature(id, name, description));
        }

        return features;
    }


    /**
     * @return The id of the editor.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The name of the editor.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Set the name of the editor.
     *
     * @param name A string that must be used for the name of the editor.
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * @return An ArrayList of the editor's purposes.
     */
    @NonNull
    public ArrayList<Purpose> getPurposes() {
        return purposes;
    }

    /**
     * Set a new ArrayList of purposes for the editor.
     *
     * @param purposes An ArrayList of purposes.
     */
    public void setPurposes(@NonNull ArrayList<Purpose> purposes) {
        this.purposes = purposes;
    }

    /**
     * @return An ArrayList of the editor's legitimate purposes.
     */
    @NonNull
    public ArrayList<Integer> getLegitimatePurposes() {
        return legitimatePurposes;
    }

    /**
     * Set a new ArrayList of purposes for the editor's legitimate purposes.
     *
     * @param legitimatePurposes An ArrayList of purposes.
     */
    @SuppressWarnings("unused")
    public void setLegitimatePurposes(@NonNull ArrayList<Integer> legitimatePurposes) {
        this.legitimatePurposes = legitimatePurposes;
    }

    /**
     * @return The version of the editor
     */
    @NonNull
    public int getVersion() {
        return version;
    }

    /**
     * @return An ArrayList of editor's features.
     */
    @NonNull
    public ArrayList<Feature> getFeatures() {
        return features;
    }

    /**
     * Set a new ArrayList of features for the editor.
     *
     * @param features an ArrayList of features.
     */
    @SuppressWarnings("unused")
    public void setFeatures(@NonNull ArrayList<Feature> features) {
        this.features = features;
    }

    /**
     * @return The privacy policy URL of the editor.
     */
    @Nullable
    public URL getPolicyURL() {
        return policyURL;
    }

    /**
     * Set the privacy policy URL of the editor.
     *
     * @param policyURL The privacy policy URL.
     */
    @SuppressWarnings("unused")
    public void setPolicyURL(@Nullable URL policyURL) {
        this.policyURL = policyURL;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Editor editor = (Editor) o;

        if (id != editor.id) return false;
        if (!name.equals(editor.name)) return false;
        if (!purposes.equals(editor.purposes)) return false;
        if (!legitimatePurposes.equals(editor.legitimatePurposes)) return false;
        if (!features.equals(editor.features)) return false;
        if (policyURL != null ? !policyURL.equals(editor.policyURL) : editor.policyURL != null) {
            return false;
        }
        return true;
    }

    /**
     * Set the id of the editor
     *
     * @param id  The id of the editor.
     */
    public Editor setId(int id ) {
        this.id = id;
        return this;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{id, name, purposes, legitimatePurposes, features, policyURL});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.version);
        dest.writeLong(this.lastUpdated.getTime());
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeSerializable(this.policyURL);
        dest.writeTypedList(this.purposes);
        dest.writeTypedList(this.features);
    }

    protected Editor(Parcel in) {
        this.version = in.readInt();
        long tmpLastUpdated = in.readLong();
        this.id = in.readInt();
        this.name = in.readString();
        this.policyURL = (URL) in.readSerializable();
        this.lastUpdated = new Date(tmpLastUpdated);
        this.purposes = in.createTypedArrayList(Purpose.CREATOR);
        this.features = in.createTypedArrayList(Feature.CREATOR);
    }

    public static final Creator<Editor> CREATOR = new Creator<Editor>() {
        @Override
        public Editor createFromParcel(Parcel source) {
            return new Editor(source);
        }

        @Override
        public Editor[] newArray(int size) {
            return new Editor[size];
        }
    };
}
