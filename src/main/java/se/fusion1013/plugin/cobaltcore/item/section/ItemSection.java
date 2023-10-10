package se.fusion1013.plugin.cobaltcore.item.section;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.util.BlockUtil;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;
import se.fusion1013.plugin.cobaltcore.util.INameProvider;

public class ItemSection implements INameProvider {

    private final String internalName;
    private String displayName;
    private String description;

    private NamedTextColor color;

    private double weight;

    public ItemSection(String internalName) {
        this.internalName = internalName;
    }

    // region File Loading

    public ItemSection(YamlConfiguration yml) {
        if (!yml.contains("internal_name")) internalName = "error_not_found";
        else internalName = yml.getString("internal_name");

        load(yml);
    }

    private void load(YamlConfiguration yml) {
        if (yml.contains("display_name")) displayName = yml.getString("display_name");
        if (yml.contains("description")) description = yml.getString("description");
        if (yml.contains("color")) {
            String colorText = yml.getString("color");
            if (colorText != null) color = NamedTextColor.NAMES.value(colorText);
        }
        if (yml.contains("weight")) weight = yml.getDouble("weight");
    }

    public ItemSection(JsonObject json) {
        if (!json.has("internal_name")) internalName = "error_not_found";
        else internalName = json.get("internal_name").getAsString();

        load(json);
    }

    private void load(JsonObject json) {

    }

    // endregion

    // region Getters / Setters

    @Override
    public String getInternalName() {
        return internalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public double getWeight() {
        return weight;
    }

    public Component getFormattedName() {
        if (color == null) return Component.text(HexUtils.colorify(displayName));
        return Component.text(displayName).color(color).decoration(TextDecoration.ITALIC, false);
    }

    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(CobaltCore.getInstance(), "item_section." + internalName);
    }

    public Material getBoxMaterial() {
        return BlockUtil.getColoredShulkerBox(color);
    }

    // endregion

}
