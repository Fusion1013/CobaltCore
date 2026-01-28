package se.fusion1013.cobaltCore.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public interface IObjectProperty<T, B> {
    void create(T obj);

    void fromJson(JsonObject obj, B builder);

    void saveJson(JsonObject json);

    void fromYaml(ConfigurationSection yaml, B builder);

    void saveYaml(ConfigurationSection yaml);

    String getId();

    default List<String> getLocalizedInfo() {
        return new ArrayList<>();
    }
}
