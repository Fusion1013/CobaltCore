package se.fusion1013.cobaltCore.components;

import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.util.INameProvider;

import java.util.Map;

public interface IComponent extends INameProvider {

    void execute(Map<String, Object> context);

    void load(ConfigurationSection yaml);

}
