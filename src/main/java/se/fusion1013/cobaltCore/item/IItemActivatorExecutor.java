package se.fusion1013.cobaltCore.item;

import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;

public interface IItemActivatorExecutor {
    void execute(ICustomItem item, Event event, EquipmentSlot slot);
}
