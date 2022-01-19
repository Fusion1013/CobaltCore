package se.fusion1013.plugin.cobaltcore.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;

public abstract class Manager {

    protected CobaltCore cobaltCore;

    public Manager(CobaltCore cobaltCore){
        this.cobaltCore = cobaltCore;
    }

    public abstract void reload();

    public abstract void disable();
}
