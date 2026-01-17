package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.loader.AbstractFileLoader;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;

public class ItemLoader extends AbstractFileLoader<ICustomItem, CobaltItem.Builder> {

    private static final IItemLoaderComponent[] LOADERS = new IItemLoaderComponent[]{
            new ItemBaseLoader(),
            new RarityLoader(),
            new EnchantmentLoader(),
            new CategoryLoader(),
            new LoreLoader(),
            new AttributeLoader(),
            new MetaEditorLoader(),
            new ComponentLoader(),
            new RecipeLoader(),
            new ItemToggleLoader()
    };

    public static ICustomItem Load(YamlConfiguration yaml) {
        String internalName = yaml.getString("internal_name");
        if (internalName == null) return null;

        var builder = new CobaltItem.Builder(internalName);
        for (IItemLoaderComponent loader : LOADERS) loader.load(yaml, builder);
        return builder.build();
    }

    public static ICustomItem Load(JsonObject json) {
        String internalName = json.get("internal_name").getAsString();
        if (internalName == null) return null;

        var builder = new CobaltItem.Builder(internalName);
        for (IItemLoaderComponent loader : LOADERS) loader.load(json, builder);
        return builder.build();
    }

    @Override
    public ICustomItem load(YamlConfiguration yaml) {
        return Load(yaml);
    }

    @Override
    public ICustomItem load(JsonObject json) {
        return Load(json);
    }

    @Override
    protected IFileLoaderComponent<CobaltItem.Builder>[] getLoaders() {
        return LOADERS;
    }
}
