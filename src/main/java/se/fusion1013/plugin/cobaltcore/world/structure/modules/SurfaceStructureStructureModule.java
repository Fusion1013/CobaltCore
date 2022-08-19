package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;
import se.fusion1013.plugin.cobaltcore.util.GeometryUtil;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

public class SurfaceStructureStructureModule extends StructureModule implements IStructureModule {

    StructureUtil.StructureHolder structureHolder;
    Vector offset;

    static Material[] removeMaterials = new Material[] {Material.OAK_LEAVES, Material.OAK_LOG, Material.BIRCH_LEAVES, Material.BIRCH_LOG, Material.DARK_OAK_LEAVES, Material.DARK_OAK_LOG, Material.ACACIA_LEAVES, Material.ACACIA_LOG, Material.JUNGLE_LEAVES, Material.JUNGLE_LOG, Material.SPRUCE_LEAVES, Material.SPRUCE_LOG, Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES};

    public SurfaceStructureStructureModule(Plugin plugin, String structure, Vector offset) {
        this.structureHolder = StructureUtil.preLoadStructure(plugin, structure);
        this.offset = offset;
    }

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        executeWithSeed(location, holder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        Location clearLocation = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ()).getLocation();

        for (Material remove : removeMaterials) BlockUtil.replaceBlocksInSphere(clearLocation.clone().add(new Vector(structureHolder.width/2, structureHolder.height/2, structureHolder.depth/2)), remove, Material.AIR, Math.max(structureHolder.width, Math.max(structureHolder.height, structureHolder.depth)));

        Location placeLocation = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ()).getLocation();
        structureHolder.placeStructure(placeLocation.clone().add(offset));

        CobaltCore.getInstance().getLogger().info("Placed surface structure at: " + location.toVector());
    }

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
