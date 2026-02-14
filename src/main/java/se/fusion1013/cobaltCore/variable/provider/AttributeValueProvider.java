package se.fusion1013.cobaltCore.variable.provider;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.CobaltCore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AttributeValueProvider extends AbstractValueProvider<Map<Attribute, AttributeModifier>> {

    private final Map<Attribute, AttributeModifier> attributes = new HashMap<>();

    public AttributeValueProvider(String parameterName, Map<Attribute, AttributeModifier> value) {
        super(parameterName);
        attributes.putAll(value);
    }

    public AttributeValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public Map<Attribute, AttributeModifier> getValue() {
        return attributes;
    }

    @Override
    public void setValue(Map<Attribute, AttributeModifier> value) {
        attributes.clear();
        attributes.putAll(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;
        attributes.putAll(fromMapList(yaml.getMapList(parameterName)));
    }

    @Override
    public void load(Map<?, ?> map) {
        // TODO
    }

    private static Map<Attribute, AttributeModifier> fromMapList(List<Map<?, ?>> mapList) {
        Map<Attribute, AttributeModifier> modifiers = new HashMap<>();
        for (Map<?, ?> map : mapList) modifiers.putAll(fromMap(map));
        return modifiers;
    }

    private static Map<Attribute, AttributeModifier> fromMap(Map<?, ?> map) {
        Map<Attribute, AttributeModifier> modifiers = new HashMap<>();
        map.keySet().forEach(k -> {
            Map<?, ?> values = (Map<?, ?>) map.get(k);

            Attribute attribute = Registry.ATTRIBUTE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, k.toString()));

            double amount = (double) values.get("amount");
            AttributeModifier.Operation operation = EnumUtils.findEnumInsensitiveCase(AttributeModifier.Operation.class, (String) values.get("operation"));
            List<String> equipmentSlots = (List<String>) values.get("equipment_slots");

            for (String s : equipmentSlots) {
                EquipmentSlot slot = EnumUtils.findEnumInsensitiveCase(EquipmentSlot.class, s);
                AttributeModifier modifier = new AttributeModifier(new NamespacedKey(CobaltCore.getInstance(), UUID.randomUUID().toString()), amount, operation, slot.getGroup());
                modifiers.put(attribute, modifier);
            }
        });
        return modifiers;
    }
}
