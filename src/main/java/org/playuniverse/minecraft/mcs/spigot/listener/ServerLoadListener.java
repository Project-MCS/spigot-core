package org.playuniverse.minecraft.mcs.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;

import com.syntaxphoenix.syntaxapi.logging.ILogger;

public class ServerLoadListener implements Listener {

    private final PluginBase<?> core;

    public ServerLoadListener(PluginBase<?> core) {
        this.core = core;
    }

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        ILogger logger = core.getPluginLogger();
        logger.log("Server is fully started!");
        core.readyPlugins();
        logger.log("Everything is ready and setup now!");
    }

}
