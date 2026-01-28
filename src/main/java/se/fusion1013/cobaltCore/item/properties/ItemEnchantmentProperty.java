package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.enchantment.EnchantmentManager;
import se.fusion1013.cobaltCore.item.enchantment.EnchantmentWrapper;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEnchantmentProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final List<EnchantmentWrapper> enchantments = new ArrayList<>();

    @Override
    public String getId() {
        return "enchantment";
    }

    @Override
    public void create(ItemCreationContext obj) {
        for (EnchantmentWrapper wrapper : enchantments)
            obj.itemStack = wrapper.add(obj.itemStack);
        obj.itemMeta = obj.itemStack.getItemMeta();
        obj.persistent = obj.itemMeta.getPersistentDataContainer();
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        if (!yaml.contains("enchantments")) return;

        List<Map<?, ?>> mapList = yaml.getMapList("enchantments");
        enchantments.addAll(fromMapList(mapList));
    }

    private static List<EnchantmentWrapper> fromMapList(List<Map<?, ?>> mapList) {
        List<EnchantmentWrapper> enchantments = new ArrayList<>();
        for (Map<?, ?> map : mapList) addFromMap(map, enchantments);
        return enchantments;
    }

    private static void addFromMap(Map<?, ?> map, List<EnchantmentWrapper> addTo) {
        map.keySet().forEach(k -> {
            Map<?, ?> values = (Map<?, ?>) map.get(k);

            String name = (String) k;
            int level = (int) values.get("level");
            boolean ignoreLevelRestriction = false;
            if (values.get("ignore_level_restrictions") != null)
                ignoreLevelRestriction = (boolean) values.get("ignore_level_restrictions");

            EnchantmentWrapper wrapper = EnchantmentManager.getEnchantment(name, level, ignoreLevelRestriction);
            if (wrapper != null) addTo.add(wrapper);
        });
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (EnchantmentWrapper wrapper : enchantments) {
            Map<String, Object> map = new HashMap<>();
            map.put(wrapper.getEnchantment().getKey().asString(), Map.of("level", wrapper.getLevel()));
            list.add(map);
        }
        yaml.set("enchantments", list);
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = super.getLocalizedInfo();

        info.add(" - Enchantments:");
        for (EnchantmentWrapper wrapper : enchantments) {
            info.add("   - " + wrapper.getEnchantment().getKey().asString() + " " + wrapper.getLevel());
        }

        return info;
    }
}
