package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;
import se.fusion1013.plugin.cobaltcore.item.loot.LootPool;

import java.util.List;

public class EntityDropModule extends EntityModule implements IDeathExecutable {

    // ----- VARIABLES -----

    // -- EXPERIENCE
    private final int xp;
    private final int xpSplit;

    // -- ITEMS
    private final CustomLootTable lootTable;

    // -- EXTRA SETTINGS
    private boolean inContainer = false;
    private Material containerMaterial = Material.BARREL;

    // ----- CONSTRUCTORS -----

    public EntityDropModule(int xp, int xpSplit, LootPool... lootPools) {
        this.xp = xp;
        this.xpSplit = xpSplit;
        this.lootTable = new CustomLootTable(new CustomLootTable.LootTarget[] {
                CustomLootTable.LootTarget.DROP,
                CustomLootTable.LootTarget.CHEST,
                CustomLootTable.LootTarget.BARREL
        }, lootPools);
    }

    public EntityDropModule(LootPool... lootPools) {
        this.xp = 0;
        this.xpSplit = 1;
        this.lootTable = new CustomLootTable(new CustomLootTable.LootTarget[] {
                CustomLootTable.LootTarget.DROP,
                CustomLootTable.LootTarget.CHEST,
                CustomLootTable.LootTarget.BARREL
        }, lootPools);
    }

    public EntityDropModule(int xp, int xpSplit) {
        this.xp = xp;
        this.xpSplit = xpSplit;
        this.lootTable = new CustomLootTable(new CustomLootTable.LootTarget[] {
                CustomLootTable.LootTarget.DROP,
                CustomLootTable.LootTarget.CHEST,
                CustomLootTable.LootTarget.BARREL
        });
    }

    // ----- BUILDER METHODS -----

    /**
     * Sets the item drops to be placed inside a chest instead of as <code>Item</code> entities.
     *
     * @return the module.
     */
    public EntityDropModule setInContainer(Material containerMaterial) {
        this.inContainer = true;
        this.containerMaterial = containerMaterial;
        return this;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        Location dropLocation = customEntity.getSummonedEntity().getLocation();
        World dropWorld = dropLocation.getWorld();

        if (dropWorld == null) return;

        // Drop items
        if (lootTable != null) {
            if (inContainer) {
                // Place barrel & insert loot
                dropLocation.getBlock().setType(containerMaterial);
                lootTable.insertLoot(dropLocation);
            } else {
                // Drop items on ground
                List<ItemStack> items = lootTable.getLoot(Integer.MAX_VALUE);
                for (ItemStack item : items) dropWorld.dropItemNaturally(dropLocation, item);
            }
        }

        // Create XP Drops
        if (xp > 0) {
            int amountPerOrb = xp / xpSplit;
            for (int i = 0; i < xpSplit; i++) {
                ExperienceOrb xpOrb = (ExperienceOrb) dropWorld.spawnEntity(dropLocation, EntityType.EXPERIENCE_ORB);
                xpOrb.setExperience(amountPerOrb);
            }
        }
    }

    // ----- CLONE -----

    public EntityDropModule(EntityDropModule target) {
        this.xp = target.xp;
        this.xpSplit = target.xpSplit;
        this.lootTable = target.lootTable;
        this.inContainer = target.inContainer;
        this.containerMaterial = target.containerMaterial;
    }

    @Override
    public EntityDropModule clone() {
        return new EntityDropModule(this);
    }
}
