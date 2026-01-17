package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;

public interface IItemLoaderComponent extends IFileLoaderComponent<CobaltItem.Builder> {
    void load(YamlConfiguration yaml, CobaltItem.Builder builder);

    void load(JsonObject json, CobaltItem.Builder builder);
}
