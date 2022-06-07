package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;
import se.fusion1013.plugin.cobaltcore.entity.ISpawnParameters;
import se.fusion1013.plugin.cobaltcore.item.loot.CustomLootTable;

import java.util.List;
import java.util.Random;

public class EntityDropModule extends EntityModule implements IDeathExecutable {

    // ----- VARIABLES -----

    int xp = 0;
    int xpSplit = 1;

    ItemStack itemDrop;
    double dropChance = 1;

    CustomLootTable lootTable;

    // ----- CONSTRUCTORS -----

    public EntityDropModule(int xp, int xpSplit, CustomLootTable lootTable) {
        this.xp = xp;
        this.xpSplit = xpSplit;
        this.lootTable = lootTable;
    }

    public EntityDropModule(CustomLootTable lootTable) {
        this.lootTable = lootTable;
    }

    @Deprecated
    public EntityDropModule(int xp, int xpSplit, ItemStack item, double dropChance) {
        this.xp = xp;
        this.xpSplit = xpSplit;
        this.itemDrop = item;
        this.dropChance = dropChance;
    }

    @Deprecated
    public EntityDropModule(ItemStack item, double dropChance) {
        this.itemDrop = item;
        this.dropChance = dropChance;
    }

    public EntityDropModule(int xp) {
        this.xp = xp;
    }

    public EntityDropModule(int xp, int xpSplit) {
        this.xp = xp;
        this.xpSplit = xpSplit;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity, ISpawnParameters spawnParameters) {
        Location dropLocation = customEntity.getSummonedEntity().getLocation();
        World dropWorld = dropLocation.getWorld();

        if (dropWorld == null) return;

        // Create Drop Items
        if (itemDrop != null) {
            Random r = new Random();

            if (r.nextDouble() <= dropChance) {
                dropWorld.dropItemNaturally(dropLocation, itemDrop);
            }
        }

        if (lootTable != null) {
            List<ItemStack> items = lootTable.getLoot(Integer.MAX_VALUE);
            for (ItemStack item : items) dropWorld.dropItemNaturally(dropLocation, item);
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
        if (target.itemDrop != null) this.itemDrop = target.itemDrop;
        this.dropChance = target.dropChance;
        if (target.lootTable != null) this.lootTable = target.lootTable;
    }

    @Override
    public EntityDropModule clone() {
        return new EntityDropModule(this);
    }
}
