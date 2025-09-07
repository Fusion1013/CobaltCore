package se.fusion1013.cobaltCore.util;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;

public interface IFileConstructor {
    INameProvider createFrom(YamlConfiguration yaml);
    INameProvider createFrom(JsonObject json);
}
