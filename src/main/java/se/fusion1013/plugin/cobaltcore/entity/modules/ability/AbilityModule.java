package se.fusion1013.plugin.cobaltcore.entity.modules.ability;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.modules.EntityModule;

/**
 * Extend this class to create a custom ability for an entity.
 */
public abstract class AbilityModule extends EntityModule implements IAbilityModule {

    // ----- VARIABLES -----

    double abilityCooldown = 0;
    double currentAbilityCooldown = 0;

    // ----- CONSTRUCTORS -----

    public AbilityModule(double cooldown) {
        this.abilityCooldown = cooldown;
        this.currentAbilityCooldown = cooldown;
    }

    // ----- COOLDOWN -----

    @Override
    public boolean attemptAbility(CustomEntity entity) {
        if (currentAbilityCooldown > 0) {
            currentAbilityCooldown-=0.05;
            return false;
        } else {
            currentAbilityCooldown = abilityCooldown;
            execute(entity);
            return true;
        }
    }

    // ----- GETTERS / SETTERS -----

    public abstract String getAbilityName();
    public abstract String getAbilityDescription();

    public void resetCooldown() {
        this.currentAbilityCooldown = 0;
    }

    public void setCurrentAbilityCooldown(double currentAbilityCooldown) {
        this.currentAbilityCooldown = currentAbilityCooldown;
    }

    // ----- CLONE -----

    public AbilityModule(AbilityModule target) {
        this.abilityCooldown = target.abilityCooldown;
        this.currentAbilityCooldown = target.abilityCooldown;
    }

    @Override
    public AbilityModule clone() {
        return null;
    }

}
