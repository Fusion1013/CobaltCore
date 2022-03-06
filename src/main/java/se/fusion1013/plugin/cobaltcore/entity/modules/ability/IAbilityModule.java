package se.fusion1013.plugin.cobaltcore.entity.modules.ability;

import org.bukkit.entity.Entity;

public interface IAbilityModule {

    /**
     * Attempts to activate the ability.
     *
     * @param entity the entity that is executing the ability.
     * @return true if the ability was executed.
     */
    boolean attemptAbility(Entity entity);

}
