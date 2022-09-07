package se.fusion1013.plugin.cobaltcore.bar.actionbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.ActionBarUtil;

import java.util.*;

public class ActionBarManager extends Manager implements Runnable {

    // ----- VARIABLES -----

    // <PlayerUUID, <SubscriberName, ActionBarComponent>>
    private static Map<UUID, Map<String, List<ActionBarUtil.ActionBarComponent>>> ACTION_BAR_QUEUE = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public ActionBarManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- MAIN RUN LOOP -----
    // Executes once every second
    // Merges all objects in the ACTION_BAR_QUEUE
    // Displays the merged object to the player
    // Clears the map to allow for new objects to come in

    @Override
    public void run() {
        for (UUID uuidKey : ACTION_BAR_QUEUE.keySet()) {
            Player player = Bukkit.getPlayer(uuidKey);
            if (player == null) continue;

            Set<String> subscriberComponentSet = ACTION_BAR_QUEUE.get(uuidKey).keySet();

            ActionBarUtil.ActionBarBuilder builder = new ActionBarUtil.ActionBarBuilder();

            // Merge all components into builder
            for (String subscriberName : subscriberComponentSet) {
                List<ActionBarUtil.ActionBarComponent> componentList = ACTION_BAR_QUEUE.get(uuidKey).get(subscriberName);
                for (ActionBarUtil.ActionBarComponent component : componentList) builder.addComponent(component);
                //subscriberComponentSet.remove(subscriberName);
            }

            ACTION_BAR_QUEUE.get(uuidKey).clear();

            // Display components to player
            if (!builder.getComponents().isEmpty()) player.sendActionBar(builder.getComponent());
        }
    }

    // ----- SUBSCRIBING -----

    /**
     * Adds a new <code>ActionBarComponent</code> to the subscription map.
     *
     * @param player the <code>Player</code> that should receive the <code>ActionBarComponent</code>.
     * @param subscriber name of the subscriber. This should be unique for different <code>ActionBarComponent</code>'s.
     * @param components the <code>ActionBarComponent</code> to add.
     */
    public static void subscribe(Player player, String subscriber, List<ActionBarUtil.ActionBarComponent> components) {
        ACTION_BAR_QUEUE.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(subscriber, components);
    }

    // TODO: Method for adding multiple components at once

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(CobaltCore.getInstance(), this, 0, 2);
    }

    @Override
    public void disable() {

    }
}
