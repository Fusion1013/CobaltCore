package se.fusion1013.cobaltCore.item.properties;

import com.google.gson.JsonObject;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.item.AbstractCobaltItem;
import se.fusion1013.cobaltCore.loader.AbstractObjectProperties;
import se.fusion1013.cobaltCore.variable.AttributeVariable;

public class ItemAttributeProperty extends AbstractObjectProperties<ItemCreationContext, AbstractCobaltItem> {

    private final AttributeVariable attributes = new AttributeVariable("attributes");
//    private final Map<Attribute, AttributeModifier> attributes = new HashMap<>();

    @Override
    public String getId() {
        return "item_attribute";
    }

    @Override
    public void create(ItemCreationContext obj) {
//        for (Attribute attribute : attributes.keySet()) {
//            obj.itemMeta.addAttributeModifier(attribute, attributes.get(attribute));
//        }
        attributes.applyToItem(obj);
    }

    @Override
    public void fromJson(JsonObject json, AbstractCobaltItem builder) {

    }

    @Override
    public void saveJson(JsonObject json) {
    }

    @Override
    public void fromYaml(ConfigurationSection yaml, AbstractCobaltItem builder) {
        attributes.load(yaml);

//        if (!yaml.contains("attributes")) return;
//        attributes.putAll(fromMapList(yaml.getMapList("attributes")));
    }

//    private static Map<Attribute, AttributeModifier> fromMapList(List<Map<?, ?>> mapList) {
//        Map<Attribute, AttributeModifier> modifiers = new HashMap<>();
//        for (Map<?, ?> map : mapList) modifiers.putAll(fromMap(map));
//        return modifiers;
//    }
//
//    private static Map<Attribute, AttributeModifier> fromMap(Map<?, ?> map) {
//        Map<Attribute, AttributeModifier> modifiers = new HashMap<>();
//        map.keySet().forEach(k -> {
//            Map<?, ?> values = (Map<?, ?>) map.get(k);
//
//            Attribute attribute = Registry.ATTRIBUTE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, k.toString()));
//
//            double amount = (double) values.get("amount");
//            AttributeModifier.Operation operation = EnumUtils.findEnumInsensitiveCase(AttributeModifier.Operation.class, (String) values.get("operation"));
//            List<String> equipmentSlots = (List<String>) values.get("equipment_slots");
//
//            for (String s : equipmentSlots) {
//                EquipmentSlot slot = EnumUtils.findEnumInsensitiveCase(EquipmentSlot.class, s);
//                AttributeModifier modifier = new AttributeModifier(new NamespacedKey(CobaltCore.getInstance(), UUID.randomUUID().toString()), amount, operation, slot.getGroup());
//                modifiers.put(attribute, modifier);
//            }
//        });
//        return modifiers;
//    }

    @Override
    public void saveYaml(ConfigurationSection yaml) {
//        List<Map<String, Object>> list = new ArrayList<>();
//        for (Attribute attribute : attributes.keySet()) {
//            AttributeModifier modifier = attributes.get(attribute);
//            Map<String, Object> modifierMap = new HashMap<>();
//            modifierMap.put(attribute.key().asString(), Map.of(
//                    "amount", modifier.getAmount(),
//                    "operation", modifier.getOperation().name(),
//                    "equipment_slots", List.of(modifier.getSlotGroup().toString())
//            ));
//            list.add(modifierMap);
//        }
//        yaml.set("attributes", list);
    }
}
