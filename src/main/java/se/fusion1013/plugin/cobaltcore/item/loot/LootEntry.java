package se.fusion1013.plugin.cobaltcore.item.loot;

import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.util.ItemUtil;

import java.util.Random;

public class LootEntry { // TODO: Incorporate Loot Functions

    // ----- VARIABLES -----

    ItemStack stack;
    int countMin;
    int countMax;

    int enchantTier = -1;
    int enchantAttempts = 0;

    // ----- CONSTRUCTORS -----

    public LootEntry(ItemStack stack, int countMin, int countMax) {
        this.stack = stack;
        this.countMin = countMin;
        this.countMax = countMax;
    }

    // ----- BUILDER METHODS -----

    public LootEntry addEnchant(int enchantTier, int enchantAttempts) {
        this.enchantTier = enchantTier;
        this.enchantAttempts = enchantAttempts;
        return this;
    }

    // ----- GETTERS / SETTERS -----

    public ItemStack getStack(Random r) {
        // Set stack amount
        if (countMin == countMax || countMax < countMin) {
            stack.setAmount(countMin);
        } else {
            stack.setAmount(r.nextInt(countMin, countMax));
        }

        // Enchantment
        if (enchantTier <= -1) return stack.clone();
        else {
            for (int i = 0; i < enchantAttempts; i++) ItemUtil.addWeightedEnchantment(stack.clone(), enchantTier);
            return stack;
        }
    }

    public ItemStack stack() {
        return this.stack;
    }

    public int countMin() {
        return this.countMin;
    }

    public int countMax() {
        return this.countMax;
    }
}
