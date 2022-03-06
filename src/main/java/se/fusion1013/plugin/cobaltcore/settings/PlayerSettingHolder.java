package se.fusion1013.plugin.cobaltcore.settings;

import java.util.HashMap;
import java.util.Map;

public class PlayerSettingHolder {

    // ----- VARIABLES -----

    private final Map<String, String> playerSettings;

    // ----- CONSTRUCTORS -----

    public PlayerSettingHolder() {
        this.playerSettings = new HashMap<>();
    }

    public PlayerSettingHolder(Map<String, String> playerSettings) {
        this.playerSettings = playerSettings;
    }

    // ----- GETTERS / SETTERS -----

    public Map<String, String> getPlayerSettings() {
        return playerSettings;
    }

    public void setPlayerSetting(String key, String value) {
        playerSettings.put(key, value);
    }

    public boolean setPlayerSetting(Setting<?> setting, String value) {
        if (setting == null) return false;

        if (setting.isValidOption(value)) {
            playerSettings.put(setting.getId(), value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the value in string format.
     *
     * @param setting the setting to get the value of.
     * @param hasPermission whether the player has permission to even use this setting.
     * @return the value in string format.
     */
    public String getValueOrDefault(Setting<?> setting, boolean hasPermission) {
        if (!hasPermission) return setting.getDefaultValueNoPermission().toString();

        String value = playerSettings.get(setting.getId());
        if (value != null) {
            return value;
        }
        return setting.getDefaultValue().toString();
    }
}
