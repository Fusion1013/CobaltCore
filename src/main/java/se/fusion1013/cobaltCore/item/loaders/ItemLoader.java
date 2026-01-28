package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.item.ICustomItem;
import se.fusion1013.cobaltCore.loader.AbstractFileLoader;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;

public class ItemLoader extends AbstractFileLoader<ICustomItem, CobaltItem.Builder> {

    private static final IItemLoaderComponent[] LOADERS = new IItemLoaderComponent[]{
            new ComponentLoader()
    };

    public static ICustomItem loadItem(YamlConfiguration yaml) {
        return CobaltItem.load(yaml);
    }

    public static ICustomItem loadItem(JsonObject json) {
        return CobaltItem.load(json);
    }

    @Override
    public ICustomItem load(YamlConfiguration yaml) {
        return loadItem(yaml);
    }

    @Override
    public ICustomItem load(JsonObject json) {
        return loadItem(json);
    }

    @Override
    protected IFileLoaderComponent<CobaltItem.Builder>[] getLoaders() {
        return LOADERS;
    }
}
