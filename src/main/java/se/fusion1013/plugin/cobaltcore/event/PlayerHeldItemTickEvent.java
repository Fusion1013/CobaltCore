package se.fusion1013.plugin.cobaltcore.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerHeldItemTickEvent extends Event implements Cancellable {

    private final Player player;
    private boolean isCancelled = false;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerHeldItemTickEvent(Player player) {
        this.player = player;
    }

    // ----- GETTERS / SETTERS -----

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    // ----- HANDLERS -----

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

}
