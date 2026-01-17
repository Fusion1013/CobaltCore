package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.entity.CustomEntity;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;

public interface IEntityLoaderComponent extends IFileLoaderComponent<CustomEntity.Builder> {
    void load(YamlConfiguration yaml, CustomEntity.Builder builder);

    void load(JsonObject json, CustomEntity.Builder builder);
}
