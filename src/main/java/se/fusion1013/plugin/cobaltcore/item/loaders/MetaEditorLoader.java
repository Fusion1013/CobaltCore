package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.Repairable;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;
import se.fusion1013.plugin.cobaltcore.util.ColorUtil;
import se.fusion1013.plugin.cobaltcore.util.HexUtils;

import java.util.List;

public class MetaEditorLoader implements IItemLoader {

    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        builder.editMeta(meta -> {
            ConfigureRepairable(yaml, meta);
            ConfigureBook(yaml, meta);
            ConfigureLeatherArmor(yaml, meta);
            ConfigureFlags(yaml, meta);
            ConfigureMisc(yaml, meta);
            return meta;
        });
    }

    private static void ConfigureRepairable(YamlConfiguration yaml, ItemMeta meta) {
        if (meta instanceof Repairable repairable) {
            if (yaml.contains("repair_cost")) repairable.setRepairCost(yaml.getInt("repair_cost"));
        }
    }

    private static void ConfigureBook(YamlConfiguration yaml, ItemMeta meta) {
        if (meta instanceof BookMeta bookMeta) {
            if (yaml.contains("book_author")) bookMeta.setAuthor(yaml.getString("book_author"));
            if (yaml.contains("book_generation")) bookMeta.setGeneration(EnumUtils.findEnumInsensitiveCase(BookMeta.Generation.class, yaml.getString("book_generation")));
            if (yaml.contains("book_title")) bookMeta.setTitle(yaml.getString("book_title"));
            if (yaml.contains("book_text")) {
                List<String> text = yaml.getStringList("book_text");
                for (String s : text) bookMeta.addPage(HexUtils.colorify(s));
            }
        }
    }

    private static void ConfigureLeatherArmor(YamlConfiguration yaml, ItemMeta meta) {
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            if (yaml.contains("leather_armor_color")) {
                leatherArmorMeta.setColor(ColorUtil.hex2Rgb(yaml.getString("leather_armor_color")));
                leatherArmorMeta.addItemFlags(ItemFlag.HIDE_DYE);
            }
        }
    }

    private static void ConfigureFlags(YamlConfiguration yaml, ItemMeta meta) {
        if (yaml.contains("flags")) {
            List<?> flags = yaml.getList("flags");
            if (flags != null) {
                flags.forEach(f -> {
                    meta.addItemFlags(EnumUtils.findEnumInsensitiveCase(ItemFlag.class, (String) f));
                });
            }
        }
    }

    private static void ConfigureMisc(YamlConfiguration yaml, ItemMeta meta) {
        if (yaml.contains("unbreakable")) meta.setUnbreakable(yaml.getBoolean("unbreakable"));
    }

    @Override
    public void Load(JsonObject json, CobaltItem.Builder builder) {

    }
}
