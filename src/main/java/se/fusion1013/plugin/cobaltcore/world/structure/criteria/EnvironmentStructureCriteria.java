package se.fusion1013.plugin.cobaltcore.world.structure.criteria;

import org.bukkit.Location;
import org.bukkit.World;

public record EnvironmentStructureCriteria(World.Environment... environments) implements IStructureGenerationCriteria {

    @Override
    public boolean generationCriteriaAchieved(Location location) {
        for (World.Environment environment : environments) {
            if (location.getWorld().getEnvironment().equals(environment)) return true;
        }
        return false;
    }
}
