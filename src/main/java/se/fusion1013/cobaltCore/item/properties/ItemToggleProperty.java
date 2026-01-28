package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.toggles.ItemToggleType;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

import java.util.Arrays;
import java.util.List;

public class ItemToggleProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private List<String> toggles;

    @Override
    public String getId() {
        return "item_toggle";
    }

    @Override
    public void create(ItemCreationContext obj) {

    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        if (!yaml.contains("toggles")) return;
        toggles = yaml.getStringList("toggles");
        for (String toggle : toggles) {
            ItemToggleType type = getToggleType(toggle);
            if (type == null) continue;
            builder.setItemToggle(type, true);
        }
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
        yaml.set("toggles", toggles);
    }

    private static ItemToggleType getToggleType(String key) {
        return Arrays.stream(ItemToggleType.values()).filter(k -> k.getKey().equalsIgnoreCase(key)).findAny().orElse(null);
    }
}
