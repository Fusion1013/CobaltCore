package se.fusion1013.cobaltCore.variable.provider;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BiomeValueProvider extends AbstractValueProvider<Biome> {

    private static final Random random = new Random();

    private final List<Biome> biomes = new ArrayList<>();

    public BiomeValueProvider(String parameterName) {
        super(parameterName);
    }

    public BiomeValueProvider(String parameterName, Biome value) {
        super(parameterName);
        this.biomes.add(value);
    }

    @Override
    public Biome getValue() {
        return biomes.get(random.nextInt(biomes.size()));
    }

    @Override
    public void setValue(Biome value) {
        biomes.clear();
        biomes.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;
        Object obj = yaml.get(parameterName);
        load(obj);
    }

    private void load(Object obj) {
        if (obj instanceof String value) {
            biomes.add(Registry.BIOME.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, value)));
        } else if (obj instanceof List<?> list) {
            for (Object o : list) {
                biomes.add(Registry.BIOME.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, (String) o)));
            }
        }
    }

    @Override
    public void load(Map<?, ?> map) {
        if (!map.containsKey(parameterName)) return;
        Object obj = map.get(parameterName);
        load(obj);
    }

    @Override
    public List<Biome> getValueList() {
        return biomes;
    }
}
