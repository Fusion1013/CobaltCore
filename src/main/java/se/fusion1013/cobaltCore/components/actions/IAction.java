package se.fusion1013.cobaltCore.components.actions;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public interface IAction {
    void execute(Map<String, Object> context);

    void loadFromYaml(ConfigurationSection yaml);

    String getId();

    void loadFromMap(Map<?, ?> map);
}
