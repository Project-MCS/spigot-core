package org.playuniverse.minecraft.mcs.spigot.listener;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.pf4j.PluginWrapper;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.plugin.SpigotPlugin;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

public class ServerLoadListener implements Listener {

    private final PluginBase<?> core;

    public ServerLoadListener(PluginBase<?> core) {
        this.core = core;
    }

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        HashMap<PluginWrapper, Throwable> map = new HashMap<>();
        for (PluginWrapper wrapper : core.getPluginManager().getStartedPlugins()) {
            SpigotPlugin<?> plugin = SpigotPlugin.getByWrapper(wrapper);
            if (plugin == null) {
                continue;
            }
            try {
                plugin.ready();
            } catch (Throwable throwable) {
                map.put(wrapper, throwable);
            }
        }
        if (map.isEmpty()) {
            return;
        }
        ILogger logger = core.getPluginLogger();
        logger.log(LogTypeId.ERROR, "Some plugins failed to ready up...");
        logger.log(LogTypeId.ERROR, "");
        PluginWrapper[] wrappers = map.keySet().toArray(PluginWrapper[]::new);
        for (int index = 0; index < wrappers.length; index++) {
            PluginWrapper wrapper = wrappers[index];
            logger.log(LogTypeId.ERROR, "===============================================");
            logger.log(LogTypeId.ERROR, "");
            logger.log(LogTypeId.ERROR, "Addon '" + wrapper.getPluginId() + "' by " + wrapper.getDescriptor().getProvider());
            logger.log(LogTypeId.ERROR, "");
            logger.log(LogTypeId.ERROR, "-----------------------------------------------");
            logger.log(LogTypeId.ERROR, map.get(wrapper));
            logger.log(LogTypeId.ERROR, "===============================================");
            if (index + 1 != wrappers.length) {
                logger.log(LogTypeId.ERROR, "");
                logger.log(LogTypeId.ERROR, "");
            }
        }
        logger.log(LogTypeId.ERROR, "");
        logger.log(LogTypeId.ERROR, "Hope you can fix those soon!");
    }

}
