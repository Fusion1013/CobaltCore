package se.fusion1013.plugin.cobaltcore.entity.modules;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;

public interface IEntityModule {

    void execute(CustomEntity customEntity, ISpawnParameters spawnParameters);

}
