package se.fusion1013.plugin.cobaltcore.item.category;

import org.bukkit.Material;

public interface IItemCategory {

    /**
     * Formatted name of the <code>IItemCategory</code>. Should only be used for user display purposes.
     *
     * @return the formatted name.
     */
    String getName();

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
