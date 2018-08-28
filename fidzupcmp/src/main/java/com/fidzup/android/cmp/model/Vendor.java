package com.fidzup.android.cmp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Representation of a vendor.
 */

@SuppressWarnings({"WeakerAccess"})
public class Vendor implements Parcelable {

    // The id of the vendor.
    private int id;

    // The name of the vendor.
    @NonNull
    private String name;

    // The list of purposes related to this vendor.
    @NonNull
    private ArrayList<Integer> purposes;

    // The list of legitimate (aka non-consentable) purposes related to this vendor.
    @NonNull
    private ArrayList<Integer> legitimatePurposes;

    // The list of features related to this vendor.
    @NonNull
    private ArrayList<Integer> features;

    // The privacy policy's URL for this vendor.
    @Nullable
    private URL policyURL;

    // A date of deletion when this vendor has been marked as deleted, null otherwise.
    @Nullable
    private Date deletedDate;

    /**
     * Initialize a new instance of Vendor.
     *
     * @param id                 The id of the vendor.
     * @param name               The name of the vendor.
     * @param purposes           The list of purposes related to this vendor.
     * @param legitimatePurposes The list of legitimate (aka non-consentable) purposes related to this vendor.
     * @param features           The list of features related to this vendor.
     * @param policyURL          The privacy policy's URL for this vendor.
     * @param deletedDate        A date of deletion of this vendor, null otherwise.
     */
    public Vendor(int id, @NonNull String name, @NonNull ArrayList<Integer> purposes, @NonNull ArrayList<Integer> legitimatePurposes, @NonNull ArrayList<Integer> features, @Nullable URL policyURL, @Nullable Date deletedDate) {
        this.id = id;
        this.name = name;
        this.purposes = purposes;
        this.legitimatePurposes = legitimatePurposes;
        this.features = features;
        this.policyURL = policyURL;
        this.deletedDate = deletedDate;
    }

    /**
     * @return The id of the vendor.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The name of the vendor.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Set the name of the vendor.
     *
     * @param name A string that must be used for the name of the vendor.
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * @return An ArrayList of the vendor's purposes.
     */
    @NonNull
    public ArrayList<Integer> getPurposes() {
        return purposes;
    }

    /**
     * Set a new ArrayList of purposes for the vendor.
     *
     * @param purposes An ArrayList of purposes.
     */
    public void setPurposes(@NonNull ArrayList<Integer> purposes) {
        this.purposes = purposes;
    }

    /**
     * @return An ArrayList of the vendor's legitimate purposes.
     */
    @NonNull
    public ArrayList<Integer> getLegitimatePurposes() {
        return legitimatePurposes;
    }

    /**
     * Set a new ArrayList of purposes for the vendor's legitimate purposes.
     *
     * @param legitimatePurposes An ArrayList of purposes.
     */
    @SuppressWarnings("unused")
    public void setLegitimatePurposes(@NonNull ArrayList<Integer> legitimatePurposes) {
        this.legitimatePurposes = legitimatePurposes;
    }

    /**
     * @return An ArrayList of vendor's features.
     */
    @NonNull
    public ArrayList<Integer> getFeatures() {
        return features;
    }

    /**
     * Set a new ArrayList of features for the vendor.
     *
     * @param features an ArrayList of features.
     */
    @SuppressWarnings("unused")
    public void setFeatures(@NonNull ArrayList<Integer> features) {
        this.features = features;
    }

    /**
     * @return The privacy policy URL of the vendor.
     */
    @Nullable
    public URL getPolicyURL() {
        return policyURL;
    }

    /**
     * Set the privacy policy URL of the vendor.
     *
     * @param policyURL The privacy policy URL.
     */
    @SuppressWarnings("unused")
    public void setPolicyURL(@Nullable URL policyURL) {
        this.policyURL = policyURL;
    }

    /**
     * @return The Date of deletion of the vendor if any, null otherwise.
     */
    @SuppressWarnings("unused")
    @Nullable
    public Date getDeletedDate() {
        return deletedDate;
    }

    /**
     * @return true if the vendor is activated (ie not deleted), false otherwise.
     */
    public boolean isActivated() {
        return deletedDate == null;
    }

    /**
     * Set the Date of deletion of the vendor.
     *
     * @param deletedDate The date of deletion.
     */
    @SuppressWarnings("unused")
    public void setDeletedDate(@Nullable Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vendor vendor = (Vendor) o;

        if (id != vendor.id) return false;
        if (!name.equals(vendor.name)) return false;
        if (!purposes.equals(vendor.purposes)) return false;
        if (!legitimatePurposes.equals(vendor.legitimatePurposes)) return false;
        if (!features.equals(vendor.features)) return false;
        if (policyURL != null ? !policyURL.equals(vendor.policyURL) : vendor.policyURL != null) {
            return false;
        }
        return deletedDate != null ? deletedDate.equals(vendor.deletedDate) : vendor.deletedDate == null;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{id, name, purposes, legitimatePurposes, features, policyURL, deletedDate});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeList(this.purposes);
        dest.writeList(this.legitimatePurposes);
        dest.writeList(this.features);
        dest.writeSerializable(this.policyURL);
        dest.writeLong(this.deletedDate != null ? this.deletedDate.getTime() : -1);
    }

    protected Vendor(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.purposes = new ArrayList<>();
        in.readList(this.purposes, Integer.class.getClassLoader());
        this.legitimatePurposes = new ArrayList<>();
        in.readList(this.legitimatePurposes, Integer.class.getClassLoader());
        this.features = new ArrayList<>();
        in.readList(this.features, Integer.class.getClassLoader());
        this.policyURL = (URL) in.readSerializable();
        long tmpDeletedDate = in.readLong();
        this.deletedDate = tmpDeletedDate == -1 ? null : new Date(tmpDeletedDate);
    }

    public static final Creator<Vendor> CREATOR = new Creator<Vendor>() {
        @Override
        public Vendor createFromParcel(Parcel source) {
            return new Vendor(source);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[size];
        }
    };
}
