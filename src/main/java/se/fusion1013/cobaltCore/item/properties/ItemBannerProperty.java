package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.BannerMeta;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemBannerProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final List<BannerData> bannerData = new ArrayList<>();

    @Override
    public String getId() {
        return "item_banner";
    }

    @Override
    public void create(ItemCreationContext obj) {
        if (obj.itemMeta instanceof BannerMeta bannerMeta) {
            setBannerMeta(bannerMeta, obj);
        }
    }

    private void setBannerMeta(BannerMeta bannerMeta, ItemCreationContext obj) {
        bannerData.forEach(bd -> {
            bannerMeta.addPattern(new Pattern(bd.color, bd.pattern));
        });
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {

    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        if (!yaml.contains("banner")) return;
        List<Map<?, ?>> mapList = yaml.getMapList("banner");
        for (Map<?, ?> map : mapList) {
            String patternString = (String) map.get("pattern");
            PatternType patternType = Registry.BANNER_PATTERN.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, patternString));
            String dyeColorString = (String) map.get("color");
            DyeColor dyeColor = EnumUtils.findEnumInsensitiveCase(DyeColor.class, dyeColorString);
            bannerData.add(new BannerData(patternType, dyeColor));
        }
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }

    private static class BannerData {
        public PatternType pattern;
        public DyeColor color;

        public BannerData(PatternType pattern, DyeColor color) {
            this.pattern = pattern;
            this.color = color;
        }
    }
}
