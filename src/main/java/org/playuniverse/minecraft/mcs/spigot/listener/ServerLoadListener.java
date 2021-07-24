package org.playuniverse.minecraft.mcs.spigot.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;

public class ServerLoadListener implements Listener {

    private final PluginBase<?> core;

    public ServerLoadListener(PluginBase<?> core) {
        this.core = core;
    }

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        core.readyPlugins();
    }

}
