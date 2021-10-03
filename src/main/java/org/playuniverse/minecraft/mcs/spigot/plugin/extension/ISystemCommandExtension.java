package org.playuniverse.minecraft.mcs.spigot.plugin.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.pf4j.ExtensionPoint;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.CommandManager;
import org.playuniverse.minecraft.mcs.spigot.command.CommandState;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftInfo;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.RootNode;
import org.playuniverse.minecraft.mcs.spigot.plugin.SpigotPlugin;
import org.playuniverse.minecraft.mcs.spigot.plugin.extension.helper.ExtensionHelper;
import org.playuniverse.minecraft.mcs.spigot.plugin.extension.info.CommandInfo;

public interface ISystemCommandExtension extends ExtensionPoint {

    static final Predicate<String> COMMAND_NAME = Pattern.compile("[\\da-z_]+").asMatchPredicate();

    RootNode<MinecraftInfo> buildRoot(String name);

    public static int[] register(SpigotPlugin<?> plugin) {
        List<ISystemCommandExtension> extensions = plugin.getBase().getPluginManager().getExtensions(ISystemCommandExtension.class, plugin.getId());
        int[] output = new int[2];
        output[1] = extensions.size();
        if (extensions.isEmpty()) {
            output[0] = 0;
            return output;
        }
        PluginBase<?> base = plugin.getBase();
        CommandManager<MinecraftInfo> commandManager = base.getCommandManager();
        int registered = 0;
        ArrayList<String> aliases = new ArrayList<>();
        for (ISystemCommandExtension extension : extensions) {
            Optional<CommandInfo> infoOption = ExtensionHelper.getAnnotationOfMethod(CommandInfo.class, extension.getClass(), "buildRoot",
                String.class);
            if (infoOption.isEmpty()) {
                continue; // Invalid command
            }
            CommandInfo info = infoOption.get();
            if (!COMMAND_NAME.test(info.name())) {
                continue; // Invalid command name
            }
            RootNode<MinecraftInfo> node = extension.buildRoot(info.name());
            for (String alias : info.aliases()) {
                if (!COMMAND_NAME.test(alias)) {
                    continue;
                }
                aliases.add(alias);
            }
            CommandState state = commandManager.register(node, aliases.toArray(String[]::new));
            aliases.clear();
            if (state == CommandState.FAILED) {
                continue; // Unable to inject command
            }
            registered++;
        }
        output[0] = registered;
        return output;
    }

}
