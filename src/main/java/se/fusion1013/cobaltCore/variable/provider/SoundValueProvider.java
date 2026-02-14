package se.fusion1013.cobaltCore.variable.provider;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SoundValueProvider extends AbstractValueProvider<Sound> {

    private static final Random random = new Random();

    private final List<Sound> sounds = new ArrayList<>();

    public SoundValueProvider(String parameterName) {
        super(parameterName);
    }

    public SoundValueProvider(String parameterName, Sound sound) {
        super(parameterName);
        sounds.add(sound);
    }

    @Override
    public Sound getValue() {
        return sounds.get(random.nextInt(sounds.size()));
    }

    @Override
    public void setValue(Sound value) {
        sounds.clear();
        sounds.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof String value) {
            this.sounds.clear();
            this.sounds.add(Registry.SOUNDS.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, value)));
        } else if (map.containsKey(parameterName)) {
            List<String> values = (List<String>) map.get(parameterName);
            this.sounds.clear();
            values.forEach(v -> {
                this.sounds.add(Registry.SOUNDS.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, v)));
            });
        }
    }
}
