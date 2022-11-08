package se.fusion1013.plugin.cobaltcore.item.components;

import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.action.CoreActionFactory;
import se.fusion1013.plugin.cobaltcore.action.system.IActionFactory;
import se.fusion1013.plugin.cobaltcore.manager.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComponentManager extends Manager {

    // ----- VARIABLES -----

    // -- Factories
    private static final List<IComponentFactory> REGISTERED_FACTORIES = new ArrayList<>();
    private static final IComponentFactory CORE_FACTORY = registerFactory(new CoreComponentFactory());

    // ----- REGISTER -----

    public static IComponentFactory registerFactory(IComponentFactory factory) {
        REGISTERED_FACTORIES.add(factory);
        return factory;
    }

    // ----- GETTERS / SETTERS -----

    public static List<IItemComponent> getComponents(List<Map<?, ?>> componentList, String owningItem) {
        List<IItemComponent> foundComponents = new ArrayList<>();

        for (Map<?, ?> componentMap : componentList) {
            componentMap.keySet().forEach(k -> {
                String componentTypeString = (String) k;
                Map<?, ?> data = (Map<?, ?>) componentMap.get(k);
                IItemComponent component = getComponent(componentTypeString, data, owningItem);
                foundComponents.add(component);
            });
        }

        return foundComponents;
    }

    public static IItemComponent getComponent(String componentType, Map<?, ?> data, String owningItem) {
        for (IComponentFactory factory : REGISTERED_FACTORIES) {
            IItemComponent component = factory.createComponent(componentType, data, owningItem);
            if (component != null) return component;
        }

        return null;
    }

    public static IItemComponent getComponent(IComponentType componentType, Map<?, ?> data, String owningItem) {
        for (IComponentFactory factory : REGISTERED_FACTORIES) {
            IItemComponent component = factory.createComponent(componentType, data, owningItem);
            if (component != null) return component;
        }

        return null;
    }

    // ----- CONSTRUCTORS -----

    public ComponentManager(CobaltCore cobaltCore) {
        super(cobaltCore);
    }

    // ----- RELOADING / DISABLING -----

    @Override
    public void reload() {

    }

    @Override
    public void disable() {

    }
}
