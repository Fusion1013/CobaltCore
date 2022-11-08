package se.fusion1013.plugin.cobaltcore.entity.modules.ability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

public interface IAbilityModule {

    /**
     * Attempts to activate the ability.
     *
     * @param entity the entity that is executing the ability.
     * @param spawnParameters spawn parameters.
     * @return true if the ability was executed.
     */
    boolean attemptAbility(CustomEntity entity, ISpawnParameters spawnParameters);

    default void onEntityDeath(CustomEntity entity, ISpawnParameters spawnParameters, Location location, Entity dyingEntity) {}

}
