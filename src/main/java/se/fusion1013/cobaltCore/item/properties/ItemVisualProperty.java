package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.util.HexUtils;

import java.util.ArrayList;
import java.util.List;

public class ItemVisualProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private int customModelData = 0;
    private String itemModel = "";
    private String itemName = "";
    private final List<String> extraLore = new ArrayList<>();

    @Override
    public String getId() {
        return "item_visual";
    }

    @Override
    public void create(ItemCreationContext obj) {
        obj.itemMeta.setCustomModelData(customModelData);

        if (!itemModel.isEmpty()) {
            String[] itemModelNamespaceSplit = itemModel.split(":");
            if (itemModelNamespaceSplit.length > 1)
                obj.itemMeta.setItemModel(new NamespacedKey(itemModelNamespaceSplit[0], itemModelNamespaceSplit[1]));
            else obj.itemMeta.setItemModel(new NamespacedKey("minecraft", itemModelNamespaceSplit[0]));
        }

        obj.itemMeta.setDisplayName(HexUtils.colorify(itemName));

        if (!extraLore.isEmpty()) {
            obj.lore.add(""); // Add a new line
            obj.lore.addAll(extraLore);
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
        if (yaml.contains("custom_model_data")) customModelData = yaml.getInt("custom_model_data");
        if (yaml.contains("item_model")) itemModel = yaml.getString("item_model");
        if (yaml.contains("display_name")) itemName = yaml.getString("display_name");
        if (yaml.contains("extra_lore")) extraLore.addAll(yaml.getStringList("extra_lore"));
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
        yaml.set("custom_model_data", customModelData);
        yaml.set("item_model", itemModel);
        yaml.set("display_name", itemName);
        yaml.set("extra_lore", extraLore);
    }
}
