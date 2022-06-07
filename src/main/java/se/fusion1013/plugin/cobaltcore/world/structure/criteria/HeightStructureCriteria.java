package se.fusion1013.plugin.cobaltcore.world.structure.criteria;

import org.bukkit.Location;

public record HeightStructureCriteria(int maxHeight, int minDepth) implements IStructureGenerationCriteria {

    // ----- CHECKING -----

    @Override
    public boolean generationCriteriaAchieved(Location location) {
        return !(location.getY() > maxHeight) && !(location.getY() < minDepth);
    }
}
