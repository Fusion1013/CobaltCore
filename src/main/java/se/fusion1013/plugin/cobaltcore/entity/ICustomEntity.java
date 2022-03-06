package se.fusion1013.plugin.cobaltcore.entity;

import org.bukkit.Location;

public interface ICustomEntity {

    /**
     * Forces the entity to spawn at the location.
     *
     * @param location the location to spawn the entity.
     */
    CustomEntity forceSpawn(Location location);

    /**
     * Attempts to spawn the entity at the location, taking its natural spawn requirements into account.
     *
     * @param location the location to spawn the entity.
     * @return null if the entity was not able to spawn.
     */
    CustomEntity attemptNaturalSpawn(Location location);

    /**
     * Checks if the <code>CustomEntity</code> is alive.
     *
     * @return true if the <code>CustomEntity</code> is alive.
     */
    boolean isAlive();

    // TODO: Kill method

    // ----- GETTERS / SETTERS -----

    String getInternalName();

}
