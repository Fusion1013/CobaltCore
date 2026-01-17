package se.fusion1013.cobaltCore.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Handles loading from Json and YAML files.
 *
 * @param <T> type of the builder.
 */
public interface IFileLoaderComponent<T> {
    void load(YamlConfiguration yaml, T builder);

    void load(JsonObject json, T builder);
}
