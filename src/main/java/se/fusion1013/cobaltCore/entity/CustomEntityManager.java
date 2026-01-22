package se.fusion1013.cobaltCore.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.entity.loader.EntityLoader;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltCore.util.IProviderStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomEntityManager extends Manager<CobaltCore> implements Listener {

    public static final NamespacedKey CUSTOM_ENTITY_KEY = new NamespacedKey(CobaltCore.getInstance(), "is_custom_entity");
    public static final NamespacedKey CUSTOM_ENTITY_TYPE_KEY = new NamespacedKey(CobaltCore.getInstance(), "custom_entity_type");
    private static final Map<String, ICustomEntity> CUSTOM_ENTITY_TYPES = new HashMap<>();
    private static final Map<UUID, ICustomEntityInstance> CUSTOM_ENTITY_INSTANCES = new HashMap<>();
    private static final EntityLoader ENTITY_LOADER = new EntityLoader();
    private static CustomEntityManager INSTANCE;

    public void spawnEntity(String entity, World world, Location location) {
        ICustomEntity customEntity = CUSTOM_ENTITY_TYPES.get(entity);
        if (customEntity == null) return;

        ICustomEntityInstance instance = customEntity.spawn(world, location);
        CUSTOM_ENTITY_INSTANCES.put(instance.entity().getUniqueId(), instance);
    }

    // ----- ENTITY FILE LOADING -----

    public static void loadEntityFiles(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "entities/", new IProviderStorage() {
            @Override
            public void put(String key, INameProvider provider) {
                register(provider);
            }

            @Override
            public boolean has(String key) {
                return getEntityType(key) != null;
            }

            @Override
            public INameProvider get(String key) {
                return getEntityType(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return ENTITY_LOADER.load(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return ENTITY_LOADER.load(json);
            }
        }, overwrite);
    }

    // ----- RELOADING / DISABLING -----

    public static void reloadEntities() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadEntityFiles(plugin, true);
        }
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
        createEntityTickHandler();
    }

    private void createEntityTickHandler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CobaltCore.getInstance(), () -> {
            // TODO: Iterate over all custom entity instances and execute their tick functions
            for (ICustomEntityInstance instance : CUSTOM_ENTITY_INSTANCES.values()) {
                Entity entity = instance.entity();
                World world = entity.getWorld();
                Location location = entity.getLocation();
                world.spawnParticle(Particle.FLAME, location, 10, 1, 1, 1, 0);
            }
        }, 0, 1);
    }

    @Override
    public void disable() {

    }

    private void loadEntity(Entity entity) {
        if (!isCustomEntity(entity)) return;

        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        if (!persistentDataContainer.has(CUSTOM_ENTITY_TYPE_KEY)) return;

        String internalEntityName = persistentDataContainer.get(CUSTOM_ENTITY_TYPE_KEY, PersistentDataType.STRING);
        ICustomEntity customEntity = CUSTOM_ENTITY_TYPES.get(internalEntityName);
        if (customEntity == null) return;

        ICustomEntityInstance instance = customEntity.load(entity);
        CUSTOM_ENTITY_INSTANCES.put(entity.getUniqueId(), instance);
    }

    private void unloadEntity(Entity entity) {
        if (!isCustomEntity(entity)) return;

        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        if (!persistentDataContainer.has(CUSTOM_ENTITY_TYPE_KEY)) return;

        CUSTOM_ENTITY_INSTANCES.remove(entity.getUniqueId());
    }

    // ----- EVENTS -----

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        loadEntity(event.getEntity());
    }

    @EventHandler
    public void onEntityChunkLoad(EntitiesLoadEvent event) {
        event.getEntities().forEach(this::loadEntity);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        unloadEntity(event.getEntity());
    }

    @EventHandler
    public void onEntityUnload(EntitiesUnloadEvent event) {
        event.getEntities().forEach(this::unloadEntity);
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent event) {
        unloadEntity(event.getEntity());
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        unloadEntity(event.getEntity());
    }

    // ----- REGISTER -----

    public static ICustomEntity register(INameProvider entity) {
        return register((ICustomEntity) entity);
    }

    private static ICustomEntity register(ICustomEntity entity) {
        CUSTOM_ENTITY_TYPES.put(entity.getId(), entity);
        return entity;
    }

    // ----- GETTERS / SETTERS -----

    public static boolean isCustomEntity(Entity entity) {
        PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
        return persistentDataContainer.has(CUSTOM_ENTITY_KEY, PersistentDataType.INTEGER);
    }

    /**
     * Gets a Custom Entity handler class.
     *
     * @param id the id of the Custom Entity.
     * @return a Custom Entity, or null if it does not exist.
     */
    public static ICustomEntity getEntityType(String id) {
        return CUSTOM_ENTITY_TYPES.getOrDefault(id, null);
    }

    public static String[] getCustomEntityNames() {
        return CUSTOM_ENTITY_TYPES.keySet().toArray(new String[0]);
    }

    public static UUID[] getCustomEntityInstanceUUIDS() {
        return CUSTOM_ENTITY_INSTANCES.keySet().toArray(new UUID[0]);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    public static CustomEntityManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomEntityManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

    public CustomEntityManager(CobaltCore plugin) {
        super(plugin);
        INSTANCE = this;
    }
}
