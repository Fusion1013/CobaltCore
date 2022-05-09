package se.fusion1013.plugin.cobaltcore.database.setting;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.settings.PlayerSettingHolder;

import java.util.Map;
import java.util.UUID;

public interface ISettingDao extends IDao {

    /**
     * Gets all <code>PlayerSettingHolder</code>'s from the database.
     *
     * @return a map of <code>PlayerSettingHolder</code>'s.
     */
    Map<UUID, PlayerSettingHolder> getSettings();

    /**
     * Saves a Map of <code>PlayerSettingHolder</code>'s to the database.
     *
     * @param settings the settings to save.
     */
    void saveSettings(Map<UUID, PlayerSettingHolder> settings);

    /**
     * Removes a setting from all players.
     *
     * @param setting the setting to remove.
     */
    void removeSetting(String setting);

    /**
     * Removes a setting for a certain player from the database.
     *
     * @param player the player to remove the setting for.
     * @param setting the setting to remove.
     */
    void removeSetting(Player player, String setting);

    /**
     * Gets a setting from the player settings.
     *
     * @param player the player to get the setting from.
     * @param setting the setting to get.
     * @return the value of the setting. Empty string if not found.
     */
    String getSetting(Player player, String setting);

    /**
     * Sets a player setting in the database.
     *
     * @param player the player to set the setting for.
     * @param setting the setting to set.
     * @param value the value to set the setting to.
     */
    void setSetting(Player player, String setting, String value);

    @Override
    default String getId() { return "setting"; }

}
