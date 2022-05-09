package se.fusion1013.plugin.cobaltcore.database.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.util.ParticleContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDaoSQLite extends Dao implements IPlayerDao {

    // ----- CREATE TABLE -----

    private static final String SQLiteCreatePlayerTable = "CREATE TABLE IF NOT EXISTS players (" +
            "`uuid` varchar(36) NOT NULL," +
            "`name` varchar(32) NOT NULL," +
            "PRIMARY KEY (`uuid`,`name`)" +
            ");";

    // ----- PLAYER INSERTING -----

    @Override
    public void insertPlayer(Player player) {
        try  {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO players(uuid, name) VALUES(?, ?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- PLAYER GETTING -----

    @Override
    public String getPlayerName(UUID uuid) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return null;
    }

    // ----- TYPE -----

    @Override
    public DataManager.StorageType getStorageType() {
        return DataManager.StorageType.SQLITE;
    }

    // ----- INIT -----

    @Override
    public void init() {
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreatePlayerTable);
    }
}
