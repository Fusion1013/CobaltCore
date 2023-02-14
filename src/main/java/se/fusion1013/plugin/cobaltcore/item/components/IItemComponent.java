package se.fusion1013.plugin.cobaltcore.item.components;

import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import se.fusion1013.plugin.cobaltcore.item.IItemActivatorExecutor;
import se.fusion1013.plugin.cobaltcore.item.ItemActivator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IItemComponent {

    // ----- GETTERS / SETTERS -----

    /**
     * The internal name of the <code>IItemComponent</code>.
     *
     * @return the internal name.
     */
    String getInternalName();

    /**
     * Gets a <code>List</code> of extra lore from the <code>IItemComponent</code>.
     *
     * @return a <code>List</code> of lore strings.
     */
    default List<String> getLore() { return new ArrayList<>(); }

    void setOwningItem(String item);

    // ----- ITEM CONSTRUCTION -----

    /**
     * Called when the <code>ItemStack</code> is being constructed.
     *
     * @param stack the <code>ItemStack</code>.
     * @param meta the <code>ItemMeta</code>.
     * @param persistentDataContainer the <code>PersistentDataContainer</code> of the item.
     */
    default void onItemConstruction(ItemStack stack, ItemMeta meta, PersistentDataContainer persistentDataContainer) {}

    // ----- LOADING / DISABLING -----

    /**
     * Called when a <code>ICustomItem</code> is loaded into the system.
     */
    default void onLoad() {}

    /**
     * Called when a <code>ICustomItem</code> is removed from the system.
     */
    default void onDisable() {}

    // ----- VALUE LOADING -----

    @Deprecated
    default void loadValues(Map<?, ?> values) {}

    // ----- EVENTS -----

    <T extends Event> void onEvent(ItemActivator activator, T event, EquipmentSlot slot);

    default Map<ItemActivator, IItemActivatorExecutor> registerEvents() {
        return new HashMap<>();
    }

}
