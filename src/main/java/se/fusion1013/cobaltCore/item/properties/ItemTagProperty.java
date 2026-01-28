package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataType;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

import java.util.ArrayList;
import java.util.List;

public class ItemTagProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final List<String> tags = new ArrayList<>();

    @Override
    public String getId() {
        return "item_tag";
    }

    @Override
    public void create(ItemCreationContext obj) {
        for (String tag : tags) {
            obj.persistent.set(new NamespacedKey(CobaltCore.getInstance(), tag), PersistentDataType.INTEGER, 1);
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
        if (yaml.contains("tags")) tags.addAll(yaml.getStringList("tags"));
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
        yaml.set("tags", tags);
    }
}
