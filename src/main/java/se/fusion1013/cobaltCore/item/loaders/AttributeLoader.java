package se.fusion1013.cobaltCore.item.loaders;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import se.fusion1013.cobaltCore.item.CobaltItem;

import java.util.List;
import java.util.Map;

public class AttributeLoader implements IItemLoaderComponent {

    @Override
    public void load(YamlConfiguration yaml, CobaltItem.Builder builder) {
        if (!yaml.contains("attributes")) return;
        FromMapList(yaml.getMapList("attributes"), builder);
    }

    private static void FromMapList(List<Map<?, ?>> mapList, CobaltItem.Builder builder) {
        for (Map<?, ?> map : mapList) FromMap(map, builder);
    }

    private static void FromMap(Map<?, ?> map, CobaltItem.Builder builder) {
        map.keySet().forEach(k -> {
            Map<?, ?> values = (Map<?, ?>) map.get(k);

            /*
            Attribute attribute = EnumUtils.findEnumInsensitiveCase(Attribute.class, (String) k);
            double amount = (double) values.get("amount");
            AttributeModifier.Operation operation = EnumUtils.findEnumInsensitiveCase(AttributeModifier.Operation.class, (String) values.get("operation"));
            List<String> equipmentSlots = (List<String>) values.get("equipment_slots");

            for (String s : equipmentSlots) {
                builder.attribute(attribute, new AttributeModifier(UUID.randomUUID(),  builder.internalName+ "_modifier", amount, operation, EnumUtils.findEnumInsensitiveCase(EquipmentSlot.class, s)));
            }
             */
        });
    }

    @Override
    public void load(JsonObject json, CobaltItem.Builder builder) {
    }
}