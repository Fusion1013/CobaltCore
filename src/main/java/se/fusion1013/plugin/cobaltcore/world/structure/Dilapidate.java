package se.fusion1013.plugin.cobaltcore.world.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Dilapidate {

    // ----- VARIABLES -----

    // Weights
    final double stabilityWeight = 1;
    final double hardnessWeight = 1;

    // Structure Information
    Location corner;

    int width;
    int depth;
    int height;

    // Dilapidation Info
    double integrity = 1;

    double[][][] blockStability;
    float[][][] blockHardness;

    double[][][] integrityArray; // Contains the final information on the stability of the blocks

    Material[] onlyRemove;
    
    // Hardness Overrides
    private static final Map<Material, Float> hardnessOverride = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public Dilapidate(double integrity) {
        this.integrity = (1-integrity);
    }

    public void run(Location corner, int width, int height, int depth) {
        this.corner = corner;

        this.width = width;
        this.height = height;
        this.depth = depth;

        constructHardnessOverrides();

        createStabilityArray();
        createHardnessArray();

        createIntegrityArray();

        destroyBlocks();
    }

    public Dilapidate(Location corner1, Location corner2, double integrity) {

        int x = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int y = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int z = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        corner = new Location(corner1.getWorld(), x, y, z);

        width = Math.abs(corner1.getBlockX() - corner2.getBlockX())+1;
        height = Math.abs(corner1.getBlockY() - corner2.getBlockY())+1;
        depth = Math.abs(corner1.getBlockZ() - corner2.getBlockZ())+1;

        this.integrity = (1-integrity);

        createStabilityArray(); // Create the initial stability array
        createHardnessArray();

        // TODO: More pre-processing

        createIntegrityArray();

        destroyBlocks(); // Destroy blocks according to the stability array and the integrity modifier

    }

    // ----- PRE-PROCESSING METHODS -----

    private void createStabilityArray() {
        blockStability = new double[width][height][depth];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {

                    Block block = corner.clone().add(new Vector(x, y, z)).getBlock();

                    // If the block is air, set integrity to 0 and continue
                    if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                        blockStability[x][y][z] = 0;
                        continue;
                    }

                    // Get adjacent blocks
                    Block xBlock1 = corner.clone().add(new Vector(x+1, y, z)).getBlock();
                    Block xBlock2 = corner.clone().add(new Vector(x-1, y, z)).getBlock();

                    Block yBlock1 = corner.clone().add(new Vector(x, y+1, z)).getBlock();
                    Block yBlock2 = corner.clone().add(new Vector(x, y-1, z)).getBlock();

                    Block zBlock1 = corner.clone().add(new Vector(x, y, z+1)).getBlock();
                    Block zBlock2 = corner.clone().add(new Vector(x, y, z-1)).getBlock();

                    // Calculate structural integrity of the block
                    double currentIntegrity = 0;
                    if (xBlock1.getType() != Material.AIR && xBlock1.getType() != Material.CAVE_AIR) currentIntegrity += .75;
                    if (xBlock2.getType() != Material.AIR && xBlock2.getType() != Material.CAVE_AIR) currentIntegrity += .75;

                    // Blocks in the y-direction contribute more to the integrity, becauase gravity
                    if (yBlock1.getType() != Material.AIR && yBlock1.getType() != Material.CAVE_AIR) currentIntegrity += 1.5;
                    if (yBlock2.getType() != Material.AIR && yBlock2.getType() != Material.CAVE_AIR) currentIntegrity += 1.5;

                    if (zBlock1.getType() != Material.AIR && zBlock1.getType() != Material.CAVE_AIR) currentIntegrity += .75;
                    if (zBlock2.getType() != Material.AIR && zBlock2.getType() != Material.CAVE_AIR) currentIntegrity += .75;

                    currentIntegrity /= 6.0;

                    blockStability[x][y][z] = currentIntegrity;

                }
            }
        }
    }

    private void createHardnessArray() {
        blockHardness = new float[width][height][depth];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Block block = corner.clone().add(new Vector(x, y, z)).getBlock();
                    if (hardnessOverride.containsKey(block.getType())) blockHardness[x][y][z] = hardnessOverride.get(block.getType()) / 10;
                    else blockHardness[x][y][z] = block.getType().getHardness() / 10;
                }
            }
        }
    }

    private void createIntegrityArray() {
        integrityArray = new double[width][height][depth];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {

                    integrityArray[x][y][z] = (stabilityWeight * blockStability[x][y][z]) + (hardnessWeight * blockHardness[x][y][z]);

                }
            }
        }
    }

    // ----- PROCESSING -----

    private void destroyBlocks() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Block block = corner.clone().add(new Vector(x, y, z)).getBlock();
                    if (integrityArray[x][y][z] < integrity) {
                        if (onlyRemove != null) {
                            for (Material mat : onlyRemove) {
                                if (mat == block.getType()) block.setType(Material.AIR);
                            }
                        } else {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }


    public static void constructHardnessOverrides() {
        hardnessOverride.put(Material.CHEST, 5f);
        hardnessOverride.put(Material.BARREL, 5f);
    }

    // ----- GETTERS / SETTERS -----

    public Dilapidate setOnlyRemove(Material[] onlyRemove) {
        this.onlyRemove = onlyRemove;
        return this;
    }
}
