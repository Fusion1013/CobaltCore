package se.fusion1013.plugin.cobaltcore.item.system;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

public enum ItemRarity implements IItemRarity {

    // ----- VALUES -----

    /**
     * The 'NONE' item rarity should not be set manually, it is set automatically if there is no rarity.
     * The automatic process does not affect the look of the item. The manual process does.
     */
    NONE("none", "None", 0, NamedTextColor.WHITE),

    COMMON("common", "Common", 10, NamedTextColor.GRAY),
    UNCOMMON("uncommon", "Uncommon", 20, NamedTextColor.GREEN),
    RARE("rare", "Rare", 30, NamedTextColor.YELLOW),
    VERY_RARE("very_rare", "Very Rare", 40, NamedTextColor.AQUA),
    LEGENDARY("legendary", "Legendary", 50, NamedTextColor.DARK_AQUA),
    MYSTIC("mystic", "Mystic", 50, NamedTextColor.LIGHT_PURPLE),
    DIVINE("divine", "Divine", 50, NamedTextColor.GOLD);

    // ----- VARIABLES -----

    final String internalName;
    final String name;
    final double rarityWeight;

    // -- COLOR
    TextColor textColor = null;
    String colorFormatString;

    // ----- CONSTRUCTORS -----

    ItemRarity(String internalName, String name, double rarityWeight, TextColor textColor) {
        this.internalName = internalName;
        this.name = name;
        this.rarityWeight = rarityWeight;
        this.textColor = textColor;
    }

    ItemRarity(String internalName, String name, double rarityWeight, String colorFormatString) {
        this.internalName = internalName;
        this.name = name;
        this.rarityWeight = rarityWeight;
        this.colorFormatString = colorFormatString;
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public Component getFormattedRarity() {
        Component component = Component.text("");
        if (textColor == null) component = component.append(Component.text(HexUtils.colorify(colorFormatString + name + " Item")));
        else component = component.append(Component.text(name + " Item").color(textColor).decoration(TextDecoration.ITALIC, false));
        return component;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(CobaltCore.getInstance(), "rarity." + internalName);
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

}
