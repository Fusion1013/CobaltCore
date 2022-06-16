package se.fusion1013.plugin.cobaltcore.world.structure;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandExecutor;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandHandler;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandManager;
import se.fusion1013.plugin.cobaltcore.commands.system.CommandResult;
import se.fusion1013.plugin.cobaltcore.database.structure.IStructureDao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.manager.Manager;
import se.fusion1013.plugin.cobaltcore.util.FileUtil;
import se.fusion1013.plugin.cobaltcore.util.LocationUUID;
import se.fusion1013.plugin.cobaltcore.util.kdtree.Hyperpoint;
import se.fusion1013.plugin.cobaltcore.util.kdtree.IMultiPoint;
import se.fusion1013.plugin.cobaltcore.util.kdtree.KDTree;
import se.fusion1013.plugin.cobaltcore.util.kdtree.TwoDPoint;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.IStructure;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.StructureInstance;

import java.util.*;

/**
 * The structure manager handles creation and storage of structures.
 */
public class StructureManager extends Manager implements CommandExecutor, Listener {

    // ----- VARIABLES -----

    private static final Map<String, IStructure> registeredStructures = new HashMap<>();

    private static final Map<String, KDTree> structureChunkLocations = new HashMap<>(); // <StructureName, LocationTree> // TODO: Incorporate different dimensions (Map<World, Map<String, KDTree>>)

    private static final Map<Long, Map<Location, String>> loadedStructures = new HashMap<>();
    private static final Map<Long, Map<Location, String>> unloadedStructures = new HashMap<>();

    private static Map<Long, Map<LocationUUID, String>> structures = new HashMap<>();

    private static Map<Vector, IStructure> alwaysLoadStructure = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public StructureManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- DILAPIDATION SYSTEM -----

    @CommandHandler(
            parameterNames = {"corner1","corner2","integrity"},
            overrideTypes = {CommandHandler.ParameterType.LOCATION_BLOCK, CommandHandler.ParameterType.LOCATION_BLOCK, CommandHandler.ParameterType.NONE}
    )
    public CommandResult dilapidate(Location corner1, Location corner2, double integrity) {
        Dilapidate dil = new Dilapidate(corner1, corner2, integrity);
        return CommandResult.SUCCESS;
    }

    // ----- STRUCTURE REGISTER -----

    public static IStructure register(IStructure structure) {
        registeredStructures.put(structure.getName(), structure);
        return structure;
    }

    public static IStructure registerAlwaysGenerate(IStructure structure, Vector vector) {
        alwaysLoadStructure.put(vector, structure);

        for (World world : Bukkit.getWorlds()) {
            Location location = new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());

            if (loadedStructures.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>()).get(location) != null) continue;
            if (unloadedStructures.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>()).get(location) != null) continue;

            boolean generated = structure.attemptGenerate(location, 1);

            if (generated) {

                TwoDPoint point = new TwoDPoint(location.getChunk().getX() * 16, location.getChunk().getZ() * 16);
                structureChunkLocations.computeIfAbsent(structure.getName(), k -> new KDTree(2));
                structureChunkLocations.get(structure.getName()).insert(point);
                structures.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>()).put(new LocationUUID(UUID.randomUUID(), location), structure.getName());
                loadedStructures.computeIfAbsent(location.getChunk().getChunkKey(), k -> new HashMap<>()).put(location, structure.getName());
            }
        }

        return structure;
    }

    // ----- CHUNK LOAD EVENT -----

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // Unload Structures
        Map<Location, String> structures = loadedStructures.get(event.getChunk().getChunkKey());
        if (structures != null) {
            // Move the structure to loaded structures
            loadedStructures.remove(event.getChunk().getChunkKey());
            unloadedStructures.put(event.getChunk().getChunkKey(), structures);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // attemptAlwaysGenerate(event.getPlayer().getChunk());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        // Load Structures
        Map<Location, String> structures = unloadedStructures.get(event.getChunk().getChunkKey());
        if (structures != null) {
            // Move the structure to loaded structures
            unloadedStructures.remove(event.getChunk().getChunkKey());
            loadedStructures.put(event.getChunk().getChunkKey(), structures);
        }

        // Structure Generation
        if (event.isNewChunk()) {

            // Chunk info
            World world = event.getWorld();
            Chunk chunk = event.getChunk();

            for (IStructure structure : registeredStructures.values()) {
                if (!structure.getNaturalGeneration()) continue;

                // Create noise generators with the structure offset seed
                NoiseGenerator noise = new PerlinNoiseGenerator(world);
                double mult = .5;

                // Structure generation
                if (noise.noise(chunk.getX() * mult, chunk.getZ() * mult) >= structure.getGenerationThreshold()) {
                    Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
                        attemptStructureGeneration(world, chunk, structure, noise, mult);
                    });
                }
            }
        }
    }

    private void attemptStructureGeneration(World world, Chunk chunk, IStructure structure, NoiseGenerator noise, double mult) {
        // Make sure new structure is far enough away from the nearest one
        TwoDPoint point = new TwoDPoint(chunk.getX() * 16, chunk.getZ() * 16);

        IMultiPoint nearestPoint = structureChunkLocations.computeIfAbsent(structure.getName(), k -> new KDTree(2)).nearest(point);
        if (nearestPoint != null) if (nearestPoint.distance(point) < structure.getMinDistance()) return;

        // CobaltCore.getInstance().getLogger().info("Attempting to generate structure " + structure.getName() + "...");

        // Attempt to generate a structure at the height
        for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
            double verticalNoise = noise.noise(chunk.getX() * mult, y * mult, chunk.getZ() * mult);

            Location generationLocation = new Location(chunk.getWorld(), chunk.getX() * 16, y, chunk.getZ() * 16);
            boolean generated = structure.attemptGenerate(generationLocation, verticalNoise);

            if (generated) {
                structureChunkLocations.get(structure.getName()).insert(point);
                structures.computeIfAbsent(chunk.getChunkKey(), k -> new HashMap<>()).put(new LocationUUID(UUID.randomUUID(), generationLocation), structure.getName());
                loadedStructures.computeIfAbsent(chunk.getChunkKey(), k -> new HashMap<>()).put(generationLocation, structure.getName());
                CobaltCore.getInstance().getLogger().info("Generated Structure " + structure.getName() + " at location " + generationLocation.toVector());
                break;
            }
        }
    }

    // ----- GETTERS / SETTERS -----

    /**
     * Gets an <code>IStructure</code> from the registered structures.
     *
     * @param name the name of the <code>IStructure</code>.
     * @return the <code>IStructure</code>.
     */
    public static IStructure getRegisteredStructure(String name) {
        return registeredStructures.get(name);
    }

    /**
     * Gets all loaded structures.
     *
     * @return all loaded structures.
     */
    public static List<StructureInstance> getLoadedStructures() {
        List<StructureInstance> structureInstances = new ArrayList<>();

        for (Map<Location, String> locNamePair : loadedStructures.values()) {
            for (Location location : locNamePair.keySet()) {
                String structureName = locNamePair.get(location);
                IStructure structure = getRegisteredStructure(structureName);

                if (structure == null) structure = alwaysLoadStructure.get(new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                if (structure == null) continue;

                structureInstances.add(new StructureInstance(location, structure));
            }
        }

        return structureInstances;
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

        // Load Structures From Database
        structures = DataManager.getInstance().getDao(IStructureDao.class).getStructures();

        // Put structure either into unloaded or loaded map
        for (Map<LocationUUID, String> locName : structures.values()) {
            for (LocationUUID locationUUID : locName.keySet()) {
                String name = locName.get(locationUUID);

                if (locationUUID.location().isChunkLoaded()) {
                    loadedStructures.computeIfAbsent(locationUUID.location().getChunk().getChunkKey(), k -> new HashMap<>()).put(locationUUID.location(), name);
                } else {
                    unloadedStructures.computeIfAbsent(locationUUID.location().getChunk().getChunkKey(), k -> new HashMap<>()).put(locationUUID.location(), name);
                }
            }
        }

        CobaltCore.getInstance().getLogger().info("Loaded " + structures.size() + " chunks with structures");

        // Create KDTree
        for (Map<LocationUUID, String> locStruPair : structures.values()) {
            for (LocationUUID location : locStruPair.keySet()) {
                double[] locDouble = new double[] {location.location().getChunk().getX(), location.location().getChunk().getZ()};
                structureChunkLocations.computeIfAbsent(locStruPair.get(location), k -> new KDTree(2)).insert(new Hyperpoint(locDouble));
            }
        }

        // Register Commands
        CommandManager.getInstance().registerCommandModule("structure", this);

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(this, CobaltCore.getInstance());
        // Bukkit.getPluginManager().registerEvents(new StructureEvents(), CobaltCore.getInstance());

        StructureEvents events = new StructureEvents();
        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> events.onEvent(event), EventPriority.NORMAL, CobaltCore.getInstance(), false);
        for (HandlerList handler : HandlerList.getHandlerLists()) handler.register(registeredListener);
    }

    @Override
    public void disable() {
        DataManager.getInstance().getDao(IStructureDao.class).saveStructures(structures);
    }

    // ----- INSTANCE VARIABLE & METHOD -----

    private static StructureManager INSTANCE = null;
    /**
     * Returns the object representing this <code>StructureManager</code>.
     *
     * @return The object of this class
     */
    public static StructureManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new StructureManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }

}
