package se.fusion1013.cobaltCore.item.properties;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.util.HexUtils;

import java.util.ArrayList;
import java.util.List;

public class ItemCreationContext {

    public ItemStack itemStack;
    public ItemMeta itemMeta;
    public PersistentDataContainer persistent;
    public final List<String> lore = new ArrayList<>();
    public final NamespacedKey key;

    public ItemCreationContext(NamespacedKey key, Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
        persistent = itemMeta.getPersistentDataContainer();
        this.key = key;
    }

    public ItemStack finalizeItem() {
        persistent.set(key, PersistentDataType.INTEGER, 1);

        itemStack.setItemMeta(itemMeta);

        lore.replaceAll(HexUtils::colorify);

        if (itemStack.getLore() != null) {
            List<String> mergedLore = new ArrayList<>(itemStack.getLore());
            mergedLore.addAll(lore);
            itemStack.setLore(mergedLore);
        } else {
            itemStack.setLore(lore);
        }

        return itemStack;
    }

}
