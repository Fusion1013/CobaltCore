package se.fusion1013.cobaltCore.variable.provider;

import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import se.fusion1013.cobaltCore.util.AttributeContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AttributeValueProvider extends AbstractValueProvider<AttributeContainer> {

    private static final Random random = new Random();
    private final List<AttributeContainer> attributes = new ArrayList<>();

    public AttributeValueProvider(String parameterName, AttributeContainer value) {
        super(parameterName);
        attributes.add(value);
    }

    public AttributeValueProvider(String parameterName) {
        super(parameterName);
    }

    @Override
    public AttributeContainer getValue() {
        return attributes.get(random.nextInt(attributes.size()));
    }

    @Override
    public void setValue(AttributeContainer value) {
        attributes.clear();
        attributes.add(value);
    }

    @Override
    public void load(ConfigurationSection yaml) {
        if (!yaml.contains(parameterName)) return;

        attributes.addAll(fromMapList(yaml.getMapList(parameterName)));
    }

    @Override
    public void load(Map<?, ?> map) {
        // TODO
    }

    @Override
    public List<AttributeContainer> getValueList() {
        return attributes;
    }

    private static List<AttributeContainer> fromMapList(List<Map<?, ?>> mapList) {
        List<AttributeContainer> modifiers = new ArrayList<>();
        for (Map<?, ?> map : mapList) modifiers.addAll(fromMap(map));
        return modifiers;
    }

    private static List<AttributeContainer> fromMap(Map<?, ?> map) {
        if (map.containsKey("attribute")) {
            return List.of(new AttributeContainer(map));
        } else {
            List<AttributeContainer> attributes = new ArrayList<>();
            map.keySet().forEach(k -> {
                Map<?, ?> values = (Map<?, ?>) map.get(k);
                Attribute attribute = Registry.ATTRIBUTE.get(new NamespacedKey(Key.MINECRAFT_NAMESPACE, k.toString()));
                attributes.add(new AttributeContainer(attribute, values));
            });
            return attributes;
        }
    }
}
