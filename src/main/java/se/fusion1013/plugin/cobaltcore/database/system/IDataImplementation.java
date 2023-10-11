package se.fusion1013.plugin.cobaltcore.database.system;

import se.fusion1013.plugin.cobaltcore.CobaltCore;

public interface IDataImplementation {

    void loadConnection();

    DataManager.StorageType type();

    void initDao(DataManager manager);

    default void onReload() {}

    default void onDisable() {}

    default DataManager dataManager() {
        return CobaltCore.getInstance().getManager(CobaltCore.getInstance(), DataManager.class);
    }

}
