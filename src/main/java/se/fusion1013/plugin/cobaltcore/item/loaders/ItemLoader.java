package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.item.ICustomItem;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;

public class ItemLoader {

    private static final IItemLoader[] LOADERS = new IItemLoader[] {
            new ItemBaseLoader(),
            new RarityLoader(),
            new EnchantmentLoader(),
            new CategoryLoader(),
            new LoreLoader(),
            new AttributeLoader(),
            new MetaEditorLoader(),
            new ComponentLoader(),
            new RecipeLoader()
    };

    public static ICustomItem Load(YamlConfiguration yaml) {
        String internalName = yaml.getString("internal_name");
        if (internalName == null) return null;

        var builder = new CobaltItem.Builder(internalName);
        for (IItemLoader loader : LOADERS) loader.Load(yaml, builder);
        return builder.build();
    }

    public static ICustomItem Load(JsonObject json) {
        String internalName = json.get("internal_name").getAsString();
        if (internalName == null) return null;

        var builder = new CobaltItem.Builder(internalName);
        for (IItemLoader loader : LOADERS) loader.Load(json, builder);
        return builder.build();
    }

}
