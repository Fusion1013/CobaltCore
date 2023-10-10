package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;

public interface IItemLoader {
    void Load(YamlConfiguration yaml, CobaltItem.Builder builder);
    void Load(JsonObject json, CobaltItem.Builder builder);
}
