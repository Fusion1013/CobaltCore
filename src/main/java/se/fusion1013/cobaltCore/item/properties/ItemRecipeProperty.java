package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.crafting.RecipeManager;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

public class ItemRecipeProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {
    @Override
    public String getId() {
        return "item_recipe";
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
        RecipeManager.loadRecipes(yaml, builder.getInternalName());
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }
}
