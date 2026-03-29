package se.fusion1013.cobaltCore.components.conditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.MaterialVariable;
import se.fusion1013.cobaltCore.variable.VectorVariable;

import java.util.List;
import java.util.Map;

public class NearbyBlockCondition extends AbstractCondition {

    private final VectorVariable bounds = new VectorVariable("bounds");
    private final IntVariable amount = new IntVariable("amount", 1);
    private final MaterialVariable material = new MaterialVariable("material");

    @Override
    protected List<AbstractVariable<?, ?, ?, ?>> getVariables() {
        return List.of(bounds);
    }

    @Override
    protected boolean evaluateCondition(Map<String, Object> context) {
        Location location = getTargetLocation(context);
        if (location == null) return false;

        World world = location.getWorld();

        int nFound = findNearbyBlocks(location, world, bounds.getValue(), material.getValue());

        return nFound >= amount.getValue();
    }

    public static int findNearbyBlocks(Location location, World world, Vector bounds, Material material) {
        int nFound = 0;

        for (int x = location.getBlockX() - bounds.getBlockX(); x <= location.getBlockX() + bounds.getBlockX(); x++) {
            for (int y = location.getBlockY() - bounds.getBlockY(); y <= location.getBlockY() + bounds.getBlockY(); y++) {
                for (int z = location.getBlockZ() - bounds.getBlockZ(); z <= location.getBlockZ() + bounds.getBlockZ(); z++) {
                    Block block = world.getBlockAt(new Location(world, x, y, z));
                    Material blockType = block.getType();
                    if (blockType == material) nFound++;
//                    CobaltCore.getInstance().getLogger().info("FOUND: " + nFound + ". X: " + x + ", Y: " + y + ", Z: " + z);
                }
            }
        }
        return nFound;
    }

    @Override
    public String getInternalName() {
        return "nearby_block";
    }

    @Override
    public String getDescription() {
        return "Requires " + amount.getValue() + "x block " + material.getValue() + " in range " + bounds.getValue();
    }
}
