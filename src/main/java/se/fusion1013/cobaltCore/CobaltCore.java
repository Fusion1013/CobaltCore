package se.fusion1013.cobaltCore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import se.fusion1013.cobaltCore.api.ApiServer;
import se.fusion1013.cobaltCore.commands.*;
import se.fusion1013.cobaltCore.commands.edit.EditCommand;
import se.fusion1013.cobaltCore.commands.entity.CSummonCommand;
import se.fusion1013.cobaltCore.commands.entity.CustomEntityCommand;
import se.fusion1013.cobaltCore.commands.item.CGiveCommand;
import se.fusion1013.cobaltCore.commands.particle.ParticleCommand;
import se.fusion1013.cobaltCore.database.system.DataManager;
import se.fusion1013.cobaltCore.database.system.Database;
import se.fusion1013.cobaltCore.database.system.SQLite;
import se.fusion1013.cobaltCore.entity.CustomEntityManager;
import se.fusion1013.cobaltCore.events.PlayerEvents;
import se.fusion1013.cobaltCore.item.CustomItemManager;
import se.fusion1013.cobaltCore.item.components.ComponentManager;
import se.fusion1013.cobaltCore.item.crafting.RecipeManager;
import se.fusion1013.cobaltCore.item.enchantment.EnchantmentManager;
import se.fusion1013.cobaltCore.item.section.ItemSectionManager;
import se.fusion1013.cobaltCore.locale.LocaleManager;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.particle.effects.ParticleEffectManager;
import se.fusion1013.cobaltCore.particle.effects.glyph.GlyphManager;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CobaltCore extends JavaPlugin implements CobaltPlugin {

    private static CobaltCore INSTANCE;
    private static Database db;
    private static ApiServer apiServer;

    public CobaltCore() {
        INSTANCE = this;
        this.managers = new LinkedHashMap<>();
    }

    // ##### ENABLING / DISABLING #####

    @Override
    public void onEnable() {
        registerCobaltPlugin(this);
        apiServer = new ApiServer(this);
        getServer().getScheduler().runTaskAsynchronously(this, apiServer::start);
    }

    @Override
    public void onDisable() {
        disableCobaltPlugin(this);
        if (apiServer != null) {
            apiServer.stop();
        }
    }

    // ##### MANAGERS #####

    private final Map<CobaltPlugin, Map<Class<?>, Manager>> managers;

    public <T extends Manager> T getManager(CobaltPlugin plugin, Class<T> managerClass) {
        this.managers.computeIfAbsent(plugin, k -> new LinkedHashMap<>());

        if (this.managers.get(plugin).containsKey(managerClass))
            return (T) this.managers.get(plugin).get(managerClass);

        try {
            long time = System.currentTimeMillis();

            // plugin.getLogger().info("Reloading manager " + managerClass.getName());

            T manager = managerClass.getConstructor(plugin.getClass()).newInstance(plugin);
            this.managers.get(plugin).put(managerClass, manager);
            manager.reload();

            // plugin.getLogger().info("Reloaded manager " + managerClass.getName() + " in " + (System.currentTimeMillis() - time) + "ms");

            return manager;
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void reloadManagers() {
        CobaltPlugin.super.reloadManagers();

        // Disable all active managers first
        if (this.managers.get(this) != null) this.managers.get(this).values().forEach(Manager::disable);

        // Register managers using getManager()
        this.getManager(this, DataManager.class);
        this.getManager(this, LocaleManager.class);
        this.getManager(this, GlyphManager.class);
        this.getManager(this, ParticleEffectManager.class);
        this.getManager(this, ItemSectionManager.class);
        this.getManager(this, ComponentManager.class);
        this.getManager(this, RecipeManager.class);
        this.getManager(this, EnchantmentManager.class);
        this.getManager(this, CustomItemManager.class);
        this.getManager(this, CustomEntityManager.class);
    }

    // ##### LISTENERS #####


    @Override
    public void registerListeners() {
        CobaltPlugin.super.registerListeners();
        // Bukkit.getPluginManager().registerEvents(..., this)
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
    }

    // ##### COMMAND REGISTRATION #####


    @Override
    public void registerCommands() {
        CobaltPlugin.super.registerCommands();

        CobaltCommand.register();
        AcceptCommand.register();
        ParticleCommand.register();
        CGiveCommand.createCgiveCommand();
        CSummonCommand.register();
        EditCommand.register();
        RotateAround.register();
        EaseCommand.register();
        IsCustomItemCommand.register();
        TestCommand.register();
        CustomEntityCommand.register();
    }

    // ##### PLUGIN REGISTRATION #####

    private final static Set<CobaltPlugin> cobaltPlugins = new HashSet<>();

    public void disableCobaltPlugin(CobaltPlugin plugin) {
        long time = System.currentTimeMillis();
        if (this.managers.get(plugin) != null) this.managers.get(plugin).values().forEach(Manager::disable);
        plugin.getLogger().info("Disabled managers in " + (System.currentTimeMillis() - time) + "ms");
        cobaltPlugins.remove(plugin);
    }

    public boolean registerCobaltPlugin(CobaltPlugin plugin) {

        getLogger().info("Registering Plugin " + plugin.getName() + ".");

        if (cobaltPlugins.add(plugin)) {

            // Pre Init
            plugin.preInit();

            // Connect to database if the plugin is CobaltCore
            if (plugin instanceof CobaltCore) {
                getLogger().info("Instantiating Database..."); // TODO: Do this for every plugin, create new database file for each plugin
                db = new SQLite(this); // TODO: Move to DataManager
                db.load();
            }

            // Register Locale for Plugin
            long time = System.currentTimeMillis();
            LocaleManager.loadLocale(plugin);
            plugin.getLogger().info("Loaded locale in " + (System.currentTimeMillis() - time) + "ms");

            // Init Database Tables
            time = System.currentTimeMillis();
            plugin.initDatabaseTables(); // TODO: Remove this method (Needs to be removed from other plugins too)
            plugin.getLogger().info("Initialized database in " + (System.currentTimeMillis() - time) + "ms");

            // Reloads all Managers
            time = System.currentTimeMillis();
            plugin.reloadManagers();
            plugin.getLogger().info("Reloaded managers in " + (System.currentTimeMillis() - time) + "ms");

            // Load custom items
            // NOTE: Must be run after manager registration. Otherwise, item components do not work
            ItemSectionManager.load(plugin, false);
            CustomItemManager.loadItemFiles(plugin, false);
            CustomEntityManager.loadEntityFiles(plugin, false);

            // Registers all Commands
            time = System.currentTimeMillis();
            plugin.registerCommands();
            plugin.getLogger().info("Commands registered in " + (System.currentTimeMillis() - time) + "ms");

            // Registers all Listeners
            time = System.currentTimeMillis();
            plugin.registerListeners();
            plugin.getLogger().info("Registered Listeners in " + (System.currentTimeMillis() - time) + "ms");

            // Post Init
            plugin.postInit();
        }

        // getLogger().info("Reloading cobalt command...");
        // CobaltCommand.register();
        // SettingCommand.register();
        // CommandGenerator.register();

        getLogger().info("Successfully registered " + plugin.getName() + ".");
        return true;
    }

    public Database getSQLiteDatabase() {
        return db;
    }

    public static CobaltCore getInstance() {
        return INSTANCE;
    }

    public static Set<CobaltPlugin> getRegisteredCobaltPlugins() {
        return cobaltPlugins;
    }

    @Override
    public String getInternalName() {
        return "CobaltCore";
    }

}
