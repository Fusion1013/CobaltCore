package se.fusion1013.plugin.cobaltcore.entity.modules;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

public interface ISpawnExecutable {
    void execute(CustomEntity customEntity);
    ISpawnExecutable clone();
}
