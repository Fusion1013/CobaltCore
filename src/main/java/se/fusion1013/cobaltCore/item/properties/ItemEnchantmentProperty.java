package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.util.EnchantmentContainer;
import se.fusion1013.cobaltCore.variable.EnchantmentVariable;

import java.util.List;

public class ItemEnchantmentProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final EnchantmentVariable enchantments = new EnchantmentVariable("enchantments");

    @Override
    public String getId() {
        return "enchantment";
    }

    @Override
    public void create(ItemCreationContext obj) {
        enchantments.getValueList().forEach(ec -> ec.apply(obj));
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
        enchantments.load(yaml);
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = super.getLocalizedInfo();

        info.add(" - Enchantments:");
        for (EnchantmentContainer wrapper : enchantments.getValueList()) {
            info.add("   - " + wrapper.getEnchantment().getKey().asString() + " " + wrapper.getLevel());
        }

        return info;
    }
}
