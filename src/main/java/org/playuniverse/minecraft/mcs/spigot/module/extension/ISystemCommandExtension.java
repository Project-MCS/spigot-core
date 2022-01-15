package org.playuniverse.minecraft.mcs.spigot.module.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.playuniverse.minecraft.mcs.spigot.base.PluginBase;
import org.playuniverse.minecraft.mcs.spigot.command.BukkitSource;
import org.playuniverse.minecraft.mcs.spigot.module.SpigotModule;
import org.playuniverse.minecraft.mcs.spigot.module.extension.helper.ExtensionHelper;
import org.playuniverse.minecraft.mcs.spigot.module.extension.info.CommandInfo;

import com.syntaxphoenix.avinity.command.CommandManager;
import com.syntaxphoenix.avinity.command.CommandState;
import com.syntaxphoenix.avinity.command.node.Root;
import com.syntaxphoenix.avinity.module.extension.ExtensionPoint;
import com.syntaxphoenix.avinity.module.extension.IExtension;

@ExtensionPoint
public interface ISystemCommandExtension extends IExtension {

    static final Predicate<String> COMMAND_NAME = Pattern.compile("[\\da-z_]+").asMatchPredicate();

    Root<BukkitSource> buildRoot(String name);

    public static int[] register(SpigotModule<?> plugin) {
        List<ISystemCommandExtension> extensions = plugin.getModuleManager().getExtensionManager().getExtensionsOf(plugin.getId(),
            ISystemCommandExtension.class);
        int[] output = new int[2];
        output[1] = extensions.size();
        if (extensions.isEmpty()) {
            output[0] = 0;
            return output;
        }
        PluginBase<?> base = plugin.getBase();
        CommandManager<BukkitSource> commandManager = base.getCommandManager();
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
            Root<BukkitSource> node = extension.buildRoot(info.name());
            for (String alias : info.aliases()) {
                if (!COMMAND_NAME.test(alias)) {
                    continue;
                }
                aliases.add(alias);
            }
            CommandState state = commandManager.register(node.build(), aliases.toArray(String[]::new));
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
