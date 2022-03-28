package se.fusion1013.plugin.cobaltcore.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.Database;
import se.fusion1013.plugin.cobaltcore.database.SQLite;
import se.fusion1013.plugin.cobaltcore.manager.ConfigManager;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;
import se.fusion1013.plugin.cobaltcore.util.VersionUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

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
                .withSubcommand(createUpdateCommand())
                .withSubcommand(createLocaleCommand())
                .withSubcommand(createItemCommand())
                //.withSubcommand(createNpcCommand())
                .register();
    }

    private static CommandAPICommand createNpcCommand() {
        return new CommandAPICommand("npc")
                .executesPlayer(((sender, args) -> {
                    NPC npc = NPCLib.getInstance().generateNPC(sender, CobaltCore.getInstance(), UUID.randomUUID().toString(), sender.getLocation());

                    npc.setSkin(new NPC.Skin("ewogICJ0aW1lc3RhbXAiIDogMTYxNDA3NzM1MjgzOSwKICAicHJvZmlsZUlkIiA6ICJkNGY1ZGQ2YzVhYjE0NTNlYmJiNTg2ZTU2NzVkMDUyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICIwMDAwMDAwMDAwMDAwMDBQIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJjNjc0OTM0ZTBjYmE3MDU4YzA4YjQwYmIwYzA0Mzk3OWRkOTZkOThiMmExZmMxNzYwNDJiMTVkMjllNjEyZjciCiAgICB9CiAgfQp9", "ftCGwl52G/z+TpPv+OvhOILXguWTckmb9UuiF4bwUMr07Iz8/iXaIKv4ElFNYJlJzs0cSKTKgWkKYqih2U0sDLO2gA8NGOItJ4WkRgh4Grnut7o9cLtm9FzljFMNgg7rmXlWwla8mp5JDgQIR36vfDf6zirFsxmM2AKRolO34S7322V3lnhf2exg6tA7iRIF4M5TcJReDYatpg6F/gaOlOr16TXQtfSAt/970OLEe5V/Syexe3D6D0p8GHX3iwurIoShG7g8pjkoXxIqDVg6Mw1hWD3fqgV9xiM1Y7natBefdL9wN7afbLstQNentPRHQDPUy0o0H2ceI/6mhHhbSb1dzRkkBMtwGg7WciiYeQuEg1I31z0YJr7pAHG2Inkx+7l4DoLIet3naiSbzzKRBgBxBy2TryFjwPioYF964GAT3AJLdHnby5FWjyVjj+Qxci80JnuXwb4220Wt4f4vFLIgdP+5s/07FgNQ5J92YGMMrgf6nS2CcpAIFDDWcYy3LUKX141DafEx5ZNgZlXxiZjI8nD2sQhryVo1yEbZU3Y6ZHvdrMDzXy6z2Po/6lesf4Erg9dTWnjGBvsJf/KcB3xk6r+QdVYcGKwo9uTIzdE4Q+1c94N2oWnbR+QkLaudyDmsuNum9vRqVGgCPbtlLtFGhZFFe0DADQjx4CK1kM4="));
                    npc.setFollowLookType(NPC.FollowLookType.NEAREST_PLAYER);

                    npc.create();
                    npc.show();

                    npc.followPlayer();
                    npc.update();
                }));
    }

    // ----- CGIVE COMMAND -----

    private static CommandAPICommand createItemCommand() {
        return new CommandAPICommand("item")
                .withPermission("commands.core.item")
                .withArguments(new StringArgument("item_name").replaceSuggestions(info -> CustomItemManager.getCustomItemNames()))
                .executesPlayer(CobaltCommand::giveItem);
    }

    /**
     * Gives a specific magick item to the player.
     *
     * @param player the player to give the item to.
     * @param args the item to give the player.
     */
    private static void giveItem(Player player, Object[] args){
        String itemName = (String)args[0];
        ItemStack is = CustomItemManager.getCustomItemStack(itemName);
        if (is != null) player.getInventory().addItem(is);
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
                .withArguments(new StringArgument("table").replaceSuggestions(info -> Database.getDatabaseTables()))
                .executes(((sender, args) -> {
                    SQLite.dropTable((String)args[0]);
                    CobaltCore.getInstance().getRDatabase().load();
                    StringPlaceholders placeholders = StringPlaceholders.builder().addPlaceholder("table", (String)args[0]).build();
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
    private static void executeTablesCommand(Player p, Object[] args) {
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
                .withArguments(new StringArgument("key").replaceSuggestions(info -> configKeys))
                .executesPlayer((sender, args) -> {
                    executeGetConfigValueCommand(sender, args, configKey);
                });

        // Command to edit values in the config file
        CommandAPICommand editBooleanCommand = new CommandAPICommand("edit_boolean")
                .withPermission("cobalt.core.commands.cobalt.config.edit")
                .withArguments(new StringArgument("key").replaceSuggestions(info -> configKeys))
                .withArguments(new BooleanArgument("value"))
                .executesPlayer(((sender, args) -> {
                    executeEditBooleanConfigKeyCommand(sender, args, configKey);
                }));

        // Command to edit values in the config file
        CommandAPICommand editStringCommand = new CommandAPICommand("edit_string")
                .withPermission("cobalt.core.commands.cobalt.config.edit");

        // Edit values
        for (String key : configKeys) {
            editStringCommand.withSubcommand(new CommandAPICommand(key)
                    .withArguments(new GreedyStringArgument("value"))
                    .executesPlayer(((sender, args) -> {
                        executeEditStringConfigKeyCommand(sender, key, (String)args[0], configKey);
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
