package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StringValueProvider extends AbstractValueProvider<String> {

    private static final Random random = new Random();

    private final List<String> values = new ArrayList<>();

    public StringValueProvider(String parameterName) {
        super(parameterName);
    }

    public StringValueProvider(String parameterName, String value) {
        super(parameterName);
        this.values.add(value);
    }

    @Override
    public String getValue() {
        if (values.isEmpty()) return null;
        return values.get(random.nextInt(values.size()));
    }

    @Override
    public void setValue(String value) {
        values.clear();
        values.add(value);
    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof String value) {
            this.values.clear();
            this.values.add(value);
        } else if (map.containsKey(parameterName)) {
            List<String> values = (List<String>) map.get(parameterName);
            this.values.clear();
            this.values.addAll(values);
        }
    }

    @Override
    public List<String> getValueList() {
        return values;
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (yaml.get(parameterName) instanceof String value) {
            this.values.clear();
            this.values.add(value);
        } else {
            this.values.clear();
            this.values.addAll(yaml.getStringList(parameterName));
        }
    }
}
