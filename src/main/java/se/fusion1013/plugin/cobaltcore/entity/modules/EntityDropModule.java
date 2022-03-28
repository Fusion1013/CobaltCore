package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

import java.util.Random;

public class EntityDropModule extends EntityModule implements IDeathExecutable {

    // ----- VARIABLES -----

    int xp = 0;
    int xpSplit = 1;

    ItemStack itemDrop;
    double dropChance = 1;

    // ----- CONSTRUCTORS -----

    public EntityDropModule(int xp, int xpSplit, ItemStack item, double dropChance) {
        this.xp = xp;
        this.xpSplit = xpSplit;
        this.itemDrop = item;
        this.dropChance = dropChance;
    }

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
    public void execute(CustomEntity customEntity) {
        Location dropLocation = customEntity.getSummonedEntity().getLocation();
        World dropWorld = dropLocation.getWorld();

        if (dropWorld == null) return;

        // Create Drop Items
        if (itemDrop != null) {
            Random r = new Random();

            if (r.nextDouble() <= dropChance) {
                Item droppedItem = (Item) dropWorld.spawnEntity(dropLocation, EntityType.DROPPED_ITEM);
                droppedItem.setItemStack(itemDrop);
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
        if (target.itemDrop != null) this.itemDrop = target.itemDrop;
        this.dropChance = target.dropChance;
    }

    @Override
    public EntityDropModule clone() {
        return new EntityDropModule(this);
    }
}
