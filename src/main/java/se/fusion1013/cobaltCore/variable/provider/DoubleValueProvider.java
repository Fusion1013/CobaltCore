package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.util.MapUtil;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DoubleValueProvider extends AbstractValueProvider<Double> {

    private static final Random random = new Random();

    private double min;
    private double max;

    public DoubleValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public DoubleValueProvider(String parameterName, double value) {
        super(parameterName);
        this.min = value;
        this.max = value;
    }

    @Override
    public Double getValue() {
        if (min == max) return min;
        return random.nextDouble(min, max);
    }

    @Override
    public void setValue(Double value) {
        this.min = value;
        this.max = value;
    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof Map<?, ?> minMax) {
            this.min = MapUtil.setDouble(minMax, "min", this.min);
            this.max = MapUtil.setDouble(minMax, "min", this.min);
        } else {
            double value = MapUtil.setDouble(map, parameterName, this.min);
            this.min = value;
            this.max = value;
        }
    }

    @Override
    public List<Double> getValueList() {
        return List.of();
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (yaml.get(parameterName) instanceof Double value) {
            this.min = value;
            this.max = value;
        } else {
            ConfigurationSection valueHolder = yaml.getConfigurationSection(parameterName);
            this.min = valueHolder.getDouble("min");
            this.min = valueHolder.getDouble("max");
        }
    }
}
