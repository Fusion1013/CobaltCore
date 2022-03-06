package se.fusion1013.plugin.cobaltcore.settings;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;

/**
 * Represents a Setting that can be set for a player.
 * @param <T> the type of the setting.
 */
public abstract class Setting<T> {

    // ----- VARIABLES -----

    private final CobaltPlugin owningPlugin;
    private final String id;
    private final String description;
    private final String permission;
    private final T defaultValue;
    private final T defaultValueNoPermission;
    private final String[] validOptions;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new setting with the given parameters.
     *
     * @param plugin the plugin that is creating the setting.
     * @param id the id of the setting.
     * @param description the description of the setting.
     * @param permission the permission node required to use this setting.
     * @param defaultValue the default value of this setting if the user has permission to use it.
     * @param defaultValueNoPermission the default value of this setting if the user does not have permission to use it.
     * @param validOptions the valid options that this setting can be set to.
     */
    public Setting(CobaltPlugin plugin, String id, String description, String permission, T defaultValue, T defaultValueNoPermission, String[] validOptions) {
        this.owningPlugin = plugin;
        this.id = id;
        this.description = description;
        this.permission = permission;
        this.defaultValue = defaultValue;
        this.defaultValueNoPermission = defaultValueNoPermission;
        this.validOptions = validOptions;
    }

    /**
     * Gets the value of the setting for the specified player.
     * @param player the player to get the setting value for.
     * @return the setting value for the given player.
     */
    public abstract T getValue(Player player);

    public boolean isValidOption(String value) {
        for (String s : validOptions) if (s.equalsIgnoreCase(value)) return true;
        return false;
    }

    public String[] getValidOptions() {
        return validOptions;
    }

    public CobaltPlugin getOwningPlugin() {
        return owningPlugin;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getDefaultValueNoPermission() {
        return defaultValueNoPermission;
    }
}
