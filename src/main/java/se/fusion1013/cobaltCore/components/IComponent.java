package se.fusion1013.cobaltCore.components;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.manager.registry.IRegistryItem;

import java.util.Map;

public interface IComponent extends IRegistryItem {

    void execute(Map<String, Object> context);

    void load(ConfigurationSection yaml);

}
