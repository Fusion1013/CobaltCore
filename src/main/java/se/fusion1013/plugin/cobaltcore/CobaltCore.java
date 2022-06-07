package se.fusion1013.plugin.cobaltcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.plugin.cobaltcore.bossbar.BossBarManager;
import se.fusion1013.plugin.cobaltcore.commands.CobaltCommand;
import se.fusion1013.plugin.cobaltcore.commands.CobaltSummonCommand;
import se.fusion1013.plugin.cobaltcore.commands.CommandGenerator;
import se.fusion1013.plugin.cobaltcore.commands.particle.MainParticleCommand;
import se.fusion1013.plugin.cobaltcore.commands.settings.SettingCommand;
import se.fusion1013.plugin.cobaltcore.commands.spawner.SpawnerCommand;
import se.fusion1013.plugin.cobaltcore.commands.structure.StructureCommand;
import se.fusion1013.plugin.cobaltcore.config.ConfigManager;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.Database;
import se.fusion1013.plugin.cobaltcore.database.system.SQLite;
import se.fusion1013.plugin.cobaltcore.debug.DebugManager;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntityManager;
import se.fusion1013.plugin.cobaltcore.event.EntitySpawnEvents;
import se.fusion1013.plugin.cobaltcore.event.PlayerEvents;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.locale.LocaleManager;
import se.fusion1013.plugin.cobaltcore.manager.*;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.settings.SettingsManager;
import se.fusion1013.plugin.cobaltcore.trades.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.world.block.BlockManager;
import se.fusion1013.plugin.cobaltcore.world.sound.SoundAreaManager;
import se.fusion1013.plugin.cobaltcore.world.sound.SoundManager;
import se.fusion1013.plugin.cobaltcore.world.spawner.SpawnerManager;
import se.fusion1013.plugin.cobaltcore.world.structure.StructureManager;

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
        this.managers.get(this).values().forEach(Manager::disable);
    }

    // ----- MANAGERS -----

    private final Map<CobaltPlugin, Map<Class<?>, Manager>> managers;

    /**
     * Gets a manager instance
     *
     * @param plugin the plugin that owns the manager.
     * @param managerClass The class of the manager instance to get
     * @param <T> The manager type
     * @return The manager instance or null if one does not exist
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(CobaltPlugin plugin, Class<T> managerClass) {
        this.managers.computeIfAbsent(plugin, k -> new LinkedHashMap<>());

        if (this.managers.get(plugin).containsKey(managerClass))
            return (T) this.managers.get(plugin).get(managerClass);

        try {
            T manager = managerClass.getConstructor(this.getClass()).newInstance(this);
            this.managers.get(plugin).put(managerClass, manager);
            manager.reload();
            return manager;
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void reloadManagers() {
        if (this.managers.get(this) != null) this.managers.get(this).values().forEach(Manager::disable);

        this.getManager(this, ConfigManager.class); // DataManager uses ConfigManager, so it has to be instantiated before

        this.getManager(this, DataManager.class); // This manager must be instantiated before any manager that uses data storage

        this.getManager(this, CommandManager.class); // This manager must be instantiated before any manager that registers a command

        this.getManager(this, LocaleManager.class);
        this.getManager(this, CustomItemManager.class);
        this.getManager(this, SettingsManager.class);
        this.getManager(this, StructureManager.class);
        this.getManager(this, CustomTradesManager.class);
        this.getManager(this, CustomEntityManager.class);
        this.getManager(this, ParticleStyleManager.class);
        this.getManager(this, ParticleGroupManager.class);
        this.getManager(this, BossBarManager.class);
        this.getManager(this, BlockManager.class);

        this.getManager(this, SoundAreaManager.class);
        this.getManager(this, SoundManager.class);

        this.getManager(this, SpawnerManager.class);

        this.getManager(this, DebugManager.class);
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
        MainParticleCommand.register();
        CobaltSummonCommand.register();

        StructureCommand.register();
        SpawnerCommand.register();
    } // Do not register cobalt and setting commands here, that is done elsewhere since they must be reloaded every time a new plugin is loaded.

    // ----- PLUGIN REGISTRATION -----

    private final static Set<CobaltPlugin> cobaltPlugins = new HashSet<>();

    public void disableCobaltPlugin(CobaltPlugin plugin) {
        if (this.managers.get(plugin) != null) this.managers.get(plugin).values().forEach(Manager::disable);
    }

    public boolean registerCobaltPlugin(CobaltPlugin plugin) {

        getLogger().info("Registering Plugin " + plugin.getName() + ".");

        if (cobaltPlugins.add(plugin)) {

            // Pre Init
            plugin.preInit();

            // Connect to database if the plugin is CobaltCore
            if (plugin instanceof CobaltCore) {
                // getLogger().info("Instantiating Database...");
                db = new SQLite(this); // TODO: Move to DataManager
                db.load();
            }

            // Register Locale for Plugin
            // getLogger().info("Loading locale for " + plugin.getName() + "...");
            LocaleManager.loadLocale(plugin);

            // Init Database Tables
            plugin.initDatabaseTables(); // TODO: Remove this method (Needs to be removed from other plugins too)

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
        CommandGenerator.register();

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
    public Database getSQLDatabase() { return db; }

    @Deprecated
    public Database getRDatabase() { return getSQLDatabase(); }

    /**
     * Gets this plugin's ClassLoader. Used to pass the right reference to Morphia for populating objects.
     *
     * @return The ClassLoader of this plugin.
     */
    public ClassLoader getMongoHack() {
        return getClassLoader();
    }

}
