package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public interface ICustomItem {

    // ----- GENERIC INFO GETTERS -----

    NamespacedKey getNamespacedKey();
    String getInternalName();
    ItemStack getItemStack();
    String[] getTags();

    // ----- ITEM COMPARISON -----

    /**
     * Gets whether the <code>ItemStack</code> is an instance of the <code>ICustomItem</code>.
     *
     * @param item the <code>ItemStack</code> to check.
     * @return whether the <code>ItemStack</code> is an instance of the <code>ICustomItem</code>.
     */
    boolean compareTo(ItemStack item);

    // ----- ITEM ACTIVATOR EVENTS -----

    void activatorTriggered(ItemActivator activator, Event event, EquipmentSlot slot);

    void activatorTriggered(ItemActivator activator, Event event);

}
