package se.fusion1013.plugin.cobaltcore.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public interface ICustomEntity {

    /**
     * Forces the entity to spawn at the location.
     *
     * @param spawnParameters parameters for the spawn.
     * @param location the location to spawn the entity.
     */
    CustomEntity forceSpawn(Location location, ISpawnParameters spawnParameters);

    /**
     * Attempts to spawn the entity at the location, taking its natural spawn requirements into account.
     *
     * @param location the location to spawn the entity.
     * @param spawnParameters parameters for the spawn.
     * @return null if the entity was not able to spawn.
     */
    CustomEntity attemptNaturalSpawn(Location location, ISpawnParameters spawnParameters);

    /**
     * Checks if the <code>CustomEntity</code> is alive.
     *
     * @return true if the <code>CustomEntity</code> is alive.
     */
    boolean isAlive();

    Location getLocation();

    void onDeath(Location location, Entity dyingEntity);

    void despawn();

    // ----- GETTERS / SETTERS -----

    String getInternalName();

    EntityType getBaseEntityType();

    UUID getEntityUuid();

}
