package se.fusion1013.plugin.cobaltcore.item.system;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;

/**
 * Used to create new item rarities.
 */
public interface IItemRarity {

    /**
     * Gets the formatted rarity.
     *
     * @return the formatted rarity.
     */
    Component getFormattedRarity();

    /**
     * Gets the <code>NamespacedKey</code> for this <code>IItemRarity</code>.
     * This should be unique for each <code>IItemRarity</code>.
     *
     * @return a <code>NamespacedKey</code>.
     */
    NamespacedKey getNamespacedKey();

    String getInternalName();

}
