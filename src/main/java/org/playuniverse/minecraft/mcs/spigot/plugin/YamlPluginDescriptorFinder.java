package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.bukkit.configuration.file.YamlConfiguration;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginRuntimeException;
import org.pf4j.util.FileUtils;

public class YamlPluginDescriptorFinder implements PluginDescriptorFinder {

    public static final String DEFAULT_FILE_NAME = "addon.yml";
    public static final String[] DEFAULT_RESOURCE_PATHS = new String[] {
        "",
        "resources/",
        "src/main/resources/"
    };

    public static final String PLUGIN_ID = "name";
    public static final String PLUGIN_CLASS = "main";
    public static final String PLUGIN_VERSION = "version";
    public static final String PLUGIN_REQUIRES = "system";

    public static final String PLUGIN_LICENSE = "license";
    public static final String PLUGIN_DESCRIPTION = "description";

    public static final String PLUGIN_PROVIDER = "authors";
    public static final String PLUGIN_DEPENDENCIES = "dependencies";

    private final String fileName;
    private final String[] paths;

    public YamlPluginDescriptorFinder(String fileName, String... paths) {
        this.fileName = fileName;
        this.paths = paths;
    }

    public YamlPluginDescriptorFinder(String... paths) {
        this(DEFAULT_FILE_NAME, paths);
    }

    public YamlPluginDescriptorFinder(String fileName) {
        this(fileName, DEFAULT_RESOURCE_PATHS);
    }

    public YamlPluginDescriptorFinder() {
        this(DEFAULT_FILE_NAME, DEFAULT_RESOURCE_PATHS);
    }

    @Override
    public boolean isApplicable(Path pluginPath) {
        return Files.exists(pluginPath) && (Files.isDirectory(pluginPath) || FileUtils.isJarFile(pluginPath));
    }

    @Override
    public PluginDescriptor find(Path pluginPath) {
        Path path = getYamlPath(pluginPath, fileName);
        YamlConfiguration config = null;
        try (InputStream stream = Files.newInputStream(path)) {
            config = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
        } catch (IOException e) {
            throw new PluginRuntimeException(e);
        }
        return createPluginDescriptor(config);
    }

    protected Path getYamlPath(Path pluginPath, String fileName) {
        Path currentPath = null;
        for (String path : paths) {
            if (Files.isDirectory(pluginPath)) {
                currentPath = pluginPath.resolve(Paths.get(path + fileName));
            } else {
                // it's a jar file
                try {
                    currentPath = FileUtils.getPath(pluginPath, path + fileName);
                } catch (IOException e) {
                    throw new PluginRuntimeException(e);
                }
            }
            if (currentPath != null && Files.exists(currentPath)) {
                return currentPath;
            }
        }
        throw new PluginRuntimeException("Couldn't find descriptor file");
    }

    protected PluginDescriptor createPluginDescriptor(YamlConfiguration config) {
        YamlPluginDescriptor pluginDescriptor = new YamlPluginDescriptor();

        runNonNullString(config, PLUGIN_ID, value -> pluginDescriptor.setPluginId(value));
        runNonNullString(config, PLUGIN_CLASS, value -> pluginDescriptor.setPluginClass(value));
        runNonNullString(config, PLUGIN_VERSION, value -> pluginDescriptor.setPluginVersion(value));
        runNonNullString(config, PLUGIN_REQUIRES, value -> pluginDescriptor.setRequires(value));

        runStringDefault(config, PLUGIN_DESCRIPTION, "", value -> pluginDescriptor.setPluginDescription(value));

        runString(config, PLUGIN_LICENSE, value -> pluginDescriptor.setLicense(value));

        runList(config, PLUGIN_PROVIDER, value -> pluginDescriptor.setProvider(value));
        runList(config, PLUGIN_DEPENDENCIES, value -> pluginDescriptor.setProvider(value));

        return pluginDescriptor;
    }

    protected final void runList(YamlConfiguration config, String path, Consumer<String> value) {
        value.accept(config.contains(path) ? String.join(",", config.getStringList(path)) : null);
    }
    
    protected final void runString(YamlConfiguration config, String path, Consumer<String> value) {
        value.accept(config.contains(path) ? config.getString(path) : null);
    }

    protected final void runNonNullString(YamlConfiguration config, String path, Consumer<String> value) {
        runString(config, path, output -> {
            if (output == null || output.isBlank()) {
                return;
            }
            value.accept(output);
        });
    }

    protected final void runStringDefault(YamlConfiguration config, String path, String fallback, Consumer<String> value) {
        runString(config, path, output -> value.accept((output == null || output.isBlank()) ? fallback : output));
    }

}
