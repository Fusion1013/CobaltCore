package se.fusion1013.cobaltCore.variable.provider;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public interface IValueProvider<T> {

    T getValue();

    void setValue(T value);

    void load(ConfigurationSection yaml);

    void load(Map<?, ?> map);

    void load(JsonObject json);

    List<T> getValueList();

    boolean isEmpty();
}
