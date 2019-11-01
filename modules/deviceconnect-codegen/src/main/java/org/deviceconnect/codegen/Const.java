/*
 Const.java
 Copyright (c) 2018 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.codegen;


import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.deviceconnect.codegen.util.ResourceBundleWithUtf8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class Const {

    private static final Logger LOGGER = LoggerFactory.getLogger(Const.class);

    static final ResourceBundle MESSAGES = ResourceBundle.getBundle("Messages", Locale.getDefault(),
            ResourceBundleWithUtf8.UTF8_ENCODING_CONTROL);

    public static final Options MAIN_OPTIONS;
    public static final Options HELP_OPTION;

    static Map<String, DConnectCodegenConfig> configs = new HashMap<String, DConnectCodegenConfig>();

    static String configString;

    static DConnectCodegenConfig getConfig(String name) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        } else {
            // see if it's a class
            try {
                LOGGER.debug("loading class " + name);
                Class<?> customClass = Class.forName(name);
                LOGGER.debug("loaded");
                return (DConnectCodegenConfig) customClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("can't load class " + name);
            }
        }
    }

    static {
        List<DConnectCodegenConfig> extensions = getExtensions();
        StringBuilder sb = new StringBuilder();

        for (DConnectCodegenConfig config : extensions) {
            if (sb.toString().length() != 0) {
                sb.append(", ");
            }
            sb.append(config.getName());
            configs.put(config.getName(), config);
        }
        configString = sb.toString();
        MAIN_OPTIONS = createOptions();
        HELP_OPTION = createHelpOption();
    }

    static List<DConnectCodegenConfig> getExtensions() {
        ServiceLoader<DConnectCodegenConfig> loader = ServiceLoader.load(DConnectCodegenConfig.class);
        List<DConnectCodegenConfig> output = new ArrayList<DConnectCodegenConfig>();
        for (DConnectCodegenConfig config : loader) {
            output.add(config);
        }
        return output;
    }

    private static Options createHelpOption() {
        Options options = new Options();
        options.addOption("h", "help", false, "shows this message");
        return options;
    }

    private static Options createOptions() {
        Options options = new Options();

        // Required
        options.addOption(option(true, "l", "lang", true, "client language to generate.\nAvailable languages include:\n\t[" + Const.configString + "]"));
        options.addOption(option(true, "o", "output", true, "where to write the generated files."));
        OptionGroup inputSpecOptions = new OptionGroup();
        inputSpecOptions.setRequired(true);
        inputSpecOptions.addOption(new Option("s", "input-spec-dir", true, "directory of the swagger specs."));
        inputSpecOptions.addOption(new Option("i", "input-spec", true, "location of the swagger spec, as URL, file or directory."));
        options.addOptionGroup(inputSpecOptions);

        // Optional
        options.addOption("h", "help", false, "shows this message");
        options.addOption("t", "template-dir", true, "folder containing the template files.");
        options.addOption("d", "debug-info", false, "prints additional info for debugging.");
        //options.addOption("a", "auth", true, "adds authorization headers when fetching the swagger definitions remotely. Pass in a URL-encoded string of name:header with a comma separating multiple values");
        options.addOption("c", "config", true, "location of the configuration file.");
        options.addOption("p", "package-name", true, "package name (for Android only)");
        options.addOption("n", "display-name", true, "display name of the generated project.");
        options.addOption("x", "class-prefix", true, "prefix of each generated class that implements a device connect profile. (for Android and iOS only)");
        options.addOption("b", "connection-type", true, "specifies the connection type with device connect manager by \"binder\" or \"broadcast\"; The default value is \"binder\". (for Android only)");
        options.addOption("r", "gradle-plugin-version", true, "version of Android Plugin for Gradle. The default value is \"3.0.0\". (for Android only)");
        options.addOption("k", "sdk", true, "location of Device Connect SDKs.");
        options.addOption("g", "signing-configs", true, "location of singing configs.");
        options.addOption("w", "overwrite", false, "allow to delete a directory which exists already on the specified path by `output` option before output.");

        return options;
    }

    private static Option option(boolean required, String opt, String longOpt, boolean hasArg, String description) {
        Option option = new Option(opt, longOpt, hasArg, description);
        option.setRequired(required);
        return option;
    }
}