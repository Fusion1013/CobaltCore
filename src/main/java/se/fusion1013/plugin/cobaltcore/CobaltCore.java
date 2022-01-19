package se.fusion1013.plugin.cobaltcore;

import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobaltcore.database.Database;
import se.fusion1013.plugin.cobaltcore.database.SQLite;
import se.fusion1013.plugin.cobaltcore.manager.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class CobaltCore extends JavaPlugin implements CobaltPlugin {

    private static CobaltCore INSTANCE;
    private static Database db;

    /**
     * Gets the instance of CobaltCore
     *
     * @return instance of CobaltCore
     */
    public static CobaltCore getInstance() { return INSTANCE; }

    /**
     * Gets the database
     *
     * @return the database
     */
    public Database getRDatabase() { return db; }

    public CobaltCore() {
        INSTANCE = this;
        this.managers = new LinkedHashMap<>();
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting up CobaltCore...");
        registerCobaltPlugin(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // ----- MANAGERS -----

    private final Map<Class<?>, Manager> managers;

    /**
     * Gets a manager instance
     *
     * @param managerClass The class of the manager instance to get
     * @param <T> The manager type
     * @return The manager instance or null if one does not exist
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> managerClass) {
        if (this.managers.containsKey(managerClass))
            return (T) this.managers.get(managerClass);

        try {
            T manager = managerClass.getConstructor(this.getClass()).newInstance(this);
            this.managers.put(managerClass, manager);
            manager.reload();
            return manager;
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void reloadManagers() {
        this.managers.values().forEach(Manager::disable);

        this.managers.values().forEach(Manager::reload);

        this.getManager(LocaleManager.class);
    }

    // ----- PLUGIN REGISTRATION -----

    private final static Set<CobaltPlugin> cobaltPlugins = new HashSet<>();

    public boolean registerCobaltPlugin(CobaltPlugin plugin) {

        getLogger().info("Registering Plugin " + plugin.getName() + ".");

        if (cobaltPlugins.add(plugin)) {

            // Connect to database if the plugin is CobaltCore
            if (plugin instanceof CobaltCore) {
                getLogger().info("Instantiating Database...");
                db = new SQLite(this);
                db.load();
            }

            // Init Database Tables
            plugin.initDatabaseTables();

            // Reloads all Managers
            getLogger().info("Reloading Managers for " + plugin.getName() + "...");
            plugin.reloadManagers();

            // Registers all Commands
            getLogger().info("Registering Commands for " + plugin.getName() + "...");
            plugin.registerCommands();

            // Registers all Listeners
            getLogger().info("Registering Listeners for " + plugin.getName() + "...");
            plugin.registerListeners();
        }

        getLogger().info("Successfully registered " + plugin.getName() + ".");
        return true;
    }
}
