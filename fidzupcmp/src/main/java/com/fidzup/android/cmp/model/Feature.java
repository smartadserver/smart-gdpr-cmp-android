package com.fidzup.android.cmp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * Representation of a feature.
 */

@SuppressWarnings("WeakerAccess")
public class Feature implements Parcelable {

    // The id of the feature.
    private int id;

    // The name of the feature.
    @NonNull
    private String name;

    // The description of the feature.
    @NonNull
    private String description;

    /**
     * Initialize a new instance of Feature.
     *
     * @param id          The id of the feature.
     * @param name        The name of the feature.
     * @param description The description of the feature.
     */
    public Feature(int id, @NonNull String name, @NonNull String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * @return The id of the feature.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The name of the feature.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Set the name of the feature.
     *
     * @param name A string that must be used for the name of the feature.
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * @return The description of the feature.
     */
    @NonNull
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the feature.
     *
     * @param description A string that must be used for the description of the feature.
     */
    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;

        if (id != feature.id) return false;
        if (!name.equals(feature.name)) return false;
        return description.equals(feature.description);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{id, name, description});
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
    }

    protected Feature(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Feature> CREATOR = new Parcelable.Creator<Feature>() {
        @Override
        public Feature createFromParcel(Parcel source) {
            return new Feature(source);
        }

        @Override
        public Feature[] newArray(int size) {
            return new Feature[size];
        }
    };
}
