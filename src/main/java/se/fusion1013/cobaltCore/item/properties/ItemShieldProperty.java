package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ShieldMeta;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

public class ItemShieldProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {
    @Override
    public String getId() {
        return "item_shield";
    }

    @Override
    public void create(ItemCreationContext obj) {
        if (obj.itemMeta instanceof ShieldMeta shieldMeta) {
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

    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }
}
