package se.fusion1013.cobaltCore.variable.provider;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public interface IValueProvider<T> {

    T getValue();

    void setValue(T value);

    void load(ConfigurationSection yaml);

    void load(Map<?, ?> map);
}
