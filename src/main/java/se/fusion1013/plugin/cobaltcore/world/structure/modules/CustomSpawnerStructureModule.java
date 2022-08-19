package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;
import se.fusion1013.plugin.cobaltcore.world.spawner.CustomSpawner;
import se.fusion1013.plugin.cobaltcore.world.spawner.SpawnerManager;

public class CustomSpawnerStructureModule extends StructureModule implements IStructureModule {

    // ----- VARIABLES -----

    CustomSpawner spawner;
    Material replaceMaterial;

    // ----- CONSTRUCTORS -----

    public CustomSpawnerStructureModule(CustomSpawner spawnerTemplate, Material replaceMaterial) {
        this.spawner = spawnerTemplate;
        this.replaceMaterial = replaceMaterial;
    }

    // ----- SPAWNER PLACING -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        executeWithSeed(location, holder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location replaceLocation = location.clone().add(new Vector(x, y, z));
                    if (replaceLocation.getBlock().getType() == replaceMaterial) {

                        replaceLocation.getBlock().setType(Material.SPAWNER);
                        BlockData block = replaceLocation.getBlock().getBlockData();

                        if (block instanceof CreatureSpawner spawnerBlock) {
                            spawnerBlock.setSpawnedType(spawner.getEntity().getBaseEntityType());
                            spawnerBlock.setSpawnCount(0);
                            spawnerBlock.update(true);
                        }

                        SpawnerManager.getInstance().placeSpawner(spawner, replaceLocation);
                    }
                }
            }
        }
    }

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
