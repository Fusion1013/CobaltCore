package se.fusion1013.plugin.cobaltcore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

public class PlayerEvents implements Listener {

    // ----- EVENT -----

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        LocaleManager localeManager = LocaleManager.getInstance();
        StringPlaceholders placeholders = StringPlaceholders.builder()
                        .addPlaceholder("version", CobaltCore.getInstance().getDescription().getVersion())
                                .build();
        localeManager.sendMessage(p, "cobalt.player.join", placeholders);
    }

}
