package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.components.ComponentManager;
import se.fusion1013.cobaltCore.components.IComponent;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.item.ItemActivator;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;

import java.util.List;
import java.util.Map;

public class ItemActivatorProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {
    @Override
    public String getId() {
        return "activator";
    }

    @Override
    public void create(ItemCreationContext obj) {

    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {

    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        if (!yaml.contains("components")) return;

        List<Map<?, ?>> components = yaml.getMapList("components");
        for (Map<?, ?> componentData : components) {
            String triggerName = (String) componentData.get("trigger");
            ItemActivator trigger = EnumUtils.findEnumInsensitiveCase(ItemActivator.class, triggerName);

            String componentId = (String) componentData.get("component");
            IComponent component = ComponentManager.getComponent(componentId);

            builder.addSyncItemActivator(trigger, component);
        }
    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {

    }
}
