package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.util.HexUtils;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;
import se.fusion1013.cobaltCore.variable.util.AbstractLoadedObject;

import java.util.List;
import java.util.Map;

public class ItemVisual extends AbstractLoadedObject {

    private final IntVariable customModelData = new IntVariable("custom_model_data");
    private final StringVariable itemModel = new StringVariable("item_model");
    private final StringVariable itemName = new StringVariable("display_name");
    private final StringVariable extraLore = new StringVariable("extra_lore");

    public void create(ItemCreationContext obj) {
        if (customModelData.isPresent()) {
            obj.itemMeta.setCustomModelData(customModelData.getValue());
        }

        if (itemModel.isPresent()) {
            NamespacedKey key = NamespacedKey.fromString(itemModel.getValue());
            obj.itemMeta.setItemModel(key);
        }

        if (itemName.isPresent()) {
            obj.itemMeta.setDisplayName(HexUtils.colorify(itemName.getValue()));
        }

        if (extraLore.isPresent()) {
            obj.lore.add(""); // Add a new line
            obj.lore.addAll(extraLore.getValueList());
        }
    }

    @Override
    protected List<AbstractVariable> variables() {
        return List.of(customModelData, itemModel, itemName, extraLore);
    }

    public ItemVisual(ConfigurationSection yaml) {
        load(yaml);
    }

    public ItemVisual(JsonObject json) {
        load(json);
    }

    public ItemVisual(Map<?, ?> map) {
        load(map);
    }
}
