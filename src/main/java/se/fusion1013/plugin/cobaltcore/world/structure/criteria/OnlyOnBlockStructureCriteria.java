package se.fusion1013.plugin.cobaltcore.world.structure.criteria;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public record OnlyOnBlockStructureCriteria(Material... onlyOnBlocks) implements IStructureGenerationCriteria {

    @Override
    public boolean generationCriteriaAchieved(Location location) {
        if (onlyOnBlocks.length > 0) {
            boolean isOnBlock = false;
            for (Material material : onlyOnBlocks) {
                if (location.clone().add(new Vector(0, -1, 0)).getBlock().getType() == material) {
                    isOnBlock = true;
                    break;
                }
            }
            return isOnBlock;
        }
        return true;
    }
}
