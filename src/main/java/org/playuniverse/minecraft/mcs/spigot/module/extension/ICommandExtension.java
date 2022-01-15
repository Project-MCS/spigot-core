package org.playuniverse.minecraft.mcs.spigot.module.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.playuniverse.minecraft.mcs.spigot.command.BukkitCommand;
import org.playuniverse.minecraft.mcs.spigot.command.BukkitSource;
import org.playuniverse.minecraft.mcs.spigot.module.SpigotModule;
import org.playuniverse.minecraft.mcs.spigot.module.extension.helper.ExtensionHelper;
import org.playuniverse.minecraft.mcs.spigot.module.extension.info.CommandInfo;

import com.syntaxphoenix.avinity.command.connection.NodeConnection;
import com.syntaxphoenix.avinity.command.node.Root;
import com.syntaxphoenix.avinity.module.extension.ExtensionPoint;
import com.syntaxphoenix.avinity.module.extension.IExtension;

@ExtensionPoint
public interface ICommandExtension extends IExtension {

    static final Predicate<String> COMMAND_NAME = Pattern.compile("[\\da-z_]+").asMatchPredicate();

    Root<BukkitSource> buildRoot(String name);

    default void configure(BukkitCommand command) {}

    public static int[] register(SpigotModule<?> plugin) {
        List<ICommandExtension> extensions = plugin.getModuleManager().getExtensionManager().getExtensionsOf(plugin.getId(),
            ICommandExtension.class);
        int[] output = new int[2];
        output[1] = extensions.size();
        if (extensions.isEmpty()) {
            output[0] = 0;
            return output;
        }
        String prefix = plugin.getId();
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
            Root<BukkitSource> node = extension.buildRoot(info.name());
            String fallbackPrefix = info.prefix().isBlank() ? prefix : info.prefix();
            for (String alias : info.aliases()) {
                if (!COMMAND_NAME.test(alias)) {
                    continue;
                }
                aliases.add(alias);
            }
            BukkitCommand command = new BukkitCommand(plugin, new NodeConnection<>(node.build()), info.name(), fallbackPrefix,
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
