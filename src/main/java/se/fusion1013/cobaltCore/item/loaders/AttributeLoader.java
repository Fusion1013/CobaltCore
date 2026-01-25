package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.CobaltItem;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttributeLoader implements IItemLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("attributes")) return;
        fromMapList(yaml.getMapList("attributes"), builder);
    }

    private static void fromMapList(List<Map<?, ?>> mapList, CobaltItem.Builder builder) {
        for (Map<?, ?> map : mapList) fromMap(map, builder);
    }

    private static void fromMap(Map<?, ?> map, CobaltItem.Builder builder) {
        map.keySet().forEach(k -> {
            Map<?, ?> values = (Map<?, ?>) map.get(k);

            Attribute attribute = Registry.ATTRIBUTE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, k.toString()));

            double amount = (double) values.get("amount");
            AttributeModifier.Operation operation = EnumUtils.findEnumInsensitiveCase(AttributeModifier.Operation.class, (String) values.get("operation"));
            List<String> equipmentSlots = (List<String>) values.get("equipment_slots");

            for (String s : equipmentSlots) {
                EquipmentSlot slot = EnumUtils.findEnumInsensitiveCase(EquipmentSlot.class, s);
                AttributeModifier modifier = new AttributeModifier(new NamespacedKey(CobaltCore.getInstance(), UUID.randomUUID().toString()), amount, operation, slot.getGroup());
                builder.attribute(attribute, modifier);
            }
        });
    }

    @Override
    public void load(JsonObject json, CobaltItem.Builder builder) {
    }
}