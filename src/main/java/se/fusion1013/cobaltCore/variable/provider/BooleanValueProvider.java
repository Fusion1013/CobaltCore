package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.util.MapUtil;

import java.util.Map;

public class BooleanValueProvider extends AbstractValueProvider<Boolean> {

    private boolean value;

    public BooleanValueProvider(String parameterName, boolean value) {
        super(parameterName);
        this.value = value;
    }

    public BooleanValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public void load(Map<?, ?> map) {
        this.value = MapUtil.setBoolean(map, parameterName, value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (yaml.get(parameterName) instanceof Boolean value) {
            this.value = value;
        }
    }
}
