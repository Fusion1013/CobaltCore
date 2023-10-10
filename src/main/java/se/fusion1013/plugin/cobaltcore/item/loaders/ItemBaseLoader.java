package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

public class ItemBaseLoader implements IItemLoader {

    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        LoadMaterial(yaml, builder);
        LoadModelData(yaml, builder);
        LoadDisplayName(yaml, builder);
    }

    private static void LoadMaterial(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("material")) return;
        Material material = EnumUtils.findEnumInsensitiveCase(Material.class, yaml.getString("material"));
        if (material != null) builder.material(material);
    }

    private static void LoadModelData(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("model_data")) return;
        builder.modelData(yaml.getInt("model_data"));
    }

    private static void LoadDisplayName(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("display_name")) return;
        builder.itemName(HexUtils.colorify(yaml.getString("display_name")));
    }

    @Override
    public void Load(JsonObject json, CobaltItem.Builder builder) {
        LoadMaterial(json, builder);
        LoadModelData(json, builder);
        LoadDisplayName(json, builder);
    }

    private static void LoadMaterial(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("material")) return;
        Material material = EnumUtils.findEnumInsensitiveCase(Material.class, json.get("material").getAsString());
        if (material != null) builder.material(material);
    }

    private static void LoadModelData(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("model_data")) return;
        builder.modelData(json.get("model_data").getAsInt());
    }

    private static void LoadDisplayName(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("display_name")) return;
        builder.itemName(HexUtils.colorify(json.get("display_name").getAsString()));
    }
}
