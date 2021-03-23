package org.playuniverse.minecraft.mcs.spigot.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.pf4j.PluginDependency;
import org.pf4j.PluginDescriptor;

public class YamlPluginDescriptor implements PluginDescriptor {

    private String pluginId;
    private String pluginDescription;
    private String pluginClass = SpigotPlugin.class.getName();
    private String version;
    private String requires = "*"; // SemVer format
    private String provider;
    private List<PluginDependency> dependencies;
    private String license;

    public YamlPluginDescriptor() {
        dependencies = new ArrayList<>();
    }

    public YamlPluginDescriptor(String pluginId, String pluginDescription, String pluginClass, String version, String requires,
        String provider, String license) {
        this();
        this.pluginId = pluginId;
        this.pluginDescription = pluginDescription;
        this.pluginClass = pluginClass;
        this.version = version;
        this.requires = requires;
        this.provider = provider;
        this.license = license;
    }

    public void addDependency(PluginDependency dependency) {
        this.dependencies.add(dependency);
    }

    /**
     * Returns the unique identifier of this plugin.
     */
    @Override
    public String getPluginId() {
        return pluginId;
    }

    /**
     * Returns the description of this plugin.
     */
    @Override
    public String getPluginDescription() {
        return pluginDescription;
    }

    /**
     * Returns the name of the class that implements Plugin interface.
     */
    @Override
    public String getPluginClass() {
        return pluginClass;
    }

    /**
     * Returns the version of this plugin.
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Returns string version of requires
     *
     * @return String with requires expression on SemVer format
     */
    @Override
    public String getRequires() {
        return requires;
    }

    /**
     * Returns the provider name of this plugin.
     */
    @Override
    public String getProvider() {
        return provider;
    }

    /**
     * Returns the legal license of this plugin, e.g. "Apache-2.0", "MIT" etc
     */
    @Override
    public String getLicense() {
        return license;
    }

    /**
     * Returns all dependencies declared by this plugin. Returns an empty array if
     * this plugin does not declare any require.
     */
    @Override
    public List<PluginDependency> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "PluginDescriptor [pluginId=" + pluginId + ", pluginClass=" + pluginClass + ", version=" + version + ", provider=" + provider
            + ", dependencies=" + dependencies + ", description=" + pluginDescription + ", requires=" + requires + ", license=" + license
            + "]";
    }

    protected YamlPluginDescriptor setPluginId(String pluginId) {
        this.pluginId = pluginId;
        return this;
    }

    protected PluginDescriptor setPluginDescription(String pluginDescription) {
        this.pluginDescription = pluginDescription;

        return this;
    }

    protected PluginDescriptor setPluginClass(String pluginClassName) {
        this.pluginClass = pluginClassName;

        return this;
    }

    protected YamlPluginDescriptor setPluginVersion(String version) {
        this.version = version;
        return this;
    }

    protected PluginDescriptor setProvider(String provider) {
        this.provider = provider;

        return this;
    }

    protected PluginDescriptor setRequires(String requires) {
        this.requires = requires;

        return this;
    }

    protected PluginDescriptor setDependencies(String dependencies) {
        this.dependencies = new ArrayList<>();

        if (dependencies != null) {
            dependencies = dependencies.trim();
            if (!dependencies.isEmpty()) {
                String[] tokens = dependencies.split(",");
                for (String dependency : tokens) {
                    dependency = dependency.trim();
                    if (!dependency.isEmpty()) {
                        this.dependencies.add(new PluginDependency(dependency));
                    }
                }
            }
        }

        return this;
    }

    public PluginDescriptor setLicense(String license) {
        this.license = license;

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof YamlPluginDescriptor))
            return false;
        YamlPluginDescriptor that = (YamlPluginDescriptor) o;
        return Objects.equals(pluginId, that.pluginId) && Objects.equals(pluginDescription, that.pluginDescription)
            && Objects.equals(pluginClass, that.pluginClass) && Objects.equals(version, that.version)
            && Objects.equals(requires, that.requires) && Objects.equals(provider, that.provider) && dependencies.equals(that.dependencies)
            && Objects.equals(license, that.license);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pluginId, pluginDescription, pluginClass, version, requires, provider, dependencies, license);
    }
}
