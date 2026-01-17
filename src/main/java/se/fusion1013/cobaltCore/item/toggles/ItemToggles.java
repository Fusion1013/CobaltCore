package se.fusion1013.cobaltCore.item.toggles;

import java.util.HashMap;
import java.util.Map;

public class ItemToggles implements IItemToggles {

    private final Map<ItemToggleType, Boolean> values = new HashMap<>();

    @Override
    public boolean getValue(ItemToggleType type) {
        return values.getOrDefault(type, false);
    }

    @Override
    public void setValue(ItemToggleType type, boolean value) {
        values.put(type, value);
    }
}
