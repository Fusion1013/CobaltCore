package se.fusion1013.plugin.cobaltcore.entity.modules;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

public interface ITickExecutable {
    void execute(CustomEntity customEntity);
    ITickExecutable clone();
}
