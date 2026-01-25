package se.fusion1013.cobaltCore.entity.loader;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.entity.CustomEntity;
import se.fusion1013.cobaltCore.entity.settings.EntityEquipmentSettings;

public class EntityEquipmentLoader implements IEntityLoaderComponent {
    @Override
    public void load(YamlConfiguration yaml, CustomEntity.Builder builder) {
        EntityEquipmentSettings.Builder armorBuilder = new EntityEquipmentSettings.Builder();

        ConfigurationSection equipmentYaml = yaml.getConfigurationSection("equipment");
        if (equipmentYaml == null) return;

        if (equipmentYaml.contains("helmet")) armorBuilder.addHelmet(equipmentYaml.getString("helmet"));
        if (equipmentYaml.contains("chestplate")) armorBuilder.addChestplate(equipmentYaml.getString("chestplate"));
        if (equipmentYaml.contains("leggings")) armorBuilder.addLeggings(equipmentYaml.getString("leggings"));
        if (equipmentYaml.contains("boots")) armorBuilder.addBoots(equipmentYaml.getString("boots"));
        if (equipmentYaml.contains("mainhand")) armorBuilder.addMainHand(equipmentYaml.getString("mainhand"));
        if (equipmentYaml.contains("offhand")) armorBuilder.addOffHand(equipmentYaml.getString("offhand"));

        builder.addEntitySettings(armorBuilder.build());
    }

    @Override
    public void load(JsonObject json, CustomEntity.Builder builder) {

    }
}
