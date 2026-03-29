package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LiteralValueProvider extends AbstractValueProvider<String> {

    private static final Random random = new Random();

    private final List<String> values = new ArrayList<>();
    private final String[] allowed;

    public LiteralValueProvider(String parameterName, String[] allowed) {
        super(parameterName);
        this.allowed = allowed;
    }

    public LiteralValueProvider(String parameterName, String value, String[] allowed) {
        super(parameterName);
        this.values.add(value);
        this.allowed = allowed;
    }

    @Override
    public String getValue() {
        return values.get(random.nextInt(values.size()));
    }

    @Override
    public void setValue(String value) {
        if (Arrays.stream(allowed).noneMatch(s -> s.equalsIgnoreCase(value))) return;
        this.values.clear();
        this.values.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {

    }

    @Override
    public void load(Map<?, ?> map) {
        if (!map.containsKey(parameterName)) return;

        if (map.get(parameterName) instanceof List<?> list) {
            values.clear();
            values.addAll((List<String>) list);
        } else if (map.get(parameterName) instanceof String stringValue) {
            setValue(stringValue);
        }
    }

    @Override
    public List<String> getValueList() {
        return values;
    }
}
