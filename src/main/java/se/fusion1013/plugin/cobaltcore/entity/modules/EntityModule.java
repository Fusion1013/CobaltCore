package se.fusion1013.plugin.cobaltcore.entity.modules;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

import java.util.List;

public abstract class EntityModule implements Cloneable {

    // TODO: Requirements

    /**
     * Executes the module.
     *
     * @param customEntity the <code>CustomEntity</code> that is executing the module.
     */
    public abstract void execute(CustomEntity customEntity);

    @Override
    public abstract EntityModule clone();
}
