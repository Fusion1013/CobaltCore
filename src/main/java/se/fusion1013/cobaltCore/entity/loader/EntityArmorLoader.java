package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.entity.CustomEntity;
import se.fusion1013.cobaltCore.entity.settings.EntityArmorSettings;

public class EntityArmorLoader implements IEntityLoaderComponent {
    @Override
    public void load(YamlConfiguration yaml, CustomEntity.Builder builder) {
        EntityArmorSettings.Builder armorBuilder = new EntityArmorSettings.Builder();

        ConfigurationSection armorYaml = yaml.getConfigurationSection("armor");
        if (armorYaml == null) return;

        if (armorYaml.contains("helmet")) armorBuilder.addHelmet(armorYaml.getString("helmet"));
        if (armorYaml.contains("chestplate")) armorBuilder.addChestplate(armorYaml.getString("chestplate"));
        if (armorYaml.contains("leggings")) armorBuilder.addLeggings(armorYaml.getString("leggings"));
        if (armorYaml.contains("boots")) armorBuilder.addBoots(armorYaml.getString("boots"));
        if (armorYaml.contains("main_hand")) armorBuilder.addMainHand(armorYaml.getString("main_hand"));
        if (armorYaml.contains("off_hand")) armorBuilder.addOffHand(armorYaml.getString("off_hand"));

        builder.addEntitySettings(armorBuilder.build());
    }

    @Override
    public void load(JsonObject json, CustomEntity.Builder builder) {

    }
}
