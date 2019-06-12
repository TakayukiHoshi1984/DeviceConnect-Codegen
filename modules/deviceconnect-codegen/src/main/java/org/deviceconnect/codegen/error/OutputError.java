package org.deviceconnect.codegen.error;


import java.io.File;

public abstract class OutputError extends CodegenError {

    private final String mDescriptionId;

    private final File mOutputDir;

    OutputError(final String descriptionId,
                final File outputDir) {
        mDescriptionId = descriptionId;
        mOutputDir = outputDir;
    }

    String description() {
        return mDescriptionId;
    }

    File outputDir() {
        return mOutputDir;
    }

    @Override
    String getMessage() {
        ErrorFormat format = new ErrorFormat("output");
        format.addProp("description", getMessageForKey(description()));
        format.addProp("outputDir", outputDir().getAbsolutePath());
        return format.format();
    }
}
