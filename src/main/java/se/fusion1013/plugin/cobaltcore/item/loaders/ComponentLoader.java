package se.fusion1013.plugin.cobaltcore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.plugin.cobaltcore.item.components.ComponentManager;
import se.fusion1013.plugin.cobaltcore.item.components.IItemComponent;
import se.fusion1013.plugin.cobaltcore.item.system.CobaltItem;

import java.util.List;
import java.util.Map;

public class ComponentLoader implements IItemLoader {

    @Override
    public void Load(YamlConfiguration yaml, CobaltItem.Builder builder) {
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
    public void Load(JsonObject json, CobaltItem.Builder builder) {

    }
}
