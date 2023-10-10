package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.item.section.ItemSection;
import se.fusion1013.plugin.cobaltcore.item.section.ItemSectionManager;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;

public class CategoryLoader implements IItemLoader {

    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("category")) return;
        var categoryName = yaml.getString("category");
        ItemSection category = ItemSectionManager.getSection(categoryName);
        if (category != null) builder.category(category);
        else CobaltCore.getInstance().getLogger().warning("Could not find category '" + categoryName + "'");
    }

    @Override
    public void Load(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("category")) return;
        var categoryName = json.get("category").getAsString();
        ItemSection category = ItemSectionManager.getSection(categoryName);
        if (category != null) builder.category(category);
        else CobaltCore.getInstance().getLogger().warning("Could not find category '" + categoryName + "'");
    }
}
