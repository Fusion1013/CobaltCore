package se.fusion1013.cobaltCore.database.system;

public interface IDao {
    DataStorageType getDataStorageType();
    void init();
    String getId();
    default int getVersion() { return 0; };

    default void update(int version) {}
}