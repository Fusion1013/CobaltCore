package se.fusion1013.plugin.cobaltcore.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.action.encounter.EncounterManager;
import se.fusion1013.plugin.cobaltcore.bar.BossBarManager;
import se.fusion1013.plugin.cobaltcore.commands.cgive.CGiveCommand;
import se.fusion1013.plugin.cobaltcore.commands.encounter.EncounterCommand;
import se.fusion1013.plugin.cobaltcore.database.system.Database;
import se.fusion1013.plugin.cobaltcore.database.system.SQLite;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.debug.DebugManager;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.category.IItemCategory;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.ItemUtil;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.VersionUtil;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CobaltCommand {

    // ----- REGISTER -----

    /**
     * Registers the cobalt command.
     */
    public static void register() {
        // Main cobalt command
        new CommandAPICommand("cobalt")
                .withSubcommand(createVersionCommand())
                .withSubcommand(createConfigCommand())
                .withSubcommand(createDatabaseCommand())
                // .withSubcommand(createUpdateCommand())
                .withSubcommand(createLocaleCommand())
                .withSubcommand(CGiveCommand.createCgiveCommand())
                .withSubcommand(createStructureCommand())
                .withSubcommand(createReloadCommand())
                .withSubcommand(EncounterCommand.createEncounterCommand())
                .register();
    }

    // ----- RELOAD COMMAND -----

    private static CommandAPICommand createReloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission("commands.core.reload")
                .withSubcommand(new CommandAPICommand("items")
                        .executes(CobaltCommand::reloadItems))
                .withSubcommand(new CommandAPICommand("encounters")
                        .executes(CobaltCommand::reloadEncounters));
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

    private static void reloadEncounters(CommandSender sender, CommandArguments args) {
        try {
            EncounterManager.reloadEncounters();
            if (sender instanceof Player player) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "commands.cobalt.reload.encounters");
        } catch (Exception ex) {
            CobaltCore.getInstance().getLogger().warning("Encountered issue while reloading encounters: " + ex.getMessage());

            if (sender instanceof Player player) {
                StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("action", "Reloading Encounters")
                        .addPlaceholder("stacktrace", ex.getMessage())
                        .build();
                LocaleManager.getInstance().sendMessage(player, "commands.error", placeholders);
            }
        }
    }

    // ----- STRUCTURE COMMAND -----

    private static CommandAPICommand createStructureCommand() {
        return new CommandAPICommand("structure")
                .withPermission("commands.core.structure")
                .withSubcommand(new CommandAPICommand("place")
                        .withArguments(new StringArgument("structure").replaceSuggestions(ArgumentSuggestions.strings(info -> StructureManager.getRegisteredStructureNames())))
                        .withArguments(new LocationArgument("location", LocationType.BLOCK_POSITION))
                        .executes(((sender, args) -> {
                            String structureName = (String) args.args()[0];
                            Location location = (Location) args.args()[1];
                            StructureManager.placeStructure(structureName, location);
                        })));
    }

    // ----- LOCALE COMMAND -----

    private static CommandAPICommand createLocaleCommand() {
        return new CommandAPICommand("locale")
                .withPermission("commands.core.locale")
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("commands.core.locale.reset")
                        .executesPlayer(((sender, args) -> {
                            LocaleManager.resetLocale();
                            StringPlaceholders placeholders = StringPlaceholders.builder()
                                    .addPlaceholder("count", LocaleManager.getLocaleFileCount())
                                    .build();
                            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), sender, "commands.cobalt.locale.reset.result", placeholders);
                        })));
    }

    // ----- UPDATE COMMAND -----

    /**
     * Creates the update command.
     * This command pushes a file to the server plugin folder.
     *
     * @return the update command.
     */
    private static CommandAPICommand createUpdateCommand() {
        return new CommandAPICommand("update")
                .withPermission("commands.core.update")
                .withArguments(new StringArgument("file name"))
                .withArguments(new GreedyStringArgument("url"))
                .executes(((sender, args) -> {
                    /*
                    try {
                        File file1 = new File("plugins", (String)args[0]); // TODO: Replace path getting
                        FileUtils.copyURLToFile(new URL((String)args[1]), file1);

                        if (sender instanceof Player p) {
                            StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("file", args[0]).build();
                            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), p, "commands.cobalt.update", placeholders);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                     */
                }));
    }

    // ----- DATABASE COMMAND -----

    /**
     * Creates the database command.
     * This command contains various subcommands that perform operations on the database.
     *
     * @return the database command.
     */
    private static CommandAPICommand createDatabaseCommand() {
        return new CommandAPICommand("database")
                .withPermission("cobalt.core.commands.cobalt.database")
                .withSubcommand(createDatabaseResetCommand())
                .withSubcommand(createDatabaseTablesCommand());
    }

    /**
     * Creates the reset command.
     * Resets all tables in the database.
     *
     * @return the reset command.
     */
    private static CommandAPICommand createDatabaseResetCommand() {
        return new CommandAPICommand("reset")
                .withPermission("cobalt.core.commands.cobalt.database.reset")
                .withArguments(new StringArgument("table").replaceSuggestions(ArgumentSuggestions.strings(info -> Database.getDatabaseTables())))
                .executes(((sender, args) -> {
                    SQLite.dropTable((String)args.args()[0]);
                    CobaltCore.getInstance().getSQLDatabase().load();
                    StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("table", (String)args.args()[0]).build();
                    if (sender instanceof Player p) LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), p, "database.reset_table", placeholders);
                }));
    }

    /**
     * Creates the tables command.
     * Prints the names of all tables in the database.
     *
     * @return the tables command.
     */
    private static CommandAPICommand createDatabaseTablesCommand() {
        return new CommandAPICommand("tables")
                .withPermission("cobalt.core.commands.cobalt.database.tables")
                .executesPlayer(CobaltCommand::executeTablesCommand);
    }

    /**
     * Executes the tables command.
     * Prints a list of all tables in the database to the player.
     *
     * @param p the player that is executing the command.
     * @param args the command arguments.
     */
    private static void executeTablesCommand(Player p, CommandArguments args) {
        String[] tables = Database.getDatabaseTables();

        StringBuilder tablesBuilder = new StringBuilder();
        for (String s : tables) {
            tablesBuilder.append("&3").append(s).append("&7").append(", ");
        }
        String tablesString = tablesBuilder.substring(0, Math.max(0, tablesBuilder.length() - 2));

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("count", tables.length)
                .addPlaceholder("tables", tablesString)
                .build();

        LocaleManager localeManager = LocaleManager.getInstance();
        localeManager.sendMessage(p, "database.tables", placeholders);
    }

    // ----- VERSION COMMAND -----

    /**
     * Creates the version command.
     * This command prints the plugin version and github issue link.
     *
     * @return the version command.
     */
    private static CommandAPICommand createVersionCommand() {
        return new CommandAPICommand("version")
                .executesPlayer(((sender, args) -> {
                    VersionUtil.printVersion(CobaltCore.getInstance(), sender);
                }));
    }

    // ----- CONFIG COMMAND -----

    /**
     * Creates the config command.
     * Contains a list of all configuration files as subcommands, enabling users to perform operations on them via a variety of subcommands.
     *
     * @return the config command.
     */
    private static CommandAPICommand createConfigCommand() {
        String[] configNames = ConfigManager.getInstance().getConfigNames();

        CommandAPICommand configCommand = new CommandAPICommand("config")
                .withPermission("cobalt.core.commands.cobalt.config");

        // Loops through all configuration files and creates subcommands for them.
        for (String s : configNames) {
            configCommand.withSubcommand(createConfigSubCommand(s));
        }

        return configCommand;
    }

    /**
     * Creates the config subcommand.
     * Allows the user to perform various operations on the specified configuration file.
     *
     * @param configKey the key of the configuration file.
     * @return the config subcommand.
     */
    private static CommandAPICommand createConfigSubCommand(String configKey) {
        String[] configKeys = ConfigManager.getInstance().getConfigKeys(configKey);

        // Command to get values from the config file
        CommandAPICommand getCommand = new CommandAPICommand("get")
                .withPermission("cobalt.core.commands.cobalt.config.get")
                .withArguments(new StringArgument("key").replaceSuggestions(ArgumentSuggestions.strings(info -> configKeys)))
                .executesPlayer((sender, args) -> {
                    executeGetConfigValueCommand(sender, args.args(), configKey);
                });

        // Command to edit values in the config file
        CommandAPICommand editBooleanCommand = new CommandAPICommand("edit_boolean")
                .withPermission("cobalt.core.commands.cobalt.config.edit")
                .withArguments(new StringArgument("key").replaceSuggestions(ArgumentSuggestions.strings(info -> configKeys)))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer(((sender, args) -> {
                    executeEditBooleanConfigKeyCommand(sender, args.args(), configKey);
                }));

        // Command to edit values in the config file
        CommandAPICommand editStringCommand = new CommandAPICommand("edit_string")
                .withPermission("cobalt.core.commands.cobalt.config.edit");

        // Edit values
        for (String key : configKeys) {
            editStringCommand.withSubcommand(new CommandAPICommand(key)
                    .withArguments(new GreedyStringArgument("value"))
                    .executesPlayer(((sender, args) -> {
                        executeEditStringConfigKeyCommand(sender, key, (String)args.args()[0], configKey);
                    })));
        }

        // Clear values
        for (String key : configKeys) {
            editStringCommand.withSubcommand(new CommandAPICommand(key)
                    .executesPlayer(((sender, args) -> {
                        executeEditStringConfigKeyCommand(sender, key, "", configKey);
                    })));
        }

        // The main command. Has the same name as the config file key
        return new CommandAPICommand(configKey)
                .withPermission("cobalt.core.commands.cobalt.config")
                .withSubcommand(getCommand)
                .withSubcommand(editBooleanCommand)
                .withSubcommand(editStringCommand);
    }

    /**
     * Executes the get config value command.
     * Gets the value of the config at the specified key.
     *
     * @param p the player that is executing the command.
     * @param args the command arguments.
     * @param configKey the configuration file key to get the value from.
     */
    private static void executeGetConfigValueCommand(Player p, Object[] args, String configKey){
        LocaleManager localeManager = LocaleManager.getInstance();

        String key = (String)args[0];
        Object value = ConfigManager.getInstance().getFromConfig(configKey, key);

        StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("key", key).addPlaceholder("value", value).build();
        localeManager.sendMessage(p, "commands.cobalt.config.get", placeholders);
    }

    /**
     * Executes the edit config key command.
     * Edits the value at the specified key in the specified configuration file.
     *
     * @param p the player that is executing the command.
     * @param key the key to the value.
     * @param value the value to change to.
     * @param configKey the configuration file to get the value from.
     */
    private static void executeEditStringConfigKeyCommand(Player p, String key, String value, String configKey){
        LocaleManager localeManager = LocaleManager.getInstance();

        Object oldValue = ConfigManager.getInstance().writeString(configKey, key, value);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("key", key)
                .addPlaceholder("value", value)
                .addPlaceholder("old_value", oldValue)
                .build();
        localeManager.sendMessage(p, "commands.cobalt.config.edit", placeholders);
    }

    private static void executeEditBooleanConfigKeyCommand(Player p, Object[] args, String configKey) {
        LocaleManager localeManager = LocaleManager.getInstance();

        String key = (String)args[0];
        boolean value = (boolean)args[1];

        Object oldValue = ConfigManager.getInstance().writeBoolean(configKey, key, value);

        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("key", key)
                .addPlaceholder("value", value)
                .addPlaceholder("old_value", oldValue)
                .build();
        localeManager.sendMessage(p, "commands.cobalt.config.edit", placeholders);
    }
}
