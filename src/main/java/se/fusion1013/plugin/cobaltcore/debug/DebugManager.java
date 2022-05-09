package se.fusion1013.plugin.cobaltcore.debug;

import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugManager extends Manager {

    // ----- VARIABLES -----

    private static final Map<String, List<Player>> debugSubscriptions = new HashMap<>();

    // ----- EVENT THROWING -----

    public static void throwDebugEvent(IDebugEvent event) {
        String name = event.getName();
        List<Player> players = debugSubscriptions.get(name);
        for (Player p : players) event.throwEvent(p);
    }

    // ----- SUBSCRIBING / UNSUBSCRIBING -----

    public static void subscribe(Player player, IDebugEvent event) {
        String name = event.getName();
        debugSubscriptions.get(name).add(player);

        // Send message to player if they are online.
        if (player.isOnline()) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("player", player.getName())
                    .addPlaceholder("event", name)
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "debug.event.subscribe", placeholders);
        }
    }

    public static void unsubscribe(Player player, IDebugEvent event) {
        String name = event.getName();
        debugSubscriptions.get(name).remove(player);

        // Send message to player if they are online.
        if (player.isOnline()) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("player", player.getName())
                    .addPlaceholder("event", name)
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "debug.event.unsubscribe", placeholders);
        }
    }

    // ----- CONSTRUCTORS -----

    public DebugManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static DebugManager INSTANCE = null;
    /**
     * Returns the object representing this <code>DebugManager</code>.
     *
     * @return The object of this class
     */
    public static DebugManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DebugManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
