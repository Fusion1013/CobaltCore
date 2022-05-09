package se.fusion1013.plugin.cobaltcore.database.setting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.database.system.Dao;
import se.fusion1013.plugin.cobaltcore.database.system.DataManager;
import se.fusion1013.plugin.cobaltcore.settings.PlayerSettingHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingDaoSQLite extends Dao implements ISettingDao {

    public static String SQLiteCreatePlayerSettingsTable = "CREATE TABLE IF NOT EXISTS player_settings (" +
            "`player_uuid` TEXT," +
            "`setting` TEXT," +
            "`value` TEXT," +
            "PRIMARY KEY (`player_uuid`,`setting`)" +
            ");";

    @Override
    public Map<UUID, PlayerSettingHolder> getSettings() {
        Map<UUID, PlayerSettingHolder> settings = new HashMap<>();

        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_settings");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));

                PlayerSettingHolder setting = settings.get(uuid);
                if (setting == null) setting = new PlayerSettingHolder();

                String key = rs.getString("setting");
                String value = rs.getString("value");

                setting.setPlayerSetting(key, value);
                settings.put(uuid, setting);
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return settings;
    }

    @Override
    public void saveSettings(Map<UUID, PlayerSettingHolder> settings) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_settings(player_uuid, setting, value) VALUES(?, ?, ?)");
            for (UUID uuid : settings.keySet()) {
                Map<String, String> values = settings.get(uuid).getPlayerSettings();
                for (String key : values.keySet()) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, key);
                    ps.setString(3, values.get(key));
                    ps.executeUpdate();
                }
            }
            conn.commit();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void removeSetting(String setting) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            try {
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM player_settings WHERE setting = ?");
                ps.setString(1, setting);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void removeSetting(Player player, String setting) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            try {
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM player_settings WHERE player_uuid = ? AND setting = ?");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, setting);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public String getSetting(Player player, String setting) {
        try {
            Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM player_settings WHERE player_uuid = ? AND setting = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, setting);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return "";
    }

    @Override
    public void setSetting(Player player, String setting, String value) {
        Bukkit.getScheduler().runTaskAsynchronously(CobaltCore.getInstance(), () -> {
            try {
                Connection conn = DataManager.getInstance().getSqliteDb().getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_settings(player_uuid, setting, value) VALUES(?, ?, ?)");
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, setting);
                ps.setString(3, value);
                ps.executeUpdate();
                conn.close();
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
        DataManager.getInstance().getSqliteDb().executeString(SQLiteCreatePlayerSettingsTable);
    }
}
