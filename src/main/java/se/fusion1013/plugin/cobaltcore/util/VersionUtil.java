package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;

public class VersionUtil {

    /**
     * Prints the plugin version to the player.
     *
     * @param plugin the plugin to get the version from.
     * @param player the player to send the version to.
     */
    public static void printVersion(CobaltPlugin plugin, Player player) {
        PluginDescriptionFile desc = plugin.getDescription();
        LocaleManager localeManager = LocaleManager.getInstance();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("plugin_name", desc.getName())
                .addPlaceholder("version", desc.getVersion())
                .addPlaceholder("website", desc.getWebsite())
                .build();

        localeManager.sendMessage(plugin, player, "version.version", placeholders);
        localeManager.sendMessage(plugin, player, "version.author", placeholders);
        localeManager.sendMessage(player, "version.website", placeholders);
    }
}
