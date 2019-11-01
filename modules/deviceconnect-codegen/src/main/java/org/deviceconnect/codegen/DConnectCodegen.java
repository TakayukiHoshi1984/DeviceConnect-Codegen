/*
 DConnectCodegen.java
 Copyright (c) 2018 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.codegen;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.common.collect.ImmutableMap;
import io.swagger.codegen.*;
import io.swagger.models.*;
import io.swagger.parser.SwaggerParser;
import org.apache.commons.cli.*;
import org.deviceconnect.codegen.app.HtmlAppCodegenConfig;
import org.deviceconnect.codegen.docs.HtmlDocsCodegenConfig;
import org.deviceconnect.codegen.docs.MarkdownDocsCodegenConfig;
import org.deviceconnect.codegen.error.DConnectError;
import org.deviceconnect.codegen.error.ErrorUtils;
import org.deviceconnect.codegen.error.Errors;
import org.deviceconnect.codegen.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import static org.deviceconnect.codegen.Const.MESSAGES;
import static org.deviceconnect.codegen.error.ErrorUtils.reportError;

public class DConnectCodegen {

    private static final Logger LOGGER = LoggerFactory.getLogger(Codegen.class);
    private static final String[] PROHIBITED_PROFILES = {
            "availability",
            "authorization",
            "serviceDiscovery",
            "serviceInformation",
            "system"
    };
    private static final String[] RESERVED_NAMES = {
            "files",
            "get",
            "put",
            "post",
            "delete"
    };

    static String debugInfoOptions = "\nThe following additional debug options are available for all codegen targets:" +
            "\n -DdebugSwagger prints the swagger specification as interpreted by the codegen" +
            "\n -DdebugModels prints models passed to the template engine" +
            "\n -DdebugOperations prints operations passed to the template engine" +
            "\n -DdebugSupportingFiles prints additional data passed to the template engine";

    private static final SwaggerJsonValidator JSON_VALIDATOR = new SwaggerJsonValidator();

    private static final MultipleSwaggerConverter SWAGGER_CONVERTER = new MultipleSwaggerConverter();

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");

        ClientOptInput clientOptInput = new ClientOptInput();
        ClientOpts clientOpts = new ClientOpts();

        CommandLine cmd;
        try {
            CommandLineParser parser = new BasicParser();
            DConnectCodegenConfig config;

            Options options = Const.MAIN_OPTIONS;
            try {
                cmd = parser.parse(Const.HELP_OPTION, args);
                if (cmd.hasOption("h")) {
                    usage(options);
                    return;
                }
            } catch (ParseException ignored) {}

            cmd = parser.parse(options, args);
            if (cmd.hasOption("d")) {
                usage(options);
                System.out.println(debugInfoOptions);
                return;
            }
            if (cmd.hasOption("h")) {
                config = Const.getConfig(cmd.getOptionValue("l"));
                if (config != null) {
                    options.addOption("h", "help", true, config.getHelp());
                    usage(options);
                    return;
                }
                usage(options);
                return;
            }
            if (cmd.hasOption("l")) {
                config = Const.getConfig(cmd.getOptionValue("l"));
                clientOptInput.setConfig(config);
            } else {
                usage(options);
                return;
            }
            if (cmd.hasOption("o")) {
                String output = cmd.getOptionValue("o");
                File outputDir = new File(output);
                if (outputDir.exists()) {
                    if (!outputDir.canWrite()) {
                        ErrorUtils.reportError(new Errors.NoWritePermission(outputDir));
                        return;
                    }
                    if (cmd.hasOption("w")) {
                        deleteDirectoryOrFile(outputDir);
                    } else {
                        // （上書き禁止の場合）すでに出力先にフォルダが存在している
                        ErrorUtils.reportError(new Errors.AlreadyCreatedTarget(outputDir));
                        return;
                    }
                }
                config.setOutputDir(output);
            }

            if (cmd.hasOption("i")) {
                File file = new File(cmd.getOptionValue("i"));
                if (file.isFile()) {
                    if(!parseSwaggerFromFile(file, clientOptInput, config)) {
                        return;
                    }
                } else if (file.isDirectory()) {
                    File dir = file;
                    if(!parseSwaggerFromDirectory(dir, clientOptInput, config)) {
                        return;
                    }
                }
            } else if (cmd.hasOption("s")) {
                File dir = new File(cmd.getOptionValue("s"));
                if (dir.isDirectory()) {
                    if(!parseSwaggerFromDirectory(dir, clientOptInput, config)) {
                        return;
                    }
                } else {
                    // TODO エラーメッセージ詳細化: ディレクトリではなくファイルへのパスが指定されている.
                    usage(options);
                    return;
                }
            }

            if (cmd.hasOption("c")) {
                String configFile = cmd.getOptionValue("c");
                Config genConfig = ConfigParser.read(configFile);
                if (null != genConfig && null != config) {
                    for (CliOption langCliOption : config.cliOptions()) {
                        if (genConfig.hasOption(langCliOption.getOpt())) {
                            config.additionalProperties().put(langCliOption.getOpt(), genConfig.getOption(langCliOption.getOpt()));
                        }
                    }
                    config.additionalProperties().putAll(genConfig.getAdditionalProperties());

                    for (Map.Entry<String, Object> prop : config.additionalProperties().entrySet()) {
                        LOGGER.info("Parsed additionProperties: " + prop.getKey() + "=" + prop.getValue());
                    }
                }
            }
            if (cmd.hasOption("t")) {
                String dirPath = String.valueOf(cmd.getOptionValue("t"));
                if (!new File(dirPath).exists()) {
                    reportError(new Errors.TemplateDirNotFound(dirPath));
                    return;
                }
                clientOpts.getProperties().put(CodegenConstants.TEMPLATE_DIR, dirPath);
            }

            String displayName;
            if (cmd.hasOption("n")) {
                displayName = cmd.getOptionValue("n");
            } else {
                displayName = config.getDefaultDisplayName();
            }
            clientOpts.getProperties().put("displayName", displayName);

            String classPrefix;
            if (cmd.hasOption("x")) {
                classPrefix = cmd.getOptionValue("x");
            } else {
                classPrefix = "My";
            }
            clientOpts.getProperties().put("classPrefix", classPrefix);

            String gradlePluginVersion;
            if (cmd.hasOption("r")) {
                gradlePluginVersion = cmd.getOptionValue("r");
            } else {
                gradlePluginVersion = "3.1.0";
            }
            clientOpts.getProperties().put("gradlePluginVersion", gradlePluginVersion);

            ValidationResultSet resultSet = config.validateOptions(cmd, clientOpts);
            if (!resultSet.isValid()) {
                for (ValidationResult result : resultSet.getResults().values()) {
                    if (!result.isValid()) {
                        LOGGER.error(result.getParamName() + " is invalid; " + result.getErrorMessage());
                    }
                }
                return;
            }
        } catch (ParseException e) {
            reportError(e);
            return;
        } catch (DuplicatedPathException e) {
            printDuplicatedPathError(e);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            new Codegen() {
                @Override
                public File writeToFile(final String filename, final String contents) throws IOException {
                    // 不要なファイルは出力させない
                    if (filename != null) {
                        if (filename.endsWith("VERSION")
                            || filename.endsWith("LICENSE")
                            || filename.endsWith(".swagger-codegen-ignore")) {
                            return null;
                        }
                    }
                    return super.writeToFile(filename, contents);
                }
            }.opts(clientOptInput.opts(clientOpts)).generate();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static boolean deleteDirectoryOrFile(final File file) {
        if (file.isFile()) {
            return file.delete();
        }
        return deleteDirectory(file);
    }

    private static boolean deleteDirectory(final File dir) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                boolean deleted = deleteDirectory(child);
                if (!deleted) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private static boolean parseSwaggerFromFile(File file,
                                             ClientOptInput clientOptInput,
                                             DConnectCodegenConfig config)
        throws IOException, ProcessingException {
        if(!checkJsonAndSwagger(file)) {
            return false;
        }
        Swagger swagger = new SwaggerParser().read(file.getAbsolutePath(), clientOptInput.getAuthorizationValues(), true);
        if (!checkDeviceConnect(file, swagger)) {
            return false;
        }

        clientOptInput.swagger(swagger);

        String basePath = swagger.getBasePath();
        if (basePath == null || basePath.equals("")) {
            basePath = "/";
            swagger.setBasePath(basePath);
        }
        Map<String, Swagger> profiles = new HashMap<>();
        Map<String, Path> paths = swagger.getPaths();
        for (Map.Entry<String, Path> entry : paths.entrySet()) {
            Path path = entry.getValue();
            String pathName = entry.getKey();
            String fullPathName = basePath.equals("/") ? pathName : basePath + pathName;
            String[] parts = fullPathName.split("/");
            if (parts.length < 3) {
                continue;
            }
            String apiPart = parts[1];
            String profilePart = parts[2];
            String subPath = "/";
            for (int i = 3; i < parts.length; i++) {
                subPath += parts[i];
                if (i < parts.length - 1) {
                    subPath += "/";
                }
            }
            checkProfileName(config, profilePart);

            Swagger profile = profiles.get(profilePart);
            if (profile == null) {
                profile = createProfileSpec(swagger);
                profile.setBasePath("/" + apiPart + "/" + profilePart);
                profiles.put(profilePart, profile);
            }
            Map<String, Path> subPaths = profile.getPaths();
            subPaths.put(subPath, path);
            profile.setPaths(subPaths);
        }
        config.setProfileSpecs(profiles);
        config.setOriginalSwagger(SwaggerUtils.cloneSwagger(swagger));
        return true;
    }

    private static boolean parseSwaggerFromDirectory(File dir,
                                                  ClientOptInput clientOptInput,
                                                  DConnectCodegenConfig config)
            throws IOException, ProcessingException, IllegalPathFormatException, DuplicatedPathException {
        File[] specFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".json") || name.endsWith(".yaml");
            }
        });
        List<Swagger> swaggerList = new ArrayList<>();
        boolean success = true;
        for (File file : specFiles) {
            success &= checkJsonAndSwagger(file);
            if(!success) {
                continue;
            }
            Swagger swagger = new SwaggerParser().read(file.getAbsolutePath(), clientOptInput.getAuthorizationValues(), true);
            success &= checkDeviceConnect(file, swagger);
            if(!success) {
                continue;
            }

            String basePath = swagger.getBasePath();
            if (basePath == null || basePath.equals("")) {
                swagger.setBasePath("/");
            }

            if (swagger != null) {
                swaggerList.add(swagger);
            }
        }
        if (!success) {
            return false;
        }

        Map<String, Swagger> profileSpecs = SWAGGER_CONVERTER.convert(swaggerList);
        for (String profileName : profileSpecs.keySet()) {
            checkProfileName(config, profileName);
        }
        config.setProfileSpecs(profileSpecs);
        Swagger swagger = mergeSwaggers(profileSpecs);
        config.setOriginalSwagger(SwaggerUtils.cloneSwagger(swagger));
        clientOptInput.swagger(swagger);
        return true;
    }

    private static boolean checkJsonAndSwagger(final File file) throws IOException, ProcessingException {
        String fileName = file.getName();
        ObjectMapper mapper;
        if (fileName.endsWith(".yaml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else if (fileName.endsWith(".json")) {
            mapper = new ObjectMapper();
        } else {
            throw new IllegalArgumentException("file must be JSON or YAML.");
        }

        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(file);
            return checkSwagger(file, jsonNode);
        } catch (JsonProcessingException e) {
            reportError(new Errors.InvalidJson(file, e));
            return false;
        }
    }

    private static boolean checkSwagger(final File file, final JsonNode jsonNode) throws IOException, ProcessingException {
        SwaggerJsonValidator.Result result = JSON_VALIDATOR.validate(jsonNode);
        if (!result.isSuccess()) {
            reportError(new Errors.InvalidSwagger(file, result.getErrors()));
            return false;
        }
        return true;
    }

    private static boolean checkDeviceConnect(final File file, final Swagger swagger) {
        List<DConnectError> errors = new ArrayList<>();

        Map<String, Path> paths = swagger.getPaths();
        String basePath = swagger.getBasePath();
        for (Map.Entry<String, Path> path : paths.entrySet()) {
            String subPath = path.getKey();

            // API パスの不正チェック
            try {
                throwIfIllegalPathFormat(basePath, subPath);
            } catch (IllegalPathFormatException e) {
                DConnectError error;
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
                errors.add(error);
            }

            Map<HttpMethod, Operation> opMap = path.getValue().getOperationMap();
            if (opMap != null) {
                for (Map.Entry<HttpMethod, Operation> entry : opMap.entrySet()) {
                    HttpMethod method = entry.getKey();
                    Operation op = entry.getValue();
                    String id = op.getOperationId();
                    String type = getOperationType(op);

                    if (id == null) {
                        // Operation ID が指定されていない
                        errors.add(new Errors.MissingOperationId(file, path.getKey()));
                    }

                    if (type == null) {
                        // API タイプが指定されていない
                        errors.add(new Errors.MissingOperationType(file, path.getKey()));
                    } else if (! ("one-shot".equals(type) || "event".equals(type) || "streaming".equals(type))) {
                        // API タイプが不正
                        errors.add(new Errors.UnknownOperationType(file, path.getKey()));
                    } else if (method == HttpMethod.PUT && "event".equals(type)) {
                        Object event = getEventMessage(op);
                        if (event == null) {
                            // イベント API に対してイベントメッセージが定義されていない
                            errors.add(new Errors.MissingEvent(file, path.getKey()));
                        } else {
                            // イベントメッセージの定義が不正 (必須プロパティがない)
                            // TODO
                        }
                    }

                    // メソッドが不正でないこと
                    if (!(method == HttpMethod.GET || method == HttpMethod.POST || method == HttpMethod.PUT ||method == HttpMethod.DELETE)) {
                        errors.add(new Errors.InvalidMethod(file, path.getKey()));
                    }
                }
            }
        }

        if (errors.size() > 0) {
            for (DConnectError error : errors) {
                ErrorUtils.reportError(error);
            }
            return false;
        }
        return true;
    }

    private static void throwIfIllegalPathFormat(final String basePath, final String subPath) throws IllegalPathFormatException {
        final String separator = DConnectPath.SEPARATOR;
        if (!basePath.startsWith(separator)) {
            throw new IllegalPathFormatException(IllegalPathFormatException.Reason.NOT_STARTED_WITH_ROOT, basePath);
        }
        if (!subPath.startsWith(separator)) {
            throw new IllegalPathFormatException(IllegalPathFormatException.Reason.NOT_STARTED_WITH_ROOT, subPath);
        }
        String path = canonicalizePath(basePath, subPath);
        String[] parts = path.split(separator);
        int length = parts.length;
        if (length < 3) {
            throw new IllegalPathFormatException(IllegalPathFormatException.Reason.TOO_SHORT, path);
        } else if (length > 5) {
            throw new IllegalPathFormatException(IllegalPathFormatException.Reason.TOO_LONG, path);
        }
    }

    private static String canonicalizePath(final String basePath, final String subPath) {
        String path = basePath.equals(DConnectPath.SEPARATOR) ? subPath : basePath + subPath;

        // 空白のパス要素は無視
        String[] parts = path.split(DConnectPath.SEPARATOR);
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            if ("".equals(part)) {
                continue;
            }
            result.add(part);
        }
        return concatParts(DConnectPath.SEPARATOR, result.toArray(new String[result.size()]));
    }

    private static String concatParts(String separator, String... parts) {
        if (parts == null) {
            return separator;
        }
        String path = separator;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] == null) {
                continue;
            }
            path += parts[i];
            if (i < parts.length - 1) {
                path += separator;
            }
        }
        return path;
    }

    private static String getOperationType(final Operation operation) {
        Map<String, Object> extensions = operation.getVendorExtensions();
        if (extensions == null) {
            return null;
        }
        Object type = extensions.get("x-type");
        if (type instanceof String) {
            return (String) type;
        }
        return null;
    }

    private static HashMap getEventMessage(final Operation operation) {
        Map<String, Object> extensions = operation.getVendorExtensions();
        if (extensions == null) {
            return null;
        }
        Object event = extensions.get("x-event");
        if (event instanceof HashMap) {
            return (HashMap) event;
        }
        return null;
    }

    private static void printError(final String message) {
        System.err.println(message);
    }

    private static void printDuplicatedPathError(final DuplicatedPathException e) {
        String template = MESSAGES.getString("errorDuplicatedPath");
        List<NameDuplication> duplications = e.getDuplications();

        String pathNames = "";
        for (Iterator<NameDuplication> it = duplications.iterator(); it.hasNext(); ) {
            NameDuplication dup = it.next();
            pathNames += dup.getName();
            if (it.hasNext()) {
                pathNames += ", ";
            }
        }

        String errorMessage = template.replace("{{paths}}", pathNames);
        printError(errorMessage);
    }

    private static Swagger createProfileSpec(final Swagger swagger) {
        Swagger profile = new SortedSwagger();
        profile.setSwagger(swagger.getSwagger());
        Info info = new Info();
        info.setTitle(swagger.getInfo().getTitle());
        info.setVersion(swagger.getInfo().getVersion());
        info.setDescription(swagger.getInfo().getDescription());
        profile.setInfo(info);
        profile.setConsumes(swagger.getConsumes());
        profile.setExternalDocs(swagger.getExternalDocs());
        profile.setHost(swagger.getHost());
        profile.setParameters(swagger.getParameters());
        profile.setResponses(swagger.getResponses());
        profile.setProduces(swagger.getProduces());
        profile.setSecurity(swagger.getSecurity());
        profile.setSecurityDefinitions(swagger.getSecurityDefinitions());
        profile.setSchemes(swagger.getSchemes());
        profile.setTags(swagger.getTags());
        Map<String, Object> extensions = swagger.getVendorExtensions();
        if (extensions != null) {
            for (Map.Entry<String, Object> entry : extensions.entrySet()) {
                profile.setVendorExtension(entry.getKey(), entry.getValue());
            }
        }
        profile.setDefinitions(swagger.getDefinitions());
        profile.setPaths(new HashMap<String, Path>());
        return profile;
    }

    private static void checkProfileName(final DConnectCodegenConfig config, final String profileName) {
        if (config instanceof HtmlAppCodegenConfig || config instanceof HtmlDocsCodegenConfig ||
            config instanceof MarkdownDocsCodegenConfig) {
            return;
        }

        // プロファイル名が予約語の場合は異常終了
        if (isReservedName(profileName)) {
            exitOnError("次の名前は予約語のためプロファイル名として使用できません: " + concat(RESERVED_NAMES));
        }
        // プロファイル名が基本プロファイル名の場合は異常終了
        if (isProhibitedProfile(profileName)) {
            exitOnError("次のプロファイルは基本プロファイルのため入力できません: " + concat(PROHIBITED_PROFILES));
        }
    }

    private static void exitOnError(final String message) {
        System.err.println(message);
        System.exit(1);
    }

    private static String concat(final String[] array) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(array[i]);
        }
        return result.toString();
    }

    private static boolean isProhibitedProfile(final String profileName) {
        for (String prohibited : PROHIBITED_PROFILES) {
            if (profileName.equalsIgnoreCase(prohibited)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isReservedName(final String name) {
        for (String reserved : RESERVED_NAMES) {
            if (name.equalsIgnoreCase(reserved)) {
                return true;
            }
        }
        return false;
    }

    private static String parseProfileName(final Swagger swagger) {
        String basePath = swagger.getBasePath();
        if (basePath == null) {
            return null;
        }
        String[] array = basePath.split("/");
        if (array.length < 3) {
            return null;
        }
        return array[2];
    }

    private static String parseProfileNameFromFileName(final String fileName) {
        if (!fileName.endsWith(".json")) {
            throw new IllegalArgumentException("JSON file is required.");
        }
        return fileName.substring(0, fileName.length() - ".json".length());
    }

    private static Swagger mergeSwaggers(Map<String, Swagger> swaggerMap) {
        Swagger merged = new SortedSwagger();
        merged.setBasePath("/");

        // info
        Info info = new Info();
        info.setTitle("Device Connect");
        info.setVersion("1.0.0");
        merged.setInfo(info);

        // consumes
        List<String> allConsumes = new ArrayList<>();
        for (Map.Entry<String, Swagger> entry : swaggerMap.entrySet()) {
            Swagger swagger = entry.getValue();
            List<String> consumes = swagger.getConsumes();
            if (consumes != null) {
                for (String mimeType : consumes) {
                    if (!allConsumes.contains(mimeType)) {
                        allConsumes.add(mimeType);
                    }
                }
            }
        }
        merged.setConsumes(allConsumes);

        // paths
        Map<String, Path> paths = new HashMap<>();
        for (Map.Entry<String, Swagger> swagger : swaggerMap.entrySet()) {
            for (Map.Entry<String, Path> subPath : swagger.getValue().getPaths().entrySet()) {
                paths.put(swagger.getValue().getBasePath() + subPath.getKey(), subPath.getValue());
            }
        }
        merged.paths(paths);

        // definitions
        Map<String, Model> definitions = new HashMap<>();
        for (Map.Entry<String, Swagger> swagger : swaggerMap.entrySet()) {
            if (swagger.getValue().getDefinitions() != null) {
                definitions.putAll(swagger.getValue().getDefinitions());
            }
        }
        merged.setDefinitions(definitions);

        return merged;
    }

    static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DConnectCodegen", options);
    }

    private static class Config {

        private final Map<String, String> options = new HashMap<>();
        private final Map<String, Object> additionalProperties = new HashMap<>();

        Map<String, String> getOptions() {
            return ImmutableMap.copyOf(options);
        }

        boolean hasOption(String opt) {
            return options.containsKey(opt);
        }

        String getOption(String opt) {
            return options.get(opt);
        }

        void setOption(String opt, String value) {
            options.put(opt, value);
        }

        Map<String, Object> getAdditionalProperties() {
            return ImmutableMap.copyOf(additionalProperties);
        }

        void addAdditionalProperties(final Map<String, Object> properties) {
            additionalProperties.putAll(properties);
        }
    }

    private static class ConfigParser {

        private static final Logger LOGGER = LoggerFactory.getLogger(config.ConfigParser.class);

        public static Config read(String location) {
           ObjectMapper mapper = new ObjectMapper();

            Config config = new Config();

            try {
                JsonNode rootNode = mapper.readTree(new File(location));
                Iterator<Map.Entry<String, JsonNode>> optionNodes = rootNode.fields();

                while (optionNodes.hasNext()) {
                    Map.Entry<String, JsonNode> optionNode = optionNodes.next();
                    if (optionNode.getValue().isValueNode()) {
                        config.setOption(optionNode.getKey(), optionNode.getValue().asText());
                    } else if (optionNode.getValue().isObject() && "additionalProperties".equals(optionNode.getKey())) {
                        Map<String, Object> map = mapper.convertValue(optionNode.getValue(), new TypeReference<Map<String, Object>>() {});
                        config.addAdditionalProperties(map);
                    } else {
                        LOGGER.warn("omitting non-value node " + optionNode.getKey());
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                return null;
            }

            return config;
        }
    }
}