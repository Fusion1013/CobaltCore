package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contains various methods for handling items.
 */
public class ItemUtil {

    // ----- ENCHANTMENTS -----

    /**
     * Applies a random weighted <code>Enchantment</code> to an <code>ItemStack</code>.
     *
     * @param stack the <code>ItemStack</code> to apply the <code>Enchantment</code> to.
     * @param tier the relative tier of the enchantment. (Between 0 & 7).
     * @return the <code>ItemStack</code> with the <code>Enchantment</code> applied.
     */
    public static ItemStack addWeightedEnchantment(ItemStack stack, int tier) {
        return CobaltEnchantment.addWeightedEnchantment(stack, tier);
    }

    /**
     * Adds an <code>Enchantment</code> to an <code>ItemStack</code>.
     *
     * @param item the <code>ItemStack</code> to add the <code>Enchantment</code> to.
     * @param enchantment the <code>Enchantment</code> to add to the <code>ItemStack</code>.
     * @param level the level of the <code>Enchantment</code>.
     * @return an <code>ItemStack</code> with the <code>Enchantment</code> applied to it.
     */
    public static ItemStack addEnchant(ItemStack item, Enchantment enchantment, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return item;
    }

    private enum CobaltEnchantment {

        // -- VALUES

        MENDING(5, 1, Enchantment.MENDING, false),
        EFFICIENCY(5, 5, Enchantment.DIG_SPEED),
        SHARPNESS(5, 5, Enchantment.DAMAGE_ALL),
        FORTUNE(5, 3, Enchantment.LOOT_BONUS_BLOCKS),
        UNBREAKING(5, 3, Enchantment.DURABILITY),
        LOOTING(5, 3, Enchantment.LOOT_BONUS_MOBS),
        FEATHER_FALLING(5, 4, Enchantment.PROTECTION_FALL),
        POWER(5, 5, Enchantment.ARROW_DAMAGE),
        INFINITY(5, 1, Enchantment.ARROW_INFINITE, false), // TODO: Higher levels of infinity allow for infinite tipped arrows (level 2: Spectral Arrows, level 3: All Tipped) (??)
        RESPIRATION(5, 3, Enchantment.OXYGEN),

        SWIFT_SNEAK(4, 3, Enchantment.SWIFT_SNEAK),
        DEPTH_STRIDER(4, 3, Enchantment.DEPTH_STRIDER),
        FIRE_ASPECT(4, 2, Enchantment.FIRE_ASPECT),
        SILK_TOUCH(4, 1, Enchantment.SILK_TOUCH, false),
        LOYALTY(4, 3, Enchantment.LOYALTY),
        FLAME(4, 1, Enchantment.ARROW_FIRE),
        RIPTIDE(4, 3, Enchantment.RIPTIDE),

        QUICK_CHARGE(3, 3, Enchantment.QUICK_CHARGE),
        AQUA_AFFINITY(3, 1, Enchantment.WATER_WORKER, false),
        SWEEPING_EDGE(3, 3, Enchantment.SWEEPING_EDGE),
        MULTISHOT(3, 1, Enchantment.MULTISHOT),
        LUCK_OF_THE_SEA(3, 3, Enchantment.LUCK),
        LURE(3, 3, Enchantment.LURE),
        CHANNELING(3, 1, Enchantment.CHANNELING, false),
        SMITE(3, 5, Enchantment.DAMAGE_UNDEAD),
        THORNS(3, 3, Enchantment.THORNS),
        SOUL_SPEED(3, 3, Enchantment.SOUL_SPEED),
        PIERCING(3, 4, Enchantment.PIERCING),
        KNOCKBACK(3, 2, Enchantment.KNOCKBACK),
        FIRE_PROTECTION(3, 4, Enchantment.PROTECTION_FIRE),
        PROJECTILE_PROTECTION(3, 4, Enchantment.PROTECTION_PROJECTILE),

        IMPALING(2, 5, Enchantment.IMPALING),
        BLAST_PROTECTION(2, 4, Enchantment.PROTECTION_EXPLOSIONS),

        FROST_WALKER(1, 2, Enchantment.FROST_WALKER),

        BANE_OF_ARTHROPODS(0, 5, Enchantment.DAMAGE_ARTHROPODS),
        CURSE_OF_BINDING(0, 1, Enchantment.BINDING_CURSE, false),
        CURSE_OF_VANISHING(0, 1, Enchantment.VANISHING_CURSE, false);

        // -- CONSTRUCTOR

        CobaltEnchantment(int tier, int maxLevel, Enchantment enchantment) {
            this.tier = tier;
            this.maxLevel = maxLevel;
            this.enchantment = enchantment;
        }

        CobaltEnchantment(int tier, int maxLevel, Enchantment enchantment, boolean canOverrideLevel) {
            this(tier, maxLevel, enchantment);
            this.canOverrideLevel = canOverrideLevel;
        }

        // -- VARIABLES

        private final int tier;
        private final int maxLevel;
        private final Enchantment enchantment;
        private boolean canOverrideLevel = true; // Weather the level for this enchantment is allowed to get a higher level than the max

        // -- METHODS

        /**
         * Gets a random <code>CobaltEnchantment</code> around the given tier.
         *
         * @param tier the tier that the <code>CobaltEnchantment</code> will exist around. (The actual tier of the enchantment will be between [tier-2, tier])
         * @return a <code>CobaltEnchantment</code>
         */
        private static CobaltEnchantment getRandomCobaltEnchantment(int tier) {
            List<CobaltEnchantment> validEnchantments = new ArrayList<>();
            for (CobaltEnchantment enchantment : values()) if (enchantment.tier <= tier && enchantment.tier >= tier-2) validEnchantments.add(enchantment);
            Random r = new Random();
            return validEnchantments.get(r.nextInt(validEnchantments.size()));
        }

        /**
         * Applies a random weighted <code>Enchantment</code> to an <code>ItemStack</code>.
         *
         * @param stack the <code>ItemStack</code> to apply the <code>Enchantment</code> to.
         * @param tier the relative tier of the enchantment. (Between 0 & 7).
         * @return the <code>ItemStack</code> with the <code>Enchantment</code> applied.
         */
        public static ItemStack addWeightedEnchantment(ItemStack stack, int tier) {
            tier = Math.min(7, Math.max(0, tier)); // Clamp value between 0 & 7

            CobaltEnchantment ench = getRandomCobaltEnchantment(tier);
            int levelDiff = 3 + ench.tier - tier; // Value between 1-3
            int actualMaxLevel = ench.maxLevel / levelDiff; // TODO: Tweak value difference

            Random r = new Random();
            int level = r.nextInt(actualMaxLevel) + 1;

            ItemMeta meta = stack.getItemMeta();
            meta.addEnchant(ench.enchantment, level, true);
            stack.setItemMeta(meta);

            return stack;
        }

    }

    // ----- CONTAINERS -----

    /**
     * Gives a number of chests filled with the provided items to the player.
     * The number of chests given depends on the number of items.
     *
     * @param p the player to give the items to.
     * @param items the items to populate the chest with.
     * @param name the name of the chest. Will ignore if set to null.
     */
    public static void giveChest(Player p, ItemStack[] items, String name) {
        ItemStack chestItem = new ItemStack(Material.CHEST, 1);
        BlockStateMeta bsm = (BlockStateMeta)chestItem.getItemMeta();
        if (bsm == null) return; // This should never happen

        ItemStack[] truncatedItems = new ItemStack[27];
        for (int i = 0; i < items.length; i++) {
            if (i % 27 == 0) {
                // If the list of items is full, give a box to the player and reset the list.
                giveBox(p, truncatedItems, chestItem, (Chest)bsm.getBlockState(), name);

                chestItem = new ItemStack(Material.CHEST, 1);
                bsm = (BlockStateMeta)chestItem.getItemMeta();
                if (bsm == null) return; // This should never happen
                truncatedItems = new ItemStack[27];
            }
            truncatedItems[i % 27] = items[i];
        }

        giveBox(p, truncatedItems, chestItem, (Chest)bsm.getBlockState(), name);
    }

    /**
     * Gives a number of shulker boxes filled with the provided items to the player.
     * The number of shulker boxes given depends on the number of items.
     *
     * @param p the player to give the items to.
     * @param items the items to populate the box with.
     * @param name the name of the shulker box. Will ignore if set to null.
     */
    public static void giveShulkerBox(Player p, ItemStack[] items, String name) {
        giveShulkerBox(p, items, Material.SHULKER_BOX, name);
    }

    /**
     * Gives a number of shulker boxes filled with the provided items to the player.
     *
     * @param p the player to give the box to.
     * @param items the items to populate the box with.
     * @param shulkerMaterial the material of the shulker box.
     * @param name the name of the shulker box. Will ignore if set to null.
     */
    public static void giveShulkerBox(Player p, ItemStack[] items, Material shulkerMaterial, String name) {

        ItemStack shulkerItem = new ItemStack(shulkerMaterial, 1);
        BlockStateMeta bsm = (BlockStateMeta)shulkerItem.getItemMeta();
        if (bsm == null) return; // This should never happen

        ItemStack[] truncatedItems = new ItemStack[27];
        for (int i = 0; i < items.length; i++) {
            if (i % 28 == 27) {
                // If the list of items is full, give a box to the player and reset the list.
                giveBox(p, truncatedItems, shulkerItem, (ShulkerBox)bsm.getBlockState(), name);

                shulkerItem = new ItemStack(shulkerMaterial, 1);
                bsm = (BlockStateMeta)shulkerItem.getItemMeta();
                if (bsm == null) return; // This should never happen
                truncatedItems = new ItemStack[27];
            }
            truncatedItems[i % 27] = items[i];
        }

        giveBox(p, truncatedItems, shulkerItem, (ShulkerBox)bsm.getBlockState(), name);
    }

    /**
     * Gives a box filled with the provided items to the player.
     *
     * @param p the player to give the items to.
     * @param items the items to populate the box with.
     * @param box the box <code>ItemStack</code> to give to the player.
     * @param container the container to populate with the items.
     * @param name the name of the box. Will ignore if set to null.
     * @param <T> the type of the container.
     * @return the filled container.
     */
    private static <T extends Container> T giveBox(Player p, ItemStack[] items, ItemStack box, T container, String name) {
        BlockStateMeta blockStateMeta = (BlockStateMeta)box.getItemMeta();
        if (blockStateMeta == null) return null;

        Inventory inventory = container.getInventory();
        insertItems(inventory, items);

        if (name != null) blockStateMeta.setDisplayName(name);
        blockStateMeta.setBlockState(container);
        box.setItemMeta(blockStateMeta);

        p.getInventory().addItem(box);

        return container;
    }

    /**
     * Inserts an array of items into an inventory.
     *
     * @param inventory the inventory to insert the items into.
     * @param items the items to insert into the inventory.
     * @return the inventory.
     */
    public static Inventory insertItems(Inventory inventory, ItemStack[] items) {
        for (ItemStack stack : items) {
            if (stack != null) inventory.addItem(stack);
        }
        return inventory;
    }
}
