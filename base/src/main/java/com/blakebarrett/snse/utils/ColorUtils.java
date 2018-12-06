package com.blakebarrett.snse.utils;

import android.graphics.Color;

public class ColorUtils {
    public static int hexToInt(final String hex) {
        if (hex.indexOf("#") != 0) {
            return Color.parseColor("#" + hex);
        }
        try {
            return Color.parseColor(hex);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String toHexString(final byte[] bytes) {
        final StringBuilder hexString = new StringBuilder("#");

        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
