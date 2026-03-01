package se.fusion1013.cobaltCore.variable.provider;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PotionTypeValueProvider extends AbstractValueProvider<PotionEffectType> {

    private static final Random random = new Random();

    private final List<PotionEffectType> values = new ArrayList<>();

    public PotionTypeValueProvider(String parameterName) {
        super(parameterName);
    }

    public PotionTypeValueProvider(String parameterName, PotionEffectType value) {
        super(parameterName);
        this.values.add(value);
    }

    @Override
    public PotionEffectType getValue() {
        return values.get(random.nextInt(values.size()));
    }

    @Override
    public void setValue(PotionEffectType value) {
        values.clear();
        values.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;

        if (yaml.get(parameterName) instanceof String value) {
            this.values.clear();
            this.values.add(Registry.POTION_EFFECT_TYPE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, value)));
        } else if (yaml.get(parameterName) instanceof List<?> list) {
            this.values.clear();
            list.forEach(v -> this.values.add(Registry.POTION_EFFECT_TYPE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, (String) v))));
        }
    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof String value) {
            this.values.clear();
            this.values.add(Registry.POTION_EFFECT_TYPE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, value)));
        } else if (map.containsKey(parameterName)) {
            List<String> values = (List<String>) map.get(parameterName);
            this.values.clear();
            values.forEach(v -> this.values.add(Registry.POTION_EFFECT_TYPE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, v))));
        }
    }

    @Override
    public List<PotionEffectType> getValueList() {
        return List.of();
    }
}
