package se.fusion1013.plugin.cobaltcore.commands.settings;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.SQLite;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class SettingCommand {

    // ----- REGISTER -----

    // TODO: Add a way to provide setting tab completion by adding a way that plugins can register settings (SettingsManager???)

    /**
     * Registers the setting command.
     */
    public static void register() {
        new CommandAPICommand("setting")
                .withAliases("settings")
                .withPermission("commands.core.setting")
                .withSubcommand(createSetCommand())
                .withSubcommand(createGetCommand())
                .register();
    }

    // ----- SET COMMAND -----

    /**
     * Creates the set command.
     * This commands sets a setting value for the executing player.
     *
     * @return the set command.
     */
    private static CommandAPICommand createSetCommand() { // TODO: Replace with subcommands that generate tab-completion
        return new CommandAPICommand("set")
                .withPermission("commands.core.setting.set")
                .withArguments(new StringArgument("setting").replaceSuggestions(info -> SettingsManager.getSettingSuggestions()))
                .withArguments(new StringArgument("value"))
                .executesPlayer(((sender, args) -> {

                    // Attempt to set the new value
                    String oldValue = SettingsManager.getPlayerSetting(sender, (String)args[0]);
                    boolean success = SettingsManager.setPlayerSetting(sender, (String)args[0], (String)args[1]);

                    // Create placeholder with variables
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("key", args[0])
                            .addPlaceholder("old_value", oldValue)
                            .addPlaceholder("value", args[1])
                            .build();

                    // Depending on if the setting change was a success or not, give appropriate feedback
                    if (success) {
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.setting.set.success", placeholders);
                    } else {
                        LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.setting.set.fail", placeholders);
                    }
                }));
    }

    private static CommandAPICommand createGetCommand() {
        return new CommandAPICommand("get")
                .withPermission("commands.core.setting.get")
                .withArguments(new StringArgument("setting").replaceSuggestions(info -> SettingsManager.getSettingSuggestions()))
                .executesPlayer(((sender, args) -> {
                    String value = SettingsManager.getPlayerSetting(sender, (String)args[0]);
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("key", args[0])
                            .addPlaceholder("value", value)
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.setting.get", placeholders);
                }));
    }
}
