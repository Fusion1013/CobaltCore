package se.fusion1013.plugin.cobaltcore.world.structure;

import org.bukkit.event.Event;

public interface IStructureEventHandler<T extends Event> {

    void execute(T event);

}
