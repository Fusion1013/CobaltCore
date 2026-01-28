package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.IItemMetaEditor;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.util.ColorUtil;
import se.fusion1013.cobaltCore.util.HexUtils;

import java.util.List;
import java.util.Map;

public class ItemMetaProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private IItemMetaEditor metaEditor;

    @Override
    public String getId() {
        return "item_meta";
    }

    @Override
    public void create(ItemCreationContext obj) {
        obj.itemMeta = metaEditor.editMeta(obj.itemMeta);
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        metaEditor = meta -> {
            configureRepairable(yaml, meta);
            configureBook(yaml, meta);
            configureLeatherArmor(yaml, meta);
            configureFlags(yaml, meta);
            configureMisc(yaml, meta);
            configureArmorTrim(yaml, meta);
            return meta;
        };
    }

    private static void configureArmorTrim(ConfigurationSection yaml, ItemMeta meta) {
        if (!yaml.contains("armor_trim")) return;
        for (Map<?, ?> map : yaml.getMapList("armor_trim")) {
            map.keySet().forEach(k -> {
                Map<?, ?> values = (Map<?, ?>) map.get(k);
                if (meta instanceof ArmorMeta armorMeta) {
                    addArmorTrim(values, armorMeta);
                }
            });
        }
    }

    private static void addArmorTrim(Map<?, ?> map, ArmorMeta armorMeta) {
        String trimMaterialName = (String) map.get("material");
        TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, trimMaterialName));

        String trimPatternName = (String) map.get("pattern");
        TrimPattern trimPattern = Registry.TRIM_PATTERN.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, trimPatternName));
        ArmorTrim trim = new ArmorTrim(trimMaterial, trimPattern);

        armorMeta.setTrim(trim);
    }

    private static void configureRepairable(ConfigurationSection yaml, ItemMeta meta) {
        if (meta instanceof Repairable repairable) {
            if (yaml.contains("repair_cost")) repairable.setRepairCost(yaml.getInt("repair_cost"));
        }
    }

    private static void configureBook(ConfigurationSection yaml, ItemMeta meta) {
        if (meta instanceof BookMeta bookMeta) {
            if (yaml.contains("book_author")) bookMeta.setAuthor(yaml.getString("book_author"));
            if (yaml.contains("book_generation"))
                bookMeta.setGeneration(EnumUtils.findEnumInsensitiveCase(BookMeta.Generation.class, yaml.getString("book_generation")));
            if (yaml.contains("book_title")) bookMeta.setTitle(yaml.getString("book_title"));
            if (yaml.contains("book_text")) {
                List<String> text = yaml.getStringList("book_text");
                for (String s : text) bookMeta.addPage(HexUtils.colorify(s));
            }
        }
    }

    private static void configureLeatherArmor(ConfigurationSection yaml, ItemMeta meta) {
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            if (yaml.contains("leather_armor_color")) {
                leatherArmorMeta.setColor(ColorUtil.hex2Rgb(yaml.getString("leather_armor_color")));
                leatherArmorMeta.addItemFlags(ItemFlag.HIDE_DYE);
            }
        }
    }

    private static void configureFlags(ConfigurationSection yaml, ItemMeta meta) {
        if (yaml.contains("flags")) {
            List<?> flags = yaml.getList("flags");
            if (flags != null) {
                flags.forEach(f -> {
                    meta.addItemFlags(EnumUtils.findEnumInsensitiveCase(ItemFlag.class, (String) f));
                });
            }
        }
    }

    private static void configureMisc(ConfigurationSection yaml, ItemMeta meta) {
        if (yaml.contains("unbreakable")) meta.setUnbreakable(yaml.getBoolean("unbreakable"));
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }
}
