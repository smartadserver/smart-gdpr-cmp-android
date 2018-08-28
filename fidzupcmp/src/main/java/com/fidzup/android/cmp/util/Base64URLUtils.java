package com.fidzup.android.cmp.util;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.util.regex.Pattern;

/**
 * Util class to encode / decode base64url strings (without padding) & Data objects.
 */

@SuppressWarnings("WeakerAccess")
public class Base64URLUtils {

    /**
     * Create a base64URL (without padding) from string.
     *
     * @param string The string that needs to be converted.
     * @return A base64URL string without padding.
     */
    @SuppressWarnings("SameParameterValue")
    static public String getBase64URL(String string) {
        return getBase64URL(string.getBytes());
    }

    /**
     * Create a base64URL (without padding) from a byte array.
     *
     * @param data The byte array that needs to be converted.
     * @return A base64URL string without padding.
     */
    static public String getBase64URL(byte[] data) {
        String encodedString = Base64.encodeToString(data, Base64.NO_WRAP);

        return encodedString.replaceAll(Pattern.quote("+"), "-")
                .replaceAll("/", "_")
                .replaceAll("=", "");
    }

    /**
     * Decode a base64URL string without padding.
     *
     * @param base64URLString The base64URL string to be decoded.
     * @return The decoded string.
     * @throws IllegalArgumentException If the given Base64 string is invalid.
     */
    static public String decodeString(@NonNull String base64URLString, boolean isBitsString) throws IllegalArgumentException {
        base64URLString = base64URLString.replaceAll("-", "+")
                .replaceAll("_", "/");

        String string = "";
        byte[] bytes;
        bytes = Base64.decode(base64URLString, Base64.NO_WRAP);

        if (isBitsString) {
            for (byte b : bytes) {
                int i = (b & 0xFF); // using 0xFF because we want unsigned int between 0 and 256 and not signed int between -128 and 127.
                String binaryString = Integer.toBinaryString(i);
                string = string.concat(BitUtils.leftPadding(8 - binaryString.length(), binaryString));
            }
        } else {
            string = new String(bytes);
        }

        return string;
    }


}
