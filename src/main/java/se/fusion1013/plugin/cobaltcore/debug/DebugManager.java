package se.fusion1013.plugin.cobaltcore.debug;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.StringPlaceholders;

import java.util.ArrayList;
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
        if (players == null) return;
        for (Player p : players) event.throwEvent(p);
    }

    // ----- SUBSCRIBING / UNSUBSCRIBING -----

    public CommandResult subscribe(Player player, String event) {
        debugSubscriptions.computeIfAbsent(event, k -> new ArrayList<>()).add(player);

        // Send message to player if they are online.
        if (player.isOnline()) {
            StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("player", player.getName())
                    .addPlaceholder("event", event)
                    .build();
            LocaleManager.getInstance().sendMessage(CobaltCore.getInstance(), player, "debug.event.subscribe", placeholders);
        }

        return CommandResult.SUCCESS;
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
