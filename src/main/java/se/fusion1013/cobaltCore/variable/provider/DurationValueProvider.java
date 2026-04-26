package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DurationValueProvider extends AbstractValueProvider<Integer> {

    private static final Random random = new Random();

    private int min;
    private int max;
    private DurationFormat format = DurationFormat.TICKS;

    public DurationValueProvider(String parameterName, int value) {
        super(parameterName);
        this.min = value;
        this.max = value;
    }

    public DurationValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Gets the value of this duration in ticks.
     *
     * @return the duration in ticks.
     */
    @Override
    public Integer getValue() {
        int value = random.nextInt(min, max + 1);
        return switch (format) {
            case TICKS -> value;
            case SECONDS -> value * 20;
            case MINUTES -> value * 20 * 60;
        };
    }

    @Override
    public void setValue(Integer value) {
        this.min = value;
        this.max = value;
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof Integer value) {
            this.min = value;
            this.max = value;
        } else if (map.containsKey(parameterName)) {
            Map<?, ?> body = (Map<?, ?>) map.get(parameterName);
            if (body.containsKey("min")) this.min = (int) body.get("min");
            if (body.containsKey("max")) this.max = (int) body.get("max");
            if (body.containsKey("value")) {
                this.min = (int) body.get("value");
                this.max = this.min;
            }
            if (body.containsKey("format"))
                this.format = EnumUtils.findEnumInsensitiveCase(DurationFormat.class, (String) body.get("format"));
        }
    }

    @Override
    public List<Integer> getValueList() {
        return List.of();
    }

    private enum DurationFormat {
        TICKS, SECONDS, MINUTES
    }
}
