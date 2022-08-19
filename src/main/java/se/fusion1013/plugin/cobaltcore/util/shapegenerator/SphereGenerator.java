package se.fusion1013.plugin.cobaltcore.util.shapegenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;
import se.fusion1013.plugin.cobaltcore.util.GeometryUtil;
import se.fusion1013.plugin.cobaltcore.world.block.BlockPlacementManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SphereGenerator implements IShapeGenerator {

    // ----- VARIABLES -----

    private double radius;
    private final Material[] placeMaterials;

    // Optional
    private boolean hollow = false;

    private List<Material> replaceWhitelist = new ArrayList<>();
    private List<Material> replaceBlacklist = new ArrayList<>();

    private Vector offset;

    private BlockData[] setBlockData;

    double expandOnTick = 0;

    // ----- CONSTRUCTORS -----

    public SphereGenerator(int radius, Material... materials) {
        this.radius = radius;
        this.placeMaterials = materials;
    }

    // ----- BUILDER METHODS -----

    public SphereGenerator setExpandOnTick(double blocksPerTick) {
        this.expandOnTick = blocksPerTick;
        return this;
    }

    public SphereGenerator setHollow(boolean hollow) {
        this.hollow = hollow;
        return this;
    }

    public SphereGenerator setBlockData(CubeGenerator.IBlockDataGenerator... blockDataGenerator) {
        setBlockData = new BlockData[blockDataGenerator.length];
        for (int i = 0; i < blockDataGenerator.length; i++) {
            setBlockData[i] = blockDataGenerator[i].createData();
        }
        return this;
    }

    public SphereGenerator setBlockData(BlockData... blockData) {
        this.setBlockData = blockData;
        return this;
    }

    public SphereGenerator setOffset(Vector offset) {
        this.offset = offset;
        return this;
    }

    public SphereGenerator whitelistReplaceMaterials(Material... materials) {
        replaceWhitelist.addAll(Arrays.stream(materials).toList());
        return this;
    }

    public SphereGenerator blacklistReplaceMaterials(Material... materials) {
        replaceBlacklist.addAll(Arrays.stream(materials).toList());
        return this;
    }

    // ----- EXECUTE -----

    @Override
    public void place(Location location) {
        List<Location> sphereLocations = BlockUtil.generateSphere(location, (int)radius, false);

        Random r = new Random();
        if (placeMaterials.length == 0) return;

        for (Location placeLocation : sphereLocations) {
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

        radius += expandOnTick;
    }

    // ----- CLONE CONSTRUCTOR & METHOD -----

    public SphereGenerator(SphereGenerator target) {
        super();
        this.radius = target.radius;
        this.expandOnTick = target.expandOnTick;
        this.placeMaterials = target.placeMaterials;
        this.offset = target.offset;
        this.hollow = target.hollow;
        this.replaceBlacklist = target.replaceBlacklist;
        this.replaceWhitelist = target.replaceWhitelist;
        this.setBlockData = target.setBlockData;
    }

    public SphereGenerator clone() {
        return new SphereGenerator(this);
    }
}
