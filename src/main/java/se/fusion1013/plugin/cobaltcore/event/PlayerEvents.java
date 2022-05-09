package se.fusion1013.plugin.cobaltcore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.CobaltPlugin;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class PlayerEvents implements Listener {

    // ----- EVENT -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        LocaleManager localeManager = LocaleManager.getInstance();

        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("plugin", plugin.getName())
                    .addPlaceholder("version", plugin.getDescription().getVersion())
                    .build();
            localeManager.sendMessage(plugin, p, "cobalt.player.join", placeholders);
        }
    }

}
