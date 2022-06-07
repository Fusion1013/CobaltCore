package se.fusion1013.plugin.cobaltcore.database.spawner;

import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.world.spawner.CustomSpawner;

import java.util.Map;
import java.util.UUID;

public interface ICustomSpawnerDao extends IDao {

    Map<Long, Map<Location, CustomSpawner>> getCustomSpawners();

    void saveCustomSpawners(Map<Long, Map<Location, CustomSpawner>> spawners);

    void removeCustomSpawner(UUID uuid);

}
