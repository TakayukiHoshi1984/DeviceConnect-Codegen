package org.deviceconnect.codegen.error;


import org.deviceconnect.codegen.util.ResourceBundleWithUtf8;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class CodegenError {

    String mMessage;

    String getMessageForKey(final String key) {
        ResourceBundle messages = ResourceBundle.getBundle("Messages",
                Locale.getDefault(),
                ResourceBundleWithUtf8.UTF8_ENCODING_CONTROL);
        return messages.getString(key);
    }

    String getMessage() {
        return mMessage;
    }

    void setMessage(final String message) {
        mMessage = message;
    }

}
