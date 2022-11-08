package se.fusion1013.plugin.cobaltcore.util;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.world.protection.WorldGuardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockUtil {

    static int radius;
    static boolean hollow;

    static Location centerBlock;

    List<Location> blockLocations;

    public static void generateShape(GeometryUtil.Shape shape){
        switch (shape){
            case SPHERE -> generateSphere(centerBlock, radius, hollow);
        }
    }

    /**
     * Generates a sphere of block locations around the given center with a given radius.
     *
     * @param centerBlock the block to center the sphere on
     * @param radius the radius of the sphere
     * @param hollow whether the sphere should be hollow or not
     * @return a list of locations representing a sphere
     */
    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow){
        if (centerBlock == null) return new ArrayList<>();

        List<Location> circleBlocks = new ArrayList<>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++){
            for (int y = by - radius; y <= by + radius; y++){
                for (int z = bz - radius; z <= bz + radius; z++){

                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))){
                        Location l = new Location(centerBlock.getWorld(), x, y, z);

                        circleBlocks.add(l);
                    }
                }
            }
        }

        return circleBlocks;
    }

    /**
     * Sets blocks in a sphere around a given center, with a given radius and a given block material.
     *
     * @param centerBlock the center of the sphere
     * @param setBlock the block the sphere is made out of
     * @param radius the radius of the sphere
     * @return the number of blocks in the sphere
     */
    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius){
        return setBlocksInSphere(centerBlock, setBlock, radius, false, false, false, false);
    }

    /**
     * Sets blocks in a sphere around a given center, with a given radius and a given block material.
     *
     * @param centerBlock the center of the sphere
     * @param setBlock the block the sphere is made out of
     * @param radius the radius of the sphere
     * @param dropItems if the replacement process should drop blocks as items
     * @return the number of blocks in the sphere
     */
    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, false, false, false);
    }

    /**
     * Sets blocks in a sphere around a given center, with a given radius and a given block material.
     *
     * @param centerBlock the center of the sphere
     * @param setBlock the block the sphere is made out of
     * @param radius the radius of the sphere
     * @param dropItems if the replacement process should drop blocks as items
     * @param slowReplace if the replacement process should be slowed down by a few ticks, randomized for each block in the sphere
     * @return the number of blocks in the sphere
     */
    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, slowReplace, false, false);
    }

    /**
     * Sets blocks in a sphere around a given center, with a given radius and a given block material.
     *
     * @param centerBlock the center of the sphere
     * @param setBlock the block the sphere is made out of
     * @param radius the radius of the sphere
     * @param dropItems if the replacement process should drop blocks as items
     * @param slowReplace if the replacement process should be slowed down by a few ticks, randomized for each block in the sphere
     * @param replaceNonAir if non-air blocks should be replaced
     * @return the number of blocks in the sphere
     */
    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace, boolean replaceNonAir){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, slowReplace, replaceNonAir, false);
    }

    /**
     * Sets blocks in a sphere around a given center, with a given radius and a given block material.
     *
     * @param centerBlock the center of the sphere
     * @param setBlock the block the sphere is made out of
     * @param radius the radius of the sphere
     * @param dropItems if the replacement process should drop blocks as items
     * @param slowReplace if the replacement process should be slowed down by a few ticks, randomized for each block in the sphere
     * @param replaceNonAir if non-air blocks should be replaced
     * @param hollow if the sphere should be hollow
     * @return the number of blocks in the sphere
     */
    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace, boolean replaceNonAir, boolean hollow){
        return setBlocksInSphere(centerBlock, setBlock, radius, dropItems, slowReplace, replaceNonAir, hollow, false);
    }

    /**
     * Sets blocks in a sphere around a given center, with a given radius and a given block material.
     *
     * @param centerBlock the center of the sphere
     * @param setBlock the block the sphere is made out of
     * @param radius the radius of the sphere
     * @param dropItems if the replacement process should drop blocks as items
     * @param slowReplace if the replacement process should be slowed down by a few ticks, randomized for each block in the sphere
     * @param replaceNonAir if non-air blocks should be replaced
     * @param hollow if the sphere should be hollow
     * @param noSound if the blocks being broken should produce sounds
     * @return the number of blocks in the sphere
     */
    public static int setBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean slowReplace, boolean replaceNonAir, boolean hollow, boolean noSound){
        if (slowReplace) return setBlocksInSphereSlowly(centerBlock, setBlock, radius, dropItems, replaceNonAir, hollow, noSound);
        else {
            List<Location> circleBlocks = generateSphere(centerBlock, radius, hollow);
            for (Location l : circleBlocks){
                replaceBlock(l, setBlock, dropItems, replaceNonAir, noSound);
            }
            return circleBlocks.size();
        }
    }

    /**
     * Sets blocks in a sphere around a given center, with a given radius and a given block material.
     *
     * @param centerBlock the center of the sphere
     * @param setBlock the block the sphere is made out of
     * @param radius the radius of the sphere
     * @param dropItems if the replacement process should drop blocks as items
     * @param replaceNonAir if non-air blocks should be replaced
     * @param hollow if the sphere should be hollow
     * @param noSound if the blocks being broken should produce sounds
     * @return the number of blocks in the sphere
     */
    public static int setBlocksInSphereSlowly(Location centerBlock, Material setBlock, int radius, boolean dropItems, boolean replaceNonAir, boolean hollow, boolean noSound){
        List<Location> circleBlocks = generateSphere(centerBlock, radius, hollow);
        Random r = new Random();

        for (Location l : circleBlocks){
            Block block = l.getBlock();
            int breakTime = Math.min((int)block.getType().getHardness(), 20);

            if (block.getType() != setBlock){
                CobaltCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () ->
                        replaceBlock(l, setBlock, dropItems, replaceNonAir, noSound), r.nextInt(0, 10) + breakTime);
            }
        }
        return circleBlocks.size();
    }

    public static int replaceBlocksInSphere(Location centerBlock, Material replaceMaterial, Material setMaterial, int radius) {
        List<Location> circleBlocks = generateSphere(centerBlock, radius, hollow);
        for (Location l : circleBlocks){
            if (l.getBlock().getType() == replaceMaterial) l.getBlock().setType(setMaterial);
        }
        return circleBlocks.size();
    }

    public static int setTopBlocksInSphere(Location centerBlock, Material setBlock, int radius, boolean slow){
        List<Location> circleBlocks = generateSphere(centerBlock, radius, false);
        Random r = new Random();

        for (Location l : circleBlocks){
            Block block = l.getBlock();
            Block lowerBlock = l.add(new Vector(0, -1, 0)).getBlock();

            if (lowerBlock.getType() != Material.AIR && lowerBlock.getType() != setBlock && block.getType() == Material.AIR && block.getType() != setBlock){
                if (slow){
                    CobaltCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(CobaltCore.getInstance(), () -> block.setType(setBlock), r.nextInt(0, 10));
                } else {
                    block.setType(setBlock);
                }
            }
        }
        return circleBlocks.size();
    }

    /**
     * Replaces a block at the specified location with another one
     *
     * @param location the location of the block to replace
     * @param setBlock the block type to set it to
     * @param dropItems if the replacement process should drop the block as an item
     * @param replaceNonAir if non-air blocks should be replaced
     * @param noSound if the blocks being broken should produce sounds
     */
    private static void replaceBlock(Location location, Material setBlock, boolean dropItems, boolean replaceNonAir, boolean noSound){
        Block block = location.getBlock();
        if (block.getType() == setBlock) return;

        if (!replaceNonAir && block.getType() != Material.AIR) return;
        if (block.getType().getHardness() <= 0) return;

        if (!noSound){
            for (Player p : Bukkit.getOnlinePlayers()){
                p.playSound(location, block.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
            }
        }
        if (dropItems) block.breakNaturally();

        block.setType(setBlock);
    }

    private static boolean canPlaceBlock(Location location, boolean replaceNonAir){
        Block block = location.getBlock();

        if (block.getType() != Material.AIR && !replaceNonAir) return false;


        return true;
    }

    public static Vector getAveragePosition(List<BlockState> blockStates){
        Vector average = new Vector();

        for (BlockState state : blockStates){
            average.add(state.getLocation().toVector());
        }

        return average.multiply(1.0 / blockStates.size());
    }

    public static void setBlocks(List<BlockState> blockStates, Material setTo){
        for (BlockState b : blockStates){
            b.getLocation().getBlock().setType(setTo);
        }
    }

    public static void createExplosion(Location location, World world, int explosionRadius, boolean dropItems, boolean fire, boolean destroyBlocks) {

        WorldGuardManager manager = CobaltCore.getInstance().getManager(CobaltCore.getInstance(), WorldGuardManager.class);
        if (manager != null) if (!manager.isBlockBreakAllowed(location)) return;

        // Explode
        if (destroyBlocks) setBlocksInSphere(location, Material.AIR, explosionRadius, dropItems, false, true, false, true);
        int iterations = Math.max(1, explosionRadius * (explosionRadius / 4));
        for (int i = 0; i < iterations; i++){
            Vector pos = GeometryUtil.getPointOnSphere(explosionRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(7, explosionRadius), fire, destroyBlocks);
        }
        for (int i = 0; i < iterations; i++){
            Vector pos = GeometryUtil.getPointInSphere(explosionRadius).add(location.toVector());
            if (world != null) world.createExplosion(new Location(world, pos.getX(), pos.getY(), pos.getZ()), (float)Math.min(7, explosionRadius), fire, destroyBlocks);
        }
    }

    // ----- COLOR RELATED BLOCK METHODS -----

    public static Material getColoredShulkerBox(NamedTextColor color) {
        return switch (color.toString()) {
            case "white" -> Material.WHITE_SHULKER_BOX;
            case "orange" -> Material.ORANGE_SHULKER_BOX;
            case "magenta" -> Material.MAGENTA_SHULKER_BOX;
            case "aqua" -> Material.LIGHT_BLUE_SHULKER_BOX;
            case "yellow" -> Material.YELLOW_SHULKER_BOX;
            case "lime" -> Material.LIME_SHULKER_BOX;
            case "pink" -> Material.PINK_SHULKER_BOX;
            case "gray" -> Material.GRAY_SHULKER_BOX;
            case "light_gray" -> Material.LIGHT_GRAY_SHULKER_BOX;
            case "cyan" -> Material.CYAN_SHULKER_BOX;
            case "purple" -> Material.PURPLE_SHULKER_BOX;
            case "blue" -> Material.BLUE_SHULKER_BOX;
            case "brown" -> Material.BROWN_SHULKER_BOX;
            case "green" -> Material.GREEN_SHULKER_BOX;
            case "red" -> Material.RED_SHULKER_BOX;
            case "black" -> Material.BLACK_SHULKER_BOX;
            default -> Material.SHULKER_BOX;
        };
    }

    public static Color getBlockColor(Material material) {
        return switch (material) {
            case WHITE_WOOL, WHITE_CONCRETE, WHITE_STAINED_GLASS_PANE, WHITE_STAINED_GLASS, WHITE_TERRACOTTA -> Color.WHITE;
            case ORANGE_WOOL, ORANGE_CONCRETE, ORANGE_STAINED_GLASS_PANE, ORANGE_STAINED_GLASS, ORANGE_TERRACOTTA -> Color.ORANGE;
            case MAGENTA_WOOL, MAGENTA_CONCRETE, MAGENTA_STAINED_GLASS_PANE, MAGENTA_STAINED_GLASS, MAGENTA_TERRACOTTA -> Color.fromBGR(151, 21, 173);
            case LIGHT_BLUE_WOOL, LIGHT_BLUE_CONCRETE, LIGHT_BLUE_STAINED_GLASS_PANE, LIGHT_BLUE_STAINED_GLASS, LIGHT_BLUE_TERRACOTTA -> Color.AQUA;
            case YELLOW_WOOL, YELLOW_CONCRETE, YELLOW_STAINED_GLASS_PANE, YELLOW_STAINED_GLASS, YELLOW_TERRACOTTA -> Color.YELLOW;
            case LIME_WOOL, LIME_CONCRETE, LIME_STAINED_GLASS_PANE, LIME_STAINED_GLASS, LIME_TERRACOTTA -> Color.LIME;
            case PINK_WOOL, PINK_CONCRETE, PINK_STAINED_GLASS_PANE, PINK_STAINED_GLASS, PINK_TERRACOTTA -> Color.fromBGR(248, 3, 252);
            case GRAY_WOOL, GRAY_CONCRETE, GRAY_STAINED_GLASS_PANE, GRAY_STAINED_GLASS, GRAY_TERRACOTTA -> Color.fromBGR(46, 46, 46);
            case LIGHT_GRAY_WOOL, LIGHT_GRAY_CONCRETE, LIGHT_GRAY_STAINED_GLASS_PANE, LIGHT_GRAY_STAINED_GLASS, LIGHT_GRAY_TERRACOTTA -> Color.fromBGR(128, 128, 128);
            case CYAN_WOOL, CYAN_CONCRETE, CYAN_STAINED_GLASS_PANE, CYAN_STAINED_GLASS, CYAN_TERRACOTTA -> Color.fromBGR(16, 146, 163);
            case PURPLE_WOOL, PURPLE_CONCRETE, PURPLE_STAINED_GLASS_PANE, PURPLE_STAINED_GLASS, PURPLE_TERRACOTTA -> Color.PURPLE;
            case BLUE_WOOL, BLUE_CONCRETE, BLUE_STAINED_GLASS_PANE, BLUE_STAINED_GLASS, BLUE_TERRACOTTA -> Color.BLUE;
            case BROWN_WOOL, BROWN_CONCRETE, BROWN_STAINED_GLASS_PANE, BROWN_STAINED_GLASS, BROWN_TERRACOTTA -> Color.fromBGR(99, 57, 25);
            case GREEN_WOOL, GREEN_CONCRETE, GREEN_STAINED_GLASS_PANE, GREEN_STAINED_GLASS, GREEN_TERRACOTTA -> Color.GREEN;
            case RED_WOOL, RED_CONCRETE, RED_STAINED_GLASS_PANE, RED_STAINED_GLASS, RED_TERRACOTTA -> Color.RED;
            case BLACK_WOOL, BLACK_CONCRETE, BLACK_STAINED_GLASS_PANE, BLACK_STAINED_GLASS, BLACK_TERRACOTTA -> Color.BLACK;
            default -> Color.fromBGR(10, 10,10);
        };
    }

}
