package se.fusion1013.plugin.cobaltcore.database;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import se.fusion1013.plugin.cobaltcore.CobaltCore;
import se.fusion1013.plugin.cobaltcore.manager.CustomTradesManager;
import se.fusion1013.plugin.cobaltcore.particle.ParticleGroup;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleGroupManager;
import se.fusion1013.plugin.cobaltcore.particle.manager.ParticleStyleManager;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyle;
import se.fusion1013.plugin.cobaltcore.particle.styles.ParticleStyleLine;
import se.fusion1013.plugin.cobaltcore.settings.PlayerSettingHolder;
import se.fusion1013.plugin.cobaltcore.util.PreCalculateWeightsRandom;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * This class is responsible for creating the database and setting up the tables and values
 */
public class SQLite extends Database {

    // ----- VARIABLES -----

    String dbname;

    public static String SQLiteCreatePlayersTable = "CREATE TABLE IF NOT EXISTS players (" +
            "`uuid` varchar(36) NOT NULL," +
            "`name` varchar(32) NOT NULL," +
            "PRIMARY KEY (`uuid`,`name`)" +
            ");";

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

    public static String SQLiteCreatePlayerSettingsTable = "CREATE TABLE IF NOT EXISTS player_settings (" +
            "`player_uuid` TEXT," +
            "`setting` TEXT," +
            "`value` TEXT," +
            "PRIMARY KEY (`player_uuid`,`setting`)" +
            ");";

    public static String SQLiteCreateMerchantTradesTable = "CREATE TABLE IF NOT EXISTS merchant_trades (" +
            "`cost_item` varchar(32)," +
            "`cost_count` INTEGER NOT NULL," +
            "`result_item` varchar(32)," +
            "`result_count` INTEGER NOT NULL," +
            "`max_uses` INTEGER NOT NULL," +
            "`weight` INTEGER NOT NULL," +
            "PRIMARY KEY (`cost_item`, `result_item`)" +
            ");";

    // Particle Tables

    public static String SQLiteCreateParticleGroupStyleView = "CREATE VIEW IF NOT EXISTS particle_group_view AS" +
            " SELECT particle_groups.uuid, particle_groups.name, particle_style_holders.style_name, particle_style_holders.offset_x, particle_style_holders.offset_y, particle_style_holders.offset_z, particle_style_holders.rotation_x, particle_style_holders.rotation_y, particle_style_holders.rotation_z, particle_style_holders.rotation_speed_x, particle_style_holders.rotation_speed_y, particle_style_holders.rotation_speed_z" +
            " FROM particle_groups" +
            " INNER JOIN particle_style_holders ON particle_style_holders.group_uuid = particle_groups.uuid;";

    public static String SQLiteCreateParticleGroupTable = "CREATE TABLE IF NOT EXISTS particle_groups (" +
            "`uuid` varchar(36)," +
            "`name` varchar(32) NOT NULL," +
            "PRIMARY KEY (`uuid`)" +
            ");";

    public static String SQLiteCreateParticleStyleHolderTable = "CREATE TABLE IF NOT EXISTS particle_style_holders (" +
            "`group_uuid` varchar(32)," +
            "`style_name` varchar(32)," +
            "`offset_x` REAL NOT NULL," +
            "`offset_y` REAL NOT NULL," +
            "`offset_z` REAL NOT NULL," +
            "`rotation_x` REAL NOT NULL," +
            "`rotation_y` REAL NOT NULL," +
            "`rotation_z` REAL NOT NULL," +
            "`rotation_speed_x` REAL NOT NULL," +
            "`rotation_speed_y` REAL NOT NULL," +
            "`rotation_speed_z` REAL NOT NULL," +
            "PRIMARY KEY (`group_uuid`, `style_name`)," +
            "FOREIGN KEY (`style_name`) REFERENCES particle_styles(`name`) ON DELETE CASCADE," +
            "FOREIGN KEY (`group_uuid`) REFERENCES particle_groups(`uuid`) ON DELETE CASCADE" +
            ");";

    public static String SQLiteCreateParticleStyleTable = "CREATE TABLE IF NOT EXISTS particle_styles (" +
            "`name` varchar(32)," +
            "`particle` varchar(32) NOT NULL," +
            "`offset_x` REAL NOT NULL," +
            "`offset_y` REAL NOT NULL," +
            "`offset_z` REAL NOT NULL," +
            "`count` REAL NOT NULL," +
            "`speed` REAL NOT NULL," +
            "`rotation_x` REAL NOT NULL," +
            "`rotation_y` REAL NOT NULL," +
            "`rotation_z` REAL NOT NULL," +
            "`angular_velocity_x` REAL NOT NULL," +
            "`angular_velocity_y` REAL NOT NULL," +
            "`angular_velocity_z` REAL NOT NULL," +
            "`style_type` varchar(32) NOT NULL," +
            "`style_extra` TEXT NOT NULL," +
            "PRIMARY KEY (`name`)" +
            ");";

    // ----- CONSTRUCTORS -----

    public SQLite(CobaltCore plugin){
        super(plugin);
        dbname = plugin.getConfig().getString("SQLite.Filename", "cobalt");
    }

    // ----- LOGIC -----

    public static void dropTable(String table) {
        try {
            Connection connection = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement s = connection.prepareStatement("DROP TABLE IF EXISTS " + table + ";");
            s.executeUpdate();
            s.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Performs the string statement on the database.
     *
     * @param string Statement to execute.
     */
    public void executeString(String string){
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(string);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----- PARTICLE GROUPS -----

    public static void removeParticleGroup(UUID groupUUID) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM particle_groups WHERE uuid = ?");
            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM particle_style_holders WHERE group_uuid = ?");
            ps.setString(1, groupUUID.toString());
            ps2.setString(1, groupUUID.toString());
            ps.executeUpdate();
            ps2.executeUpdate();
            conn.commit();
            conn.close();
            ps.close();
            ps2.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insertParticleGroups(List<ParticleGroup> groups) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO particle_groups(uuid, name) VALUES(?,?)");
            PreparedStatement ps2 = conn.prepareStatement("INSERT OR REPLACE INTO particle_style_holders(group_uuid, style_name, offset_x, offset_y, offset_z, rotation_x, rotation_y, rotation_z, rotation_speed_x, rotation_speed_y, rotation_speed_z) VALUES(?,?,?,?,?,?,?,?,?,?,?)");

            for (ParticleGroup group : groups) {
                ps.setString(1, group.getUuid().toString());
                ps.setString(2, group.getName());

                // Insert style holders
                for (ParticleGroup.ParticleStyleHolder holder : group.getParticleStyleList()) {
                    ps2.setString(1, group.getUuid().toString());
                    ps2.setString(2, holder.style.getName());
                    ps2.setDouble(3, holder.offset.getX());
                    ps2.setDouble(4, holder.offset.getY());
                    ps2.setDouble(5, holder.offset.getZ());
                    ps2.setDouble(6, holder.rotation.getX());
                    ps2.setDouble(7, holder.rotation.getY());
                    ps2.setDouble(8, holder.rotation.getZ());
                    ps2.setDouble(9, holder.rotationSpeed.getX());
                    ps2.setDouble(10, holder.rotationSpeed.getY());
                    ps2.setDouble(11, holder.rotationSpeed.getZ());

                    ps2.executeUpdate();
                }

                ps.executeUpdate();
            }

            conn.commit();
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, ParticleGroup> getParticleGroups() {

        Map<String, ParticleGroup> groupMap = new HashMap<>();

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM particle_group_view");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID groupUuid = UUID.fromString(rs.getString("uuid"));
                String groupName = rs.getString("name");
                String styleName = rs.getString("style_name");
                double offsetX = rs.getDouble("offset_x");
                double offsetY = rs.getDouble("offset_y");
                double offsetZ = rs.getDouble("offset_z");
                double rotationX = rs.getDouble("rotation_x");
                double rotationY = rs.getDouble("rotation_y");
                double rotationZ = rs.getDouble("rotation_z");
                double rotationSpeedX = rs.getDouble("rotation_speed_x");
                double rotationSpeedY = rs.getDouble("rotation_speed_y");
                double rotationSpeedZ = rs.getDouble("rotation_speed_z");

                ParticleGroup group = groupMap.get(groupName);
                if (group == null) {
                    group = new ParticleGroup(groupUuid, groupName);
                    groupMap.put(groupName, group);
                }
                ParticleStyle style = ParticleStyleManager.getParticleStyle(styleName);
                if (style != null) {
                    group.addParticleStyle(style);
                    group.setStyleOffset(styleName, new Vector(offsetX, offsetY, offsetZ));
                    group.setStyleRotation(styleName, new Vector(rotationX, rotationY, rotationZ), new Vector(rotationSpeedX, rotationSpeedY, rotationSpeedZ));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return groupMap;
    }

    // ----- PARTICLES STYLES -----

    public static void removeParticleStyle(String styleName) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM particle_styles WHERE name = ?");
            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM particle_style_holders WHERE style_name = ?");
            ps.setString(1, styleName);
            ps2.setString(1, styleName);
            ps.executeUpdate();
            ps2.executeUpdate();
            conn.commit();
            conn.close();
            ps2.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insertParticleStyles(List<ParticleStyle> styles) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO particle_styles(name, particle, offset_x, offset_y, offset_z, count, speed, rotation_x, rotation_y, rotation_z, angular_velocity_x, angular_velocity_y, angular_velocity_z, style_type, style_extra) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            for (ParticleStyle style : styles) {
                ps.setString(1, style.getName());
                ps.setString(2, style.getParticle().name());
                ps.setDouble(3, style.getOffset().getX());
                ps.setDouble(4, style.getOffset().getY());
                ps.setDouble(5, style.getOffset().getZ());
                ps.setDouble(6, style.getCount());
                ps.setDouble(7, style.getSpeed());
                ps.setDouble(8, style.getRotation().getX());
                ps.setDouble(9, style.getRotation().getY());
                ps.setDouble(10, style.getRotation().getZ());
                ps.setDouble(11, style.getAngularVelocityX());
                ps.setDouble(12, style.getAngularVelocityY());
                ps.setDouble(13, style.getAngularVelocityZ());
                ps.setString(14, style.getInternalName());
                ps.setString(15, style.getExtraSettings());
                ps.executeUpdate();
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<String, ParticleStyle> getParticleStyles() {

        Map<String, ParticleStyle> styles = new HashMap<>();

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM particle_styles");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                Particle particle = Particle.valueOf(rs.getString("particle"));
                double offsetX = rs.getDouble("offset_x");
                double offsetY = rs.getDouble("offset_y");
                double offsetZ = rs.getDouble("offset_z");
                int count = rs.getInt("count");
                double speed = rs.getDouble("speed");
                double rotationX = rs.getDouble("rotation_x");
                double rotationY = rs.getDouble("rotation_y");
                double rotationZ = rs.getDouble("rotation_z");
                double angularVelocityX = rs.getDouble("angular_velocity_x");
                double angularVelocityY = rs.getDouble("angular_velocity_y");
                double angularVelocityZ = rs.getDouble("angular_velocity_z");
                String styleType = rs.getString("style_type");
                String styleExtra = rs.getString("style_extra");

                ParticleStyle style = ParticleStyleManager.getDefaultParticleStyle(styleType).clone();
                if (style != null) {
                    style.setName(name);
                    style.setParticle(particle);
                    style.setOffset(new Vector(offsetX, offsetY, offsetZ));
                    style.setCount(count);
                    style.setSpeed(speed);
                    style.setRotation(new Vector(rotationX, rotationY, rotationZ));
                    style.setAngularVelocity(angularVelocityX, angularVelocityY, angularVelocityZ);
                    style.setExtraSettings(styleExtra);

                    styles.put(name, style);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return styles;
    }

    // ----- MERCHANT TRADES -----

    public static void removeMerchantTrade(String costItem, String resultItem) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM merchant_trades WHERE cost_item = ? AND result_item = ?");
            ps.setString(1, costItem);
            ps.setString(2, resultItem);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static PreCalculateWeightsRandom<CustomTradesManager.MerchantRecipePlaceholder> getMerchantTrades() {

        PreCalculateWeightsRandom<CustomTradesManager.MerchantRecipePlaceholder> list = new PreCalculateWeightsRandom<>();

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM merchant_trades");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String costItemName = rs.getString("cost_item");
                int costCount = rs.getInt("cost_count");

                String resultItemName = rs.getString("result_item");
                int resultCount = rs.getInt("result_count");

                int maxUses = rs.getInt("max_uses");

                CustomTradesManager.MerchantRecipePlaceholder mr = new CustomTradesManager.MerchantRecipePlaceholder(costItemName, costCount, resultItemName, resultCount, maxUses);

                list.addItem(mr, rs.getInt("weight"));
            }
            conn.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public static void saveMerchantTrades(List<CustomTradesManager.MerchantRecipePlaceholder> recipes, List<Integer> weights) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO merchant_trades(cost_item, cost_count, result_item, result_count, max_uses, weight) VALUES(?, ?, ?, ?, ?, ?)");
            for (int i = 0; i < recipes.size(); i++) {
                CustomTradesManager.MerchantRecipePlaceholder mr = recipes.get(i);
                ps.setString(1, mr.getCostItemName());
                ps.setInt(2, mr.getCostAmount());
                ps.setString(3, mr.getResultItemName());
                ps.setInt(4, mr.getResultAmount());
                ps.setInt(5, mr.getMaxUses());
                ps.setInt(6, weights.get(i));
                ps.executeUpdate();
            }
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- SETTINGS -----

    public static Map<UUID, PlayerSettingHolder> getSettings() {
        Map<UUID, PlayerSettingHolder> settings = new HashMap<>();

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
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

    public static void saveSettings(Map<UUID, PlayerSettingHolder> settings) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
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

    /**
     * Removes a setting from all players.
     *
     * @param setting the setting to remove.
     */
    public static void removeSetting(String setting) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM player_settings WHERE setting = ?");
            ps.setString(1, setting);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes a setting for a certain player from the database.
     *
     * @param player the player to remove the setting for.
     * @param setting the setting to remove.
     */
    public static void removeSetting(Player player, String setting) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM player_settings WHERE player_uuid = ? AND setting = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, setting);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets a setting from the player settings.
     *
     * @param player the player to get the setting from.
     * @param setting the setting to get.
     * @return the value of the setting. Empty string if not found.
     */
    public static String getSetting(Player player, String setting) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
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

    /**
     * Sets a player setting in the database.
     *
     * @param player the player to set the setting for.
     * @param setting the setting to set.
     * @param value the value to set the setting to.
     */
    public static void setSetting(Player player, String setting, String value) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO player_settings(player_uuid, setting, value) VALUES(?, ?, ?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, setting);
            ps.setString(3, value);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ----- LOCATION -----

    /**
     * Removes a location from the database.
     *
     * @param uuid the unique identifier of the location.
     */
    public static void removeLocation(UUID uuid) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM locations WHERE uuid = ?");
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Inserts a location into the database.
     *
     * @param uuid the unique identifier of the location.
     * @param location the location to insert.
     * @return the number of rows inserted.
     */
    public static int insertLocation(UUID uuid, Location location) {
        int rowsInserted = 0;

        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO locations(uuid, world, x_pos, y_pos, z_pos, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, uuid.toString());
            ps.setString(2, location.getWorld().getName());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setDouble(6, location.getYaw());
            ps.setDouble(7, location.getPitch());

            rowsInserted = ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rowsInserted;
    }

    // ----- PLAYER -----

    /**
     * Gets a player name from the database.
     *
     * @param uuid the uuid of the player.
     * @return the player name, or an empty string if not found.
     */
    public static String getPlayerName(UUID uuid) {
        try {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
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

    /**
     * Inserts a player into the database.
     *
     * @param player the player to insert.
     * @return the number of rows inserted.
     */
    public static int insertPlayer(Player player) {
        int rowsInserted = 0;

        try  {
            Connection conn = CobaltCore.getInstance().getRDatabase().getSQLConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO players(uuid, name) VALUES(?, ?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());

            rowsInserted = ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return rowsInserted;
    }

    public Connection getSQLConnection(){
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()){
            try {
                plugin.getDataFolder().mkdir();
                dataFolder.createNewFile();
            } catch (IOException e){
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db", e);
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            System.out.println(dbname);
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex){
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }

        return null;
    }

    // ----- LOADING -----

    public void load(){
        executeString("PRAGMA foreign_keys = ON;");

        executeString(SQLiteCreatePlayersTable);
        executeString(SQLiteCreatePlayerLocationsTable);
        executeString(SQLiteCreatePlayerSettingsTable);
        executeString(SQLiteCreateMerchantTradesTable);
        executeString(SQLiteCreateParticleStyleTable);
        executeString(SQLiteCreateParticleStyleHolderTable);
        executeString(SQLiteCreateParticleGroupTable);
        executeString(SQLiteCreateParticleGroupStyleView);
    }
}
