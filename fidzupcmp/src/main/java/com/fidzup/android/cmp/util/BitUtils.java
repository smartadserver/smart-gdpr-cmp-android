package com.fidzup.android.cmp.util;

import android.support.annotation.NonNull;

import com.fidzup.android.cmp.model.Language;

import java.util.Date;

/**
 * Util class to manipulate string of bits.
 */

@SuppressWarnings("WeakerAccess")
public class BitUtils {

    static private int LANGUAGE_LETTER_BIT_ENCODING_LENGTH = 6;

    /**
     * Return a "0" left padded version of the string.
     *
     * @param padding Number of "0" character that must be added.
     * @param bits    The bit string that needs to be modified.
     * @return A left padded string.
     */
    static public String leftPadding(int padding, @NonNull String bits) {
        for (int i = 0; i < padding; i++) {
            bits = "0".concat(bits);
        }
        return bits;
    }

    /**
     * Return a "0" right padded version of the string.
     *
     * @param padding number of "0" character that must be added.
     * @param bits    The bit string that needs to be modified.
     * @return A right padded string.
     */
    static public String rightPadding(int padding, @NonNull String bits) {
        for (int i = 0; i < padding; i++) {
            bits = bits.concat("0");
        }
        return bits;
    }

    /**
     * Encode a long into a bit string.
     *
     * @param number       The long that needs to be encoded.
     *                     - Precondition: number must be a positive number (or equals to 0).
     * @param numberOfBits The minimum length of the returned string.
     *                     - Precondition: numberOfBits must be a positive number (or equals to 0).
     * @return A string encoded long.
     */
    static public String longToBits(long number, int numberOfBits) {
        if (number < 0 || numberOfBits < 0) {
            return null;
        }

        String bits = Long.toBinaryString(number);
        return leftPadding(numberOfBits - bits.length(), bits);
    }

    /**
     * Decode a bit string into a long.
     *
     * @param bits The bits string that must be decoded.
     * @return A long if bits string is valid, null otherwise.
     */
    static public Long bitsToLong(@NonNull String bits) {
        try {
            return Long.parseLong(bits, 2);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Encode a boolean into a bit string.
     *
     * @param bool         The boolean that needs to be encoded.
     * @param numberOfBits The minimum length of the returned string.
     *                     - Precondition: numberOfBits must be a positive number (or equals to 0).
     * @return a string encoded boolean.
     */
    static public String boolToBits(boolean bool, int numberOfBits) {
        if (numberOfBits < 0) {
            return null;
        }

        String bits = bool ? "1" : "0";
        return leftPadding(numberOfBits - bits.length(), bits);
    }

    /**
     * decode a bit string into a boolean.
     *
     * @param bits the bit string that must be decoded.
     *             - Precondition: bit string must be exactly 1 bit long.
     * @return A boolean if bit string is valid, null otherwise.
     */
    static public Boolean bitsToBool(@NonNull String bits) {
        switch (bits) {
            case "0":
                return false;
            case "1":
                return true;
            default:
                return null;
        }
    }

    /**
     * Encode a Date into a bit string.
     *
     * @param date         The boolean that needs to be encoded.
     * @param numberOfBits The minimum length of the returned string.
     *                     - Precondition: numberOfBits must be a positive number (or equals to 0).
     * @return A string encoded Date.
     */
    static public String dateToBits(@NonNull Date date, int numberOfBits) {
        if (numberOfBits < 0) {
            return null;
        }

        long deciseconds = date.getTime() / 100;
        return longToBits(deciseconds, numberOfBits);
    }

    /**
     * Decode a bit string into a Date.
     *
     * @param bits The bit string that must be decoded.
     * @return A date if bit string is valid, null otherwise.
     */
    static public Date bitsToDate(@NonNull String bits) {
        Long deciseconds = bitsToLong(bits);
        if (deciseconds == null) {
            return null;
        }

        return new Date(deciseconds * 100);
    }

    /**
     * Encode a letter into a bit string.
     *
     * @param letter       The letter that needs to be encoded.
     *                     - Precondition: letter must be an unique and valid letter ([a-z] or [A-Z]).
     * @param numberOfBits The minimum length of the returned string.
     *                     - Precondition: numberOfBits must be a positive number (or equals to 0).
     * @return A string encoded letter.
     */
    static public String letterToBits(@NonNull String letter, int numberOfBits) {
        if (!Language.VALID_LETTERS.contains(letter.toLowerCase()) || letter.length() != 1 || numberOfBits < 0) {
            return null;
        }

        int idx = Language.VALID_LETTERS.indexOf(letter.toLowerCase());
        return longToBits(idx, numberOfBits);
    }

    /**
     * Decode a bit string into a letter.
     *
     * @param bits The bit string that must be decoded.
     * @return A letter if bit string is valid, null otherwise.
     */
    static public String bitsToLetter(@NonNull String bits) {
        Long letterIndex = bitsToLong(bits);

        if (letterIndex != null) {
            try {
                return "" + Language.VALID_LETTERS.toCharArray()[letterIndex.intValue()];
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return null;
    }

    /**
     * Encode a Language into a bit string.
     *
     * @param language     The Language that needs to be encoded.
     * @param numberOfBits The minimum length of the returned string.
     *                     - Precondition: numberOfBits must be a positive number (or equals to 0).
     * @return A string encoded Language.
     */
    static public String languageToBits(@NonNull Language language, int numberOfBits) {
        if (numberOfBits < 0) {
            return null;
        }

        String bits = "";
        for (char c : language.toString().toCharArray()) {
            String letter = letterToBits("" + c, LANGUAGE_LETTER_BIT_ENCODING_LENGTH);
            if (letter == null) {
                return null;
            }
            bits = bits.concat(letter);
        }

        return leftPadding(numberOfBits - bits.length(), bits);
    }

    /**
     * Decode a bit string into a Language.
     *
     * @param bits the bit string that must be decoded.
     * @return A Language if the bit string is valid, null otherwise.
     */
    static public Language bitsToLanguage(@NonNull String bits) throws IllegalArgumentException {
        String language = "";

        for (int idx = 0; idx < bits.length(); idx += LANGUAGE_LETTER_BIT_ENCODING_LENGTH) {
            String letter = bitsToLetter(bits.substring(idx, idx + LANGUAGE_LETTER_BIT_ENCODING_LENGTH));

            if (letter == null) {
                return null;
            }

            language = language.concat(letter);
        }

        return new Language(language);
    }
}
