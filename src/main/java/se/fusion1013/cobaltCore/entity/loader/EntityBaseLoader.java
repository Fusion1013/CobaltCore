package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.entity.CustomEntity;
import se.fusion1013.cobaltCore.entity.settings.EntityBaseSettings;

public class EntityBaseLoader implements IEntityLoaderComponent {
    @Override
    public void load(YamlConfiguration yaml, CustomEntity.Builder builder) {
        EntityBaseSettings.Builder settings = new EntityBaseSettings.Builder();

        if (yaml.contains("display_name")) settings.addDisplayName(yaml.getString("display_name"));
        if (yaml.contains("no_gravity")) settings.addNoGravity(yaml.getBoolean("no_gravity"));
        if (yaml.contains("silent")) settings.addSilent(yaml.getBoolean("silent"));
        if (yaml.contains("invulnerable")) settings.addInvulnerable(yaml.getBoolean("invulnerable"));
        if (yaml.contains("glowing")) settings.addGlowing(yaml.getBoolean("glowing"));

        builder.addEntitySettings(settings.build());
    }

    @Override
    public void load(JsonObject json, CustomEntity.Builder builder) {

    }
}
