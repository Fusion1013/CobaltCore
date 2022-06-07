package se.fusion1013.plugin.cobaltcore.entity.modules.ability;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

public class MultiAbility extends AbilityModule implements IAbilityModule {

    // ----- VARIABLES -----

    AbilityModule[] modules;

    // ----- CONSTRUCTORS -----

    /**
     * Creates a new <code>MultiAbility</code>. Executes all abilities stored inside at once. Cooldowns for the abilities will be ignored.
     *
     * @param cooldown the cooldown for the abilities.
     * @param modules the modules to execute.
     */
    public MultiAbility(int cooldown, AbilityModule... modules) {
        super(cooldown);

        this.modules = modules;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        for (AbilityModule module : modules) module.execute(customEntity, spawnParameters);
    }

    @Override
    public String getAbilityName() {
        return "Multi";
    }

    @Override
    public String getAbilityDescription() {
        return "Executes multiple abilities at once";
    }

    // ----- CLONE -----

    public MultiAbility(MultiAbility target) {
        super(target);

        this.modules = target.modules;
    }

    @Override
    public MultiAbility clone() {
        return new MultiAbility(this);
    }
}
