package se.fusion1013.plugin.cobaltcore.manager;

import se.fusion1013.plugin.cobaltcore.CobaltCore;

public abstract class Manager {

    // ----- CORE PLUGIN ----- // TODO: Replace with owner plugin?

    protected CobaltCore core;

    // ----- CONSTRUCTOR -----

    public Manager(CobaltCore cobaltCore) {
        this.core = cobaltCore;
    }

    // ----- RELOADING / DISABLING -----

    public abstract void reload();

    public abstract void disable();
}
