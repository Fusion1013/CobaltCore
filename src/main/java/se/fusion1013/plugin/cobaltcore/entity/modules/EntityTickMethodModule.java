package se.fusion1013.plugin.cobaltcore.entity.modules;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

public class EntityTickMethodModule extends EntityModule implements ITickExecutable {

    // ----- CONSTRUCTORS -----

    IEntityModule method;

    public EntityTickMethodModule(IEntityModule method) {
        this.method = method;
    }

    // ----- EXECUTE METHOD -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        method.execute(customEntity, spawnParameters);
    }

    // ----- CLONE -----

    public EntityTickMethodModule(EntityTickMethodModule target) {
        this.method = target.method;
    }

    @Override
    public EntityTickMethodModule clone() {
        return new EntityTickMethodModule(this);
    }
}
