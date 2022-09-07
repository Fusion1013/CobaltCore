package se.fusion1013.plugin.cobaltcore.settings;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;

public class StringSetting extends Setting<String> {

    /**
     * Creates a new setting with the given parameters.
     *
     * @param plugin                   the plugin that is creating the setting.
     * @param id                       the id of the setting.
     * @param description              the description of the setting.
     * @param permission               the permission node required to use this setting.
     * @param defaultValue             the default value of this setting if the user has permission to use it.
     * @param defaultValueNoPermission the default value of this setting if the user does not have permission to use it.
     * @param validOptions             the valid options that this setting can be set to.
     */
    public StringSetting(CobaltPlugin plugin, String id, String description, String permission, String defaultValue, String defaultValueNoPermission, String[] validOptions) {
        super(plugin, id, description, permission, defaultValue, defaultValueNoPermission, validOptions);
    }

    @Override
    public String getValue(Player player) {
        return SettingsManager.getPlayerSetting(player, this);
    }
}
