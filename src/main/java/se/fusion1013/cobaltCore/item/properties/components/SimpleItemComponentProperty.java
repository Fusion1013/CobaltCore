package se.fusion1013.cobaltCore.item.properties.components;

import com.google.gson.JsonObject;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

public class SimpleItemComponentProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private int maxStackSize = -1;

    @Override
    public String getId() {
        return "simple_item_component";
    }

    @Override
    public void create(ItemCreationContext obj) {
        if (maxStackSize > 0) obj.itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {

    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        maxStackSize = yaml.getInt("max_stack_size");
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }
}
