package se.fusion1013.plugin.cobaltcore.world.structure.criteria;

import org.bukkit.Location;
import org.bukkit.block.Biome;

public record BiomeStructureCriteria(Biome... biomes) implements IStructureGenerationCriteria {

    // ----- CHECKING -----

    @Override
    public boolean generationCriteriaAchieved(Location location) {
        for (Biome biome : biomes) {
            if (location.getBlock().getBiome() == biome) return true;
        }
        return false;
    }
}
