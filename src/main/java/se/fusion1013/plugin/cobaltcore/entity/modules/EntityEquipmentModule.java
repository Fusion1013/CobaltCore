package se.fusion1013.plugin.cobaltcore.entity.modules;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.entity.CustomEntity;

public class EntityEquipmentModule extends EntityModule implements ISpawnExecutable, Cloneable {

    // ----- VARIABLES -----

    final EquipmentSlot slot;
    final ItemStack item;

    float dropChance = 0;

    // ----- CONSTRUCTORS -----

    /**
     * Sets the item in the given <code>EquipmentSlot</code> with a drop chance of 0.
     *
     * @param slot the <code>EquipmentSlot</code> to set the item of.
     * @param item the <code>ItemStack</code> to set in the slot.
     */
    public EntityEquipmentModule(EquipmentSlot slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    /**
     * Sets the item in the given <code>EquipmentSlot</code> with a drop chance.
     *
     * @param slot the <code>EquipmentSlot</code> to set the item of.
     * @param item the <code>ItemStack</code> to set in the slot.
     * @param dropChance the drop chance of the item.
     */
    public EntityEquipmentModule(EquipmentSlot slot, ItemStack item, float dropChance) {
        this(slot, item);
        this.dropChance = dropChance;
    }

    // ----- EXECUTE -----

    @Override
    public void execute(CustomEntity customEntity) {
        Entity entity = customEntity.getSummonedEntity();

        if (entity instanceof LivingEntity living) {
            EntityEquipment eq = living.getEquipment();
            if (eq == null) return;

            eq.setItem(slot, item);
            eq.setDropChance(slot, dropChance);
        }
    }

    // ----- CLONE -----

    public EntityEquipmentModule(EntityEquipmentModule target) {
        this.slot = target.slot;
        this.item = target.item;
        this.dropChance = target.dropChance;
    }

    public EntityEquipmentModule clone() {
        return new EntityEquipmentModule(this);
    }

}
