package se.fusion1013.plugin.cobaltcore.database.system;

import se.fusion1013.plugin.cobaltcore.database.system.DataManager;

public interface IDao {
    DataManager.StorageType getStorageType();
    void init();
    String getId();
    default int getVersion() { return 0; };

    default void update(int version) {}
}
