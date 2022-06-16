package se.fusion1013.plugin.cobaltcore.world.structure.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.world.structure.IStructureEventHandler;
import se.fusion1013.plugin.cobaltcore.world.structure.criteria.IStructureGenerationCriteria;
import se.fusion1013.plugin.cobaltcore.world.structure.modules.IStructureModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractStructure implements IStructure {

    // ----- VARIABLES -----

    // Identification
    final Plugin ownerPlugin;
    final int id;
    final String structureName;
    final String structureFilePath;

    // Generation
    NoiseGenerator noiseGenerator;
    static final Material[] nonGroundBlocks = new Material[] {Material.AIR, Material.CAVE_AIR, Material.WATER};

    // Generation Criteria
    boolean naturalGeneration = true;
    double generationThreshold;
    int minDistance = 100;
    List<IStructureGenerationCriteria> generationCriteria = new ArrayList<>();

    // Generation Modifiers
    List<IStructureModule> structureModules = new ArrayList<>();

    // Event Handling
    Map<Class<?>, IStructureEventHandler<? extends Event>> structureEvents = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public AbstractStructure(Plugin ownerPlugin, int id, String structureName, String structureFilePath) {
        this.ownerPlugin = ownerPlugin;
        this.id = id;
        this.structureName = structureName;
        this.structureFilePath = structureFilePath;

        noiseGenerator = new PerlinNoiseGenerator(id);
    }

    public AbstractStructure(AbstractStructure target) {
        this.ownerPlugin = target.ownerPlugin;
        this.id = target.id;
        this.structureName = target.structureName;
        this.structureFilePath = target.structureFilePath;

        this.noiseGenerator = target.noiseGenerator;

        this.naturalGeneration = target.naturalGeneration;
        this.generationThreshold = target.generationThreshold;
        this.generationCriteria = target.generationCriteria;

        this.structureModules = target.structureModules;

        this.structureEvents = target.structureEvents;
        this.minDistance = target.minDistance;
    }

    // ----- GENERATION -----

    public void moveToGround(Location location) {

        boolean moveUp = isGroundBlock(location);

        if (moveUp) {
            for (int y = location.getBlockY(); y < location.getWorld().getMaxHeight(); y++) {
                if (!isGroundBlock(location)) break;
                location.setY(y);
            }
        } else {
            for (int y = location.getBlockY(); y > location.getWorld().getMinHeight(); y--) {
                location.setY(y);
                if (isGroundBlock(location)) break;
            }
        }
    }

    private boolean isGroundBlock(Location location) {
        for (Material mat : nonGroundBlocks) {
            if (location.getBlock().getType() == mat) return false;
        }

        return location.getBlock().getType().isSolid();
    }

    @Override
    public boolean attemptGenerate(Location location, double threshold, int depth) {
        return attemptGenerate(location, threshold);
    }

    @Override
    public boolean attemptGenerate(Location location, double threshold) {
        if (!naturalGeneration) return false;
        if (!canGenerate(location, threshold)) return false;
        generate(location);
        return true;
    }

    @Override
    public boolean softForceGenerate(Location location) {
        if (!canGenerate(location, 1)) return false;
        generate(location);
        return true;
    }

    @Override
    public void forceGenerate(Location location) {
        generate(location);
    }

    @Override
    public boolean canGenerate(Location location, double threshold) {
        if (threshold < generationThreshold) return false;
        for (IStructureGenerationCriteria criteria : generationCriteria) if (!criteria.generationCriteriaAchieved(location)) return false;
        return true;
    }

    // ----- EVENT HANDLING -----

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void executeEvent(T event, Location location) {
        IStructureEventHandler<T> handler = (IStructureEventHandler<T>) structureEvents.get(event.getClass());
        if (handler != null) handler.execute(event);
        for (IStructureModule module : structureModules) module.onEvent(event, location, getStructureHolder());
    }

    // ----- BUILDER -----

    protected static abstract class AbstractStructureBuilder<T extends AbstractStructure, B extends AbstractStructureBuilder> {

        // ----- VARIABLES -----

        protected T obj;

        // Identification
        final Plugin plugin;
        final int id;
        final String structureName;
        final String structureFilePath;

        // Generation Criteria
        boolean naturalGeneration;
        double generationThreshold = .7;
        int minDistance = 100;
        List<IStructureGenerationCriteria> generationCriteria = new ArrayList<>();

        // Generation Modifiers
        List<IStructureModule> structureModules = new ArrayList<>();

        // Event Handler
        Map<Class<?>, IStructureEventHandler<?>> eventHandlers = new HashMap<>();

        // ----- CONSTRUCTORS -----

        public AbstractStructureBuilder(Plugin plugin, int id, String structureName, String structureFilePath) {
            this.plugin = plugin;
            this.id = id;
            this.structureName = structureName;
            this.structureFilePath = structureFilePath;
        }

        // ----- BUILDING -----

        public T build() {
            obj = createObj();

            obj.setMinDistance(minDistance);
            obj.setNaturalGeneration(naturalGeneration);
            obj.setGenerationThreshold(generationThreshold);
            obj.setGenerationCriteria(generationCriteria);
            obj.setStructureModules(structureModules);

            return obj;
        }

        // ----- HELPER METHODS -----

        protected abstract T createObj();
        protected abstract B getThis();

        // ----- SETTERS -----

        public B setMinDistance(int minDistance) {
            this.minDistance = minDistance;
            return getThis();
        }

        public B setNaturalGeneration(boolean naturalGeneration) {
            this.naturalGeneration = naturalGeneration;
            return getThis();
        }

        public B setGenerationThreshold(double generationThreshold) {
            this.generationThreshold = generationThreshold;
            return getThis();
        }

        public B addGenerationCriteria(IStructureGenerationCriteria generationCriteria) {
            this.generationCriteria.add(generationCriteria);
            return getThis();
        }

        public B addStructureModule(IStructureModule structureModule) {
            this.structureModules.add(structureModule);
            return getThis();
        }

        public B addStructureEvent(Class<?> eventClass, IStructureEventHandler<?> eventHandler) {
            this.eventHandlers.put(eventClass, eventHandler);
            return getThis();
        }
    }

    public void setNaturalGeneration(boolean naturalGeneration) {
        this.naturalGeneration = naturalGeneration;
    }

    public void setGenerationThreshold(double generationThreshold) {
        this.generationThreshold = generationThreshold;
    }

    public void setGenerationCriteria(List<IStructureGenerationCriteria> generationCriteria) {
        this.generationCriteria = generationCriteria;
    }

    public void setStructureModules(List<IStructureModule> structureModules) {
        this.structureModules = structureModules;
    }

    public void setStructureEventHandlers(Map<Class<?>, IStructureEventHandler<?>> eventHandlers) {
        this.structureEvents = eventHandlers;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    @Override
    public boolean getNaturalGeneration() {
        return naturalGeneration;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return structureName;
    }

    @Override
    public double getGenerationThreshold() {
        return generationThreshold;
    }

    @Override
    public Plugin getOwnerPlugin() {
        return ownerPlugin;
    }

    @Override
    public String getStructureFilePath() {
        return structureFilePath;
    }

    @Override
    public int getMinDistance() {
        return minDistance;
    }
}
