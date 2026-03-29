package se.fusion1013.cobaltCore.components.conditions;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.Map;

public interface ICondition extends IRegistryItem {

    boolean evaluate(Map<String, Object> context);

    void loadFromYaml(ConfigurationSection yaml);

    void loadFromMap(Map<?, ?> map);

    String getDescription();

}
