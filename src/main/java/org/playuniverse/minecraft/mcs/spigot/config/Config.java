package org.playuniverse.minecraft.mcs.spigot.config;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;

import com.syntaxphoenix.syntaxapi.config.yaml.YamlConfig;
import com.syntaxphoenix.syntaxapi.config.yaml.YamlConfigSection;
import com.syntaxphoenix.syntaxapi.reflection.Reflect;
import com.syntaxphoenix.syntaxapi.utils.java.Times;

public abstract class Config {

    public static final ConfigAccess ACCESS = new ConfigAccess();

    private final Reflect migrationRef;

    protected long loaded = -1;

    protected File file;
    protected YamlConfig config = new YamlConfig();

    protected final int latestVersion;
    protected int version;

    public Config(File file, Class<? extends Migration> clazz, int latestVersion) {
        this.file = file;
        this.migrationRef = new Reflect(clazz);
        this.latestVersion = latestVersion;
        this.version = latestVersion;
    }

    /*
     * Getter
     */

    public final Reflect getMigrationRef() {
        return migrationRef;
    }

    public final int getLatestVersion() {
        return latestVersion;
    }

    public final int getVersion() {
        return version;
    }

    protected Class<?> loadAfter() {
        return null;
    }

    /*
     * Management
     */

    public <E> E check(String path, E input) {
        if (config.contains(path))
            return safeCast(input, get(path));
        set(path, input);
        return input;
    }

    public Number check(String path, Number input) {
        if (config.contains(path))
            return safeCast(input, get(path));
        set(path, input);
        return input;
    }

    public <E> E get(String path, E input) {
        if (config.contains(path))
            return safeCast(input, get(path));
        return input;
    }

    public Object get(String path) {
        return config.get(path);
    }

    public String[] getKeys(String path) {
        return Optional.of(config).filter(config -> config.isInstance(path, YamlConfigSection.class))
            .map(config -> config.getSection(path).getKeys().toArray(new String[0])).orElseGet(() -> new String[0]);
    }

    public void set(String path, Object input) {
        config.set(path, input);
    }

    /*
     * IO
     */

    public final void reload() {
        load();

        if (loaded == -1) {
            onSetup();
        }
        loaded = file.lastModified();

        if (file.exists())
            version = check("version", 1).intValue();

        if (latestVersion > version) {
            MigrationContext context = new MigrationContext(config);
            while (latestVersion > version) {
                String method = "update" + version++;
                migrationRef.searchMethod(method, method, MigrationContext.class).execute(method, context);
            }
            file.delete();
            config.clear();
            context.remove("version");
            config.set("version", version);
            for (Entry<String, Object> entry : context.getValues().entrySet())
                config.set(entry.getKey(), entry.getValue());
        } else if (latestVersion < version) {
            backupAndClear();
        }

        onLoad();

        save();
        loaded = file.lastModified();
    }

    public final void unload() {
        onUnload();
        save();

        ConfigTimer.TIMER.unload(this);

        loaded = -1;
        config.clear();
    }

    private final void backupAndClear() {
        String name = file.getName().replace(".yml", "") + "-" + Times.getDate("_") + "-backup-%count%.yml";
        int tries = 0;

        String parent = file.getParent();

        File backupFile = new File(parent, name.replace("%count%", tries + ""));
        while (backupFile.exists())
            backupFile = new File(parent, name.replace("%count%", (tries++) + ""));

        try {
            config.save(backupFile);
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        config.clear();
    }

    private final void load() {
        if (!file.exists())
            return;
        try {
            config.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final void save() {
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists())
                    parent.mkdirs();
                file.createNewFile();
            }
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Type Handle
     */

    protected abstract String getName();

    /*
     * Handle
     */

    protected void onSetup() {}

    protected void onLoad() {}
    
    protected void onSave() {}

    protected void onUnload() {
        onSave();
    }

    /*
     * Utils
     */

    @SuppressWarnings("unchecked")
    protected <E> E safeCast(E sample, Object input) {
        return sample.getClass().isInstance(input) ? (E) input : sample;
    }

}
