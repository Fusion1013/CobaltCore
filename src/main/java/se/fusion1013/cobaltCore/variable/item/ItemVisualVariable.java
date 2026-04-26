package se.fusion1013.cobaltCore.variable.item;

import dev.jorel.commandapi.arguments.Argument;
import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.item.properties.ItemVisual;
import se.fusion1013.cobaltCore.variable.AbstractVariable;
import se.fusion1013.cobaltCore.variable.provider.item.ItemVisualValueProvider;

public class ItemVisualVariable extends AbstractVariable<ItemVisual, ItemVisualValueProvider, Argument<ItemVisual>, ItemVisualVariable> {

    public ItemVisualVariable(String name, ItemVisual defaultValue) {
        super(name, new ItemVisualValueProvider(name, defaultValue));
    }

    public ItemVisualVariable(String name) {
        super(name, new ItemVisualValueProvider(name));
    }

    public void create(ItemCreationContext obj) {
        valueGetter.create(obj);
    }

    @Override
    public ItemVisualVariable getSelf() {
        return this;
    }

    @Override
    public Argument<ItemVisual> getArgument() {
        return null;
    }
}
