package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.entity.Entity;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

/**
 * Executes a lambda expression on the entity on spawn
 */
public class EntitySpawnMethodModule extends EntityModule implements ISpawnExecutable {

    // ----- CONSTRUCTORS -----

    IEntityModule method;

    public EntitySpawnMethodModule(IEntityModule method) {
        this.method = method;
    }

    // ----- EXECUTE METHOD -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        method.execute(customEntity, spawnParameters);
    }

    // ----- CLONE -----

    public EntitySpawnMethodModule(EntitySpawnMethodModule target) {
        this.method = target.method;
    }

    @Override
    public EntitySpawnMethodModule clone() {
        return new EntitySpawnMethodModule(this);
    }
}
