package se.fusion1013.cobaltCore.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractObjectProperties<T, E> implements IObjectProperty<T, E> {
    public abstract String getId();

    public abstract void create(T obj);

    public abstract void fromJson(JsonObject json, E builder);

    public abstract void saveJson(JsonObject json);

    public abstract void fromYaml(ConfigurationSection yaml, E builder);

    public abstract void saveYaml(ConfigurationSection yaml);
}
