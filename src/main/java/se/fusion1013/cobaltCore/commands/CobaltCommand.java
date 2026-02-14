package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.components.ComponentManager;
import se.fusion1013.cobaltCore.entity.CustomEntityManager;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

import java.util.Optional;
import java.util.function.Supplier;

public class CobaltCommand {

    public static void register() {
        new CommandAPICommand("cobalt")
                .withSubcommand(createReloadCommand())
                .register();
    }

    // ##### RELOAD COMMAND #####

    private static CommandAPICommand createReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("commands.core.reload")
                .withSubcommand(new CommandAPICommand("entities")
                        .withOptionalArguments(new GreedyStringArgument("options"))
                        .executes(CobaltCommand::reloadEntities))
                .withSubcommand(new CommandAPICommand("items")
                        .withOptionalArguments(new GreedyStringArgument("options"))
                        .executes(CobaltCommand::reloadItems))
                .withSubcommand(new CommandAPICommand("locale")
                        .withOptionalArguments(new GreedyStringArgument("options"))
                        .executes(CobaltCommand::reloadLocale))
                .withSubcommand(new CommandAPICommand("components")
                        .withOptionalArguments(new GreedyStringArgument("options"))
                        .executes(CobaltCommand::reloadComponents));
    }

    private static void reloadEntities(CommandSender commandSender, CommandArguments commandArguments) {
        reload(commandSender, commandArguments, "Entities", CustomEntityManager::reloadEntities, CustomEntityManager::getCustomEntityNames);
    }

    private static void reloadItems(CommandSender commandSender, CommandArguments commandArguments) {
        reload(commandSender, commandArguments, "Items", CustomItemManager::reloadItems, CustomItemManager::getCustomItemNames);
    }

    private static void reloadComponents(CommandSender commandSender, CommandArguments commandArguments) {
        reload(commandSender, commandArguments, "Components", () -> {
            ComponentManager.reloadComponents();
            CustomItemManager.reloadItems();
        }, ComponentManager::getComponentNames);
    }

    private static void reloadLocale(CommandSender commandSender, CommandArguments commandArguments) {
        reload(commandSender, commandArguments, "Locale", LocaleManager::resetLocale, LocaleManager::getLocaleStrings);
    }

    private static void reload(CommandSender sender, CommandArguments args, String type, Runnable run, Supplier<String[]> verboseResult) {
        try {
            Optional<Object> options = args.getOptional("options");
            String commandOptions = options.map(o -> (String) o).orElse("");

            boolean verbose = commandOptions.contains("-v");

            run.run();

            if (sender instanceof Player player) {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.core.reload",
                        StringPlaceholders.builder()
                                .addPlaceholder("type", type)
                                .addPlaceholder("count", verboseResult.get().length)
                                .build()
                );
                if (verbose) {
                    for (String s : verboseResult.get()) {
                        LocaleManager.getInstance().sendMessage("", player, "commands.core.reload.item", StringPlaceholders.builder().addPlaceholder("name", s).build());
                    }
                }
            }
        } catch (Exception ex) {
            CobaltCore.getInstance().getLogger().warning("Encountered issue while reloading " + type + ": " + ex.getMessage());

            if (sender instanceof Player player) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("action", "Reloading " + type)
                        .addPlaceholder("stacktrace", ex.getMessage())
                        .build();
                LocaleManager.getInstance().sendMessage(player, "commands.error", placeholders);
            }
        }
    }

}
