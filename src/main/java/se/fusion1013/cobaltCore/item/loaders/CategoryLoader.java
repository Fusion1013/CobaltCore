package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.item.section.ItemSection;
import se.fusion1013.cobaltCore.item.section.ItemSectionManager;

public class CategoryLoader implements IItemLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("category")) return;
        var categoryName = yaml.getString("category");
        ItemSection category = ItemSectionManager.getSection(categoryName);
        if (category != null) builder.category(category);
        else CobaltCore.getInstance().getLogger().warning("Could not find category '" + categoryName + "'");
    }

    @Override
    public void load(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("category")) return;
        var categoryName = json.get("category").getAsString();
        ItemSection category = ItemSectionManager.getSection(categoryName);
        if (category != null) builder.category(category);
        else CobaltCore.getInstance().getLogger().warning("Could not find category '" + categoryName + "'");
    }
}