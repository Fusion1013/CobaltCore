package se.fusion1013.plugin.cobaltcore.database.system;

public interface ISystemDao extends IDao {

    int getVersion(String id, int internalVersion);
    void setVersion(String id, int version);

    @Override
    default String getId() {
        return "system";
    }

}
