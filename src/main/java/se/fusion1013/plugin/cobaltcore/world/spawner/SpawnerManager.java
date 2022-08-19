package se.fusion1013.plugin.cobaltcore.world.spawner;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.spawner.ICustomSpawnerDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.entity.ICustomEntity;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.HashMap;
import java.util.Map;

public class SpawnerManager extends Manager implements Listener, Runnable {

    // ----- VARIABLES -----

    // Store spawners together with the chunk they are in. Only spawners which chunks are loaded will be in here
    Map<Long, Map<Location, CustomSpawner>> loadedSpawners = new HashMap<>(); // The long is the chunk key
    Map<Long, Map<Location, CustomSpawner>> unloadedSpawners = new HashMap<>();

    // ----- LISTENERS -----

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Chunk chunk = location.getChunk();

        Map<Location, CustomSpawner> loaded = loadedSpawners.get(chunk.getChunkKey());
        if (loaded != null) {
            CustomSpawner spawner = loaded.get(location);
            if (spawner != null) {
                loaded.remove(location);
                DataManager.getInstance().getDao(ICustomSpawnerDao.class).removeCustomSpawner(spawner.getUuid());
            }
        }

        Map<Location, CustomSpawner> unloaded = unloadedSpawners.get(chunk.getChunkKey());
        if (unloaded != null) {
            CustomSpawner spawner = unloaded.get(location);
            if (spawner != null) {
                unloaded.remove(location);
                DataManager.getInstance().getDao(ICustomSpawnerDao.class).removeCustomSpawner(spawner.getUuid());
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Map<Location, CustomSpawner> spawners = loadedSpawners.get(event.getChunk().getChunkKey());
        if (spawners == null) return;

        // Disable the spawner and move it to the unloaded spawner map
        loadedSpawners.remove(event.getChunk().getChunkKey());
        unloadedSpawners.put(event.getChunk().getChunkKey(), spawners);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Map<Location, CustomSpawner> spawners = unloadedSpawners.get(event.getChunk().getChunkKey());
        if (spawners == null) return;

        // Enable the spawner and move it to the loaded spawner map
        unloadedSpawners.remove(event.getChunk().getChunkKey());
        loadedSpawners.put(event.getChunk().getChunkKey(), spawners);
    }

    // ----- RUNNABLE -----

    @Override
    public void run() {
        // Tick all loaded spawners
        for (Map<Location, CustomSpawner> spawners : loadedSpawners.values()) {
            for (Location location : spawners.keySet()) {
                CustomSpawner spawner = spawners.get(location);
                if (spawner.removeNextTick) {
                    spawners.remove(location);
                    DataManager.getInstance().getDao(ICustomSpawnerDao.class).removeCustomSpawner(spawner.getUuid());
                }
                spawner.tick();
            }
        }
    }

    // ----- SPAWNER PLACING -----

    public void placeSpawner(Location location, String entity, int spawnCount, double activationRange, int spawnRadius) {
        CustomSpawner spawner = new CustomSpawner(location, entity, spawnCount, activationRange, spawnRadius);
        Map<Location, CustomSpawner> spawnerMap = loadedSpawners.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>());
        spawnerMap.put(location, spawner);
        loadedSpawners.put(location.getChunk().getChunkKey(), spawnerMap);
    }

    public void placeSpawner(Location location, String entity, int spawnCount, double activationRange, int spawnRadius, int cooldown) {
        CustomSpawner spawner = new CustomSpawner(location, entity, spawnCount, activationRange, spawnRadius, cooldown);
        Map<Location, CustomSpawner> spawnerMap = loadedSpawners.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>());
        spawnerMap.put(location, spawner);
        loadedSpawners.put(location.getChunk().getChunkKey(), spawnerMap);
    }

    public void placeSpawner(CustomSpawner spawnerTemplate, Location location) {
        spawnerTemplate.setLocation(location);
        Map<Location, CustomSpawner> spawnerMap = loadedSpawners.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>());
        spawnerMap.put(location, spawnerTemplate);
        loadedSpawners.put(location.getChunk().getChunkKey(), spawnerMap);
    }

    // ----- SPAWNER GETTING -----

    public CustomSpawner getSpawner(Location location) {
        Map<Location, CustomSpawner> spawnerMap = loadedSpawners.get(location.getChunk().getChunkKey());
        if (spawnerMap == null) spawnerMap = unloadedSpawners.get(location.getChunk().getChunkKey());
        if (spawnerMap == null) return null;

        return spawnerMap.get(location);
    }

    // ----- CONSTRUCTORS -----

    public SpawnerManager(CobaltCore cobaltCore) {
        super(cobaltCore);
        INSTANCE = this;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {
        unloadedSpawners = DataManager.getInstance().getDao(ICustomSpawnerDao.class).getCustomSpawners();

        // Move unloaded spawners in loaded chunks to loaded spawners map
        for (World world : Bukkit.getWorlds()) {
            for (Chunk c : world.getLoadedChunks()) {
                Map<Location, CustomSpawner> spawners = unloadedSpawners.get(c.getChunkKey());
                if (spawners != null) {
                    loadedSpawners.put(c.getChunkKey(), spawners);
                    unloadedSpawners.remove(c.getChunkKey());
                }
            }
        }

        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
        Bukkit.getScheduler().runTaskTimer(CobaltCore.getInstance(), this, 1, 1);
    }

    @Override
    public void disable() {
        // unloadAllSpawners();
        DataManager.getInstance().getDao(ICustomSpawnerDao.class).saveCustomSpawners(unloadedSpawners);
        DataManager.getInstance().getDao(ICustomSpawnerDao.class).saveCustomSpawners(loadedSpawners);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static SpawnerManager INSTANCE = null;
    /**
     * Returns the object representing this <code>SpawnerManager</code>.
     *
     * @return The object of this class
     */
    public static SpawnerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SpawnerManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
