package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.section.ItemSection;
import se.fusion1013.cobaltCore.item.section.ItemSectionManager;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

public class ItemCategoryProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private ItemSection itemCategory;

    @Override
    public String getId() {
        return "item_category";
    }

    @Override
    public void create(ItemCreationContext obj) {
        if (itemCategory != null) {
            obj.persistent.set(itemCategory.getNamespacedKey(), PersistentDataType.BYTE, (byte) 1);

            // Add item category lore
            obj.lore.add(""); // Add a new line
            obj.lore.add(
                    LegacyComponentSerializer.legacyAmpersand().serialize(
                            itemCategory.getFormattedName().color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
                    )
            );
        }
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        if (!yaml.contains("category")) return;
        var categoryName = yaml.getString("category");
        ItemSection category = ItemSectionManager.getSection(categoryName);
        if (category != null) itemCategory = category;
        else CobaltCore.getInstance().getLogger().warning("Could not find category '" + categoryName + "'");
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
        if (itemCategory != null) yaml.set("category", itemCategory.getInternalName());
    }
}
