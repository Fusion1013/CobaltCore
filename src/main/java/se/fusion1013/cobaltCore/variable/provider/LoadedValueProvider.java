package se.fusion1013.cobaltCore.variable.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class LoadedValueProvider<T> extends AbstractValueProvider<T> {

    private final List<T> values = new ArrayList<>();

    private final Function<ConfigurationSection, T> fromYaml;
    private final Function<JsonObject, T> fromJson;
    private final Function<Map<?, ?>, T> fromMap;

    public LoadedValueProvider(String parameterName, Function<ConfigurationSection, T> fromYaml, Function<JsonObject, T> fromJson, Function<Map<?, ?>, T> fromMap) {
        super(parameterName);
        this.fromYaml = fromYaml;
        this.fromJson = fromJson;
        this.fromMap = fromMap;
    }

    public LoadedValueProvider(String parameterName, T value, Function<ConfigurationSection, T> fromYaml, Function<JsonObject, T> fromJson, Function<Map<?, ?>, T> fromMap) {
        super(parameterName);
        this.fromYaml = fromYaml;
        this.fromJson = fromJson;
        this.fromMap = fromMap;
        this.values.add(value);
    }

    @Override
    public T getValue() {
        if (values.isEmpty()) return null;
        return values.get(random.nextInt(values.size()));
    }

    @Override
    public void setValue(T value) {
        values.clear();
        values.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (yaml.contains(parameterName) && yaml.get(parameterName) instanceof List<?>) {
            List<Map<?, ?>> mapList = yaml.getMapList(parameterName);
            for (Map<?, ?> map : mapList) {
                T result = fromMap.apply(map);
                if (result != null) values.add(result);
            }
        } else {
            T result = fromYaml.apply(yaml);
            if (result != null) values.add(result);
        }
    }

    @Override
    public void load(JsonObject json) {
        if (json.has(parameterName) && json.get(parameterName) instanceof JsonArray) {
            JsonArray jsonArray = json.getAsJsonArray(parameterName);
            jsonArray.forEach(item -> {
                T result = fromJson.apply(item.getAsJsonObject());
                if (result != null) values.add(result);
            });
        } else {
            T result = fromJson.apply(json);
            if (result != null) values.add(result);
        }
    }

    @Override
    public void load(Map<?, ?> map) {
        if (map.containsKey(parameterName) && map.get(parameterName) instanceof List<?>) {
            List<Map<?, ?>> mapList = (List<Map<?, ?>>) map.get(parameterName);
            for (Map<?, ?> internalMap : mapList) {
                T result = fromMap.apply(internalMap);
                if (result != null) values.add(result);
            }
        } else {
            T result = fromMap.apply(map);
            if (result != null) values.add(result);
        }
    }

    @Override
    public List<T> getValueList() {
        return values;
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }
}
