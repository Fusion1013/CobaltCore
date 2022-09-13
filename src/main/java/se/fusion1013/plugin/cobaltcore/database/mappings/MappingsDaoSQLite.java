package se.fusion1013.plugin.cobaltcore.database.mappings;

import org.bukkit.Bukkit;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.advancement.CobaltAdvancementManager;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MappingsDaoSQLite extends Dao implements IMappingsDao {

    public static final String SQLiteCreateMappingsTable = "CREATE TABLE IF NOT EXISTS mappings (" +
            "`type` TEXT," + // Used for getting multiple objects at once
            "`uuid` varchar(36)," +
            "`mapping` TEXT," +
            "PRIMARY KEY (`type`,`uuid`)" +
            ");";

    @Override
    public void insertMappingSync(String type, UUID id, String mapping) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO mappings(type, uuid, mapping) VALUES(?,?,?)")
            ) {
                ps.setString(1, type);
                ps.setString(2, id.toString());
                ps.setString(3, mapping);
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void insertMappingAsync(String type, UUID id, String mapping) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            insertMappingSync(type, id, mapping);
        });
    }

    @Override
    public void removeMappingSync(UUID id) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM mappings WHERE uuid = ?")
            ) {
                ps.setString(1, id.toString());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void removeMappingAsync(UUID id) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> removeMappingSync(id));
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        getDataManager().getSqliteDb().executeString(SQLiteCreateMappingsTable);
    }
}
