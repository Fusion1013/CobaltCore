package se.fusion1013.plugin.cobaltcore.item.enchantment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.plugin.cobaltcore.util.ItemUtil;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentWrapper {

    // ----- VARIABLES -----

    // -- Enchantment holder
    Enchantment enchantment;
    ICobaltEnchantment cobaltEnchantment;

    // -- Generic values
    int level;
    boolean ignoreLevelRestriction;

    // ----- CONSTRUCTORS -----

    public EnchantmentWrapper(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        this.enchantment = enchantment;
        this.level = level;
        this.ignoreLevelRestriction = ignoreLevelRestriction;
    }

    public EnchantmentWrapper(ICobaltEnchantment cobaltEnchantment, int level, boolean ignoreLevelRestriction) {
        this.cobaltEnchantment = cobaltEnchantment;
        this.level = level;
        this.ignoreLevelRestriction = ignoreLevelRestriction;
    }

    // ----- APPLYING -----

    public ItemStack add(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        if (enchantment != null) {
            meta.addEnchant(enchantment, level, ignoreLevelRestriction); // TODO: Check for correct tool
        } else {
            // TODO: If the item already has that enchant, attempt to merge them (increase level if same level)
            // TODO: Add enchant glint

            // If the tool already has the enchantment, do not add it
            if (meta.getPersistentDataContainer().has(cobaltEnchantment.getNamespacedKey())) return stack; // TODO: Check if higher level, if so replace

            // If it is of an incorrect tool type, do not add it
            boolean isTarget = false;
            for (ICobaltEnchantment.EnchantmentTarget target : cobaltEnchantment.getEnchantmentTargets()) if (target.isOfTarget(stack)) isTarget = true;
            if (stack.getType() == Material.ENCHANTED_BOOK) isTarget = true;
            if (!isTarget) return stack;

            // Calculate level cap
            int actualLevel = level;
            if (!ignoreLevelRestriction) actualLevel = Math.min(actualLevel, cobaltEnchantment.getMaxLevel());

            meta.getPersistentDataContainer().set(cobaltEnchantment.getNamespacedKey(), PersistentDataType.INTEGER, actualLevel);

            // Add lore
            List<Component> newLore = new ArrayList<>();
            TextColor color;
            if (cobaltEnchantment.isCurse()) color = NamedTextColor.RED;
            else color = NamedTextColor.GRAY;

            Component loreComponent = cobaltEnchantment.getName()
                    .color(color)
                    .decoration(TextDecoration.ITALIC, false);
            if (actualLevel > 1) loreComponent = loreComponent.append(Component.text(" " + ICobaltEnchantment.intToRoman(actualLevel))
                    .color(color)
                    .decoration(TextDecoration.ITALIC, false));

            newLore.add(loreComponent);
            if (meta.lore() != null) newLore.addAll(stack.lore());
            meta.lore(newLore);
        }

        stack.setItemMeta(meta);

        // Enchant Glow (Using nms)
        if (!meta.hasEnchants()) {
            stack = ItemUtil.addEnchantmentGlint(stack);
        }

        return stack;
    }

    // ----- GETTERS / SETTERS -----

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public ICobaltEnchantment getCobaltEnchantment() {
        return cobaltEnchantment;
    }

    public int getLevel() {
        return level;
    }

    public boolean isIgnoreLevelRestriction() {
        return ignoreLevelRestriction;
    }
}
