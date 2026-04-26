package se.fusion1013.cobaltCore.variable.provider.item;

import se.fusion1013.cobaltCore.item.properties.ItemCreationContext;
import se.fusion1013.cobaltCore.item.properties.ItemVisual;
import se.fusion1013.cobaltCore.variable.provider.LoadedValueProvider;

public class ItemVisualValueProvider extends LoadedValueProvider<ItemVisual> {

    public ItemVisualValueProvider(String parameterName, ItemVisual defaultValue) {
        super(parameterName, defaultValue, ItemVisual::new, ItemVisual::new, ItemVisual::new);
    }

    public ItemVisualValueProvider(String parameterName) {
        super(parameterName, ItemVisual::new, ItemVisual::new, ItemVisual::new);
    }

    public void create(ItemCreationContext obj) {
        ItemVisual value = getValue();
        if (value != null) value.create(obj);
    }
}
