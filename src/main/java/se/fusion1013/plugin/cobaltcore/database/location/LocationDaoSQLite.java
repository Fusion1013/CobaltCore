package se.fusion1013.plugin.cobaltcore.database.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class LocationDaoSQLite extends Dao implements ILocationDao {

    public static String SQLiteCreatePlayerLocationsTable = "CREATE TABLE IF NOT EXISTS locations (" +
            "`uuid` varchar(36) NOT NULL," +
            "`world` varchar(32) NOT NULL," +
            "`x_pos` REAL NOT NULL," +
            "`y_pos` REAL NOT NULL," +
            "`z_pos` REAL NOT NULL," +
            "`yaw` REAL NOT NULL," +
            "`pitch` REAL NOT NULL," +
            "PRIMARY KEY (`uuid`)" +
            ");";

    @Override
    @Deprecated
    public void removeLocation(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            removeLocationSync(uuid);
        });
    }

    @Override
    @Deprecated
    public void removeLocationSync(UUID uuid) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM locations WHERE uuid = ?")
            ) {
                ps.setString(1, uuid.toString());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    @Deprecated
    public void insertLocation(UUID uuid, Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            insertLocationSync(uuid, location);
        });
    }

    @Override
    @Deprecated
    public void insertLocationSync(UUID uuid, Location location) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO locations(uuid, world, x_pos, y_pos, z_pos, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)")
            ) {
                ps.setString(1, uuid.toString());
                ps.setString(2, location.getWorld().getName());
                ps.setDouble(3, location.getX());
                ps.setDouble(4, location.getY());
                ps.setDouble(5, location.getZ());
                ps.setDouble(6, location.getYaw());
                ps.setDouble(7, location.getPitch());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreatePlayerLocationsTable);
    }
}
