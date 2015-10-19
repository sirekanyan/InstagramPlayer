package com.sirekanyan.instagram;

import java.util.HashMap;
import java.util.Map;

/**
 * User: sirekanyan, date: 10/18/15
 */
public class MetaProperties {

    public final Map<String, String> properties;

    public static final String TITLE = "og:title";
    public static final String IMAGE = "og:image";
    public static final String VIDEO = "og:video:secure_url";

    public MetaProperties() {
        properties = new HashMap<>();
    }

    public void addMeta(String key, String content) {
        properties.put(key, content);
    }

    public String getTitle() {
        return properties.get(TITLE);
    }

    public String getImage() {
        return properties.get(IMAGE);
    }

    public String getVideo() {
        return properties.get(VIDEO);
    }
}
