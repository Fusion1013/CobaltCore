package se.fusion1013.plugin.cobaltcore.entity;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EntityDeathEvent extends Event implements Cancellable {

    private final ICustomEntity entity;
    private final Location location;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled = false;

    public EntityDeathEvent(ICustomEntity entity, Location location) {
        this.entity = entity;
        this.location = location;
    }

    public ICustomEntity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }
}
