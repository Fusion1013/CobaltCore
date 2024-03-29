package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import se.fusion1013.plugin.cobaltcore.item.section.ItemSection;
import se.fusion1013.plugin.cobaltcore.util.INameProvider;

public interface ICustomItem extends INameProvider {

    // ----- GENERIC INFO GETTERS -----

    NamespacedKey getNamespacedKey();
    String getInternalName();
    ItemStack getItemStack();
    ItemSection getItemCategory();
    String[] getTags();
    default void onDisable() {};

    // ----- ITEM COMPARISON -----

    /**
     * Gets whether the <code>ItemStack</code> is an instance of the <code>ICustomItem</code>.
     *
     * @param item the <code>ItemStack</code> to check.
     * @return whether the <code>ItemStack</code> is an instance of the <code>ICustomItem</code>.
     */
    boolean compareTo(ItemStack item);

    // ----- ITEM ACTIVATOR EVENTS -----

    <T extends Event> void activatorTriggeredAsync(ItemActivator activator, T event, EquipmentSlot slot);

    <T extends Event> void activatorTriggeredAsync(ItemActivator activator, T event);

    <T extends Event> void activatorTriggeredSync(ItemActivator activator, T event, EquipmentSlot slot);

    <T extends Event> void activatorTriggeredSync(ItemActivator activator, T event);

}
