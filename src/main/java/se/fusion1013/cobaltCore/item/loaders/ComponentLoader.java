package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.item.CobaltItem;
import se.fusion1013.cobaltCore.item.components.ComponentManager;
import se.fusion1013.cobaltCore.item.components.IItemComponent;

import java.util.List;
import java.util.Map;

public class ComponentLoader implements IItemLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("components")) return;
        List<Map<?, ?>> mapList = yaml.getMapList("components");
        for (Map<?, ?> map : mapList) FromMap(map, builder);
    }

    private static void FromMap(Map<?, ?> map, CobaltItem.Builder builder) {
        map.keySet().forEach(k -> {
            String internalComponentName = (String) k;
            IItemComponent component = ComponentManager.getComponent(internalComponentName, (Map<?, ?>) map.get(k), builder.internalName);
            if (component != null) builder.component(component);
        });
    }

    @Override
    public void load(JsonObject json, CobaltItem.Builder builder) {

    }
}