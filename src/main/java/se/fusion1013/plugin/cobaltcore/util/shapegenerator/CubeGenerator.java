package se.fusion1013.plugin.cobaltcore.util.shapegenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.world.block.BlockPlacementManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CubeGenerator implements IShapeGenerator {

    // ----- VARIABLES -----

    private final int width;
    private final int height;
    private final int depth;
    private final Material[] placeMaterials;

    List<Material> replaceWhitelist = new ArrayList<>();
    List<Material> replaceBlacklist = new ArrayList<>();

    private Vector offset;

    private BlockData[] setBlockData;

    // ----- CONSTRUCTORS -----

    public CubeGenerator(int width, int height, int depth, Material... placeMaterials) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.placeMaterials = placeMaterials;
    }

    // ----- BUILDER METHODS -----

    public CubeGenerator setBlockData(IBlockDataGenerator... blockDataGenerator) {
        setBlockData = new BlockData[blockDataGenerator.length];
        for (int i = 0; i < blockDataGenerator.length; i++) {
            setBlockData[i] = blockDataGenerator[i].createData();
        }
        return this;
    }

    public CubeGenerator setBlockData(BlockData... blockData) {
        this.setBlockData = blockData;
        return this;
    }

    public CubeGenerator setOffset(Vector offset) {
        this.offset = offset;
        return this;
    }

    public CubeGenerator whitelistReplaceMaterials(Material... materials) {
        replaceWhitelist.addAll(Arrays.stream(materials).toList());
        return this;
    }

    public CubeGenerator blacklistReplaceMaterials(Material... materials) {
        replaceBlacklist.addAll(Arrays.stream(materials).toList());
        return this;
    }

    // ----- EXECUTE -----

    @Override
    public void place(Location location) {
        Random r = new Random();

        if (placeMaterials.length == 0) return;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Location placeLocation = location.clone().add(new Vector(x + offset.getX(), y + offset.getY(), z + offset.getZ()));
                    int materialId = r.nextInt(0, placeMaterials.length);
                    Material placeMaterial = placeMaterials[materialId];

                    // If it's in the blacklist, cancel the operation
                    if (replaceBlacklist.contains(placeLocation.getBlock().getType())) continue;

                    // If it's in the whitelist or the whitelist is empty, place the block
                    if (replaceWhitelist.contains(placeLocation.getBlock().getType()) || replaceWhitelist.isEmpty()) {
                        if (setBlockData != null) {
                            if (setBlockData.length > materialId) BlockPlacementManager.addBlock(placeMaterial, setBlockData[materialId], placeLocation);
                            else BlockPlacementManager.addBlock(placeMaterial, placeLocation);
                        }
                        else BlockPlacementManager.addBlock(placeMaterial, placeLocation);
                    }
                }
            }
        }
    }

    // ----- INTERFACE -----

    public interface IBlockDataGenerator {
        BlockData createData();
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public CubeGenerator(CubeGenerator target) {
        super();
        this.width = target.width;
        this.height = target.height;
        this.depth = target.depth;
        this.placeMaterials = target.placeMaterials;
        this.offset = target.offset;
        this.replaceBlacklist = target.replaceBlacklist;
        this.replaceWhitelist = target.replaceWhitelist;
        this.setBlockData = target.setBlockData;
    }

    public CubeGenerator clone() {
        return new CubeGenerator(this);
    }

}
