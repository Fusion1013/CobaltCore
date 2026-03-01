package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.util.MapUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LiteralValueProvider extends AbstractValueProvider<String> {

    private String value;
    private final String[] allowed;

    public LiteralValueProvider(String parameterName, String[] allowed) {
        super(parameterName);
        this.allowed = allowed;
    }

    public LiteralValueProvider(String parameterName, String value, String[] allowed) {
        super(parameterName);
        this.value = value;
        this.allowed = allowed;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        if (Arrays.stream(allowed).noneMatch(s -> s.equalsIgnoreCase(value))) return;
        this.value = value;
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {
        String newValue = MapUtil.setString(map, parameterName, value == null ? "" : value);
        setValue(newValue);
    }

    @Override
    public List<String> getValueList() {
        return List.of();
    }
}
