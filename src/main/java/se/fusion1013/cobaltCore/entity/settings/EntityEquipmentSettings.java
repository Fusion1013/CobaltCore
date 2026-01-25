package se.fusion1013.cobaltCore.entity.settings;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.cobaltCore.item.CustomItemManager;

public class EntityEquipmentSettings implements IEntitySettings {

    protected String helmet;
    protected String chestplate;
    protected String leggings;
    protected String boots;
    protected String mainHand;
    protected String offHand;

    @Override
    public void load(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            load(livingEntity);
        }
    }

    private void load(LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        if (helmet != null && !helmet.isEmpty()) {
            ItemStack helmetItem = CustomItemManager.getItemStack(helmet);
            if (helmetItem != null) equipment.setHelmet(helmetItem);
        }

        if (chestplate != null && !chestplate.isEmpty()) {
            ItemStack chestplateItem = CustomItemManager.getItemStack(chestplate);
            if (chestplateItem != null) equipment.setChestplate(chestplateItem);
        }

        if (leggings != null && !leggings.isEmpty()) {
            ItemStack leggingsItem = CustomItemManager.getItemStack(leggings);
            if (leggingsItem != null) equipment.setLeggings(leggingsItem);
        }

        if (boots != null && !boots.isEmpty()) {
            ItemStack bootsItem = CustomItemManager.getItemStack(boots);
            if (bootsItem != null) equipment.setBoots(bootsItem);
        }

        if (mainHand != null && !mainHand.isEmpty()) {
            ItemStack mainHandItem = CustomItemManager.getItemStack(mainHand);
            if (mainHandItem != null) equipment.setItemInMainHand(mainHandItem);
        }

        if (offHand != null && !offHand.isEmpty()) {
            ItemStack offHandItem = CustomItemManager.getItemStack(offHand);
            if (offHandItem != null) equipment.setItemInOffHand(offHandItem);
        }
    }

    public static class Builder {

        private final EntityEquipmentSettings obj;

        public Builder() {
            obj = new EntityEquipmentSettings();
        }

        public Builder addHelmet(String helmet) {
            obj.helmet = helmet;
            return this;
        }

        public Builder addChestplate(String chestplate) {
            obj.chestplate = chestplate;
            return this;
        }

        public Builder addLeggings(String leggings) {
            obj.leggings = leggings;
            return this;
        }

        public Builder addBoots(String boots) {
            obj.boots = boots;
            return this;
        }

        public Builder addMainHand(String mainHand) {
            obj.mainHand = mainHand;
            return this;
        }

        public Builder addOffHand(String offHand) {
            obj.offHand = offHand;
            return this;
        }

        public EntityEquipmentSettings build() {
            return obj;
        }

    }
}
