package se.fusion1013.plugin.cobaltcore.world.block.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.UUID;

public class BlockEntityCollection {

    // ----- VARIABLES -----

    Location location;
    BlockEntity[][][] blockEntities;

    boolean isValid = true;

    UUID uuid;

    // ----- CONSTRUCTORS -----

    public BlockEntityCollection(Location location, int width, int height, int depth) {
        this.location = location;
        this.blockEntities = new BlockEntity[width][height][depth];
        this.uuid = UUID.randomUUID();
        createBlockEntities(width, height, depth);
    }

    public BlockEntityCollection(Location location, Material[][][] materials) {
        this.location = location;
        this.blockEntities = new BlockEntity[materials.length][materials[0].length][materials[0][0].length];
        this.uuid = UUID.randomUUID();
        createBlockEntities(location, materials);
    }

    // ----- MOVEMENT METHODS -----

    public void moveTo(Location location) {
        for (int x = 0; x < blockEntities.length; x++) {
            BlockEntity[][] blockEntities1 = blockEntities[x];
            for (int y = 0; y < blockEntities1.length; y++) {
                BlockEntity[] blockEntities2 = blockEntities1[y];
                for (int z = 0; z < blockEntities2.length; z++) {
                    BlockEntity blockEntity = blockEntities2[z];
                    Location currentLocation = location.clone().add(new Vector(x, y, z));
                    blockEntity.moveTo(currentLocation);
                }
            }
        }
    }

    // ----- CREATION METHODS -----

    private void createBlockEntities(int width, int height, int depth) {

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Location currentLocation = location.clone().add(new Vector(x, y, z));

                    blockEntities[x][y][z] = new BlockEntity(currentLocation.getBlock());
                    currentLocation.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    private void createBlockEntities(Location location, Material[][][] materials) {
        for (int x = 0; x < materials.length; x++) {
            Material[][] materials1 = materials[x];
            for (int y = 0; y < materials1.length; y++) {
                Material[] materials2 = materials1[y];
                for (int z = 0; z < materials2.length; z++) {
                    Material material = materials2[z];
                    Location currentLocation = location.clone().add(new Vector(x, y, z));

                    blockEntities[x][y][z] = new BlockEntity(currentLocation, material);
                }
            }
        }
    }

    // ----- REMOVAL METHODS -----

    public void removeBlockEntities() {
        for (BlockEntity[][] entityDim2 : blockEntities) {
            for (BlockEntity[] entityDim1 : entityDim2) {
                for (BlockEntity entity : entityDim1) {
                    entity.removeFallingBlock();
                }
            }
        }

        isValid = false;
    }

    // ----- UTIL METHODS -----

    public void resetTicks() {
        for (BlockEntity[][] x : blockEntities) {
            for (BlockEntity[] y : x) {
                for (BlockEntity entity : y) {
                    entity.keepAlive();
                }
            }
        }
    }

    // ----- GETTERS / SETTERS -----

    public boolean isValid() {
        return isValid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
