package se.fusion1013.cobaltCore.util;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.variable.BooleanVariable;
import se.fusion1013.cobaltCore.variable.IntVariable;

import java.util.Map;

public class EnchantmentContainer {

    private final Enchantment enchantment;
    private final IntVariable level = new IntVariable("level");
    private final BooleanVariable ignoreLevelRestrictions = new BooleanVariable("ignore_level_restrictions");

    public EnchantmentContainer(Map<?, ?> map) {
        Registry<Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        String enchantmentName = (String) map.get("enchantment");
        NamespacedKey key = NamespacedKey.fromString(enchantmentName);
        enchantment = enchantmentRegistry.get(key);

        level.load(map);
        ignoreLevelRestrictions.load(map);
    }

    public void apply(ItemCreationContext context) {
        context.itemMeta.addEnchant(enchantment, level.getValue(), ignoreLevelRestrictions.getValue());
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level.getValue();
    }

    public boolean getIgnoreLevelRestrictions() {
        return ignoreLevelRestrictions.getValue();
    }
}
