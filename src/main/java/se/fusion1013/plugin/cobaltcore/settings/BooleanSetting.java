package se.fusion1013.plugin.cobaltcore.settings;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;

/**
 * Represents a boolean setting that can be set for a player.
 */
public class BooleanSetting extends Setting<Boolean> {

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>BooleanSetting</code> with the given parameters.
     * @param plugin the plugin that is creating the setting.
     * @param id the id of the setting.
     * @param description the description of the setting.
     * @param permission the permission node required to use the setting.
     * @param defaultValue the default value of the setting if the user has permission to use it.
     * @param defaultValueNoPermission the default value of the setting if the user does not have permission to use it.
     */
    public BooleanSetting(CobaltPlugin plugin, String id, String description, String permission, boolean defaultValue, boolean defaultValueNoPermission) {
        super(plugin, id, description, permission, defaultValue, defaultValueNoPermission, new String[]{"true", "false"});
    }

    // ----- GETTERS / SETTERS -----

    /**
     * Gets the boolean value of this setting for the specific player.
     *
     * @param player the player to get the setting from.
     * @return the boolean value of the setting.
     */
    @Override
    public Boolean getValue(Player player) {
        return Boolean.parseBoolean(SettingsManager.getPlayerSetting(player, this));
    }
}
