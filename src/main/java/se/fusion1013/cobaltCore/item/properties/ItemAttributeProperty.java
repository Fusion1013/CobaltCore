package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.variable.AttributeVariable;

public class ItemAttributeProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final AttributeVariable attributes = new AttributeVariable("attributes");

    @Override
    public String getId() {
        return "item_attribute";
    }

    @Override
    public void create(ItemCreationContext obj) {
        attributes.applyToItem(obj);
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {
        // TODO
    }

    @Override
    public void saveJson(JsonObject json) {
        // TODO
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        attributes.load(yaml);
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
        // TODO
    }
}
