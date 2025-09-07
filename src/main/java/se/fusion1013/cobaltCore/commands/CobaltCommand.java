package se.fusion1013.cobaltCore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.item.loaders.ItemLoader;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class CobaltCommand {

    public static void register() {
        new CommandAPICommand("cobalt")
                .withSubcommand(createLocaleCommand())
                .withSubcommand(createReloadCommand())
                .register();
    }

    // ##### RELOAD COMMAND #####

    private static CommandAPICommand createReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("commands.core.reload")
                .withSubcommand(new CommandAPICommand("items")
                        .executes(CobaltCommand::reloadItems));
    }

    private static void reloadItems(CommandSender sender, CommandArguments args) {
        try {
            CustomItemManager.reloadItems();
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cobalt.reload.items");
        } catch (Exception ex) {
            CobaltCore.getInstance().getLogger().warning("Encountered issue while reloading items: " + ex.getMessage());

            if (sender instanceof Player player) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("action", "Reloading Items")
                        .addPlaceholder("stacktrace", ex.getMessage())
                        .build();
                LocaleManager.getInstance().sendMessage(player, "commands.error", placeholders);
            }
        }
    }

    // ##### LOCALE COMMAND #####

    private static CommandAPICommand createLocaleCommand() {
        return new CommandAPICommand("locale")
                .withPermission("commands.core.locale")
                .withSubcommand(createLocaleResetCommand());
    }

    private static CommandAPICommand createLocaleResetCommand() {
        return new CommandAPICommand("reset")
                .withPermission("commands.core.locale.reset")
                .executesPlayer((sender, args) -> {
                    LocaleManager.resetLocale();
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("count", LocaleManager.getLocaleFileCount())
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.cobalt.locale.reset.result", placeholders);
                });
    }

}
