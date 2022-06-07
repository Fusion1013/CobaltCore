package se.fusion1013.plugin.cobaltcore.world.structure.modules;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.util.StructureUtil;

public class LootStructureModule extends StructureModule implements IStructureModule {

    // ----- VARIABLES -----

    CustomLootTable lootTable;

    // ----- CONSTRUCTORS -----

    public LootStructureModule(CustomLootTable lootTable) {
        this.lootTable = lootTable;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(Location location, StructureUtil.StructureHolder holder) {
        executeWithSeed(location, holder, 0);
    }

    @Override
    public void executeWithSeed(Location location, StructureUtil.StructureHolder holder, long seed) {
        for (int x = 0; x < holder.width; x++) {
            for (int y = 0; y < holder.height; y++) {
                for (int z = 0; z < holder.depth; z++) {
                    Location lootLocation = location.clone().add(new Vector(x, y, z));
                    lootTable.insertLoot(lootLocation);
                }
            }
        }
    }

    // ----- TYPE -----

    @Override
    public StructureModuleType getModuleType() {
        return StructureModuleType.POST;
    }
}
