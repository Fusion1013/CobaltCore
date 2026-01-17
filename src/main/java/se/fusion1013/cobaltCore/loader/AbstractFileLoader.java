package se.fusion1013.cobaltCore.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class AbstractFileLoader<T, B> {

    public abstract T load(YamlConfiguration yaml);

    public abstract T load(JsonObject json);

    protected abstract IFileLoaderComponent<B>[] getLoaders();
}
