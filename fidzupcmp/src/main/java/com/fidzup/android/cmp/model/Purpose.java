package com.fidzup.android.cmp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * Representation of a purpose.
 */

@SuppressWarnings("WeakerAccess")
public class Purpose implements Parcelable {

    // The id of the purpose.
    private int id;

    // The name of the purpose.
    @NonNull
    private String name;

    // The description of the purpose.
    @NonNull
    private String description;

    /**
     * Initialize a new instance of Purpose.
     *
     * @param id          The id of the purpose.
     * @param name        The name of the purpose.
     * @param description The description of the purpose.
     */
    public Purpose(int id, @NonNull String name, @NonNull String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * @return The id of the purpose.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The name of the purpose.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Set the name of the purpose.
     *
     * @param name A string that must be used for the name of the purpose.
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }

    /**
     * @return The description of the purpose.
     */
    @NonNull
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the purpose.
     *
     * @param description A string that must be used for the description of the purpose.
     */
    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Purpose purpose = (Purpose) o;

        if (id != purpose.id) return false;
        if (!name.equals(purpose.name)) return false;
        return description.equals(purpose.description);
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

    protected Purpose(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Purpose> CREATOR = new Parcelable.Creator<Purpose>() {
        @Override
        public Purpose createFromParcel(Parcel source) {
            return new Purpose(source);
        }

        @Override
        public Purpose[] newArray(int size) {
            return new Purpose[size];
        }
    };
}
