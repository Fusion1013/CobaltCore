package se.fusion1013.plugin.cobaltcore.item.loot;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class LootEntry { // TODO: Incorporate Loot Functions

    ItemStack stack;
    int countMin;
    int countMax;

    public LootEntry(ItemStack stack, int countMin, int countMax) {
        this.stack = stack;
        this.countMin = countMin;
        this.countMax = countMax;
    }

    public ItemStack getStack(Random r) {
        if (countMin == countMax || countMax < countMin) {
            stack.setAmount(countMin);
        } else {
            stack.setAmount(r.nextInt(countMin, countMax));
        }
        return stack.clone();
    }

    // ----- GETTERS / SETTERS -----

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
