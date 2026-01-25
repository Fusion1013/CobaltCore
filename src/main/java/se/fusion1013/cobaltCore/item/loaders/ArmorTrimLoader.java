package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import se.fusion1013.cobaltCore.item.CobaltItem;

import java.util.List;
import java.util.Map;

public class ArmorTrimLoader implements IItemLoaderComponent {
    @Override
    public void load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("armor_trim")) return;
        fromMapList(yaml.getMapList("armor_trim"), builder);
    }

    private static void fromMapList(List<Map<?, ?>> mapList, CobaltItem.Builder builder) {
        for (Map<?, ?> map : mapList) fromMap(map, builder);
    }

    private static void fromMap(Map<?, ?> map, CobaltItem.Builder builder) {
        map.keySet().forEach(k -> {
            Map<?, ?> values = (Map<?, ?>) map.get(k);
            builder.editMeta(meta -> {
                if (meta instanceof ArmorMeta armorMeta) {
                    addArmorTrim(values, armorMeta);
                }
                return meta;
            });
        });
    }

    private static void addArmorTrim(Map<?, ?> map, ArmorMeta armorMeta) {
        String trimMaterialName = (String) map.get("material");
        TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, trimMaterialName));

        String trimPatternName = (String) map.get("pattern");
        TrimPattern trimPattern = Registry.TRIM_PATTERN.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, trimPatternName));
        ArmorTrim trim = new ArmorTrim(trimMaterial, trimPattern);

        armorMeta.setTrim(trim);
    }

    @Override
    public void load(JsonObject json, CobaltItem.Builder builder) {

    }
}
