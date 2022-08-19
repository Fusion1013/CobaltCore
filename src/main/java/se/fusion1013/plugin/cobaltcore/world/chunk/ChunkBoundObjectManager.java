package se.fusion1013.plugin.cobaltcore.world.chunk;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.*;

public class ChunkBoundObjectManager extends Manager implements Listener {

    // ----- VARIABLES -----

    private static final Map<String, Map<Class<?>, Map<UUID, IChunkBound<?>>>> LOADED_CHUNK_BOUND_OBJECTS = new HashMap<>();
    private static final Map<String, Map<Class<?>, Map<UUID, IChunkBound<?>>>> UNLOADED_CHUNK_BOUND_OBJECTS = new HashMap<>();

    // ----- GETTERS / SETTERS -----

    @SuppressWarnings("unchecked")
    public static <T> IChunkBound<T> getChunkBoundObject(Class<T> objectClass, Chunk chunk, UUID uuid) {
        Map<Class<?>, Map<UUID, IChunkBound<?>>> classMap = LOADED_CHUNK_BOUND_OBJECTS.get(getChunkWorldKey(chunk));
        if (classMap == null) return null;

        Map<UUID, IChunkBound<?>> uuidMap = classMap.get(objectClass);
        if (uuidMap == null) return null;

        return (IChunkBound<T>) uuidMap.get(uuid);
    }

    public static List<IChunkBound<?>> getLoadedOfType(Class<?> objectClass) {
        List<IChunkBound<?>> list = new ArrayList<>();

        for (Map<Class<?>, Map<UUID, IChunkBound<?>>> map : LOADED_CHUNK_BOUND_OBJECTS.values()) {
            Map<UUID, IChunkBound<?>> uuidChunkBoundMap = map.get(objectClass);
            if (uuidChunkBoundMap != null) list.addAll(uuidChunkBoundMap.values());
        }

        return list;
    }

    public static void removeChunkBound(Class<?> objectClass, UUID uuid) {
        for (Map<Class<?>, Map<UUID, IChunkBound<?>>> map : LOADED_CHUNK_BOUND_OBJECTS.values()) {
            Map<UUID, IChunkBound<?>> uuidChunkBoundMap = map.get(objectClass);
            if (uuidChunkBoundMap == null) continue;

            uuidChunkBoundMap.remove(uuid);
        }

        for (Map<Class<?>, Map<UUID, IChunkBound<?>>> map : UNLOADED_CHUNK_BOUND_OBJECTS.values()) {
            Map<UUID, IChunkBound<?>> uuidChunkBoundMap = map.get(objectClass);
            if (uuidChunkBoundMap == null) continue;

            uuidChunkBoundMap.remove(uuid);
        }
    }

    /**
     * Adds an object that should only be loaded when the chunk it is in is loaded to the map.
     *
     * @param chunk the chunk the object is in.
     * @param object the object to keep chunk bound.
     */
    public static void addChunkLoadableObject(Chunk chunk, IChunkBound<?> object) {
        if (chunk.isLoaded()) {
            LOADED_CHUNK_BOUND_OBJECTS.computeIfAbsent(getChunkWorldKey(chunk), k -> new HashMap<>()).computeIfAbsent(object.getClass(), k -> new HashMap<>()).put(object.getUUID(), object);
        } else {
            UNLOADED_CHUNK_BOUND_OBJECTS.computeIfAbsent(getChunkWorldKey(chunk), k -> new HashMap<>()).computeIfAbsent(object.getClass(), k -> new HashMap<>()).put(object.getUUID(), object);
        }
    }

    // ----- EVENT -----

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Load unloaded
        Map<Class<?>, Map<UUID, IChunkBound<?>>> chunkBounds = UNLOADED_CHUNK_BOUND_OBJECTS.get(getChunkWorldKey(event.getChunk()));
        if (chunkBounds == null) return;

        UNLOADED_CHUNK_BOUND_OBJECTS.remove(getChunkWorldKey(event.getChunk()));
        LOADED_CHUNK_BOUND_OBJECTS.put(getChunkWorldKey(event.getChunk()), chunkBounds);

        // Call onChunkLoad method for all newly loaded chunk-bound objects
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            for (Map<UUID, IChunkBound<?>> uuidChunkLoadedMap : chunkBounds.values()) {
                for (IChunkBound<?> chunkLoaded : uuidChunkLoadedMap.values()) {
                    chunkLoaded.onChunkLoad();
                }
            }
        });
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // Unload loaded
        Map<Class<?>, Map<UUID, IChunkBound<?>>> chunkBounds = LOADED_CHUNK_BOUND_OBJECTS.get(getChunkWorldKey(event.getChunk()));
        if (chunkBounds == null) return;

        LOADED_CHUNK_BOUND_OBJECTS.remove(getChunkWorldKey(event.getChunk()));
        UNLOADED_CHUNK_BOUND_OBJECTS.put(getChunkWorldKey(event.getChunk()), chunkBounds);

        // Call onChunkUnload method for all newly loaded chunk-bound objects
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            for (Map<UUID, IChunkBound<?>> uuidChunkLoadedMap : chunkBounds.values()) {
                for (IChunkBound<?> chunkLoaded : uuidChunkLoadedMap.values()) {
                    chunkLoaded.onChunkUnload();
                }
            }
        });
    }

    // ----- UTIL -----

    private static String getChunkWorldKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getChunkKey();
    }

    // ----- CONSTRUCTORS -----

    public ChunkBoundObjectManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    @Override
    public void reload() {
        Bukkit.getServer().getPluginManager().registerEvents(this, CobaltCore.getInstance());
    }

    @Override
    public void disable() {
    }
}
