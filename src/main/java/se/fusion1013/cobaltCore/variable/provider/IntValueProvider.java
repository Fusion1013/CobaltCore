package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Random;

public class IntValueProvider extends AbstractValueProvider<Integer> {

    private static final Random random = new Random();

    private int min;
    private int max;

    public IntValueProvider(String parameterName, int value) {
        this(parameterName);
        this.min = value;
        this.max = value;
    }

    public IntValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public Integer getValue() {
        if (min == max) return min;
        return random.nextInt(min, max + 1);
    }

    @Override
    public void setValue(Integer value) {
        this.min = value;
        this.max = value;
    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof Integer value) {
            this.min = value;
            this.max = value;
        } else if (map.containsKey(parameterName)) {
            Map<?, ?> minMax = (Map<?, ?>) map.get(parameterName);
            this.min = (int) minMax.get("min");
            this.max = (int) minMax.get("max");
        }
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (yaml.get(parameterName) instanceof Integer value) {
            this.min = value;
            this.max = value;
        } else {
            ConfigurationSection valueHolder = yaml.getConfigurationSection(parameterName);
            this.min = valueHolder.getInt("min");
            this.min = valueHolder.getInt("max");
        }
    }
}
