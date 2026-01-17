package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.util.HexUtils;

public class ItemBaseLoader implements IItemLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        loadMaterial(yaml, builder);
        loadModelData(yaml, builder);
        loadDisplayName(yaml, builder);
        loadItemModel(yaml, builder);
    }

    private static void loadMaterial(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("material")) return;
        Material material = EnumUtils.findEnumInsensitiveCase(Material.class, yaml.getString("material"));
        if (material != null) builder.material(material);
    }

    private static void loadModelData(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("model_data")) return;
        builder.modelData(yaml.getInt("model_data"));
    }

    private static void loadDisplayName(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("display_name")) return;
        builder.itemName(HexUtils.colorify(yaml.getString("display_name")));
    }

    private static void loadItemModel(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("item_model")) return;
        builder.itemModel(yaml.getString("item_model"));
    }

    @Override
    public void load(JsonObject json, CobaltItem.Builder builder) {
        loadMaterial(json, builder);
        loadModelData(json, builder);
        loadDisplayName(json, builder);
        loadItemModel(json, builder);
    }

    private static void loadMaterial(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("material")) return;
        Material material = EnumUtils.findEnumInsensitiveCase(Material.class, json.get("material").getAsString());
        if (material != null) builder.material(material);
    }

    private static void loadModelData(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("model_data")) return;
        builder.modelData(json.get("model_data").getAsInt());
    }

    private static void loadDisplayName(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("display_name")) return;
        builder.itemName(HexUtils.colorify(json.get("display_name").getAsString()));
    }

    private static void loadItemModel(JsonObject json, CobaltItem.Builder builder) {
        if (!json.has("item_model")) return;
        builder.itemModel(json.get("item_model").getAsString());
    }
}