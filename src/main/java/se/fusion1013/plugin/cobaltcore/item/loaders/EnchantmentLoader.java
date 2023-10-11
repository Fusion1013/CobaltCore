package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentManager;
import se.fusion1013.plugin.cobaltcore.item.enchantment.EnchantmentWrapper;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantmentLoader implements IItemLoader {
    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("enchantments")) return;

        List<Map<?, ?>> mapList = yaml.getMapList("enchantments");
        builder.enchantments(FromMapList(mapList));
    }

    @Override
    public void Load(JsonObject json, CobaltItem.Builder builder) {
    }

    private static EnchantmentWrapper[] FromMapList(List<Map<?, ?>> mapList) {
        List<EnchantmentWrapper> enchantments = new ArrayList<>();
        for (Map<?, ?> map : mapList) AddFromMap(map, enchantments);
        return enchantments.toArray(new EnchantmentWrapper[0]);
    }

    private static void AddFromMap(Map<?, ?> map, List<EnchantmentWrapper> addTo) {
        map.keySet().forEach(k -> {
            Map<?, ?> values = (Map<?, ?>) map.get(k);

            String name = (String) k;
            int level = (int) values.get("level");
            boolean ignoreLevelRestriction = false;
            if (values.get("ignore_level_restrictions") != null) ignoreLevelRestriction = (boolean) values.get("ignore_level_restrictions");

            EnchantmentWrapper wrapper = EnchantmentManager.getEnchantment(name, level, ignoreLevelRestriction);
            if (wrapper != null) addTo.add(wrapper);
        });
    }
}
