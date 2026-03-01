package se.fusion1013.cobaltCore.variable;

import dev.jorel.commandapi.arguments.Argument;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.util.AttributeContainer;
import se.fusion1013.cobaltCore.variable.provider.AttributeValueProvider;

public class AttributeVariable extends AbstractVariable<AttributeContainer, AttributeValueProvider, Argument<AttributeContainer>, AttributeVariable> {

    public AttributeVariable(String name) {
        super(name, new AttributeValueProvider(name));
    }

    public AttributeVariable(String name, AttributeContainer defaultValue) {
        super(name, new AttributeValueProvider(name, defaultValue));
    }

    public void applyToItem(ItemCreationContext context) {
        for (AttributeContainer attribute : getValueList()) {
            attribute.apply(context);
        }
    }

    @Override
    public AttributeVariable getSelf() {
        return this;
    }

    @Override
    public Argument<AttributeContainer> getArgument() {
        return null;
    }
}
