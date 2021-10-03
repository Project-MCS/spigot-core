package org.playuniverse.minecraft.mcs.spigot.plugin.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.pf4j.ExtensionPoint;
import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftCommand;
import org.playuniverse.minecraft.mcs.spigot.command.listener.MinecraftInfo;
import org.playuniverse.minecraft.mcs.spigot.command.listener.redirect.NodeRedirect;
import org.playuniverse.minecraft.mcs.spigot.command.nodes.RootNode;
import org.playuniverse.minecraft.mcs.spigot.plugin.SpigotPlugin;
import org.playuniverse.minecraft.mcs.spigot.plugin.extension.helper.ExtensionHelper;
import org.playuniverse.minecraft.mcs.spigot.plugin.extension.info.CommandInfo;

public interface ICommandExtension extends ExtensionPoint {

    static final Predicate<String> COMMAND_NAME = Pattern.compile("[\\da-z_]+").asMatchPredicate();

    RootNode<MinecraftInfo> buildRoot(String name);

    default void configure(MinecraftCommand command) {}

    public static int[] register(SpigotPlugin<?> plugin) {
        List<ICommandExtension> extensions = plugin.getBase().getPluginManager().getExtensions(ICommandExtension.class);
        int[] output = new int[2];
        output[1] = extensions.size();
        if (extensions.isEmpty()) {
            output[0] = 0;
            return output;
        }
        String prefix = plugin.getId();
        PluginBase<?> base = plugin.getBase();
        int registered = 0;
        ArrayList<String> aliases = new ArrayList<>();
        for (ICommandExtension extension : extensions) {
            Optional<CommandInfo> infoOption = ExtensionHelper.getAnnotationOfMethod(CommandInfo.class, extension.getClass(), "buildRoot",
                String.class);
            if (infoOption.isEmpty()) {
                System.out.println("No info");
                continue; // Invalid command
            }
            CommandInfo info = infoOption.get();
            if (!COMMAND_NAME.test(info.name())) {
                System.out.println("Invalid name");
                continue; // Invalid command name
            }
            RootNode<MinecraftInfo> node = extension.buildRoot(info.name());
            String fallbackPrefix = info.prefix().isBlank() ? prefix : info.prefix();
            for (String alias : info.aliases()) {
                if (!COMMAND_NAME.test(alias)) {
                    continue;
                }
                aliases.add(alias);
            }
            MinecraftCommand command = new MinecraftCommand(new NodeRedirect(node, plugin), fallbackPrefix, base, info.name(),
                aliases.toArray(String[]::new));
            aliases.clear();
            extension.configure(command);
            if (!plugin.inject(command)) {
                System.out.println("Failed to inject");
                continue; // Unable to inject command
            }
            registered++;
        }
        output[0] = registered;
        return output;
    }

}
