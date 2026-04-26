package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.variable.item.ItemVisualVariable;

public class ItemVisualProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final ItemVisualVariable itemVisual = new ItemVisualVariable("item_visual");

    @Override
    public String getId() {
        return "item_visual";
    }

    @Override
    public void create(ItemCreationContext obj) {
        itemVisual.create(obj);
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        itemVisual.load(yaml);
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
//        yaml.set("custom_model_data", customModelData);
//        yaml.set("item_model", itemModel);
//        yaml.set("display_name", itemName);
//        yaml.set("extra_lore", extraLore);
    }
}
