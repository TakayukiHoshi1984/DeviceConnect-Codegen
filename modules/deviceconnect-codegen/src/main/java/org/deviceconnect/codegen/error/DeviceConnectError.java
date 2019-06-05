package org.deviceconnect.codegen.error;


import java.io.File;

abstract class DeviceConnectError extends CodegenError {

    private final String mDescriptionId;

    private final File mFile;

    private final String mApiPath;

    DeviceConnectError(final String descriptionId,
                       final File file,
                       final String apiPath) {
        mDescriptionId = descriptionId;
        mFile = file;
        mApiPath = apiPath;
    }

    String description() {
        return mDescriptionId;
    }

    File filePath() {
        return mFile;
    }

    String apiPath() {
        return mApiPath;
    }

    @Override
    String getMessage() {
        ErrorFormat format = new ErrorFormat("deviceConnect");
        format.addProp("description", description());
        format.addProp("filePath", filePath().getAbsolutePath());
        format.addProp("apiPath", apiPath());
        return format.format();
    }
}
