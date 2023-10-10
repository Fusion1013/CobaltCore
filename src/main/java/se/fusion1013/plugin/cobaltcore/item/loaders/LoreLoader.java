package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;

import java.util.ArrayList;
import java.util.List;

public class LoreLoader implements IItemLoader {

    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("extra_lore")) return;
        List<String> extraLore = yaml.getStringList("extra_lore");
        builder.extraLore(extraLore.toArray(new String[0]));
    }

    @Override
    public void Load(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("extra_lore")) return;
        var jsonArray = json.getAsJsonArray("extra_lore");
        List<String> extraLore = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) extraLore.add(jsonElement.getAsString());
        builder.extraLore(extraLore.toArray(new String[0]));
    }
}
