package io.github.ericdriggs.reportcard.controller.html;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public enum ExtensionImage {
    CSS("css", "/image/css.svg"),
    FILE("*", "/image/file.svg"),
    FOLDER("/", "/image/file.svg"),
    HTML("html", "/image/html.svg"),
    XML("xml", "/image/xml.svg");

    private String extension;
    private String image;
    ExtensionImage(String extension, String image) {
        this.extension = extension;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    static Map<String, String> extenstionImageMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    static {
        for (ExtensionImage extensionImage :  ExtensionImage.values()) {
            extenstionImageMap.put(extensionImage.extension, extensionImage.image);
        }
    }

    public static String getImageForExtension(String extension) {
        final String image = extenstionImageMap.get(extension);
        if (image != null) {
            return image;
        }
        return FILE.image;
    }
}
