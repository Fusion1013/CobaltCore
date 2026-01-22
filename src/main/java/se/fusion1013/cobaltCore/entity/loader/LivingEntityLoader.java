package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.entity.CustomEntity;

public class LivingEntityLoader implements IEntityLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CustomEntity.Builder builder) {
        double maxHealth = yaml.getDouble("max_health");
    }

    @Override
    public void load(JsonObject json, CustomEntity.Builder builder) {
        double maxHealth = json.get("max_health").getAsDouble();
    }
}
