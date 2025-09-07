package se.fusion1013.cobaltCore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.util.StringPlaceholders;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        LocaleManager localeManager = LocaleManager.getInstance();

        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("plugin", plugin.getName())
                    .addPlaceholder("version", plugin.getDescription().getVersion())
                    .build();
            localeManager.sendMessage(plugin, player, "cobalt.player.join", placeholders);
        }
    }

}
