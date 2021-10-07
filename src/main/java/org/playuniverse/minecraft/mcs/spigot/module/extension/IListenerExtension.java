package org.playuniverse.minecraft.mcs.spigot.module.extension;

import java.util.List;
import java.util.Optional;

import org.playuniverse.minecraft.mcs.spigot.module.SpigotModule;
import org.playuniverse.minecraft.mcs.spigot.module.extension.helper.ExtensionHelper;
import org.playuniverse.minecraft.mcs.spigot.module.extension.info.EventInfo;

import com.syntaxphoenix.avinity.module.extension.ExtensionPoint;
import com.syntaxphoenix.avinity.module.extension.IExtension;
import com.syntaxphoenix.syntaxapi.event.EventListener;

@ExtensionPoint
public interface IListenerExtension extends IExtension, EventListener {

    static int[] register(SpigotModule<?> plugin) {
        List<IListenerExtension> extensions = plugin.getModuleManager().getExtensionManager().getExtensionsOf(plugin.getId(),
            IListenerExtension.class);
        int[] output = new int[2];
        output[1] = extensions.size();
        if (extensions.isEmpty()) {
            output[0] = 0;
            return output;
        }
        int registered = 0;
        for(IListenerExtension extension : extensions) {
            Optional<EventInfo> infoOption = ExtensionHelper.getAnnotation(extension.getClass(), EventInfo.class);
            if(infoOption.isEmpty()) {
                continue;
            }
            registered++;
            if(infoOption.get().bukkit()) {
                plugin.getBukkitManager().registerEvents(extension);
                continue;
            }
            plugin.getEventManager().registerEvents(extension);
        }
        output[0] = registered;
        return output;
    }

}
