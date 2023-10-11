package se.fusion1013.plugin.cobaltcore.database.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.database.system.implementations.SQLiteImplementation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStorageDaoSQLite extends Dao implements IPlayerDataStorageDao {

    //region FIELDS

    private static final String SQLiteCreatePlayerDataStorageTable = "CREATE TABLE IF NOT EXISTS player_data_storage (" +
            "`player_uuid` varchar(36)," +
            "`content` TEXT NOT NULL," +
            "PRIMARY KEY (`player_uuid`)" +
            ");";

    //endregion

    //region REMOVAL

    @Override
    public void removePlayerDataStorageAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> removePlayerDataStorageSync(uuid));
    }

    @Override
    public void removePlayerDataStorageSync(UUID uuid) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(connection -> {
            try (
                    PreparedStatement ps = connection.prepareStatement("DELETE FROM player_data_storage WHERE player_uuid = ?")
            ) {
                ps.setString(1, uuid.toString());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    //endregion

    //region INSERTION

    @Override
    public void insertPlayerDataStorageAsync(UUID uuid, String data) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> insertPlayerDataStorageSync(uuid, data));
    }

    @Override
    public void insertPlayerDataStorageSync(UUID uuid, String data) {
        SQLiteImplementation.performThreadSafeSQLiteOperations(connection -> {
            try (
                    PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO player_data_storage(player_uuid, content) VALUES(?, ?)")
            ) {
                ps.setString(1, uuid.toString());
                ps.setString(2, data);
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    //endregion

    //region GETTERS/SETTERS

    @Override
    public JsonObject getPlayerData(UUID playerUuid) {
        Map<UUID, JsonObject> map = new HashMap<>();

        SQLiteImplementation.performThreadSafeSQLiteOperations(connection -> {
            try (
                    PreparedStatement ps = connection.prepareStatement("SELECT * FROM player_data_storage WHERE player_uuid = ?")
            ) {
                ps.setString(1, playerUuid.toString());
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Gson gson = new Gson();
                    JsonObject content = gson.fromJson(rs.getString("content"), JsonObject.class);
                    map.put(playerUuid, content);
                    rs.close();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return map.get(playerUuid);
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        SQLiteImplementation.getSqliteDb().executeString(SQLiteCreatePlayerDataStorageTable);
    }

    //endregion

}
