package se.fusion1013.plugin.cobaltcore.commands.settings;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.settings.Setting;
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
        CommandAPICommand command = new CommandAPICommand("set")
                .withPermission("commands.core.setting.set");
        for (String settingKey : SettingsManager.getSettingNames()) command.withSubcommand(createSetSubCommand(settingKey));
        return command;
    }

    private static CommandAPICommand createSetSubCommand(String settingKey) {
        CommandAPICommand command = new CommandAPICommand(settingKey);
        Setting<?> setting = SettingsManager.getSetting(settingKey);
        command.withArguments(new StringArgument("value").replaceSuggestions(ArgumentSuggestions.strings(info -> setting.getValidOptions())));
        command.executesPlayer(((sender, args) -> {
            // Attempt to set the new value
            String oldValue = SettingsManager.getPlayerSetting(sender, settingKey);
            boolean success = SettingsManager.setPlayerSetting(sender, settingKey, (String)args.args()[0]);

            // Create placeholder with variables
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("key", settingKey)
                    .addPlaceholder("old_value", oldValue)
                    .addPlaceholder("value", args.args()[0])
                    .build();

            // Depending on if the setting change was a success or not, give appropriate feedback
            if (success) {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.setting.set.success", placeholders);
            } else {
                LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.setting.set.fail", placeholders);
            }
        }));
        return command;
    }

    private static CommandAPICommand createGetCommand() {
        return new CommandAPICommand("get")
                .withPermission("commands.core.setting.get")
                .withArguments(new StringArgument("setting").replaceSuggestions(ArgumentSuggestions.strings(info -> SettingsManager.getSettingNames())))
                .executesPlayer(((sender, args) -> {
                    String value = SettingsManager.getPlayerSetting(sender, (String)args.args()[0]);
                    StringPlaceholders placeholders = StringPlaceholders.builder()
                            .addPlaceholder("key", args.args()[0])
                            .addPlaceholder("value", value)
                            .build();
                    LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.setting.get", placeholders);
                }));
    }
}
