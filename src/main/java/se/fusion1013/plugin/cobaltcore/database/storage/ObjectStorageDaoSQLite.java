package se.fusion1013.plugin.cobaltcore.database.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.storage.IStorageObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ObjectStorageDaoSQLite extends Dao implements IObjectStorageDao {

    // ----- TABLE & VIEW STRINGS -----

    private static final String SQLiteCreateObjectStorageTable = "CREATE TABLE IF NOT EXISTS object_storage (" +
            "`uuid` varchar(36)," +
            "`chunk_world_key` TEXT," +
            "`object_type` TEXT," +
            "`content` TEXT NOT NULL," +
            "PRIMARY KEY (`uuid`, `object_type`)" +
            ");";

    // ----- REMOVAL METHODS -----

    @Override
    public void removeJsonStorageAsync(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> removeJsonStorageSync(uuid));
    }

    @Override
    public void removeJsonStorageSync(UUID uuid) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM object_storage WHERE uuid = ?")
            ) {
                ps.setString(1, uuid.toString());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    // ----- INSERTION METHODS -----

    @Override
    public void insertJsonStorageAsync(UUID uuid, String chunkWorldKey, IStorageObject storage) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> insertJsonStorageSync(uuid, chunkWorldKey, storage));
    }

    @Override
    public void insertJsonStorageSync(UUID uuid, String chunkWorldKey, IStorageObject storage) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO object_storage(uuid, chunk_world_key, object_type, content) VALUES(?, ?, ?, ?)")
            ) {
                ps.setString(1, uuid.toString());
                ps.setString(2, chunkWorldKey);
                ps.setString(3, storage.getObjectIdentifier());
                ps.setString(4, storage.toJson().toString());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    // ----- GETTERS -----


    @Override
    public Map<String, JsonObject> getJsonStorageInChunk(String chunkWorldKey) {
        // <StorageIdentifier, <StorageUUID, StorageObject>>
        Map<String, JsonObject> jsonObjectMap = new HashMap<>();

        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM object_storage WHERE chunk_world_key = ?")
            ) {
                ps.setString(1, chunkWorldKey);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Gson gson = new Gson();
                    String objectType = rs.getString("object_type");
                    JsonObject content = gson.fromJson(rs.getString("content"), JsonObject.class);
                    jsonObjectMap.put(objectType, content);
                }

                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return jsonObjectMap;
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        getDataManager().getSqliteDb().executeString(SQLiteCreateObjectStorageTable);
    }
}
