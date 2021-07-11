package org.playuniverse.minecraft.mcs.spigot.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.plugin.SafePluginManager;
import org.playuniverse.minecraft.mcs.spigot.plugin.SpigotPlugin;
import org.playuniverse.minecraft.mcs.spigot.utils.java.InstanceCreator;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

public class ConfigAccess {

    private final HashMap<Class<? extends ConfigBase<?, ?>>, ConfigBase<?, ?>> configs = new HashMap<>();
    private final ILogger logger;

    ConfigAccess() {
        PluginBase<?> base = PluginBase.get(SpigotCore.class);
        logger = base.getPluginLogger();
        createDefaults(base);
    }

    private final void createDefaults(PluginBase<?> base) {
        ArrayList<Class<? extends ConfigBase<?, ?>>> list = new ArrayList<>();
        base.createConfigs(list);
        if (list.isEmpty()) {
            return;
        }
        File configFolder = new File(base.getDirectory(), "config");
        Files.createFolder(configFolder);
        ArrayList<ConfigBase<?, ?>> configs = new ArrayList<>();
        for (Class<? extends ConfigBase<?, ?>> clazz : list) {
            try {
                configs.add(load0(clazz, configFolder, base.getLogger()));
            } catch (Exception e) {
                logger.log(e);
            }
        }
        sort(configs);
        for (ConfigBase<?, ?> config : configs) {
            ConfigTimer.TIMER.load(config);
        }
    }

    /*
     * Config loading
     */

    private ConfigBase<?, ?> load0(Class<? extends ConfigBase<?, ?>> clazz, Object... arguments) throws Exception {
        ConfigBase<?, ?> config = InstanceCreator.create(clazz, arguments);
        if (config == null) {
            throw new NullPointerException("config is null!");
        }
        configs.put(clazz, config);
        return config;
    }

    /*
     * Plugin specific
     */

    public ConfigAccess load(PluginWrapper wrapper) {
        SpigotPlugin<?> plugin = SpigotPlugin.getByWrapper(wrapper);
        if (plugin == null) {
            return this;
        }
        Class<? extends ConfigBase<?, ?>>[] classes = plugin.getConfigurations();
        if (classes.length == 0) {
            return this;
        }
        ArrayList<ConfigBase<?, ?>> configList = new ArrayList<>();
        for (Class<? extends ConfigBase<?, ?>> clazz : classes) {
            ConfigBase<?, ?> config = null;
            try {
                config = load0(clazz, plugin.getDataLocation(), plugin, plugin.getLogger());
            } catch (Exception e) {
                logger.log(e);
            }
            if (config == null || configList.stream().anyMatch(current -> current.getClass().getName().equals(clazz.getName()))) {
                continue;
            }
            configList.add(config);
        }
        ConfigBase<?, ?>[] configs = sort(configList);
        for (ConfigBase<?, ?> config : configs) {
            ConfigTimer.TIMER.load(config);
        }
        return this;
    }

    private ConfigBase<?, ?>[] sort(ArrayList<ConfigBase<?, ?>> configs) {
        ConfigBase<?, ?>[] output = new ConfigBase[configs.size()];
        ConfigBase<?, ?>[] waiting = new ConfigBase[configs.size()];
        int id = 0, wait = 0;
        for (int index = 0; index < output.length; index++) {
            ConfigBase<?, ?> config = configs.get(index);
            Class<?> dependency0 = config.loadAfter();
            if (dependency0 != null) {
                if (index == 0) {
                    waiting[wait++] = config;
                    continue;
                }
                boolean found = false;
                for (int filter = 0; filter < id; filter++) {
                    ConfigBase<?, ?> possible = output[filter];
                    if (dependency0.isInstance(possible)) {
                        output[id++] = possible;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    waiting[wait++] = config;
                    continue;
                }
            } else {
                output[id++] = config;
            }
            for (int waiter = 0; waiter < wait; waiter++) {
                ConfigBase<?, ?> current = waiting[waiter];
                Class<?> dependency1 = current.loadAfter();
                if (dependency1.isInstance(config)) {
                    output[id++] = current;
                    if (waiter != wait - 1) {
                        for (int sort = waiter; sort < wait - 1; sort++) {
                            waiting[sort] = waiting[sort + 1];
                        }
                    } else {
                        waiting[waiter] = null;
                    }
                    waiter--;
                    wait--;
                    break;
                }
            }
        }
        return output;
    }

    public ConfigAccess unload(PluginWrapper wrapper) {
        SafePluginManager pluginManager = ((SafePluginManager) wrapper.getPluginManager());
        synchronized (configs) {
            Class<?>[] classes = configs.keySet().stream().filter(clazz -> pluginManager.isFromPlugin(wrapper, clazz))
                .toArray(size -> new Class[size]);
            for (int index = 0; index < classes.length; index++) {
                configs.remove(classes[index]).unload();
            }
        }
        return this;
    }

    /*
     * Config management
     */

    @SuppressWarnings("unchecked")
    public <E extends ConfigBase<?, ?>> E get(Class<E> config) {
        synchronized (configs) {
            return (E) configs.get(config);
        }
    }

    public <E extends ConfigBase<?, ?>> Optional<E> optional(Class<E> config) {
        return Optional.ofNullable(get(config));
    }

}
