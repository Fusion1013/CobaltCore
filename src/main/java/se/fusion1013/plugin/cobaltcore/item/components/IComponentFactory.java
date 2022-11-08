package se.fusion1013.plugin.cobaltcore.item.components;

import java.util.Map;

public interface IComponentFactory {

    IItemComponent createComponent(IComponentType componentType, Map<?, ?> data, String owningItemName);

    IItemComponent createComponent(String componentType, Map<?, ?> data, String owningItemName);

}
