package org.deviceconnect.codegen.error;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.cli.*;
import org.deviceconnect.codegen.Const;
import org.deviceconnect.codegen.util.SwaggerJsonValidator;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public final class Errors {

    public static class MissingOption extends CodegenError {

        MissingOption(final MissingOptionException e) {
            String message = getMessageForKey("errorMissingOption");
            List<Object> missingOptions = e.getMissingOptions();
            mMessage = message + ": " + concatOptions(missingOptions);
        }

        private static String concatOptions(List<Object> list) {
            String result = "";
            for (int i = 0; i < list.size(); i++) {
                Object opt = list.get(i);
                if (opt == null) {
                    continue;
                }

                result += " - ";
                if (opt instanceof OptionGroup) {
                    OptionGroup group = (OptionGroup) opt;
                    String groupStr = "";
                    for (Iterator it = group.getOptions().iterator(); it.hasNext(); ) {
                        Option o = (Option) it.next();
                        groupStr += o.getLongOpt();
                        if (it.hasNext()) {
                            groupStr += " or ";
                        }
                    }
                    result += groupStr + "\n";
                } else {
                    Option found = Const.OPTIONS.getOption(opt.toString());
                    if (found != null) {
                        result += found.getLongOpt() + "\n";
                    }
                }
            }
            return result;
        }
    }

    public static class MissingArgument extends CodegenError {

        public MissingArgument(final MissingArgumentException e) {
            String message = getMessageForKey("errorMissingArgument");
            Option option = e.getOption();
            mMessage = message + ": " + option.getLongOpt();
        }
    }

    public static class AlreadySelectedOption extends CodegenError {

        public AlreadySelectedOption(final AlreadySelectedException e) {
            String message = getMessageForKey("errorAlreadySelectedOption");
            Option option = e.getOption();
            mMessage = message + ": " + option.getLongOpt();
        }
    }


    public static class UnrecognizedOption extends CodegenError {

        public UnrecognizedOption(final UnrecognizedOptionException e) {
            String message = getMessageForKey("errorUndefinedOption");
            String optionName = e.getOption();
            mMessage = message + ": " + optionName;
        }
    }

    public static class InvalidJson extends CodegenError {
        public InvalidJson(final File file, final JsonProcessingException e) {
            ErrorFormat format = new ErrorFormat("json");
            format.addProp("description", getMessageForKey("errorInvalidJson"));
            format.addProp("filePath", file.getAbsolutePath());
            format.addProp("lineNumber", e.getLocation().getLineNr());
            format.addProp("jsonPointer", e.getLocation().getSourceRef().toString());
            setMessage(format.format());
        }
    }

    public static class InvalidSwagger extends CodegenError {
        public InvalidSwagger(final File file, final List<SwaggerJsonValidator.Error> errors) {
            String message = getMessageForKey("errorInvalidSwagger");
            String errorMessage = message.replace("{{file}}", file.getName());
            String reasons = "";
            for (SwaggerJsonValidator.Error error : errors) {
                String pointer = error.getJsonPointer();
                String reason = error.getMessage();
                reasons += " - Pointer = " + pointer + ", Reason = " + reason + "\n";
            }
            mMessage = errorMessage + ": \n" + reasons;
        }
    }

    public static class InvalidMethod extends DeviceConnectError {
        public InvalidMethod(final File file, final String apiPath) {
            super("errorInvalidMethod", file, apiPath);
        }
    }

    public static class DuplicatedPath extends DeviceConnectError {
        public DuplicatedPath(final File file, final String apiPath) {
            super("errorDuplicatedPath", file, apiPath);
        }
    }

    public static class InvalidPathLength extends DeviceConnectError {
        public InvalidPathLength(final File file, final String apiPath) {
            super("errorInvalidPathLength", file, apiPath);
        }
    }

    public static class InvalidPathNotStartedWithRoot extends DeviceConnectError {
        public InvalidPathNotStartedWithRoot(final File file, final String apiPath) {
            super("errorInvalidPathNotStartedWithRoot", file, apiPath);
        }
    }

    public static class MissingEvent extends DeviceConnectError {
        public MissingEvent(final File file, final String apiPath) {
            super("errorMissingEvent", file, apiPath);
        }
    }

    public static class MissingOperationId extends DeviceConnectError {
        public MissingOperationId(final File file, final String apiPath) {
            super("errorMissingOperationId", file, apiPath);
        }
    }

    public static class MissingOperationType extends DeviceConnectError {
        public MissingOperationType(final File file, final String apiPath) {
            super("errorMissingOperationType", file, apiPath);
        }
    }

    public static class MissingRequiredPropertyForEvent extends DeviceConnectError {
        public MissingRequiredPropertyForEvent(final File file, final String apiPath) {
            super("errorMissingRequiredPropertyForEvent", file, apiPath);
        }
    }

    public static class UnknownOperationType extends DeviceConnectError {
        public UnknownOperationType(final File file, final String apiPath) {
            super("errorUnknownOperationType", file, apiPath);
        }
    }

    public static class AlreadyCreatedTarget extends CodegenError {
        public AlreadyCreatedTarget() {
            String message = getMessageForKey("errorAlreadyCreatedTarget");
            mMessage = message;
        }
    }

    public static class NotEnoughDiscSpace extends CodegenError {
        public NotEnoughDiscSpace() {
            String message = getMessageForKey("errorNotEnoughDiscSpace");
            mMessage = message;
        }
    }

    public static class NoWritePermission extends CodegenError {
        public NoWritePermission() {
            String message = getMessageForKey("errorNoWritePermission");
            mMessage = message;
        }
    }
}
