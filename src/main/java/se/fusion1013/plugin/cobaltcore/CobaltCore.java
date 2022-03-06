package se.fusion1013.plugin.cobaltcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobaltcore.commands.CobaltCommand;
import se.fusion1013.plugin.cobaltcore.commands.CobaltSummonCommand;
import se.fusion1013.plugin.cobaltcore.commands.TradeCommand;
import se.fusion1013.plugin.cobaltcore.commands.particle.MainParticleCommand;
import se.fusion1013.plugin.cobaltcore.commands.settings.SettingCommand;
import se.fusion1013.plugin.cobaltcore.database.Database;
import se.fusion1013.plugin.cobaltcore.database.SQLite;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.event.EntitySpawnEvents;
import se.fusion1013.plugin.cobaltcore.event.PlayerEvents;
import se.fusion1013.plugin.cobaltcore.manager.*;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;

import java.util.*;

public final class CobaltCore extends JavaPlugin implements CobaltPlugin {

    // ----- VARIABLES -----

    private static CobaltCore INSTANCE;
    private static Database db;

    // ----- CONSTRUCTORS -----

    public CobaltCore() {
        this.managers = new LinkedHashMap<>();
    }

    // ----- ENABLING / DISABLING -----

    @Override
    public void onEnable() {
        getLogger().info("Starting up CobaltCore...");
        INSTANCE = this;
        registerCobaltPlugin(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.managers.values().forEach(Manager::disable);
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

        this.getManager(ConfigManager.class);
        this.getManager(LocaleManager.class);
        this.getManager(CustomItemManager.class);
        this.getManager(SettingsManager.class);
        this.getManager(StructureManager.class);
        this.getManager(CustomTradesManager.class);
        this.getManager(CustomEntityManager.class);
    }

    // ----- LISTENERS -----

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new EntitySpawnEvents(), this);
    }


    // ----- COMMAND REGISTRATION -----

    @Override
    public void registerCommands() {
        TradeCommand.register();
        MainParticleCommand.register();
        CobaltSummonCommand.register();
    } // Do not register cobalt and setting commands here, that is done elsewhere since they must be reloaded every time a new plugin is loaded.

    // ----- PLUGIN REGISTRATION -----

    private final static Set<CobaltPlugin> cobaltPlugins = new HashSet<>();

    public boolean registerCobaltPlugin(CobaltPlugin plugin) {

        getLogger().info("Registering Plugin " + plugin.getName() + ".");

        if (cobaltPlugins.add(plugin)) {

            // Pre Init
            plugin.preInit();

            // Connect to database if the plugin is CobaltCore
            if (plugin instanceof CobaltCore) {
                // getLogger().info("Instantiating Database...");
                db = new SQLite(this);
                db.load();
            }

            // Register Locale for Plugin
            // getLogger().info("Loading locale for " + plugin.getName() + "...");
            LocaleManager.loadLocale(plugin);

            // Init Database Tables
            plugin.initDatabaseTables();

            // Reloads all Managers
            // getLogger().info("Reloading Managers for " + plugin.getName() + "...");
            plugin.reloadManagers();

            // Registers all Commands
            // getLogger().info("Registering Commands for " + plugin.getName() + "...");
            plugin.registerCommands();

            // Registers all Listeners
            // getLogger().info("Registering Listeners for " + plugin.getName() + "...");
            plugin.registerListeners();

            // Post Init
            plugin.postInit();
        }

        // getLogger().info("Reloading cobalt command...");
        CobaltCommand.register();
        SettingCommand.register();

        getLogger().info("Successfully registered " + plugin.getName() + ".");
        return true;
    }

    // ----- GETTERS / SETTERS -----

    /**
     * Gets all registered <code>CobaltPlugin</code>'s.
     *
     * @return all registered <code>CobaltPlugin</code>'s.
     */
    public static Set<CobaltPlugin> getRegisteredCobaltPlugins() {
        return cobaltPlugins;
    }

    /**
     * Gets the instance of CobaltCore
     *
     * @return instance of CobaltCore
     */
    public static CobaltCore getInstance() {
        return INSTANCE;
    }

    public static CobaltCore getPlugin() {
        return (CobaltCore) getProvidingPlugin(CobaltCore.class);
    }

    /**
     * Gets the database
     *
     * @return the database
     */
    public Database getRDatabase() { return db; }
}
