package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public interface ICustomItem {
    NamespacedKey getNamespacedKey();
    String getInternalName();
    ItemStack getItemStack();
    String[] getTags();
}
