package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.properties.components.SimpleItemComponentProperty;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.loader.IObjectProperty;

import java.util.List;

public class ItemComponentProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private static final List<IObjectProperty<ItemCreationContext, AbstractCobaltItem>> PROPERTIES = List.of(
            new SimpleItemComponentProperty()
    );

    @Override
    public String getId() {
        return "item_component";
    }

    @Override
    public void create(ItemCreationContext obj) {
        obj.itemStack.setItemMeta(obj.itemMeta);
        for (IObjectProperty<ItemCreationContext, AbstractCobaltItem> property : PROPERTIES) {
            property.create(obj);
        }
        obj.itemMeta = obj.itemStack.getItemMeta();
        obj.persistent = obj.itemMeta.getPersistentDataContainer();
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {
    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
//        if (!yaml.contains("components")) return;
//        ConfigurationSection componentYaml = yaml.getConfigurationSection("components");
//
//        for (IObjectProperty<ItemCreationContext, AbstractCobaltItem> property : PROPERTIES) {
//            property.fromYaml(componentYaml, builder);
//        }
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
    }
}
