package se.fusion1013.plugin.cobaltcore.item.components;

import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import se.fusion1013.plugin.cobaltcore.item.CustomItemManager;
import se.fusion1013.plugin.cobaltcore.item.IItemActivatorExecutor;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractItemComponent implements IItemComponent {

    // ----- VARIABLES -----

    protected String owningItem;
    // TODO: private List<IItemComponent> requiredComponents = new ArrayList<>();
    private Map<ItemActivator, IItemActivatorExecutor> eventActivators = new HashMap<>();

    // ----- CONSTRUCTORS -----

    public AbstractItemComponent(String owningItem) {
        this.owningItem = owningItem;

        // Register events
        eventActivators.putAll(registerEvents());
    }

    protected AbstractItemComponent(String owningItem, Map<?, ?> data) {
        this.owningItem = owningItem;
    }

    // ----- ACTIVATION -----

    @Override
    public <T extends Event> void onEvent(ItemActivator activator, T event, EquipmentSlot slot) {
        IItemActivatorExecutor executor = eventActivators.get(activator);
        if (executor != null) executor.execute(CustomItemManager.getCustomItem(owningItem), event, slot);
    }

    // ----- ABSTRACT BUILDER -----

    public static abstract class Builder<T extends AbstractItemComponent, B extends Builder> {

        // ----- VARIABLES -----

        protected String owningItem;

        // ----- CONSTRUCTORS -----

        public Builder() {}

        // ----- CREATION METHODS -----

        public T build() {
            return createObj();
        }

        protected abstract T createObj();
        protected abstract B getThis();
    }

    // ----- GETTERS / SETTERS -----

    @Override
    public void setOwningItem(String item) {
        this.owningItem = item;
    }
}
