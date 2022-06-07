package se.fusion1013.plugin.cobaltcore.entity.modules;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;

public class EntityParticleModule extends EntityModule implements ITickExecutable, ISpawnExecutable, IDeathExecutable {

    // ----- VARIABLES -----

    ParticleGroup group;

    // ----- CONSTRUCTORS -----

    public EntityParticleModule(ParticleGroup group) {
        this.group = group;
    }

    public EntityParticleModule(EntityParticleModule target) {
        this.group = target.group;
    }

    // ----- EXECUTING -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        group.display(customEntity.getSummonedEntity().getLocation());
    }

    // ----- CLONING -----

    @Override
    public EntityParticleModule clone() {
        return new EntityParticleModule(this);
    }
}
