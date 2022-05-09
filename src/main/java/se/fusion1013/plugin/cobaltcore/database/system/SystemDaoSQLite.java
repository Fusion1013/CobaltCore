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
    public int getVersion(String id) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM versions WHERE identifier = ?");
            ps.setString(1, id);

            ResultSet result = ps.executeQuery();

            int currentVersion = 0;
            while (result.next()) {
                if (result.getInt("version") > currentVersion) currentVersion = result.getInt("version");
            }
            conn.close();
            return currentVersion;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public void setVersion(String id, int version) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO versions(identifier, version) VALUES(?, ?)");
            ps.setString(1, id);
            ps.setInt(2, version);
            ps.executeUpdate();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
