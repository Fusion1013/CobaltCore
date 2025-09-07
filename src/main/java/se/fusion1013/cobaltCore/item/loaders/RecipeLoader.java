package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.item.crafting.RecipeManager;

public class RecipeLoader implements IItemLoader {

    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        RecipeManager.loadRecipes(yaml, builder.internalName);
    }

    @Override
    public void Load(JsonObject json, CobaltItem.Builder builder) {

    }
}