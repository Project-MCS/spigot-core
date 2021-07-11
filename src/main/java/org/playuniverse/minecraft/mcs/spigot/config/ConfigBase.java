package org.playuniverse.minecraft.mcs.spigot.config;

import java.io.File;
import java.util.Map.Entry;
import java.util.Optional;

import com.syntaxphoenix.syntaxapi.config.BaseConfig;
import com.syntaxphoenix.syntaxapi.config.BaseSection;
import com.syntaxphoenix.syntaxapi.reflection.Reflect;
import com.syntaxphoenix.syntaxapi.utils.java.Times;

public abstract class ConfigBase<C extends BaseConfig, B extends BaseSection> {

    public static final ConfigAccess ACCESS = new ConfigAccess();

    private final Reflect migrationRef;

    protected long loaded = -1;

    protected File file;

    protected final int latestVersion;
    protected int version;

    public ConfigBase(File file, Class<? extends Migration> clazz, int latestVersion) {
        this.file = file;
        this.migrationRef = new Reflect(clazz);
        this.latestVersion = latestVersion;
        this.version = latestVersion;
    }
    
    /*
     * Abstract Getter
     */
    
    public abstract C getConfig();
    
    public abstract B getStorage();

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
        if (getStorage().contains(path))
            return safeCast(input, get(path));
        set(path, input);
        return input;
    }

    public Number check(String path, Number input) {
        if (getStorage().contains(path))
            return safeCast(input, get(path));
        set(path, input);
        return input;
    }

    public <E> E get(String path, E input) {
        if (getStorage().contains(path))
            return safeCast(input, get(path));
        return input;
    }

    public Object get(String path) {
        return getStorage().get(path);
    }

    public String[] getKeys(String path) {
        return Optional.of(getStorage()).filter(config -> config.isInstance(path, BaseSection.class))
            .map(config -> config.getSection(path).getKeys().toArray(new String[0])).orElseGet(() -> new String[0]);
    }

    public void set(String path, Object input) {
        getStorage().set(path, input);
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
            MigrationContext context = new MigrationContext(getStorage());
            while (latestVersion > version) {
                String method = "update" + version++;
                migrationRef.searchMethod(method, method, MigrationContext.class).execute(method, context);
            }
            file.delete();
            getStorage().clear();
            context.remove("version");
            getStorage().set("version", version);
            for (Entry<String, Object> entry : context.getValues().entrySet())
                getStorage().set(entry.getKey(), entry.getValue());
        } else if (latestVersion < version) {
            backupAndClear();
        }

        onLoad();
        loaded = file.lastModified();

        save();
        loaded = file.lastModified();
    }

    public final void unload() {
        onUnload();
        save();

        ConfigTimer.TIMER.unload(this);

        loaded = -1;
        getStorage().clear();
    }

    private final void backupAndClear() {
        String name = file.getName().replace(".yml", "") + "-" + Times.getDate("_") + "-backup-%count%.yml";
        int tries = 0;

        String parent = file.getParent();

        File backupFile = new File(parent, name.replace("%count%", tries + ""));
        while (backupFile.exists())
            backupFile = new File(parent, name.replace("%count%", (tries++) + ""));

        try {
            getConfig().save(backupFile);
            file.delete();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        getStorage().clear();
    }

    private final void load() {
        if (!file.exists())
            return;
        try {
            getConfig().load(file);
        } catch (Throwable e) {
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
            getConfig().save(file);
        } catch (Throwable e) {
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
