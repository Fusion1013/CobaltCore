package se.fusion1013.plugin.cobaltcore.settings;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.setting.ISettingDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.SQLite;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsManager extends Manager {

    // ----- VARIABLES -----

    private static final Map<String, Setting<?>> registeredSettings = new HashMap<>(); // Stores all registered settings.
    private static Map<UUID, PlayerSettingHolder> playerSettings = new HashMap<>(); // Stores all player settings.

    // ----- SETTINGS -----

    public static final Setting<Boolean> TEST_BOOLEAN_SETTING = register(new BooleanSetting(CobaltCore.getInstance(), "test_boolean_setting",
            "This is a test boolean setting description", "core.setting.test_boolean_setting", false, false));

    // ----- REGISTER -----

    public static BooleanSetting register(BooleanSetting setting) {
        registeredSettings.put(setting.getId(), setting);
        return setting;
    }

    // ----- GETTERS / SETTERS -----

    public static Setting<?> getSetting(String key) {
        return registeredSettings.get(key);
    }

    public static String getPlayerSetting(Player player, String key) {
        PlayerSettingHolder holder = playerSettings.get(player.getUniqueId());

        if (holder == null) {
            holder = new PlayerSettingHolder();
            playerSettings.put(player.getUniqueId(), holder);
        }

        Setting<?> setting = registeredSettings.get(key);
        return holder.getValueOrDefault(setting, player.hasPermission(setting.getPermission()));
    }

    public static String getPlayerSetting(Player player, Setting<?> setting) {
        PlayerSettingHolder holder = playerSettings.get(player.getUniqueId());

        if (holder == null) {
            holder = new PlayerSettingHolder();
            playerSettings.put(player.getUniqueId(), holder);
        }

        return holder.getValueOrDefault(setting, player.hasPermission(setting.getPermission()));
    }

    public static boolean setPlayerSetting(Player player, String settingKey, String value) {
        PlayerSettingHolder holder = playerSettings.get(player.getUniqueId());

        if (holder == null) {
            holder = new PlayerSettingHolder();
            playerSettings.put(player.getUniqueId(), holder);
        }

        return holder.setPlayerSetting(registeredSettings.get(settingKey), value);
    }

    public static void setPlayerSettingForce(Player player, String settingKey, String value) {
        PlayerSettingHolder holder = playerSettings.get(player.getUniqueId());

        if (holder == null) {
            holder = new PlayerSettingHolder();
            playerSettings.put(player.getUniqueId(), holder);
        }

        holder.setPlayerSetting(settingKey, value);
    }

    public static boolean setPlayerSetting(Player player, Setting<?> setting, String value) {
        PlayerSettingHolder holder = playerSettings.get(player.getUniqueId());

        if (holder == null) {
            holder = new PlayerSettingHolder();
            playerSettings.put(player.getUniqueId(), holder);
        }

        return holder.setPlayerSetting(setting, value);
    }

    public static String[] getSettingSuggestions() {
        return registeredSettings.keySet().toArray(new String[0]);
    }

    // ----- CONSTRUCTORS -----

    public SettingsManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING

    @Override
    public void reload() {
        loadSettings();
    }

    @Override
    public void disable() {
        savePlayerSettings();
    }

    private void loadSettings() {
        playerSettings = DataManager.getInstance().getDao(ISettingDao.class).getSettings();
    }

    private void savePlayerSettings() {
        DataManager.getInstance().getDao(ISettingDao.class).saveSettings(playerSettings);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static SettingsManager INSTANCE = null;
    /**
     * Returns the object representing this <code>SettingsManager</code>.
     *
     * @return The object of this class
     */
    public static SettingsManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new SettingsManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
