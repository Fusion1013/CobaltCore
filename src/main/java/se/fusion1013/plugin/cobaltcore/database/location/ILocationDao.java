package se.fusion1013.plugin.cobaltcore.database.location;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.database.system.IDao;

import java.util.UUID;

public interface ILocationDao extends IDao {

    /**
     * Removes a location from the database.
     *
     * @param uuid the unique identifier of the location.
     */
    void removeLocation(UUID uuid);

    /**
     * Removes a location from the database.
     *
     * @param uuid the unique identifier of the location.
     */
    void removeLocationSync(UUID uuid);

    /**
     * Inserts a location into the database.
     *
     * @param uuid the unique identifier of the location.
     * @param location the location to insert.
     */
    void insertLocation(UUID uuid, Location location);

    /**
     * Inserts a location into the database.
     *
     * @param uuid the unique identifier of the location.
     * @param location the location to insert.
     */
    void insertLocationSync(UUID uuid, Location location);

    @Override
    default String getId() { return "location"; }
}
