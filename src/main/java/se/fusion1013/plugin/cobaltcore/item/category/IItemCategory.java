package se.fusion1013.plugin.cobaltcore.item.category;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public interface IItemCategory {

    /**
     * Get the formatted name of this <code>IItemCategory</code>.
     *
     * @return the formatted name <code>Component</code>.
     */
    Component getFormattedName();

    /**
     * Gets the <code>NamespacedKey</code> of this <code>IItemCategory</code>.
     *
     * @return a <code>NamespacedKey</code>.
     */
    NamespacedKey getNamespacedKey();

    /**
     * The internal name of the <code>IItemCategory</code>. Should be used when referring to it in code.
     *
     * @return the internal name.
     */
    String getInternalName();

    /**
     * The description of the <code>IItemCategory</code>.
     *
     * @return the description.
     */
    String getDescription();

    /**
     * The material of the box for the <code>IItemCategory</code>.
     *
     * @return the material of the box.
     */
    Material getBoxMaterial();

}
