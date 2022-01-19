package se.fusion1013.plugin.cobaltcore;

import org.bukkit.plugin.Plugin;

public interface CobaltPlugin extends Plugin {

    /**
     * Registers the <code>CobaltCommand</code> of this <code>CobaltPlugin</code> to be handled by CobaltCore's <code>CommandManager</code>.
     */
    default void registerCommands() {}

    default void registerSettings() {}

    /**
     * Registers the event Listeners of this <code>CobaltPlugin</code> using Bukkit's event system.
     */
    default void registerListeners() {}

    default void reloadManagers() {}

    default void initDatabaseTables() {}
}
