package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.item.toggles.ItemToggleType;

import java.util.Arrays;
import java.util.List;

public class ItemToggleLoader implements IItemLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        loadItemToggles(yaml, builder);
    }

    private static void loadItemToggles(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("toggles")) return;
        List<String> toggles = yaml.getStringList("toggles");
        for (String toggle : toggles) {
            ItemToggleType type = getToggleType(toggle);
            if (type == null) continue;
            builder.setToggle(type, true);
        }
    }

    @Override
    public void load(JsonObject json, CobaltItem.Builder builder) {

    }

    private static void loadItemToggles(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("toggles")) return;
        JsonArray jsonArray = json.getAsJsonArray("toggles");
        // TODO
    }

    private static ItemToggleType getToggleType(String key) {
        return Arrays.stream(ItemToggleType.values()).filter(k -> k.getKey().equalsIgnoreCase(key)).findAny().orElse(null);
    }
}
