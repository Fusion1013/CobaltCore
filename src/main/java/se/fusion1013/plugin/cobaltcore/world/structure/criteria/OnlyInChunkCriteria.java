package se.fusion1013.plugin.cobaltcore.world.structure.criteria;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class OnlyInChunkCriteria implements IStructureGenerationCriteria {

    // ----- VARIABLES -----

    int chunkX;
    int chunkZ;

    // ----- CONSTRUCTORS -----

    public OnlyInChunkCriteria(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    // ----- GENERATION -----

    @Override
    public boolean generationCriteriaAchieved(Location location) {
        Chunk chunk = location.getChunk();
        return chunk.getX() == chunkX && chunk.getZ() == chunkZ;
    }
}
