package se.fusion1013.plugin.cobaltcore.world.structure;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import se.fusion1013.plugin.cobaltcore.world.structure.structure.StructureInstance;

import java.util.List;

public class StructureEvents implements Listener {

    // ----- STRUCTURE EVENT HANDLING ----

    @EventHandler
    public void onEvent(Event event) {
        List<StructureInstance> loadedStructures = StructureManager.getLoadedStructures();
        for (StructureInstance instance : loadedStructures) instance.structure().executeEvent(event, instance.location());
    }
}
