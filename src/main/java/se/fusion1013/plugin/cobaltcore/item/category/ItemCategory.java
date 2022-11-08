package se.fusion1013.plugin.cobaltcore.item.category;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;
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

    // -- COLOR
    NamedTextColor color;
    String colorFormatString;

    ItemCategory(String internalName, String name, String description, NamedTextColor color) {
        this.internalName = internalName;
        this.name = name;
        this.description = description;
        this.color = color;
    }

    ItemCategory(String internalName, String name, String description, String colorFormatString) {
        this.internalName = internalName;
        this.name = name;
        this.description = description;
        this.colorFormatString = colorFormatString;
    }

    @Override
    public Component getFormattedName() {
        if (color != null) return Component.text(name).color(color).decoration(TextDecoration.ITALIC, false);
        else return Component.text(HexUtils.colorify(colorFormatString + name)).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(CobaltCore.getInstance(), "item_category." + internalName);
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
        return BlockUtil.getColoredShulkerBox(color);
    }
}
