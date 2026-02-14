package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.variable.provider.AttributeValueProvider;

import java.util.Map;

public class AttributeVariable extends AbstractVariable<Map<Attribute, AttributeModifier>, AttributeValueProvider, Argument<Map<Attribute, AttributeModifier>>, AttributeVariable> {

    public AttributeVariable(String name) {
        super(name, new AttributeValueProvider(name));
    }

    public AttributeVariable(String name, Map<Attribute, AttributeModifier> defaultValue) {
        super(name, new AttributeValueProvider(name, defaultValue));
    }

    public void applyToItem(ItemCreationContext context) {
        for (Attribute attribute : getValue().keySet()) {
            context.itemMeta.addAttributeModifier(attribute, getValue().get(attribute));
        }
    }

    @Override
    public AttributeVariable getSelf() {
        return this;
    }

    @Override
    public Argument<Map<Attribute, AttributeModifier>> getArgument() {
        return null;
    }
}
