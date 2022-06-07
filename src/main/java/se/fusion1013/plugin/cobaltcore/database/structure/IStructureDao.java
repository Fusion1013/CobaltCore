package se.fusion1013.plugin.cobaltcore.database.structure;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;
import se.fusion1013.plugin.cobaltcore.util.LocationUUID;

import java.util.Map;

public interface IStructureDao extends IDao {

    Map<Long, Map<LocationUUID, String>> getStructures();

    void saveStructures(Map<Long, Map<LocationUUID, String>> structures);

}
