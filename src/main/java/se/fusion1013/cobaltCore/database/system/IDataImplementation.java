package se.fusion1013.cobaltCore.database.system;

import se.fusion1013.cobaltCore.CobaltCore;

public interface IDataImplementation {

    void loadConnection();

    DataStorageType type();

    void initDao(DataManager manager);

    default void onReload() {}

    default void onDisable() {}

    default DataManager dataManager() {
        return CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class);
    }

}