package com.smartadserver.android.smartcmp.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * ISO 639-1 language representation for the ConsentString
 */
public class Language implements Parcelable {

    // The list of valid letters for the language string.
    static public final String VALID_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    // The valid length for the language string.
    static private final int VALID_LENGTH = 2;

    // The string representation of the Language instance
    private String string;

    /**
     * Initialize a new instance of Language from a string representation.
     *
     * @param string The string representation of the language (it must be ISO 639-1 compliant).
     * @throws IllegalArgumentException if the given String is not ISO 639-1.
     */
    public Language(@NonNull String string) throws IllegalArgumentException {

        // The language string must be ISO-639-1 compliant, aka:
        // - two characters long
        // - using only letters between A and Z (no special characters).

        int invalidCharactersCount = 0;
        string = string.toLowerCase();
        for (char c : string.toCharArray()) {
            if (!VALID_LETTERS.contains("" + c)) {
                invalidCharactersCount++;
            }
        }

        if (string.length() != VALID_LENGTH || invalidCharactersCount > 0) {
            throw new IllegalArgumentException("Bad language naming.");
        }

        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    /**
     * A static method that will return the language used by default (en).
     *
     * @return The default language (En).
     */
    static public Language getDefaultLanguage() {
        return new Language("en");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        return string != null ? string.equals(language.string) : language.string == null;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{string});
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
        dest.writeString(this.string);
    }

    protected Language(Parcel in) {
        this.string = in.readString();
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel source) {
            return new Language(source);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
}
