package se.fusion1013.cobaltCore.components;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.CobaltPlugin;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltCore.util.FileUtil;
import se.fusion1013.cobaltCore.util.IFileConstructor;
import se.fusion1013.cobaltCore.util.INameProvider;
import se.fusion1013.cobaltCore.util.IProviderStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ComponentManager extends Manager<CobaltCore> {

    private static final Map<String, IComponent> COMPONENTS = new HashMap<>();

    public ComponentManager(CobaltCore plugin) {
        super(plugin);
    }

    public static void loadComponentFiles(CobaltPlugin plugin, boolean overwrite) {
        FileUtil.loadFilesInto(plugin, "components/", new IProviderStorage() {
            @Override
            public void put(String key, INameProvider provider) {
                register(provider);
            }

            @Override
            public boolean has(String key) {
                return getComponent(key) != null;
            }

            @Override
            public INameProvider get(String key) {
                return getComponent(key);
            }
        }, new IFileConstructor() {
            @Override
            public INameProvider createFrom(YamlConfiguration yaml) {
                return ComponentLoader.loadComponent(yaml);
            }

            @Override
            public INameProvider createFrom(JsonObject json) {
                return ComponentLoader.loadComponent(json);
            }
        }, overwrite);
    }

    public static void reloadComponents() {
        for (CobaltPlugin plugin : CobaltCore.getRegisteredCobaltPlugins()) {
            loadComponentFiles(plugin, true);
        }
    }

    @Override
    public void reload() {
        reloadComponents();
    }

    @Override
    public void disable() {

    }

    public static IComponent register(INameProvider component) {
        return register((IComponent) component);
    }

    public static IComponent register(IComponent component) {
        COMPONENTS.put(component.getInternalName(), component);
        return component;
    }

    public static IComponent getComponent(String key) {
        return COMPONENTS.get(key);
    }

    public static Collection<IComponent> getComponents() {
        return COMPONENTS.values();
    }

    public static String[] getComponentNames() {
        return getComponents().stream().map(INameProvider::getInternalName).toList().toArray(new String[0]);
    }

    private static ComponentManager INSTANCE;

    public static ComponentManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComponentManager(CobaltCore.getInstance());
        }
        return INSTANCE;
    }
}
