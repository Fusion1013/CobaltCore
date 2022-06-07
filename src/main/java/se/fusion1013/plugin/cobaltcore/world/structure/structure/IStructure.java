package se.fusion1013.plugin.cobaltcore.world.structure.structure;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.noise.NoiseGenerator;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

public interface IStructure {

    // ----- GENERAL STRUCTURE INFO -----

    Plugin getOwnerPlugin();

    String getStructureFilePath();

    int getId();

    String getName();

    // ----- GENERATION PARAMETERS -----

    boolean getNaturalGeneration();

    double getGenerationThreshold();

    /**
     * Gets the minimum distance allowed between two structures of the same type.
     *
     * @return the minimum distance.
     */
    int getMinDistance();

    // ----- GENERATION METHODS -----

    boolean attemptGenerate(Location location, double threshold);
    boolean attemptGenerate(Location location, double threshold, int depth);

    void generate(Location location);

    boolean softForceGenerate(Location location);
    void forceGenerate(Location location);

    /**
     * Checks if the structure can generate at this <code>Location</code> with the given threshold.
     *
     * @param location the <code>Location</code> to check generation conditions for.
     * @param threshold the generation threshold of the structure.
     * @return whether the structure can generate or not.
     */
    boolean canGenerate(Location location, double threshold);

    // ----- EVENT EXECUTING -----

    <T extends Event> void executeEvent(T event, Location location);

    // ----- GETTERS / SETTERS -----

    NoiseGenerator getNoiseGenerator();

    StructureUtil.StructureHolder getStructureHolder();

    IStructure getRotatedClone();

    void rotate();

}
