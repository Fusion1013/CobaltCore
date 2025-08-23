package se.fusion1013.plugin.cobaltcore.world.structure.modifier;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import se.fusion1013.plugin.cobaltcore.util.Vector3Int;
import se.fusion1013.plugin.cobaltcore.util.heatmap.Heatmap3D;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WetnessStructureModifier {

    private final Location corner;
    private final int width;
    private final int depth;
    private final int height;

    private Material[][][] originalMaterials;
    private BlockData[][][] originalData;

    public WetnessStructureModifier(Location corner, int width, int depth, int height) {
        this.corner = corner;
        this.width = width;
        this.depth = depth;
        this.height = height;

        saveInitialState();
    }

    private void saveInitialState() {
        originalMaterials = new Material[width][height][depth];
        originalData = new BlockData[width][height][depth];

        var world = corner.getWorld();

        // Loop over all blocks in the region and save them to the arrays
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {

                    var location = new Location(world, x + corner.getX(), y + corner.getY(), z + corner.getZ());
                    var block = location.getBlock();

                    originalMaterials[x][y][z] = block.getType();
                    originalData[x][y][z] = block.getBlockData();

                }
            }
        }
    }

    public Heatmap3D getWetnessMap() {
        Heatmap3D heatmap = new Heatmap3D(1, 16);

        // Loop over all blocks in the materials and add them to the heatmap
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {


                    var material = originalMaterials[x][y][z];
                    var data = originalData[x][y][z];
                    if (data instanceof Waterlogged || material == Material.WATER) {
                        heatmap.addPoint(new Vector3Int(x, y, z));
                    }


                }
            }
        }

        return heatmap;
    }

    public void colorize() {
        var heatmap = getWetnessMap();
        var world = corner.getWorld();

        heatmap.points.forEach((id, value) -> {

            var valueNormalized = value / heatmap.maxValue;

            var split = id.split(",");
            var pos = new Vector3Int(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));

            var xPos = pos.x + corner.getX();
            var yPos = pos.y + corner.getY();
            var zPos = pos.z + corner.getZ();

            var location = new Location(world, xPos, yPos, zPos);
            // if (canReplace(location)) setConcrete(location, valueNormalized);
            // if (canReplace(location)) setMossStoneCombo(location, valueNormalized);
            if (canReplace(location)) tryReplaceWithWet(location, valueNormalized);
        });
    }

    private void setMossStoneCombo(Location location, float value) {
        var r = new Random();

        if (value <= 0) return;

        int type = r.nextInt(0, 2);
        boolean isMossy = r.nextFloat(value) > .2f;

        Material material = Material.STONE;

        if (type == 0) {
            if (isMossy) material = Material.MOSSY_COBBLESTONE;
            else material = getRandomStoneRough();
        } else {
            if (isMossy) material = Material.MOSSY_STONE_BRICKS;
            else material = getRandomStonePolished();
        }

        location.getBlock().setType(material);
    }

    private void tryReplaceWithWet(Location location, float wetness) {
        var r = new Random();

        if (wetness <= 0) return;

        boolean isMossy = r.nextFloat(wetness) > .2f;
        if (!isMossy) return;

        var material = WET_VARIANTS.get(location.getBlock().getType());
        if (material == null) return;

        location.getBlock().setType(material);
    }

    private Material getRandomStonePolished() {
        Random r = new Random();
        float val = r.nextFloat(0, 1);
        if (val < .5f) return Material.STONE_BRICKS;
        else if (val < .9f) return Material.STONE;
        else return Material.POLISHED_ANDESITE;
    }

    private Material getRandomStoneRough() {
        Random r = new Random();
        float val = r.nextFloat(0, 1);
        if (val < .4f) return Material.STONE;
        else if (val < .85f) return Material.ANDESITE;
        else return Material.COBBLESTONE;
    }

    private void setConcrete(Location location, float value) {
        var block = location.getBlock();

        // Set an appropriate block depending on the value of the heatmap
        if (value <= .1f) block.setType(Material.GRAY_CONCRETE);
        else if (value <= .2f) block.setType(Material.BROWN_CONCRETE);
        else if (value <= .3f) block.setType(Material.RED_CONCRETE);
        else if (value <= .4f) block.setType(Material.ORANGE_CONCRETE);
        else if (value <= .5f) block.setType(Material.YELLOW_CONCRETE);
        else if (value <= .6f) block.setType(Material.LIME_CONCRETE);
        else if (value <= .7f) block.setType(Material.GREEN_CONCRETE);
        else if (value <= .8f) block.setType(Material.CYAN_CONCRETE);
        else if (value <= .9f) block.setType(Material.LIGHT_BLUE_CONCRETE);
        else if (value <= 1f) block.setType(Material.BLUE_CONCRETE);
    }

    private boolean canReplace(Location location) {
        var material = location.getBlock().getType();

        if (material == Material.AIR) return false;
        if (material == Material.CAVE_AIR) return false;
        if (material == Material.WATER) return false;

        var data = location.getBlock().getBlockData();
        if (data instanceof Waterlogged) return false;

        return true;
    }

    private static final Map<Material, Material> WET_VARIANTS = new HashMap<>() {
        {
            // STONE BRICK
            put(Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS);
            put(Material.STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_STAIRS);
            put(Material.STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_SLAB);
            put(Material.STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL);

            // COBBLESTONE
            put(Material.COBBLESTONE, Material.MOSSY_COBBLESTONE);
            put(Material.COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE_STAIRS);
            put(Material.COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_SLAB);
            put(Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL);

            // GRASS
            put(Material.GRASS_BLOCK, Material.MOSS_BLOCK);
        }
    };

}
