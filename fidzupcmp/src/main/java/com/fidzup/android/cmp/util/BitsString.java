package com.fidzup.android.cmp.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Consent string expressed as a Base64URL string or a bit array string.
 */

public class BitsString {

    // The base64URL representation of the string.
    @NonNull
    public String stringValue;

    // The bits representation of the string.
    @NonNull
    public String bitsValue;

    /**
     * Initialize a BitsString from a bits string or a base64URL string.
     *
     * @param isBitsString A boolean that specified if the string is a bits string or a Base64URL string.
     * @param string       A valid base64URL string.
     * @throws IllegalArgumentException If the Base64 or the bits string are invalid.
     */
    public BitsString(boolean isBitsString, @NonNull String string) throws IllegalArgumentException {
        if (!isBitsString) {
            stringValue = string;

            bitsValue = Base64URLUtils.decodeString(string, true);
            bitsValue = BitUtils.leftPadding(8 - bitsValue.length(), bitsValue);

        } else {
            if (!isValidBitsString(string)) {
                throw new IllegalArgumentException("Bad bits string.");
            }

            bitsValue = string;

            // The string is right padded so it always correspond to complete bytes.
            int paddingCount = 7 - ((string.length() + 7) % 8);
            String paddedBitsString = BitUtils.rightPadding(paddingCount, string);

            // Split bits string into byte array.
            ArrayList<Integer> intArrayList = new ArrayList<>();
            for (int idx = 0; idx < paddedBitsString.length(); idx += 8) {
                String str = paddedBitsString.substring(idx, idx + 8);
                intArrayList.add(Integer.parseInt(str, 2));
            }

            byte[] byteArray = new byte[intArrayList.size()];
            for (int idx = 0; idx < intArrayList.size(); idx++) {
                byteArray[idx] = (byte) (int) intArrayList.get(idx);
            }

            // Convert it to Base64URL
            stringValue = Base64URLUtils.getBase64URL(byteArray);
        }
    }

    /**
     * Validate if a bits string is valid: ie it's only containing '0' and '1' digits.
     *
     * @param bitsString The string to be validated.
     * @return true if the string is valid, false otherwise.
     */
    public static boolean isValidBitsString(@NonNull String bitsString) {
        for (char c : bitsString.toCharArray()) {
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }
}
