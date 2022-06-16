package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;

public interface IItemActivatorExecutor {

    void execute(ICustomItem item, Event event, EquipmentSlot slot);

}
