package org.deviceconnect.codegen.error;


import org.deviceconnect.codegen.util.ResourceBundleWithUtf8;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

class ErrorFormat {

    private final String mFormat;

    private final Map<String, String> mProperties = new HashMap<>();

    ErrorFormat(final String key) {
        mFormat = getErrorFormats().getString(key);
    }

    ResourceBundle getErrorFormats() {
        return ResourceBundle.getBundle("ErrorFormat",
                Locale.getDefault(),
                ResourceBundleWithUtf8.UTF8_ENCODING_CONTROL);
    }

    void addProp(final String key, final int value) {
        addProp(key, Integer.toString(value));
    }

    void addProp(final String key, final String value) {
        mProperties.put("{{" + key + "}}", value);
    }

    String format() {
        String message = mFormat;
        for (Map.Entry<String, String> e : mProperties.entrySet()) {
            message = message.replace(e.getKey(), e.getValue());
        }
        return message;
    }
}
