package se.fusion1013.plugin.cobaltcore.database.mappings;

import se.fusion1013.plugin.cobaltcore.database.system.IDao;

import java.util.UUID;

public interface IMappingsDao extends IDao {



    void insertMappingSync(String type, UUID id, String mapping);

    void insertMappingAsync(String type, UUID id, String mapping);

    void removeMappingSync(UUID id);

    void removeMappingAsync(UUID id);

    @Override
    default String getId() { return "mappings"; }

}