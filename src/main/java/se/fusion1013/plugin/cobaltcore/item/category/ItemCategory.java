package se.fusion1013.plugin.cobaltcore.item.category;

import org.bukkit.Material;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

public enum ItemCategory implements IItemCategory {

    NONE("none", "None", "If this shows up on an item, something has gone wrong", NamedTextColor.WHITE),

    WEAPON("weapon", "Weapon", "Weapon items", NamedTextColor.DARK_GRAY),
    ARMOR("armor", "Armor", "Armor items", NamedTextColor.GOLD),

    TESTING("testing", "Testing","Items used only for testing", NamedTextColor.WHITE),
    OTHER("other", "Other", "Other items", "&f");

    final String internalName;
    final String name;
    final String description;
    final Material boxMaterial;

    ItemCategory(String internalName, String name, String description, Material boxMaterial) {
        this.internalName = internalName;
        this.name = name;
        this.description = description;
        this.boxMaterial = boxMaterial;
    }

    @Override
    public String getName() {
        return HexUtils.colorify(name);
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Material getBoxMaterial() {
        return boxMaterial;
    }


}
