package com.melodicloud.util;

/**
 * Created by Salem on 29-Apr-15.
 */
public class FileNameUtil {

    private static final String ReservedChars = "[|\\?*<\":>+/']";

    public static String sanitizeFileName(String filename) {

        //for (char c : ReservedChars.toCharArray()) {
        filename = filename.replaceAll(ReservedChars, "_");
        // }

        return filename;
    }
}

