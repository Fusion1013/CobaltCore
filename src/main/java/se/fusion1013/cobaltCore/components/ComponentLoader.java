package se.fusion1013.cobaltCore.components;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.loader.AbstractFileLoader;
import se.fusion1013.cobaltCore.loader.IFileLoaderComponent;

public class ComponentLoader extends AbstractFileLoader<IComponent, Component> {

    public static IComponent loadComponent(YamlConfiguration yaml) {
        IComponent component = new Component();
        component.load(yaml);
        return component;
    }

    public static IComponent loadComponent(JsonObject json) {
        return null;
    }

    @Override
    public IComponent load(YamlConfiguration yaml) {
        return loadComponent(yaml);
    }

    @Override
    public IComponent load(JsonObject json) {
        return loadComponent(json);
    }

    @Override
    protected IFileLoaderComponent<Component>[] getLoaders() {
        return new IFileLoaderComponent[0];
    }
}
