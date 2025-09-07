package se.fusion1013.cobaltCore.manager;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class Manager<T extends JavaPlugin> {
    // ----- CORE PLUGIN ----- // TODO: Replace with owner plugin?

    protected T plugin;

    // ----- CONSTRUCTOR -----

    public Manager(T plugin) {
        this.plugin = plugin;
    }

    // ----- RELOADING / DISABLING -----

    public abstract void reload();

    public abstract void disable();
}
