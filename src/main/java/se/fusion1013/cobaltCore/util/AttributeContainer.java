package se.fusion1013.cobaltCore.util;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.yaml.snakeyaml.util.EnumUtils;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.variable.DoubleVariable;
import se.fusion1013.cobaltCore.variable.LiteralVariable;
import se.fusion1013.cobaltCore.variable.StringVariable;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class AttributeContainer {

    private final Attribute attribute;
    private final DoubleVariable amount = new DoubleVariable("amount");
    private final LiteralVariable operation = new LiteralVariable("operation", Arrays.stream(AttributeModifier.Operation.values()).map(Enum::toString).toArray(String[]::new));
    private final StringVariable equipmentSlots = new StringVariable("equipment_slots");

    public AttributeContainer(Map<?, ?> map) {
        attribute = Registry.ATTRIBUTE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, (String) map.get("attribute")));
        loadMap(map);
    }

    public AttributeContainer(Attribute attribute, Map<?, ?> map) {
        this.attribute = attribute;
        loadMap(map);
    }

    private void loadMap(Map<?, ?> map) {
        amount.load(map);
        operation.load(map);
        equipmentSlots.load(map);
    }

    public void apply(ItemCreationContext context) {
        AttributeModifier.Operation op = EnumUtils.findEnumInsensitiveCase(AttributeModifier.Operation.class, operation.getValue());
        for (String s : equipmentSlots.getValueList()) {
            EquipmentSlot slot = EnumUtils.findEnumInsensitiveCase(EquipmentSlot.class, s);
            AttributeModifier modifier = new AttributeModifier(new NamespacedKey(CobaltCore.getInstance(), UUID.randomUUID().toString()), amount.getValue(), op, slot.getGroup());
            context.itemMeta.addAttributeModifier(attribute, modifier);
        }
    }
}
