package se.fusion1013.plugin.cobaltcore.database.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemDaoSQLite extends Dao implements ISystemDao {

    // ----- VARIABLES -----

    public static String SQLiteCreateVersionTable = "CREATE TABLE IF NOT EXISTS versions (" +
            "`identifier` varchar(36)," +
            "`version` INTEGER," +
            "PRIMARY KEY (`identifier`)" +
            ");";

    // ----- METHODS ------

    @Override
    public int getVersion(String id, int internalVersion) {
        try (
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps1 = conn.prepareStatement("INSERT OR IGNORE INTO versions(identifier, version) VALUES(?, ?)");
                PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM versions WHERE identifier = ?");
        ) {
            conn.setAutoCommit(false);

            // Inserts the internal version if the database was just created
            ps1.setString(1, id);
            ps1.setInt(2, internalVersion);
            ps1.execute();

            // Get the version from the database
            ps2.setString(1, id);

            ResultSet result = ps2.executeQuery();

            int currentVersion = 0;
            while (result.next()) {
                if (result.getInt("version") > currentVersion) currentVersion = result.getInt("version");
            }
            conn.commit();

            result.close();

            return currentVersion;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setVersion(String id, int version) {
        getDataManager().performThreadSafeSQLiteOperations(conn -> {
            try (
                    PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO versions(identifier, version) VALUES(?, ?)");
            ) {
                ps.setString(1, id);
                ps.setInt(2, version);
                ps.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    // ----- SYSTEM -----

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreateVersionTable);
    }
}
