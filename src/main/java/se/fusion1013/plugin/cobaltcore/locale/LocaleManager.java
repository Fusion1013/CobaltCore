package se.fusion1013.plugin.cobaltcore.locale;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LocaleManager extends Manager {

    // ----- VARIABLES -----

    private static final String[] localeStrings = new String[]{ "en_us", "sv_se" };

    private static final Map<String, Map<String, String>> localeMessages = new HashMap<>(); // <locale, <code, message>>

    // ----- CONSTRUCTORS -----

    public LocaleManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- LOGIC -----

    /**
     * Gets the total number of locale files.
     *
     * @return the number of locale files.
     */
    public static int getLocaleFileCount() {
        Set<CobaltPlugin> cobaltPlugins = CobaltCore.getRegisteredCobaltPlugins();

        int localeCount = 0;

        for (CobaltPlugin plugin : cobaltPlugins) {
            for (String locale : localeStrings) {
                File file = new File(plugin.getDataFolder(), "lang/" + locale + ".json");
                if (file.exists()) localeCount++;
            }
        }

        return localeCount;
    }

    /**
     * Resets all locale messages and reload them from the plugin data folders.
     */
    public static void resetLocale() {
        Set<CobaltPlugin> cobaltPlugins = CobaltCore.getRegisteredCobaltPlugins();

        localeMessages.clear();

        for (CobaltPlugin plugin : cobaltPlugins) {
            resetLocale(plugin);
        }
    }

    private static void resetLocale(Plugin plugin) {
        for (String locale : localeStrings) {
            File file = new File(plugin.getDataFolder(), "lang/" + locale + ".json");
            if (file.exists()) file.delete();
            FileUtil.getOrCreateFileFromResource(plugin, "lang/" + locale + ".json");
        }

        loadLocale(plugin);
    }

    /**
     * Loads all locale files from the given plugin.
     *
     * @param plugin the plugin to get the locale files from.
     */
    public static void loadLocale(Plugin plugin) {
        for (String s : localeStrings) {
            String filePath = "lang/" + s + ".json";

            // Gets / creates the old locale
            File oldLocale = FileUtil.getOrCreateFileFromResource(plugin, filePath);

            if (oldLocale.exists()) {

                // Store the old values in a map
                Map<String, String> oldValues = getJsonValues(oldLocale);

                // Rename the old locale file
                oldLocale.renameTo(new File(plugin.getDataFolder(), "lang/" + s + "old.json"));

                // Continue if the map is not null
                if (oldValues != null) {
                    createNewLocale(plugin, s, oldValues);
                }

                // Deletes the old locale
                File oldLocaleToDelete = FileUtil.getOrCreateFileFromResource(plugin, "lang/" + s + "old.json");
                if (oldLocaleToDelete.exists()) oldLocaleToDelete.delete();
            }
        }
    }

    private static void storeJsonValues(File file, Map<String, String> values) {
        JSONObject obj = new JSONObject();
        for (String key : values.keySet()) {
            obj.put(key, values.get(key));

            try {
                FileWriter writer = new FileWriter(file);
                writer.write(obj.toJSONString());
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static Map<String, String> getJsonValues(File file) {
        Map<String, String> values = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            values = new Gson().fromJson(reader, mapType);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return values;
    }

    private static void createNewLocale(Plugin plugin, String locale, Map<String, String> oldValues) {

        // Gets / Creates the new locale file
        File newLocale = FileUtil.getOrCreateFileFromResource(plugin, "lang/" + locale + ".json");

        Map<String, String> newValues = getJsonValues(newLocale);

        // Continue if the map is not null
        if (newValues != null) {
            Map<String, String> mergedValues = mergeLocales(locale, oldValues, newValues);

            storeJsonValues(newLocale, mergedValues);
        }
    }

    private static Map<String, String> mergeLocales(String locale, Map<String, String> oldValues, Map<String, String> newValues) {
        // Copies over all values from the old config file to the new config file
        for (String key : oldValues.keySet()) {
            newValues.put(key, oldValues.get(key));
        }

        // TODO: Store new values in file

        // Merge new locale with already stored locale
        Map<String, String> mergeWith = localeMessages.get(locale);

        if (mergeWith == null) localeMessages.put(locale, newValues);
        else mergeWith.putAll(newValues);

        return newValues;
    }

    /**
     * Gets a localized message from the given key.
     *
     * @param messageKey the key of the message.
     * @return the localized message.
     */
    public String getLocaleMessage(String messageKey) {
        return this.getLocaleMessage(messageKey, StringPlaceholders.empty());
    }

    /**
     * Gets a localized message from the given key and applies the placeholder values to it.
     *
     * @param messageKey the key of the message.
     * @param stringPlaceholders the placeholders to apply to the message.
     * @return the localized message.
     */
    public String getLocaleMessage(String messageKey, StringPlaceholders stringPlaceholders) {
        Map<String, String> locale = localeMessages.get("en_us");
        if (locale == null) return ChatColor.RED + "Missing locale file: en_us";
        String message = locale.get(messageKey);
        if (messageKey.equalsIgnoreCase("")) message = "";
        if (message == null) return ChatColor.RED + "Missing message in locale file: " + messageKey;
        return HexUtils.colorify(stringPlaceholders.apply(message));
    }

    /**
     * Gets a localized message from the given key and applies the placeholder value to it.
     * @param p the player to get the locale from.
     * @param messageKey the key of the message.
     * @return the localized message.
     */
    public String getLocaleMessage(Player p, String messageKey) {
        return getLocaleMessage(p, messageKey, StringPlaceholders.empty());
    }

    /**
     * Gets a localized message from the given key and applies the placeholder value to it.
     * @param p the player to get the locale from.
     * @param messageKey the key of the message.
     * @param stringPlaceholders the placeholders to apply to the message.
     * @return the localized message.
     */
    public String getLocaleMessage(Player p, String messageKey, StringPlaceholders stringPlaceholders) {
        String playerLanguage = p.getLocale();
        Map<String, String> locale = localeMessages.get(playerLanguage);
        if (locale == null) return getLocaleMessage(messageKey, stringPlaceholders);
        String message = locale.get(messageKey);
        if (messageKey.equalsIgnoreCase("")) message = "";
        if (message == null) return ChatColor.RED + "Missing message in locale file: " + messageKey;
        return HexUtils.colorify(stringPlaceholders.apply(message));
    }

    // ----- MESSAGE BROADCASTING -----

    // TODO: Replace message with getting prefix directly from plugin.

    /**
     * Broadcasts a message to the server.
     *
     * @param messageKey the key to the message to broadcast.
     */
    public void broadcastMessage(String messageKey) {
        broadcastMessage(CobaltCore.getInstance(), messageKey);
    }

    /**
     * Broadcasts a message to the server.
     *
     * @param plugin the plugin that is broadcasting the message.
     * @param messageKey the key to the message to broadcast.
     */
    public void broadcastMessage(CobaltPlugin plugin, String messageKey) {
        broadcastMessage(plugin, messageKey, StringPlaceholders.empty());
    }

    /**
     * Broadcasts a message to the server.
     *
     * @param messageKey the key to the message to broadcast.
     * @param placeholders the placeholders to apply to the message.
     */
    public void broadcastMessage(String messageKey, StringPlaceholders placeholders) {
        broadcastMessage(CobaltCore.getInstance(), messageKey, placeholders);
    }

    /**
     * Broadcasts a message to the server.
     *
     * @param plugin the plugin that is broadcasting the message.
     * @param messageKey the key to the message to broadcast.
     * @param placeholders the placeholders to apply to the message.
     */
    public void broadcastMessage(CobaltPlugin plugin, String messageKey, StringPlaceholders placeholders) {
        HexUtils.broadcastMessage(this.getLocaleMessage(plugin.getPrefix()) + this.getLocaleMessage(messageKey, placeholders));
    }

    /**
     * Broadcasts a message to the server.
     *
     * @param prefix the message prefix.
     * @param messageKey the key to the message to broadcast.
     */
    public void broadcastMessage(String prefix, String messageKey) {
        broadcastMessage(prefix, messageKey, StringPlaceholders.empty());
    }

    /**
     * Broadcasts a message to the server.
     *
     * @param prefix the message prefix.
     * @param messageKey the key to the message to broadcast.
     * @param placeholders the placeholders to apply to the message.
     */
    public void broadcastMessage(String prefix, String messageKey, StringPlaceholders placeholders) {
        HexUtils.broadcastMessage(this.getLocaleMessage(prefix) + this.getLocaleMessage(messageKey, placeholders));
    }

    // ----- MESSAGE SENDING -----

    /**
     * Sends a localized message to the player.
     *
     * @param player the player to send the message to.
     * @param messageKey the key to the message to send.
     */
    public void sendMessage(Player player, String messageKey) {
        sendMessage(player, messageKey, StringPlaceholders.empty());
    }

    /**
     * Sends a localized message to the player.
     *
     * @param player the player to send the message to.
     * @param messageKey the key of the message to send.
     * @param placeholders the placeholders to apply to the message.
     */
    public void sendMessage(Player player, String messageKey, StringPlaceholders placeholders) {
        sendMessage(CobaltCore.getInstance(), player, messageKey, placeholders);
    }

    /**
     * Sends a localized message to the player.
     *
     * @param plugin the plugin that is sending the message.
     * @param player the player to send the message to.
     * @param messageKey the key of the message to send.
     */
    public void sendMessage(CobaltPlugin plugin, Player player, String messageKey) {
        sendMessage(plugin, player, messageKey, StringPlaceholders.empty());
    }

    /**
     * Sends a localized message to the player.
     *
     * @param plugin the plugin that is sending the message.
     * @param player the player to send the message to.
     * @param messageKey the key of the message to send.
     * @param placeholders the placeholders to apply to the message.
     */
    public void sendMessage(CobaltPlugin plugin, Player player, String messageKey, StringPlaceholders placeholders) {
        sendParsedMessage(player, this.getLocaleMessage(plugin.getPrefix()) + this.getLocaleMessage(player, messageKey, placeholders));
    }

    /**
     * Sends a localized message to the player.
     *
     * @param prefix the prefix of the message.
     * @param player the player to send the message to.
     * @param messageKey the key of the message to send.
     */
    public void sendMessage(String prefix, Player player, String messageKey) {
        sendMessage(prefix, player, messageKey, StringPlaceholders.empty());
    }

    /**
     * Sends a localized message to the player.
     *
     * @param prefix the prefix of the message.
     * @param player the player to send the message to.
     * @param messageKey the key of the message to send.
     * @param placeholders the placeholders to apply to the message.
     */
    public void sendMessage(String prefix, Player player, String messageKey, StringPlaceholders placeholders) {
        sendParsedMessage(player, this.getLocaleMessage(prefix) + this.getLocaleMessage(player, messageKey, placeholders));
    }

    /**
     * Sends a message to the specified player.
     *
     * @param player the player to send the message to.
     * @param message the message to send.
     */
    private void sendParsedMessage(Player player, String message) {
        HexUtils.sendMessage(player, message);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
    }

    @Override
    public void disable() {
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static LocaleManager INSTANCE = null;
    /**
     * Returns the object representing this <code>LocaleManager</code>.
     *
     * @return The object of this class
     */
    public static LocaleManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LocaleManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
