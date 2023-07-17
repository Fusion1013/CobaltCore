package se.fusion1013.plugin.cobaltcore.action.encounter;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public interface IEncounter {

    /**
     * Attempt to trigger the <code>Encounter</code> at the given <code>Location</code>.
     * @param location the <code>Location</code> to trigger the <code>Encounter</code> at.
     * @return The task that was created.
     */
    BukkitTask trigger(Location location, String id);

}
