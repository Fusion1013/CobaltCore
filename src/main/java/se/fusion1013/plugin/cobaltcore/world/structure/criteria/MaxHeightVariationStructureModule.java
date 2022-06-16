package se.fusion1013.plugin.cobaltcore.world.structure.criteria;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MaxHeightVariationStructureModule implements IStructureGenerationCriteria {

    int maxVariation;
    int width;
    int depth;

    public MaxHeightVariationStructureModule(int maxVariation, int width, int depth) {
        this.maxVariation = maxVariation;
        this.width = width;
        this.depth = depth;
    }

    @Override
    public boolean generationCriteriaAchieved(Location location) {
        int lowestBlock = location.toHighestLocation().getBlockY();
        int highestBlock = location.toHighestLocation().getBlockY();

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                Location currentLocation = location.clone().add(new Vector(x, 0, z)).toHighestLocation();

                while (!currentLocation.getBlock().getType().isOccluding()) currentLocation.add(new Vector(0, -1, 0));
                int current = currentLocation.getBlockY();

                if (current > highestBlock) highestBlock = current;
                else if (current < lowestBlock) lowestBlock = current;
            }
        }

        return highestBlock - lowestBlock < maxVariation;
    }
}
