package se.fusion1013.plugin.cobaltcore.item.components;

import java.util.Map;

public class CoreComponentFactory implements IComponentFactory {

    @Override
    public IItemComponent createComponent(IComponentType componentType, Map<?, ?> data, String owningItemName) {
        if (componentType == CoreComponentType.ACTION_BAR_COMPONENT) return new ActionbarComponent(owningItemName, data);
        else if (componentType == CoreComponentType.CHARGE_COMPONENT) return new ChargeComponent(owningItemName, data);

        return null;
    }

    @Override
    public IItemComponent createComponent(String componentType, Map<?, ?> data, String owningItemName) {
        if (componentType.equalsIgnoreCase("actionbar_component")) return new ActionbarComponent(owningItemName, data);
        else if (componentType.equalsIgnoreCase("charge_component")) return new ChargeComponent(owningItemName, data);

        return null;
    }
}
