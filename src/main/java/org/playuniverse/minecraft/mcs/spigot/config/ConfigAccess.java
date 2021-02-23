package org.playuniverse.minecraft.mcs.spigot.config;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.SpigotCore;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.plugin.SafePluginManager;
import org.playuniverse.minecraft.mcs.spigot.plugin.SpigotPlugin;
import org.playuniverse.minecraft.mcs.utils.java.ReflectionProvider;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.java.Arrays;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

public class ConfigAccess {

    private final HashMap<Class<? extends Config>, Config> configs = new HashMap<>();
    private final ReflectionProvider provider;
    private final ILogger logger;

    ConfigAccess() {
        PluginBase<?> base = PluginBase.get(SpigotCore.class);
        logger = base.getPluginLogger();
        provider = base.getReflectionProvider();
        createDefaults(base);
    }

    private final void createDefaults(PluginBase<?> base) {
        ArrayList<Class<? extends Config>> list = new ArrayList<>();
        base.createConfigs(list);
        if (list.isEmpty()) {
            return;
        }
        File configFolder = new File(base.getDirectory(), "config");
        Files.createFolder(configFolder);
        ArrayList<Config> configs = new ArrayList<>();
        for (Class<? extends Config> clazz : list) {
            try {
                configs.add(load0(configFolder, clazz));
            } catch (Exception e) {
                logger.log(e);
            }
        }
        sort(configs);
        for (Config config : configs) {
            ConfigTimer.TIMER.load(config);
        }
    }

    /*
     * Config loading
     */

    @SuppressWarnings("deprecation")
    private Config load0(File folder, Class<? extends Config> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            return null;
        }
        Constructor<?> current;
        Constructor<?> execute = null;
        boolean paramFile = false;
        for (int index = 0; index < constructors.length; index++) {
            current = constructors[index];
            switch (current.getParameterCount()) {
            case 0:
                if (execute == null) {
                    execute = current;
                }
                continue;
            case 1:
                if (!ILogger.class.equals(current.getParameters()[0].getType())
                    && !(paramFile = File.class.equals(current.getParameters()[0].getType()))) {
                    continue;
                }
                execute = current;
                continue;
            case 2:
                if (!File.class.equals(current.getParameters()[0].getType())
                    || !ILogger.class.equals(current.getParameters()[1].getType())) {
                    continue;
                }
                execute = current;
                continue;
            default:
                continue;
            }
        }
        if (execute == null) {
            return null;
        }
        boolean access = execute.isAccessible();
        if (!access) {
            execute.setAccessible(true);
        }
        Config config = (Config) (execute.getParameterCount() == 0 ? execute.newInstance()
            : (execute.getParameterCount() == 2 ? execute.newInstance(folder, logger) : execute.newInstance(paramFile ? folder : logger)));
        execute.setAccessible(access);
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
        String[] path = wrapper.getDescriptor().getPluginClass().split("\\.");
        String packageName = String.join(".", Arrays.subArray(size -> new String[size], path, 0, path.length - 1)) + ".config";
        Set<Class<? extends Config>> classes = provider.of(wrapper, packageName).getSubTypesOf(Config.class);
        if (classes.isEmpty()) {
            return this;
        }
        ArrayList<Config> configList = new ArrayList<>();
        for (Class<? extends Config> clazz : classes) {
            Config config = null;
            try {
                config = load0(plugin.getDataLocation(), clazz);
            } catch (Exception e) {
                logger.log(e);
            }
            if (configList.stream().anyMatch(current -> current.getClass().getName().equals(clazz.getName()))) {
                continue;
            }
            configList.add(config);
        }
        Config[] configs = sort(configList);
        for (Config config : configs) {
            ConfigTimer.TIMER.load(config);
        }
        return this;
    }

    private Config[] sort(ArrayList<Config> configs) {
        Config[] output = new Config[configs.size()];
        Config[] waiting = new Config[configs.size()];
        int id = 0, wait = 0;
        for (int index = 0; index < output.length; index++) {
            Config config = configs.get(index);
            Class<?> dependency0 = config.loadAfter();
            if (dependency0 != null) {
                if (index == 0) {
                    waiting[wait++] = config;
                    continue;
                }
                boolean found = false;
                for (int filter = 0; filter < id; filter++) {
                    Config possible = output[filter];
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
                Config current = waiting[waiter];
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
    public <E extends Config> E get(Class<E> config) {
        synchronized (configs) {
            return (E) configs.get(config);
        }
    }

    public <E extends Config> Optional<E> optional(Class<E> config) {
        return Optional.ofNullable(get(config));
    }

}
