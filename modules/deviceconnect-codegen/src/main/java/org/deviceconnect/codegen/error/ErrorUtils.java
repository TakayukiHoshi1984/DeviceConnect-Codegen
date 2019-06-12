package org.deviceconnect.codegen.error;


import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.deviceconnect.codegen.IllegalPathFormatException;

import java.io.File;

public class ErrorUtils {

    public static void reportError(final File file, final IllegalPathFormatException e) {
        CodegenError error;
        switch (e.getReason()) {
            case TOO_SHORT:
            case TOO_LONG:
                error = new Errors.InvalidPathLength(file, e.getPath());
                break;
            case NOT_STARTED_WITH_ROOT:
                error = new Errors.InvalidPathNotStartedWithRoot(file, e.getPath());
                break;
            default:
                throw new IllegalArgumentException();
        }
        reportError(error);
    }

    public static void reportError(final org.apache.commons.cli.ParseException e) {
        CodegenError error;
        if (e instanceof MissingOptionException) {
            error = new Errors.MissingOption((MissingOptionException) e);
        } else if (e instanceof MissingArgumentException) {
            error = new Errors.MissingArgument((MissingArgumentException) e);
        } else if (e instanceof AlreadySelectedException) {
            error = new Errors.AlreadySelectedOption((AlreadySelectedException) e);
        } else if (e instanceof UnrecognizedOptionException) {
            error = new Errors.UnrecognizedOption((UnrecognizedOptionException) e);
        } else {
            error = null;
        }
        reportError(error);
    }

    public static void reportUnknownError(final Exception e) {

    }

    public static void reportError(final CodegenError error) {
        System.err.println(error.getMessage());
    }

    private ErrorUtils() {}
}
