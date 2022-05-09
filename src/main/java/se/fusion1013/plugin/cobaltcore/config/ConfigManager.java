package se.fusion1013.plugin.cobaltcore.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager extends Manager {

    // ----- VARIABLES -----

    Map<String, File> configFileMap = new HashMap<>();
    Map<String, FileConfiguration> fileConfigurationMap = new HashMap<>();

    // ----- CONSTRUCTOR -----

    /**
     * Creates a new <code>ConfigManager</code>.
     *
     * @param cobaltCore the <code>CobaltCore</code> plugin.
     */
    public ConfigManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- LOGIC -----

    /**
     * Gets an array containing all config names.
     *
     * @return an array of config names.
     */
    public String[] getConfigNames() {
        return new ArrayList<>(fileConfigurationMap.keySet()).toArray(new String[0]);
    }

    /**
     * Gets an array containing all keys of the specified config.
     *
     * @param configKey the key of the config.
     * @return an array of keys.
     */
    public String[] getConfigKeys(String configKey) {
        FileConfiguration config = fileConfigurationMap.get(configKey);
        return new ArrayList<>(config.getKeys(true)).toArray(new String[0]);
    }

    /**
     * Gets an array containing all keys of the specified config.
     *
     * @param plugin the plugin that is getting the keys.
     * @param configName the name of the config.
     * @return an array of keys.
     */
    public String[] getConfigKeys(Plugin plugin, String configName) {
        FileConfiguration config = fileConfigurationMap.get(getConfigKey(plugin, configName));
        return new ArrayList<>(config.getKeys(true)).toArray(new String[0]);
    }

    /**
     * Gets a value from the specified config file.
     *
     * @param configKey the key of the config file.
     * @param key the key where the value is stored.
     * @return the value.
     */
    public Object getFromConfig(String configKey, String key) {
        FileConfiguration config = fileConfigurationMap.get(configKey);
        return config.get(key);
    }

    /**
     * Gets a value from the specified config file.
     *
     * @param plugin the plugin that is getting the value.
     * @param configName the name of the config file.
     * @param key the key where the value is stored.
     * @return the value.
     */
    public Object getFromConfig(Plugin plugin, String configName, String key) {
        FileConfiguration config = fileConfigurationMap.get(getConfigKey(plugin, configName));
        return config.get(key);
    }

    /**
     * Gets a boolean value from the specified config file.
     *
     * @param plugin the plugin that is getting the value.
     * @param configName the name of the config file.
     * @param key the key where the value is stored.
     * @return the boolean value.
     */
    public boolean getBooleanFromConfig(Plugin plugin, String configName, String key) {
        return (boolean)getFromConfig(getConfigKey(plugin, configName), key);
    }

    // ----- WRITE TO CONFIG -----

    /**
     * Writes a new string value to the specified config file.
     *
     * @param configKey the key of the config.
     * @param key the key to store the new value at.
     * @param value the value to store.
     * @return the old value.
     */
    public Object writeString(String configKey, String key, String value) {
        // Adds the new value
        FileConfiguration config = fileConfigurationMap.get(configKey);
        Object oldValue = config.get(key);
        config.set(key, value);

        // Tries to save the config file
        try {
            config.save(configFileMap.get(configKey));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return oldValue;
    }

    /**
     * Writes a new string value to the specified config file.
     *
     * @param plugin the plugin that is writing to the config.
     * @param configName the name of the config file.
     * @param key the key to store the new value at.
     * @param value the value to store.
     * @return the old value.
     */
    public Object writeString(Plugin plugin, String configName, String key, String value) {
        // Adds the new value
        FileConfiguration config = fileConfigurationMap.get(getConfigKey(plugin, configName));
        Object oldValue = config.get(key);
        config.set(key, value);

        // Tries to save the config file
        try {
            config.save(configFileMap.get(getConfigKey(plugin, configName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return oldValue;
    }

    /**
     * Writes a new boolean to the specified config file.
     *
     * @param configKey the key of the config.
     * @param key the key to store the new value at.
     * @param value the value to store.
     * @return the old value.
     */
    public Object writeBoolean(String configKey, String key, Boolean value) {
        // Adds the new value
        FileConfiguration config = fileConfigurationMap.get(configKey);
        Object oldValue = config.get(key);
        config.set(key, value);

        // Tries to save the config file
        try {
            config.save(configFileMap.get(configKey));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return oldValue;
    }

    /**
     * Writes a new boolean value to the specified config file.
     *
     * @param plugin the plugin that is writing to the config.
     * @param configName the name of the config file.
     * @param key the key to store the new value at.
     * @param value the value to store.
     * @return the old value.
     */
    public Object writeBoolean(Plugin plugin, String configName, String key, Boolean value) {
        // Adds the new value
        FileConfiguration config = fileConfigurationMap.get(getConfigKey(plugin, configName));
        Object oldValue = config.get(key);
        config.set(key, value);

        // Tries to save the config file
        try {
            config.save(configFileMap.get(getConfigKey(plugin, configName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return oldValue;
    }

    // ----- UPDATE CONFIG -----

    /**
     * Creates a configuration file if one does not exist. If one does exist, updates it with potentially new keys and populates them with default values.
     * This will not override old configuration values.
     *
     * @param plugin the plugin that is updating the config file.
     * @param configName the name of the config file.
     */
    public void updateCustomConfig(Plugin plugin, String configName){
        // Gets / creates the old config file and loads it as a YamlConfiguration
        File oldConfig = FileUtil.getOrCreateFileFromResource(plugin, configName);
        YamlConfiguration oldYamlConfig = new YamlConfiguration();
        try {
            oldYamlConfig.load(oldConfig);
        } catch (IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }

        // Get all keys and values
        List<String> keys = new ArrayList<>(oldYamlConfig.getKeys(true));
        Map<String, Object> values = new HashMap<>(oldYamlConfig.getValues(true));

        // Rename the old config
        oldConfig.renameTo(new File(plugin.getDataFolder(), "old." + configName));

        // Gets / creates the new config file and loads it as a YamlConfiguration
        File newConfig = FileUtil.getOrCreateFileFromResource(plugin, configName);
        YamlConfiguration newYamlConfig = new YamlConfiguration();
        try {
            newYamlConfig.load(newConfig);
        } catch (IOException |InvalidConfigurationException e){
            e.printStackTrace();
        }

        // Copies over all values from the old config file to the new config file // TODO: Test if this works when an old key has been removed (inserting into a key that does not exist)
        for (String key : keys){
            newYamlConfig.set(key, values.get(key));
            try{
                newYamlConfig.save(newConfig);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        // Add the new config files to the hash map
        configFileMap.put(getConfigKey(plugin, configName), newConfig);
        fileConfigurationMap.put(getConfigKey(plugin, configName), newYamlConfig);

        // Deletes the old config
        File configToDelete = FileUtil.getOrCreateFileFromResource(plugin, "old." + configName);
        if (configToDelete.exists()) configToDelete.delete();
    }

    private String getConfigKey(Plugin plugin, String configName) {
        return plugin.getName() + ":" + configName;
    }

    // ----- CREATE CONFIG -----

    private void createConfig() {
        updateCustomConfig(CobaltCore.getInstance(), "cobalt.yml");
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        createConfig();
    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static ConfigManager INSTANCE = null;
    /**
     * Returns the object representing this <code>ConfigManager</code>.
     *
     * @return The object of this class
     */
    public static ConfigManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ConfigManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
