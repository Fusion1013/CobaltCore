package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.util.EnchantmentContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnchantmentValueProvider extends AbstractValueProvider<EnchantmentContainer> {

    private static final Random random = new Random();
    private final List<EnchantmentContainer> enchantments = new ArrayList<>();

    public EnchantmentValueProvider(String parameterName, EnchantmentContainer value) {
        super(parameterName);
        enchantments.add(value);
    }

    public EnchantmentValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public boolean isEmpty() {
        return enchantments.isEmpty();
    }

    @Override
    public EnchantmentContainer getValue() {
        if (enchantments.isEmpty()) return null;
        return enchantments.get(random.nextInt(enchantments.size()));
    }

    @Override
    public void setValue(EnchantmentContainer value) {
        enchantments.clear();
        enchantments.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;
        enchantments.addAll(fromMapList(yaml.getMapList(parameterName)));
    }

    @Override
    public void load(Map<?, ?> map) {
        // TODO
    }

    private static List<EnchantmentContainer> fromMapList(List<Map<?, ?>> mapList) {
        List<EnchantmentContainer> enchantments = new ArrayList<>();
        for (Map<?, ?> map : mapList) enchantments.addAll(fromMap(map));
        return enchantments;
    }

    private static List<EnchantmentContainer> fromMap(Map<?, ?> map) {
        if (map.containsKey("enchantment")) {
            return List.of(new EnchantmentContainer(map));
        } else {
            return List.of(); // TODO
        }
    }

    @Override
    public List<EnchantmentContainer> getValueList() {
        return enchantments;
    }
}
