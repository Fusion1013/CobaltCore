package se.fusion1013.plugin.cobaltcore.entity.modules;

import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.state.StateEngine;

import java.util.ArrayList;
import java.util.List;

public class EntityStateModule extends EntityModule implements ITickExecutable {

    // ----- VARIABLES -----

    private final StateEngine<CustomEntity>[] stateEngines;

    // ----- CONSTRUCTOR -----

    /**
     * Creates a new <code>EntityStateModule</code>.
     *
     * @param engines the engines to run.
     */
    @SafeVarargs
    public EntityStateModule(StateEngine<CustomEntity>... engines) {
        this.stateEngines = engines;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity) {
        for (StateEngine<CustomEntity> engine : stateEngines) {
            engine.tick(customEntity);
        }
    }

    // ----- CLONE -----

    public EntityStateModule(EntityStateModule target) {
        StateEngine<CustomEntity>[] newEngines = new StateEngine[target.stateEngines.length];
        System.arraycopy(target.stateEngines, 0, newEngines, 0, target.stateEngines.length);
        this.stateEngines = newEngines;
    }

    @Override
    public EntityStateModule clone() {
        return new EntityStateModule(this);
    }
}
