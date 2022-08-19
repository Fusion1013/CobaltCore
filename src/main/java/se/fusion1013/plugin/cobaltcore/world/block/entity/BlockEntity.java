package se.fusion1013.plugin.cobaltcore.world.block.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class BlockEntity {

    // ----- VARIABLES -----

    FallingBlock fallingBlock;
    ArmorStand armorStand;

    private static final Vector ARMOR_STAND_OFFSET = new Vector(0, 0, 0);

    // ----- CONSTRUCTORS -----

    public BlockEntity(Block block) {
        createFallingBlock(block);
    }

    public BlockEntity(Location location, Material material) {
        createFallingBlock(location, material);
    }

    // ----- MOVEMENT METHODS -----

    public void moveTo(Location location) {
        armorStand.eject(); // First remove falling block
        armorStand.teleport(location.clone().add(new Vector(.5, 0, .5))); // Teleport armor stand
        armorStand.addPassenger(fallingBlock); // Add falling block back as passenger

    }

    // ----- CREATION METHODS -----

    private void createFallingBlock(Location location, Material material) {
        World world = location.getWorld();
        Location formattedLocation = location.clone().add(new Vector(.5, 0, .5));

        fallingBlock = world.spawnFallingBlock(formattedLocation, material.createBlockData());
        fallingBlock.setGravity(false);

        armorStand = world.spawn(formattedLocation.add(ARMOR_STAND_OFFSET), ArmorStand.class, stand -> {
            stand.setGravity(false);
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.addPassenger(fallingBlock);
        });
    }

    private void createFallingBlock(Block block) {
        World world = block.getWorld();
        Location formattedLocation = block.getLocation().clone().add(new Vector(.5, 0, .5));

        fallingBlock = world.spawnFallingBlock(formattedLocation, block.getBlockData().clone());
        fallingBlock.setGravity(false);

        armorStand = world.spawn(formattedLocation.add(ARMOR_STAND_OFFSET), ArmorStand.class, stand -> {
            stand.setGravity(false);
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.addPassenger(fallingBlock);
        });
    }

    // ----- REMOVAL METHODS -----

    public void removeFallingBlock() {
        fallingBlock.remove();
        armorStand.remove();
    }

    // ----- UTIL METHODS -----

    public void keepAlive() {
        if (fallingBlock != null) fallingBlock.setTicksLived(1);
    }

}
