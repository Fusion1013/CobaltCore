package se.fusion1013.plugin.cobaltcore.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface IItemActivatorExecutor {

    void execute(ICustomItem item, Event event);

}
