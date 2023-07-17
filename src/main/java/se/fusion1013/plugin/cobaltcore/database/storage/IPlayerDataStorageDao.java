package se.fusion1013.plugin.cobaltcore.database.storage;

import com.google.gson.JsonObject;
import se.fusion1013.plugin.cobaltcore.database.system.IDao;

import java.util.Map;
import java.util.UUID;

public interface IPlayerDataStorageDao extends IDao {

    //region REMOVE

    void removePlayerDataStorageAsync(UUID uuid);
    void removePlayerDataStorageSync(UUID uuid);

    //endregion

    //region INSERT

    void insertPlayerDataStorageAsync(UUID uuid, String data);
    void insertPlayerDataStorageSync(UUID uuid, String data);

    //endregion

    //region GETTERS/SETTERS

    JsonObject getPlayerData(UUID playerUuid);

    @Override
    default String getId() { return "player_data_storage"; }

    //endregion

}
