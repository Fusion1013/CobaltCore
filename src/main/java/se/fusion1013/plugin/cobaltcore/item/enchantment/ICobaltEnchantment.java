package se.fusion1013.plugin.cobaltcore.item.enchantment;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface ICobaltEnchantment {

    String getInternalName();

    Component getName();

    Component getDescription();

    int getMaxLevel();

    NamespacedKey getNamespacedKey();

    boolean hasEnchantment(ItemStack stack);

    int getLevel(ItemStack stack);

    EnchantmentTarget[] getEnchantmentTargets();

    default boolean isCurse() { return false; }

    static String intToRoman(int num)
    {
        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] romanLetters = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder roman = new StringBuilder();
        for(int i=0;i<values.length;i++)
        {
            while(num >= values[i])
            {
                num = num - values[i];
                roman.append(romanLetters[i]);
            }
        }

        return roman.toString();
    }

    enum EnchantmentTarget {
        // Armor
        HELMET(Material.TURTLE_HELMET, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.GOLDEN_HELMET, Material.NETHERITE_HELMET),
        CHESTPLATE(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.NETHERITE_CHESTPLATE),
        LEGGINGS(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.NETHERITE_LEGGINGS),
        BOOTS(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.GOLDEN_BOOTS, Material.NETHERITE_BOOTS),

        ELYTRA(Material.ELYTRA),

        // Tools
        PICKAXE(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE),
        AXE(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE),
        SHOVEL(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL),
        HOE(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE),
        SHEARS(Material.SHEARS),
        FLINT_AND_STEAL(Material.FLINT_AND_STEEL),
        FISHING_ROD(Material.FISHING_ROD),

        // Weapons
        SWORD(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.NETHERITE_SWORD, Material.DIAMOND_SWORD),
        BOW(Material.BOW),
        CROSSBOW(Material.CROSSBOW),
        TRIDENT(Material.TRIDENT),

        // Misc
        SHIELD(Material.SHIELD),

        ANY;

        final Material[] applicableItems;

        EnchantmentTarget(Material... applicableItems) {
            this.applicableItems = applicableItems;
        }

        public boolean isOfTarget(ItemStack item) {
            if (applicableItems == null) return true;
            if (applicableItems.length == 0) return true;

            for (Material material : applicableItems) {
                if (item.getType() == material) return true;
            }

            return false;
        }
    }

}
