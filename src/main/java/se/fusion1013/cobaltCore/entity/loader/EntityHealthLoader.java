package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.entity.CustomEntity;
import se.fusion1013.cobaltCore.entity.settings.EntityHealthSettings;

public class EntityHealthLoader implements IEntityLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CustomEntity.Builder builder) {
        EntityHealthSettings.Builder healthBuilder = new EntityHealthSettings.Builder();

        if (!yaml.contains("health")) return;

        ConfigurationSection healthYaml = yaml.getConfigurationSection("health");
        if (healthYaml == null) return;

        if (healthYaml.contains("max"))
            healthBuilder.addMaxHealth(healthYaml.getDouble("max"));
        if (healthYaml.contains("scale_factor"))
            healthBuilder.addHealthScaling(healthYaml.getDouble("scale_factor"));
        if (healthYaml.contains("scale_distance"))
            healthBuilder.addHealthScaleDistance(healthYaml.getDouble("scale_distance"));

        builder.addEntitySettings(healthBuilder.build());
    }

    @Override
    public void load(JsonObject json, CustomEntity.Builder builder) {
        EntityHealthSettings.Builder healthBuilder = new EntityHealthSettings.Builder();
    }
}
