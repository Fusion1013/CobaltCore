package se.fusion1013.plugin.cobaltcore.item.enchantment;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.CobaltCore;

public enum CobaltEnchantment implements ICobaltEnchantment {

    // Passive
    LIFESTEAL("lifesteal", Component.text("Lifesteal"), Component.text("When the holder kills a mob, heal immediately"), 2),

    // Sword Enchants
    POISON("poison", Component.text("Poison"), Component.text("Applies poison to hit entity"), 3),
    WITHER("wither", Component.text("Wither"), Component.text("Applies wither to hit entity"), 5);

    // TODO: Set what items the enchant can be applied to

    // ----- VARIABLES -----

    final String internalName;
    final Component name;
    final Component description;
    final int maxLevel;

    EnchantmentTarget[] enchantmentTargets = {EnchantmentTarget.ANY};

    // ----- CONSTRUCTORS -----

    CobaltEnchantment(String internalName, Component name, Component description, int maxLevel) {
        this.internalName = internalName;
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
    }

    CobaltEnchantment(String internalName, Component name, Component description, int maxLevel, EnchantmentTarget... enchantmentTargets) {
        this.internalName = internalName;
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
        this.enchantmentTargets = enchantmentTargets;
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Component getDescription() {
        return description;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(CobaltCore.getInstance(), "enchantment." + internalName);
    }

    @Override
    public boolean hasEnchantment(ItemStack stack) {
        if (stack == null) return false;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(getNamespacedKey());
    }

    @Override
    public int getLevel(ItemStack stack) {
        if (!hasEnchantment(stack)) return 0;
        else return stack.getItemMeta().getPersistentDataContainer().get(getNamespacedKey(), PersistentDataType.INTEGER);
    }

    @Override
    public EnchantmentTarget[] getEnchantmentTargets() {
        return enchantmentTargets;
    }

}
